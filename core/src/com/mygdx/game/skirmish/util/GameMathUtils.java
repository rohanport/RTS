package com.mygdx.game.skirmish.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

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

        if (Intersector.intersectSegmentCircle(quadA, quadB, circleCenter, circleRadSqr)) {
            return true;
        } if (Intersector.intersectSegmentCircle(quadB, quadC, circleCenter, circleRadSqr)) {
            return true;
        } if (Intersector.intersectSegmentCircle(quadC, quadD, circleCenter, circleRadSqr)) {
            return true;
        } if (Intersector.intersectSegmentCircle(quadD, quadA, circleCenter, circleRadSqr)) {
            return true;
        }

        return false;
    }
}
