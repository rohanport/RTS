package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.List;

/**
 * Created by paddlefish on 06-Oct-16.
 */
public class SelectionUtils {

    public static Vector2 getDiffFromSelectionCenterAndClick(int mapX, int mapY, List<Commandable> moveables) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Commandable moveable : moveables) {
            if (moveable.getNodeX() < minX) {
                minX = moveable.getNodeX();
            }

            if (moveable.getNodeX() > maxX) {
                maxX = moveable.getNodeX();
            }

            if (moveable.getNodeY() < minY) {
                minY = moveable.getNodeY();
            }

            if (moveable.getNodeY() > maxY) {
                maxY = moveable.getNodeY();
            }
        }
        if (minX <= mapX &&
                mapX <= maxX &&
                minY <= mapY &&
                mapY <= maxY) {
            return null;
        }

        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;
        int diffX = mapX - centerX;
        int diffY = mapY - centerY;

        return new Vector2(diffX, diffY);
    }

    public static Polygon getSelectingPolygon(Camera cam, Rectangle selector) {
        Vector2 mapA = MapUtils.screenCoords2PxCoords(cam, selector.getX(), selector.getY());
        Vector2 mapB = MapUtils.screenCoords2PxCoords(cam, selector.getX() + selector.getWidth(), selector.getY());
        Vector2 mapC = MapUtils.screenCoords2PxCoords(cam, selector.getX() + selector.getWidth(), selector.getY() + selector.getHeight());
        Vector2 mapD = MapUtils.screenCoords2PxCoords(cam, selector.getX(), selector.getY() + selector.getHeight());

        return new Polygon(new float[]{mapA.x, mapA.y,
                mapB.x, mapB.y,
                mapC.x, mapC.y,
                mapD.x, mapD.y
        });
    }
}
