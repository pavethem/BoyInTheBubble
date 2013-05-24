package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.math.Rectangle;

public class Spawner {
	
	public Rectangle area;
	
	public Spawner(int width, int height) {
		
		area = new Rectangle(width, 0, 1, height);
		
	}
	
}
