package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.utils.Array;

/**
 * Created by paddlefish on 24-Sep-16.
 */
public class UnitCollisionHandlingGroundGraph extends GroundGraph {

    private final static int COLLISION_HANDLING_RANGE = 5;

    private final int unitX;
    private final int unitY;

    public UnitCollisionHandlingGroundGraph(int width, int height, int unitX, int unitY) {
        super(width, height);

        this.unitX = unitX;
        this.unitY = unitY;
    }

    @Override
    public Array<Connection<GroundNode>> getConnections(GroundNode fromNode) {
        if (getManhattanDistanceFromUnit(fromNode) < COLLISION_HANDLING_RANGE) {
            Array<Connection<GroundNode>> connections = new Array<>();

            for (int i = fromNode.x - 1; i < fromNode.x + 2; i++) {
                for (int j = fromNode.y - 1; j < fromNode.y + 2; j++) {
                    if (i == fromNode.x && j == fromNode.y) {
                        continue;
                    }

                    if (nodeExists(i, j)) {
                        GroundNode node = getNodeByCoords(i, j);
                        if (node.getOccupant() == NodeOccupant.NONE) {
                            connections.add(new DefaultConnection<>(fromNode, node));
                        }
                    }
                }
            }

            return connections;
        }

        return super.getConnections(fromNode);
    }

    private int getManhattanDistanceFromUnit(GroundNode node) {
        return Math.abs(node.x - unitX) + Math.abs(node.y - unitY);
    }
}
