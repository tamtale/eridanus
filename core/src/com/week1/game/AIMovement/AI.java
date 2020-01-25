package com.week1.game.AIMovement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        Vector2 vec2 = new Vector2(newTarget.x, newTarget.y);
        SteeringAgent agent = unitAgentMap.get(unit.ID);
        agent.setGoal(vec2);
    }

    public void buildMap(){}

    public void update(float delta) {
        //TODO:

        for (SteeringAgent agent: agents) {
            agent.update(delta);
        }
    }

}
