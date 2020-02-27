package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.AIMovement.WarrenIndexedAStarPathFinder;
import com.week1.game.Model.Entities.*;
import com.week1.game.Model.World.Block;
import com.week1.game.Model.World.GameGraph;
import com.week1.game.Model.World.GameWorld;
import com.week1.game.Model.World.IWorldBuilder;
import com.week1.game.Model.Entities.*;
import com.week1.game.Pair;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.TowerBuilder.BlockSpec;
import com.week1.game.TowerBuilder.TowerDetails;
import com.week1.game.TowerBuilder.TowerPresets;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.week1.game.Model.StatsConfig.*;


public class GameState implements RenderableProvider {

    private GameGraph graph;

    private Array<Clickable> clickables = new Array<>();
    private int minionCount;
    private PathFinder<Vector3> pathFinder;
    private Array<Unit> units = new Array<>();
    private Array<Crystal> crystals = new Array<>();
    private Array<Tower> towers = new Array<>();
    private Map<Integer, Tower> playerBases = new HashMap<>(); // bases are just special towers, not their own class
    private Array<PlayerStat> playerStats = new Array<>();
    private Array<SteeringAgent> agents;
    private IWorldBuilder worldBuilder;
    private GameWorld world;
    
    private TowerLoadouts towerLoadouts;
    /*
     * Runnable to execute immediately after the game state has been initialized.
     */
    private Runnable postInit;
    private boolean fullyInitialized = false;

    public GameState(IWorldBuilder worldBuilder, Runnable postInit){
        // TODO tower types in memory after exchange
        this.worldBuilder = worldBuilder;
        world = new GameWorld(worldBuilder);
        world = new GameWorld(worldBuilder);
        world.getHeightMap();
        graph = world.buildGraph();
        for (Vector3 loc: worldBuilder.crystalLocations()) {
            crystals.add(new Crystal(loc.x, loc.y));
        }
        graph.setPathFinder(new WarrenIndexedAStarPathFinder<>(graph));
        this.postInit = postInit;
    }

    /*
     This message will come in when the network has chosen the specific number of players that
     will be in the game. It inadvertently means the game is about to start.

     This will create the bases for all of the players and give them all an amount of currency.
     */
    public void initializeGame(int numPlayers) {
        // Create the correct amount of bases.
        Gdx.app.log("GameState -pjb3", "The number of players received is " +  numPlayers);

        // Create the correct amount of actual players
        Vector3[] startLocs = worldBuilder.startLocations();
        for (int i = 0; i < numPlayers; i++) {
            playerStats.add(new PlayerStat());
            
            // Create and add a base for each player
            Tower newBase = new Tower((int) startLocs[i].x, (int) startLocs[i].y, (int) startLocs[i].z, 
                    towerLoadouts.getTowerDetails(i,-1), i, -1);
            addTower(newBase, i, true);
        }
        Gdx.app.log("GameState -pjb3", " Finished creating bases and Player Stats" +  numPlayers);
        fullyInitialized = true;
        postInit.run();
    }

//    public void removePlayerBase(int startX, int startY, PlayerBase b){
//        for(int i = startX - 4; i <= startX + 3; i++){
//            for (int j = startY - 4; j <= startY + 4; j++){
//                graph.removeAllConnections(new Vector3(i, j, 0), b);
//            }
//        }
//    }
    
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

    public void addUnit(Unit u){
        u.ID = minionCount;
        units.add(u);
        clickables.add(u);
        minionCount += 1;
    }

    public void addTower(Tower t, int playerID, boolean isBase) {
        if (isBase) {
            playerBases.put(playerID, t);
        } else {
            towers.add(t);
        }
        
        int startX = (int) t.x - 4;
        int startY = (int) t.y - 4;
        TowerFootprint footprint = towerLoadouts.getTowerDetails(playerID, t.getTowerType()).getFootprint();
        boolean[][] fp = footprint.getFp();
        int i = 0;
        for(boolean[] bool: fp){
            int j = 0;
            for(boolean boo: bool){
                if(boo){
                    graph.removeAllConnections(new Vector3(startX + i, startY + j, 0), t);
                }
                j++;
            }
            i++;
        }

        for(BlockSpec bs : t.getLayout()) {
            this.world.setBlock(
                    (int)(t.x + bs.getX()),
                    (int)(t.y + bs.getZ()),
                    (int)(t.z + bs.getY()),
                    Block.TowerBlock.towerBlockMap.get(bs.getBlockCode()));
        }

    }

