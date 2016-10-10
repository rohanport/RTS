package com.mygdx.game.skirmish.map;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.resources.Resource;
import com.mygdx.game.skirmish.resources.ResourceType;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 10-Oct-16.
 */
public class TiledMapLoader {

    private final World world;

    public TiledMapLoader(World world) {
        this.world = world;
    }

    public void loadMap(Map map) {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("Ground");
        if (tileLayer != null) {
            initializeGraphFromTileLayer(tileLayer);
        }

        MapLayer objectLayer = map.getLayers().get("GameObjects");
        if (objectLayer != null) {
            addObjectsFromObjectLayer(objectLayer);
        }
    }

    private void initializeGraphFromTileLayer(TiledMapTileLayer tileLayer) {
        for(int i = 0; i < MapUtils.TILED_MAP_WIDTH; i++) {
            for (int j = 0; j < MapUtils.TILED_MAP_HEIGHT; j++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(i, j);
                int startingX = i * MapUtils.nodesPerTileHorizontal();
                int startingY = j * MapUtils.nodesPerTileVertical();
                String[] tileTerrains = ((String) cell.getTile().getProperties().get("terrain")).split(",");
                for (int y = startingY; y < startingY + MapUtils.nodesPerTileVertical(); y++) {
                    for (int x = startingX; x < startingX + MapUtils.nodesPerTileHorizontal(); x++) {
                        if (!TileSetData.getDesertTerrainAccess(Integer.parseInt(tileTerrains[getQuadrant(x, y, startingX, startingY)]))) {
                            world.getGroundGraph().setTerrainNode(x, y);
                        }
                    }
                }
            }
        }
    }

    private int getQuadrant(int x, int y, int startX, int startY) {
        int diffX = x - startX;
        int diffY = y - startY;

        if (diffX < MapUtils.nodesPerTileHorizontal() / 2) {
            if (diffY < MapUtils.nodesPerTileVertical() / 2) {
                return 2;
            } else {
                return 0;
            }
        } else {
            if (diffY < MapUtils.nodesPerTileVertical() / 2) {
                return 3;
            } else {
                return 1;
            }
        }
    }

    private void addObjectsFromObjectLayer(MapLayer objectLayer) {
        for (MapObject mapObject : objectLayer.getObjects()) {
            MapProperties objectProperties = mapObject.getProperties();
            String type = (String) objectProperties.get("type");
            if (type == null) {
                throw new NullPointerException("Game object from tile map must have a type!");
            }

            Vector2 nodeCoords = MapUtils.tileCoords2NodeCoords(
                    (float) objectProperties.get("x"),
                    (float) objectProperties.get("y")
            );
            GameObject gameObject;
            switch (type) {
                case "resource":
                    gameObject = new Resource(
                            ResourceType.FOOD,
                            2000,
                            Math.round(nodeCoords.x),
                            Math.round(nodeCoords.y),
                            3
                    );
                    break;
                default:
                    throw new RuntimeException("Unknown gameobject type loading from map: " + type);
            }

            world.getGameObjectCache().add(gameObject);
        }
    }
}
