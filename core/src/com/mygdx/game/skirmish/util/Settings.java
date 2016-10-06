package com.mygdx.game.skirmish.util;

import com.badlogic.gdx.Input;

/**
 * Created by paddlefish on 22-Sep-16.
 */
public class Settings {
    public static final boolean DEBUG_MODE = true;
    public static final boolean FULLSCREEN_MODE = false;

    public static final float TIMEFRAME = 1/25f;
    public static final float MIN_DELTA = 1/4f;

    public class HotKeys {
        public static final int ATK = Input.Keys.A;
    }

}
