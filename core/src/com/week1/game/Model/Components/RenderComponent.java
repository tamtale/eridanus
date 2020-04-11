package com.week1.game.Model.Components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

/*
 * Component for entities rendered onto the map (but not the map itself).
 */
public class RenderComponent extends AComponent {
//    public RenderAction renderAction;
//    public RenderComponent(RenderAction renderAction) {
//        this.renderAction = renderAction;
//    }

    public ModelInstance modelInstance;
    public RenderComponent(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }


//    abstract class RenderAction {
//        /*
//         * Assumes that the modelBatch
//         */
//        abstract void render(RenderConfig renderConfig, PositionComponent positionComponent);
//    }
//
//    class RenderActionWithModelInstance extends RenderAction {
//        public ModelInstance modelInstance;
//
//        public RenderActionWithModelInstance(ModelInstance modelInstance, PositionComponent positionComponent) {
//            this.modelInstance = modelInstance;
//        }
//
//        @Override
//        void render(RenderConfig renderConfig, PositionComponent positionComponent) {
//            modelInstance.transform.setTranslation(positionComponent.position);
//            renderConfig.getModelBatch().render(modelInstance, renderConfig.getEnv());
//        }
//    }
}
