package com.mygdx.game.skirmish.gameobjects.buildings;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.GameObjectManager;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameobjects.GameObjectsObserver;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.util.GameMathUtils;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public class BuildingManager implements GameObjectsObserver, GameObjectManager<BuildingBase> {

    private final SkirmishScreen screen;

    private SpriteBatch buildingRenderer;
    private ShapeRenderer buildingShapeRenderer;

    private List<BuildingBase> buildings;
    
    //------ Getters and Setters ----------

    //--------------------------------------

    public BuildingManager(SkirmishScreen screen) {
        this.screen = screen;

        buildingRenderer = new SpriteBatch();
        buildingShapeRenderer = new ShapeRenderer();

        buildings = new ArrayList<>();
    }

    public void update(float delta) {
        buildings.forEach(building -> building.update(delta));
    }

    @Override
    public void add(BuildingBase building) {
        buildings.add(building);
    }

    @Override
    public void remove(BuildingBase building) {
        buildings.remove(building);
    }

    @Override
    public GameObjectType getObjectType() {
        return GameObjectType.BUILDING;
    }

    @Override
    public void render(boolean debug) {
        if (debug) {
            renderBuildingsDebug();
        } else {
            render();
        }
    }

    private void render() {
        Camera cam = screen.getCam();
        buildingRenderer.setProjectionMatrix(cam.combined);

        buildingRenderer.begin();
        for (BuildingBase building : buildings) {
            building.render(buildingRenderer);
            building.renderHealthBar(buildingRenderer);
        }
        buildingRenderer.end();
    }

    private void renderBuildingsDebug() {
        buildingShapeRenderer.setProjectionMatrix(screen.getCam().combined);

        buildingShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BuildingBase building : buildings) {
            buildingShapeRenderer.setColor(screen.getPlayerManager().getPlayerByID(building.getPlayerID()).color);
            buildingShapeRenderer.rect(building.rect.x,
                    building.rect.y,
                    building.rect.getWidth(),
                    building.rect.getHeight()
            );
        }
        buildingShapeRenderer.end();
    }

    @Override
    public List<BuildingBase> getIntersecting(Polygon box) {
        return buildings.stream()
                .filter(building -> GameMathUtils.isRectangleIntersectQuadrilateral(building.rect, box))
                .collect(Collectors.toList());
    }

    @Override
    public List<Commandable> getIntersectingCommandables(Vector2 point) {
        return getIntersecting(point).stream()
                .map(Commandable.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<Commandable> getIntersectingCommandables(Polygon box) {
        return getIntersecting(box).stream()
                .map(Commandable.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildingBase> getIntersecting(Vector2 point) {
        return buildings.stream()
                .filter(building -> building.rect.contains(point))
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildingBase> getAtNode(GroundNode node) {
        return buildings.stream()
                .filter(building -> isBuildingAtNode(building, node))
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildingBase> getEnemiesInRange(int playerID, float x, float y, float radius) {
        Circle range = new Circle(x, y, radius);
        return buildings.stream()
                .filter(building -> range.contains(building.getCenterX(), building.getCenterY()))
                .filter(unit -> screen.getPlayerManager().areEnemies(playerID, unit.getPlayerID()))
                .collect(Collectors.toList());
    }

    private boolean isBuildingAtNode(BuildingBase building, GroundNode node) {
        int mapX = Math.round(building.rect.x / MapUtils.NODE_WIDTH_PX);
        int mapY = Math.round(building.rect.y / MapUtils.NODE_WIDTH_PX);

        return (mapX <= node.x && node.x < mapX + building.size) &&
                (mapY <= node.y && node.y < mapY + building.size);
    }

    @Override
    public void notify(GameObject gameObject, Notification notification) {
        if (gameObject.getGameObjectType() != GameObjectType.BUILDING) {
            return;
        }

        switch (notification) {
            case CREATE:
                add((BuildingBase) gameObject);
                break;
            case DESTROY:
                remove((BuildingBase) gameObject);
                break;
            default:
                throw new RuntimeException("Unknown notification " + notification);
        }
    }

    public List<BuildingBase> getDropOffBuildings() {
        return buildings.stream()
                .filter(BuildingBase::isResourceDropOff)
                .collect(Collectors.toList());
    }
}
