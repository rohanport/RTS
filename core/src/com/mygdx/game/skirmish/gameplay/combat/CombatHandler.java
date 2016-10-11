package com.mygdx.game.skirmish.gameplay.combat;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.Attacker;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.util.GameMathUtils;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.List;

/**
 * Created by paddlefish on 28-Sep-16.
 */
public class CombatHandler {

    private final World world;

    public CombatHandler(World world) {
        this.world = world;
    }

    public void handleAtkStarting(float delta, List<? extends Attacker> attackers) {
        for (Attacker attacker : attackers) {
            attacker.setCurAtkStartup(attacker.getCurAtkStartup() + delta);
            if (attacker.getCurAtkStartup() > attacker.getTotalAtkStartup()) {
                // The attack is reading to launch
                GameObject atkTarget = world.getGameObjectCache().getGameObjectByID(attacker.getAtkTargetID());
                if (atkTarget != null) {
                    atkTarget.applyDamage(attacker.getAtk());
                }

                attacker.setCurAtkStartup(0f);
                attacker.setCurAtkEnd(-delta);
                attacker.startEndOfAttack();
            }
        }
    }

    public void handleAtkEnding(float delta, List<? extends Attacker> attackers) {
        for (Attacker attacker : attackers){
            attacker.setCurAtkEnd(attacker.getCurAtkEnd() + delta);
            if (attacker.getCurAtkEnd() > attacker.getTotalAtkEnd()) {
                GameObject atkTarget = world.getGameObjectCache().getGameObjectByID(attacker.getAtkTargetID());
                if (atkTarget == null) {
                    // Attack target has been destroyed, so stop attacking
                    attacker.stopAttacking();
                } else if (GameMathUtils.distBetween(
                            attacker.getCenterX() / MapUtils.NODE_WIDTH_PX,
                            attacker.getCenterY() / MapUtils.NODE_HEIGHT_PX,
                            atkTarget.getCenterX() / MapUtils.NODE_WIDTH_PX,
                            atkTarget.getCenterY() / MapUtils.NODE_HEIGHT_PX) < attacker.getRange()) {
                    // Attack target exists and is in range, so begin a new attack
                    attacker.startAttacking();
                } else if (attacker.canMove()) {
                    // Attacker can move and target is out of range, so begin moving towards it
                    attacker.startMovingToAttack();
                } else {
                    // Attacker cannot move and target is out of range, so stop attacking
                    attacker.stopAttacking();
                }

                attacker.setCurAtkEnd(0f);
            }
        }
    }
}
