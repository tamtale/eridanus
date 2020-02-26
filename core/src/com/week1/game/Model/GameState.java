package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.AIMovement.WarrenIndexedAStarPathFinder;
import com.week1.game.Model.Entities.*;
import com.week1.game.Model.World.Block;
import com.week1.game.Model.World.GameGraph;
import com.week1.game.Model.World.GameWorld;
import com.week1.game.Model.World.IWorldBuilder;
import com.week1.game.Pair;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.TowerBuilder.TowerDetails;

import java.sql.Time;
import java.time.Instant;
import java.time.Period;
import java.util.Map;

import static com.week1.game.Model.Entities.TowerType.BASIC;
import static com.week1.game.Model.Entities.TowerType.SNIPER;
import static com.week1.game.Model.StatsConfig.*;
import static com.week1.game.Model.Entities.TowerType.*;


public class GameState {

    private GameGraph graph;
    private PathFinder<Vector3> pathFinder;
    private Array<Unit> units;
    private int minionCount;
    private Array<Tower> towers;
    private Array<PlayerBase> playerBases;
    private Array<PlayerStat> playerStats;
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
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange
        this.worldBuilder = worldBuilder;
        towers = new Array<>();
        units = new Array<>();
        Gdx.app.log("Game State - wab2", "units set");
        world = new GameWorld(worldBuilder);
        Gdx.app.log("Game State - wab2", "world built");
        world.getHeightMap();
        graph = world.buildGraph();
        graph.setPathFinder(new WarrenIndexedAStarPathFinder<>(graph));
        Unit.unit2StateAdapter = new Unit2StateAdapter() {
            @Override
            public Block getBlock(int i, int j, int k) {
                return world.getBlock(i, j, k);
            }

            @Override
            public int getHeight(int i, int j) {
                return world.getHeight(i, j);
            }
        };
//        graph.search(new Vector3(0, 0, 0), new Vector3(1, 1, 0));
//        pathFinder = new WarrenIndexedAStarPathFinder<>(graph);
        OutputPath path = new OutputPath();

