package com.mygdx.game.skirmish.gameplay;

import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.buildings.BuildingBase;
import com.mygdx.game.skirmish.units.UnitBase;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by paddlefish on 28-Sep-16.
 */
public class GameObjectManager {

    private final SkirmishScreen screen;
    private final ConcurrentMap<Integer, GameObject> gameObjectMap;
    private int numGameObject = 0;

    public GameObjectManager(SkirmishScreen screen) {
        this.screen = screen;
        gameObjectMap = new ConcurrentHashMap<>();
    }

    public void add(GameObject gameObject) {
        int id = numGameObject++;
        gameObjectMap.put(id, gameObject);
        gameObject.setID(id);
        switch (gameObject.getGameObjectType()) {
            case UNIT:
                screen.getUnitManager().addUnit((UnitBase) gameObject);
                break;
            case BUILDING:
                screen.getBuildingManager().addBuilding((BuildingBase) gameObject);
                break;
            default:
                throw new RuntimeException("Unknown game object type added: " + gameObject.getGameObjectType());
        }
    }

    public GameObject getGameObjectByID(int id) {
        return gameObjectMap.get(id);
    }
}
