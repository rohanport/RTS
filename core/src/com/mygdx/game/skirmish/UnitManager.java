package com.mygdx.game.skirmish;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.skirmish.units.UnitBase;

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

    private List<UnitBase> units;

    public UnitManager(SkirmishScreen screen) {
        this.screen = screen;

        unitRenderer = new SpriteBatch();

        units = new ArrayList<UnitBase>();
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
        for (UnitBase unit: units) {
            unit.render(unitRenderer);
        }
        unitRenderer.end();
    }

}
