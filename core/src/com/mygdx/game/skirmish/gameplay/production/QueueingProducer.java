package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameplay.ProductionTask;

/**
 * Created by paddlefish on 03-Oct-16.
 *
 * Produces things one by one (eg. Units, buildings, upgrades) and stores them in a queue
 */
public interface QueueingProducer {
    ProductionTask getFirstInQueue();
    void addToProductionQueue(ProductionTask productionTask);
    void removeFromProductionQueue(ProductionTask productionTask);
    GameObject getRallyObject();
    boolean hasRallyPoint();
    int getRallyX();
    int getRallyY();
}
