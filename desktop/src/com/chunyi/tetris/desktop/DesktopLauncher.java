package com.chunyi.tetris.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chunyi.tetris.TetrisGame;

public class DesktopLauncher {


	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "The Tetris TetrisGame";

		config.width = 1;
		config.height = 1;

		config.resizable = false;
		config.vSyncEnabled = true;

		config.backgroundFPS = 60;
		config.foregroundFPS = 60;

		new LwjglApplication(new TetrisGame(), config);

	}


}
