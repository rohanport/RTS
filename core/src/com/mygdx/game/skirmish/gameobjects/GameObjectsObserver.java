package com.mygdx.game.skirmish.gameobjects;

/**
 * Created by paddlefish on 29-Sep-16.
 */
public interface GameObjectsObserver {

    enum Notification {
        CREATE,
        DESTROY
    }

    void notify(GameObject gameObject, Notification notification);
}
