package com.week1.game.Model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class GameGraph implements IndexedGraph<Block> {

    private int nodeCount;
    private GameHeuristic heuristic = new GameHeuristic();
    Array<Block> blocks = new Array<>();
    Array<Border> borders = new Array<>();

    ObjectMap<Block, Array<Border>> borderMap = new ObjectMap<>();

    public GameGraph(){
        super();
        this.nodeCount = 0;
        Gdx.app.log("GameGraph", "Building GameGraph");
    }

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
        Border border = new Border(weight, fromNode, toNode);
        fromNode.setConnection(new Border(weight, fromNode, toNode));
        if(!borderMap.containsKey(fromNode)){
            borderMap.put(fromNode, new Array<>());
        }
        borderMap.get(fromNode).add(border);
        borders.add(border);
    }

    public void addBlock(Block block) {
        block.setIndex(nodeCount);
        nodeCount+=1;
        blocks.add(block);
    }
}
