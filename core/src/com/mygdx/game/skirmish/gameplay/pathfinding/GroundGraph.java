package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.units.UnitState;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 21-Sep-16.
 */
public class GroundGraph implements IndexedGraph<GroundNode> {
    private final World world;

    private final GroundNode[][] nodes;
    private final GroundHeuristic heuristic;

    private final int width;
    private final int height;

    //-------- Getters and Setters ------------
    public GroundHeuristic getHeuristic() {
        return heuristic;
    }

    //-----------------------------------------

    public GroundGraph(World world) {
        this.world = world;
        this.width = world.width;
        this.height = world.height;

        nodes = new GroundNode[height][width];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                nodes[i][j] = new GroundNode(i, j);
            }
        }
        heuristic = new GroundHeuristic();
    }

    public void update() {
        List<UnitBase> units = world.getUnitManager().getUnits();

        setAllNodesTo(NodeOccupant.NONE);
        units.forEach(unit ->
                getNodeByMapPixelCoords(unit.circle.x, unit.circle.y)
                .setOccupant(unit.state == UnitState.MOVING ? NodeOccupant.MOVING_UNIT : NodeOccupant.STOPPED_UNIT)
        );
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
        return getConnections(fromNode, false);
    }

    public Array<Connection<GroundNode>> getConnections(GroundNode fromNode, boolean nodeMustBeOpen) {
        Array<Connection<GroundNode>> connections = new Array<>();

        for (int i = fromNode.x - 1; i < fromNode.x + 2; i++) {
            for (int j = fromNode.y - 1; j < fromNode.y + 2; j++) {
                if (i == fromNode.x && j == fromNode.y) {
                    continue;
                }

                if (nodeExists(i, j)) {
                    GroundNode node = getNodeByCoords(i, j);
                    if (nodeMustBeOpen) {
                        if (nodeIsOpen(node)) {
                            if (i == fromNode.x || j == fromNode.y) {
                                connections.add(new DefaultConnection<>(fromNode, node));
                            } else {
                                GroundNode horizontalNode = getNodeByCoords(fromNode.x, j);
                                GroundNode verticalNode = getNodeByCoords(i, fromNode.y);
                                if (nodeIsOpen(horizontalNode) && nodeIsOpen(verticalNode)) {
                                    connections.add(new DefaultConnection<>(fromNode, node));
                                }
                            }
                        }
                    } else {
                        connections.add(new DefaultConnection<>(fromNode, node));
                    }
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

    @SuppressWarnings("unused")
    public GroundNode getNodeByScreenCoords(Camera cam, float screenX, float screenY) {
        Vector2 nodeCoords = MapUtils.screenCoords2MapCoords(cam, screenX, screenY);
        return nodes[Math.round(nodeCoords.x)][Math.round(nodeCoords.y)];
    }

    public boolean nodeExists(int x, int y) {
        return (0 <= x && x < width && 0 <= y && y < height);
    }

    public boolean nodeIsOpen(GroundNode node) {
        return node.getOccupant() == NodeOccupant.NONE ||
                node.getOccupant() == NodeOccupant.MOVING_UNIT;
    }

    public GroundNode getClosestFreeNode(GroundNode curNode, GroundNode destNode) {
        if (nodeIsOpen(destNode)) {
            return destNode;
        } else {
            int dist = 1;

            while (dist < Math.max(height, width) / 2) {
                List<GroundNode> openNodesAtDist = getFreeNodesAtDist(destNode, dist);

                if (openNodesAtDist.size() > 0) {
                    openNodesAtDist.sort((node1, node2) ->
                            Math.round(Math.signum(heuristic.estimate(node1, curNode) - heuristic.estimate(node2, curNode)))
                    );

                    return openNodesAtDist.get(0);
                }
                dist++;
            }
        }

        throw new RuntimeException("Couldn't find open node in the whole world");
    }

    public int getDist(GroundNode src, GroundNode dest) {
        return Math.max(Math.abs(dest.x - src.x), Math.abs(dest.y - src.y));
    }

    public int getDistOfClosestFreeNode(GroundNode destNode) {
        if (nodeIsOpen(destNode)) {
            return 0;
        } else {
            int dist = 1;

            while (dist < Math.max(height, width) / 2) {
                if (isFreeNodeAtDist(destNode, dist)) {
                    return dist;
                }

                dist++;
            }

            return -1;
        }
    }

    private void setAllNodesTo(NodeOccupant occupant) {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                nodes[i][j].setOccupant(occupant);
            }
        }
    }

    private boolean isFreeNodeAtDist(GroundNode baseNode, int dist) {
        for (int i = -dist; i <= dist; i++) {
            for (int j = -dist; j <= dist; j++) {
                if (Math.abs(i) < dist && Math.abs(j) < dist) {
                    continue;
                }

                if (nodeExists(baseNode.x + i, baseNode.y + j)) {
                    GroundNode node = getNodeByCoords(baseNode.x + i, baseNode.y + j);
                    if (nodeIsOpen(node)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private List<GroundNode> getFreeNodesAtDist(GroundNode baseNode, int dist) {
        List<GroundNode> openNodesAtDist = new ArrayList<>();
        for (int i = -dist; i <= dist; i++) {
            for (int j = -dist; j <= dist; j++) {
                if (Math.abs(i) < dist && Math.abs(j) < dist) {
                    continue;
                }

                if (nodeExists(baseNode.x + i, baseNode.y + j)) {
                    GroundNode node = getNodeByCoords(baseNode.x + i, baseNode.y + j);
                    if (nodeIsOpen(node)) {
                        openNodesAtDist.add(node);
                    }
                }
            }
        }

        return openNodesAtDist;
    }

    public UnitCollisionHandlingGroundGraph getCollisionHandlingGraphFor(GroundNode node) {
        return new UnitCollisionHandlingGroundGraph(world, this, node);
    }
}
