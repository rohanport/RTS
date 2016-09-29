package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.math.Circle;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 29-Sep-16.
 */
public class GroundGraphUtils {

    public static int getDistOfClosestFreeNode(GroundGraph graph, GroundNode destNode) {
        if (graph.nodeIsOpen(destNode)) {
            return 0;
        } else {
            int dist = 1;

            while (dist < Math.max(MapUtils.MAP_WIDTH, MapUtils.MAP_HEIGHT) / 2) {
                if (isFreeNodeAtDist(graph, destNode, dist)) {
                    return dist;
                }

                dist++;
            }

            return -1;
        }
    }

    public static int getDist(GroundNode src, GroundNode dest) {
        return Math.max(Math.abs(dest.x - src.x), Math.abs(dest.y - src.y));
    }

    public static boolean isFreeNodeAtDist(GroundGraph graph, GroundNode baseNode, int dist) {
        for (int i = -dist; i <= dist; i++) {
            for (int j = -dist; j <= dist; j++) {
                if (Math.abs(i) < dist && Math.abs(j) < dist) {
                    continue;
                }

                if (graph.nodeExists(baseNode.x + i, baseNode.y + j)) {
                    GroundNode node = graph.getNodeByCoords(baseNode.x + i, baseNode.y + j);
                    if (graph.nodeIsOpen(node)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static List<GroundNode> getFreeNodesAtDist(GroundGraph graph, GroundNode baseNode, int dist) {
        List<GroundNode> openNodesAtDist = new ArrayList<>();
        for (int i = -dist; i <= dist; i++) {
            for (int j = -dist; j <= dist; j++) {
                if (Math.abs(i) < dist && Math.abs(j) < dist) {
                    continue;
                }

                if (graph.nodeExists(baseNode.x + i, baseNode.y + j)) {
                    GroundNode node = graph.getNodeByCoords(baseNode.x + i, baseNode.y + j);
                    if (graph.nodeIsOpen(node)) {
                        openNodesAtDist.add(node);
                    }
                }
            }
        }

        return openNodesAtDist;
    }

    public static List<GroundNode> getFreeNodesAtDistEuclidean(GroundGraph graph, float destX, float destY, int radius) {
        List<GroundNode> openNodesAtDist = new ArrayList<>();
        Circle atkCircle = new Circle(destX, destY, radius - 0.5f);
        int i;
        int j;

        // 1st Quadrant
        i = radius;
        j = 0;
        while (i >= 0 && j <= radius) {
            while (atkCircle.contains(destX + i, destY + j) &&
                    graph.nodeIsOpen(graph.getNodeByCoords((int) Math.floor(destX + i), (int) Math.floor(destY + j)))) {
                openNodesAtDist.add(graph.getNodeByCoords((int) Math.floor(destX + i), (int) Math.floor(destY + j)));
                j++;
            }
            i--;
        }

        // 2nd Quadrant
        i = 0;
        j = radius;
        while (i >= -radius && j >= 0) {
            while (atkCircle.contains(destX + i, destY + j) &&
                    graph.nodeIsOpen(graph.getNodeByCoords((int) Math.ceil(destX + i), (int) Math.floor(destY + j)))) {
                openNodesAtDist.add(graph.getNodeByCoords((int) Math.ceil(destX + i), (int) Math.floor(destY + j)));
                i--;
            }
            j--;
        }

        // 3rd Quadrant
        i = -radius;
        j = 0;
        while (i <= 0 && j >= -radius) {
            while (atkCircle.contains(destX + i, destY + j) &&
                    graph.nodeIsOpen(graph.getNodeByCoords((int) Math.ceil(destX + i), (int) Math.ceil(destY + j)))) {
                openNodesAtDist.add(graph.getNodeByCoords((int) Math.ceil(destX + i), (int) Math.ceil(destY + j)));
                j--;
            }
            i++;
        }

        // 4th Quadrant
        i = 0;
        j = -radius;
        while (i <= radius && j <= 0) {
            while (atkCircle.contains(destX + i, destY + j) &&
                    graph.nodeIsOpen(graph.getNodeByCoords((int) Math.floor(destX + i), (int) Math.ceil(destY + j)))) {
                openNodesAtDist.add(graph.getNodeByCoords((int) Math.floor(destX + i), (int) Math.ceil(destY + j)));
                i++;
            }
            j++;
        }

        return openNodesAtDist;
    }


}
