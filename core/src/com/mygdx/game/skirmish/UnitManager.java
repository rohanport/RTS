package com.mygdx.game.skirmish;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.util.GameMathUtils;

import java.util.ArrayList;
import java.util.List;

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

}
