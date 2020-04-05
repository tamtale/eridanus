package com.week1.game.Model.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.OwnedComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.TargetingComponent;
import com.week1.game.Model.Events.DamageEvent;
import com.week1.game.Pair;
import com.week1.game.Tuple3;

import java.util.ArrayList;

/*
 * System responsible for updating targets and dispatching events.
 * On update, will refresh targets for any entity whose target is out of range.
 */
public class TargetingSystem implements ISystem, Publisher<DamageEvent> {

    private IntMap<TargetingNode> nodes = new IntMap<>();
    private ArrayList<Subscriber<DamageEvent>> damageEventSubscribers = new ArrayList<>();

    /*
     * Service to find a suitable target given a target component and position.
     * Will return a pair consisting of entity ID and position.
     * If no suitable target found, the key is -1.
     */
    private IService<Tuple3<OwnedComponent, TargetingComponent, PositionComponent>, Pair<Integer, PositionComponent>> findNearbyService;

    public TargetingSystem(IService<Tuple3<OwnedComponent, TargetingComponent, PositionComponent>, Pair<Integer, PositionComponent>> findNearbyService) {
        this.findNearbyService = findNearbyService;
    }

    private Tuple3<OwnedComponent, TargetingComponent, PositionComponent> serviceQuery = new Tuple3<>(null, null, null);

    @Override
    public void update(float delta) {
        for (IntMap.Entry<TargetingNode> entry: nodes.entries()) {
            updateNode(entry.value);
            generateDamage(entry.key, entry.value);
        }
    }

    @Override
    public void remove(int entID) {
        nodes.remove(entID);
    }

    private void updateNode(TargetingNode node) {
        TargetingComponent targetingComponent = node.targetingComponent;
        PositionComponent targetPositionComponent = node.targetPositionComponent;
        PositionComponent positionComponent = node.positionComponent;
        OwnedComponent ownedComponent = node.ownedComponent;
        // Check if the current target is still in range.
        if ((targetingComponent.targetID != -1)
            && (positionComponent.position.dst(targetPositionComponent.position) < targetingComponent.range)) return;

        if (!targetingComponent.switchTargets) return;

        serviceQuery.set(ownedComponent, targetingComponent, positionComponent);
        Pair<Integer, PositionComponent> nearby = findNearbyService.query(serviceQuery);
        targetingComponent.targetID = nearby.key;
        if (targetingComponent.targetID!= -1) {
            node.targetPositionComponent = nearby.value;
        } else {
            node.targetPositionComponent = null;
        }

    }
    private void generateDamage(int id, TargetingNode node) {
        if (node.targetingComponent.targetID == -1) return;
        DamageEvent damageEvent = new DamageEvent(id, node.targetingComponent.targetID);
        for (Subscriber<DamageEvent> subscriber: damageEventSubscribers) {
            subscriber.process(damageEvent);
        }
    }

    public void addNode(int id, OwnedComponent ownedComponent, TargetingComponent targetingComponent, PositionComponent positionComponent) {
        nodes.put(id, new TargetingNode(ownedComponent, targetingComponent, positionComponent, null));
    }

    @Override
    public void addSubscriber(Subscriber<DamageEvent> subscriber) {
        damageEventSubscribers.add(subscriber);
    }

    static class TargetingNode {
        public OwnedComponent ownedComponent;
        public TargetingComponent targetingComponent;
        public PositionComponent positionComponent;
        public PositionComponent targetPositionComponent;

        public TargetingNode(OwnedComponent ownedComponent,
                             TargetingComponent targetingComponent,
                             PositionComponent positionComponent,
                             PositionComponent targetPositionComponent) {
            this.ownedComponent = ownedComponent;
            this.targetingComponent = targetingComponent;
            this.positionComponent = positionComponent;
            this.targetPositionComponent = targetPositionComponent;
        }
    }
}
