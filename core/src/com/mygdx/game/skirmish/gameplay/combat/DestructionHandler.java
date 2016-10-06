package com.mygdx.game.skirmish.gameplay.combat;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.gameobjects.GameObject;

import java.util.List;

/**
 * Created by paddlefish on 29-Sep-16.
 */
public class DestructionHandler {

    private final World world;

    public DestructionHandler(World world) {
        this.world = world;
    }

    public void handleGameObjectDestruction(List<? extends GameObject> gameObjects) {
        gameObjects.forEach(gameObject -> world.getGameObjectCache().removeGameObjectByID(gameObject.getID()));
    }
}
