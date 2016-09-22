package com.mygdx.game.skirmish;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.DefaultScreen;
import com.mygdx.game.Resources;
import com.mygdx.game.skirmish.map.MapCamera;
import com.mygdx.game.skirmish.map.SelectorRenderer;
import com.mygdx.game.skirmish.ui.SelectionManager;
import com.mygdx.game.skirmish.units.Soldier1;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 17-Sep-16.
 */
public class SkirmishScreen extends DefaultScreen implements InputProcessor {

    private static final int MAP_HEIGHT = 1000;
    private static final int MAP_WIDTH  = 1000;

    //--------- Managers -------
    private final World world;
    private final InputMultiplexer inputHandler;
    private final UnitManager unitManager;
    private final SelectionManager selectionManager;
    private final SelectorRenderer selectorRenderer;

    //-------- Sprites --------
    private Sprite background;

    private SpriteBatch backgroundBatch;

    //-------- Camera ---------
    private MapCamera cam = new MapCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), MAP_WIDTH, MAP_HEIGHT);


    //------- Getters and Setters --------
    public UnitManager getUnitManager() {
        return unitManager;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public MapCamera getCam() {
        return cam;
    }

    //---------------------------------------------------------------------
    public SkirmishScreen(Game game) {
        super(game);

        unitManager = new UnitManager(this);
        selectionManager = new SelectionManager(this);
        selectorRenderer = new SelectorRenderer();
        inputHandler = new InputMultiplexer();

        Gdx.input.setInputProcessor(inputHandler);
        inputHandler.addProcessor(this);
        inputHandler.addProcessor(selectionManager);
        world = new World(this, 500, 500);
    }

    @Override
    public void show() {
        background = Resources.getInstance().bgGrass01;
        background.setPosition(0, 0);
        background.setSize(MAP_WIDTH, MAP_HEIGHT);
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

        unitManager.renderUnitsDebug();

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
        if (keycode == Input.Keys.Q) {
            cam.rotate(1, 0, 0.1f , 0);
        } else if (keycode == Input.Keys.W) {
            cam.rotate(1, 0, -0.1f, 0);
        }

        if (keycode == Input.Keys.A) {
            cam.rotate(1, 0.1f, 0 , 0);
        } else if (keycode == Input.Keys.S) {
            cam.rotate(1, -0.1f, 0 , 0);
        }

        if (keycode == Input.Keys.Z) {
            cam.rotate(1, 0, 0 , 0.1f);
        } else if (keycode == Input.Keys.X) {
            cam.rotate(1, 0, 0 , -0.1f);
        }

        if (keycode == Input.Keys.ENTER) {
            Vector2 middleOfScreen = MapUtils.screenCoords2MapCoords(cam, cam.viewportWidth / 2f, cam.viewportHeight / 2f);
            Soldier1 test = new Soldier1(Math.round(middleOfScreen.x), Math.round(middleOfScreen.y));
            unitManager.addUnit(test);
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
