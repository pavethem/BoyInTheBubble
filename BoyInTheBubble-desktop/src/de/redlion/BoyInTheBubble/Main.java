package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Boy In The Bubble";
		cfg.useGL20 = true;
		cfg.samples = 4;
		cfg.width = 480;
		cfg.height = 320;
		cfg.vSyncEnabled = true;
		cfg.foregroundFPS = 60;
		
		new LwjglApplication(new GameScreen(), cfg);
	}
}
