package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.units.Soldier1;
import com.mygdx.game.skirmish.gameobjects.units.UnitType;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;

/**
 * Created by paddlefish on 30-Sep-16.
 */
public class UnitProducerSupplier {
    private final World world;

    public UnitProducerSupplier(World world) {
        this.world = world;
    }

    public Runnable getUnitProducerFor(UnitType unitType, int x, int y) {
        GroundGraph graph = world.getGroundGraph();
        GroundNode sourceNode = graph.getNodeByCoords(x, y);
        switch (unitType) {
            case SOLDIER1:
                return () -> new Soldier1(graph.getClosestFreeNode(sourceNode).x, graph.getClosestFreeNode(sourceNode).y);
            default:
                throw new RuntimeException("Attempting to build unknown unit type " + unitType);
        }
    }
}
