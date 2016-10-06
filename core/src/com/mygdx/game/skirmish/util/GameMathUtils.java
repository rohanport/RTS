package com.mygdx.game.skirmish.util;

import com.badlogic.gdx.math.*;
import com.mygdx.game.skirmish.gameobjects.GameObject;

import java.util.List;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class GameMathUtils {

    public static boolean isCircleIntersectQuadrilateral(Circle circle, Polygon quadrilateral) {
        if (quadrilateral.contains(circle.x, circle.y)) {
            return true;
        }

        Vector2 quadA = new Vector2(quadrilateral.getVertices()[0], quadrilateral.getVertices()[1]);
        Vector2 quadB = new Vector2(quadrilateral.getVertices()[2], quadrilateral.getVertices()[3]);
        Vector2 quadC = new Vector2(quadrilateral.getVertices()[4], quadrilateral.getVertices()[5]);
        Vector2 quadD = new Vector2(quadrilateral.getVertices()[6], quadrilateral.getVertices()[7]);

        Vector2 circleCenter = new Vector2(circle.x, circle.y);
        float circleRadSqr = circle.radius * circle.radius;

        return Intersector.intersectSegmentCircle(quadA, quadB, circleCenter, circleRadSqr) ||
                Intersector.intersectSegmentCircle(quadB, quadC, circleCenter, circleRadSqr) ||
                Intersector.intersectSegmentCircle(quadC, quadD, circleCenter, circleRadSqr) ||
                Intersector.intersectSegmentCircle(quadD, quadA, circleCenter, circleRadSqr);

    }

    public static boolean isRectangleIntersectQuadrilateral(Rectangle rect, Polygon quadrilateral) {
        Vector2 rectA = new Vector2(rect.x, rect.y);
        Vector2 rectB = new Vector2(rect.x + rect.getWidth(), rect.y);
        Vector2 rectC = new Vector2(rect.x + rect.getWidth(), rect.y + rect.getHeight());
        Vector2 rectD = new Vector2(rect.x, rect.y + rect.getHeight());
        Vector2 quadA = new Vector2(quadrilateral.getVertices()[0], quadrilateral.getVertices()[1]);
        Vector2 quadB = new Vector2(quadrilateral.getVertices()[2], quadrilateral.getVertices()[3]);
        Vector2 quadC = new Vector2(quadrilateral.getVertices()[4], quadrilateral.getVertices()[5]);
        Vector2 quadD = new Vector2(quadrilateral.getVertices()[6], quadrilateral.getVertices()[7]);

        return rect.contains(quadA) ||
                rect.contains(quadB) ||
                rect.contains(quadC) ||
                rect.contains(quadD) ||
                quadrilateral.contains(rectA) ||
                quadrilateral.contains(rectB) ||
                quadrilateral.contains(rectC) ||
                quadrilateral.contains(rectD) ||
                Intersector.intersectSegmentPolygon(rectA, rectB, quadrilateral) ||
                Intersector.intersectSegmentPolygon(rectB, rectC, quadrilateral) ||
                Intersector.intersectSegmentPolygon(rectC, rectD, quadrilateral) ||
                Intersector.intersectSegmentPolygon(rectD, rectA, quadrilateral);

    }

    public static float distBetween(float x1, float y1, float x2, float y2) {
        return ((float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
    }

    public static void sortListByDistFrom(GameObject gameObject, List<? extends GameObject> gameObjectsToSort) {
        gameObjectsToSort.sort((o1, o2) ->
                Math.round(Math.signum(
                        GameMathUtils.distBetween(gameObject.getCenterX(), gameObject.getCenterY(), o1.getCenterX(), o1.getCenterY()) -
                                GameMathUtils.distBetween(gameObject.getCenterX(), gameObject.getCenterY(), o2.getCenterX(), o2.getCenterY())
                ))
        );
    }
}
