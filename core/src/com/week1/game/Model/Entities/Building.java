package com.week1.game.Model.Entities;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public abstract class Building implements Damageable{
    protected Array<Connection<Vector3>> removedEdges;

    public abstract boolean overlap(float x, float y);
    public abstract Vector3 closestPoint(float x, float y);


    public abstract Array<Connection<Vector3>> getRemovedEdges();

    public abstract void putRemovedEdges(Vector3 fromNode, Array<Connection<Vector3>> connections);
}
