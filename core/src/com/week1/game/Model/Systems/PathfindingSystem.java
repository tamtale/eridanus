package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PathComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.VelocityComponent;
import com.week1.game.Model.Unit2StateAdapter;

/*
 * System responsible for maintaining entities' paths toward some goal.
 */
public class PathfindingSystem implements ISystem {

    private IntMap<PathfindingNode> nodeMap = new IntMap<>(); // Entity IDs are keys to their nodes.
    private Unit2StateAdapter stateAdapter;

    public PathfindingSystem(Unit2StateAdapter stateAdapter) {
        this.stateAdapter = stateAdapter;
    }

    public void addNode(int id, PositionComponent positionComponent, VelocityComponent velocityComponent, PathComponent pathComponent) {
        nodeMap.put(id, new PathfindingNode(positionComponent, velocityComponent, pathComponent));
    }

    @Override
    public void update(float delta) {
        for (PathfindingNode node: nodeMap.values()) {
            updateNode(delta, node);
        }
    }

    @Override
    public void remove(int entID) {
        nodeMap.remove(entID);
    }

    private Vector2 vecToPath = new Vector2();
    private void updateNode(float delta, PathfindingNode node) {
        PathComponent pathComponent = node.pathComponent;
        VelocityComponent velocityComponent = node.velocityComponent;
        PositionComponent positionComponent = node.positionComponent;
        if (velocityComponent.distTraveled >= pathComponent.distanceToNext) {
            if ((pathComponent.path == null) || (pathComponent.path.getCount() <= 0)) {
                pathComponent.path = null;
                velocityComponent.velocity.x = 0;
                velocityComponent.velocity.y = 0;
                return;
            }

            Vector2 nextStep = pathComponent.path.get(0);
            vecToPath.set(nextStep.x - positionComponent.position.x, nextStep.y - positionComponent.position.y);
            int height = stateAdapter.getHeight((int) positionComponent.position.x, (int) positionComponent.position.y);
            int nxtHeight = stateAdapter.getHeight((int) pathComponent.path.get(0).x, (int) pathComponent.path.get(0).y);
            float blockSpeed = 1f / stateAdapter.getBlock((int) positionComponent.position.x, (int) positionComponent.position.y,
                    height).getCost();
            positionComponent.position.z = nxtHeight + 1;
            float distanceToNext = (float) Math.sqrt(Math.pow(vecToPath.x, 2f) + Math.pow(vecToPath.y, 2f));
            pathComponent.distanceToNext = distanceToNext;
            double angle = Math.atan(vecToPath.y / vecToPath.x);
            if (vecToPath.x < 0) {
                angle += Math.PI;
            } else if (vecToPath.y < 0) {
                angle += 2 * Math.PI;
            }
            velocityComponent.velocity.x = blockSpeed * (float) velocityComponent.baseSpeed * (float) Math.cos(angle);
            velocityComponent.velocity.y = blockSpeed * (float) velocityComponent.baseSpeed * (float) Math.sin(angle);
            pathComponent.path.removeIndex(0);
            velocityComponent.distTraveled = 0;
            // TODO look over/redo the arrival detection mechanism

        }
    }

    static class PathfindingNode {

        public PathComponent pathComponent;
        public PositionComponent positionComponent;
        public VelocityComponent velocityComponent;

        public PathfindingNode(PositionComponent positionComponent,
                               VelocityComponent velocityComponent,
                               PathComponent pathComponent) {
            this.pathComponent = pathComponent;
            this.positionComponent = positionComponent;
            this.velocityComponent = velocityComponent;
        }
    }
}
