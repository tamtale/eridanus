package com.week1.game.AIMovement;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Entities.Unit;

import java.util.HashMap;
import java.util.Map;

/**
 * Allen Iverson
 */
public class AI {

    private Array<SteeringAgent> agents;
    private Map<Integer, SteeringAgent> unitAgentMap;
    public AI() {
        this.agents = new Array<>();
        this.unitAgentMap = new HashMap<>();
    }

    public void spawn(Unit unit) {
        SteeringAgent agent = new SteeringAgent(unit);
        agents.add(agent);
        unitAgentMap.put(unit.ID, agent);
    }

    public void spawnTower(){}

    public void updateTarget(Unit unit, Vector3 newTarget) {
        Vector3 vec3 = new Vector3(newTarget.x, newTarget.y, 0); //TODO: actually make 3D
        SteeringAgent agent = unitAgentMap.get(unit.ID);
        agent.setGoal(vec3);
    }

    public void buildMap(){}

    public void update(float delta) {
        //TODO:

        for (SteeringAgent agent: agents) {
            agent.update(delta);
        }
    }

}
