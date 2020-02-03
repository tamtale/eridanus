package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;

public class Border implements Connection<Block> {

    private float cost;
    private Block fromNode;
    private Block toNode;

    public Border(float cost, Block fromNode, Block toNode){
        this.cost = cost;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Block getFromNode() {
        return fromNode;
    }

    @Override
    public Block getToNode() {
        return toNode;
    }
}
