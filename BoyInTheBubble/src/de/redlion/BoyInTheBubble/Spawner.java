package de.redlion.BoyInTheBubble;

import java.util.ArrayList;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public class Spawner {
	
	public MapLayer layer;
	
	public ArrayList<Rectangle> rectangles;
	private final float EPSILON = 0.1f;
	
	public Spawner(MapLayer layer) {
		
		this.layer = layer;
		
//		rectangles = new ArrayList<Rectangle>(layer.getObjects().getCount());
//
//		for(MapObject o : layer.getObjects()) {
//			if(o instanceof RectangleMapObject) {
//				RectangleMapObject ro = (RectangleMapObject) o;
//				Rectangle temp = ro.getRectangle();
//				
//				System.out.println(temp);
//				
//				temp.x *= GameScreen.tiled.getUnitScale();
//				
//				//fix y cause libgdx screws it up
//				
//				temp.y = 800 - temp.height - temp.y;
//				
//				
//				temp.y *= GameScreen.tiled.getUnitScale();
//				
//				rectangles.add(temp);
//			}
//		}
	}
	
	public void update(float x) {
		
//		
//		for(Rectangle r : rectangles) {
//			if(Math.abs(r.x - x) < EPSILON) {
//				rectangles.remove(r);
//				break;
//			}
//		}
		
	}
	
}
