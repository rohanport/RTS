package com.mygdx.game.skirmish.gameobjects.units;

import com.mygdx.game.skirmish.gameobjects.buildings.BuildingType;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public interface Builder {
    boolean isBuilding();
    boolean isMovingToBuild();

    int getBuildLocationX();
    int getBuildLocationY();

    BuildingType getBuildingType();

    int getMapCenterX();

    int getMapCenterY();

    void startBuilding();
}
