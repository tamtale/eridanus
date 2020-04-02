package com.week1.game.Model.Components;

import com.badlogic.gdx.math.Vector3;

public class PositionComponent extends AComponent {
    public Vector3 position;
    public PositionComponent(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
    }
}
