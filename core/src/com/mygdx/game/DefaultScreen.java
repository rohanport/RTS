package com.mygdx.game;

/**
 * Created by paddlefish on 17-Sep-16.
 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public abstract class DefaultScreen implements Screen {
    protected Game game;

    public DefaultScreen(Game game) {
        this.game = game;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
//		dispose();
    }

    @Override
    public void resume() {
//		Resources.getInstance().reInit();
    }

    @Override
    public void dispose() {
//		Resources.getInstance().dispose();
    }

}
