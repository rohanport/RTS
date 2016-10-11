package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.units.Soldier1;
import com.mygdx.game.skirmish.gameobjects.units.UnitBase;
import com.mygdx.game.skirmish.gameobjects.units.UnitType;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;

/**
 * Created by paddlefish on 30-Sep-16.
 *
 * Creates ProductionTasks for building units
 */
public class UnitProductionTaskFactory {
    private final World world;

    public UnitProductionTaskFactory(World world) {
        this.world = world;
    }

    public UnitProductionTask getUnitProducerFor(QueueingProducer queueingProducer,
                                                 UnitType unitType,
                                                 int playerID,
                                                 int x,
                                                 int y,
                                                 float duration) {
        Runnable action = () -> {
            UnitBase unit = produceUnit(unitType, playerID, x, y);
            GameObject rallyObject = queueingProducer.getRallyObject();
            if (rallyObject != null) {
                //If the rally object still exists, the unit processes how to deal with it
                unit.processRightClickOn(false, rallyObject);
            } else if (queueingProducer.hasRallyPoint()) {
                //If a rally point has been set, move towards it
                unit.processMoveCommand(false, queueingProducer.getRallyX(), queueingProducer.getRallyY());
            }
        };

        return new UnitProductionTask(
                queueingProducer,
                action,
                duration
        );
    }

    private UnitBase produceUnit(UnitType unitType, int playerID, int x, int y) {
        GroundGraph graph = world.getGroundGraph();
        GroundNode sourceNode = graph.getNodeByCoords(x, y);
        GroundNode createAtNode = graph.getClosestFreeNode(sourceNode);

        UnitBase unit;
        switch (unitType) {
            case SOLDIER1:
                unit = new Soldier1(world, playerID, createAtNode.x, createAtNode.y);
                break;
            default:
                throw new RuntimeException("Attempting to build unknown unit type " + unitType);
        }

        world.getGameObjectCache().add(unit);
        return unit;
    }
}
