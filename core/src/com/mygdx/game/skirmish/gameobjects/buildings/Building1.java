package com.mygdx.game.skirmish.gameobjects.buildings;

import com.badlogic.gdx.Input;
import com.mygdx.game.GameData;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.units.UnitType;
import com.mygdx.game.skirmish.gameplay.production.TransactionHandler;
import com.mygdx.game.skirmish.gameplay.production.UnitProductionTask;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public class Building1 extends BuildingBase {
    public static final int SIZE = 3;
    public static final float BUILD_DURATION = 2f;
    public static final float HP = 100f;

    public Building1(World world, int playerID, int x, int y) {
        super(world, playerID, x, y, SIZE);

        hp = HP;
        curHp = hp;
        portrait = GameData.getInstance().building1Portrait;
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
        Runnable action = () -> {
            UnitProductionTask soldierBuildingTask = world.getUnitProductionTaskFactory()
                    .getUnitProducerFor(this, UnitType.SOLDIER1, getPlayerID(), getNodeX(), getNodeY(), 1.0f);
            addToProductionQueue(soldierBuildingTask);
        };

        TransactionHandler transactionHandler = world.getTransactionHandler();
        transactionHandler.processTransaction(
                getPlayerID(),
                transactionHandler.BUILDING1_SOLDIER1,
                action
        );

        return false;
    }

    @Override
    public boolean isResourceDropOff() {
        return true;
    }
}
