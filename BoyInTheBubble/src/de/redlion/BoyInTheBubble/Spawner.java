package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public class Spawner {
	
	public MapLayer layer;
	
	private Rectangle[] rectangles;
	
	public Spawner(MapLayer layer) {
		
		this.layer = layer;
		
		rectangles = new Rectangle[layer.getObjects().getCount()];
		
		int i=0;
		for(MapObject o : layer.getObjects()) {
			if(o instanceof RectangleMapObject) {
				RectangleMapObject ro = (RectangleMapObject) o;
				rectangles[i] = ro.getRectangle();
				System.out.println(ro.getRectangle().y);
				i++;
			}
		}
	}
	
	public void update(float x) {
		
		for(Rectangle r : rectangles) {
//			if(r.x == x) {
//				System.out.println(r.y);
//			}
		}
		
	}
	
}
