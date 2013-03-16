package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;

public class Configuration {
	
	public Preferences preferences;
	public boolean fullscreen;
	public float brighness = 0.0f;
	public boolean debug = true;
	
	public final String TAG = "de.redlion.BoyInTheBubble";
	
	static Configuration instance;
	
	private Configuration() {
		preferences = Gdx.app.getPreferences("Boy In The Bubble");
		loadConfig();
	}
	
	private void loadConfig() {
		fullscreen = preferences.getBoolean("fullscreen", false);
	}
	
	public void setConfiguration() {
		if(Gdx.app.getType() == ApplicationType.Desktop) {
			if(fullscreen) {
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
			} else {
				Gdx.graphics.setDisplayMode(1000,600, false);		
			}
		}
	}
	
	public void setFullscreen(boolean onOff) {
		preferences.putBoolean("fullscreen", onOff);
		fullscreen = onOff;
		preferences.flush();
	}
		
	public static Configuration getInstance() {
		if(instance!=null) return instance;
		instance = new Configuration();		
		return instance;
	}	
	


}
