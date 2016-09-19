package com.mygdx.game.skirmish.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.skirmish.gameplay.Commandable;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public abstract class UnitBase implements Commandable {

    protected int x;
    protected int y;
    protected float hp;
    protected float atk;
    protected int baseSpeed;
    protected float speedMulti = 1;
    protected float baseAtkSpeed;
    protected float baseAtkSpeedMulti;

    protected boolean isMoving;
    protected int destX;
    protected int destY;


    protected Sprite sprite;

    public void move(float delta) {
        if (isMoving) {
            x += Math.min(Math.abs(baseSpeed * speedMulti * delta), Math.abs(destX - x)) * Math.signum(destX - x);
            y += Math.min(Math.abs(baseSpeed * speedMulti * delta), Math.abs(destY - y)) * Math.signum(destY - y);
            if (destX == x && destY == y) {
                isMoving = false;
            }
        }
    }

    public void update(float delta) {
        move(delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, x, y);
    }
}
