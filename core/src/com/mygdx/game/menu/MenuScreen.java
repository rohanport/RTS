package com.mygdx.game.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.mygdx.game.DefaultScreen;
import com.mygdx.game.GameData;
import com.mygdx.game.ScreenOption;
import com.mygdx.game.skirmish.SkirmishScreen;

/**
 * Created by paddlefish on 17-Sep-16.
 *
 * Main menu of the game
 */
public class MenuScreen extends DefaultScreen implements InputProcessor {

    private Sprite title;
    private BoundingBox collisionTitle = new BoundingBox();

    private SpriteBatch titleBatch;

    private ScreenOption nextScreen = ScreenOption.MENU;

    public MenuScreen(Game game) {
        super(game);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {
        title = GameData.getInstance().title;
        collisionTitle.set(
                new Vector3(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4, -10),
                new Vector3(3 * Gdx.graphics.getWidth() / 4, 3 * Gdx.graphics.getHeight() / 4, 10)
        );

        titleBatch = new SpriteBatch();
        titleBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        titleBatch.begin();
        titleBatch.draw(title, Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        titleBatch.end();

        if (nextScreen == ScreenOption.SKIRMISH) {
            game.setScreen(new SkirmishScreen(game));
            this.dispose();
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        title.getTexture().dispose();
        titleBatch.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if ((Gdx.graphics.getWidth() / 4 < screenX) && (screenX < 3 * Gdx.graphics.getWidth() / 4) &&
                (Gdx.graphics.getHeight() / 4 < screenY) && (screenY < 3 * Gdx.graphics.getHeight() / 4)) {
            nextScreen = ScreenOption.SKIRMISH;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }
}
