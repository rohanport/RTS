package com.mygdx.game.skirmish.gameobjects.buildings;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public class Building1 extends BuildingBase {

    public Building1(int x, int y) {
        super(x, y, 3);

        hp = 100f;
        curHp = hp;
    }

    @Override
    public Sprite getPortrait() {
        return null;
    }
}
