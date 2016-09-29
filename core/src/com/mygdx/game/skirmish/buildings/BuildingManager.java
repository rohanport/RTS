package com.mygdx.game.skirmish.buildings;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameplay.GameObject;
import com.mygdx.game.skirmish.gameplay.GameObjectType;
import com.mygdx.game.skirmish.gameplay.GameObjectsObserver;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.util.GameMathUtils;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 26-Sep-16.
 */
public class BuildingManager implements GameObjectsObserver {

    private final SkirmishScreen screen;

    private SpriteBatch buildingRenderer;
    private ShapeRenderer buildingShapeRenderer;

    private List<BuildingBase> buildings;
    
    //------ Getters and Setters ----------
    
    public List<BuildingBase> getBuildings() {
        return buildings;
    }
    
    //--------------------------------------

    public BuildingManager(SkirmishScreen screen) {
        this.screen = screen;

        buildingRenderer = new SpriteBatch();
        buildingShapeRenderer = new ShapeRenderer();

        buildings = new ArrayList<>();
    }

    public void addBuilding(BuildingBase building) {
        buildings.add(building);
    }

    public void removeBuilding(BuildingBase building) {
        buildings.remove(building);
    }

    public void renderBuildings() {
        Camera cam = screen.getCam();
        buildingRenderer.setProjectionMatrix(cam.combined);

        buildingRenderer.begin();
        buildings.forEach(building -> building.render(buildingRenderer));
        buildingRenderer.end();
    }

    public void renderBuildingsDebug() {
        buildingShapeRenderer.setProjectionMatrix(screen.getCam().combined);

        buildingShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        buildingShapeRenderer.setColor(Color.RED);
        buildings.forEach(building -> buildingShapeRenderer.rect(building.rect.x,
                building.rect.y,
                building.rect.getWidth(),
                building.rect.getHeight()));

        buildingShapeRenderer.end();
    }

    public List<BuildingBase> getIntersectingBuildings(final Polygon box) {
        List<BuildingBase> intersectingBuildings = new ArrayList<>();

        buildings.stream()
                .filter(building -> GameMathUtils.isRectangleIntersectQuadrilateral(building.rect, box))
                .forEach(intersectingBuildings::add);

        return intersectingBuildings;
    }

    public List<BuildingBase> getBuildingsAtNode(GroundNode node) {
        return buildings.stream()
                .filter(building -> isBuildingAtNode(building, node))
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
                addBuilding((BuildingBase) gameObject);
                break;
            case DESTROY:
                removeBuilding((BuildingBase) gameObject);
                break;
            default:
                throw new RuntimeException("Unknown notification " + notification);
        }
    }
}
