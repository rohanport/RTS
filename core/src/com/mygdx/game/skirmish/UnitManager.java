package com.mygdx.game.skirmish;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.skirmish.units.Soldier1;
import com.mygdx.game.skirmish.units.UnitBase;
import com.mygdx.game.skirmish.units.UnitType;
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

    public UnitManager(SkirmishScreen screen) {
        this.screen = screen;

        unitRenderer = new SpriteBatch();
        unitShapeRenderer = new ShapeRenderer();

        units = new ArrayList<UnitBase>();
    }

    @Deprecated
    public void createUnit(UnitType unitType, float x, float y) {
        UnitBase unit;

        switch (unitType) {
            case SOLDIER1:
                unit = new Soldier1(x, y);
                break;
            default:
                throw new RuntimeException("UNHANDLED UNIT TYPE GIVEN: " + unitType);
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = screen.getWorld().createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(50f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture fixture = body.createFixture(fixtureDef);

        circle.dispose();

        addUnit(unit);
        screen.getSelectionManager().addToSelection(unit);
    }

    public void addUnit(UnitBase unit) {
        units.add(unit);
    }

    public void update(float delta) {
        for (UnitBase unit: units) {
            unit.update(delta);
        }
    }

    public void renderUnits() {
        Camera cam = screen.getCam();
        unitRenderer.setProjectionMatrix(cam.combined);

        unitRenderer.begin();
        for (UnitBase unit : units) {
            unit.render(unitRenderer);
        }
        unitRenderer.end();
    }

    public void renderUnitsDebug() {
        unitShapeRenderer.setProjectionMatrix(screen.getCam().combined);

        unitShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        unitShapeRenderer.setColor(Color.RED);
        for (UnitBase unit : units) {
            unitShapeRenderer.circle(unit.circle.x, unit.circle.y, unit.circle.radius);
        }
        unitShapeRenderer.end();
    }

    public List<UnitBase> getIntersectingUnits(final Polygon box) {
        List<UnitBase> intersectingUnits = new ArrayList<UnitBase>();

        for (UnitBase unit : units) {
            if (GameMathUtils.isCircleIntersectQuadrilateral(unit.circle, box)) {
                intersectingUnits.add(unit);
            }
        }

        return intersectingUnits;
    }

}
