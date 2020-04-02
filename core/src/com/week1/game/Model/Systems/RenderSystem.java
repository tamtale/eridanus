package com.week1.game.Model.Systems;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.RenderComponent;
import com.week1.game.Model.Components.VelocityComponent;
import com.week1.game.Renderer.RenderConfig;

/*
 * System responsible for rendering game world entities (not the world itself).
 * Performs positional extrapolation based on the time since the last game state update
 * and the current time.
 */
public class RenderSystem implements ISystem {

    IntMap<RenderNode> nodes = new IntMap<>();
    private float updateDelta = 0; // time from the last logical update
    private Vector3 interpolated = new Vector3();

    public void render(RenderConfig config) {
        config.getModelBatch().begin(config.getCam());
        updateDelta += config.getDelta();
        for (RenderNode node: nodes.values()) {
            Vector3 position = node.positionComponent.position;
            Vector3 velocity = node.velocityComponent.velocity;
            interpolated.set(position);
            interpolated.mulAdd(velocity, updateDelta);
            node.renderComponent.modelInstance.transform.setTranslation(interpolated);
            config.getModelBatch().render(node.renderComponent.modelInstance, config.getEnv());
        }
        config.getModelBatch().end();
    }

    public void add(int id, RenderComponent renderComponent, PositionComponent positionComponent, VelocityComponent velocityComponent) {
        nodes.put(id, new RenderNode(renderComponent, positionComponent, velocityComponent));
    }

    public boolean remove(int id) {
        return nodes.remove(id) != null;
    }

    @Override
    public void update(float delta) {
        updateDelta = 0;
    }

    static class RenderNode {
        RenderComponent renderComponent;
        PositionComponent positionComponent;
        VelocityComponent velocityComponent;
        public RenderNode(RenderComponent renderComponent, PositionComponent positionComponent, VelocityComponent velocityComponent) {
            this.renderComponent = renderComponent;
            this.positionComponent = positionComponent;
            this.velocityComponent = velocityComponent;
        }
    }
}
