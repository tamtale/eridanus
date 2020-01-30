package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

public class GameGraph implements IndexedGraph<Block.TerrainBlock> {

    private int nodeCount;

    @Override
    public int getIndex(Block.TerrainBlock node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public Array<Connection<Block.TerrainBlock>> getConnections(Block.TerrainBlock fromNode) {
        return fromNode.getConnections();
    }
}
