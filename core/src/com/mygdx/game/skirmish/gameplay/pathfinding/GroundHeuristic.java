package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class GroundHeuristic implements Heuristic<GroundNode> {
    @Override
    public float estimate(GroundNode node, GroundNode endNode) {
        return GroundNode.dist(node, endNode);
    }
}