        //graph.getPathFinder().searchNodePath(new Vector3(0, 0, 0), new Vector3(1, 1, 0), new GameHeuristic(), path);
        playerBases = new Array<>();
        playerStats = new Array<>();
        agents = new Array<>();
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
            PlayerBase base = new PlayerBase(playerBaseInitialHp, (int) startLocs[i].x, (int) startLocs[i].y, i);
            playerBases.add(base);
            removePlayerBase((int) startLocs[i].x, (int) startLocs[i].y, base);
        }
        Gdx.app.log("GameState -pjb3", " Finished creating bases and Player Stats" +  numPlayers);
        fullyInitialized = true;
        postInit.run();
    }

    public void removePlayerBase(int startX, int startY, PlayerBase b){
        for(int i = startX - 4; i <= startX + 3; i++){
            for (int j = startY - 4; j <= startY + 4; j++){
                graph.removeAllConnections(new Vector3(i, j, 0), b);
            }
        }
    }
    public PlayerStat getPlayerStats(int playerNum) {
        if (isInitialized()) {
            return playerStats.get(playerNum);
        } else {
            return PlayerStat.BLANK;
        }
    }

    public void stepUnits(float delta) {
        for(Unit unit: units) {
            //System.out.println("from step " + agent.getSteeringOutput().linear);
            unit.step(delta);
            for(Tower tower: towers) {
                if ((unit.getX() > tower.x - (tower.getSidelength() / 2f) + 0.5f) &&
                        (unit.getX() < tower.x + (tower.getSidelength() / 2f) + 0.5f) &&
                        (unit.getY() > tower.y - (tower.getSidelength() / 2f) + 0.5f) &&
                        (unit.getY() < tower.y + (tower.getSidelength() / 2f) + 0.5f)) {
                    collide(unit);
                    Gdx.app.log("stepUnits - wab2", "Unit " + unit.ID + " collided with " + tower);
                }
            }

            for(PlayerBase base: playerBases) {
                if ((unit.getX() > base.x - (base.getSidelength() / 2f)) &&
                        (unit.getX() < base.x + (base.getSidelength() / 2f)) &&
                        (unit.getY() > base.y - (base.getSidelength() / 2f)) &&
                        (unit.getY() < base.y + (base.getSidelength() / 2f))) {
                    collide(unit);
                    Gdx.app.log("stepUnits - wab2", "Unit " + unit.ID + " collided with " + base);
                }
            }
        }

    }

    public void collide(Unit unit){
//        unit.setPosition(unit.x - 2 * unit.getVelocityX(), unit.y - 2 * unit.getVelocityY());
//        System.out.println("changing position");
//        OutputPath path = unit.getPath();
//        Array<Vector3> pathArray = path.getPath();
//        Vector3 goal = pathArray.get(pathArray.size - 1);
//        System.out.println("SEARCHING");
//        OutputPath newPath = graph.search(new Vector3(unit.x, unit.y, 0), goal);
//        System.out.println("SEARCHED");
//        System.out.println(newPath);
//        unit.setPath(newPath);
    }
    public void updateMana(float amount){
        for (PlayerStat player : playerStats) {
            player.regenMana(amount);
        }
    }

    public void addUnit(Unit u){

        
        SteeringAgent agent = new SteeringAgent(u);
        u.agent = agent;
        u.ID = minionCount;
        units.add(u);
        minionCount += 1;
    }

    public void addTower(Tower t, int playerID) {
        towers.add(t);
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
    }

    public void updateGoal(Unit unit, Vector3 goal) {
        SteeringAgent agent = unit.getAgent();
        Vector3 unitPos = new Vector3((int) unit.x, (int) unit.y, 0); //TODO: make acutal z;
        unit.setGoal(goal);
        OutputPath path = new OutputPath();
        Array<Building> buildings = this.getBuildings();

        for(Building building: buildings) {
            if(building.overlap(goal.x, goal.y)) {
                goal = building.closestPoint(unit.x, unit.y);
                break;
            }
        }
        Vector3 goalPos = new Vector3((int) goal.x, (int) goal.y, (int) goal.z);

        long start = System.nanoTime();
        path = graph.search(unitPos, goalPos);
        long end = System.nanoTime();
        Gdx.app.log("wab2 - ASTAR", "AStar completed in " + (end - start) + " nanoseconds");
        if (path != null) {
            unit.setPath(path);
        }else{
            Gdx.app.error("wab2 - ASTAR", "Astar broke");
        }
        agent.setGoal(goal);
    }
    public void addAgent(SteeringAgent a){
        agents.add(a);
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

        PlayerBase playerBase;
        for (int i = 0; i < playerBases.size; i++) {
            playerBase = playerBases.get(i);
            if (playerBase.getPlayerId() == renderPlayerId) {
                // only show the spawn radius for your own base
                playerBase.draw(batch, showSpawnRadius);
            } else {
                playerBase.draw(batch, false);
            }
        }
    }

    public Unit findUnit(Vector3 position) {
        for (Unit unit: units) {
           if (unit.contains(position.x, position.y))  {
               return unit;
           }
        }
        return null;
    }

    public Array<Unit> findUnitsInBox(Vector3 cornerA, Vector3 cornerB) {
        Array<Unit> unitsToSelect = new Array<>();
        for (Unit u : units) {
            if (Math.min(cornerA.x, cornerB.x) < u.x && u.x < Math.max(cornerA.x, cornerB.x) &&
                Math.min(cornerA.y, cornerB.y) < u.y && u.y < Math.max(cornerA.y, cornerB.y)) {
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
        System.out.println("u.x: " + u.x + " u.y: " + u.y + " dx: " + dx + " dy: " + dy);
        updateGoal(u, new Vector3(u.x + dx, u.y + dy, 0));
    }

    public void dealDamage(float delta) {
        Array<Pair<Damaging, Damageable>> deadEntities  = new Array<>();

        Array<Damaging> everythingDamaging = new Array<>(units);
        everythingDamaging.addAll(towers);

        Array<Damageable> everythingDamageable = new Array<>(units);
        everythingDamageable.addAll(towers);
        everythingDamageable.addAll(playerBases);

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

            if (deadEntity.getClass() == Unit.class) {
                units.removeValue((Unit)deadEntity, false);

            } else if (deadEntity.getClass() == Tower.class) {
                towers.removeValue((Tower)deadEntity, false);
                Map<Vector3, Array<Connection<Vector3>>> edges = ((Tower) deadEntity).getRemovedEdges();
                for(Vector3 block: edges.keySet()){
                    graph.setConnections(block, edges.get(block));
                }
                // Reward the player who destroyed the tower the mana.
                playerStats.get(attackingPlayerId).giveMana(((Tower)deadEntity).getCost() * towerDestructionBonus);

            } else {
                int deadPlayer = deadEntity.getPlayerId();
                playerBases.removeIndex(deadPlayer);

                playerBases.insert(deadPlayer, new DestroyedBase(0, deadEntity.getX(), deadEntity.getY(), deadPlayer));
                // Reward the player who destroyed the base a lump sum
                playerStats.get(attackingPlayerId).giveMana((playerBaseBonus));
            }
        }
    }
  
    public boolean findNearbyStructure(float x, float y, int playerId) {
        // Check if it is near the home base
        if (Math.sqrt(Math.pow(x - playerBases.get(playerId).x, 2) + Math.pow(y - playerBases.get(playerId).y, 2)) < placementRange){
            return true;
        }

        // Check if it is near any of your towers
        for (Tower t : towers) {
            if (t.getPlayerId() == playerId) {
                if (Math.sqrt(Math.pow(x - t.x, 2) + Math.pow(y - t.y, 2)) < placementRange){
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
        
        for (PlayerBase pb: playerBases) {
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
        for (int playerIndex = 0; playerIndex < playerBases.size; playerIndex += 1) {
            if (playerIndex != playerId && !playerBases.get(playerIndex).isDead()) {
                // Since there is another placers base that is not dead yet, you have not won.
                return false;
            }
        }
        return true;
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

        int numPlayersAlive = 0;
        // Check if you are the last base alive
        for (int playerIndex = 0; playerIndex < playerBases.size; playerIndex += 1) {
            if (!playerBases.get(playerIndex).isDead()) {
                // Since there is another placers base that is not dead yet, you have not won.
                numPlayersAlive += 1;
            }
        }
        return numPlayersAlive <= 1;
    }

    Array<PlayerBase> getPlayerBases() {
        return playerBases;
    }

    public Array<Building> getBuildings() {
        Array<Building> buildings = new Array<>();
        buildings.addAll(playerBases);
        buildings.addAll(towers);
        return buildings;
    }

    public void moveUnits(float movementAmount) {
        for (Unit u: units) {
            u.step(movementAmount);
        }
    }

    public PackagedGameState packState() {
        return new PackagedGameState(units, towers, playerBases, playerStats);
    }


    /**
     * This inner class maintains the wrapper information for creating the wrapped version of the gamestate
     * that is used to create and verify the hashes. It contains the encoded hash and the human readable string version
     * of everything concatenated together.
      */
    public static class PackagedGameState {

        int encodedhash;
        private String gameString;

        public PackagedGameState (Array<Unit> units, Array<Tower> towers, Array<PlayerBase> bases, Array<PlayerStat> stats) {
            gameString = "";
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

            PlayerBase pb;
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
}
