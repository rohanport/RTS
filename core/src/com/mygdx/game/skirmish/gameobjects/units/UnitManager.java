package com.mygdx.game.skirmish.gameobjects.units;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.SkirmishScreen;
import com.mygdx.game.skirmish.gameobjects.*;
import com.mygdx.game.skirmish.gameplay.Commandable;
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
public class UnitManager implements GameObjectsObserver, GameObjectManager<UnitBase> {

    private final SkirmishScreen screen;

    private SpriteBatch unitRenderer;
    private ShapeRenderer debugRenderer;

    private List<UnitBase> units;

    // Getters and setters
    public List<UnitBase> getUnits() {
        return units;
    }

    //---------------------------------------------

    public UnitManager(SkirmishScreen screen) {
        this.screen = screen;

        unitRenderer = new SpriteBatch();
        debugRenderer = new ShapeRenderer();

        units = new ArrayList<>();
    }

    public void update(float delta) {
        units.forEach(unit -> unit.update(delta));
    }

    @Override
    public void add(UnitBase unit) {
        units.add(unit);
    }

    @Override
    public void remove(UnitBase unit) {
        units.remove(unit);
    }

    @Override
    public GameObjectType getObjectType() {
        return GameObjectType.UNIT;
    }

    @Override
    public void render(boolean debug) {
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

    /**
     * Units are rendered as circles using a ShapeRenderer instead of using sprites
     */
    private void renderUnitsDebug() {
        debugRenderer.setProjectionMatrix(screen.getCam().combined);

        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (UnitBase unit : units) {
            debugRenderer.setColor(screen.getPlayerManager().getPlayerByID(unit.getPlayerID()).color);
            debugRenderer.circle(unit.circle.x, unit.circle.y, unit.circle.radius);
        }
        debugRenderer.setColor(Color.GREEN);
        for (UnitBase unit : units) {
            debugRenderer.rect(
                    unit.circle.x - unit.circle.radius,
                    unit.circle.y + unit.circle.radius + HealthBar.BUFFER_ABOVE_OWNER,
                    2 * unit.circle.radius * (unit.curHp / unit.hp),
                    HealthBar.DEBUG_HEALTH_BAR_HEIGHT);
        }
        debugRenderer.setColor(Color.BLACK);
        for (UnitBase unit : units) {
            debugRenderer.rect(
                    unit.circle.x - unit.circle.radius + 2 * unit.circle.radius * (unit.curHp / unit.hp),
                    unit.circle.y + unit.circle.radius + HealthBar.BUFFER_ABOVE_OWNER,
                    2 * unit.circle.radius * (1 - (unit.curHp / unit.hp)),
                    HealthBar.DEBUG_HEALTH_BAR_HEIGHT);
        }
        debugRenderer.end();
    }

    public List<Builder> getBuilderUnitsInState(UnitState state) {
        return units.stream()
                .filter(unit -> unit instanceof Builder)
                .filter(unit -> unit.state == state)
                .map(Builder.class::cast)
                .collect(Collectors.toList());
    }

    public List<Gatherer> getGatherersInState(UnitState state) {
        return units.stream()
                .filter(unit -> unit instanceof Gatherer)
                .filter(unit -> unit.state == state)
                .map(Gatherer.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitBase> getIntersecting(Vector2 point) {
        return units.stream()
                .filter(unit -> unit.circle.contains(point))
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitBase> getIntersecting(Polygon box) {
        return units.stream()
                .filter(unit -> GameMathUtils.isCircleIntersectQuadrilateral(unit.circle, box))
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

    public List<UnitBase> getUnitsInState(UnitState state) {
        return units.stream()
                .filter(unit -> unit.state == state)
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitBase> getAtNode(GroundNode node) {
        return units.stream()
                .filter(unit -> isUnitAtNode(unit, node))
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitBase> getEnemiesInRange(int playerID, float x, float y, float radius) {
        Circle range = new Circle(x, y, radius);
        return units.stream()
                .filter(unit -> range.contains(unit.getCenterX(), unit.getCenterY()))
                .filter(unit -> screen.getPlayerManager().areEnemies(playerID, unit.getPlayerID()))
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
                add((UnitBase) gameObject);
                break;
            case DESTROY:
                remove((UnitBase) gameObject);
                break;
            default:
                throw new RuntimeException("Unknown notification " + notification);
        }
    }
}
