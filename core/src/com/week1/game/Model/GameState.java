package com.week1.game.Model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathFinder;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.AIMovement.WarrenIndexedAStarPathFinder;
import com.week1.game.Model.World.GameGraph;
import com.week1.game.Model.World.GameWorld;

import static com.week1.game.Model.StatsConfig.*;
import static com.week1.game.Model.StatsConfig.tempTower2Cost;
import static com.week1.game.Model.TowerType.*;



public class GameState {

    private GameGraph graph;
    private PathFinder<Vector3> pathFinder;
    private Array<Unit> units;
    private int minionCount;
    private Array<Tower> towers;
    private Array<PlayerBase> playerBases;
    private Array<PlayerStat> playerStats;
    private Array<SteeringAgent> agents;
    private GameWorld world;

    private boolean fullyInitialized = false;

    public GameState(){
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange
        System.out.println("GAMESTATE");
        towers = new Array<>();
        units = new Array<>();
        Gdx.app.log("Game State - wab2", "units set");
        world = new GameWorld();
        Gdx.app.log("Game State - wab2", "world built");
        graph = world.buildGraph();
        pathFinder = new WarrenIndexedAStarPathFinder<>(graph);
        System.out.println("PATHFINDER");
        Heuristic<Vector3> heuristic = new Heuristic<Vector3>() {
            @Override
            public float estimate(Vector3 node, Vector3 endNode) {
                //TODO: Blocks have there x,y,z? do distance formula
                //System.out.println(node + " " + endNode);
                float D = 1f;
                float D2 = (float) Math.sqrt(2);
                float dx = Math.abs(node.x - endNode.x);
                float dy = Math.abs(node.y - endNode.y);
                float dz = Math.abs(node.z - endNode.z);
                return D * (dx + dy + dz) + (D2 - 2 * D) * Math.min(dx, Math.min(dy, dz));
            }
        };
        OutputPath path = new OutputPath();
        pathFinder.searchNodePath(new Vector3(1, 7, 0),
                new Vector3(9, 8, 0),
                heuristic, path);
        System.out.println(path.getPath());
        System.out.println(graph.getIndex(new Vector3(0, 0, 0)));
        System.out.println(graph.getIndex(new Vector3(0, 1, 0)));
        System.out.println(graph.getIndex(new Vector3(5, 7, 0)));
        System.out.println(graph.getConnections(new Vector3(0, 0, 0)));
        playerBases = new Array<>();
        playerStats = new Array<>();
        agents = new Array<>();

    }

