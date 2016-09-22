package com.mygdx.game.skirmish;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundHeuristic;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.gameplay.pathfinding.NodeOccupant;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.util.MapUtils;
import com.mygdx.game.skirmish.util.Settings;

import java.util.List;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class World implements Disposable {

    private final SkirmishScreen screen;
    private final UnitManager unitManager;
    private final GroundGraph groundGraph;
    private final IndexedAStarPathFinder<GroundNode> groundPathFinder;
    private final Heuristic<GroundNode> groundNodeHeuristic;

    private final int width;
    private final int height;

    private float accumulatedDelta = 0f;

    public World(SkirmishScreen screen, int width, int height) {
        this.screen = screen;
        this.width = width;
        this.height = height;

        this.unitManager = screen.getUnitManager();
        this.groundGraph = new GroundGraph(width, height);
        initializeGroundGraph();
        groundPathFinder = new IndexedAStarPathFinder<GroundNode>(groundGraph);
        groundNodeHeuristic = new GroundHeuristic();
    }

    // Update to be called after rendering
    public void update(float delta) {
        delta = Math.min(delta, Settings.MIN_DELTA);
        accumulatedDelta += delta;

        for (; accumulatedDelta >= Settings.TIMEFRAME; accumulatedDelta -= Settings.TIMEFRAME) {
            step(Settings.TIMEFRAME);
        }
    }

    private void step(float timeframe) {
        handleUnitMovement(timeframe);
    }

    private void handleUnitMovement(float delta) {
        List<UnitBase> units = unitManager.getUnits();

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
                    groundNodeHeuristic,
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



    @Override
    public void dispose() {

    }

    private void initializeGroundGraph() {
        List<UnitBase> units = unitManager.getUnits();

        Circle unitCircle;
        for (UnitBase unit : units) {
            unitCircle =  unit.circle;
            GroundNode node = groundGraph.getNodeByMapPixelCoords(unitCircle.x, unitCircle.y);
            node.setOccupant(NodeOccupant.GROUND_UNIT);
        }
    }
}
