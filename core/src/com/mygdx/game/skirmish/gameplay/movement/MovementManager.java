package com.mygdx.game.skirmish.gameplay.movement;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.gameplay.pathfinding.NodeOccupant;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class MovementManager {

    private final ConcurrentMap<UnitBase, GraphPath<GroundNode>> groundPathCache;
    private final World world;

    public MovementManager(World world) {
        this.world = world;
        this.groundPathCache = new ConcurrentHashMap<UnitBase, GraphPath<GroundNode>>();
    }

    public void handleGroundUnitMovement(float delta, List<UnitBase> units,
                                         GroundGraph groundGraph,
                                         PathFinder groundPathFinder) {
        Circle unitCircle;
        GroundNode curNode;
        GroundNode newNode;
        GroundNode destNode;
        GroundNode finNode;
        Vector2 pos = new Vector2();
        Vector2 destPos = new Vector2();
        Vector2 travelVec;
        for (UnitBase unit : units) {
            unitCircle =  unit.circle;
            pos.set(unitCircle.x, unitCircle.y);
            curNode = groundGraph.getNodeByMapPixelCoords(pos.x, pos.y);
            finNode = groundGraph.getNodeByCoords(unit.destNodeX, unit.destNodeY);

            GraphPath<GroundNode> graphPath = new DefaultGraphPath<GroundNode>();

            if (groundPathFinder.searchNodePath(curNode,
                    finNode,
                    groundGraph.getHeuristic(),
                    graphPath)) {
                if (graphPath.getCount() > 1) {
                    destNode = graphPath.get(1);

                    destPos.set(destNode.x * MapUtils.NODE_WIDTH_PX, destNode.y * MapUtils.NODE_HEIGHT_PX);
                    travelVec = destPos.cpy().sub(pos);
                    float maxTravelDist = unit.baseSpeed * unit.speedMulti * delta;
                    travelVec.setLength(Math.min(maxTravelDist, travelVec.len()));

                    newNode = groundGraph.getNodeByMapPixelCoords(pos.x + travelVec.x, pos.y + travelVec.y);
                    if (newNode != curNode && newNode.getOccupant() == NodeOccupant.NONE) {
                        unit.translate(travelVec);
                        curNode.setOccupant(NodeOccupant.NONE);
                        newNode.setOccupant(NodeOccupant.GROUND_UNIT);
                    } else if (newNode == curNode) {
                        unit.translate(travelVec);
                    }
                }
            }
        }
    }

    private void handleLocalMovement() {

    }
}
