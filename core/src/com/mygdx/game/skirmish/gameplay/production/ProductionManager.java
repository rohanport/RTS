package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameplay.ProductionTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 03-Oct-16.
 */
public class ProductionManager {

    private final SkirmishScreen screen;
    private final List<ProductionTask> productions;

    //------------- Getters and Setters -----------
    public List<ProductionTask> getProductions() {
        return productions;
    }
    //------------------------------------------

    public ProductionManager(SkirmishScreen screen) {
        this.screen = screen;
        productions = new ArrayList<>();
    }

    public void add(ProductionTask production) {
        productions.add(production);
    }

    public void removeAll(Collection<ProductionTask> toRemove) {
        productions.removeAll(toRemove);
    }

    public boolean has(ProductionTask productionTask) {
        return productions.contains(productionTask);
    }

    public List<ProductionTask> getRunningProductionTasks() {
        return productions.stream()
                .filter(ProductionTask::isBeingProduced)
                .collect(Collectors.toList());
    }
}
