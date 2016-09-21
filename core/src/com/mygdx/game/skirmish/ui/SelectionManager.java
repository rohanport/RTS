package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.units.UnitBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 19-Sep-16.
 */
public class SelectionManager implements InputProcessor {
    private final SkirmishScreen screen;

    private List<Commandable> selection;
    private List<Commandable> newSelection;
    private Rectangle selector;

    private boolean isSelecting;

    public SelectionManager(SkirmishScreen screen) {
        this.screen = screen;

        selection = new ArrayList<Commandable>();
        newSelection = new ArrayList<Commandable>();
        selector = new Rectangle();
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
        Vector3 mapCords = screen.getCam().unproject(new Vector3(screenX, screenY, 0));

        if (button == 0) {
            handleSelectionStart(screenX, screenY);
        } else if (button == 1) {
            handleRightClick(mapCords);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            handleSelectionEnd();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        handleSelectionDrag(screenX, screenY);
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

    private void handleSelectionStart(float screenX, float screenY) {
        selector.setX(screenX);
        selector.setY(screenY);
        isSelecting = true;
    }

    private void handleSelectionEnd() {
        if (newSelection.size() > 0) {
            selection.clear();
            selection.addAll(newSelection);
            newSelection.clear();
        }

        isSelecting = false;
    }

    private void handleSelectionDrag(float screenX, float screenY) {
        if (isSelecting) {
            selector.setWidth(screenX - selector.getX());
            selector.setHeight(screenY - selector.getY());

            Vector3 mapA = screen.getCam().unproject(new Vector3(selector.getX(), selector.getY(), 0));
            Vector3 mapB = screen.getCam().unproject(new Vector3(selector.getX() + selector.getWidth(), selector.getY(), 0));
            Vector3 mapC = screen.getCam().unproject(new Vector3(selector.getX() + selector.getWidth(), selector.getY() + selector.getHeight(), 0));
            Vector3 mapD = screen.getCam().unproject(new Vector3(selector.getX(), selector.getY() + selector.getHeight(), 0));

            Polygon selectionPolygon = new Polygon(new float[]{mapA.x, mapA.y,
                    mapB.x, mapB.y,
                    mapC.x, mapC.y,
                    mapD.x, mapD.y
            });

            List<UnitBase> intersectingUnits = screen.getUnitManager().getIntersectingUnits(selectionPolygon);

            newSelection.clear();
            newSelection.addAll(intersectingUnits);
        }
    }

    private void handleRightClick(Vector3 mapCords) {
        for (Commandable commandable : selection) {
            commandable.processRightClick(Math.round(mapCords.x), Math.round(mapCords.y));
        }
    }
}
