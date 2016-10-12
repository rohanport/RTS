package com.mygdx.game.skirmish.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class MapCamera extends OrthographicCamera {
    private final int mapWidth;
    private final int mapHeight;

    private static final int X_OFFSET = 1000;  // x distance from the point that the camera is looking at
    private static final int Y_OFFSET = -1000; // y distance from the point that the camera is looking at
    private static final int Z_OFFSET = 1000;  // z distance from the point that the camera is looking at

    private static final float MAX_X_SPEED = 10f;
    private static final float MAX_Y_SPEED = 10f;
    private static final float X_ACCELERATION = 40f;
    private static final float Y_ACCELERATION = 40f;

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

        //These operations set the camera into an isometric view
        float centerX = mapWidth / 2f;
        float centerY = mapHeight / 2f;

        position.set(
                centerX + X_OFFSET,
                centerY + Y_OFFSET,
                Z_OFFSET
        );
        lookAt(centerX, centerY, 0);
        rotate(-60, -1, 1, -1);
        near = 0;
        far = 10000;
    }

    public void update(float delta) {
        translate(calculateTranslation(delta));

        super.update();
    }

    private Vector2 calculateTranslation(float delta) {
        Vector2 translation = new Vector2(0, 0);

        if (isMovingUp() || isMovingDown()) {
            // Since the map is rotated, Up is the original top left corner
            ySpeed = calculateCurrentYSpeed(delta);
            translation.add((new Vector2(-0.5f, 0.5f)).scl(ySpeed));
        }

        if (isMovingLeft() || isMovingRight()) {
            // Since the map is rotated, Right is the original top right corner
            xSpeed = calculateCurrentXSpeed(delta);
            translation.add((new Vector2(0.5f, 0.5f)).scl(xSpeed));
        }

        return keepWithinBounds(translation);
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

    private Vector2 keepWithinBounds(Vector2 translation) {
        // Calculate the point on the map that is in the center of the screen
        Vector2 lookAtPoint = MapUtils.screenCoords2PxCoordsUnSafe(this, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);

        float distFromMapRight = mapWidth - lookAtPoint.x;
        if (translation.x < 0) {
            // If going left, ensure that it can't go below 0
            translation.x = Math.abs(translation.x) < Math.abs(lookAtPoint.x) ? translation.x : -lookAtPoint.x;
        } else if (translation.x > 0) {
            // If going right, ensure that it can't go below mapRight
            translation.x = Math.abs(translation.x) < Math.abs(distFromMapRight) ? translation.x : distFromMapRight;
        }

        float distFromMapTop = mapHeight - lookAtPoint.y;
        if (translation.y < 0) {
            // If going downwards, ensure it can't go below 0
            translation.y = Math.abs(translation.y) < Math.abs(lookAtPoint.y) ? translation.y : -lookAtPoint.y;
        } else if (translation.y > 0) {
            // If going up, ensure that it can't go below mapTop
            translation.y = Math.abs(translation.y) < Math.abs(distFromMapTop) ? translation.y : distFromMapTop;
        }

        return translation;
    }
}
