package com.mygdx.game.skirmish.gameobjects;

/**
 * Created by paddlefish on 06-Oct-16.
 */
public interface Attacker {
    int getAtkTargetID();
    float getAtk();
    int getRange();
    float getTotalAtkStartup();
    float getTotalAtkEnd();
    float getCurAtkStartup();
    void setCurAtkStartup(float curAtkStartup);
    float getCurAtkEnd();
    void setCurAtkEnd(float curAtkEnd);

    void startMovingToAttack();
    void startAttacking();
    void startEndOfAttack();
    void stopAttacking();

    float getCenterX();
    float getCenterY();

    boolean canMove();
}
