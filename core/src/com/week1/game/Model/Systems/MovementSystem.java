package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.VelocityComponent;

public class MovementSystem implements ISystem {

    private Array<MoveNode> moveNodes = new Array<>();

    @Override
    public void update(float delta) {
        for (MoveNode node: moveNodes) {
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

    public void add(PositionComponent position, VelocityComponent velocity) {
        moveNodes.add(new MoveNode(position, velocity));
    }

    public void remove(MoveNode node) {
        moveNodes.removeValue(node, true);
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
