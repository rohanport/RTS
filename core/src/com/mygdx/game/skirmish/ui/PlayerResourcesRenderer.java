package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.skirmish.player.Player;

/**
 * Created by paddlefish on 07-Oct-16.
 */
public class PlayerResourcesRenderer {
    public static final float SCREEN_WIDTH_RATIO         = 1f;
    public static final float SCREEN_HEIGHT_RATIO        = 1 / 24f;
    public static final float SCREEN_LEFT_OFFSET_RATIO   = 0f;
    public static final float SCREEN_BOTTOM_OFFSET_RATIO = 23 / 24f;

    private final ShapeRenderer debugRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public PlayerResourcesRenderer() {
        debugRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    public void renderDebug(Player player) {
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.setColor(Color.BLACK);
        debugRenderer.rect(
                Gdx.graphics.getWidth()  * SCREEN_LEFT_OFFSET_RATIO,
                Gdx.graphics.getHeight() * SCREEN_BOTTOM_OFFSET_RATIO,
                Gdx.graphics.getWidth()  * SCREEN_WIDTH_RATIO,
                Gdx.graphics.getHeight() * SCREEN_HEIGHT_RATIO
        );
        debugRenderer.end();
        batch.begin();
        font.draw(batch, "Food: " + player.food, 10, Gdx.graphics.getHeight() * SCREEN_BOTTOM_OFFSET_RATIO + 15);
        batch.end();
    }
}
