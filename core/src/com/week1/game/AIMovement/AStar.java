package com.week1.game.AIMovement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.week1.game.Model.OutputPath;

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

    public boolean searchConnectionPath(N startNode, N endNode, Heuristic<N> heuristic, GraphPath<Connection<N>> outPath) {
        boolean found = this.search(startNode, endNode, heuristic);
        if (found) {
            this.generateConnectionPath(startNode, outPath);
        }

        return found;
    }

    public boolean searchNodePath(N startNode, N endNode, Heuristic<N> heuristic, GraphPath<N> outPath) {
        boolean found = this.search(startNode, endNode, heuristic);
        Gdx.app.debug("wab2 - AStar", "found? " + found);
        if (found) {
            this.generateNodePath(startNode, outPath);
        }

        return found;
    }

    protected boolean search(N startNode, N endNode, Heuristic<N> heuristic) {
        if (!this.initSearch(startNode, endNode, heuristic)) {
            return false;
        };
        do {
            RouteNode<N> next = null;
            try {
                next = openList.poll();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            assert next != null;
            if (next.getNode().toString().equals(endNode.toString())){
                this.endNode = next;
                return true;
            }

            for (Connection<N> connection:
                 graph.getConnections(next.node)) {
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
            if (this.estimatedScore > other.estimatedScore) {
                return 1;
            } else if (this.estimatedScore < other.estimatedScore) {
                return -1;
            } else {
                return 0;
            }
        }

        public N getNode() {
            return node;
        }
    }
}
