package com.week1.game.Model;

import com.badlogic.gdx.math.Rectangle;
import com.week1.game.SteeringAgent;
import com.week1.game.Week1Demo;

public class Unit extends Rectangle {
    public float dx;
    public float dy;
    public boolean clicked;
    public SteeringAgent agent;

    public Unit(float x, float y, float dx, float dy) {
        super(x, y, Week1Demo.SCALE, Week1Demo.SCALE);
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.clicked = false;
    }

    public void step(float delta) {
        agent.update(delta);
    }
}

