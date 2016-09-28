package com.mygdx.game.skirmish.gameplay.pathfinding;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class GroundNode {
    public final int x;
    public final int y;

    private int updateFrame = 0;

    private final GroundGraph graph;

    private NodeOccupant occupant;

    public void setOccupant(NodeOccupant occupant) {
        this.occupant = occupant;
    }

    public NodeOccupant getOccupant() {
        if (updateFrame != graph.getUpdateFrame()) {
            graph.update(this);
            updateFrame = graph.getUpdateFrame();
        }

        return occupant;
    }

    public GroundNode(GroundGraph graph, int x, int y) {
        this.x = x;
        this.y = y;
        this.graph = graph;

        setOccupant(NodeOccupant.NONE);
    }

    public static float dist(GroundNode node, GroundNode endNode) {
        return ((float) Math.sqrt(Math.pow(endNode.x - node.x, 2) + Math.pow(endNode.y - node.y, 2)));
    }
}
