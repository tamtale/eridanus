//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.week1.game.AIMovement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathFinderRequest;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.BinaryHeap.Node;

import java.util.Arrays;

public class WarrenIndexedAStarPathFinder<N> implements PathFinder<N> {
    IndexedGraph<N> graph;
    WarrenIndexedAStarPathFinder.NodeRecord<N>[] nodeRecords;
    WarrenBinaryHeap<WarrenIndexedAStarPathFinder.NodeRecord<N>> openList;
    WarrenIndexedAStarPathFinder.NodeRecord<N> current;
    public WarrenIndexedAStarPathFinder.Metrics metrics;
    private int searchId;
    private static final int UNVISITED = 0;
    private static final int OPEN = 1;
    private static final int CLOSED = 2;

    public WarrenIndexedAStarPathFinder(IndexedGraph<N> graph) {
        this(graph, true);
    }

    public WarrenIndexedAStarPathFinder(IndexedGraph<N> graph, boolean calculateMetrics) {
        this.graph = graph;
        this.nodeRecords = (WarrenIndexedAStarPathFinder.NodeRecord[])(new WarrenIndexedAStarPathFinder.NodeRecord[2 * graph.getNodeCount()]);
        this.openList = new WarrenBinaryHeap(16, false);
        if (calculateMetrics) {
            this.metrics = new WarrenIndexedAStarPathFinder.Metrics();
        }

    }

    public void reset(){
        Arrays.fill(nodeRecords, null);
        this.openList = new WarrenBinaryHeap<>();
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
        if (this.initSearch(startNode, endNode, heuristic) == false) {
            return false;
        };

        do {
            if (this.openList == null){
                Gdx.app.error("AStar - wab2", "openList is null");
                return false;
            }
            NodeRecord<N> next = this.openList.peek();
            if (next == null) {
                Gdx.app.error("AStar - wab2", "Next is null");
                this.openList.pop();
                return false;
            }
            this.openList.pop();
            this.current = next;
            this.current.category = 2;
            if (this.current.node.toString().equals(endNode.toString())) {
                return true;
            }

            this.visitChildren(endNode, heuristic);
        } while(this.openList.size > 0);

        return false;
    }

    public boolean search(PathFinderRequest<N> request, long timeToRun) {
        long lastTime = TimeUtils.nanoTime();
        if (request.statusChanged) {
            this.initSearch(request.startNode, request.endNode, request.heuristic);
            request.statusChanged = false;
        }

        do {
            long currentTime = TimeUtils.nanoTime();
            timeToRun -= currentTime - lastTime;
            if (timeToRun <= 100L) {
                return false;
            }

            this.current = (WarrenIndexedAStarPathFinder.NodeRecord)this.openList.pop();
            this.current.category = 2;

            if (this.current.node == request.endNode) {
                request.pathFound = true;
                this.generateNodePath(request.startNode, request.resultPath);
                return true;
            }

            //If it can't find a connection or the nodeRecord runs out, returns false
            if (!this.visitChildren(request.endNode, request.heuristic)) {
                return false;
            };
            lastTime = currentTime;
        } while(this.openList.size > 0);

        request.pathFound = false;
        return true;
    }

    protected boolean initSearch(N startNode, N endNode, Heuristic<N> heuristic) {
        if (this.metrics != null) {
            this.metrics.reset();
        }

        if (++this.searchId < 0) {
            this.searchId = 1;
        }

        this.openList.clear();
        WarrenIndexedAStarPathFinder.NodeRecord<N> startRecord = this.getNodeRecord(startNode);
        if (startRecord == null) {
            return false;
        }
        startRecord.node = startNode;
        startRecord.connection = null;
        startRecord.costSoFar = 0.0F;
        this.addToOpenList(startRecord, heuristic.estimate(startNode, endNode));
        this.current = null;
        return true;
    }

