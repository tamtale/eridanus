package com.week1.game.AIMovement;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Unit;

import java.util.UUID;

/**
 * Allen Iverson
 */
public class AI {

    private Array<SteeringAgent> agents;
    public AI() {
        this.agents = new Array<>();
    }

    public void spawn(Unit unit, UUID id) {
        
    }

    public void spawnTower(){}

    public void updateTarget(Unit unit, UUID id, Vector3 newTarget) {

    }

    public void buildMap(){}

    public void update() {
        //TODO:
    }

}
