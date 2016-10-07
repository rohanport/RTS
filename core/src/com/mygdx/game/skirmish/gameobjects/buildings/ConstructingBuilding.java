package com.mygdx.game.skirmish.gameobjects.buildings;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameplay.ProductionTask;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class ConstructingBuilding extends BuildingBase implements ProductionTask {

    public final float duration;
    private float timeRemaining;
    public boolean isConstructing = true;
    public boolean isComplete = false;
    public int constructingUnitID;

    public final BuildingType finBuildingType;

    public ConstructingBuilding(World world,
                                int playerID,
                                BuildingType finBuildingType,
                                int x,
                                int y,
                                int constructingUnitID) {
        super(world, playerID, x, y, BuildingUtils.getSizeFor(finBuildingType));

        this.hp = BuildingUtils.getHpFor(finBuildingType);
        this.duration = BuildingUtils.getBuildDurationFor(finBuildingType);
        this.timeRemaining = duration;
        this.constructingUnitID = constructingUnitID;
        this.finBuildingType = finBuildingType;

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        isConstructing = world.getGameObjectCache().getGameObjectByID(constructingUnitID) != null;

        if (isConstructing) {
            curHp = Math.min(curHp + hp * (delta / duration), hp);
        }
    }

    @Override
    public Sprite getPortrait() {
        return null;
    }

    @Override
    public boolean isBeingProduced() {
        return isConstructing;
    }

    @Override
    public float getTotalTime() {
        return duration;
    }

    @Override
    public void passTime(float time) {
        timeRemaining -= time;
    }

    @Override
    public float getRemainingTime() {
        return timeRemaining;
    }

    @Override
    public void performOperation() {
        produceBuildingAndDestroyThis();
    }

    private void produceBuildingAndDestroyThis() {
        BuildingBase building;
        switch (finBuildingType) {
            case BUILDING1:
                building = new Building1(world, getPlayerID(), getMapCenterX(), getMapCenterY());
                break;
            default:
                throw new RuntimeException("Attempting to build unknown unit type " + finBuildingType);
        }

        world.getGameObjectCache().add(building);

        isComplete = true;
    }

    @Override
    public boolean isToBeDestroyed() {
        return isComplete || super.isToBeDestroyed();
    }
}
