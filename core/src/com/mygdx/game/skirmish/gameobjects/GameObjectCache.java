package com.mygdx.game.skirmish.gameobjects;

import com.mygdx.game.skirmish.SkirmishScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 28-Sep-16.
 */
public class GameObjectCache {

    private final SkirmishScreen screen;
    private final ConcurrentMap<Integer, GameObject> gameObjectMap;
    private int numGameObject = 0;
    private final List<GameObjectsObserver> observers;

    public GameObjectCache(SkirmishScreen screen) {
        this.screen = screen;
        gameObjectMap = new ConcurrentHashMap<>();
        observers = new ArrayList<>();
    }

    public void addObserver(GameObjectsObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(GameObject gameObject, GameObjectsObserver.Notification notification) {
        observers.forEach(observer -> observer.notify(gameObject, notification));
    }

    public void add(GameObject gameObject) {
        int id = numGameObject++;
        gameObjectMap.put(id, gameObject);
        gameObject.setID(id);
        notifyObservers(gameObject, GameObjectsObserver.Notification.CREATE);
    }

    public void removeGameObjectByID(int id) {
        GameObject gameObject = gameObjectMap.get(id);
        gameObjectMap.remove(id);
        notifyObservers(gameObject, GameObjectsObserver.Notification.DESTROY);
    }

    public GameObject getGameObjectByID(int id) {
        return gameObjectMap.get(id);
    }

    public List<GameObject> getGameObjectsToBeDestroyed() {
        return gameObjectMap.values().stream()
                .filter(GameObject::isToBeDestroyed)
                .collect(Collectors.toList());
    }
}
