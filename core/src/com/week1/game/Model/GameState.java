package com.week1.game.Model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.AIMovement.AStar;
import com.week1.game.Model.Components.*;
import com.week1.game.Model.Entities.*;
import com.week1.game.Model.Events.SelectionEvent;
import com.week1.game.Model.Systems.*;
import com.week1.game.Model.World.*;
import com.week1.game.Pair;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.BlockType;
import com.week1.game.TowerBuilder.TowerDetails;
import com.week1.game.Tuple3;

import java.util.*;

import static com.week1.game.MenuScreens.GameScreen.THRESHOLD;
import static com.week1.game.Model.StatsConfig.*;

public class GameState implements GameRenderable {
    private final Unit2StateAdapter u2s;
    private GameGraph graph;
    private Array<Clickable> clickables = new Array<>();
    private IWorldBuilder worldBuilder;
    private GameWorld world;
    
    private MovementSystem movementSystem = new MovementSystem();
    private PathfindingSystem pathfindingSystem;
    private RenderSystem renderSystem = new RenderSystem();
    private RenderNametagSystem renderNametagSystem = new RenderNametagSystem();
    private TargetingSystem targetingSystem;
    private TargetingSystem.RenderTargetingSystem renderTargetingSystem;
    private InterpolatorSystem interpolatorSystem = new InterpolatorSystem();
    private DeathSystem deathSystem;
    private CrystalRespawnSystem crystalRespawnSystem;
    private DamageSystem damageSystem = new DamageSystem();
    private EntityManager entityManager = new EntityManager();
    private ManaRegenSystem manaRegenSystem = new ManaRegenSystem();
    private DeathRewardSystem deathRewardSystem = new DeathRewardSystem();
    private DamageRewardSystem damageRewardSystem = new DamageRewardSystem();
    private HealthRenderSystem healthRenderSystem = new HealthRenderSystem();
    private HealthGrowthSystem healthGrowthSystem = new HealthGrowthSystem();
    private FogSystem fogSystem = new FogSystem();
    private TowerSpawnSystem towerSpawnSystem;

    private Array<Crystal> crystals = new Array<>();
    private Array<Unit> units = new Array<>();
    private Array<Tower> towers = new Array<>();
    private IntMap<Tower> playerBases = new IntMap<>();
    private Array<PlayerEntity> players = new Array<>();
    private OwnedComponent noOwn = new OwnedComponent(-1);
    private TowerLoadouts towerLoadouts;

    // need to determine which units should push back the fog of war
    private int localPlayerID;

    /*
     * Runnable to execute immediately after the game state has been initialized.
     */
    private Runnable postInit;
    private boolean fullyInitialized = false;

    public GameState(IWorldBuilder worldBuilder, Runnable postInit, List<PlayerInfo> playerInfo, int localPlayerID){
        this.localPlayerID = localPlayerID;
        // TODO tower types in memory after exchange
        // Create player entities
        for (int playerId = 0; playerId < playerInfo.size(); playerId++) {
            PlayerInfo info = playerInfo.get(playerId);
            addPlayer(playerId, info.getPlayerName(), info.getColor());
        }
        this.worldBuilder = worldBuilder;
        this.u2s = new Unit2StateAdapter() {
            @Override
            public Block getBlock(int i, int j, int k) {
                return world.getBlock(i, j, k);
            }

            @Override
            public int getHeight(int i, int j) {
                return world.getHeight(i, j);
            }
        };
        this.pathfindingSystem = new PathfindingSystem(u2s);
        initTargetingSystem();
        initDeathSystem();
        initCrystalRespawnSystem();
        initTowerSpawnSystem();
        targetingSystem.addSubscriber(damageSystem);
        targetingSystem.addSubscriber(damageRewardSystem);
        damageSystem.addSubscriber(deathSystem);
        damageSystem.addSubscriber(deathRewardSystem);
        damageSystem.addSubscriber(crystalRespawnSystem);
        this.postInit = postInit;
    }

