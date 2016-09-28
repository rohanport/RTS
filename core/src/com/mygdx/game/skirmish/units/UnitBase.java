package com.mygdx.game.skirmish.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.gameplay.GameObject;
import com.mygdx.game.skirmish.gameplay.GameObjectType;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public abstract class UnitBase implements Commandable, GameObject {

    public Circle circle;
    public int mapX;
    public int mapY;
    public float size;
    public float hp;
    public float curHp;
    public float atk;
    public float baseSpeed;
    public float speedMulti = 1f;
    protected float baseAtkStartup;
    protected float baseAtkEnd;
    public float curAtkStartup;
    public float curAtkEnd;
    public float baseAtkSpeedMulti = 1f;
    private int atkTargetID;

    public UnitState state;
    public int destNodeX;
    public int destNodeY;

    private int gameID;

    protected Sprite sprite;

    //----------- Getters and Setters ------------
    @Override
    public int getID() {
        return gameID;
    }
    @Override
    public void setID(int id) {
        gameID = id;
    }

    public float getTotalAtkStartup() {
        return baseAtkStartup / baseAtkSpeedMulti;
    }
    public float getTotalAtkEnd() {
        return baseAtkEnd / baseAtkSpeedMulti;
    }

    public int getAtkTargetID() {
        return atkTargetID;
    }

    //-----------------------------------------

    public UnitBase(int mapX, int mapY, int size) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.destNodeX = mapX;
        this.destNodeY = mapY;
        this.size = size;

        circle = new Circle();
        circle.setX(mapX * MapUtils.NODE_WIDTH_PX);
        circle.setY(mapY * MapUtils.NODE_HEIGHT_PX);
        circle.setRadius(size * MapUtils.NODE_WIDTH_PX / 2f);
    }

    public void translate(Vector2 translation) {
        circle.setX(circle.x + translation.x);
        circle.setY(circle.y + translation.y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, circle.x - sprite.getWidth()/2, circle.y - sprite.getHeight()/2);
    }

    @Override
    public GameObjectType getGameObjectType() {
        return GameObjectType.UNIT;
    }

    @Override
    public void renderSelectionMarker(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(circle.x, circle.y, circle.radius);
    }

    @Override
    public boolean isMoveable() {
        return true;
    }

    @Override
    public int getMapCenterX() {
        return Math.round(circle.x / MapUtils.NODE_WIDTH_PX);
    }

    @Override
    public int getMapCenterY() {
        return Math.round(circle.y / MapUtils.NODE_HEIGHT_PX);
    }

    @Override
    public void applyDamage(float damage) {
        curHp -= damage;
    }
}
