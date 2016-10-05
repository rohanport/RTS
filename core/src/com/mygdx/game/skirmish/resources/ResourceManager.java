package com.mygdx.game.skirmish.resources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameobjects.GameObjectManager;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameobjects.GameObjectsObserver;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.util.GameMathUtils;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class ResourceManager implements GameObjectsObserver, GameObjectManager<Resource> {
    private final SkirmishScreen screen;
    private final List<Resource> resources;
    private final ShapeRenderer debugRenderer;

    public ResourceManager(SkirmishScreen screen) {
        this.screen = screen;
        resources = new ArrayList<>();
        debugRenderer = new ShapeRenderer();
    }

    @Override
    public GameObjectType getObjectType() {
        return GameObjectType.RESOURCE;
    }

    @Override
    public void render(boolean debug) {
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

    @Override
    public void add(Resource resource) {
        resources.add(resource);
    }

    @Override
    public void remove(Resource resource) {
        resources.remove(resource);
    }

    @Override
    public List<Resource> getIntersecting(Vector2 point) {
        return resources.stream()
                .filter(resource -> resource.rect.contains(point))
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> getIntersecting(Polygon box) {
        return resources.stream()
                .filter(resource -> GameMathUtils.isRectangleIntersectQuadrilateral(resource.rect, box))
                .collect(Collectors.toList());
    }

    @Override
    public List<Commandable> getIntersectingCommandables(Vector2 point) {
        return Collections.emptyList();
    }

    @Override
    public List<Commandable> getIntersectingCommandables(Polygon box) {
        return Collections.emptyList();
    }

    @Override
    public void notify(com.mygdx.game.skirmish.gameobjects.GameObject gameObject, Notification notification) {
        if (gameObject.getGameObjectType() != GameObjectType.RESOURCE) {
            return;
        }

        switch (notification) {
            case CREATE:
                add((Resource) gameObject);
                break;
            case DESTROY:
                remove((Resource) gameObject);
                break;
            default:
                throw new RuntimeException("Unknown notification " + notification);
        }
    }

    @Override
    public List<Resource> getAtNode(GroundNode node) {
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
