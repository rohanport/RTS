package com.mygdx.game.skirmish.gameobjects.buildings;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.units.UnitType;
import com.mygdx.game.skirmish.gameplay.production.UnitProductionTask;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public class Building1 extends BuildingBase {

    public Building1(World world,int x, int y) {
        super(world, x, y, 3);

        hp = 100f;
        curHp = hp;
    }

    @Override
    public Sprite getPortrait() {
        return null;
    }

    @Override
    public boolean processKeyStroke(int keycode) {
        switch (keycode) {
            case Input.Keys.S:
                UnitProductionTask soldierBuildingTask = world.getUnitProductionTaskFactory()
                        .getUnitProducerFor(this, UnitType.SOLDIER1, getMapCenterX(), getMapCenterY(), 0.1f);
                addToProductionQueue(soldierBuildingTask);
                return true;
            default:
                return false;
        }
    }
}
