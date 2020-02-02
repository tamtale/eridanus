package com.week1.game.Model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.SteeringAgent;
import com.week1.game.Model.World.GameWorld;

import static com.week1.game.Model.StatsConfig.*;
import com.week1.game.Model.World.GameWorld;

public class GameState {

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

    public void render(DrawFunction drawFunc){
        for (Unit unit : units){
            if (unit.clicked) {
                drawFunc.draw(unit.getSelectedSkin(), unit.x, unit.y);
            } else {
                drawFunc.draw(unit.getUnselectedSkin(), unit.x, unit.y);
            }
            drawFunc.draw(unit.getHealthBar(), unit.x, (float)(unit.y + 1.5));
        }

        for (Tower tower : towers) {
            drawFunc.draw(tower.getSkin(), tower.x, tower.y);
        }

        for (PlayerBase playerBase : playerBases) {
            drawFunc.draw(playerBase.getSkin(), playerBase.x, playerBase.y);
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
    

    public GameWorld getWorld() {
        return world;
    }

    public boolean isInitialized() {
        return fullyInitialized;
    }
}
