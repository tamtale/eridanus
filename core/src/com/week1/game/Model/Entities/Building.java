package com.week1.game.Model.Entities;

import com.badlogic.gdx.math.Vector3;

public abstract class Building implements Damageable{
    public abstract boolean overlap(float x, float y);
    public abstract Vector3 closestPoint(float x, float y);
}
