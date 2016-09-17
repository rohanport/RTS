package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.menu.MenuScreen;

public class TestGame extends Game {
	
	@Override
	public void create () {
		setScreen(new MenuScreen(this));
	}
}
