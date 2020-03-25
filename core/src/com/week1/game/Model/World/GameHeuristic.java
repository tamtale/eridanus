package com.week1.game.Model.World;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class GameHeuristic implements Heuristic<Vector2> {


    @Override
    public float estimate(Vector2 node, Vector2 endNode) {
        //TODO: Blocks have there x,y,z? do distance formula
        float D = 1f;
        float D2 = (float) Math.sqrt(2);
        float dx = Math.abs(node.x - endNode.x);
        float dy = Math.abs(node.y - endNode.y);
        return D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
    }
}
