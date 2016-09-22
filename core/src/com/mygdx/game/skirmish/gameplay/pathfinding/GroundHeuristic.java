package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class GroundHeuristic implements Heuristic<GroundNode> {
    @Override
    public float estimate(GroundNode node, GroundNode endNode) {
        switch (endNode.getOccupant()) {
            case GROUND_UNIT:
                return GroundNode.dist(node, endNode) + 100;
            case NONE:
                return GroundNode.dist(node, endNode);
            default:
                throw new RuntimeException("Unknown Node occupant: " + endNode.getOccupant());
        }
    }
}
