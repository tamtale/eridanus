package com.week1.game.AIMovement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class AStar<N> implements PathFinder<N> {

    IndexedGraph<N> graph;
    PriorityQueue<RouteNode<N>> openList = new PriorityQueue<>();
    RouteNode<N> current;
    Map<N, RouteNode<N>> allNodes = new HashMap<>();
    private int searchId;
    private RouteNode<N> endNode;

    public AStar(IndexedGraph<N> graph) {
        this.graph = graph;
    }

    /**
     * Would search through connections rather than nodes, but it does NOT WORK. DO NOT CALL THIS METHOD.
     */
    public boolean searchConnectionPath(N startNode, N endNode, Heuristic<N> heuristic, GraphPath<Connection<N>> outPath) {
        return false;
    }

    /**
     * Takes a startNode and an EndNode and finds the AStar-iest path between them. Mostly just calls search and
     * then generates the path if search works.
     *
     */
    public boolean searchNodePath(N startNode, N endNode, Heuristic<N> heuristic, GraphPath<N> outPath) {
        boolean found = this.search(startNode, endNode, heuristic);
        Gdx.app.debug("wab2 - AStar", "found? " + found);
        if (found) {
            this.generateNodePath(startNode, outPath);
        }

        return found;
    }

    /**
     * Attempts to find a path between startNode and endNode.
     * @return on success true
     */
    protected boolean search(N startNode, N endNode, Heuristic<N> heuristic) {
        if (!this.initSearch(startNode, endNode, heuristic)) {
            return false;
        };
        do {
            RouteNode<N> next = null;
            try {
                //Take the first item off the priority queue.
                next = openList.poll();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            assert next != null;

            //if current node is the endNode, the search is complete.
            if (next.getNode().toString().equals(endNode.toString())){
                this.endNode = next;
                return true;
            }

            //Go through all the connections for node "next" and estimate their value at getting to the endNode
            //Then store all connections in the priorityQueue
            Array<Connection<N>> connections = graph.getConnections(next.node);
            for (int i = 0; i < connections.size; i++) {
                Connection<N> connection = connections.get(i);
                RouteNode<N> nextNode = allNodes.getOrDefault(connection.getToNode(), new RouteNode<N>(connection.getToNode()));
                allNodes.put(connection.getToNode(), nextNode);
                double newScore = next.costSoFar + heuristic.estimate(next.node, connection.getToNode());
                if (newScore < nextNode.costSoFar){
                    nextNode.previous = next.node;
                    nextNode.costSoFar = newScore;
                    nextNode.estimatedScore = (newScore + heuristic.estimate(connection.getToNode(), endNode));
                    openList.add(nextNode);
                }
            }
        } while(this.openList.size() > 0);
        return false;
    }

    public boolean search(PathFinderRequest<N> request, long timeToRun) {
        long lastTime = TimeUtils.nanoTime();
        if (request.statusChanged) {
            this.initSearch(request.startNode, request.endNode, request.heuristic);
            request.statusChanged = false;
        }

        do {
            RouteNode<N> next = openList.poll();

        } while(this.openList.size() > 0);

        request.pathFound = false;
        return true;
    }

    /**
     * initialize all elements of the search.
     */
    protected boolean initSearch(N startNode, N endNode, Heuristic<N> heuristic) {

        if (++this.searchId < 0) {
            this.searchId = 1;
        }
        openList.clear();
        allNodes.clear();
        RouteNode<N> start = new RouteNode<>(startNode, null, 0d, heuristic.estimate(startNode, endNode));
        openList.add(start);
        allNodes.put(startNode, start);
        return true;
    }



    protected void generateConnectionPath(N startNode, GraphPath<Connection<N>> outPath) {
    }

    /**
     * Generate the path which search found (traceback). Sets outPath to the path.
     */
    protected void generateNodePath(N startNode, GraphPath<N> outPath) {
        RouteNode<N> node = endNode;
        do {
            outPath.add(node.node);
            node = allNodes.get(node.previous);
            if (node == null){
                break;
            }
        }while (node.previous != null);
        outPath.add(startNode);
        outPath.reverse();
    }



    class RouteNode<N>  implements Comparable<RouteNode<N>>{
        private final N node;
        private N previous;
        double costSoFar;
        double estimatedScore;

        RouteNode(N node){
            this(node, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        RouteNode(N node, N previous, double costSoFar, double estimatedScore) {
            this.node = node;
            this.previous = previous;
            this.costSoFar = costSoFar;
            this.estimatedScore = estimatedScore;
        }

        @Override
        public int compareTo(RouteNode<N> other) {
            if (other == null){
                return 1;
            }
            return Double.compare(this.estimatedScore, other.estimatedScore);
        }

        public N getNode() {
            return node;
        }
    }
}
