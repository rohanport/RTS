package com.mygdx.game.skirmish.gameplay.gathering;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.units.Gatherer;
import com.mygdx.game.skirmish.resources.Resource;

import java.util.List;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class GatheringHandler {

    private final World world;

    public GatheringHandler(World world) {
        this.world = world;
    }

    public void handleGatherers(float delta, List<? extends Gatherer> gatherers) {
        for (Gatherer gatherer : gatherers) {
            if (gatherer.getCurrentFood() >= gatherer.getMaxCarryFood()) {
                gatherer.startDropOff();
                continue;
            }

            Resource resource = (Resource) world.getGameObjectCache().getGameObjectByID(gatherer.getGatherSourceID());
            if (resource == null) {
                // The resource no longer exists
                gatherer.stopGathering();
                continue;
            }

            gatherer.setCurGatherTime(gatherer.getCurGatherTime() + delta);

            if (gatherer.getCurGatherTime() >= gatherer.getTotalGatherDuration()) {
                // The gather has mined 1 resource
                resource.amountRemaining -= 1;
                gatherer.setCurrentFood(gatherer.getCurrentFood() + 1);
                gatherer.setCurGatherTime(gatherer.getCurGatherTime() - gatherer.getTotalGatherDuration());
            }
        }
    }
}
