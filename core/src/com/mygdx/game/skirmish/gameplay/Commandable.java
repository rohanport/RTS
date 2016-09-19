package com.mygdx.game.skirmish.gameplay;

/**
 * Created by paddlefish on 19-Sep-16.
 * Interface for objects that can process keyboard commands and mouseclicks, eg. Units, buildings, etc
 */
public interface Commandable {

    public boolean processKeyStroke(int keycode);

    public boolean processRightClick(int screenX, int screenY);
}
