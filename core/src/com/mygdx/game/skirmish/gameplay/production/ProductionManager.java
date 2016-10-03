package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameplay.TimeDelayedOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by paddlefish on 03-Oct-16.
 */
public class ProductionManager {

    private final SkirmishScreen screen;
    private final List<TimeDelayedOperation> productions;

    //------------- Getters and Setters -----------
    public List<TimeDelayedOperation> getProductions() {
        return productions;
    }
    //------------------------------------------

    public ProductionManager(SkirmishScreen screen) {
        this.screen = screen;
        productions = new ArrayList<>();
    }

    public void add(TimeDelayedOperation production) {
        productions.add(production);
    }

    public void removeAll(Collection<TimeDelayedOperation> toRemove) {
        productions.removeAll(toRemove);
    }
}
