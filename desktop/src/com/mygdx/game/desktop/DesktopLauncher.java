package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.RTSGame;
import com.mygdx.game.skirmish.util.Settings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 160 * 9;
		config.height = 90 * 9;
		config.resizable = false;
		config.fullscreen = Settings.FULLSCREEN_MODE;
		new LwjglApplication(new RTSGame(), config);
	}
}
