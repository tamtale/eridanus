package com.week1.game.Model.Entities;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Map;

public abstract class Building extends Damageable {
    public abstract boolean overlap(float x, float y);
    public abstract Vector3 closestPoint(float x, float y);
    public abstract void putRemovedEdges(Vector3 fromNode, Array<Connection<Vector3>> connections);
}
