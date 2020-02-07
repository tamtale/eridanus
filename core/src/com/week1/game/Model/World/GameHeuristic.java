package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector3;

public class GameHeuristic implements Heuristic<Block> {


    @Override
    public float estimate(Block node, Block endNode) {
        //TODO: Blocks have there x,y,z? do distance formula
        float D = 1f;
        float D2 = (float) Math.sqrt(2);
        Vector3 nodeCoords = node.getCoords();
        Vector3 endNodeCoords = endNode.getCoords();
        float dx = Math.abs(nodeCoords.x - endNodeCoords.x);
        float dy = Math.abs(nodeCoords.y - endNodeCoords.y);
        float dz = Math.abs(nodeCoords.z - endNodeCoords.z);
        return D * (dx + dy + dz) + (D2 - 2 * D) * Math.min(dx, Math.min(dy, dz));
    }
}
