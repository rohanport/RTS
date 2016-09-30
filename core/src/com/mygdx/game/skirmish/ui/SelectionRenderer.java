package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.skirmish.gameplay.Commandable;

import java.util.List;

/**
 * Created by paddlefish on 30-Sep-16.
 */
public class SelectionRenderer {
    public static final int MAX_SELECTED_RENDERED        = 16;
    public static final float SCREEN_WIDTH_RATIO         = 1 / 2f;
    public static final float SCREEN_HEIGHT_RATIO        = 1 / 4f;
    public static final float SCREEN_LEFT_OFFSET_RATIO   = 1 / 4f;
    public static final float SCREEN_BOTTOM_OFFSET_RATIO = 0f;

    private static final int NUM_ROWS = 2;

    private final ShapeRenderer debugRenderer;
    private final SpriteBatch portraitRenderer;

    public SelectionRenderer() {
        debugRenderer = new ShapeRenderer();
        portraitRenderer = new SpriteBatch();
    }

    public void renderDebug(List<Commandable> selection) {
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.setColor(Color.BLACK);
        debugRenderer.rect(
                Gdx.graphics.getWidth()  * SCREEN_LEFT_OFFSET_RATIO,
                Gdx.graphics.getHeight() * SCREEN_BOTTOM_OFFSET_RATIO,
                Gdx.graphics.getWidth()  * SCREEN_WIDTH_RATIO,
                Gdx.graphics.getHeight() * SCREEN_HEIGHT_RATIO
        );
        debugRenderer.end();

        float portraitWidth = (Gdx.graphics.getWidth()  * SCREEN_WIDTH_RATIO) / ((MAX_SELECTED_RENDERED / NUM_ROWS));
        float portraitHeight = (Gdx.graphics.getHeight() * SCREEN_HEIGHT_RATIO) / (NUM_ROWS);

        int numDisplayedSelected = Math.min(selection.size(), MAX_SELECTED_RENDERED);

        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.WHITE);
        for (int i = 0; i < numDisplayedSelected; i ++) {
            debugRenderer.rect(
                    Gdx.graphics.getWidth()  * SCREEN_LEFT_OFFSET_RATIO + portraitWidth * (i % (MAX_SELECTED_RENDERED / NUM_ROWS)) + 1,
                    Gdx.graphics.getHeight() * SCREEN_BOTTOM_OFFSET_RATIO + portraitHeight * (1 - (i / (MAX_SELECTED_RENDERED / NUM_ROWS))) + 1,
                    portraitWidth - 2,
                    portraitHeight - 2
                    );
        }
        debugRenderer.end();

        portraitRenderer.begin();
        for (int i = 0; i < numDisplayedSelected; i ++) {
            Sprite portrait = selection.get(i).getPortrait();
            if (portrait == null) {
                continue;
            }

            portrait.setPosition(
                    Gdx.graphics.getWidth()  * SCREEN_LEFT_OFFSET_RATIO + portraitWidth * (i % (MAX_SELECTED_RENDERED / NUM_ROWS)) + 2,
                    Gdx.graphics.getHeight() * SCREEN_BOTTOM_OFFSET_RATIO + portraitHeight * (1 - (i / (MAX_SELECTED_RENDERED / NUM_ROWS))) + 2
            );

            portrait.setSize(
                    portraitWidth - 4,
                    portraitHeight - 4
            );
            portrait.draw(portraitRenderer);
        }
        portraitRenderer.end();
    }
}
