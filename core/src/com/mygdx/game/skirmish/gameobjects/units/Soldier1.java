package com.mygdx.game.skirmish.gameobjects.units;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.GameData;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingBase;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingType;
import com.mygdx.game.skirmish.gameobjects.buildings.ConstructingBuilding;
import com.mygdx.game.skirmish.player.Player;
import com.mygdx.game.skirmish.ui.SelectionInputState;
import com.mygdx.game.skirmish.util.GameMathUtils;

import java.util.List;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class Soldier1 extends UnitBase implements Builder, Gatherer {

    private int buildLocationX;
    private int buildLocationY;
    private BuildingType targetBuildingType;

    private final int maxCarryFood = 10;
    private int curFood = 0;
    private float totalGatherDuration = 1.0f;
    private float curGatherTime = 0.0f;

    private int gatherSourceID;
    private int dropOffTargetID;

    //-------- Getters and Setters ------------

    @Override
    public int getMaxCarryFood() {
        return maxCarryFood;
    }

    @Override
    public int getCurrentFood() {
        return curFood;
    }

    @Override
    public void setCurrentFood(int currentFood) {
        this.curFood = currentFood;
    }

    @Override
    public float getTotalGatherDuration() {
        return totalGatherDuration;
    }

    @Override
    public float getCurGatherTime() {
        return curGatherTime;
    }

    @Override
    public void setCurGatherTime(float curGatherTime) {
        this.curGatherTime = curGatherTime;
    }

    @Override
    public int getGatherSourceID() {
        return gatherSourceID;
    }

    @Override
    public int getDropOffTargetID() {
        return dropOffTargetID;
    }

    @Override
    public int getBuildLocationX() {
        return buildLocationX;
    }

    @Override
    public int getBuildLocationY() {
        return buildLocationY;
    }
    //-----------------------------------------

    public Soldier1(World world, int playerID, int x, int y) {
        super(world, playerID, x, y, 1);

        hp = 100f;
        curHp = hp;

        atk = 10f;
        range = 6;

        baseSpeed = 100f;

        baseAtkStartup = 0.7f;
        baseAtkEnd = 0.3f;

        sprite = GameData.getInstance().soldier1;
        portrait = GameData.getInstance().soldier1Portrait;
    }

    @Override
    public boolean processKeyStroke(int keycode) {
        switch (keycode) {
            case Input.Keys.B:
                world.getScreen().getSelectionManager().setState(SelectionInputState.BUILD);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean processMoveCommand(int x, int y) {
        destNodeX = x;
        destNodeY = y;
        state = UnitState.MOVING;

        return false;
    }

    @Override
    public boolean processRightClickOn(GameObject gameObject) {
        switch (gameObject.getGameObjectType()) {
            case UNIT:
            case BUILDING:
                return processMoveCommand(gameObject.getMapCenterX(), gameObject.getMapCenterY());
            case RESOURCE:
                gatherSourceID = gameObject.getID();
                state = UnitState.MOVING_TO_GATHER;
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean processBuildCommand(int x, int y) {
        buildLocationX = x;
        buildLocationY = y;
        targetBuildingType = BuildingType.BUILDING1;
        state = UnitState.MOVING_TO_BUILD;
        return true;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(sprite,
                circle.x - (circle.radius * 1.3f),
                circle.y - (circle.radius * 1.3f),
                circle.radius * 2 * 1.3f,
                4 * (circle.radius * 2 / 3f) * 1.3f
        );
    }

    @Override
    public boolean isBuilding() {
        return state == UnitState.BUILDING;
    }

    @Override
    public BuildingType getBuildingType() {
        return targetBuildingType;
    }

    @Override
    public void startBuilding() {
        state = UnitState.BUILDING;
        ConstructingBuilding constructingBuilding = new ConstructingBuilding(
                world,
                getPlayerID(),
                targetBuildingType,
                buildLocationX,
                buildLocationY,
                getID()
        );
        world.getGameObjectCache().add(constructingBuilding);
        world.getProductionManager().add(constructingBuilding);

        targetBuildingType = null;
        buildLocationX = -1;
        buildLocationY = -1;
    }

    @Override
    public void startGathering() {
        state = UnitState.GATHERING;
    }

    @Override
    public void stopGathering() {
        state = UnitState.NONE;
    }

    @Override
    public void startDropOff() {
        curGatherTime = 0.0f;
        List<BuildingBase> dropOffBuildings = world.getBuildingManager().getDropOffBuildings();
        dropOffBuildings.sort((o1, o2) ->
                Math.round(Math.signum(
                        GameMathUtils.distBetween(circle.x, circle.y, o1.getCenterX(), o1.getCenterY()) -
                                GameMathUtils.distBetween(circle.x, circle.y, o2.getCenterX(), o2.getCenterY())
                ))
        );

        dropOffTargetID = dropOffBuildings.get(0).getID();
        state = UnitState.MOVING_TO_RETURN_RESOURCES;
    }

    @Override
    public void performDropOff() {
        Player player = world.getScreen().getPlayerManager().getPlayerByID(getPlayerID());
        player.food += curFood;
        curFood = 0;
        state = UnitState.MOVING_TO_GATHER;
    }
}