    protected boolean visitChildren(N endNode, Heuristic<N> heuristic) {
        Array<Connection<N>> connections = this.graph.getConnections(this.current.node);
        if (connections == null){
            Gdx.app.error("wab2 - connections", "Connections are null meaning someones out of bounds");
            return false;
        }
        for(int i = 0; i < connections.size; ++i) {
            if (this.metrics != null) {
                ++this.metrics.visitedNodes;
            }

            Connection<N> connection = (Connection)connections.get(i);
            N node = connection.getToNode();
            if (this.current == null){
                return false;
            }
            float nodeCost = this.current.costSoFar + connection.getCost();
            WarrenIndexedAStarPathFinder.NodeRecord<N> nodeRecord = this.getNodeRecord(node);
            if (nodeRecord == null) {
                return false;
            }
            float nodeHeuristic;
            if (nodeRecord.category == 2) {
                if (nodeRecord.costSoFar <= nodeCost) {
                    continue;
                }

                nodeHeuristic = nodeRecord.getEstimatedTotalCost() - nodeRecord.costSoFar;
            } else if (nodeRecord.category == 1) {
                if (nodeRecord.costSoFar <= nodeCost) {
                    continue;
                }
                if (this.openList.contains(nodeRecord, true)) {
                    this.openList.remove(nodeRecord);
                }
                nodeHeuristic = nodeRecord.getEstimatedTotalCost() - nodeRecord.costSoFar;
            } else {
                nodeHeuristic = heuristic.estimate(node, endNode);
            }

            nodeRecord.costSoFar = nodeCost;
            nodeRecord.connection = connection;
            if (!this.addToOpenList(nodeRecord, nodeCost + nodeHeuristic))
                return false;
        }
        return true;
    }

    protected void generateConnectionPath(N startNode, GraphPath<Connection<N>> outPath) {
        while(this.current.node != startNode) {
            outPath.add(this.current.connection);
            this.current = this.nodeRecords[this.graph.getIndex(this.current.connection.getFromNode())];
        }

        outPath.reverse();
    }

    protected void generateNodePath(N startNode, GraphPath<N> outPath) {
        while(this.current.connection != null) {
            outPath.add(this.current.node);
            this.current = this.nodeRecords[this.graph.getIndex(this.current.connection.getFromNode())];
        }
        outPath.add(startNode);
        outPath.reverse();
    }

    protected boolean addToOpenList(WarrenIndexedAStarPathFinder.NodeRecord<N> nodeRecord, float estimatedTotalCost) {
        if (nodeRecord == null){
            Gdx.app.error("AStar - wab2", "node record Null ");
            return false;
        }
        if (openList == null){
            Gdx.app.error("AStar - wab2", "openList null");
            return false;
        }
//        System.out.println(openList);

        this.openList.add(nodeRecord, estimatedTotalCost);
        nodeRecord.category = 1;
        if (this.metrics != null) {

            ++this.metrics.openListAdditions;
            this.metrics.openListPeak = Math.max(this.metrics.openListPeak, this.openList.size);
        }
        return true;
    }

    protected WarrenIndexedAStarPathFinder.NodeRecord<N> getNodeRecord(N node) {
        int index = this.graph.getIndex(node);
        if (index >= nodeRecords.length) {
            Gdx.app.error("AStar - wab2", "index larger than nodeLength");
            return null;
        }
        WarrenIndexedAStarPathFinder.NodeRecord<N> nr = this.nodeRecords[index];
        if (nr != null) {
            if (nr.searchId != this.searchId) {
                nr.category = 0;
                nr.searchId = this.searchId;
            }

            return nr;
        } else {
            nr = this.nodeRecords[index] = new WarrenIndexedAStarPathFinder.NodeRecord();
            nr.node = node;
            nr.searchId = this.searchId;
            return nr;
        }
    }

    public static class Metrics {
        public int visitedNodes;
        public int openListAdditions;
        public int openListPeak;

        public Metrics() {
        }

        public void reset() {
            this.visitedNodes = 0;
            this.openListAdditions = 0;
            this.openListPeak = 0;
        }
    }

    static class NodeRecord<N> extends WarrenBinaryHeap.Node {
        N node;
        Connection<N> connection;
        float costSoFar;
        int category;
        int searchId;

        public NodeRecord() {
            super(0.0F);
        }

        public float getEstimatedTotalCost() {
            return this.getValue();
        }
    }
}
