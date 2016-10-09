package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.skirmish.SkirmishScreen;

/**
 * Created by paddlefish on 30-Sep-16.
 */
public class GUI {
    private final Stage stage;
    private final SelectionTable selectionTable;
    private final SkirmishScreen screen;
    private final PlayerResourcesRenderer playerResourcesRenderer;

    public GUI(SkirmishScreen screen) {
        this.screen = screen;
        playerResourcesRenderer = new PlayerResourcesRenderer();
        stage = new Stage();
        selectionTable = new SelectionTable();
        stage.addActor(selectionTable.getTable());
    }

    public void render(boolean debug) {
        if (debug) {
            renderDebug();
        } else {
            render();
        }
    }

    private void renderDebug() {
        selectionTable.setSelection(screen.getSelectionManager().getSelection());
        stage.draw();
//        selectionRenderer.renderDebug(screen.getSelectionManager().getSelection());
        if (screen.getPlayerManager().getNumPlayers() > 0) {
            playerResourcesRenderer.renderDebug(screen.getPlayerManager().getPlayerByID(0));
        }
    }

    private void render() {
        //DO something soon
    }
}
