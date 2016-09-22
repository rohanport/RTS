package com.mygdx.game.skirmish;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.skirmish.gameplay.movement.MovementManager;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.gameplay.pathfinding.NodeOccupant;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.util.Settings;

import java.util.List;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class World implements Disposable {

    private final SkirmishScreen screen;
    private final UnitManager unitManager;
    private final MovementManager movementManager;
    private final GroundGraph groundGraph;
    private final IndexedAStarPathFinder<GroundNode> groundPathFinder;

    private final int width;
    private final int height;

    private float accumulatedDelta = 0f;

    public World(SkirmishScreen screen, int width, int height) {
        this.screen = screen;
        this.width = width;
        this.height = height;

        this.unitManager = this.screen.getUnitManager();
        movementManager = new MovementManager(this);

        this.groundGraph = new GroundGraph(width, height);
        initializeGroundGraph();
        groundPathFinder = new IndexedAStarPathFinder<GroundNode>(groundGraph);
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

        movementManager.handleGroundUnitMovement(timeframe,
                unitManager.getUnits(),
                groundGraph,
                groundPathFinder);

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