    /*
     * Targeting system uses a service that (currently) needs intimate access to the gamestate,
     * so we'll do the initialization here.
     */
    private void initTargetingSystem() {
        this.targetingSystem = new TargetingSystem(new IService<Tuple3<OwnedComponent, TargetingComponent, PositionComponent>, Pair<Integer, PositionComponent>>() {
            Vector3 otherPosition = new Vector3();
            float minDist = Float.POSITIVE_INFINITY;
            Pair<Integer, PositionComponent> result = new Pair<>(-1, null);
            @Override
            public Pair<Integer, PositionComponent> query(Tuple3<OwnedComponent, TargetingComponent, PositionComponent> key) {
                result.key = -1;
                result.value = null;
                OwnedComponent ownedComponent = key._1;
                TargetingComponent targetingComponent = key._2;
                PositionComponent positionComponent = key._3;

                PositionComponent otherPositionComponent;
                minDist = targetingComponent.range; // Any target must be within range.
                float dist;
                // Check through all units to determine closet suitable target
                for (int i = 0; i < units.size; i++) {
                    Unit unit = units.get(i);
                    // Check if unit owner follows owner rule.
                    int unitOwner = unit.getPlayerId();
                    switch (targetingComponent.strategy) {
                        case ENEMY:
                            if (unitOwner == ownedComponent.playerID) continue;
                            break;
                        case TEAM:
                            if (unitOwner != ownedComponent.playerID) continue;
                            break;
                        case ALL:
                            break;
                    }
                    // Check position of unit.
                    otherPositionComponent = unit.getPositionComponent();
                    if (otherPositionComponent == positionComponent) continue; // TODO should be a better way of making sure there's no self targeting
                    dist = positionComponent.position.dst(otherPositionComponent.position);
                    if (dist < minDist) {
                        minDist = dist;
                        result.key = unit.ID;
                        result.value = unit.getPositionComponent();
                    }
                }

                // Are there any towers that might be closer suitable targets?
                for (Tower tower: towers) {
                    int towerOwner = tower.getPlayerId();
                    switch (targetingComponent.strategy) {
                        case ENEMY:
                            if (towerOwner == ownedComponent.playerID) continue;
                            break;
                        case TEAM:
                            if (towerOwner != ownedComponent.playerID) continue;
                            break;
                        case ALL:
                            break;
                    }
                    // Check position of unit.
                    otherPositionComponent = tower.getPositionComponent();
                    if (otherPositionComponent == positionComponent) continue; // TODO should be a better way of making sure there's no self targeting
                    dist = positionComponent.position.dst(otherPositionComponent.position);
                    if (dist < minDist) {
                        minDist = dist;
                        result.key = tower.ID;
                        result.value = tower.getPositionComponent();
                    }
                }
                
                // Any crystals that are closer suitable targets?
                for (int c = 0; c < crystals.size; c++) {
                    Crystal crystal = crystals.get(c);
                    switch (targetingComponent.strategy) {
                        case ENEMY:
                            break;
                        case TEAM:
                            continue; // if attempting to team kill, don't target crystals
                        case ALL:
                            break;
                    }
                    // Check position of unit.
                    otherPositionComponent = crystal.getPositionComponent();
                    // Don't need to check for self-target here, because crystals don't deal damage
                    dist = positionComponent.position.dst(otherPositionComponent.position);
                    if (dist < minDist) {
                        minDist = dist;
                        result.key = crystal.ID;
                        result.value = crystal.getPositionComponent();
                    }
                }
                
                return result;
            }
        });
        this.renderTargetingSystem = targetingSystem.new RenderTargetingSystem();
    }

    private void initCrystalRespawnSystem() {
        this.crystalRespawnSystem = new CrystalRespawnSystem(
                (key) -> {
                    // ignore the position given in the key (dictated by worldBuilder.nextCrystalLocation()
                    return placeCrystal(worldBuilder.nextCrystalLocation());
                },
                (key) -> {
                    // Does the given id 'key' correspond to a crystal?
                    for (int i = 0; i < crystals.size; i++) {
                        if (crystals.get(i).ID == key) {
                            return crystals.get(i).getPositionComponent();
                        }
                    }
                    return null;
                });
    }

    
    private void initDeathSystem() {
        this.deathSystem = new DeathSystem(
                new IService<Integer, Void>() {
                    @Override
                    public Void query(Integer key) {
                        removeEntity(key);
                        return null;
                    }
                }
        );
    }

    private void initTowerSpawnSystem() {
        this.towerSpawnSystem = new TowerSpawnSystem(
            new IService<Integer, Void>() {
                @Override
                public Void query(Integer key) {
                    removeEntity(key);
                    return null;
                }
            }, new IService<javafx.util.Pair<Tower, Integer>, Void>() {
                @Override
                public Void query(javafx.util.Pair<Tower, Integer> key) {
                    addFinishedTower(key.getKey(), key.getValue());
                    return null;
                }
            }
        );
    }

