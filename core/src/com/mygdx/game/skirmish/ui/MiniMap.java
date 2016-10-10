package com.mygdx.game.skirmish.ui;

import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundGraph;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 09-Oct-16.
 */
public class MiniMap {
    private static final int TILE_WIDTH = 5;
    private static final int TILE_HEIGHT = 5;

    private final SkirmishScreen screen;

    public MiniMap(SkirmishScreen screen) {
        this.screen = screen;
    }

    public void render(boolean debug) {

    }

    private void renderDebug() {
        GroundGraph groundGraph = screen.getWorld().getGroundGraph();
        for (int i = 0; i < MapUtils.MAP_WIDTH; i++) {
            for (int j = 0; j < MapUtils.MAP_HEIGHT; j++) {
            }
        }
    }


}
