package com.mygdx.game.skirmish.gameplay;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by paddlefish on 19-Sep-16.
 * Interface for objects that can process keyboard commands and mouseclicks, eg. Units, buildings, etc
 */
public interface Commandable {

    boolean processKeyStroke(int keycode);

    boolean processRightClick(int screenX, int screenY);

    boolean processAtkCommand(int targetID);

    void renderSelectionMarker(ShapeRenderer shapeRenderer);

    boolean isMoveable();

    int getMapCenterX();

    int getMapCenterY();
}
