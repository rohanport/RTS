package com.mygdx.game.skirmish.resources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameobjects.GameObjectsObserver;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class ResourceManager implements GameObjectsObserver {
    private final SkirmishScreen screen;
    private final List<Resource> resources;
    private final ShapeRenderer debugRenderer;

    public ResourceManager(SkirmishScreen screen) {
        this.screen = screen;
        resources = new ArrayList<>();
        debugRenderer = new ShapeRenderer();
    }

    public void renderResources(boolean debug) {
        if (debug) {
            renderDebug();
        } else {
            render();
        }
    }

    private void renderDebug() {
        debugRenderer.setProjectionMatrix(screen.getCam().combined);

        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Resource resource : resources) {
            switch (resource.type) {
                case FOOD:
                    debugRenderer.setColor(Color.RED);
                    break;
            }
            debugRenderer.rect(resource.rect.x,
                    resource.rect.y,
                    resource.rect.getWidth(),
                    resource.rect.getHeight()
            );
        }
        debugRenderer.end();
    }

    private void render(){
        //Not finished yet
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
    }

    @Override
    public void notify(GameObject gameObject, Notification notification) {
        if (gameObject.getGameObjectType() != GameObjectType.RESOURCE) {
            return;
        }

        switch (notification) {
            case CREATE:
                addResource((Resource) gameObject);
                break;
            case DESTROY:
                removeResource((Resource) gameObject);
                break;
            default:
                throw new RuntimeException("Unknown notification " + notification);
        }
    }

    public List<Resource> getResourcesAtNode(GroundNode node) {
        return resources.stream()
                .filter(resource -> isResourceAtNode(resource, node))
                .collect(Collectors.toList());
    }

    private boolean isResourceAtNode(Resource resource, GroundNode node) {
        int mapX = Math.round(resource.rect.x / MapUtils.NODE_WIDTH_PX);
        int mapY = Math.round(resource.rect.y / MapUtils.NODE_WIDTH_PX);

        return (mapX <= node.x && node.x < mapX + resource.size) &&
                (mapY <= node.y && node.y < mapY + resource.size);
    }
}
