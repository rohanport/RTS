package com.mygdx.game.skirmish.gameobjects.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Resources;
import com.mygdx.game.skirmish.World;

/**
 * Created by paddlefish on 18-Sep-16.
 */
public class Soldier1 extends UnitBase {

    public Soldier1(World world, int x, int y) {
        super(world, x, y, 1);

        hp = 100f;
        curHp = hp;

        atk = 10f;
        range = 6;

        baseSpeed = 100f;

        baseAtkStartup = 0.7f;
        baseAtkEnd = 0.3f;

        sprite = Resources.getInstance().soldier1;
        portrait = Resources.getInstance().soldier1Portrait;
    }

    @Override
    public boolean processKeyStroke(int keycode) {
        return false;
    }

    @Override
    public boolean processRightClick(int screenX, int screenY) {
        destNodeX = screenX;
        destNodeY = screenY;
        state = UnitState.MOVING;

        return false;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(sprite,
                circle.x - (circle.radius * 1.3f),
                circle.y - (circle.radius * 1.3f),
                circle.radius * 2 * 1.3f,
                4 * (circle.radius * 2 / 3f) * 1.3f
        );
    }
}
