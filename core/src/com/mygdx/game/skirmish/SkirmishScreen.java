package com.mygdx.game.skirmish;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.DefaultScreen;
import com.mygdx.game.Resources;

/**
 * Created by paddlefish on 17-Sep-16.
 */
public class SkirmishScreen extends DefaultScreen implements InputProcessor {

    //--------- Managers -------
    private UnitManager unitManager;

    //-------- Sprites --------
    private Sprite background;

    private SpriteBatch backgroundBatch;

    //-------- Camera ---------
    private OrthographicCamera cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    public SkirmishScreen(Game game) {
        super(game);
        Gdx.input.setInputProcessor(this);

        unitManager = new UnitManager();
    }

    @Override
    public void show() {
        background = Resources.getInstance().bgGrass01;

        backgroundBatch = new SpriteBatch();
        backgroundBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        backgroundBatch.begin();
        backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundBatch.end();
    }

    @Override
    public void hide() {

    }

    @Override
    public boolean keyDown(int keycode) {
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