    public void synchronousUpdateState(int communicationTurn) {
        fogSystem.update(THRESHOLD);
        manaRegenSystem.update(THRESHOLD);
        pathfindingSystem.update(THRESHOLD);
        movementSystem.update(THRESHOLD);
        targetingSystem.update(THRESHOLD);
        interpolatorSystem.update(THRESHOLD);
        renderSystem.update(THRESHOLD);
        renderNametagSystem.update(THRESHOLD);
        damageSystem.update(THRESHOLD);
        crystalRespawnSystem.update(THRESHOLD); // important that this happens before death (or else crystal may be removed before being queued for respawn)
        damageRewardSystem.update(THRESHOLD);
        deathRewardSystem.update(THRESHOLD);
        deathSystem.update(THRESHOLD);
        healthGrowthSystem.update(THRESHOLD);
        towerSpawnSystem.update(THRESHOLD);
        doTowerSpecialAbilities(communicationTurn);
    }

    public Tower getPlayerBase(int playerId) {
        return playerBases.get(playerId);
    }

    /*
     This message will come in when the network has chosen the specific number of players that
     will be in the game. It inadvertently means the game is about to start.

     This will create the bases for all of the players and give them all an amount of currency.
     */
    public void initializeGame(long mapSeed, int numPlayers) {
        boolean[] mapReady = {false};
        
        // Needs to happen in initializeGame, so that the host can send the mapSeed,
        // needs to happen in postRunnable because initializeGame is not called on the right thread
        Gdx.app.postRunnable(() -> {
            if (mapSeed == 0) {
                worldBuilder = SmallWorldBuilder.ONLY;
            }
            worldBuilder.addSeed(mapSeed);
            world = new GameWorld(worldBuilder);
            world.getHeightMap();
            graph = world.buildGraph();
            graph.setPathFinder(new AStar<>(graph));

            // notify the GameState that it can proceed with initialization
            mapReady[0] = true; // 'the array trick'
        });
        
        try {
            while(!mapReady[0]) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Initialize the fog of war system, now that the game world is ready
        fogSystem.init(world, this.localPlayerID);
        fogSystem.update(0);

        // Create the correct amount of bases.
        Gdx.app.log("GameState -pjb3", "The number of players received is " +  numPlayers);

        // Create the correct amount of actual players
        Vector3[] startLocs = worldBuilder.startLocations(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            // Create and add a base for each player
            Tower newBase = addBase((int) startLocs[i].x, (int) startLocs[i].y, (int) startLocs[i].z,
                    towerLoadouts.getTowerDetails(i,-1), i, -1);
            addBase(newBase, i);
        }
        Gdx.app.log("GameState -pjb3", " Finished creating bases and Player Stats" +  numPlayers);

        // Create the crystals
        placeCrystals();


        fullyInitialized = true;
        postInit.run();
    }

    private void placeCrystals() {
        int numPlaced = 0;
        while (numPlaced < worldBuilder.getNumCrystals()) { // keep looking for positions until all the crystals have been placed
            Vector2 desiredLoc = worldBuilder.nextCrystalLocation();
            if (placeCrystal(desiredLoc)) {
                numPlaced++;
            }
        }

        // If there are no suitable blocks, then maybe the crystal doesn't get placed
    }

    /*
     * Returns true on success, false on failure
     */
    private boolean placeCrystal(Vector2 desiredLoc) {
        // Start at z = 1, since crystals shouldn't be spawned on the base layer of the map
        for (int z = 1; z < world.getWorldDimensions()[2]; z++) {
            // Place the crystal in the first available airblock
            if ((world.getBlock((int)desiredLoc.x, (int)desiredLoc.y, z) == Block.TerrainBlock.AIR) &&
                    (world.getBlock((int)desiredLoc.x, (int)(desiredLoc.y), z - 1).canSupportTower())) {
                addCrystal(desiredLoc.x, desiredLoc.y, z);
                return true;
            }
        }

        return false;
    }

    public PlayerEntity getPlayer(int playerNum) {
        if (isInitialized()) {
            return players.get(playerNum);
        } else {
            return PlayerEntity.BLANK;
        }
    }

    public void addPlayer(int playerID, String name, Color color) {
        OwnedComponent ownedComponent = new OwnedComponent(playerID);
        ManaComponent manaComponent = new ManaComponent(startingMana);
        NameComponent nameComponent = new NameComponent(name);
        ColorComponent colorComponent = new ColorComponent(color);
        
        PlayerEntity player = new PlayerEntity(ownedComponent, manaComponent, nameComponent, colorComponent);
        players.add(player);
        
        // Register with manaRegenSystem so that the player's mana will regenerate over time.
        manaRegenSystem.addMana(player.getPlayerID(), manaComponent);
        
        // Register with reward systems, so the player can be rewarded for kills and damage
        damageRewardSystem.addMana(player.getPlayerID(), manaComponent);
        deathRewardSystem.addMana(player.getPlayerID(), manaComponent);
    }

    public void addCrystal(float x, float y, float z) {
        PositionComponent positionComponent = new PositionComponent(x, y, z);
        HealthComponent healthComponent = new HealthComponent(CRYSTAL_HEALTH, CRYSTAL_HEALTH);
        ManaRewardComponent manaRewardComponent = new ManaRewardComponent(100, 0.25f);
        RenderComponent renderComponent = new RenderComponent(new ModelInstance(Initializer.crystal));
        VisibleComponent visibleComponent = new VisibleComponent(false);

        Crystal c = new Crystal(positionComponent, healthComponent, manaRewardComponent, renderComponent, visibleComponent, entityManager.newID());
        crystals.add(c);
        clickables.add(c);
        
        // Register with the damage system, so that the crystal can take damage
        damageSystem.addHealth(c.ID, healthComponent);
        // Register with damage reward system, so rewards are given for damaging this crystal
        damageRewardSystem.addManaReward(c.ID, manaRewardComponent);
        // Register with death reward system, so rewards are given for killing this crystal
        deathRewardSystem.addManaReward(c.ID, manaRewardComponent);
        healthRenderSystem.addNode(c.ID, positionComponent, healthComponent, noOwn, visibleComponent);
        renderSystem.addNode(c.ID, renderComponent, positionComponent, visibleComponent);
        fogSystem.addSeen(c.ID, positionComponent, visibleComponent);
    }

    public Unit addUnit(float x, float y, float z, float tempHealth, int playerID){
        PositionComponent positionComponent = new PositionComponent(x, y, z);
        VelocityComponent velocityComponent = new VelocityComponent((float) Unit.speed, 0, 0, 0);
        PathComponent pathComponent = new PathComponent();
        RenderComponent renderComponent = new RenderComponent(new ModelInstance(Unit.modelMap.get(playerID)));
        OwnedComponent ownedComponent = new OwnedComponent(playerID);
        TargetingComponent targetingComponent = new TargetingComponent(-1, (float) tempMinionRange, true, TargetingComponent.TargetingStrategy.ENEMY);
        HealthComponent healthComponent = new HealthComponent(tempHealth, tempHealth);
        DamagingComponent damagingComponent = new DamagingComponent((float) tempMinionDamage);
        ManaRewardComponent manaRewardComponent = new ManaRewardComponent(0, 0);
        VisibleComponent visibleComponent = new VisibleComponent(localPlayerID == playerID); // if built locally, show the hp right away
        Unit u = new Unit(positionComponent, velocityComponent, pathComponent, renderComponent, ownedComponent, healthComponent, visibleComponent);
        u.ID = entityManager.newID();
        units.add(u);
        movementSystem.addNode(u.ID, positionComponent, velocityComponent);
        pathfindingSystem.addNode(u.ID, positionComponent, velocityComponent, pathComponent);
        PositionComponent interpolated = new PositionComponent(positionComponent.position);
        interpolatorSystem.addNode(u.ID, positionComponent, interpolated, velocityComponent);
        renderSystem.addNode(u.ID, renderComponent, interpolated, visibleComponent);
        targetingSystem.addNode(u.ID, ownedComponent, targetingComponent, positionComponent);
        damageSystem.addHealth(u.ID, healthComponent);
        damageSystem.addDamage(u.ID, damagingComponent);
        damageRewardSystem.addManaReward(u.ID, manaRewardComponent);
        damageRewardSystem.addDamage(u.ID, damagingComponent);
        deathRewardSystem.addManaReward(u.ID, manaRewardComponent);
        healthRenderSystem.addNode(u.ID, interpolated, healthComponent, ownedComponent, visibleComponent);
        if (ownedComponent.playerID == localPlayerID) { // only locally owned seers should be added
            fogSystem.addSeer(u.ID, interpolated, targetingComponent);
        }
        fogSystem.addSeen(u.ID, positionComponent, visibleComponent);
        clickables.add(u);
        return u;
    }

    public Tower addTower(int x, int y, int z, TowerDetails towerDetails, int playerID, int towerType) {
        PositionComponent positionComponent = new PositionComponent((float) x, (float) y, (float) z);
        HealthComponent healthComponent = new HealthComponent((float) towerDetails.getHp(), (float) towerDetails.getHp());
        HealthComponent unfinishedHealthComponent = new HealthComponent((float) towerDetails.getHp(),1f, (float) towerDetails.getHp()/buildDelay);
        DamagingComponent damagingComponent = new DamagingComponent((float) towerDetails.getAtk());
        TargetingComponent targetingComponent = new TargetingComponent(-1, (float) towerDetails.getRange(), true,
            TargetingComponent.TargetingStrategy.ENEMY);
        OwnedComponent ownedComponent = new OwnedComponent(playerID);
        ManaRewardComponent manaRewardComponent = new ManaRewardComponent(100, 0);
        VisibleComponent visibleComponent = new VisibleComponent(localPlayerID == playerID);
        List<BlockSpec> unfinishedBlockSpecs = new ArrayList<BlockSpec>();
        for(int i = 0; i < towerDetails.getLayout().size(); i++){
            BlockSpec spec = towerDetails.getLayout().get(i);
            BlockSpec newSpec = new BlockSpec(BlockType.ETHERITE, spec.getX(), spec.getY(), spec.getZ());//TODO:switch to unfinished
            unfinishedBlockSpecs.add(newSpec);
        }
        TowerDetails unfinishedTowerDetails = new TowerDetails(unfinishedBlockSpecs, "unfinished " + towerDetails.getName());
        Tower.TowerAdapter adapter = new Tower.TowerAdapter() {
            @Override
            public void hover(boolean shouldHover) {
                for (BlockSpec b: towerDetails.getLayout()) {
                    // Note: we switch getZ() and getY() because the BlockSpec coordinates are Y-up.
                    world.setBlockHovered(x + b.getX(), y + b.getZ(), z + b.getY(), shouldHover);
                }
            }

            @Override
            public void select(boolean shouldSelect) {
                for (BlockSpec b: towerDetails.getLayout()) {
                    // Note: we switch getZ() and getY() because the BlockSpec coordinates are Y-up.
                    world.setBlockSelected(x + b.getX(), y + b.getZ(), z + b.getY(), shouldSelect);
                }
            }

            @Override
            public boolean intersects(Ray ray, Vector3 intersection) {
                // For now, we don't actually need this to do anything,
                // we use an optimized version of tower detection in the ClickOracle.
                return false;
            }
        };
        Tower tower = new Tower(positionComponent, healthComponent, ownedComponent, visibleComponent, targetingComponent, damagingComponent,
                towerDetails, adapter, towerType, entityManager.newID());
        Tower unfinishedTower = new Tower(positionComponent, unfinishedHealthComponent, ownedComponent,
                visibleComponent, targetingComponent, damagingComponent, unfinishedTowerDetails, adapter, towerType, entityManager.newID());
        damageSystem.addHealth(unfinishedTower.ID, unfinishedHealthComponent);
        damageSystem.addDamage(unfinishedTower.ID, damagingComponent);
        damageRewardSystem.addManaReward(unfinishedTower.ID, manaRewardComponent);
        damageRewardSystem.addDamage(unfinishedTower.ID, damagingComponent);
        deathRewardSystem.addManaReward(unfinishedTower.ID, manaRewardComponent);
        healthRenderSystem.addNode(unfinishedTower.ID, new PositionComponent(unfinishedTower.highestBlockLocation), unfinishedHealthComponent, ownedComponent, visibleComponent);
        healthGrowthSystem.addHealthGrowth(unfinishedTower.ID, unfinishedHealthComponent);
        fogSystem.addSeen(unfinishedTower.ID, positionComponent, visibleComponent);
        towers.add(unfinishedTower);
        addBuilding(unfinishedTower, playerID);
        towerSpawnSystem.addNode(tower.ID, tower, unfinishedTower.ID);

        return tower;
    }

    public Tower addBase(int x, int y, int z, TowerDetails towerDetails, int playerID, int towerType) {
        PositionComponent positionComponent = new PositionComponent((float) x, (float) y, (float) z);
        HealthComponent healthComponent = new HealthComponent((float) towerDetails.getHp(), (float) towerDetails.getHp());
        DamagingComponent damagingComponent = new DamagingComponent((float) towerDetails.getAtk());
        TargetingComponent targetingComponent = new TargetingComponent(-1, (float) towerDetails.getRange(), true,
                TargetingComponent.TargetingStrategy.ENEMY);
        OwnedComponent ownedComponent = new OwnedComponent(playerID);
        ManaRewardComponent manaRewardComponent = new ManaRewardComponent(100, 0);
        VisibleComponent visibleComponent = new VisibleComponent(localPlayerID == playerID);
        Tower.TowerAdapter adapter = new Tower.TowerAdapter() {
            @Override
            public void hover(boolean shouldHover) {
                for (BlockSpec b: towerDetails.getLayout()) {
                    // Note: we switch getZ() and getY() because the BlockSpec coordinates are Y-up.
                    world.setBlockHovered(x + b.getX(), y + b.getZ(), z + b.getY(), shouldHover);
                }
            }

            @Override
            public void select(boolean shouldSelect) {
                for (BlockSpec b: towerDetails.getLayout()) {
                    // Note: we switch getZ() and getY() because the BlockSpec coordinates are Y-up.
                    world.setBlockSelected(x + b.getX(), y + b.getZ(), z + b.getY(), shouldSelect);
                }
            }

            @Override
            public boolean intersects(Ray ray, Vector3 intersection) {
                // For now, we don't actually need this to do anything,
                // we use an optimized version of tower detection in the ClickOracle.
                return false;
            }
        };
        Tower base = new Tower(positionComponent, healthComponent, ownedComponent, visibleComponent, targetingComponent, damagingComponent,
                towerDetails, adapter, towerType, entityManager.newID());
        damageSystem.addHealth(base.ID, healthComponent);
        damageSystem.addDamage(base.ID, damagingComponent);
        damageRewardSystem.addManaReward(base.ID, manaRewardComponent);
        damageRewardSystem.addDamage(base.ID, damagingComponent);
        deathRewardSystem.addManaReward(base.ID, manaRewardComponent);
        healthRenderSystem.addNode(base.ID, new PositionComponent(base.highestBlockLocation), healthComponent, ownedComponent, visibleComponent);
        healthGrowthSystem.addHealthGrowth(base.ID, healthComponent);
        if (ownedComponent.playerID == localPlayerID) { // only locally owned seers should be added
            fogSystem.addSeer(base.ID, positionComponent, targetingComponent);
        }
        fogSystem.addSeen(base.ID, positionComponent, visibleComponent);
        towers.add(base);
        addBuilding(base, playerID);
        return base;
    }
    public void addFinishedTower(Tower tower, int dummyID) {
        removeEntity(dummyID);
        OwnedComponent ownedComponent = tower.getOwnedComponent();
        TargetingComponent targetingComponent = tower.getTargetingComponent();
        HealthComponent healthComponent = tower.getHealthComponent();
        PositionComponent positionComponent = tower.getPositionComponent();
        DamagingComponent damagingComponent = tower.getDamagingComponent();
        ManaRewardComponent manaRewardComponent = new ManaRewardComponent(100, 0);
        VisibleComponent visibleComponent = new VisibleComponent(localPlayerID == tower.getPlayerId());
        targetingSystem.addNode(tower.ID, ownedComponent, targetingComponent, positionComponent);
        damageSystem.addHealth(tower.ID, healthComponent);
        damageSystem.addDamage(tower.ID, damagingComponent);
        damageRewardSystem.addManaReward(tower.ID, manaRewardComponent);
        damageRewardSystem.addDamage(tower.ID, damagingComponent);
        deathRewardSystem.addManaReward(tower.ID, manaRewardComponent);
        healthRenderSystem.addNode(tower.ID, new PositionComponent(tower.highestBlockLocation), healthComponent, ownedComponent, visibleComponent);
        if (ownedComponent.playerID == localPlayerID) { // only locally owned seers should be added
            fogSystem.addSeer(tower.ID, positionComponent, targetingComponent);
        }
        fogSystem.addSeen(tower.ID, positionComponent, visibleComponent);
        towers.add(tower);
        addBuilding(tower, ownedComponent.playerID);

    }
    public void addBase(Tower pb, int playerID) {
        String playerName = players.get(playerID).getName();
        Color playerColor = players.get(playerID).getColor();
        
        renderNametagSystem.addNode(pb.ID, new RenderNametagComponent(playerName), pb.getPositionComponent());
        playerBases.put(playerID, pb);
        addBuilding(pb, playerID);
    }

    public void addBuilding(Tower t, int playerID) {
        List<BlockSpec> blockSpecs = t.getLayout();
        for (int k = 0; k < blockSpecs.size(); k++) {
            BlockSpec bs = blockSpecs.get(k);
            world.addTowerBlock(
                    (int)(t.getX() + bs.getX()),
                    (int)(t.getY() + bs.getZ()),
                    (int)(t.getZ() + bs.getY()),
                    Block.TowerBlock.towerBlockMap.get(bs.getBlockCode()),
                    t.getPlayerId() == this.localPlayerID);

        }
    }

    /*
     * Remove all references to this entity (purge all systems and game state).
     */
    public void removeEntity(int id) {
        movementSystem.remove(id);
        pathfindingSystem.remove(id);
        renderSystem.remove(id);
        damageSystem.remove(id);
        targetingSystem.remove(id);
        crystalRespawnSystem.remove(id); // noop
        deathRewardSystem.remove(id);
        damageRewardSystem.remove(id);
        renderNametagSystem.remove(id);
        healthRenderSystem.remove(id);
        towerSpawnSystem.remove(id);
        fogSystem.remove(id);

        healthGrowthSystem.remove(id);
        units.select(u -> u.ID == id).forEach(unit -> {
            units.removeValue(unit, true);
            clickables.removeValue(unit, true);
        });
        towers.select(t -> t.ID == id).forEach(tower -> {
            List<BlockSpec> blockSpecs = tower.getLayout();
            for(int k = 0; k < blockSpecs.size(); k++) {
                BlockSpec bs = blockSpecs.get(k);
                world.clearBlock(
                        (int)(tower.getX() + bs.getX()),
                        (int)(tower.getY() + bs.getZ()),
                        (int)(tower.getZ() + bs.getY()));
            }
            towers.removeValue(tower, true);
            if (playerBases.containsValue(tower, true)) {
                playerDies(tower.getPlayerId());
                playerBases.remove(tower.getPlayerId());
            }
        });
        crystals.select(c -> c.ID == id).forEach(crystal -> {
            crystals.removeValue(crystal, true);
            clickables.removeValue(crystal, true);
        });
    }
    
    private void playerDies(int playerID) {
        manaRegenSystem.removePlayer(playerID);
        damageRewardSystem.removePlayer(playerID);
        deathRewardSystem.removePlayer(playerID);
    }
    
    public void updateGoal(Unit unit, Vector3 goal) {
        Vector2 unitPos = new Vector2((int) unit.getX(), (int) unit.getY()); //TODO: make acutal z;
        unit.setGoal(goal);
        OutputPath path;

        Vector2 goalPos = new Vector2(goal.x, goal.y);

        long start = System.nanoTime();
        path = graph.search(unitPos, goalPos);
        long end = System.nanoTime();
        Gdx.app.debug("GameState - wab2", "AStar completed in " + (end - start) + " nanoseconds");
        if (path != null) {
            unit.setPath(path);
        }else{
            Gdx.app.error("GameState - wab2", "Astar broke");
        }
    }

    public Array<Unit> findUnitsInBox(Vector3 cornerA, Vector3 cornerB, RenderConfig renderConfig) {
        Array<Unit> unitsToSelect = new Array<>();
        Vector3 scrnCoords = new Vector3();
        
        for (Unit u : units) {
            scrnCoords.set(u.getX(), u.getY(), u.getZ());
            renderConfig.getCam().project(scrnCoords);
            
            if (Math.min(cornerA.x, cornerB.x) < scrnCoords.x && scrnCoords.x < Math.max(cornerA.x, cornerB.x) &&
                Math.min(cornerA.y, cornerB.y) < scrnCoords.y && scrnCoords.y < Math.max(cornerA.y, cornerB.y)) {
                unitsToSelect.add(u);
            }
        }
        return unitsToSelect;
    }

    public Array<Unit> getUnits() {
        return units;
    }
    public Unit getMinionById(int minionId) {

        for (int i = 0; i < units.size; i++) {
            if (minionId == units.get(i).ID) {
                return units.get(i);
            }
        }

        return null;
    }
    public void moveMinion(float x, float y, Unit u) {
        updateGoal(u, new Vector3(x, y, 0));
    }

    public void doTowerSpecialAbilities(int communicationTurn) {
        for (int i = 0; i < towers.size; i++) {
            Tower t = towers.get(i);
            t.doSpecialEffect(communicationTurn, this);
        }
    }

    public boolean findNearbyStructure(float x, float y, float z, int playerId) {

        // Check if it is near any of your towers
        for (Tower t : towers) {
            if (t.getPlayerId() == playerId) {
                if (Math.sqrt(Math.pow(x - t.getX(), 2) + Math.pow(y - t.getY(), 2) + Math.pow(z - t.getZ(), 2)) < placementRange){
                    return true;
                }
            }
        }

        return false;
    }

    public GameWorld getWorld() {
        return world;
    }

    public boolean isInitialized() {
        return fullyInitialized;
    }

    public boolean isPlayerAlive(int playerId) {
        if (!isInitialized() || playerId == PLAYERNOTASSIGNED){
            return true; // Return that the player is alive because the game has not started
        }
        return playerBases.get(playerId) != null;
    }

    public boolean checkIfWon(int playerId) {
        if (!isPlayerAlive(playerId) || !isInitialized() || playerId == PLAYERNOTASSIGNED){
            return false; // Can't win if you're dead lol or if the game has not started
        }

        // Check if you are the last base alive
        return playerBases.size == 1;

    }
    
    public void setTowerInfo(TowerLoadouts info) {
        this.towerLoadouts = info;
    }
    public TowerDetails getTowerDetails(int playerId, int towerId) {
        return this.towerLoadouts.getTowerDetails(playerId, towerId);
    }

    public boolean getGameOver() {
        if (!isInitialized()){
            return false; // Can't win if you're dead lol or if the game has not started
        }

        int numPlayersAlive = playerBases.size;
        return numPlayersAlive <= 1;
    }

    public PackagedGameState packState(int turn) {
        return new PackagedGameState(turn, units, towers, playerBases, players);
    }

    @Override
    public void render(RenderConfig config) {
        world.render(config);
        interpolatorSystem.render(config);
        renderSystem.render(config);
        healthRenderSystem.render(config); // need to be rendered before nametags, or they get covered
        renderNametagSystem.render(config);
        renderTargetingSystem.render(config);
    }

    public void setFog(boolean fog) {
        fogSystem.setFog(fog);
    }


    /**
     * This inner class maintains the wrapper information for creating the wrapped version of the gamestate
     * that is used to create and verify the hashes. It contains the encoded hash and the human readable string version
     * of everything concatenated together.
      */
    public static class PackagedGameState {

        int encodedhash;
        private String gameString;

        public PackagedGameState (int turn, Array<Unit> units, Array<Tower> towers, IntMap<Tower> bases, Array<PlayerEntity> players) {
            
            // TODO: Should use StringBuilder utility
            
            gameString = "Turn " + turn;
            Unit u;
            for (int i = 0; i < units.size; i++) {
                u = units.get(i);
                gameString += u.toString() + "\n";
            }
            gameString += "\n";

            Tower t;
            for (int i = 0 ; i < towers.size; i++) {
                t = towers.get(i);
                gameString += t.toString() + "\n";
            }
            gameString += "\n";

            Tower pb;
            for (int i = 0; i < bases.size; i ++) {
                pb = bases.get(i);
                if (pb != null) {
                    gameString += pb.toString() + "\n";
                }
            }
            gameString += "\n";

            PlayerEntity s;
            for (int i = 0; i < players.size; i++) {
                s = players.get(i);
                gameString += s.toString();
            }


            encodedhash = gameString.hashCode();
        }

        public int getHash() {
            return this.encodedhash;
        }

        public String getGameString() {
            return this.gameString;
        }
    }

    /*
     * Returns a clickable that lies on the ray, closest to the endpoint.
     * Returns null if there is no clickable.
     */
    public Clickable getClickableOnRay(Ray ray, Vector3 intersection) {

        // TODO: doesn't neccessarily find the clickable closest to the camera

        // Look through all the standard clickables
        for (Clickable clickable: clickables) {
            if (clickable.intersects(ray, intersection) && clickable.visible()) {
                return clickable;
            }
        }

        // Look through all the blocks
        Clickable clickedBlock = world.getBlockOnRay(ray, intersection);
        return clickedBlock.accept(new Clickable.ClickableVisitor<Clickable>() {
            @Override
            public Clickable acceptUnit(Unit unit) {
                return Clickable.NULL;
            }

            @Override
            public Clickable acceptBlock(Clickable.ClickableBlock block) {
                // If it's a tower block, return the tower clickable.
                for (int i = 0; i < towers.size; i++) {
                        Tower t = towers.get(i);
                        if (t.visible()) {
                            List<BlockSpec> blocks = t.getLayout();
                            for (BlockSpec spec: blocks) {
                                if ((int) (spec.getX() + t.getX()) == block.x &&
                                    (int) (spec.getZ() + t.getY()) == block.y &&
                                    (int) (spec.getY() + t.getZ()) == block.z) {
                                    return t;
                                }
                            }
                        }
                    }
                return block;
            }

            @Override
            public Clickable acceptCrystal(Crystal crystal) {
                return Clickable.NULL;
            }

            @Override
            public Clickable acceptTower(Tower t) {
                return Clickable.NULL;
            }

            @Override
            public Clickable acceptNull() {
                return Clickable.NULL;
            }
        });
    }

    /* Give the world a subscriber of selection events.*/
    public Subscriber<SelectionEvent> getSelectionSubscriber() {
        return renderTargetingSystem;
    }
}
