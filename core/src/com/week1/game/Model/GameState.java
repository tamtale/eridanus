package com.week1.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.SteeringAgent;

public class GameState {

    private Array<Unit> units;
    private Array<Tower> towers;
    private Array<PlayerBase> playerBases;
    private Array<SteeringAgent> agents;

    public GameState(){
        // TODO board
        // TODO player data
        // TODO towers
        // TODO tower types in memory after exchange
        towers = new Array<>();
        units = new Array<>();
        units.add(new Unit(20, 20));

        playerBases = new Array<>();
        playerBases.add(new PlayerBase(100, 10, 190, 0));
        playerBases.add(new PlayerBase(100, 190, 10, 0));

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
    
    public void addTower(Tower t) {
        towers.add(t);
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
}
