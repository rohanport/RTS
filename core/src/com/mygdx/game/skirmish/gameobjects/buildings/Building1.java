package com.mygdx.game.skirmish.gameobjects.buildings;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public class Building1 extends BuildingBase {

    public Building1(int x, int y) {
        super(x, y, 3);

        hp = 100f;
        curHp = hp;
    }
}
