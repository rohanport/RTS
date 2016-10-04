package com.mygdx.game.skirmish.gameobjects.buildings;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class BuildingUtils {

    public static int getSizeFor(BuildingType buildingType) {
        switch (buildingType) {
            case BUILDING1:
                return Building1.SIZE;
            default:
                throw new RuntimeException("Unknown building type " + buildingType);
        }
    }

    public static float getHpFor(BuildingType buildingType) {
        switch (buildingType) {
            case BUILDING1:
                return Building1.HP;
            default:
                throw new RuntimeException("Unknown building type " + buildingType);
        }
    }

    public static float getBuildDurationFor(BuildingType buildingType) {
        switch (buildingType) {
            case BUILDING1:
                return Building1.BUILD_DURATION;
            default:
                throw new RuntimeException("Unknown building type " + buildingType);
        }
    }
}
