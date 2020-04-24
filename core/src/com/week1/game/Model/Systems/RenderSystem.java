package com.week1.game.Model.Systems;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.week1.game.Model.Components.PositionComponent;
import com.week1.game.Model.Components.RenderComponent;
import com.week1.game.Model.Components.VelocityComponent;
import com.week1.game.Model.Components.VisibleComponent;
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
            if (node != null) { // nodes may be removed asynchronously
                // Always update the modelInstance's position (even if not visible)
                node.renderComponent.modelInstance.transform.setToTranslation(node.positionComponent.position);

//                Rotate any moving entities to look where they are going
                if (!node.velocityComponent.velocity.equals(VelocityComponent.ZERO.velocity)) {
                    node.renderComponent.modelInstance.transform.rotate(Vector3.Z, MathUtils.atan2(node.velocityComponent.velocity.x,
                            -node.velocityComponent.velocity.y) * MathUtils.radiansToDegrees);
                }

                if (node.visibleComponent.visible()) { // only render if unit should be visible
                    modelBatch.render(node.renderComponent.modelInstance, env);
                }
            }
        }
        modelBatch.end();
    }

    public void addNode(int id, RenderComponent renderComponent, PositionComponent positionComponent, VisibleComponent visibleComponent,
                        VelocityComponent velocityComponent) {
        nodes.put(id, new RenderNode(renderComponent, positionComponent, visibleComponent, velocityComponent));
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
        VisibleComponent visibleComponent;
        VelocityComponent velocityComponent;
        public RenderNode(RenderComponent renderComponent, PositionComponent positionComponent, VisibleComponent visibleComponent, VelocityComponent velocityComponent) {
            this.renderComponent = renderComponent;
            this.positionComponent = positionComponent;
            this.visibleComponent = visibleComponent;
            this.velocityComponent = velocityComponent;
        }
    }
}
