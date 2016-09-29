package com.mygdx.game.skirmish.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class MapUtils {
    public static final int NODE_WIDTH_PX  = 15;
    public static final int NODE_HEIGHT_PX = 15;

    public static final int MAP_HEIGHT = 1000;
    public static final int MAP_WIDTH  = 1000;

    public static Vector2 screenCoords2NodeCoords(Camera cam, float screenX, float screenY) {
        Vector3 worldCordsInPix = cam.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(
                Math.min(Math.max(0f, worldCordsInPix.x / NODE_WIDTH_PX), MAP_WIDTH),
                Math.min(Math.max(0f, worldCordsInPix.y / NODE_HEIGHT_PX), MAP_HEIGHT)
        );
    }

    public static Vector2 screenCoords2MapCoords(Camera cam, float screenX, float screenY) {
        Vector3 worldCordsInPix = cam.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(
                Math.min(Math.max(0f, worldCordsInPix.x), MAP_WIDTH * NODE_WIDTH_PX),
                Math.min(Math.max(0f, worldCordsInPix.y), MAP_HEIGHT * NODE_HEIGHT_PX)
        );
    }
}
