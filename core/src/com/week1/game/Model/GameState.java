package com.week1.game.Model;

import com.badlogic.gdx.utils.Array;
import com.week1.game.SteeringAgent;

public class GameState {

    private Array<Unit> units;
    private Array<SteeringAgent> agents;

    public GameState(){
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange

        units = new Array<>();
        agents = new Array<>();
    }

    public void stepUnits(float delta) {
        for(Unit unit: units) {
            //System.out.println("from step " + agent.getSteeringOutput().linear);
            unit.step(delta);
        }
    }

    public void addUnit(Unit u){
        units.add(u);
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
}
