package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.VelocityComponent;
import com.week1.game.Renderer.RenderConfig;

/*
 * System that updates interpolated positions of entities that are rendered
 * on the map, so that they move smoothly.
 */
public class InterpolatorSystem implements ISystem {

    IntMap<InterpolNode> nodes = new IntMap<>();

    private float updateDelta = 0;

    @Override
    public void update(float delta) {
        updateDelta = 0;
    }

    /*
     * On each render, should update the position of each node.
     */
    public void render(RenderConfig config) {
        updateDelta += config.getDelta();
        for (InterpolNode node: nodes.values()) {
            Vector3 position = node.positionComponent.position;
            Vector3 velocity = node.velocityComponent.velocity;
            Vector3 interpolated = node.interpolatedComponent.position;
            interpolated.set(position);
            interpolated.mulAdd(velocity, updateDelta);
        }
    }

    public void addNode(int id, PositionComponent positionComponent,
                        PositionComponent interpolatedComponent,
                        VelocityComponent velocityComponent) {
        nodes.put(id, new InterpolNode(positionComponent, interpolatedComponent, velocityComponent));
    }

    @Override
    public void remove(int entID) {
        nodes.remove(entID);
    }

    static class InterpolNode {
        public PositionComponent positionComponent;
        public PositionComponent interpolatedComponent;
        public VelocityComponent velocityComponent;
        public InterpolNode(PositionComponent positionComponent,
                            PositionComponent interpolatedComponent, VelocityComponent velocityComponent) {
            this.positionComponent = positionComponent;
            this.interpolatedComponent = interpolatedComponent;
            this.velocityComponent = velocityComponent;
        }
    }
}
