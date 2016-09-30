package com.mygdx.game.skirmish.gameplay;

/**
 * Created by paddlefish on 30-Sep-16.
 */
public interface TimeDelayedOperation {
    float getTotalTime();
    void passTime(float time);
    float getRemainingTime();
    void performOperation();
}
