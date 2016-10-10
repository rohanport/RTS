package com.mygdx.game.skirmish.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class MapCamera extends OrthographicCamera {
    private final int mapWidth;
    private final int mapHeight;

    private static final float MAX_X_SPEED = 300f;
    private static final float MAX_Y_SPEED = 300f;
    private static final float X_ACCELERATION = 400f;
    private static final float Y_ACCELERATION = 400f;

    private float xSpeed;
    private float ySpeed;

    private boolean isMovingRight;
    private boolean isMovingLeft;
    private boolean isMovingUp;
    private boolean isMovingDown;

    public boolean isMovingRight() {
        return isMovingRight;
    }
    public void setMovingRight(boolean movingRight) {
        isMovingRight = movingRight;
    }
    public boolean isMovingLeft() {
        return isMovingLeft;
    }
    public void setMovingLeft(boolean movingLeft) {
        isMovingLeft = movingLeft;
    }
    public boolean isMovingUp() {
        return isMovingUp;
    }
    public void setMovingUp(boolean movingUp) {
        isMovingUp = movingUp;
    }
    public boolean isMovingDown() {
        return isMovingDown;
    }
    public void setMovingDown(boolean movingDown) {
        isMovingDown = movingDown;
    }

    public MapCamera(float viewportWidth, float viewportHeight, int mapWidth, int mapHeight) {
        super(viewportWidth, viewportHeight);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        translate(mapWidth / 2f, mapHeight / 2f
        );
    }

    public void update(float delta) {
        translate(calculateTranslation(delta));

        super.update();
    }

    private Vector3 calculateTranslation(float delta) {
        Vector3 translation = new Vector3(0, 0, 0);

        if (isMovingUp() || isMovingDown()) {
            translation.add((new Vector3(-0.5f, 0.5f, 0)).scl(calculateCurrentYSpeed(delta)));
        }

        if (isMovingLeft() || isMovingRight()) {
            translation.add((new Vector3(0.5f, 0.5f, 0)).scl(calculateCurrentXSpeed(delta)));
        }

        return translation;
    }


    private float calculateCurrentXSpeed(float delta) {
        float currSpeed = 0;
        if (isMovingLeft()) {
            currSpeed = (Math.abs(xSpeed) < MAX_X_SPEED) ? Math.max(xSpeed - (delta * X_ACCELERATION), -MAX_X_SPEED) :
                    -MAX_X_SPEED;
        } else if (isMovingRight()) {
            currSpeed = (Math.abs(xSpeed) < MAX_X_SPEED) ? Math.min(xSpeed + (delta * X_ACCELERATION), MAX_X_SPEED) :
                    MAX_X_SPEED;
        }

        return currSpeed;
    }

    private float calculateCurrentYSpeed(float delta) {
        float currSpeed = 0;
        if (isMovingUp()) {
            currSpeed = (Math.abs(ySpeed) < MAX_Y_SPEED) ? Math.min(ySpeed + (delta * Y_ACCELERATION), MAX_Y_SPEED) :
                    MAX_Y_SPEED;
        } else if (isMovingDown()) {
            currSpeed = (Math.abs(ySpeed) < MAX_Y_SPEED) ? Math.max(ySpeed - (delta * Y_ACCELERATION), -MAX_Y_SPEED) :
                    -MAX_Y_SPEED;
        }

        return currSpeed;
    }

    private float calculateTransX(float delta) {
        xSpeed = calculateCurrentXSpeed(delta);
        float transX = xSpeed * delta;
        float distFromMapRight = mapWidth - position.x;

        if (transX < 0) {
            transX = Math.abs(transX) < Math.abs(position.x) ? transX : transX;
        } else if (transX > 0) {
            transX = Math.abs(transX) < Math.abs(distFromMapRight) ? transX : transX;
        }

        return transX;
    }

    private float calculateTransY(float delta) {
        ySpeed = calculateCurrentYSpeed(delta);
        float transY = ySpeed * delta;
        float distFromMapTop = mapHeight - position.y;

        if (transY < 0) {
            transY = Math.abs(transY) < Math.abs(position.y) ? transY : transY;
        } else if (transY > 0) {
            transY = Math.abs(transY) < Math.abs(distFromMapTop) ? transY : transY;
        }

        return transY;
    }
}
