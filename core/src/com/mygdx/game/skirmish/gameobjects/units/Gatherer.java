package com.mygdx.game.skirmish.gameobjects.units;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public interface Gatherer {
    int getMaxCarryFood();
    int getCurrentFood();
    void setCurrentFood(int currentFood);
    float getTotalGatherDuration();
    float getCurGatherTime();
    void setCurGatherTime(float curGatherTime);

    int getNodeX();
    int getNodeY();

    int getGatherSourceID();
    int getDropOffTargetID();

    void startGathering();
    void stopGathering();

    void startDropOff();
    void performDropOff();
}
