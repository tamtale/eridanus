package com.week1.game.Model.Components;

import com.badlogic.gdx.math.Vector3;

public class VelocityComponent {
    public float baseSpeed;
    public float distTraveled = 0; // current distance traveled with the same velocity.
    public Vector3 velocity;
    public VelocityComponent(float baseSpeed, float x, float y, float z) {
        this.baseSpeed = baseSpeed;
        this.velocity = new Vector3(x, y, z);
    }
}
