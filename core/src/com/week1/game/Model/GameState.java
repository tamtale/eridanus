package com.week1.game.Model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.SteeringAgent;

public class GameState {

    private Array<Unit> units;
    private int minionCount;
    private GameEngine engine;
    private Array<SteeringAgent> agents;

    public GameState(GameEngine engine){
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange

        units = new Array<>();
        units.add(new Unit(20, 20));
        agents = new Array<>();
        this.engine = engine;
    }

    public void stepUnits(float delta) {
        for(Unit unit: units) {
            //System.out.println("from step " + agent.getSteeringOutput().linear);
            unit.step(delta);
        }
    }

    public void addUnit(Unit u){
        units.add(u);
        u.ID = minionCount;
        minionCount += 1;
        engine.spawn(u);
    }

    public void updateGoal(Unit unit, Vector3 goal) {
        engine.updateGoal(unit, goal);
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
            if (unit.ID == minionID) {
                engine.updateGoal(unit, new Vector3(x, y, 0));
            }
        }
    }
}