    public void updateGoal(Unit unit, Vector3 goal) {
        SteeringAgent agent = unit.getAgent();
        Vector3 unitPos = new Vector3((int) unit.getX(), (int) unit.getY(), 0); //TODO: make acutal z;
        unit.setGoal(goal);
        OutputPath path = new OutputPath();
        Array<Building> buildings = this.getBuildings();

        for(Building building: buildings) {
            if(building.overlap(goal.x, goal.y)) {
                goal = building.closestPoint(unit.getX(), unit.getY());
                break;
            }
        }
        Vector3 goalPos = new Vector3(goal);

        long start = System.nanoTime();
        path = graph.search(unitPos, goalPos);
        long end = System.nanoTime();
        Gdx.app.log("GameState - wab2", "AStar completed in " + (end - start) + " nanoseconds");
        if (path != null) {
            unit.setPath(path);
        }else{
            Gdx.app.error("GameState - wab2", "Astar broke");
        }
    }
    public void render(ModelBatch modelBatch, Camera cam, RenderConfig renderConfig, int renderPlayerId) {
        modelBatch.begin(cam);
        modelBatch.render(world);
        modelBatch.end();
    }

    public void render(Batch batch, RenderConfig renderConfig, int renderPlayerId){
        boolean showAttackRadius = renderConfig.isShowAttackRadius();
        boolean showSpawnRadius = renderConfig.isShowSpawnRadius();

        Unit unit;
        for (int indx = 0; indx < units.size; indx ++) {
            unit = units.get(indx);
            unit.draw(batch, renderConfig.getDelta(), showAttackRadius);
        }

        Tower tower;
        for (int i = 0; i < towers.size; i++) {
            tower = towers.get(i);
            if (tower.getPlayerId() == renderPlayerId) {
                // Only show the spawn radius for your own tower.
                tower.draw(batch, showAttackRadius, showSpawnRadius);
            } else {
                tower.draw(batch, showAttackRadius, false);
            }
        }

        for (Tower playerBase : playerBases.values()) {
            if (playerBase.getPlayerId() == renderPlayerId) {
                // only show the spawn radius for your own base
                playerBase.draw(batch, false, showSpawnRadius);
            } else {
                playerBase.draw(batch, false, false);
            }
        }

        for (Crystal crystal : crystals) {
            crystal.draw(batch);
        }
    }

    public Array<Unit> findUnitsInBox(Vector3 cornerA, Vector3 cornerB) {
        Array<Unit> unitsToSelect = new Array<>();
        for (Unit u : units) {
            if (Math.min(cornerA.x, cornerB.x) < u.getX() && u.getX() < Math.max(cornerA.x, cornerB.x) &&
                Math.min(cornerA.y, cornerB.y) < u.getY() && u.getY() < Math.max(cornerA.y, cornerB.y)) {
                unitsToSelect.add(u);
            }
        }
        return unitsToSelect;
    }


    public Unit getMinionById(int minionId) {

        for (int i = 0; i < units.size; i++) {
            if (minionId == units.get(i).ID) {
                return units.get(i);
            }
        }

//        Gdx.app.error("getMinionById - lji1", "Unable to find minion by given ID, returning null.");
        return null;
    }
    public void moveMinion(float dx, float dy, Unit u) {
        System.out.println("u.x: " + u.getX() + " u.y: " + u.getY() + " dx: " + dx + " dy: " + dy);
        updateGoal(u, new Vector3(u.getX() + dx, u.getY() + dy, 0));
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
        everythingDamageable.addAll(getPlayerBases());
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

                    if (victim.takeDamage(attacker.getDamage() * delta)) {
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
            playerStats.get(attackingPlayerId).giveMana(deadEntity.getReward());
            // Do other bookkeeping related to death.
            deadEntity.accept(deathVisitor);        
        }
    }

