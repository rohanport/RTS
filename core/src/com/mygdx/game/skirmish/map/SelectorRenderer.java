package com.mygdx.game.skirmish.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class SelectorRenderer {

    private ShapeRenderer renderer;

    private int x;
    private int y;
    private int height;
    private int width;

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    private boolean isDrawing;

    public boolean isDrawing() {
        return isDrawing;
    }

    public void setDrawing(boolean drawing) {
        isDrawing = drawing;
    }

    public SelectorRenderer() {
        renderer = new ShapeRenderer();
        renderer.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render() {
        if (isDrawing()) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(Color.WHITE);
            renderer.rect(x, y, width, height);
            renderer.end();
        }
    }

    public void handleMouseDown(int screenX, int screenY) {
        setX(screenX);
        setY(Gdx.graphics.getHeight() - screenY);

        setDrawing(true);
    }

    public void handleMouseMove(int screenX, int screenY) {
        if (isDrawing()) {
            setWidth(screenX - x);
            setHeight((Gdx.graphics.getHeight() - screenY) - y);
        }
    }

    public void handleMouseUp() {
        setWidth(0);
        setHeight(0);
        setDrawing(false);
    }
}
