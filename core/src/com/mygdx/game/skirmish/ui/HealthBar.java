package com.mygdx.game.skirmish.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Resources;
import com.mygdx.game.skirmish.gameobjects.GameObject;

/**
 * Created by paddlefish on 29-Sep-16.
 */
public class HealthBar {

    public static final int BUFFER_ABOVE_OWNER      = 3;
    public static final int DEBUG_HEALTH_BAR_HEIGHT = 3;

    private final GameObject owner;

    private final Sprite sprite = Resources.getInstance().healthBar;

    public HealthBar(GameObject owner) {
        this.owner = owner;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(sprite, owner.getCenterX(), owner.getCenterY());
    }
}
