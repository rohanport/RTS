package com.mygdx.game.skirmish.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    public boolean isMoving;
    public int destNodeX;
    public int destNodeY;


    protected Sprite sprite;

    public UnitBase(int mapX, int mapY) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.destNodeX = mapX;
        this.destNodeY = mapY;

        circle = new Circle();
        circle.setX(mapX * MapUtils.NODE_WIDTH_PX);
        circle.setY(mapY * MapUtils.NODE_HEIGHT_PX);
    }

    public void move(float delta) {
        if (isMoving) {
            float transX = Math.min(Math.abs(baseSpeed * speedMulti * delta), Math.abs(destNodeX * MapUtils.NODE_WIDTH_PX - circle.x)) * Math.signum(destNodeX * MapUtils.NODE_WIDTH_PX - circle.x);
            float transY = Math.min(Math.abs(baseSpeed * speedMulti * delta), Math.abs(destNodeY * MapUtils.NODE_HEIGHT_PX - circle.y)) * Math.signum(destNodeY * MapUtils.NODE_HEIGHT_PX - circle.y);

            circle.setX(circle.x + transX);
            circle.setY(circle.y + transY);
            if (destNodeX == circle.x && destNodeY == circle.y) {
                isMoving = false;
            }
        }
    }

    public void translate(Vector2 translation) {
        circle.setX(circle.x + translation.x);
        circle.setY(circle.y + translation.y);
    }

    public void update(float delta) {
        move(delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, circle.x - sprite.getWidth()/2, circle.y - sprite.getHeight()/2);
    }
}
