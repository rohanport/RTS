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

    public static final int MAP_HEIGHT = 160;
    public static final int MAP_WIDTH  = 160;

    public static final int TILED_MAP_WIDTH = 40;
    public static final int TILED_MAP_HEIGHT = 40;

    public static final int TILE_WIDTH_PX = 32;
    public static final int TILE_HEIGHT_PX = 32;

    /**
     * Returns the node coordinates of a given point on the screen
     * @param cam
     * @param screenX
     * @param screenY
     * @return
     */
    public static Vector2 screenCoords2NodeCoords(Camera cam, float screenX, float screenY) {
        Vector3 worldCordsInPix = new Vector3();
        Ray pickRay = cam.getPickRay(screenX, screenY);
        Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0, 0, 1), 0), worldCordsInPix);
        return new Vector2(
                Math.min(Math.max(0f, worldCordsInPix.x / NODE_WIDTH_PX), MAP_WIDTH - 1),
                Math.min(Math.max(0f, worldCordsInPix.y / NODE_HEIGHT_PX), MAP_HEIGHT - 1)
        );
    }

    /**
     * Returns the map pixel coordinates of a given point on the screen
     * @param cam
     * @param screenX
     * @param screenY
     * @return
     */
    public static Vector2 screenCoords2PxCoords(Camera cam, float screenX, float screenY) {
        Vector3 worldCordsInPix = new Vector3();
        Ray pickRay = cam.getPickRay(screenX, screenY);
        Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0, 0, 1), 0), worldCordsInPix);
        return new Vector2(
                Math.min(Math.max(0f, worldCordsInPix.x), (MAP_WIDTH - 1) * NODE_WIDTH_PX),
                Math.min(Math.max(0f, worldCordsInPix.y), (MAP_HEIGHT - 1) * NODE_HEIGHT_PX)
        );
    }

    /**
     * Returns the map pixel coordinates of a given point on the screen
     * Unsafe because it does not ensure that the returned coordinates are within the bounds of of the map
     * @param cam
     * @param screenX
     * @param screenY
     * @return
     */
    public static Vector2 screenCoords2PxCoordsUnSafe(Camera cam, float screenX, float screenY) {
        Vector3 worldCordsInPix = new Vector3();
        Ray pickRay = cam.getPickRay(screenX, screenY);
        Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0, 0, 1), 0), worldCordsInPix);
        return new Vector2(worldCordsInPix.x, worldCordsInPix.y);
    }

    /**
     * Returns the pixel coordinates of a given pixel coordinates from a tile map
     * @param x
     * @param y
     * @return
     */
    public static Vector2 tilePxCoords2PxCoords(float x, float y) {
        return new Vector2(
                x * (MAP_WIDTH * NODE_WIDTH_PX) / (float) (TILED_MAP_WIDTH * TILE_WIDTH_PX),
                y * (MAP_HEIGHT * NODE_HEIGHT_PX) / (float) (TILED_MAP_HEIGHT * TILE_HEIGHT_PX)
        );
    }

    /**
     * Returns the node coordinates of a given pixel coordinates from a tile map
     * @param x
     * @param y
     * @return
     */
    public static Vector2 tilePxCoords2NodeCoords(float x, float y) {
        return new Vector2(
                Math.round(x * (MAP_WIDTH) / (float) (TILED_MAP_WIDTH * TILE_WIDTH_PX)),
                Math.round(y * (MAP_HEIGHT) / (float) (TILED_MAP_HEIGHT * TILE_HEIGHT_PX))
        );
    }

    public static int nodesPerTileHorizontal() {
//        return NODE_WIDTH_PX * MAP_WIDTH / (TILE_WIDTH_PX * TILED_MAP_WIDTH);
        return 4;
    }

    public static int nodesPerTileVertical() {
//        return NODE_HEIGHT_PX * MAP_HEIGHT / (TILE_HEIGHT_PX * TILED_MAP_HEIGHT);
        return 4;
    }
}
