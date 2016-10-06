package com.mygdx.game.skirmish.gameplay;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.skirmish.gameobjects.GameObject;

/**
 * Created by paddlefish on 19-Sep-16.
 * Interface for objects that can process keyboard commands and mouseclicks, eg. Units, buildings, etc
 */
public interface Commandable {

    boolean processKeyStroke(boolean chain, int keycode);

    boolean processMoveCommand(boolean chain, int x, int y);

    boolean processRightClickOn(boolean chain, GameObject gameObject);

    boolean processAtkCommand(boolean chain, int targetID);

    boolean processAtkMoveCommand(boolean chain, int x, int y);

    boolean processBuildCommand(boolean chain, int x, int y);

    void renderSelectionMarker(ShapeRenderer shapeRenderer);

    Sprite getPortrait();

    boolean isMoveable();

    int getMapCenterX();

    int getMapCenterY();
}
