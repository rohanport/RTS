package com.mygdx.game.skirmish.gameobjects.units;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameobjects.GameObject;
import com.mygdx.game.skirmish.gameobjects.GameObjectType;
import com.mygdx.game.skirmish.gameobjects.GameObjectsObserver;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.ui.HealthBar;
import com.mygdx.game.skirmish.util.GameMathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 18-Sep-16.
 *
 * Manages units in skirmish mode
 */
public class UnitManager implements GameObjectsObserver {

    private final SkirmishScreen screen;

    private SpriteBatch unitRenderer;
    private ShapeRenderer unitShapeRenderer;

    private List<UnitBase> units;

    // Getters and setters
    public List<UnitBase> getUnits() {
        return units;
    }

    //---------------------------------------------

    public UnitManager(SkirmishScreen screen) {
        this.screen = screen;

        unitRenderer = new SpriteBatch();
        unitShapeRenderer = new ShapeRenderer();

        units = new ArrayList<>();
    }

    public void addUnit(UnitBase unit) {
        units.add(unit);
    }

    public void removeUnit(UnitBase unit) {
        units.remove(unit);
    }

    public void renderUnits(boolean debug) {
        if (debug) {
            renderUnitsDebug();
        } else {
            renderUnits();
        }
    }

    private void renderUnits() {
        Camera cam = screen.getCam();
        unitRenderer.setProjectionMatrix(cam.combined);

        unitRenderer.begin();
        for (UnitBase unit : units) {
            unit.render(unitRenderer);
            unit.renderHealthBar(unitRenderer);
        }
        unitRenderer.end();
    }

    private void renderUnitsDebug() {
        unitShapeRenderer.setProjectionMatrix(screen.getCam().combined);

        unitShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        unitShapeRenderer.setColor(Color.RED);
        units.forEach(unit -> unitShapeRenderer.circle(unit.circle.x, unit.circle.y, unit.circle.radius));
        unitShapeRenderer.setColor(Color.GREEN);
        for (UnitBase unit : units) {
            unitShapeRenderer.rect(
                    unit.circle.x - unit.circle.radius,
                    unit.circle.y + unit.circle.radius + HealthBar.BUFFER_ABOVE_OWNER,
                    2 * unit.circle.radius * (unit.curHp / unit.hp),
                    HealthBar.DEBUG_HEALTH_BAR_HEIGHT);
        }
        unitShapeRenderer.setColor(Color.BLACK);
        for (UnitBase unit : units) {
            unitShapeRenderer.rect(
                    unit.circle.x - unit.circle.radius + 2 * unit.circle.radius * (unit.curHp / unit.hp),
                    unit.circle.y + unit.circle.radius + HealthBar.BUFFER_ABOVE_OWNER,
                    2 * unit.circle.radius * (1 - (unit.curHp / unit.hp)),
                    HealthBar.DEBUG_HEALTH_BAR_HEIGHT);
        }
        unitShapeRenderer.end();
    }

    public List<UnitBase> getIntersectingUnits(Vector2 point) {
        return units.stream()
                .filter(unit -> unit.circle.contains(point))
                .collect(Collectors.toList());
    }

    public List<UnitBase> getIntersectingUnits(Polygon box) {
        return units.stream()
                .filter(unit -> GameMathUtils.isCircleIntersectQuadrilateral(unit.circle, box))
                .collect(Collectors.toList());
    }

    public List<UnitBase> getUnitsInState(UnitState state) {
        return units.stream()
                .filter(unit -> unit.state == state)
                .collect(Collectors.toList());
    }

    public List<UnitBase> getUnitsAtNode(GroundNode node) {
        return units.stream()
                .filter(unit -> isUnitAtNode(unit, node))
                .collect(Collectors.toList());
    }

    public boolean isUnitAtNode(UnitBase unit, GroundNode node) {
        return unit.getMapCenterX() == node.x &&
                unit.getMapCenterY() == node.y;
    }

    @Override
    public void notify(GameObject gameObject, Notification notification) {
        if (gameObject.getGameObjectType() != GameObjectType.UNIT) {
            return;
        }

        switch (notification) {
            case CREATE:
                addUnit((UnitBase) gameObject);
                break;
            case DESTROY:
                removeUnit((UnitBase) gameObject);
                break;
            default:
                throw new RuntimeException("Unknown notification " + notification);
        }
    }
}
