package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.SteeringAgent;

import static com.week1.game.Model.StatsConfig.*;

public class GameState {

    private Array<Unit> units;
    private int minionCount;
    private Array<Tower> towers;
    private Array<PlayerBase> playerBases;
    private Array<PlayerStat> playerStats;
    private Array<SteeringAgent> agents;

    public GameState(){
        // TODO board
        // TODO tower types in memory after exchange
        towers = new Array<>();
        units = new Array<>();
        playerBases = new Array<>();
        playerStats = new Array<>();

        agents = new Array<>();

    }

    /*
     This message will come in when the network has chosen the specific number of players that
     will be in the game. It inadvertently means the game is about to start.

     This will create the bases for all of the players and give them all an amount of currency.
     */
    public void setNumPlayers(int numPlayers) {
        // Create the correct amount of bases.
        Gdx.app.log("GameState -pjb3", "The number of players received is " +  numPlayers);
        if (numPlayers == 1) {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 100, 100, 0));
        } else if (numPlayers == 2) {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 10, 190, 0));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 190, 10, 1));
        } else {
            playerBases.add(new PlayerBase(playerBaseInitialHp, 10, 190, 0));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 190, 100, 1));
            playerBases.add(new PlayerBase(playerBaseInitialHp, 40, 10, 2));
        }


        // Create the correct amount of actual players
        for (int i = 0; i < numPlayers; i++) {
            playerStats.add(new PlayerStat());
        }
        Gdx.app.log("GameState -pjb3", " Finished creating bases and Player Stats" +  numPlayers);
    }

    public PlayerStat getPlayerStats(int playerNum) {
        return playerStats.get(playerNum);
    }

    public void stepUnits(float delta) {
        for(Unit unit: units) {
            unit.step(delta);
        }
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
        Vector2 vec2 = new Vector2(goal.x, goal.y);
        SteeringAgent agent = unit.getAgent();
//        System.out.println(agent);
        agent.setGoal(vec2);
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
}
