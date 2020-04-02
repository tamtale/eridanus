package com.week1.game.Model.Components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class RenderComponent extends AComponent {
    public ModelInstance modelInstance;
    public RenderComponent(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }
}
