package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameplay.ProductionTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 03-Oct-16.
 */
public class ProductionHandler {

    private final World world;

    public ProductionHandler(World world) {
        this.world = world;
    }

    /**
     * Updates the remaining time of productions and runs them if they have completed
     * @param delta
     * @param productions
     */
    public void handleRunningProductions(float delta, List<? extends ProductionTask> productions) {
        List<ProductionTask> completedProductions = new ArrayList<>();

        for (ProductionTask production : productions) {
            if (production.getRemainingTime() <= 0) {
                production.performOperation();
                completedProductions.add(production);
            } else {
                production.passTime(delta);
            }
        }

        world.getProductionManager().removeAll(completedProductions);
    }
}
