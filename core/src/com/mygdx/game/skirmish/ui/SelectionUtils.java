package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.gameplay.Commandable;

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
            if (moveable.getMapCenterX() < minX) {
                minX = moveable.getMapCenterX();
            }

            if (moveable.getMapCenterX() > maxX) {
                maxX = moveable.getMapCenterX();
            }

            if (moveable.getMapCenterY() < minY) {
                minY = moveable.getMapCenterY();
            }

            if (moveable.getMapCenterY() > maxY) {
                maxY = moveable.getMapCenterY();
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
}
