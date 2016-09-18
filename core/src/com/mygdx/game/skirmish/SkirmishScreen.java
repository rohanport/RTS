package com.mygdx.game.skirmish;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.DefaultScreen;
import com.mygdx.game.Resources;
import com.mygdx.game.skirmish.map.MapCamera;
import com.mygdx.game.skirmish.map.SelectorRenderer;

/**
 * Created by paddlefish on 17-Sep-16.
 */
public class SkirmishScreen extends DefaultScreen implements InputProcessor {

    private static final int MAP_HEIGHT = 5000;
    private static final int MAP_WIDTH  = 5000;

    //--------- Managers -------
    private UnitManager unitManager;
    private SelectorRenderer selectorRenderer;

    //-------- Sprites --------
    private Sprite background;

    private SpriteBatch backgroundBatch;

    //-------- Camera ---------
    private MapCamera cam = new MapCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    public SkirmishScreen(Game game) {
        super(game);
        Gdx.input.setInputProcessor(this);

        unitManager = new UnitManager();
        selectorRenderer = new SelectorRenderer();
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

        selectorRenderer.render();
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
        selectorRenderer.handleMouseDown(screenX, screenY);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        selectorRenderer.handleMouseUp();

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
