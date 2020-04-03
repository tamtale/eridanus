package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.TargetingComponent;
import com.week1.game.Pair;

/*
 * System responsible for updating targets.
 * On update, will refresh targets for any entity whose target is out of range.
 */
public class TargetingSystem implements ISystem {

    private IntMap<TargetingNode> nodes = new IntMap<>();

    /*
     * Service to find a suitable target given a TargetingNode.
     * Will return a pair consisting of entity ID and position.
     * If no suitable target found, the key is -1.
     */
    private IService<TargetingNode, Pair<Integer, PositionComponent>> findNearbyService;

    public TargetingSystem(IService<TargetingNode, Pair<Integer, PositionComponent>> findNearbyService) {
        this.findNearbyService = findNearbyService;
    }

    @Override
    public void update(float delta) {
        for (TargetingNode node: nodes.values()) {
            updateNode(node);
        }
    }

    private void updateNode(TargetingNode node) {
        TargetingComponent targetingComponent = node.targetingComponent;
        // Check if the current target is still in range.
        if (node.positionComponent.position.dst(node.targetPositionComponent.position) < targetingComponent.range) return;
        if (!targetingComponent.switchTargets) return;
        Pair<Integer, PositionComponent> nearby = findNearbyService.query(node);
        targetingComponent.targetID = nearby.key;
        if (nearby.key != -1) {
            node.targetPositionComponent = nearby.value;
        }
    }

    public void addNode(int id, PositionComponent positionComponent, TargetingComponent targetingComponent) {
        nodes.put(id, new TargetingNode(positionComponent, targetingComponent));
    }

    public boolean removeNode(int id) {
        return (nodes.remove(id) != null);
    }

    static class TargetingNode {
        public TargetingComponent targetingComponent;
        public PositionComponent positionComponent;
        public PositionComponent targetPositionComponent;
        public TargetingNode(PositionComponent positionComponent, TargetingComponent targetingComponent) {
            this.positionComponent = positionComponent;
            this.targetingComponent = targetingComponent;
        }
    }
}
