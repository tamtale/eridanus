package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

public class GameGraph implements IndexedGraph<Block> {

    private int nodeCount;

    @Override
    public int getIndex(Block node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public Array<Connection<Block>> getConnections(Block fromNode) {
        return fromNode.getConnections();
    }

    public void setConnection(float weight, Block fromNode, Block toNode){
        fromNode.setConnection(new WeightedBlockEdge(weight, fromNode, toNode));
    }
}
