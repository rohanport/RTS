package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.units.Soldier1;
import com.mygdx.game.skirmish.gameobjects.units.UnitBase;
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
        return () -> produceUnit(unitType, x, y);
    }

    private void produceUnit(UnitType unitType, int x, int y) {
        GroundGraph graph = world.getGroundGraph();
        GroundNode sourceNode = graph.getNodeByCoords(x, y);

        UnitBase unit;
        switch (unitType) {
            case SOLDIER1:
                unit = new Soldier1(world, graph.getClosestFreeNode(sourceNode).x, graph.getClosestFreeNode(sourceNode).y);
                world.getGameObjectManager().add(unit);
                break;
            default:
                throw new RuntimeException("Attempting to build unknown unit type " + unitType);
        }
    }
}
