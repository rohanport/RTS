package com.mygdx.game.skirmish;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.skirmish.buildings.BuildingManager;
import com.mygdx.game.skirmish.gameplay.movement.MovementHandler;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.units.UnitManager;
import com.mygdx.game.skirmish.units.UnitState;
import com.mygdx.game.skirmish.util.Settings;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class World implements Disposable {

    private final SkirmishScreen screen;
    private final UnitManager unitManager;
    private final BuildingManager buildingManager;
    private final MovementHandler movementHandler;
    private final GroundGraph groundGraph;

    public final int width;
    public final int height;

    private float accumulatedDelta = 0f;

    //------ Setters and Getters ----------
    public UnitManager getUnitManager() {
        return unitManager;
    }
    public BuildingManager getBuildingManager() {
        return buildingManager;
    }
    public GroundGraph getGroundGraph() {
        return groundGraph;
    }
    //-------------------------------

    public World(SkirmishScreen screen, int width, int height) {
        this.screen = screen;
        this.width = width;
        this.height = height;

        this.unitManager = this.screen.getUnitManager();
        this.buildingManager = this.screen.getBuildingManager();

        this.groundGraph = new GroundGraph(this);
        groundGraph.newUpdateFrame();
        movementHandler = new MovementHandler(this);
    }

    // Update to be called after rendering
    public void update(float delta) {
        delta = Math.min(delta, Settings.MIN_DELTA);
        accumulatedDelta += delta;

        for (; accumulatedDelta >= Settings.TIMEFRAME; accumulatedDelta -= Settings.TIMEFRAME) {
            step(Settings.TIMEFRAME);
        }
    }

    public void renderNodesDebug() {
        groundGraph.debugRender(screen.getCam());
    }

    private void step(float timeframe) {

        movementHandler.handleGroundUnitMovement(timeframe, unitManager.getUnitsInState(UnitState.MOVING));

    }



    @Override
    public void dispose() {

    }
}
