package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector3;

public class GameHeuristic implements Heuristic<Vector3> {


    @Override
    public float estimate(Vector3 node, Vector3 endNode) {
        //TODO: Blocks have there x,y,z? do distance formula
        float D = 1f;
        float D2 = (float) Math.sqrt(2);
        float dx = Math.abs(node.x - endNode.x);
        float dy = Math.abs(node.y - endNode.y);
        float dz = Math.abs(node.z - endNode.z);
        return D * (dx + dy + dz) + (D2 - 2 * D) * Math.min(dx, Math.min(dy, dz));
    }
}
