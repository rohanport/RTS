package com.mygdx.game.skirmish.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public abstract class UnitBase implements Commandable {

    public Circle circle;
    public int mapX;
    public int mapY;
    public float size;
    public float hp;
    public float atk;
    public int baseSpeed;
    public float speedMulti = 1;
    public float baseAtkSpeed;
    public float baseAtkSpeedMulti;

    public UnitState state;
    public int destNodeX;
    public int destNodeY;


    protected Sprite sprite;

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
}
