package com.week1.game.Model.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.AIMovement.WarrenIndexedAStarPathFinder;
import com.week1.game.Model.Entities.Building;
import com.week1.game.Model.OutputPath;
import com.week1.game.Pair;

import java.util.HashMap;
import java.util.Map;


public class GameGraph implements IndexedGraph<Vector2> {

    private int nodeCount;
    private GameHeuristic heuristic = new GameHeuristic();
    private PathFinder<Vector2> pathFinder;
    Array<Vector2> Vector2s = new Array<>();
    Border[][][][] borders;
    private Array<Connection<Vector2>>[][] edges;

    //TODO: make general
    public GameGraph(Block[][][] blocks){
        super();
        edges = new Array[blocks.length][blocks[0].length];
        borders = new Border[blocks.length][blocks[0].length][3][3];
        this.pathFinder = new WarrenIndexedAStarPathFinder<>(this);
        this.nodeCount = 0;
        for (int i = 0; i < edges.length; i++) {
            for (int j = 0; j < edges[0].length; j++) {
                edges[i][j] = new Array<>();
            }
        }
        Gdx.app.log("GameGraph - wab2", "Building GameGraph");
    }

    @Override
    public int getIndex(Vector2 node) {
        int index = (int) (node.x + edges.length * node.y + edges.length);

        return index;
//        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public Array<Connection<Vector2>> getConnections(Vector2 fromNode) {
        if (fromNode.x < 0 || fromNode.x > edges.length - 1 || fromNode.y < 0 || fromNode.y > edges[0].length - 1){
            return null;
        }
        return edges[(int) fromNode.x][(int) fromNode.y];

    }
    
    public Array<Connection<Vector2>> getConnections(int i, int j){
        if (i < 0 || i> edges.length - 1 || j < 0 || j > edges[0].length){
            return null;
        }
        return edges[i][j];
    }
    public void setConnection(Vector2 fromNode, Vector2 toNode, float weight){
        Border border = new Border(weight, fromNode, toNode);
        int i = (int) fromNode.x - (int) toNode.x + 1;
        int j = (int) fromNode.y - (int) toNode.y + 1;
        borders[(int) fromNode.x][(int) fromNode.y][i][j] = border;
        edges[(int) fromNode.x][(int) fromNode.y].add(border);
    }

    public void removeAllConnections(Vector2 fromNode, Building b){
        // TODO: throwing exceptions, so I commented this out (lji1)???
//        b.putRemovedEdges(fromNode, edges[(int) fromNode.x][(int) fromNode.y][(int) fromNode.z]);
//        edges[(int) fromNode.x][(int) fromNode.y][(int) fromNode.z] = new Array<>();
    }

    public void addVector2(Vector2 Vector2) {
        nodeCount+=1;
        Vector2s.add(Vector2);
        //vecCoords[(int) Vector2.x][(int) Vector2.y][(int) Vector2.z] = Vector2;
    }

    public OutputPath search(Vector2 startNode, Vector2 endNode) {
        OutputPath path = new OutputPath();

        if (pathFinder.searchNodePath(startNode, endNode, heuristic, path)) {
            System.out.println("return path");
            return path;
        }
        return null;
    }

    public PathFinder<Vector2> getPathFinder() {
        return pathFinder;
    }

    public void setPathFinder(PathFinder<Vector2> pathFinder) {
        this.pathFinder = pathFinder;
    }

    public void setConnections(Vector2 fromNode, Array<Connection<Vector2>> connections) {
        setConnections((int) fromNode.x, (int) fromNode.y, connections);
    }


    public void setConnections(int i, int j, Array<Connection<Vector2>> connections) {
        for (Connection<Vector2> connection: connections){
            setConnection(connection.getFromNode(), connection.getToNode(), connection.getCost());
        }
    }

    public Connection<Vector2> getConnection(int fromX, int fromY, int toX, int toY){
//        System.out.println(borders.get(new Pair<>(fromX, fromY)));
        int i = (int) fromX - (int) toX + 1;
        int j = (int) fromY - (int) toY + 1;
//        for(Pair<Integer, Integer> key: borders.keySet()){
//            System.out.println(" x " + key.key + " y " + key.value);
//        }
        if (i < 0 || i > 2 || j < 0 || j > 2){
            return null;
        }
        return borders[fromX][fromY][i][j];
    }

    public void removeConnection(int fromX, int fromY, int toX, int toY) {
        Connection<Vector2> border = getConnection(fromX, fromY, toX, toY);
        edges[fromX][fromY].removeValue(border, false);
        int i = (int) fromX - (int) toX + 1;
        int j = (int) fromY - (int) toY + 1;
        if (i >= 0 && i <= 2 && j >= 0 && j <= 2){
            borders[fromX][fromY][i][j] = null;
        }

    }
}
