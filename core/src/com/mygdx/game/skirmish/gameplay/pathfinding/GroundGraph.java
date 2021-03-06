package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingBase;
import com.mygdx.game.skirmish.gameobjects.units.UnitBase;
import com.mygdx.game.skirmish.resources.Resource;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.List;

/**
 * Created by paddlefish on 21-Sep-16.
 *
 * Graph for the group map. Used for AStar pathing for ground units
 */
public class GroundGraph implements IndexedGraph<GroundNode> {
    private final World world;
    private final UnitCollisionHandlingGroundGraph unitCollisionHandlingGroundGraph;

    private final NodeOccupant[][] baseNodes;
    private final GroundNode[][] nodes;
    private final GroundHeuristic heuristic;

    private final int width;
    private final int height;

    private int updateFrame = 0;
    private final GroundGraphRenderer renderer;

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
        baseNodes = new NodeOccupant[height][width];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                nodes[i][j] = new GroundNode(this, i, j);
                baseNodes[i][j] = NodeOccupant.NONE;
            }
        }
        heuristic = new GroundHeuristic();
        unitCollisionHandlingGroundGraph = new UnitCollisionHandlingGroundGraph(this, null);
        renderer = new GroundGraphRenderer(this);
    }
    
    public void debugRender(Camera cam) {
        renderer.debugRender(cam);
    }

    public void newUpdateFrame() {
        updateFrame++;
    }

    public void update(GroundNode node) {
        //This is here to ensure that terrain nodes never get overridden
        if (baseNodes[node.x][node.y] == NodeOccupant.TERRAIN) {
            node.setOccupant(NodeOccupant.TERRAIN);
            return;
        }

        List<Resource> resources = world.getResourceManager().getAtNode(node);
        if (resources.size() > 0) {
            node.setOccupant(NodeOccupant.RESOURCE);
            return;
        }

        List<BuildingBase> buildings = world.getBuildingManager().getAtNode(node);
        if (buildings.size() > 0) {
            node.setOccupant(NodeOccupant.BUILDING);
            return;
        }

        List<UnitBase> units = world.getUnitManager().getAtNode(node);

        node.setOccupant(NodeOccupant.NONE);
        for (UnitBase unit : units) {
            switch (unit.state) {
                case MOVING:
                case MOVING_TO_ATK:
                case MOVING_TO_BUILD:
                case MOVING_TO_GATHER:
                case MOVING_TO_RETURN_RESOURCES:
                case ATTACK_MOVING:
                    node.setOccupant(NodeOccupant.MOVING_UNIT);
                    break; // Only breaks here because STOPPED_UNIT overrides MOVING_UNIT
                case ATK_STARTING:
                case ATK_ENDING:
                case BUILDING:
                case GATHERING:
                case NONE:
                    node.setOccupant(NodeOccupant.STOPPED_UNIT);
                    return;
                default:
                    throw new RuntimeException("Unknown unit state when updating node: " + unit.state);
            }
        }
    }

    /**
     * Sets a node index as a Terrain node, which can never be overridden
     * Use when initializing terrain nodes from a tile map
     * @param x
     * @param y
     */
    public void setTerrainNode(int x, int y) {
        baseNodes[x][y] = NodeOccupant.TERRAIN;
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

    /**
     * Returns a different set of connections depending on whether the nodes must be open (ie. not containing a STOPPED_UNIT)
     * The nodesMustBeOpen flag is set to true when calculating a collision handling path
     * @param fromNode
     * @param nodeMustBeOpen
     * @return
     */
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
                        if (i == fromNode.x || j == fromNode.y) {
                            connections.add(new DefaultConnection<>(fromNode, node));
                        } else {
                            GroundNode verticalNode = getNodeByCoords(fromNode.x, j);
                            GroundNode horizontalNode = getNodeByCoords(i, fromNode.y);
                            if (nodeIsAvailable(horizontalNode) && nodeIsAvailable(verticalNode)) {
                                connections.add(new DefaultConnection<>(fromNode, node));
                            }
                        }
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
        Vector2 nodeCoords = MapUtils.screenCoords2NodeCoords(cam, screenX, screenY);
        return nodes[Math.round(nodeCoords.x)][Math.round(nodeCoords.y)];
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
        return node.getOccupant() != NodeOccupant.BUILDING &&
                node.getOccupant() != NodeOccupant.TERRAIN;
    }

    /**
     * Returns the closest open node to the dest Node using manhattan distance
     * @param destNode
     * @return
     */
    public GroundNode getClosestFreeNode(GroundNode destNode) {
        if (nodeIsOpen(destNode)) {
            return destNode;
        } else {
            int dist = 1;

            while (dist < Math.max(height, width) / 2) {
                List<GroundNode> openNodesAtDist = GroundGraphUtils.getFreeNodesAtDist(this, destNode, dist);

                if (openNodesAtDist.size() > 0) {
                    return openNodesAtDist.get(0);
                }
                dist++;
            }
        }

        throw new RuntimeException("Couldn't find open node in the whole world");
    }

    /**
     * Returns the closet node to the cur node, of the closest open nodes to the dest node, in manhattan distance
     * @param curNode
     * @param destNode
     * @return
     */
    public GroundNode getClosestFreeNode(GroundNode curNode, GroundNode destNode) {
        if (nodeIsOpen(destNode)) {
            return destNode;
        } else {
            int dist = 1;

            while (dist < Math.max(height, width) / 2) {
                List<GroundNode> openNodesAtDist = GroundGraphUtils.getFreeNodesAtDist(this, destNode, dist);

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

    /**
     * Returns the closet node to the cur node, of the closest open nodes to the point (destX, destY), in euclidean distance
     * @param curNode
     * @param destX
     * @param destY
     * @param radius
     * @return
     */
    public GroundNode getClosestFreeNodeEuclidean(GroundNode curNode, float destX, float destY, int radius) {
        List<GroundNode> openNodesAtDist = GroundGraphUtils.getFreeNodesAtDistEuclidean(this, destX, destY, radius);

        if (openNodesAtDist.size() > 0) {
            openNodesAtDist.sort((node1, node2) ->
                    Math.round(Math.signum(heuristic.estimate(node1, curNode) - heuristic.estimate(node2, curNode)))
            );

            return openNodesAtDist.get(0);
        }

        throw new RuntimeException("Couldn't find free node at radius in the whole world");
    }

    public UnitCollisionHandlingGroundGraph getCollisionHandlingGraphFor(GroundNode node) {
        unitCollisionHandlingGroundGraph.setUnitNode(node);
        return unitCollisionHandlingGroundGraph;
    }
}
