package com.mygdx.game.skirmish.gameplay;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.skirmish.gameobjects.GameObject;

/**
 * Created by paddlefish on 19-Sep-16.
 * Interface for objects that can process keyboard commands and mouseclicks, eg. Units, buildings, etc
 */
public interface Commandable {

    boolean processKeyStroke(int keycode);

    boolean processMoveCommand(int x, int y);

    boolean processRightClickOn(GameObject gameObject);

    boolean processAtkCommand(int targetID);

    boolean processBuildCommand(int x, int y);

    void renderSelectionMarker(ShapeRenderer shapeRenderer);

    Sprite getPortrait();

    boolean isMoveable();

    int getMapCenterX();

    int getMapCenterY();
}
