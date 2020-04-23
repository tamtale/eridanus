package com.week1.game.Model.Systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.OwnedComponent;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.TargetingComponent;
import com.week1.game.Model.Events.DamageEvent;
import com.week1.game.Model.Events.SelectionEvent;
import com.week1.game.Model.Initializer;
import com.week1.game.Networking.Messages.Game.TargetMessage;
import com.week1.game.Pair;
import com.week1.game.Renderer.RenderConfig;
import com.week1.game.Tuple3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * System responsible for updating targets and dispatching events.
 * On update, will refresh targets for any entity whose target is out of range.
 */
public class TargetingSystem implements ISystem, Subscriber<TargetMessage>, Publisher<DamageEvent> {

    private IntMap<TargetingNode> nodes = new IntMap<>();
    private IntMap<PositionComponent> positions = new IntMap<>(); // positions of all targetable things.
    private ArrayList<Subscriber<DamageEvent>> damageEventSubscribers = new ArrayList<>();
    private Queue<TargetMessage> targetMessages = new ConcurrentLinkedQueue<>();

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
        // Switch targets according to whatever's in the queue.
        for (TargetMessage msg: targetMessages) {
            for (int id: msg.minionIDs) {
                TargetingNode node = nodes.get(id);
                if (node != null && node.targetingComponent != null) {
                    node.targetingComponent.intentID = msg.targetID;
                }
            }
        }
        targetMessages.clear();

        // Update all nodes and generate damage.
        for (IntMap.Entry<TargetingNode> entry: nodes.entries()) {
            updateNode(entry.value);
            generateDamage(entry.key, entry.value);
        }
    }

    @Override
    public void remove(int entID) {
        positions.remove(entID);
        TargetingNode removedNode = nodes.remove(entID);
        // Set the targeting component to invalid, so that other systems (i.e. targeting renderer) don't keep using.
        if (removedNode != null) removedNode.targetingComponent.targetID = -1;
        // fix any targeting nodes.
        for (IntMap.Entry<TargetingNode> entry: nodes.entries()) {
            TargetingNode node = entry.value;
            if (node.targetingComponent.targetID == entID) {
                node.targetingComponent.targetID = -1;
            }
            if (node.targetingComponent.intentID == entID) {
                node.targetingComponent.intentID = -1;
            }
        }
    }

    private void updateNode(TargetingNode node) {
        TargetingComponent targetingComponent = node.targetingComponent;
        PositionComponent targetPositionComponent = node.targetPositionComponent;
        PositionComponent positionComponent = node.positionComponent;
        OwnedComponent ownedComponent = node.ownedComponent;
        // Check if intent is there.
        if (targetingComponent.intentID != -1) {
            // If intent is in range, make sure it's targeted.
            PositionComponent intentPosition = positions.get(targetingComponent.intentID);
            if ((intentPosition != null) && (intentPosition.position.dst(positionComponent.position) < targetingComponent.range)) {
                targetingComponent.targetID = targetingComponent.intentID;
                node.targetPositionComponent = intentPosition;
                return;
            }
        }

        // Check if the current target is still in range.
        if ((targetingComponent.targetID != -1)
            && (positionComponent.position.dst(targetPositionComponent.position) < targetingComponent.range)) return;

        if (!targetingComponent.switchTargets) return;

        // Look for a new target
        serviceQuery.set(ownedComponent, targetingComponent, positionComponent);
        Pair<Integer, PositionComponent> nearby = findNearbyService.query(serviceQuery);
        targetingComponent.targetID = nearby.key;
        
        if (targetingComponent.targetID!= -1) { // a target has been found
            node.targetPositionComponent = nearby.value;
        } else { // no target has been found
            node.targetPositionComponent = null;
        }
    }
    
    private void generateDamage(int id, TargetingNode node) {
        if (node.targetingComponent.targetID == -1) return;
        Gdx.app.debug("TargetingSystem", "creating damageevent by " + id + " against " + node.targetingComponent.targetID);
        DamageEvent damageEvent = new DamageEvent(node.ownedComponent.playerID, id, node.targetingComponent.targetID);
        publish(damageEvent);
    }

    public void addNode(int id, OwnedComponent ownedComponent, TargetingComponent targetingComponent, PositionComponent positionComponent) {
        nodes.put(id, new TargetingNode(ownedComponent, targetingComponent, positionComponent, null));
    }

    public void addPosition(int id, PositionComponent position) {
        positions.put(id, position);
    }

    @Override
    public void addSubscriber(Subscriber<DamageEvent> subscriber) {
        damageEventSubscribers.add(subscriber);
    }

    @Override
    public Collection<Subscriber<DamageEvent>> getSubscribers() {
        return damageEventSubscribers;
    }

    @Override
    public void process(TargetMessage targetMessage) {
        targetMessages.add(targetMessage);
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

    /*
     * System to render targets for currently selected units.
     */
    public class RenderTargetingSystem implements Subscriber<SelectionEvent> {

        ArrayList<TargetingNode> selected = new ArrayList<>();

        @Override
        public void process(SelectionEvent selectionEvent) {
            selected.clear();
            for (int id: selectionEvent.unitIDs) {
                TargetingNode node = nodes.get(id);
                if (node == null) {
                    Gdx.app.error("RenderTargetingSystem", "Unable to find TargetingNode for id: " + id);
                    continue;
                }
                selected.add(node);
            }
        }

        private Vector3 renderVec = new Vector3();
        public void render(RenderConfig config) {
            Batch batch = config.getBatch();
            batch.begin();
            for (TargetingNode selectedNode: selected) {
                if (selectedNode != null) { // nodes may be removed asynchronously
                    if (selectedNode.targetingComponent.targetID == -1) continue;
                    renderVec.set(selectedNode.targetPositionComponent.position);
                    config.getCam().project(renderVec);
                    batch.draw(Initializer.targetX, renderVec.x - 16, renderVec.y - 16, 32, 32);
                }
            }
            batch.end();
        }
    }
}
