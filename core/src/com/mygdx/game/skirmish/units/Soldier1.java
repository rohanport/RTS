package com.mygdx.game.skirmish.units;

import com.mygdx.game.Resources;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class Soldier1 extends UnitBase {

    public Soldier1(int x, int y) {
        super(x, y, 1);

        hp = 10;
        baseSpeed = 100;

        sprite = Resources.getInstance().soldier1;
    }

    @Override
    public boolean processKeyStroke(int keycode) {
        return false;
    }

    @Override
    public boolean processRightClick(int screenX, int screenY) {
        destNodeX = screenX;
        destNodeY = screenY;
        state = UnitState.MOVING;

        return false;
    }
}
