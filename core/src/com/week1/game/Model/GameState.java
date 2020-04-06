package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.AIMovement.WarrenIndexedAStarPathFinder;
import com.week1.game.Model.Components.*;
import com.week1.game.Model.Entities.*;
import com.week1.game.Model.Systems.*;
import com.week1.game.Model.World.Block;
import com.week1.game.Model.World.GameGraph;
import com.week1.game.Model.World.GameWorld;
import com.week1.game.Model.World.IWorldBuilder;
import com.week1.game.Pair;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;
import com.week1.game.Tuple3;

import java.util.List;

import static com.week1.game.MenuScreens.GameScreen.THRESHOLD;
import static com.week1.game.Model.StatsConfig.*;

public class GameState implements GameRenderable {
    private final Unit2StateAdapter u2s;
    private GameGraph graph;
    private Array<Clickable> clickables = new Array<>();
    private Array<Unit> units = new Array<>();
    private Array<Crystal> crystals = new Array<>();
    private Array<Tower> towers = new Array<>();
    private IntMap<Tower> playerBases = new IntMap<>();
    private Array<PlayerStat> playerStats = new Array<>();
    private IWorldBuilder worldBuilder;
    private GameWorld world;
    private MovementSystem movementSystem = new MovementSystem();
    private PathfindingSystem pathfindingSystem;
    private RenderSystem renderSystem = new RenderSystem();
    private TargetingSystem targetingSystem;
    private DeathSystem deathSystem;
    private DamageSystem damageSystem = new DamageSystem();
    private EntityManager entityManager = new EntityManager();

    private TowerLoadouts towerLoadouts;
    /*
     * Runnable to execute immediately after the game state has been initialized.
     */
    private Runnable postInit;
    private boolean fullyInitialized = false;

