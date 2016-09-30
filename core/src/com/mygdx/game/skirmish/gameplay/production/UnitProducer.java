package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.gameplay.TimeDelayedOperation;

/**
 * Created by paddlefish on 30-Sep-16.
 */
public class UnitProducer implements TimeDelayedOperation {

    private final float totalDuration;
    private float timeRemaining;
    private final Runnable unitProducer;

    public UnitProducer(Runnable unitProducer, float totalDuration) {
        this.totalDuration = totalDuration;
        this.unitProducer = unitProducer;
    }

    @Override
    public float getTotalTime() {
        return totalDuration;
    }

    @Override
    public void passTime(float time) {
        timeRemaining -= time;
    }

    @Override
    public float getRemainingTime() {
        return timeRemaining;
    }

    @Override
    public void performOperation() {

    }
}
