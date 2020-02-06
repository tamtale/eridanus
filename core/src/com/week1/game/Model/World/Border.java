package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector3;

public class Border implements Connection<Vector3> {

    private float cost;
    private Vector3 fromNode;
    private Vector3 toNode;

    public Border(float cost, Vector3 fromNode, Vector3 toNode){
        this.cost = cost;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Vector3 getFromNode() {
        return fromNode;
    }

    @Override
    public Vector3 getToNode() {
        return toNode;
    }
}
