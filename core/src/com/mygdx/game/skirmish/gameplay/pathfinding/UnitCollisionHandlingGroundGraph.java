package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.skirmish.World;

/**
 * Created by paddlefish on 24-Sep-16.
 */
public class UnitCollisionHandlingGroundGraph implements IndexedGraph<GroundNode> {

    private static final int COLLISION_HANDLING_RANGE = 10;

    private final World world;
    private final GroundGraph graph;

    private GroundNode unitNode;

    public UnitCollisionHandlingGroundGraph(World world, GroundGraph graph, GroundNode unitNode) {
        this.world = world;
        this.graph = graph;
        this.unitNode = unitNode;
    }

    @Override
    public Array<Connection<GroundNode>> getConnections(GroundNode fromNode) {
        if (getManhattanDistance(unitNode, fromNode) < COLLISION_HANDLING_RANGE) {
            return graph.getConnections(fromNode, true);
        } else if (getManhattanDistance(unitNode, fromNode) == COLLISION_HANDLING_RANGE) {
            Array<Connection<GroundNode>> connections = graph.getConnections(fromNode, false);
            connections.select(connection ->
                    getManhattanDistance(fromNode, connection.getToNode()) < COLLISION_HANDLING_RANGE &&
                    !graph.nodeIsOpen(connection.getToNode()))
                    .forEach(connection -> connections.removeValue(connection, true));
        }

        return graph.getConnections(fromNode, false);
    }

    protected int getManhattanDistance(GroundNode node1, GroundNode node2) {
        return Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y);
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
