package com.mygdx.game.skirmish.buildings;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.util.MapUtils;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public abstract class BuildingBase implements Commandable {

    public Rectangle rect;
    public float hp;
    public int size;

    protected Sprite sprite;

    public BuildingBase(int x, int y, int size) {
        this.rect = new Rectangle(x * MapUtils.NODE_WIDTH_PX,
                y * MapUtils.NODE_HEIGHT_PX,
                size * MapUtils.NODE_WIDTH_PX,
                size * MapUtils.NODE_HEIGHT_PX);
    }

    @Override
    public boolean processKeyStroke(int keycode) {
        return false;
    }

    @Override
    public boolean processRightClick(int screenX, int screenY) {
        return false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, rect.x - sprite.getWidth()/2, rect.y - sprite.getHeight()/2);
    }

    @Override
    public void renderSelectionMarker(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(rect.x, rect.y, rect.getWidth(), rect.getHeight());
    }

    @Override
    public boolean isMoveable() {
        return false;
    }

    @Override
    public int getMapCenterX() {
        return 0;
    }

    @Override
    public int getMapCenterY() {
        return 0;
    }
}
