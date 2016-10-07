package com.mygdx.game.skirmish.gameobjects.buildings;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.units.Soldier1;
import com.mygdx.game.skirmish.gameobjects.units.UnitType;
import com.mygdx.game.skirmish.gameplay.production.UnitProductionTask;
import com.mygdx.game.skirmish.player.Player;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public class Building1 extends BuildingBase {
    public static final int SIZE = 3;
    public static final float BUILD_DURATION = 2f;
    public static final float HP = 100f;
    public static final int COST = 100;

    public Building1(World world, int playerID, int x, int y) {
        super(world, playerID, x, y, SIZE);

        hp = HP;
        curHp = hp;
    }

    @Override
    public Sprite getPortrait() {
        return null;
    }

    @Override
    public boolean processKeyStroke(boolean chain, int keycode) {
        switch (keycode) {
            case Input.Keys.S:
                return handleSoldierBuildKeyPressed();
            default:
                return false;
        }
    }

    private boolean handleSoldierBuildKeyPressed() {
        Player player = world.getPlayerManager().getPlayerByID(getPlayerID());
        if (player.food >= Soldier1.COST) {
            UnitProductionTask soldierBuildingTask = world.getUnitProductionTaskFactory()
                    .getUnitProducerFor(this, UnitType.SOLDIER1, getPlayerID(), getMapCenterX(), getMapCenterY(), 1.0f);
            addToProductionQueue(soldierBuildingTask);
            player.food -= Soldier1.COST;
        }

        return false;
    }

    @Override
    public boolean isResourceDropOff() {
        return true;
    }
}
