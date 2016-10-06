package com.mygdx.game.skirmish.gameobjects.units;

/**
 * Created by paddlefish on 23-Sep-16.
 */
public enum UnitState {
    ATK_STARTING,
    ATK_ENDING,

    BUILDING,

    GATHERING,

    ATTACK_MOVING,
    MOVING_TO_ATK,
    MOVING_TO_BUILD,
    MOVING_TO_GATHER,
    MOVING_TO_RETURN_RESOURCES,
    MOVING,
    NONE
}
