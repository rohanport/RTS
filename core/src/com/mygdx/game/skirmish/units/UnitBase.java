package com.mygdx.game.skirmish.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.mygdx.game.skirmish.gameplay.Commandable;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public abstract class UnitBase implements Commandable {

    public Circle circle;
    protected float size;
    protected float hp;
    protected float atk;
    protected int baseSpeed;
    protected float speedMulti = 1;
    protected float baseAtkSpeed;
    protected float baseAtkSpeedMulti;

    protected boolean isMoving;
    protected float destX;
    protected float destY;


    protected Sprite sprite;

    public void move(float delta) {
        if (isMoving) {
            float transX = Math.min(Math.abs(baseSpeed * speedMulti * delta), Math.abs(destX - circle.x)) * Math.signum(destX - circle.x);
            float transY = Math.min(Math.abs(baseSpeed * speedMulti * delta), Math.abs(destY - circle.y)) * Math.signum(destY - circle.y);
            circle.setX(circle.x + transX);
            circle.setY(circle.y + transY);
            if (destX == circle.x && destY == circle.y) {
                isMoving = false;
            }
        }
    }

    public void update(float delta) {
        move(delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, circle.x - sprite.getWidth()/2, circle.y - sprite.getHeight()/2);
    }
}
