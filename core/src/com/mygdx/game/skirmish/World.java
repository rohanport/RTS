package com.mygdx.game.skirmish;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.skirmish.gameobjects.GameObjectManager;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingManager;
import com.mygdx.game.skirmish.gameobjects.units.UnitManager;
import com.mygdx.game.skirmish.gameobjects.units.UnitState;
import com.mygdx.game.skirmish.gameplay.combat.CombatHandler;
import com.mygdx.game.skirmish.gameplay.combat.DestructionHandler;
import com.mygdx.game.skirmish.gameplay.gathering.GatheringHandler;
import com.mygdx.game.skirmish.gameplay.movement.MovementHandler;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.gameplay.production.ProductionHandler;
import com.mygdx.game.skirmish.gameplay.production.ProductionManager;
import com.mygdx.game.skirmish.gameplay.production.UnitProductionTaskFactory;
import com.mygdx.game.skirmish.resources.ResourceManager;
import com.mygdx.game.skirmish.util.Settings;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class World implements Disposable {

    private final SkirmishScreen screen;
    private final GameObjectManager gameObjectManager;
    private final UnitManager unitManager;
    private final BuildingManager buildingManager;
    private final ProductionManager productionManager;
    private final ResourceManager resourceManager;
    private final ProductionHandler productionHandler;
    private final UnitProductionTaskFactory unitProductionTaskFactory;
    private final MovementHandler movementHandler;
    private final CombatHandler combatHandler;
    private final DestructionHandler destructionHandler;
    private final GatheringHandler gatheringHandler;
    private final GroundGraph groundGraph;

    public final int width;
    public final int height;

    private float accumulatedDelta = 0f;

    //------ Setters and Getters ----------
    public SkirmishScreen getScreen() {
        return screen;
    }
    public GameObjectManager getGameObjectManager() {
        return gameObjectManager;
    }
    public UnitManager getUnitManager() {
        return unitManager;
    }
    public BuildingManager getBuildingManager() {
        return buildingManager;
    }
    public ProductionManager getProductionManager() {
        return productionManager;
    }
    public ResourceManager getResourceManager() {
        return resourceManager;
    }
    public GroundGraph getGroundGraph() {
        return groundGraph;
    }
    public UnitProductionTaskFactory getUnitProductionTaskFactory() {
        return unitProductionTaskFactory;
    }
    //-------------------------------

    public World(SkirmishScreen screen, int width, int height) {
        this.screen = screen;
        this.width = width;
        this.height = height;

        gameObjectManager = this.screen.getGameObjectManager();
        unitManager = this.screen.getUnitManager();
        buildingManager = this.screen.getBuildingManager();
        productionManager = this.screen.getProductionManager();
        resourceManager = this.screen.getResourceManager();

        this.groundGraph = new GroundGraph(this);
        groundGraph.newUpdateFrame();

        movementHandler = new MovementHandler(this);
        combatHandler = new CombatHandler(this);
        destructionHandler = new DestructionHandler(this);
        productionHandler = new ProductionHandler(this);
        unitProductionTaskFactory = new UnitProductionTaskFactory(this);
        gatheringHandler = new GatheringHandler(this);
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
        movementHandler.handleGroundUnitMoving(timeframe, unitManager.getUnitsInState(UnitState.MOVING));
        movementHandler.handleGroundUnitMovingToAtk(timeframe, unitManager.getUnitsInState(UnitState.MOVING_TO_ATK), gameObjectManager);
        movementHandler.handleGroundUnitMovingToBuild(timeframe, unitManager.getBuilderUnitsInState(UnitState.MOVING_TO_BUILD));
        movementHandler.handleGroundUnitMovingToGather(timeframe, unitManager.getGatherersInState(UnitState.MOVING_TO_GATHER), gameObjectManager);
        movementHandler.handleGroundUnitMovingToReturnResources(timeframe, unitManager.getGatherersInState(UnitState.MOVING_TO_RETURN_RESOURCES), gameObjectManager);
        combatHandler.handleAtkStarting(timeframe, unitManager.getUnitsInState(UnitState.ATK_STARTING));
        combatHandler.handleAtkEnding(timeframe, unitManager.getUnitsInState(UnitState.ATK_ENDING));
        gatheringHandler.handleGatherers(timeframe, unitManager.getGatherersInState(UnitState.GATHERING));
        destructionHandler.handleGameObjectDestruction(gameObjectManager.getGameObjectsToBeDestroyed());
        productionHandler.handleRunningProductions(timeframe, productionManager.getRunningProductionTasks());
        buildingManager.update(timeframe);
    }



    @Override
    public void dispose() {

    }
}
