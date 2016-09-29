package com.mygdx.game.skirmish.buildings;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.gameplay.GameObject;
import com.mygdx.game.skirmish.gameplay.GameObjectType;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public abstract class BuildingBase implements Commandable, GameObject {

    public Rectangle rect;
    public float hp;
    public float curHp;
    public int size;

    private int gameID;

    protected Sprite sprite;

    //----------- Getters and Setters ---------------
    @Override
    public int getID() {
        return gameID;
    }
    @Override
    public void setID(int id) {
        gameID = id;
    }

    @Override
    public float getCenterX() {
        return rect.x + rect.getWidth() / 2;
    }

    @Override
    public float getCenterY() {
        return rect.y + rect.getHeight() / 2;
    }

    //--------------------------------------------

    public BuildingBase(int x, int y, int size) {
        this.size = size;

        this.rect = new Rectangle(
                x * MapUtils.NODE_WIDTH_PX - MapUtils.NODE_WIDTH_PX / 2,
                y * MapUtils.NODE_HEIGHT_PX - MapUtils.NODE_HEIGHT_PX / 2,
                size * MapUtils.NODE_WIDTH_PX,
                size * MapUtils.NODE_HEIGHT_PX
        );
    }

    @Override
    public GameObjectType getGameObjectType() {
        return GameObjectType.BUILDING;
    }

    @Override
    public boolean processKeyStroke(int keycode) {
        return false;
    }

    @Override
    public boolean processRightClick(int screenX, int screenY) {
        return false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, rect.x - sprite.getWidth()/2, rect.y - sprite.getHeight()/2);
    }

    @Override
    public void renderSelectionMarker(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(rect.x, rect.y, rect.getWidth(), rect.getHeight());
    }

    @Override
    public boolean isMoveable() {
        return false;
    }

    @Override
    public int getMapCenterX() {
        return 0;
    }

    @Override
    public int getMapCenterY() {
        return 0;
    }

    @Override
    public void applyDamage(float damage) {
        curHp -= damage;
    }

    @Override
    public boolean processAtkCommand(int targetID) {
        return false;
    }

    @Override
    public boolean isToBeDestroyed() {
        return curHp <= 0;
    }
}
