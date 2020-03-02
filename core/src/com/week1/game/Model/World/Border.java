package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;

public class Border implements Connection<Vector2> {

    private float cost;
    private Vector2 fromNode;
    private Vector2 toNode;

    public Border(float cost, Vector2 fromNode, Vector2 toNode){
        this.cost = cost;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Vector2 getFromNode() {
        return fromNode;
    }

    @Override
    public Vector2 getToNode() {
        return toNode;
    }
    
    @Override
    public boolean equals(Object object){
        return  (((Border) object).fromNode.toString().equals(this.fromNode.toString()) &&
                ((Border) object).toNode.toString().equals(this.toNode.toString()));
    }
}