    /*
     This message will come in when the network has chosen the specific number of players that
     will be in the game. It inadvertently means the game is about to start.

     This will create the bases for all of the players and give them all an amount of currency.
     */
    public void initializeGame(int numPlayers) {
        // Create the correct amount of bases.
        Gdx.app.log("GameState -pjb3", "The number of players received is " +  numPlayers);
        if (numPlayers == 1) {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 50, 50, 0));
        } else if (numPlayers == 2) {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 0, 0, 0));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 90, 90, 1));
        } else {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 0, 0, 0));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 50, 70, 1));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 0, 90, 2));
        }


        // Create the correct amount of actual players
        for (int i = 0; i < numPlayers; i++) {
            playerStats.add(new PlayerStat());
        }
        Gdx.app.log("GameState -pjb3", " Finished creating bases and Player Stats" +  numPlayers);
        fullyInitialized = true;
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
                if (unit.getX() > tower.x && unit.getX() < tower.x + tower.getSidelength() &&
                        unit.getY() > tower.y && unit.getY() < tower.y + tower.getSidelength()){
                    collide(unit);
                    Gdx.app.log("stepUnits - wab2", "Unit " + unit.ID + " collided with " + tower);
                }
            }

            for(PlayerBase base: playerBases) {
                if (unit.getX() > base.x && unit.getX() < base.x + base.getSidelength() &&
                        unit.getY() > base.y && unit.getY() < base.y + base.getSidelength()){
                    collide(unit);
                    Gdx.app.log("stepUnits - wab2", "Unit " + unit.ID + " collided with " + base);
                }
            }
        }

    }

    public void collide(Unit unit){
        Vector3 linVel = unit.agent.getLinearVelocity();
        unit.setX(unit.getX() - 2 * linVel.x);
        unit.setY(unit.getY() - 2 * linVel.y);
        unit.agent.setLinearVelocity(new Vector3(0, 0, 0));
        unit.agent.setSteeringBehavior(null);
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
//        System.out.println(u.agent);
//        System.out.println(u.ID);
        units.add(u);
        minionCount += 1;
    }

    public void addTower(Tower t) {
        towers.add(t);
    }

    public void updateGoal(Unit unit, Vector3 goal) {
//        Vector2 vec2 = new Vector2(goal.x, goal.y);
        SteeringAgent agent = unit.getAgent();
//        System.out.println(agent);
        agent.setGoal(goal);
    }
    public void addAgent(SteeringAgent a){
        agents.add(a);
    }

    public void render(Batch batch){
        for (Unit unit : units){
            unit.draw(batch);
        }

        for (Tower tower : towers) {
            batch.draw(tower.getSkin(), tower.x, tower.y);
        }

        for (PlayerBase playerBase : playerBases) {
            batch.draw(playerBase.getSkin(), playerBase.x, playerBase.y);
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

    public void moveMinion(float x, float y, int minionID) {
        for (Unit unit: units) {
//            System.out.println(unit.agent);
            if (unit.ID == minionID) {
//                System.out.println(unit.ID);
//                System.out.println(unit.agent);
                updateGoal(unit, new Vector3(x, y, 0));
            }
        }
    }
    
    public void dealDamage(float delta) {
        Array<Integer> deadUnits  = new Array<>();
        
        for (int attackerIdx = 0; attackerIdx < units.size; attackerIdx++) {
            Unit attacker = units.get(attackerIdx);
            for (int victimIdx = 0; victimIdx < units.size; victimIdx++) {
                Unit victim = units.get(victimIdx);
                
                if (!victim.equals(attacker) && // check each unit against all OTHER units
                        attacker.hasUnitInRange(victim) && // victim is within range
                        !victim.isDead() && // the victim is not already dead
                        attacker.getPlayerId() != victim.getPlayerId()) { // TODO: victim was spawned by another player

                    if (victim.takeDamage(attacker.getDamage() * delta)) {
                        deadUnits.add(victimIdx);
                    } 
                    break; // the attacker can only damage one opponent per attack cycle
                }
            }
        }
        
        for (int towerIdx = 0; towerIdx < towers.size; towerIdx++) {
            Tower tower = towers.get(towerIdx);
            for (int victimIdx = 0; victimIdx < units.size; victimIdx++) {
                Unit victim = units.get(victimIdx);

                if (tower.hasUnitInRange(victim) && // victim is within range
                        !victim.isDead() && // the victim is not already dead
                        tower.getPlayerId() != victim.getPlayerId()) { // TODO: victim was spawned by another player

                    if (victim.takeDamage(tower.getDamage() * delta)) {
                        deadUnits.add(victimIdx);
                    }
                    break; // the attacker can only damage one opponent per attack cycle
                }
            }
        }
        
        // get rid of all the dead units
        for (int deadUnitIdx : deadUnits) {
            units.removeIndex(deadUnitIdx);
        }
    }

    public double getTowerHp(TowerType towerType) {
        // TODO fill this out with dynamically sent messages. Currently it will just look up things from the current tower
        if (towerType == BASIC) {
            return tempTower1Health;
        } else if (towerType == SNIPER) {
            return tempTower2Health;
        } else {
            return tempTower3Health;
        }
    }

    public double getTowerCost(TowerType towerType) {
        // TODO fill this out with dynamically sent messages. Currently it will just look up things from the current tower
        if (towerType == BASIC) {
            return tempTower1Cost;
        } else if (towerType == SNIPER) {
            return tempTower2Cost;
        } else {
            return tempTower3Cost;
        }
    }

    public double getTowerDmg(TowerType towerType) {
        // TODO fill this out with dynamically sent messages. Currently it will just look up things from the current tower
        if (towerType == BASIC) {
            return tempTower1Damage;
        } else if (towerType == SNIPER) {
            return tempTower2Damage;
        } else {
            return tempTower3Damage;
        }
    }

    public double getTowerRange(TowerType towerType) {
        // TODO fill this out with dynamically sent messages. Currently it will just look up things from the current tower
        if (towerType == BASIC) {
            return tempTower1Range;
        } else if (towerType == SNIPER) {
            return tempTower2Range;
        } else {
            return tempTower3Range;
        }
    }

    public Pixmap getTowerPixmap(TowerType towerType) {
        // TODO fill this out with dynamically sent messages. Currently it will just look up things from the current tower
        if (towerType == BASIC) {
            return basicTexture;
        } else if (towerType == SNIPER) {
            return sniperTexture;
        } else {
            return tankTexture;
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

    public GameWorld getWorld() {
        return world;
    }

    public boolean isInitialized() {
        return fullyInitialized;
    }
}
