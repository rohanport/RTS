package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

/**
 * Created by paddlefish on 24-Sep-16.
 */
public class UnitCollisionHandlingGroundGraph implements IndexedGraph<GroundNode> {

    public static final int COLLISION_HANDLING_RANGE = 10;
    private final GroundGraph graph;

    private GroundNode unitNode;

    public void setUnitNode(GroundNode unitNode) {
        this.unitNode = unitNode;
    }

    public UnitCollisionHandlingGroundGraph(GroundGraph graph, GroundNode unitNode) {
        this.graph = graph;
        this.unitNode = unitNode;
    }

    @Override
    public Array<Connection<GroundNode>> getConnections(GroundNode fromNode) {
        if (GroundGraphUtils.getDist(unitNode, fromNode) <= COLLISION_HANDLING_RANGE) {
            return graph.getConnections(fromNode, true);
        }

        return new Array<>();
    }

    @Override
    public int getIndex(GroundNode node) {
        return graph.getIndex(node);
    }

    @Override
    public int getNodeCount() {
        return graph.getNodeCount();
    }
}
