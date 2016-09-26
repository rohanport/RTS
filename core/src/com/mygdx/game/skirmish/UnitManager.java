package com.mygdx.game.skirmish;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.units.UnitState;
import com.mygdx.game.skirmish.util.GameMathUtils;
import com.mygdx.game.skirmish.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by paddlefish on 18-Sep-16.
 *
 * Manages units in skirmish mode
 */
public class UnitManager {

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

    public void renderUnits() {
        Camera cam = screen.getCam();
        unitRenderer.setProjectionMatrix(cam.combined);

        unitRenderer.begin();
        units.forEach(unit -> unit.render(unitRenderer));
        unitRenderer.end();
    }

    public void renderUnitsDebug() {
        unitShapeRenderer.setProjectionMatrix(screen.getCam().combined);

        unitShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        unitShapeRenderer.setColor(Color.RED);
        units.forEach(unit -> unitShapeRenderer.circle(unit.circle.x, unit.circle.y, unit.circle.radius));
        unitShapeRenderer.end();
    }

    public List<UnitBase> getIntersectingUnits(final Polygon box) {
        List<UnitBase> intersectingUnits = new ArrayList<>();

        units.stream()
                .filter(unit -> GameMathUtils.isCircleIntersectQuadrilateral(unit.circle, box))
                .forEach(intersectingUnits::add);

        return intersectingUnits;
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
        return node.x * MapUtils.NODE_WIDTH_PX < unit.circle.x &&
                (node.x + 1) * MapUtils.NODE_WIDTH_PX >= unit.circle.x &&
                node.y * MapUtils.NODE_HEIGHT_PX < unit.circle.y &&
                (node.y + 1) * MapUtils.NODE_HEIGHT_PX >= unit.circle.y;
    }

}
