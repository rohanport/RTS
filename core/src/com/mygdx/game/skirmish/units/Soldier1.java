package com.mygdx.game.skirmish.units;

import com.mygdx.game.Resources;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class Soldier1 extends UnitBase {

    public Soldier1(int x, int y) {
        super(x, y, 1);

        hp = 10f;
        curHp = hp;

        atk = 10f;
        range = 6;

        baseSpeed = 100f;

        baseAtkStartup = 0.7f;
        baseAtkEnd = 0.3f;

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
