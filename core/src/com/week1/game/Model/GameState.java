package com.week1.game.Model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.SteeringAgent;

public class GameState {

    private Array<Unit> units;
    private int minionCount;
    private Array<SteeringAgent> agents;

    public GameState(){
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange

        units = new Array<>();
//        units.add(new Unit(20, 20));
        agents = new Array<>();
    }

    public void stepUnits(float delta) {
        for(Unit unit: units) {
            //System.out.println("from step " + agent.getSteeringOutput().linear);
            unit.step(delta);
        }
    }

    public void addUnit(Unit u){
        Gdx.app.setLogLevel(Application.LOG_NONE);

        SteeringAgent agent = new SteeringAgent(u);
        u.agent = agent;
        u.ID = minionCount;
//        System.out.println(u.agent);
//        System.out.println(u.ID);
        units.add(u);
        minionCount += 1;
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
            System.out.println(units.size);
//            System.out.println(unit.agent);
            if (unit.ID == minionID) {
//                System.out.println(unit.ID);
//                System.out.println(unit.agent);
                updateGoal(unit, new Vector3(x, y, 0));
            }
        }
    }
}
