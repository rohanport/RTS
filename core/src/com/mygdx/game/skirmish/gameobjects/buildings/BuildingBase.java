package com.mygdx.game.skirmish.gameobjects.buildings;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.gameplay.ProductionTask;
import com.mygdx.game.skirmish.gameplay.production.QueueingProducer;
import com.mygdx.game.skirmish.ui.HealthBar;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public abstract class BuildingBase implements Commandable, GameObject, QueueingProducer {

    public Rectangle rect;
    public float hp;
    public float curHp;
    public int size;

    private int gameID;
    private int playerID;

    protected Sprite sprite;
    protected Sprite portrait;
    protected final World world;
    protected final List<ProductionTask> productionQueue;
    private final HealthBar healthBar;

    protected boolean hasRallyPoint;
    protected int rallyX;
    protected int rallyY;

    //----------- Getters and Setters ---------------
    @Override
    public int getID() {
        return gameID;
    }
    @Override
    public void setID(int id) {
        gameID = id;
    }

    @Override
    public float getCenterX() {
        return rect.x + rect.getWidth() / 2;
    }

    @Override
    public float getCenterY() {
        return rect.y + rect.getHeight() / 2;
    }

    @Override
    public int getPlayerID() {
        return playerID;
    }

    @Override
    public boolean hasRallyPoint() {
        return hasRallyPoint;
    }

    @Override
    public int getRallyX() {
        return rallyX;
    }

    @Override
    public int getRallyY() {
        return rallyY;
    }

    @Override
    public Sprite getPortrait() {
        return portrait;
    }

    //--------------------------------------------

    public BuildingBase(World world, int playerID, int x, int y, int size) {
        this.world = world;
        this.playerID = playerID;
        this.size = size;

        this.rect = new Rectangle(
                x * MapUtils.NODE_WIDTH_PX - size * MapUtils.NODE_WIDTH_PX / 2,
                y * MapUtils.NODE_HEIGHT_PX - size * MapUtils.NODE_HEIGHT_PX / 2,
                size * MapUtils.NODE_WIDTH_PX,
                size * MapUtils.NODE_HEIGHT_PX
        );

        healthBar = new HealthBar(this);
        productionQueue = new ArrayList<>();
    }

    public void update(float delta) {
    }

    @Override
    public GameObjectType getGameObjectType() {
        return GameObjectType.BUILDING;
    }

    @Override
    public boolean processKeyStroke(boolean chain, int keycode) {
        return false;
    }

    @Override
    public boolean processMoveCommand(boolean chain, int x, int y) {
        return false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, rect.x - sprite.getWidth()/2, rect.y - sprite.getHeight()/2);
    }

    public void renderHealthBar(SpriteBatch batch) {
        healthBar.render(batch);
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
        return Math.round(getCenterX() / MapUtils.NODE_WIDTH_PX);
    }

    @Override
    public int getMapCenterY() {
        return Math.round(getCenterY() / MapUtils.NODE_HEIGHT_PX);
    }

    @Override
    public void applyDamage(float damage) {
        curHp -= damage;
    }

    @Override
    public boolean processAtkCommand(boolean chain, int targetID) {
        return false;
    }

    @Override
    public boolean isToBeDestroyed() {
        return curHp <= 0;
    }

    @Override
    public ProductionTask getFirstInQueue() {
        if (productionQueue.size() > 0) {
            return productionQueue.get(0);
        }

        return null;
    }

    @Override
    public void addToProductionQueue(ProductionTask productionTask) {
        productionQueue.add(productionTask);
        world.getProductionManager().add(productionTask);
    }

    @Override
    public void removeFromProductionQueue(ProductionTask productionTask) {
        productionQueue.remove(productionTask);
    }

    @Override
    public boolean processBuildCommand(boolean chain, int x, int y) {
        return false;
    }

    public boolean isResourceDropOff() {
        return false;
    }

    @Override
    public boolean processRightClickOn(boolean chain, GameObject gameObject) {
        return false;
    }

    @Override
    public boolean processRightClick(boolean chain, int x, int y) {
        rallyX = x;
        rallyY = y;
        hasRallyPoint = true;
        return false;
    }

    @Override
    public boolean processAtkMoveCommand(boolean chain, int x, int y) {
        return false;
    }
}
