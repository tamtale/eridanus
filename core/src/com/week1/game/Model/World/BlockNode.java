package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BlockNode {

    private Vector3 coords;
    private int index;
    private Array<Connection<BlockNode>> edges = new Array<>();
    public BlockNode(Vector3 coords) {
        this.coords = coords;
    }

    public Vector3 getCoords() {
        return coords;
    }

    public void setIndex(int nodeCount) {
        this.index = nodeCount;
    }

    public int getIndex() {
        return index;
    }

    public Array<Connection<BlockNode>> getConnections() {
        return edges;
    }

    public void setConnection(Border border) {

    }
}
