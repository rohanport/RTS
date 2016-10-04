package com.mygdx.game.skirmish.gameobjects.units;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Resources;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.buildings.BuildingType;
import com.mygdx.game.skirmish.gameobjects.buildings.ConstructingBuilding;
import com.mygdx.game.skirmish.ui.SelectionInputState;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class Soldier1 extends UnitBase implements Builder {

    private int buildLocationX;
    private int buildLocationY;
    private BuildingType targetBuildingType;

    public Soldier1(World world, int x, int y) {
        super(world, x, y, 1);

        hp = 100f;
        curHp = hp;

        atk = 10f;
        range = 6;

        baseSpeed = 100f;

        baseAtkStartup = 0.7f;
        baseAtkEnd = 0.3f;

        sprite = Resources.getInstance().soldier1;
        portrait = Resources.getInstance().soldier1Portrait;
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
    public boolean processRightClick(int screenX, int screenY) {
        destNodeX = screenX;
        destNodeY = screenY;
        state = UnitState.MOVING;

        return false;
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
    public boolean isMovingToBuild() {
        return state == UnitState.MOVING_TO_BUILD;
    }

    @Override
    public int getBuildLocationX() {
        return buildLocationX;
    }

    @Override
    public int getBuildLocationY() {
        return buildLocationY;
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
                targetBuildingType,
                buildLocationX,
                buildLocationY,
                getID()
        );
        world.getGameObjectManager().add(constructingBuilding);
        world.getProductionManager().add(constructingBuilding);

        targetBuildingType = null;
        buildLocationX = -1;
        buildLocationY = -1;
    }
}
