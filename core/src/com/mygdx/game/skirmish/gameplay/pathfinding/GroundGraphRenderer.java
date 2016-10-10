package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 29-Sep-16.
 */
public class GroundGraphRenderer {

    private final GroundGraph graph;
    private final ShapeRenderer debugRenderer;
    private final List<GroundNode> nodesToRenderBlue;
    private final List<GroundNode> nodesToRenderBlack;

    public GroundGraphRenderer(GroundGraph graph) {
        this.graph = graph;

        debugRenderer = new ShapeRenderer();
        nodesToRenderBlue = new ArrayList<>();
        nodesToRenderBlack = new ArrayList<>();
    }

    public void debugRender(Camera cam) {
        debugRenderer.setProjectionMatrix(cam.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int startNodeLeft = Math.max(Math.round(MapUtils.screenCoords2NodeCoords(cam, 0, 0).x) - 2, 0);
        int startNodeTop = Math.min(Math.round(MapUtils.screenCoords2NodeCoords(cam, Gdx.graphics.getWidth(), 0).y) + 2, MapUtils.MAP_HEIGHT);
        int startNodeRight = Math.min(Math.round(MapUtils.screenCoords2NodeCoords(cam, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).x) + 2, MapUtils.MAP_WIDTH);
        int startNodeBottom = Math.max(Math.round(MapUtils.screenCoords2NodeCoords(cam, 0, Gdx.graphics.getHeight()).y) - 2, 0);
        float xOffset = MapUtils.NODE_WIDTH_PX / 2f;
        float yOffset = MapUtils.NODE_HEIGHT_PX / 2f;

        for (int i = startNodeLeft; i < startNodeRight; i++) {
            for (int j = startNodeBottom; j < startNodeTop; j++) {
                GroundNode node = graph.getNodeByCoords(i, j);
                if (!graph.nodeIsOpen(node)) {
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

        debugRenderer.setColor(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, 0.5f);
        for (GroundNode node : nodesToRenderBlue) {
            debugRenderer.rect(node.x * MapUtils.NODE_WIDTH_PX - xOffset,
                    node.y * MapUtils.NODE_HEIGHT_PX - yOffset,
                    MapUtils.NODE_WIDTH_PX,
                    MapUtils.NODE_HEIGHT_PX
            );
        }
        nodesToRenderBlue.clear();

        debugRenderer.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.5f);
        for (GroundNode node : nodesToRenderBlack) {
            debugRenderer.rect(node.x * MapUtils.NODE_WIDTH_PX - xOffset,
                    node.y * MapUtils.NODE_HEIGHT_PX - yOffset,
                    MapUtils.NODE_WIDTH_PX,
                    MapUtils.NODE_HEIGHT_PX
            );
        }
        nodesToRenderBlack.clear();

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
        for (int i = startNodeBottom; i < startNodeTop; i++) {
            debugRenderer.line(startNodeLeft * MapUtils.NODE_WIDTH_PX - xOffset,
                    i * MapUtils.NODE_HEIGHT_PX - yOffset,
                    startNodeRight * MapUtils.NODE_WIDTH_PX - xOffset,
                    i * MapUtils.NODE_HEIGHT_PX - yOffset
            );
        }
        debugRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void addToNodesToRenderBlue(List<GroundNode> nodes) {
        nodesToRenderBlue.addAll(nodes);
    }

    public void addToNodesToRenderBlack(List<GroundNode> nodes) {
        nodesToRenderBlack.addAll(nodes);
    }
}
