package com.mygdx.game.skirmish.ui;

import com.mygdx.game.skirmish.SkirmishScreen;

/**
 * Created by paddlefish on 30-Sep-16.
 */
public class GUI {
    private final SkirmishScreen screen;
    private final SelectionRenderer selectionRenderer;

    public GUI(SkirmishScreen screen) {
        this.screen = screen;
        selectionRenderer = new SelectionRenderer();
    }

    public void render(boolean debug) {
        if (debug) {
            renderDebug();
        } else {
            render();
        }
    }

    private void renderDebug() {
        selectionRenderer.renderDebug(screen.getSelectionManager().getSelection());
    }

    private void render() {
        //DO something soon
    }
}
