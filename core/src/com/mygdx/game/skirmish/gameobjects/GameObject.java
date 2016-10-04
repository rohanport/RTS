package com.mygdx.game.skirmish.gameobjects;

/**
 * Created by paddlefish on 28-Sep-16.
 */
public interface GameObject {
    int getID();
    void setID(int id);
    int getPlayerID();

    float getCenterX();
    float getCenterY();

    GameObjectType getGameObjectType();

    void applyDamage(float damage);
    boolean isToBeDestroyed();
}
