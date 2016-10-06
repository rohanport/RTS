package com.mygdx.game.skirmish.gameobjects.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.Attacker;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.ui.HealthBar;
import com.mygdx.game.skirmish.util.GameMathUtils;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.List;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public abstract class UnitBase implements Commandable, GameObject, Attacker {

    public Circle circle;
    public int mapX;
    public int mapY;
    public float size;
    public float hp;
    public float curHp;
    protected float atk;
    protected int range;
    public int LOS;
    public float baseSpeed;
    public float speedMulti = 1f;
    protected float baseAtkStartup;
    protected float baseAtkEnd;
    protected float curAtkStartup;
    protected float curAtkEnd;
    public float baseAtkSpeedMulti = 1f;
    private int atkTargetID;

    public UnitState state = UnitState.NONE;
    public int destNodeX;
    public int destNodeY;

    private int gameID;
    private int playerID;

    protected World world;
    protected Sprite sprite;
    protected Sprite portrait;
    private final HealthBar healthBar;

    //----------- Getters and Setters ------------
    @Override
    public int getID() {
        return gameID;
    }
    @Override
    public void setID(int id) {
        gameID = id;
    }

    @Override
    public int getPlayerID() {
        return playerID;
    }

    @Override
    public float getCenterX() {
        return circle.x;
    }
    @Override
    public float getCenterY() {
        return circle.y;
    }

    @Override
    public float getAtk() {
        return atk;
    }
    @Override
    public int getRange() {
        return range;
    }
    @Override
    public float getTotalAtkStartup() {
        return baseAtkStartup / baseAtkSpeedMulti;
    }
    @Override
    public float getTotalAtkEnd() {
        return baseAtkEnd / baseAtkSpeedMulti;
    }
    @Override
    public float getCurAtkStartup() {
        return curAtkStartup;
    }
    @Override
    public void setCurAtkStartup(float curAtkStartup) {
        this.curAtkStartup = curAtkStartup;
    }
    @Override
    public float getCurAtkEnd() {
        return curAtkEnd;
    }
    @Override
    public void setCurAtkEnd(float curAtkEnd) {
        this.curAtkEnd = curAtkEnd;
    }

    @Override
    public int getAtkTargetID() {
        return atkTargetID;
    }

    @Override
    public Sprite getPortrait() {
        return portrait;
    }

    //-----------------------------------------

    public UnitBase(World world, int playerID, int mapX, int mapY, int size) {
        this.world = world;
        this.playerID = playerID;
        this.mapX = mapX;
        this.mapY = mapY;
        this.destNodeX = mapX;
        this.destNodeY = mapY;
        this.size = size;

        circle = new Circle();
        circle.setX(mapX * MapUtils.NODE_WIDTH_PX);
        circle.setY(mapY * MapUtils.NODE_HEIGHT_PX);
        circle.setRadius(size * MapUtils.NODE_WIDTH_PX / 2f);

        healthBar = new HealthBar(this);
    }

    public void update(float delta) {
        if (state == UnitState.NONE && isAggressive()) {
            handleIdleAggressiveUnit();
        }
    }

    private void handleIdleAggressiveUnit() {
        List<UnitBase> unitsInLos = world.getUnitManager().getEnemiesInRange(playerID, circle.x, circle.y, LOS * MapUtils.NODE_WIDTH_PX);
        if (unitsInLos.size() > 0) {
            GameMathUtils.sortListByDistFrom(this, unitsInLos);
            processAtkCommand(unitsInLos.get(0).getID());
        }
    }

    public void translate(Vector2 translation) {
        circle.setX(circle.x + translation.x);
        circle.setY(circle.y + translation.y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(sprite, circle.x - sprite.getWidth()/2, circle.y - sprite.getHeight()/2);
    }

    public void renderHealthBar(SpriteBatch batch) {
        healthBar.render(batch);
    }

    @Override
    public void renderSelectionMarker(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(circle.x, circle.y, circle.radius);
    }

    @Override
    public GameObjectType getGameObjectType() {
        return GameObjectType.UNIT;
    }

    @Override
    public boolean isMoveable() {
        return true;
    }

    @Override
    public int getMapCenterX() {
        return Math.round(circle.x / MapUtils.NODE_WIDTH_PX);
    }

    @Override
    public int getMapCenterY() {
        return Math.round(circle.y / MapUtils.NODE_HEIGHT_PX);
    }

    @Override
    public void applyDamage(float damage) {
        curHp -= damage;
    }

    @Override
    public boolean processAtkCommand(int targetID) {
        this.atkTargetID = targetID;
        this.state = UnitState.MOVING_TO_ATK;
        return false;
    }

    @Override
    public boolean isToBeDestroyed() {
        return curHp <= 0;
    }

    @Override
    public void startMovingToAttack() {
        state = UnitState.MOVING_TO_ATK;
    }

    @Override
    public void startAttacking() {
        state = UnitState.ATK_STARTING;
    }

    @Override
    public void startEndOfAttack() {
        state = UnitState.ATK_ENDING;
    }

    @Override
    public void stopAttacking() {
        state = UnitState.NONE;
    }

    @Override
    public boolean canMove() {
        return true;
    }

    public boolean isAggressive() {
        return false;
    }
}