    public GameState(IWorldBuilder worldBuilder, Runnable postInit){
        // TODO tower types in memory after exchange
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
        targetingSystem.addSubscriber(damageSystem);
        damageSystem.addSubscriber(deathSystem);
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
                // Check all the units for the closest suitable target.
                for (Unit unit: units) {
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
                return result;
            }
        });
    }

    private void initDeathSystem() {
        this.deathSystem = new DeathSystem(new IService<Integer, Void>() {
            @Override
            public Void query(Integer key) {
                removeEntity(key);
                return null;
            }
        });
    }

    public void synchronousUpdateState(int communicationTurn) {
        updateMana(1);
        pathfindingSystem.update(THRESHOLD);
        movementSystem.update(THRESHOLD);
        targetingSystem.update(THRESHOLD);
        renderSystem.update(THRESHOLD);
        damageSystem.update(THRESHOLD);
        deathSystem.update(THRESHOLD);
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
            worldBuilder.addSeed(mapSeed);
            world = new GameWorld(worldBuilder);
            world.getHeightMap();
            graph = world.buildGraph();
            for (Vector3 loc: worldBuilder.crystalLocations()) {
                crystals.add(new Crystal(loc.x, loc.y));
            }
            graph.setPathFinder(new WarrenIndexedAStarPathFinder<>(graph));
            
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

        // Create the correct amount of bases.
        Gdx.app.log("GameState -pjb3", "The number of players received is " +  numPlayers);

        // Create the correct amount of actual players
        Vector3[] startLocs = worldBuilder.startLocations();
        for (int i = 0; i < numPlayers; i++) {
            playerStats.add(new PlayerStat());

            // Create and add a base for each player
            Tower newBase = addTower((int) startLocs[i].x, (int) startLocs[i].y, (int) startLocs[i].z,
                    towerLoadouts.getTowerDetails(i,-1), i, -1);
            addBase(newBase, i);
        }
        Gdx.app.log("GameState -pjb3", " Finished creating bases and Player Stats" +  numPlayers);
        fullyInitialized = true;
        postInit.run();
    }

    public PlayerStat getPlayerStats(int playerNum) {
        if (isInitialized()) {
            return playerStats.get(playerNum);
        } else {
            return PlayerStat.BLANK;
        }
    }

    public void updateMana(float amount){
        for (PlayerStat player : playerStats) {
            player.regenMana(amount);
        }
    }

    public Unit addUnit(float x, float y, float z, float tempHealth, int playerID){
        PositionComponent positionComponent = new PositionComponent(x, y, z);
        VelocityComponent velocityComponent = new VelocityComponent((float) Unit.speed, 0, 0, 0);
        PathComponent pathComponent = new PathComponent();
        RenderComponent renderComponent = new RenderComponent(new ModelInstance(Unit.modelMap.get(playerID)));
        OwnedComponent ownedComponent = new OwnedComponent(playerID);
        TargetingComponent targetingComponent = new TargetingComponent(-1, (float) tempMinionRange, true, TargetingComponent.TargetingStrategy.ENEMY);
        HealthComponent healthComponent = new HealthComponent(tempHealth, tempHealth);
        DamagingComponent damagingComponent = new DamagingComponent((float) tempDamage);
        Unit u = new Unit(positionComponent, velocityComponent, pathComponent, renderComponent, ownedComponent, targetingComponent, healthComponent, damagingComponent);
        u.ID = entityManager.newID();
        units.add(u);
        movementSystem.addNode(u.ID, positionComponent, velocityComponent);
        pathfindingSystem.addNode(u.ID, positionComponent, velocityComponent, pathComponent);
        renderSystem.addNode(u.ID, renderComponent, positionComponent, velocityComponent);
        targetingSystem.addNode(u.ID, ownedComponent, targetingComponent, positionComponent);
        damageSystem.addHealth(u.ID, healthComponent);
        damageSystem.addDamage(u.ID, damagingComponent);
        clickables.add(u);
        return u;
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
        renderSystem.remove(id);
        units.select(u -> u.ID == id).forEach(unit -> units.removeValue(unit, true));
        towers.select(t -> t.ID == id).forEach(tower -> {
            List<BlockSpec> blockSpecs = tower.getLayout();
            for(int k = 0; k < blockSpecs.size(); k++) {
                BlockSpec bs = blockSpecs.get(k);
                world.setBlock(
                    (int)(tower.getX() + bs.getX()),
                    (int)(tower.getY() + bs.getZ()),
                    (int)(tower.getZ() + bs.getY()),
                    Block.TerrainBlock.AIR);
            }
            towers.removeValue(tower, true);
            if (playerBases.containsValue(tower, true)) {
                playerBases.remove(tower.getPlayerID());
            }
        });

    }

    public Tower addTower(int x, int y, int z, TowerDetails towerDetails, int playerID, int towerType) {
        PositionComponent positionComponent = new PositionComponent((float) x, (float) y, (float) z);
        HealthComponent healthComponent = new HealthComponent((float) towerDetails.getHp(), (float) towerDetails.getHp());
        DamagingComponent damagingComponent = new DamagingComponent((float) towerDetails.getAtk());
        TargetingComponent targetingComponent = new TargetingComponent(-1, (float) towerDetails.getRange(), true,
            TargetingComponent.TargetingStrategy.ENEMY);
        OwnedComponent ownedComponent = new OwnedComponent(playerID);
        Tower tower = new Tower(positionComponent, healthComponent, damagingComponent, targetingComponent, ownedComponent, towerDetails, towerType, entityManager.newID());
        targetingSystem.addNode(tower.ID, ownedComponent, targetingComponent, positionComponent);
        damageSystem.addHealth(tower.ID, healthComponent);
        damageSystem.addDamage(tower.ID, damagingComponent);
        towers.add(tower);
        addBuilding(tower, playerID);
        return tower;
    }

    public void addBase(Tower pb, int playerID) {
        playerBases.put(playerID, pb);
        addBuilding(pb, playerID);
    }

    public void addBuilding(Tower t, int playerID) {
        List<BlockSpec> blockSpecs = t.getLayout();
        for (int k = 0; k < blockSpecs.size(); k++) {
            BlockSpec bs = blockSpecs.get(k);
            this.world.setBlock(
                    (int)(t.getX() + bs.getX()),
                    (int)(t.getY() + bs.getZ()),
                    (int)(t.getZ() + bs.getY()),
                    Block.TowerBlock.towerBlockMap.get(bs.getBlockCode()));
        }
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

    private Array<Pair<Damaging, Damageable>> deadEntities  = new Array<>();

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

    public Array<Building> getBuildings() {
        Array<Building> buildings = new Array<>();
//        buildings.addAll(getPlayerBases());
        playerBases.values().forEach(buildings::add);
        buildings.addAll(towers);
        return buildings;
    }

    public PackagedGameState packState(int turn) {
        return new PackagedGameState(turn, units, towers, playerBases, playerStats);
    }

    @Override
    public void render(RenderConfig config) {
        world.render(config);
        ModelBatch modelBatch = config.getModelBatch();
        Batch batch2D = config.getBatch();

        renderSystem.render(config);

        // TODO Render overlay stuff
    }


    /**
     * This inner class maintains the wrapper information for creating the wrapped version of the gamestate
     * that is used to create and verify the hashes. It contains the encoded hash and the human readable string version
     * of everything concatenated together.
      */
    public static class PackagedGameState {

        int encodedhash;
        private String gameString;

        public PackagedGameState (int turn, Array<Unit> units, Array<Tower> towers, IntMap<Tower> bases, Array<PlayerStat> stats) {
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

            PlayerStat s;
            for (int i = 0; i < stats.size; i++) {
                s = stats.get(i);
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
            if (clickable.intersects(ray, intersection)) {
                return clickable;
            }
        }

        // Look through all the blocks
        Clickable clickedBlock = world.getBlockOnRay(ray, intersection);

        return clickedBlock;
    }
}
