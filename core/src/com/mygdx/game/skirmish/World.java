package com.mygdx.game.skirmish;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.skirmish.gameplay.movement.MovementManager;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.util.Settings;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class World implements Disposable {

    private final SkirmishScreen screen;
    private final UnitManager unitManager;
    private final MovementManager movementManager;
    private final GroundGraph groundGraph;

    public final int width;
    public final int height;

    private float accumulatedDelta = 0f;

    //------ Setters and Getters ----------
    public UnitManager getUnitManager() {
        return unitManager;
    }

    //-------------------------------

    public World(SkirmishScreen screen, int width, int height) {
        this.screen = screen;
        this.width = width;
        this.height = height;

        this.unitManager = this.screen.getUnitManager();
        movementManager = new MovementManager(this);

        this.groundGraph = new GroundGraph(this);
        groundGraph.update();
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
                groundGraph);

    }



    @Override
    public void dispose() {

    }
}
