package com.mygdx.game.skirmish.gameplay.movement;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameplay.pathfinding.*;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.units.UnitState;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class MovementHandler {

    private final ConcurrentMap<UnitBase, ReroutableGraphPath<GroundNode>> groundPathCache;
    private final ConcurrentMap<UnitBase, PathFinder<GroundNode>> pathFinderCache;
    private final World world;
    private final GroundGraph groundGraph;
    private final UnitCollisionHandlingGroundGraph unitCollisionHandlingGroundGraph;
    private final IndexedAStarPathFinder<GroundNode> pathFinder;
    private final IndexedAStarPathFinder<GroundNode> unitPathFinder;

    public MovementHandler(World world) {
        this.world = world;
        this.groundPathCache = new ConcurrentHashMap<>();
        pathFinderCache = new ConcurrentHashMap<>();
        groundGraph = world.getGroundGraph();
        unitCollisionHandlingGroundGraph = groundGraph.getCollisionHandlingGraphFor(null);
        pathFinder = new IndexedAStarPathFinder<>(groundGraph);
        unitPathFinder = new IndexedAStarPathFinder<>(unitCollisionHandlingGroundGraph);
    }

    public void handleGroundUnitMovement(float delta, List<UnitBase> units) {
        Circle unitCircle;
        GroundNode curNode;
        GroundNode newNode;
        GroundNode destNode;
        GroundNode finNode;
        Vector2 pos = new Vector2();
        Vector2 destPos = new Vector2();
        Vector2 travelVec;
        groundGraph.newUpdateFrame();
        for (UnitBase unit : units) {
            unitCircle =  unit.circle;
            pos.set(unitCircle.x, unitCircle.y);
            curNode = groundGraph.getNodeByCoords(unit.getMapCenterX(), unit.getMapCenterY());
            finNode = groundGraph.getNodeByCoords(unit.destNodeX, unit.destNodeY);
            if (groundGraph.getDist(curNode, finNode) <= groundGraph.getDistOfClosestFreeNode(finNode)) {
                unit.state = UnitState.NONE;
                groundGraph.update(curNode);
                continue;
            } else {
                finNode = groundGraph.getClosestFreeNode(curNode, finNode);
            }

            ReroutableGraphPath<GroundNode> graphPath = groundPathCache.get(unit);
            unitCollisionHandlingGroundGraph.setUnitNode(curNode);

            if (graphPath == null || graphPath.getCount() < 1 || finNode != graphPath.get(graphPath.getCount() -1)) {
                findAndCacheGraphPath(unit, curNode, finNode, groundGraph.getHeuristic(), pathFinder);
                graphPath = groundPathCache.get(unit);
                graphPath.setNodesInPathToReroute(UnitCollisionHandlingGroundGraph.COLLISION_HANDLING_RANGE);
            } else {
                GraphPath<GroundNode> collisionHandlingPath = new DefaultGraphPath<>();
                int endOfCollisionPathIndex = Math.min(graphPath.getCount() - 1, UnitCollisionHandlingGroundGraph.COLLISION_HANDLING_RANGE);
                GroundNode endOfCollisionHandlingPath = graphPath.get(endOfCollisionPathIndex);
                if (unitPathFinder.searchNodePath(curNode,
                        endOfCollisionHandlingPath,
                        groundGraph.getHeuristic(),
                        collisionHandlingPath)) {
                    for (int i = 0; i <= endOfCollisionPathIndex; i++) {
                        graphPath.remove(0);
                    }
                    collisionHandlingPath.forEach(graphPath::addToReroute);
                }
            }

            if (graphPath.getCount() > 1) {
                destNode = graphPath.get(1);

                destPos.set(destNode.x * MapUtils.NODE_WIDTH_PX, destNode.y * MapUtils.NODE_HEIGHT_PX);
                travelVec = destPos.cpy().sub(pos);
                float maxTravelDist = unit.baseSpeed * unit.speedMulti * delta;
                travelVec.setLength(Math.min(maxTravelDist, travelVec.len()));

                newNode = groundGraph.getNodeByMapPixelCoords(pos.x + travelVec.x, pos.y + travelVec.y);
                if (newNode != curNode && newNode.getOccupant() == NodeOccupant.NONE) {
                    if (newNode == destNode) {
                        graphPath.remove(0);
                    }
                    unit.translate(travelVec);
                    groundGraph.update(curNode);
                    groundGraph.update(newNode);
                } else if (newNode == curNode) {
                    unit.translate(travelVec);
                } else {
//                    if (curNode == groundGraph.getNodeByMapPixelCoords(pos.x + travelVec.x, pos.y)) {
//                        //Entering from top or bottom
//                        travelVec.rotate90(-1 * Math.round(Math.signum(travelVec.x * travelVec.y)));
//                    } else {
//                        travelVec.rotate90(1 * Math.round(Math.signum(travelVec.x * travelVec.y)));
//                    }
//
//                    newNode = groundGraph.getNodeByMapPixelCoords(pos.x + travelVec.x, pos.y + travelVec.y);
//                    if (newNode != curNode && newNode.getOccupant() == NodeOccupant.NONE) {
//                        if (newNode == destNode) {
//                            graphPath.remove(0);
//                        }
//                        unit.translate(travelVec);
//                        curNode.setOccupant(NodeOccupant.NONE);
//                        newNode.setOccupant(NodeOccupant.MOVING_UNIT);
//                        findAndCacheGraphPath(unit, newNode, finNode, groundGraph, groundPathFinder);
//                    } else if (newNode == curNode) {
//                        unit.translate(travelVec);
//                    }
//                    graphPath.remove(0);
//                    graphPath.clearReroute();
//                    //Next node is new and contains something
//                    int dirHorizontal = Math.round(Math.signum(travelVec.x));
//                    if (dirHorizontal == 0) {
//                        dirHorizontal = 1;
//                    }
//                    int dirVertical = Math.round(Math.signum(travelVec.y));
//                    if (dirVertical == 0) {
//                        dirVertical = 1;
//                    }
//                    if (curNode != groundGraph.getNodeByMapPixelCoords(pos.x + travelVec.x, pos.y)) {
//                        //Entering from right or left
//                        graphPath.addToReroute(groundGraph.getNodeByCoords(curNode.x + dirHorizontal, curNode.y));
//                        graphPath.addToReroute(groundGraph.getNodeByCoords(curNode.x + dirHorizontal, curNode.y + dirVertical));
//                        graphPath.addToReroute(groundGraph.getNodeByCoords(curNode.x + dirHorizontal, curNode.y + 2 * dirVertical));
//                    } else {
//                        //Entering from top or bottom
//                        graphPath.addToReroute(groundGraph.getNodeByCoords(curNode.x, curNode.y + dirVertical));
//                        graphPath.addToReroute(groundGraph.getNodeByCoords(curNode.x + dirHorizontal, curNode.y + dirVertical));
//                        graphPath.addToReroute(groundGraph.getNodeByCoords(curNode.x + 2 * dirHorizontal, curNode.y + dirVertical));
//                    }
                }
            }
        }
    }

    private void findAndCacheGraphPath(UnitBase unit,
                                       GroundNode startNode,
                                       GroundNode finNode,
                                       Heuristic<GroundNode> heuristic,
                                       PathFinder<GroundNode> pathFinder) {

        ReroutableGraphPath<GroundNode> graphPath = new ReroutableGraphPath<>();

        if (pathFinder.searchNodePath(startNode,
                finNode,
                heuristic,
                graphPath)) {
            groundPathCache.put(unit, graphPath);
        }
    }
}
