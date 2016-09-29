package com.mygdx.game.skirmish;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.DefaultScreen;
import com.mygdx.game.Resources;
import com.mygdx.game.skirmish.buildings.Building1;
import com.mygdx.game.skirmish.buildings.BuildingManager;
import com.mygdx.game.skirmish.gameplay.GameObjectManager;
import com.mygdx.game.skirmish.map.MapCamera;
import com.mygdx.game.skirmish.map.SelectorRenderer;
import com.mygdx.game.skirmish.ui.SelectionManager;
import com.mygdx.game.skirmish.units.Soldier1;
import com.mygdx.game.skirmish.units.UnitManager;
import com.mygdx.game.skirmish.util.MapUtils;
import com.mygdx.game.skirmish.util.Settings;

/**
 * Created by paddlefish on 17-Sep-16.
 */
public class SkirmishScreen extends DefaultScreen implements InputProcessor {

    //--------- Managers -------
    private final World world;
    private final InputMultiplexer inputHandler;
    private final GameObjectManager gameObjectManager;
    private final UnitManager unitManager;
    private final BuildingManager buildingManager;
    private final SelectionManager selectionManager;
    private final SelectorRenderer selectorRenderer;

    //-------- Sprites --------
    private Sprite background;

    private SpriteBatch backgroundBatch;

    //-------- Camera ---------
    private MapCamera cam = new MapCamera(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            MapUtils.MAP_WIDTH * MapUtils.NODE_WIDTH_PX,
            MapUtils.MAP_HEIGHT * MapUtils.NODE_HEIGHT_PX
    );


    //------- Getters and Setters --------
    public GameObjectManager getGameObjectManager() {
        return gameObjectManager;
    }

    public UnitManager getUnitManager() {
        return unitManager;
    }

    public BuildingManager getBuildingManager() {
        return buildingManager;
    }

    public MapCamera getCam() {
        return cam;
    }

    //---------------------------------------------------------------------
    public SkirmishScreen(Game game) {
        super(game);

        gameObjectManager = new GameObjectManager(this);
        unitManager = new UnitManager(this);
        buildingManager = new BuildingManager(this);
        selectionManager = new SelectionManager(this);
        selectorRenderer = new SelectorRenderer();
        inputHandler = new InputMultiplexer();

        Gdx.input.setInputProcessor(inputHandler);
        inputHandler.addProcessor(this);
        inputHandler.addProcessor(selectionManager);
        world = new World(this, MapUtils.MAP_WIDTH, MapUtils.MAP_HEIGHT);
    }

    @Override
    public void show() {
        background = Resources.getInstance().bgGrass01;
        background.setPosition(0, 0);
        background.setSize(MapUtils.MAP_WIDTH * MapUtils.NODE_WIDTH_PX, MapUtils.MAP_HEIGHT * MapUtils.NODE_HEIGHT_PX);
        background.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        backgroundBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        cam.update(delta);
        backgroundBatch.setProjectionMatrix(cam.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backgroundBatch.begin();
        background.draw(backgroundBatch);
        backgroundBatch.end();

        if (Settings.DEBUG_MODE) {
            world.renderNodesDebug();
            unitManager.renderUnitsDebug();
            buildingManager.renderBuildingsDebug();
        } else {
            unitManager.renderUnits();
            buildingManager.renderBuildings();
        }

        selectionManager.renderSelection(cam);

        selectorRenderer.render();

        world.update(delta);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.getTexture().dispose();
        backgroundBatch.dispose();
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            Vector2 middleOfScreen = MapUtils.screenCoords2NodeCoords(cam, cam.viewportWidth / 2f, cam.viewportHeight / 2f);
            Soldier1 test = new Soldier1(Math.round(middleOfScreen.x + 20 * ((float) Math.random() - 0.5f)),
                    Math.round(middleOfScreen.y + 20 * ((float) Math.random() - 0.5f)));
            gameObjectManager.add(test);
            selectionManager.addToSelection(test);
        }

        if (keycode == Input.Keys.SPACE) {
            Vector2 middleOfScreen = MapUtils.screenCoords2NodeCoords(cam, cam.viewportWidth / 2f, cam.viewportHeight / 2f);
            Building1 test = new Building1(Math.round(middleOfScreen.x), Math.round(middleOfScreen.y));
            gameObjectManager.add(test);
            selectionManager.addToSelection(test);
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
            selectorRenderer.handleMouseDown(screenX, screenY);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            selectorRenderer.handleMouseUp();
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        selectorRenderer.handleMouseMove(screenX, screenY);

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

        if (screenX < 2 && !cam.isMovingLeft()) {
            cam.setMovingLeft(true);
        }
        if (screenX > 2 && cam.isMovingLeft()) {
            cam.setMovingLeft(false);
        }

        if (distFromScreenRight < 2 && !cam.isMovingRight()) {
            cam.setMovingRight(true);
        }
        if (distFromScreenRight > 2 && cam.isMovingRight()) {
            cam.setMovingRight(false);
        }

        if (screenY < 2 && !cam.isMovingUp()) {
            cam.setMovingUp(true);
        }
        if (screenY > 2 && cam.isMovingUp()) {
            cam.setMovingUp(false);
        }

        if (distFromScreenBottom < 2 && !cam.isMovingDown()) {
            cam.setMovingDown(true);
        }
        if (distFromScreenBottom > 2 && cam.isMovingDown()) {
            cam.setMovingDown(false);
        }
    }
}
