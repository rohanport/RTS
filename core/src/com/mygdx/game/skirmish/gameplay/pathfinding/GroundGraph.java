package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private final UnitCollisionHandlingGroundGraph unitCollisionHandlingGroundGraph;

    private final GroundNode[][] nodes;
    private final GroundHeuristic heuristic;

    private final int width;
    private final int height;

    private int updateFrame = 0;
    
    private final ShapeRenderer debugRenderer;

    //-------- Getters and Setters ------------
    public GroundHeuristic getHeuristic() {
        return heuristic;
    }

    public int getUpdateFrame() {
        return updateFrame;
    }
//-----------------------------------------

    public GroundGraph(World world) {
        this.world = world;
        this.width = world.width;
        this.height = world.height;

        nodes = new GroundNode[height][width];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                nodes[i][j] = new GroundNode(this, i, j);
            }
        }
        heuristic = new GroundHeuristic();
        unitCollisionHandlingGroundGraph = new UnitCollisionHandlingGroundGraph(this, null);
        debugRenderer = new ShapeRenderer();
    }
    
    public void debugRender(Camera cam) {
        debugRenderer.setProjectionMatrix(cam.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int startNodeLeft = Math.max(Math.round((cam.position.x - cam.viewportWidth / 2f) / MapUtils.NODE_WIDTH_PX) - 2, 0);
        int startNodeTop = Math.max(Math.round((cam.position.y - cam.viewportHeight / 2f) / MapUtils.NODE_HEIGHT_PX) - 2, 0);
        int startNodeRight = Math.min(Math.round(startNodeLeft + cam.viewportWidth / MapUtils.NODE_WIDTH_PX) + 4, width);
        int startNodeBottom = Math.min(Math.round(startNodeTop + cam.viewportHeight / MapUtils.NODE_HEIGHT_PX) + 4, height);
        float xOffset = MapUtils.NODE_WIDTH_PX / 2f;
        float yOffset = MapUtils.NODE_HEIGHT_PX / 2f;

        for (int i = startNodeLeft; i < startNodeRight; i++) {
            for (int j = startNodeTop; j < startNodeBottom; j++) {
                GroundNode node = nodes[i][j];
                if (!nodeIsOpen(node)) {
                    debugRenderer.setColor(Color.RED.r, Color.RED.g, Color.RED.b, 0.5f);
                } else if (node.getOccupant() == NodeOccupant.MOVING_UNIT) {
                    debugRenderer.setColor(Color.YELLOW.r, Color.YELLOW.g, Color.YELLOW.b, 0.5f);
                } else {
                    debugRenderer.setColor(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, 0.5f);
                }
                debugRenderer.rect(node.x * MapUtils.NODE_WIDTH_PX - xOffset,
                        node.y * MapUtils.NODE_HEIGHT_PX - yOffset,
                        MapUtils.NODE_WIDTH_PX,
                        MapUtils.NODE_HEIGHT_PX
                );
            }
        }
        debugRenderer.end();
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.BLACK);
        for (int i = startNodeLeft; i < startNodeRight; i++) {
            debugRenderer.line(i * MapUtils.NODE_WIDTH_PX - xOffset,
                    startNodeTop * MapUtils.NODE_HEIGHT_PX - yOffset,
                    i * MapUtils.NODE_WIDTH_PX - xOffset,
                    startNodeBottom * MapUtils.NODE_HEIGHT_PX - yOffset
            );
        }
        for (int i = startNodeTop; i < startNodeBottom; i++) {
            debugRenderer.line(startNodeLeft * MapUtils.NODE_WIDTH_PX - xOffset,
                    i * MapUtils.NODE_HEIGHT_PX - yOffset,
                    startNodeRight * MapUtils.NODE_WIDTH_PX - xOffset,
                    i * MapUtils.NODE_HEIGHT_PX - yOffset
            );
        }
        debugRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void newUpdateFrame() {
        updateFrame++;
    }

    public void update(GroundNode node) {
        List<UnitBase> units = world.getUnitManager().getUnitsAtNode(node);

        node.setOccupant(NodeOccupant.NONE);
        for (UnitBase unit : units) {
            if (unit.state == UnitState.MOVING) {
                node.setOccupant(NodeOccupant.MOVING_UNIT);
            } else {
                node.setOccupant(NodeOccupant.STOPPED_UNIT);
                break;
            }
        }
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
                                GroundNode verticalNode = getNodeByCoords(fromNode.x, j);
                                GroundNode horizontalNode = getNodeByCoords(i, fromNode.y);
                                if (nodeIsOpen(horizontalNode) && nodeIsOpen(verticalNode)) {
                                    connections.add(new DefaultConnection<>(fromNode, node));
                                }
                            }
                        }
                    } else if (nodeIsAvailable(node)) {
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

    public List<GroundNode> getNodesCoveredByBuilding(float x, float y, int size) {
        List<GroundNode> coveredNodes = new ArrayList<>();

        GroundNode rootNode = getNodeByMapPixelCoords(x, y);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                coveredNodes.add(getNodeByCoords(rootNode.x + i, rootNode.y + j));
            }
        }

        return coveredNodes;
    }

    public boolean nodeExists(int x, int y) {
        return (0 <= x && x < width && 0 <= y && y < height);
    }

    public boolean nodeIsOpen(GroundNode node) {
        return nodeIsAvailable(node) &&
                (node.getOccupant() == NodeOccupant.NONE ||
                node.getOccupant() == NodeOccupant.MOVING_UNIT);
    }

    public boolean nodeIsAvailable(GroundNode node) {
        return node.getOccupant() != NodeOccupant.BUILDING;
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
        unitCollisionHandlingGroundGraph.setUnitNode(node);
        return unitCollisionHandlingGroundGraph;
    }
}
