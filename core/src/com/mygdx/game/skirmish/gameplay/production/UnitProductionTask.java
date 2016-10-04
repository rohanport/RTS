package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.gameplay.ProductionTask;

/**
 * Created by paddlefish on 30-Sep-16.
 */
public class UnitProductionTask implements ProductionTask {

    private final QueueingProducer productionSource;
    private final float totalDuration;
    private float timeRemaining;
    private final Runnable unitProducer;

    public UnitProductionTask(QueueingProducer productionSource, Runnable unitProducer, float totalDuration) {
        this.productionSource = productionSource;
        this.totalDuration = totalDuration;
        this.timeRemaining = totalDuration;
        this.unitProducer = unitProducer;
    }

    @Override
    public boolean isBeingProduced() {
        return productionSource.getFirstInQueue() == this;
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
        unitProducer.run();
        productionSource.removeFromProductionQueue(this);
    }
}
