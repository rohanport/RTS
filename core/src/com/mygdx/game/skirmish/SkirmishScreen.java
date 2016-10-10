package com.mygdx.game.skirmish;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.DefaultScreen;
import com.mygdx.game.skirmish.gameobjects.GameObjectCache;
import com.mygdx.game.skirmish.gameobjects.GameObjectManager;
import com.mygdx.game.skirmish.gameobjects.buildings.Building1;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingManager;
import com.mygdx.game.skirmish.gameobjects.units.UnitManager;
import com.mygdx.game.skirmish.gameplay.production.ProductionManager;
import com.mygdx.game.skirmish.map.DragBoxRenderer;
import com.mygdx.game.skirmish.map.MapCamera;
import com.mygdx.game.skirmish.player.Player;
import com.mygdx.game.skirmish.player.PlayerManager;
import com.mygdx.game.skirmish.resources.Resource;
import com.mygdx.game.skirmish.resources.ResourceManager;
import com.mygdx.game.skirmish.resources.ResourceType;
import com.mygdx.game.skirmish.ui.GUI;
import com.mygdx.game.skirmish.ui.SelectionManager;
import com.mygdx.game.skirmish.util.MapUtils;
import com.mygdx.game.skirmish.util.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 17-Sep-16.
 */
public class SkirmishScreen extends DefaultScreen implements InputProcessor {

    //--------- Managers -------
    private final World world;
    private final InputMultiplexer inputHandler;
    private final GameObjectCache gameObjectCache;
    private final UnitManager unitManager;
    private final BuildingManager buildingManager;
    private final ProductionManager productionManager;
    private final SelectionManager selectionManager;
    private final PlayerManager playerManager;
    private final ResourceManager resourceManager;
    private final DragBoxRenderer dragBoxRenderer;
    private final GUI gui;
    private final List<GameObjectManager> gameObjectManagers;

    //-------- Map --------
    private final TiledMap map;
    private final TiledMapRenderer mapRenderer;

    //-------- Camera ---------
    private MapCamera cam = new MapCamera(
            700,
            700 * (Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth()),
            MapUtils.MAP_WIDTH * MapUtils.NODE_WIDTH_PX,
            MapUtils.MAP_HEIGHT * MapUtils.NODE_HEIGHT_PX
    );


    //------- Getters and Setters --------
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

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public List<GameObjectManager> getGameObjectManagers() {
        return gameObjectManagers;
    }

    public MapCamera getCam() {
        return cam;
    }

    public InputMultiplexer getInputHandler() {
        return inputHandler;
    }
    public World getWorld() {
        return world;
    }
    //---------------------------------------------------------------------
    public SkirmishScreen(Game game) {
        super(game);

        gameObjectCache = new GameObjectCache(this);
        unitManager = new UnitManager(this);
        buildingManager = new BuildingManager(this);
        productionManager = new ProductionManager(this);
        selectionManager = new SelectionManager(this);
        resourceManager = new ResourceManager(this);
        gameObjectCache.addObserver(unitManager);
        gameObjectCache.addObserver(buildingManager);
        gameObjectCache.addObserver(selectionManager);
        gameObjectCache.addObserver(resourceManager);
        playerManager = new PlayerManager(this);
        dragBoxRenderer = new DragBoxRenderer();
        inputHandler = new InputMultiplexer();
        gui = new GUI(this);

        Gdx.input.setInputProcessor(inputHandler);
        inputHandler.addProcessor(this);
        inputHandler.addProcessor(selectionManager);

        world = new World(this, MapUtils.MAP_WIDTH, MapUtils.MAP_HEIGHT);
        gameObjectManagers = new ArrayList<>();
        gameObjectManagers.add(unitManager);
        gameObjectManagers.add(buildingManager);
        gameObjectManagers.add(resourceManager);

        map = new TmxMapLoader().load("maps/desert.tmx");

        float ratio = (float)(MapUtils.NODE_WIDTH_PX * MapUtils.MAP_WIDTH) /
                ((int) map.getProperties().get("width") * (int) map.getProperties().get("tilewidth"));
        map.getTileSets().getTileSet(0).forEach(tiledMapTile -> {
            tiledMapTile.setOffsetX(-MapUtils.NODE_WIDTH_PX  / (ratio * 2f));
            tiledMapTile.setOffsetY(-MapUtils.NODE_HEIGHT_PX / (ratio * 2f));
        });

        mapRenderer = new OrthogonalTiledMapRenderer(
                map,
                ratio
        );

        world.loadMap(map);
    }

