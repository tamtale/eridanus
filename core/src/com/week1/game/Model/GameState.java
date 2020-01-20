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

    public void addUnit(Unit u){
        units.add(u);
    }

    public void addAgent(SteeringAgent a){
        agents.add(a);
    }
}
