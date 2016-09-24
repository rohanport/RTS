package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class GroundGraph implements IndexedGraph<GroundNode> {
    private final GroundNode[][] nodes;
    private final GroundHeuristic heuristic;

    private final int width;
    private final int height;

    //-------- Getters and Setters ------------
    public GroundHeuristic getHeuristic() {
        return heuristic;
    }

    public GroundGraph(int width, int height) {
        this.width = width;
        this.height = height;

        nodes = new GroundNode[height][width];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                nodes[i][j] = new GroundNode(i, j);
            }
        }
        heuristic = new GroundHeuristic();
    }

    @Override
    public int getIndex(GroundNode node) {
        return node.x + node.y * width;
    }

    @Override
    public int getNodeCount() {
        return width * height;
    }

    @Override
    public Array<Connection<GroundNode>> getConnections(GroundNode fromNode) {
        Array<Connection<GroundNode>> connections = new Array<>();

        for (int i = fromNode.x - 1; i < fromNode.x + 2; i++) {
            for (int j = fromNode.y - 1; j < fromNode.y + 2; j++) {
                if (i == fromNode.x && j == fromNode.y) {
                    continue;
                }

                if (nodeExists(i, j)) {
                    connections.add(new DefaultConnection<>(fromNode, getNodeByCoords(i, j)));
                }
            }
        }

        return connections;
    }

    public GroundNode getNodeByCoords(int x, int y) {
        return nodes[x][y];
    }

    public GroundNode getNodeByMapPixelCoords(float pixelX, float pixelY) {
        return nodes[Math.round(pixelX / MapUtils.NODE_WIDTH_PX)][Math.round(pixelY / MapUtils.NODE_HEIGHT_PX)];
    }

    public GroundNode getNodeByScreenCoords(Camera cam, float screenX, float screenY) {
        Vector2 nodeCoords = MapUtils.screenCoords2MapCoords(cam, screenX, screenY);
        return nodes[Math.round(nodeCoords.x)][Math.round(nodeCoords.y)];
    }

    public boolean nodeExists(int x, int y) {
        return (0 <= x && x < width && 0 <= y && y < height);
    }


}
