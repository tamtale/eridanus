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
import com.week1.game.Model.Entities.*;
import com.week1.game.AIMovement.WarrenIndexedAStarPathFinder;
import com.week1.game.Model.World.GameGraph;
import com.week1.game.Model.World.GameWorld;
import com.week1.game.Pair;


import static com.week1.game.Model.StatsConfig.*;
import static com.week1.game.Model.StatsConfig.tempTower2Cost;
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
    private GameWorld world;

    private boolean fullyInitialized = false;

    public GameState(){
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange
        towers = new Array<>();
        units = new Array<>();
        Gdx.app.log("Game State - wab2", "units set");
        world = new GameWorld();
        Gdx.app.log("Game State - wab2", "world built");
        graph = world.buildGraph();
        pathFinder = new WarrenIndexedAStarPathFinder<>(graph);
        OutputPath path = new OutputPath();
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
            playerBases.add(new PlayerBase(playerBaseInitialHp, 15, 15, 0));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 85, 85, 1));
        } else if (numPlayers == 3) {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 15, 15, 0));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 50, 85, 1));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 15, 85, 2));
        } else {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 15, 15, 0));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 85, 85, 1));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 15, 85, 2));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 85, 15, 3));
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
            tower.draw(batch);
        }

        for (PlayerBase playerBase : playerBases) {
            playerBase.draw(batch);
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
}
