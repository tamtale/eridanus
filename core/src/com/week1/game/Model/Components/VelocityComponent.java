package com.week1.game.Model.Components;

import com.badlogic.gdx.math.Vector3;

public class VelocityComponent extends AComponent {
    public static VelocityComponent ZERO = new VelocityComponent(0, 0, 0, 0);
    public float baseSpeed;
    public float distTraveled = 0; // Current distance traveled with the same velocity.
    public Vector3 velocity;
    public VelocityComponent(float baseSpeed, float x, float y, float z) {
        this.baseSpeed = baseSpeed;
        this.velocity = new Vector3(x, y, z);
    }
}
