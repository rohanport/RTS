package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.menu.MenuScreen;

public class RTSGame extends Game {
	
	@Override
	public void create () {
		setScreen(new MenuScreen(this));
	}
}
