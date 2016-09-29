package com.mygdx.game.skirmish.gameplay.movement;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameplay.GameObject;
import com.mygdx.game.skirmish.gameplay.GameObjectManager;
import com.mygdx.game.skirmish.gameplay.pathfinding.*;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.units.UnitState;
import com.mygdx.game.skirmish.util.GameMathUtils;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class MovementHandler {

    private final ConcurrentMap<UnitBase, ReroutableGraphPath<GroundNode>> groundPathCache;
    private final GroundGraph groundGraph;
    private final UnitCollisionHandlingGroundGraph unitCollisionHandlingGroundGraph;
    private final IndexedAStarPathFinder<GroundNode> pathFinder;
    private final IndexedAStarPathFinder<GroundNode> unitPathFinder;

    public MovementHandler(World world) {
        this.groundPathCache = new ConcurrentHashMap<>();
        groundGraph = world.getGroundGraph();
        unitCollisionHandlingGroundGraph = groundGraph.getCollisionHandlingGraphFor(null);
        pathFinder = new IndexedAStarPathFinder<>(groundGraph);
        unitPathFinder = new IndexedAStarPathFinder<>(unitCollisionHandlingGroundGraph);
    }

    public void handleGroundUnitMoving(float delta, List<UnitBase> units) {
        GroundNode curNode;
        GroundNode finNode;
        groundGraph.newUpdateFrame();
        for (UnitBase unit : units) {
            curNode = groundGraph.getNodeByCoords(unit.getMapCenterX(), unit.getMapCenterY());
            finNode = groundGraph.getNodeByCoords(unit.destNodeX, unit.destNodeY);
            if (GroundGraphUtils.getDist(curNode, finNode) <= GroundGraphUtils.getDistOfClosestFreeNode(groundGraph, finNode)) {
                unit.state = UnitState.NONE;
                groundGraph.update(curNode);
                continue;
            } else {
                finNode = groundGraph.getClosestFreeNode(curNode, finNode);
            }

            moveUnitAlongPath(delta, unit, curNode, finNode);
        }
    }

    public void handleGroundUnitMovingToAtk(float delta, List<UnitBase> units, GameObjectManager gameObjectManager) {
        GameObject atkTarget;
        GroundNode curNode;
        GroundNode finNode;
        groundGraph.newUpdateFrame();
        for (UnitBase unit : units) {
            atkTarget = gameObjectManager.getGameObjectByID(unit.getAtkTargetID());
            curNode = groundGraph.getNodeByCoords(unit.getMapCenterX(), unit.getMapCenterY());
            if (GameMathUtils.distBetween(
                    unit.getCenterX() / MapUtils.NODE_WIDTH_PX,
                    unit.getCenterY() / MapUtils.NODE_HEIGHT_PX,
                    atkTarget.getCenterX() / MapUtils.NODE_WIDTH_PX,
                    atkTarget.getCenterY() / MapUtils.NODE_HEIGHT_PX
                    ) < unit.range) {
                unit.state = UnitState.ATK_STARTING;
                groundGraph.update(curNode);
                continue;
            } else {
                finNode = groundGraph.getClosestFreeNodeEuclidean(
                        curNode,
                        atkTarget.getCenterX() / MapUtils.NODE_WIDTH_PX,
                        atkTarget.getCenterY() / MapUtils.NODE_HEIGHT_PX,
                        unit.range
                );
            }

            moveUnitAlongPath(delta, unit, curNode, finNode);
        }
    }

    private void moveUnitAlongPath(float delta, UnitBase unit, GroundNode curNode, GroundNode finNode) {
        ReroutableGraphPath<GroundNode> graphPath = groundPathCache.get(unit);
        if (graphPath == null || graphPath.getCount() < 1 || finNode != graphPath.get(graphPath.getCount() - 1)) {
            findAndCacheGraphPath(unit, curNode, finNode, groundGraph.getHeuristic(), pathFinder);
            graphPath = groundPathCache.get(unit);
        }

        updateCollisionHandlingSectionOfPath(graphPath, curNode);

        if (graphPath.getCount() > 1) {
            GroundNode destNode = graphPath.get(1);

            Vector2 pos = new Vector2(unit.circle.x, unit.circle.y);
            Vector2 destPos = new Vector2(destNode.x * MapUtils.NODE_WIDTH_PX, destNode.y * MapUtils.NODE_HEIGHT_PX);
            Vector2 travelVec = destPos.cpy().sub(pos);
            float maxTravelDist = unit.baseSpeed * unit.speedMulti * delta;
            travelVec.setLength(Math.min(maxTravelDist, travelVec.len()));

            GroundNode newNode = groundGraph.getNodeByMapPixelCoords(pos.x + travelVec.x, pos.y + travelVec.y);
            if (newNode != curNode && newNode.getOccupant() == NodeOccupant.NONE) {
                if (newNode == destNode) {
                    graphPath.remove(0);
                }
                unit.translate(travelVec);
                groundGraph.update(curNode);
                groundGraph.update(newNode);
            } else if (newNode == curNode) {
                unit.translate(travelVec);
            }
        }
    }

    private void updateCollisionHandlingSectionOfPath(ReroutableGraphPath<GroundNode> graphPath, GroundNode unitNode) {
        unitCollisionHandlingGroundGraph.setUnitNode(unitNode);
        GraphPath<GroundNode> collisionHandlingPath = new DefaultGraphPath<>();
        int endOfCollisionPathIndex = Math.min(graphPath.getCount() - 1, UnitCollisionHandlingGroundGraph.COLLISION_HANDLING_RANGE);
        GroundNode endOfCollisionHandlingPath = graphPath.get(endOfCollisionPathIndex);
        if (unitPathFinder.searchNodePath(unitNode,
                endOfCollisionHandlingPath,
                groundGraph.getHeuristic(),
                collisionHandlingPath)) {
            for (int i = 0; i <= endOfCollisionPathIndex; i++) {
                graphPath.remove(0);
            }
            collisionHandlingPath.forEach(graphPath::addToReroute);
        }
    }

    @SuppressWarnings("unchecked")
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
