package com.week1.game.Model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


public class GameGraph implements IndexedGraph<Vector3> {

    private int nodeCount;
    private GameHeuristic heuristic = new GameHeuristic();
    Array<Vector3> Vector3s = new Array<>();
    Array<Border> borders = new Array<>();
    //Vector3[][][] vecCoords = new Vector3[10][10][3];
    Array<Connection<Vector3>>[][][] edges = new Array[100][100][3];

    //TODO: make general
    public GameGraph(){
        super();
        this.nodeCount = 0;
        for (int i = 0; i < edges.length; i++) {
            for (int j = 0; j < edges[0].length; j++) {
                for (int k = 0; k < edges[0][0].length; k++) {
//                    System.out.println(i + " " + j + " " + k);
//                    System.out.println(i + " " + (edges.length - 1));
//                    System.out.println(edges[0].length);
//                    System.out.println(edges[0][0].length);
//                    System.out.println((i < edges.length));
                    edges[i][j][k] = new Array<>();
                    //vecCoords[i][j][k] = new Vector3();
                }
            }
        }
        Gdx.app.log("GameGraph - wab2", "Building GameGraph");
    }

    @Override
    public int getIndex(Vector3 node) {
        int index = (int) (node.x + edges.length * node.y + edges.length * edges[0].length * node.z);

        return index;
//        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public Array<Connection<Vector3>> getConnections(Vector3 fromNode) {
        return edges[(int) fromNode.x][(int) fromNode.y][(int) fromNode.z];

    }

    public void setConnection(Vector3 fromNode, Vector3 toNode, float weight){
        Border border = new Border(weight, fromNode, toNode);
        edges[(int) fromNode.x][(int) fromNode.y][(int) fromNode.z].add(border);
    }

    public void addVector3(Vector3 Vector3) {
        nodeCount+=1;
        Vector3s.add(Vector3);
        //vecCoords[(int) Vector3.x][(int) Vector3.y][(int) Vector3.z] = Vector3;
    }

//    public Vector3 getVector3(int i, int j, int k) {
//        return vecCoords[i][j][k];
//    }

}
