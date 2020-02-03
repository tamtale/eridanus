package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;



import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.Model.World.GameWorld;
import jdk.internal.net.http.common.Pair;

import static com.week1.game.Model.StatsConfig.*;
import static com.week1.game.Model.StatsConfig.tempTower2Cost;
import static com.week1.game.Model.TowerType.*;


public class GameState {

    private Array<Unit> units;
    private int minionCount;
    private Array<Tower> towers;
    private Array<PlayerBase> playerBases;
    private Array<PlayerStat> playerStats;
    private Array<SteeringAgent> agents;
    private GameWorld world;

    private int playerId; // Not part of the game state exactly, but used to determine if the game is over for this user

    private boolean fullyInitialized = false;

    public GameState(){
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange
        towers = new Array<>();
        units = new Array<>();
        world = new GameWorld();
        playerBases = new Array<>();
        playerStats = new Array<>();
        agents = new Array<>();
    }

    /*
     This message will come in when the network has chosen the specific number of players that
     will be in the game. It inadvertently means the game is about to start.

     This will create the bases for all of the players and give them all an amount of currency.
     */
    public void initializeGame(int numPlayers, int playerId) {
        this.playerId = playerId;

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
                }
            }

            for(PlayerBase base: playerBases) {
                if (unit.getX() > base.x && unit.getX() < base.x + base.getSidelength() &&
                        unit.getY() > base.y && unit.getY() < base.y + base.getSidelength()){
                    collide(unit);
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
                        deadEntities.add(new Pair(attacker, victim));
                    }
                    // the attacker can only damage one opponent per attack cycle
                    break;
                }
            }
        }

        // get rid of all the dead entities and gives rewards
        for (Pair<Damaging, Damageable> deadPair : deadEntities) {
            int attackingPlayerId = deadPair.first.getPlayerId();
            Damageable deadEntity = deadPair.second;

            if (deadEntity.getClass() == Unit.class) {
                units.removeValue((Unit)deadEntity, false);

            } else if (deadEntity.getClass() == Tower.class) {
                towers.removeValue((Tower)deadEntity, false);
                // Reward the player who destroyed the tower the mana.
                playerStats.get(attackingPlayerId).giveMana(((Tower)deadEntity).getCost() * towerDestructionBonus);

            } else {
                playerBases.removeValue((PlayerBase)deadEntity, false);
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

    public boolean isPlayerAlive() {
        if (playerBases.get(this.playerId).getHp() <= 0) {
           // Yikes, you died!
            return false;
        }
        return true;
    }

    public boolean checkIfWon() {
        if (!isPlayerAlive()){
            return false; // Can't win if you're dead lol
        }

        for (PlayerBase p : playerBases) {

            // Check if there are any other bases still alive.
            if (p.getPlayerId() != playerId && p.getHp() > 0) {
                return false; // You have not won yet.
            }
        }
        return true;
    }
}
