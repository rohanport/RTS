package com.mygdx.game.skirmish.gameplay.combat;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.units.UnitBase;
import com.mygdx.game.skirmish.gameobjects.units.UnitState;

import java.util.List;

/**
 * Created by paddlefish on 28-Sep-16.
 */
public class CombatHandler {

    private final World world;

    public CombatHandler(World world) {
        this.world = world;
    }

    public void handleAtkStarting(float delta, List<UnitBase> units) {
        for (UnitBase unit : units) {
            unit.curAtkStartup += delta;
            if (unit.curAtkStartup > unit.getTotalAtkStartup()) {
                GameObject atkTarget = world.getGameObjectCache().getGameObjectByID(unit.getAtkTargetID());
                if (atkTarget != null) {
                    atkTarget.applyDamage(unit.atk);
                }

                unit.curAtkStartup = 0f;
                unit.curAtkEnd = -delta;
                unit.state = UnitState.ATK_ENDING;
            }
        }
    }

    public void handleAtkEnding(float delta, List<UnitBase> units) {
        for (UnitBase unit : units){
            unit.curAtkEnd += delta;
            if (unit.curAtkEnd > unit.getTotalAtkEnd()) {
                GameObject atkTarget = world.getGameObjectCache().getGameObjectByID(unit.getAtkTargetID());
                unit.state = atkTarget != null ? UnitState.MOVING_TO_ATK : UnitState.NONE;
                unit.curAtkEnd = 0f;
            }
        }
    }
}
