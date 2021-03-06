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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public abstract class UnitBase implements Commandable, GameObject, Attacker {

    public Circle circle;
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

    protected final List<Runnable> commandQueue;

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

    public UnitBase(World world, int playerID, int nodeX, int nodeY, int size) {
        this.world = world;
        this.playerID = playerID;
        this.destNodeX = nodeX;
        this.destNodeY = nodeY;
        this.size = size;

        circle = new Circle();
        circle.setX(nodeX * MapUtils.NODE_WIDTH_PX);
        circle.setY(nodeY * MapUtils.NODE_HEIGHT_PX);
        circle.setRadius(size * MapUtils.NODE_WIDTH_PX / 2f);

        healthBar = new HealthBar(this);
        commandQueue = new ArrayList<>();
    }

    public void update(float delta) {
        if (state == UnitState.ATTACK_MOVING) {
            handleAttackMoving();
        }

        if (state == UnitState.NONE) {
            if (commandQueue.size() > 0) {
                Runnable action = commandQueue.get(0);
                commandQueue.remove(0);
                action.run();
                return;
            }

            if (isAggressive()) {
                handleIdleAggressiveUnit();
            }
        }
    }

    private void handleIdleAggressiveUnit() {
        List<? extends GameObject> enemiesInRange = world.getUnitManager().getEnemiesInRange(playerID, circle.x, circle.y, LOS * MapUtils.NODE_WIDTH_PX);
        if (enemiesInRange.size() > 0) {
            GameMathUtils.sortListByDistFrom(this, enemiesInRange);
            processAtkCommand(false, enemiesInRange.get(0).getID());
            return;
        }

        enemiesInRange = world.getBuildingManager().getEnemiesInRange(playerID, circle.x, circle.y, LOS * MapUtils.NODE_WIDTH_PX);
        if (enemiesInRange.size() > 0) {
            GameMathUtils.sortListByDistFrom(this, enemiesInRange);
            processAtkCommand(false, enemiesInRange.get(0).getID());
            return;
        }
    }

    private void handleAttackMoving() {
        List<? extends GameObject> enemiesInRange = world.getUnitManager().getEnemiesInRange(playerID, circle.x, circle.y, LOS * MapUtils.NODE_WIDTH_PX);
        if (enemiesInRange.size() > 0) {
            GameMathUtils.sortListByDistFrom(this, enemiesInRange);
            commandQueue.add(0, getAtkMoveAction(destNodeX, destNodeY));
            getAtkAction(enemiesInRange.get(0).getID()).run();
            return;
        }

        enemiesInRange = world.getBuildingManager().getEnemiesInRange(playerID, circle.x, circle.y, LOS * MapUtils.NODE_WIDTH_PX);
        if (enemiesInRange.size() > 0) {
            GameMathUtils.sortListByDistFrom(this, enemiesInRange);
            commandQueue.add(0, getAtkMoveAction(destNodeX, destNodeY));
            getAtkAction(enemiesInRange.get(0).getID()).run();
            return;
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
    public int getNodeX() {
        return Math.round(circle.x / MapUtils.NODE_WIDTH_PX);
    }

    @Override
    public int getNodeY() {
        return Math.round(circle.y / MapUtils.NODE_HEIGHT_PX);
    }

    @Override
    public void applyDamage(float damage) {
        curHp -= damage;
    }

    @Override
    public boolean processAtkCommand(boolean chain, int targetID) {
        Runnable action = getAtkAction(targetID);
        handleAddingToCommandQueue(chain, action);
        return false;
    }

    protected Runnable getAtkAction(int targetID) {
        return () -> {
            this.atkTargetID = targetID;
            this.state = UnitState.MOVING_TO_ATK;
        };
    }

    @Override
    public boolean processMoveCommand(boolean chain, int x, int y) {
        Runnable action = getMoveAction(x, y);
        handleAddingToCommandQueue(chain, action);
        return false;
    }

    @Override
    public boolean processRightClick(boolean chain, int x, int y) {
        return processMoveCommand(chain, x, y);
    }

    protected Runnable getMoveAction(int x, int y) {
        return () -> {
            destNodeX = x;
            destNodeY = y;
            state = UnitState.MOVING;
        };
    }

    @Override
    public boolean processAtkMoveCommand(boolean chain, int x, int y) {
        Runnable action = getAtkMoveAction(x, y);
        handleAddingToCommandQueue(chain, action);
        return false;
    }

    protected Runnable getAtkMoveAction(int x, int y) {
        return () -> {
            destNodeX = x;
            destNodeY = y;
            state = UnitState.ATTACK_MOVING;
        };
    }

    @Override
    public boolean processKeyStroke(boolean chain, int keycode) {
        return false;
    }

    @Override
    public boolean processRightClickOn(boolean chain, GameObject gameObject) {
        return false;
    }

    @Override
    public boolean processBuildCommand(boolean chain, int x, int y) {
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

    protected void handleAddingToCommandQueue(boolean chain, Runnable command) {
        if (chain) {
            commandQueue.add(command);
        } else {
            commandQueue.clear();
            command.run();
        }
    }
}
