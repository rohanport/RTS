package com.mygdx.game.skirmish;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.skirmish.gameobjects.GameObjectCache;
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
import com.mygdx.game.skirmish.gameplay.production.TransactionHandler;
import com.mygdx.game.skirmish.gameplay.production.UnitProductionTaskFactory;
import com.mygdx.game.skirmish.map.TiledMapLoader;
import com.mygdx.game.skirmish.player.PlayerManager;
import com.mygdx.game.skirmish.resources.ResourceManager;
import com.mygdx.game.skirmish.util.Settings;

/**
 * Created by paddlefish on 22-Sep-16.
 *
 * Responsible for controlling the stepping through of each gameplay frame.
 */
public class World implements Disposable {

    private final SkirmishScreen screen;
    private final GameObjectCache gameObjectCache;
    private final UnitManager unitManager;
    private final BuildingManager buildingManager;
    private final ProductionManager productionManager;
    private final ResourceManager resourceManager;
    private final PlayerManager playerManager;
    private final ProductionHandler productionHandler;
    private final UnitProductionTaskFactory unitProductionTaskFactory;
    private final MovementHandler movementHandler;
    private final CombatHandler combatHandler;
    private final DestructionHandler destructionHandler;
    private final GatheringHandler gatheringHandler;
    private final TransactionHandler transactionHandler;
    private final GroundGraph groundGraph;
    private final TiledMapLoader tiledMapLoader;

    public final int width;
    public final int height;

    private float accumulatedDelta = 0f;

    //------ Setters and Getters ----------
    public SkirmishScreen getScreen() {
        return screen;
    }
    public GameObjectCache getGameObjectCache() {
        return gameObjectCache;
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
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
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

        gameObjectCache = this.screen.getGameObjectCache();
        unitManager = this.screen.getUnitManager();
        buildingManager = this.screen.getBuildingManager();
        productionManager = this.screen.getProductionManager();
        resourceManager = this.screen.getResourceManager();
        playerManager = this.screen.getPlayerManager();

        groundGraph = new GroundGraph(this);
        groundGraph.newUpdateFrame();

        movementHandler = new MovementHandler(this);
        combatHandler = new CombatHandler(this);
        destructionHandler = new DestructionHandler(this);
        productionHandler = new ProductionHandler(this);
        unitProductionTaskFactory = new UnitProductionTaskFactory(this);
        gatheringHandler = new GatheringHandler(this);
        transactionHandler = new TransactionHandler(this);
        tiledMapLoader = new TiledMapLoader(this);
    }

    /**
     * Call this method before rendering
     * @param delta
     */
    public void update(float delta) {
        delta = Math.min(delta, Settings.MIN_DELTA); //This step allows for the game to slow under heavy lag
        accumulatedDelta += delta;

        //Performs a gameplay step for each frame within delta
        while (accumulatedDelta >= Settings.TIMEFRAME) {
            step(Settings.TIMEFRAME);
            accumulatedDelta -= Settings.TIMEFRAME;
        }
    }

    public void renderNodesDebug() {
        groundGraph.debugRender(screen.getCam());
    }

    /**
     * Performs a single gameplay frame and updates all GameObjects
     * @param timeframe
     */
    private void step(float timeframe) {
        groundGraph.newUpdateFrame(); //Refreshes the graph nodes

        //Handlers handle interactions between GameObjects and their environment
        movementHandler.handleGroundUnitMoving(timeframe, unitManager.getUnitsInState(UnitState.MOVING));
        movementHandler.handleGroundUnitMoving(timeframe, unitManager.getUnitsInState(UnitState.ATTACK_MOVING));
        movementHandler.handleGroundUnitMovingToAtk(timeframe, unitManager.getUnitsInState(UnitState.MOVING_TO_ATK), gameObjectCache);
        movementHandler.handleGroundUnitMovingToBuild(timeframe, unitManager.getBuilderUnitsInState(UnitState.MOVING_TO_BUILD));
        movementHandler.handleGroundUnitMovingToGather(timeframe, unitManager.getGatherersInState(UnitState.MOVING_TO_GATHER), gameObjectCache);
        movementHandler.handleGroundUnitMovingToReturnResources(timeframe, unitManager.getGatherersInState(UnitState.MOVING_TO_RETURN_RESOURCES), gameObjectCache);

        combatHandler.handleAtkStarting(timeframe, unitManager.getUnitsInState(UnitState.ATK_STARTING));
        combatHandler.handleAtkEnding(timeframe, unitManager.getUnitsInState(UnitState.ATK_ENDING));

        gatheringHandler.handleGatherers(timeframe, unitManager.getGatherersInState(UnitState.GATHERING));

        destructionHandler.handleGameObjectDestruction(gameObjectCache.getGameObjectsToBeDestroyed());

        productionHandler.handleRunningProductions(timeframe, productionManager.getRunningProductionTasks());

        //GameObject updates handle internal state of GameObjects
        unitManager.update(timeframe);
        buildingManager.update(timeframe);
    }



    @Override
    public void dispose() {

    }

    /**
     * Loads information stored in a Map into the Graph and GameObjectManagers
     * @param map
     */
    public void loadMap(Map map) {
        tiledMapLoader.loadMap(map);
    }
}