    @Override
    public void show() {
        cam.position.set(
                MapUtils.MAP_WIDTH * MapUtils.NODE_WIDTH_PX + 1000,
                -1000,
                1000
        );
        cam.lookAt(MapUtils.MAP_WIDTH * MapUtils.NODE_WIDTH_PX, 0, 0);
        cam.rotate(-60, -1, 1, -1);
        cam.near = 0;
        cam.far = 10000;
    }

    @Override
    public void render(float delta) {
        cam.update(delta); //Move the camera

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float left = MapUtils.screenCoords2MapCoords(cam, 0, 0).x;
        float top = MapUtils.screenCoords2MapCoords(cam, Gdx.graphics.getWidth(), 0).y;
        float right = MapUtils.screenCoords2MapCoords(cam, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).x;
        float bottom = MapUtils.screenCoords2MapCoords(cam, 0, Gdx.graphics.getHeight()).y;
        mapRenderer.setView(cam.combined, left, bottom, right - left, top - bottom);
        mapRenderer.render();

        if (Settings.DEBUG_MODE) {
            world.renderNodesDebug();
        }

        unitManager.render(Settings.DEBUG_MODE);
        buildingManager.render(Settings.DEBUG_MODE);
        resourceManager.render(Settings.DEBUG_MODE);
        selectionManager.renderSelectionMarkers(cam);

        gui.render(Settings.DEBUG_MODE);
        dragBoxRenderer.render();

        world.update(delta);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
    }


    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.SPACE) {
            if (playerManager.getNumPlayers() <= 0) {
                Player player1 = new Player(0, Color.BLUE);
                playerManager.addPlayer(player1);
            }
            Vector2 middleOfScreen = MapUtils.screenCoords2NodeCoords(cam, cam.viewportWidth / 2f, cam.viewportHeight / 2f);
            Building1 test = new Building1(world, playerManager.getPlayerByID(0).id, Math.round(middleOfScreen.x), Math.round(middleOfScreen.y));
            gameObjectCache.add(test);
            selectionManager.addToSelection(test);
        }

        if (keycode == Input.Keys.ENTER) {
            if (playerManager.getNumPlayers() <= 1) {
                Player player1 = new Player(1, Color.YELLOW);
                playerManager.addPlayer(player1);
            }
            Vector2 middleOfScreen = MapUtils.screenCoords2NodeCoords(cam, cam.viewportWidth / 2f, cam.viewportHeight / 2f);
            Building1 test = new Building1(world, playerManager.getPlayerByID(1).id, Math.round(middleOfScreen.x + 20), Math.round(middleOfScreen.y));
            gameObjectCache.add(test);
            selectionManager.addToSelection(test);
        }

        if (keycode == Input.Keys.P) {
            Vector2 middleOfScreen = MapUtils.screenCoords2NodeCoords(cam, cam.viewportWidth / 2f, cam.viewportHeight / 2f);
            Resource test = new Resource(
                    ResourceType.FOOD,
                    2000,
                    Math.round(middleOfScreen.x + 60f * ((float) Math.random() - 0.5f)),
                    Math.round(middleOfScreen.y + 60f * ((float) Math.random() - 0.5f)),
                    3
            );
            gameObjectCache.add(test);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            dragBoxRenderer.handleMouseDown(screenX, screenY);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            dragBoxRenderer.handleMouseUp();
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        dragBoxRenderer.handleMouseMove(screenX, screenY);

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        handleCameraScrolling(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private void handleCameraScrolling(int screenX, int screenY) {
        int distFromScreenRight = Gdx.graphics.getWidth() - screenX;
        int distFromScreenBottom = Gdx.graphics.getHeight() - screenY;

        if (screenX < 5 && !cam.isMovingLeft()) {
            cam.setMovingLeft(true);
        }
        if (screenX > 5 && cam.isMovingLeft()) {
            cam.setMovingLeft(false);
        }

        if (distFromScreenRight < 5 && !cam.isMovingRight()) {
            cam.setMovingRight(true);
        }
        if (distFromScreenRight > 5 && cam.isMovingRight()) {
            cam.setMovingRight(false);
        }

        if (screenY < 5 && !cam.isMovingUp()) {
            cam.setMovingUp(true);
        }
        if (screenY > 5 && cam.isMovingUp()) {
            cam.setMovingUp(false);
        }

        if (distFromScreenBottom < 5 && !cam.isMovingDown()) {
            cam.setMovingDown(true);
        }
        if (distFromScreenBottom > 5 && cam.isMovingDown()) {
            cam.setMovingDown(false);
        }
    }
}
