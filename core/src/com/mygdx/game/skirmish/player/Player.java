package com.mygdx.game.skirmish.player;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class Player {
    public final Color color;
    public final int id;

    public int food = 200;

    public Player(int id, Color color) {
        this.color = color;
        this.id = id;
    }
}
