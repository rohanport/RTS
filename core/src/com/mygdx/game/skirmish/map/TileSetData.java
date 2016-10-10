package com.mygdx.game.skirmish.map;

/**
 * Created by paddlefish on 10-Oct-16.
 */
public class TileSetData {
    private static final boolean[] DESERT_TERRAIN_NODE_ACCESS_MATRIX = new boolean[] {true, false, false, true};

    public static boolean getDesertTerrainAccess(int terrainID) {
        return DESERT_TERRAIN_NODE_ACCESS_MATRIX[terrainID];
    }
}
