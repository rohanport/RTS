package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.gameplay.ProductionTask;

/**
 * Created by paddlefish on 03-Oct-16.
 */
public interface QueueingProducer {
    void addToProductionQueue(ProductionTask productionTask);
    void removeFromProductionQueue(ProductionTask productionTask);
}
