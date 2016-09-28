package com.mygdx.game.skirmish.gameplay.combat;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameplay.GameObject;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.units.UnitState;

import java.util.List;

/**
 * Created by paddlefish on 28-Sep-16.
 */
public class CombatManager {

    private final World world;

    public CombatManager(World world) {
        this.world = world;
    }

    public void handleUnitAttacks(float delta, List<UnitBase> units) {
        for (UnitBase unit : units) {
            switch (unit.state) {
                case ATK_STARTING:
                    handleUnitAtkStarting(delta, unit);
                    break;
                case ATK_ENDING:
                    handleUnitAtkEnding(delta, unit);
                    break;
            }
        }
    }

    private void handleUnitAtkStarting(float delta, UnitBase unit) {
        unit.curAtkStartup += delta;
        if (unit.curAtkStartup > unit.getTotalAtkStartup()) {
            GameObject atkTarget = world.getGameObjectManager().getGameObjectByID(unit.getAtkTargetID());
            if (atkTarget != null) {
                atkTarget.applyDamage(unit.atk);
            }

            unit.curAtkStartup = 0f;
            unit.curAtkEnd = 0f;
            unit.state = UnitState.ATK_ENDING;
        }
    }

    private void handleUnitAtkEnding(float delta, UnitBase unit) {
        unit.curAtkEnd += delta;
        if (unit.curAtkEnd > unit.getTotalAtkEnd()) {
            unit.curAtkEnd = 0f;
            unit.state = UnitState.NONE;
        }
    }
}
