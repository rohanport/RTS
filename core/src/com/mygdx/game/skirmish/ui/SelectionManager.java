package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.game.skirmish.gameplay.Commandable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 19-Sep-16.
 */
public class SelectionManager implements InputProcessor {

    private List<Commandable> selection;

    public SelectionManager() {
        selection = new ArrayList<Commandable>();
    }

    public void addToSelection(Commandable commandable) {
        selection.add(commandable);
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
        if (button == 1) {
            for (Commandable commandable : selection) {
                commandable.processRightClick(screenX, screenY);
            }
        }

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
