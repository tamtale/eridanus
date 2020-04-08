package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.AIMovement.WarrenIndexedAStarPathFinder;
import com.week1.game.Model.Entities.*;
import com.week1.game.Model.World.Block;
import com.week1.game.Model.World.GameGraph;
import com.week1.game.Model.World.GameWorld;
import com.week1.game.Model.World.IWorldBuilder;
import com.week1.game.Pair;
import com.week1.game.Renderer.GameRenderable;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;
import java.util.List;

import static com.week1.game.Model.StatsConfig.*;

public class GameState implements GameRenderable {
    private final static int CRYSTAL_RESPAWN_INTERVAL = 400;
    private final static int SECONDARY_CRYSTAL_RESPAWN_INTERVAL = 10;
    
    private final Unit2StateAdapter u2s;
    private final CrystalToStateAdapter c2s;
    private GameGraph graph;

    private Array<Clickable> clickables = new Array<>();
    private int minionCount;
    private Array<Unit> units = new Array<>();
    private Array<Tower> towers = new Array<>();
    private IntMap<PlayerBase> playerBases = new IntMap<>();
    private Array<PlayerStat> playerStats = new Array<>();
    private Array<Damageable> damageables = new Array<>();
    private IWorldBuilder worldBuilder;
    private GameWorld world;
    private List<PlayerInfo> playerInfo;
    
    private Array<Crystal> crystals = new Array<>();
    private Array<Pair<Integer, Crystal>> crystalsWaitingToRespawn = new Array<>();
    
    private TowerLoadouts towerLoadouts;
    /*
     * Runnable to execute immediately after the game state has been initialized.
     */
    private Runnable postInit;
    private boolean fullyInitialized = false;
    
    private GameState getGameState() {
        return this;
    }

    public GameState(IWorldBuilder worldBuilder, Runnable postInit, List<PlayerInfo> playerInfo){
        // TODO tower types in memory after exchange
        this.playerInfo = playerInfo;
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
        this.c2s = new CrystalToStateAdapter() {
            @Override
            public void rewardPlayer(int playerId, double amt) {
                rewardPlayerById(playerId, amt);
            }
        };
        this.postInit = postInit;
    }

