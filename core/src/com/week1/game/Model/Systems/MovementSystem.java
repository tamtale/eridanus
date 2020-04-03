package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.VelocityComponent;

import java.util.HashMap;
import java.util.Map;

/*
 * System responsible for updating entities' positions on the map
 * according to their velocity.
 */
public class MovementSystem implements ISystem {

    private IntMap<MoveNode> nodeMap = new IntMap<>(); // Entity IDs are keys into their nodes.

    @Override
    public void update(float delta) {
        for (MoveNode node: nodeMap.values()) {
            VelocityComponent velocityComponent = node.velocityComponent;
            Vector3 velocity = velocityComponent.velocity;
            Vector3 position = node.positionComponent.position;
            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
            position.z += velocity.z * delta;
            velocityComponent.distTraveled += Math.sqrt(Math.pow(velocity.x * delta, 2) + Math.pow(velocity.y * delta, 2));
            // TODO add back in moving up a block
        }
    }

    public void addNode(int id, PositionComponent position, VelocityComponent velocity) {
        nodeMap.put(id, new MoveNode(position, velocity));
    }

    public boolean removeNode(int id) {
        return nodeMap.remove(id) != null;
    }

    static class MoveNode {
        MoveNode(PositionComponent position, VelocityComponent velocity) {
            this.positionComponent = position;
            this.velocityComponent = velocity;
        }
        public VelocityComponent velocityComponent;
        public PositionComponent positionComponent;
    }

}
