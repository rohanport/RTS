package com.mygdx.game.skirmish.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class MapUtils {
    public static final int NODE_WIDTH_PX  = 16;
    public static final int NODE_HEIGHT_PX = 16;

    public static final int MAP_HEIGHT = 120;
    public static final int MAP_WIDTH  = 120;

    public static Vector2 screenCoords2NodeCoords(Camera cam, float screenX, float screenY) {
        Vector3 worldCordsInPix = new Vector3();
        Ray pickRay = cam.getPickRay(screenX, screenY);
        Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0, 0, 1), 0), worldCordsInPix);
        return new Vector2(
                Math.min(Math.max(0f, worldCordsInPix.x / NODE_WIDTH_PX), MAP_WIDTH - 1),
                Math.min(Math.max(0f, worldCordsInPix.y / NODE_HEIGHT_PX), MAP_HEIGHT - 1)
        );
    }

    public static Vector2 screenCoords2MapCoords(Camera cam, float screenX, float screenY) {
        Vector3 worldCordsInPix = new Vector3();
        Ray pickRay = cam.getPickRay(screenX, screenY);
        Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0, 0, 1), 0), worldCordsInPix);
        return new Vector2(
                Math.min(Math.max(0f, worldCordsInPix.x), (MAP_WIDTH - 1) * NODE_WIDTH_PX),
                Math.min(Math.max(0f, worldCordsInPix.y), (MAP_HEIGHT - 1) * NODE_HEIGHT_PX)
        );
    }
}
