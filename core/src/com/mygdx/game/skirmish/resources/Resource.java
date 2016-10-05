package com.mygdx.game.skirmish.resources;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class Resource implements com.mygdx.game.skirmish.gameobjects.GameObject {

    public final ResourceType type;
    public final int totalAmount;
    public int amountRemaining;
    public Rectangle rect;
    public int size;
    private int id;

    public Resource(ResourceType type, int totalAmount, int x, int y, int size) {
        this.type = type;
        this.totalAmount = totalAmount;
        this.amountRemaining = totalAmount;
        this.size = size;

        this.rect = new Rectangle(
                x * MapUtils.NODE_WIDTH_PX - size * MapUtils.NODE_WIDTH_PX / 2,
                y * MapUtils.NODE_HEIGHT_PX - size * MapUtils.NODE_HEIGHT_PX / 2,
                size * MapUtils.NODE_WIDTH_PX,
                size * MapUtils.NODE_HEIGHT_PX
        );
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getPlayerID() {
        return -1;
    }

    @Override
    public float getCenterX() {
        return rect.x + rect.getWidth() / 2;
    }

    @Override
    public float getCenterY() {
        return rect.y + rect.getHeight() / 2;
    }

    @Override
    public int getMapCenterX() {
        return Math.round(getCenterX() / MapUtils.NODE_WIDTH_PX);
    }

    @Override
    public int getMapCenterY() {
        return Math.round(getCenterY() / MapUtils.NODE_HEIGHT_PX);
    }

    @Override
    public GameObjectType getGameObjectType() {
        return GameObjectType.RESOURCE;
    }

    @Override
    public void applyDamage(float damage) {
        //Do nothing
    }

    @Override
    public boolean isToBeDestroyed() {
        return amountRemaining <= 0;
    }
}