    public PlayerBase getPlayerBase(int playerId) {
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
            PlayerBase newBase = new PlayerBase((int) startLocs[i].x, (int) startLocs[i].y, (int) startLocs[i].z,
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
        Vector2[] crystalLocs = worldBuilder.crystalLocations();

        for (int crystalNum = 0; crystalNum < crystalLocs.length; crystalNum++) {
            Vector2 desiredLoc = crystalLocs[crystalNum];
            // Start at z = 1, since crystals shouldn't be spawned on the base layer of the map
            for (int z = 1; z < world.getWorldDimensions()[2]; z++) {
                // Place the crystal in the first available airblock
                if ((world.getBlock((int)desiredLoc.x, (int)desiredLoc.y, z) == Block.TerrainBlock.AIR) &&
                        (world.getBlock((int)desiredLoc.x, (int)(desiredLoc.y), z - 1).canSupportTower())){
                    Crystal c = new Crystal(desiredLoc.x, desiredLoc.y, z);
                    addCrystal(c);
                    break;
                }
            }
        }
        
        // If there are no suitable blocks, then maybe the crystal doesn't get placed
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
    
    public void addCrystal(Crystal c) {
        crystals.add(c);
        damageables.add(c);
        c.setCrystalToStateAdapter(c2s);
        world.setBlock((int)c.getX(), (int)c.getY(), (int)c.getZ(), Block.TerrainBlock.CRYSTAL);
    }

    public void addUnit(Unit u){
        u.ID = minionCount;
        units.add(u);
        u.setUnit2StateAdapter(u2s);
        clickables.add(u);
        damageables.add(u);
        minionCount += 1;
    }

    public void addTower(Tower t, int playerID) {
        towers.add(t);
        damageables.add(t);
        addBuilding(t, playerID);
    }

    public void addBase(PlayerBase pb, int playerID) {
        playerBases.put(playerID, pb);
        damageables.add(pb);
        addBuilding(pb, playerID);
    }

    public void addBuilding(Tower t, int playerID) {
        List<BlockSpec> blockSpecs = t.getLayout();
        for (int k = 0; k < blockSpecs.size(); k++) {
            BlockSpec bs = blockSpecs.get(k);
            world.setBlock(
                    (int)(t.x + bs.getX()),
                    (int)(t.y + bs.getZ()),
                    (int)(t.z + bs.getY()),
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
    
    public void crystalRespawn() {
        Array<Pair<Integer, Crystal>> remainingWaitingCrystals = new Array<>();
        for (int i = 0; i < crystalsWaitingToRespawn.size; i++) {
            Pair<Integer, Crystal> waitingCrystal = crystalsWaitingToRespawn.get(i);
            
            if (--waitingCrystal.key == 0) { // Decrement turns to wait, are we there yet?
                // crystal is done waiting to respawn
                
                //make sure that there aren't any towers in the way
                int tempX = (int)waitingCrystal.value.getX();
                int tempY = (int)waitingCrystal.value.getY();
                int tempZ = (int)waitingCrystal.value.getZ();
                if (world.getBlock(tempX, tempY, tempZ) != Block.TerrainBlock.AIR) {
                    // There's a tower in the way, so let the crystal wait a while longer
                    waitingCrystal.key = SECONDARY_CRYSTAL_RESPAWN_INTERVAL;
                    remainingWaitingCrystals.add(waitingCrystal);
                    
                } else {
                    // It's safe to add the crystal
                    addCrystal(waitingCrystal.value);
                }
            } else {
                // not there yet, keep waiting
                remainingWaitingCrystals.add(waitingCrystal);
            }
        }
    }

    private Array<Pair<Damaging, Damageable>> deadEntities  = new Array<>();
    private Array<Damaging> everythingDamaging = new Array<>();
    private Array<Damageable> everythingDamageable = new Array<>();
    public void dealDamage(float delta) {

        everythingDamaging.clear();
        everythingDamaging.addAll(units);
        everythingDamaging.addAll(towers);

        everythingDamageable.clear();
        everythingDamageable.addAll(units);
        everythingDamageable.addAll(towers);
        playerBases.values().forEach(everythingDamageable::add);
        everythingDamageable.addAll();
        everythingDamageable.addAll(crystals);

        deadEntities.clear();
        // Loop through all entities (units and towers) that can attack
        for (int attackerIdx = 0; attackerIdx < everythingDamaging.size; attackerIdx++) {
            Damaging attacker = everythingDamaging.get(attackerIdx);

            // Loop though all entities that can be damaged (units, towers, and bases)
            for (int victimIdx = 0; victimIdx < everythingDamageable.size; victimIdx++) {
                Damageable victim = everythingDamageable.get(victimIdx);

                if (attacker.hasTargetInRange(victim) && // victim is within range
                        !victim.isDead() && // the victim is not already dead
                        attacker.getPlayerId() != victim.getPlayerId()) {

                    if (victim.takeDamage(attacker, attacker.getDamage() * delta)) {
                        deadEntities.add(new Pair<>(attacker, victim));
                    }
                    // the attacker can only damage one opponent per attack cycle
                    break;
                }
            }
        }

        // get rid of all the dead entities and gives rewards
        for (int deadIndex = 0; deadIndex < deadEntities.size; deadIndex++) {
            Pair<Damaging, Damageable> deadPair = deadEntities.get(deadIndex);
            int attackingPlayerId = deadPair.key.getPlayerId();
            Damageable deadEntity = deadPair.value;

            // Reward mana.
            rewardPlayerById(attackingPlayerId, deadEntity.getReward());
            // Do other bookkeeping related to death.
            deadEntity.accept(deathVisitor);
        }
    }
    
    public void rewardPlayerById(int playerId, double amount) {
        playerStats.get(playerId).giveMana(amount);
    }

    public void doTowerSpecialAbilities(int communicationTurn) {
        for (int i = 0; i < towers.size; i++) {
            Tower t = towers.get(i);
            t.doSpecialEffect(communicationTurn, this);
        }
    }
    
    /**
     * Visitor handling when a damageable is killed.
     */
    private Damageable.DamageableVisitor<Void> deathVisitor = new Damageable.DamageableVisitor<Void>() {
        @Override
        public Void acceptTower(Tower t) {
            // Remove the tower from the map
            List<BlockSpec> blockSpecs = t.getLayout();
            for(int k = 0; k < blockSpecs.size(); k++) {
                BlockSpec bs = blockSpecs.get(k);
                world.setBlock(
                        (int)(t.x + bs.getX()),
                        (int)(t.y + bs.getZ()),
                        (int)(t.z + bs.getY()),
                        Block.TerrainBlock.AIR);
            }
            
            // Remove the tower from the game state
            towers.removeValue(t, true);
            damageables.removeValue(t, true);
            return null;
        }

        @Override
        public Void acceptUnit(Unit unit) {
            units.removeValue(unit, true);
            damageables.removeValue(unit, true);
            clickables.removeValue(unit, true);
            return null;
        }

        @Override
        public Void acceptBase(PlayerBase base) {
            playerBases.remove(base.getPlayerId());
            return null;
        }

        @Override
        public Void acceptCrystal(Crystal crystal) {
            // Replace the crystal with air
            Vector3 pos = new Vector3();
            crystal.getPos(pos);
            world.setBlock(
                    (int)pos.x,
                    (int)pos.y,
                    (int)pos.z,
                    Block.TerrainBlock.AIR
            );
            
            // Remove it from the GameState
            crystals.removeValue(crystal, true);
            damageables.removeValue(crystal, true);
            
            // The crystal will respawn in the same location later
            crystalsWaitingToRespawn.add(new Pair<>(CRYSTAL_RESPAWN_INTERVAL, new Crystal(crystal.getX(), crystal.getY(), crystal.getZ())));
            
            return null;
        }
    };

    public boolean findNearbyStructure(float x, float y, float z, int playerId) {
        // Check if it is near the home base
        PlayerBase base = playerBases.get(playerId);
        if (base != null) {
            if (Math.sqrt(
                Math.pow(x - base.x, 2) +
                    Math.pow(y - base.y, 2) +
                    Math.pow(z - base.z, 2)) < placementRange){
                return true;
            }
        }

        // Check if it is near any of your towers
        for (Tower t : towers) {
            if (t.getPlayerId() == playerId) {
                if (Math.sqrt(Math.pow(x - t.x, 2) + Math.pow(y - t.y, 2) + Math.pow(z - t.z, 2)) < placementRange){
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

    public void moveUnits(float movementAmount) {
        for (int i = 0; i < units.size; i++) {
            units.get(i).step(movementAmount);
        }
    }

    public PackagedGameState packState(int turn) {
        return new PackagedGameState(turn, units, towers, playerBases, playerStats);
    }

    @Override
    public void render(RenderConfig config) {
        world.render(config);
        ModelBatch modelBatch = config.getModelBatch();
        Batch batch2D = config.getBatch();

        // Render Units
        modelBatch.begin(config.getCam());
        for (int i = 0; i < units.size; i++) {
            units.get(i).render(config);
        }
        modelBatch.end();

        // Render overlay stuff
        batch2D.begin();
        for (int i = 0; i < damageables.size; i++) {
            damageables.get(i).drawHealthBar(config);
        }
        
        Array<PlayerBase> bases = playerBases.values().toArray();
        for (int i = 0; i < bases.size; i++) {
            bases.get(i).drawName(config, playerInfo.get(bases.get(i).getPlayerId()).getPlayerName());
        }
        
        batch2D.end();
    }


    /**
     * This inner class maintains the wrapper information for creating the wrapped version of the gamestate
     * that is used to create and verify the hashes. It contains the encoded hash and the human readable string version
     * of everything concatenated together.
      */
    public static class PackagedGameState {

        int encodedhash;
        private String gameString;

        public PackagedGameState (int turn, Array<Unit> units, Array<Tower> towers, IntMap<PlayerBase> bases, Array<PlayerStat> stats) {
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
