package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameplay.TimeDelayedOperation;

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

    public void handleProductions(float delta, List<TimeDelayedOperation> productions) {
        List<TimeDelayedOperation> completedProductions = new ArrayList<>();

        for (TimeDelayedOperation production : productions) {
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