    /**
     * Visitor handling when a damageable is killed.
     */
    private Damageable.DamageableVisitor<Void> deathVisitor = new Damageable.DamageableVisitor<Void>() {
        @Override
        public Void acceptTower(Tower tower) {
            towers.removeValue(tower, false);
            Map<Vector3, Array<Connection<Vector3>>> edges = tower.getRemovedEdges();
            for(Vector3 block: edges.keySet()){
                graph.setConnections(block, edges.get(block));
            }
            return null;
        }

        @Override
        public Void acceptUnit(Unit unit) {
            units.removeValue(unit, false);
            return null;
        }

  
//        @Override
//        public Void acceptBase(PlayerBase base) {
//            int deadPlayer = base.getPlayerId();
//            playerBases.removeIndex(deadPlayer);
//            playerBases.insert(deadPlayer, new DestroyedBase(0, base.getX(), base.getY(), deadPlayer));
//            return null;
//        }

        @Override
        public Void acceptCrystal(Crystal crystal) {
            // crystals don't die :^)
            return null;
        }
    };

public boolean findNearbyStructure(float x, float y, float z, int playerId) {
        // Check if it is near the home base
        if (Math.sqrt(
                Math.pow(x - playerBases.get(playerId).x, 2) + 
                Math.pow(y - playerBases.get(playerId).y, 2) + 
                Math.pow(z - playerBases.get(playerId).z, 2)) < placementRange){
            return true;
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

    public boolean overlapsExistingStructure(int playerId, int towerType, int x, int y) {
        TowerFootprint footprint = towerLoadouts.getTowerDetails(playerId, towerType).getFootprint();
        for (Tower t: towers) {
            if (TowerFootprint.overlap(footprint, x, y, towerLoadouts.getTowerDetails(t.getPlayerId(), t.getTowerType()).getFootprint(), (int)t.x, (int)t.y)) {
                return true;
            }
        }
        
        for (Tower pb: playerBases.values()) {
            // use -1 as towerType for the player base
            if (TowerFootprint.overlap(footprint, x, y, towerLoadouts.getTowerDetails(-1, -1).getFootprint(), (int)pb.x, (int)pb.y)) {
                return true;
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

        if (playerBases.get(playerId).getHp() <= 0) {
           // Yikes, you died!
            return false;
        }
        return true;
    }

    public boolean checkIfWon(int playerId) {
        if (!isPlayerAlive(playerId) || !isInitialized() || playerId == PLAYERNOTASSIGNED){
            return false; // Can't win if you're dead lol or if the game has not started
        }

        // Check if you are the last base alive
        return playerBases.values().size() == 1;
        
//        for (int playerIndex = 0; playerIndex < playerBases.size; playerIndex += 1) {
//            if (playerIndex != playerId && !playerBases.get(playerIndex).isDead()) {
//                // Since there is another placers base that is not dead yet, you have not won.
//                return false;
//            }
//        }
//        return true;
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

        int numPlayersAlive = playerBases.values().size();
//        // Check if you are the last base alive
//        for (int playerIndex = 0; playerIndex < playerBases.size; playerIndex += 1) {
//            if (!playerBases.get(playerIndex).isDead()) {
//                // Since there is another placers base that is not dead yet, you have not won.
//                numPlayersAlive += 1;
//            }
//        }
        return numPlayersAlive <= 1;
    }

    // TODO: Maps from java.util don't play well with gdx arrays
    Array<Tower> getPlayerBases() {
        Tower[] pbs = new Tower[playerBases.size()];
        playerBases.values().toArray(pbs);
        return new Array<>(pbs);
    }

    public Array<Building> getBuildings() {
        Array<Building> buildings = new Array<>();
        buildings.addAll(getPlayerBases());
        buildings.addAll(towers);
        return buildings;
    }

    public void moveUnits(float movementAmount) {
        for (Unit u: units) {
            u.step(movementAmount);
        }
    }

    public PackagedGameState packState(int turn) {
        return new PackagedGameState(turn, units, towers, getPlayerBases(), playerStats);
    }


    /**
     * This inner class maintains the wrapper information for creating the wrapped version of the gamestate
     * that is used to create and verify the hashes. It contains the encoded hash and the human readable string version
     * of everything concatenated together.
      */
    public static class PackagedGameState {

        int encodedhash;
        private String gameString;

        public PackagedGameState (int turn, Array<Unit> units, Array<Tower> towers, Array<Tower> bases, Array<PlayerStat> stats) {
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
                gameString += pb.toString() + "\n";
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

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        world.getRenderables(renderables, pool);
        Unit unit;
        for (int u = 0; u < units.size; u++) {
            unit = units.get(u);
            unit.getRenderables(renderables, pool);
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
//        Gdx.app.log("GameState.getClickableOnRay", "No clickables (units) found");
        
        // Look through all the blocks
        Clickable clickedBlock = world.getBlockOnRay(ray, intersection);
        
        return clickedBlock;
    }
}
