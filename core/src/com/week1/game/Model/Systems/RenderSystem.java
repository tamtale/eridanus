package com.week1.game.Model.Systems;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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

    IntMap<RenderNode> nodes = new IntMap<>(); // Entity IDs are keys to their nodes.

    public void render(RenderConfig config) {
        Environment env = config.getEnv();
        ModelBatch modelBatch = config.getModelBatch();
        modelBatch.begin(config.getCam());
        for (RenderNode node: nodes.values()) {
            node.renderComponent.modelInstance.transform.setTranslation(node.positionComponent.position);
            modelBatch.render(node.renderComponent.modelInstance, env);
        }
        config.getModelBatch().end();
    }

    public void addNode(int id, RenderComponent renderComponent, PositionComponent positionComponent) {
        nodes.put(id, new RenderNode(renderComponent, positionComponent));
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void remove(int entID) {
        nodes.remove(entID);
    }

    static class RenderNode {
        RenderComponent renderComponent;
        PositionComponent positionComponent;
        public RenderNode(RenderComponent renderComponent, PositionComponent positionComponent) {
            this.renderComponent = renderComponent;
            this.positionComponent = positionComponent;
        }
    }
}
