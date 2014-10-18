package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class CollisionDetector {
	
	int lastRow;
	//collisionobjects
	Array<Circle> circles; 
	
	public CollisionDetector(MapLayers layers) {
		lastRow = -1;
		
		circles = new Array<Circle>(layers.get("collisions").getObjects().getCount());
		
		for(MapObject c : layers.get("collisions").getObjects()) {
			
			//just produce some circles for easy collision detection
			if(c instanceof EllipseMapObject) {
				EllipseMapObject e = (EllipseMapObject) c;
				Ellipse el = e.getEllipse();
				
				float width = el.width;
				float x = el.x + width / 2;
				float y = el.y + width / 2;
				
				Circle ci = new Circle(x / (1/GameScreen.tiled.getUnitScale()), y / (1/GameScreen.tiled.getUnitScale()), el.width / (2/GameScreen.tiled.getUnitScale()));
				
				circles.add(ci);

			}
		}
	}
	
	//checks each position individually (for bubbly physics)
	public boolean bubbleCheck(Circle boundingCircle, Vector2 pos) {
		
		for (Circle c : circles) {
			if(boundingCircle.overlaps(c)) {
				if(c.contains(pos))
					return true;
			}
		}
		return false;
	}

	//check if bubble is stretches too thin -> collision
	public void collisionCheck(Boy boy, Rectangle viewBounds) {

		if(!boy.isSplit) {
			
			for (Circle c : circles) {
				if(boy.boundingCircle.overlaps(c)) {
					boy.isdead = true;
				}
			}
			
		}
		else {
				//check GameScreen.splitBoy1
				if(!GameScreen.splitBoy1.isdead) {

					for (Circle c : circles) {
						if(GameScreen.splitBoy1.boundingCircle.overlaps(c)) {
							GameScreen.lastPositionBeforeDeath = GameScreen.splitBoy1.getPosition();
							GameScreen.splitBoy1.isdead = true;
						}
					}
				}
				//check GameScreen.splitBoy2
				if(!GameScreen.splitBoy2.isdead) {
					
					for (Circle c : circles) {
						if(GameScreen.splitBoy2.boundingCircle.overlaps(c)) {
							GameScreen.lastPositionBeforeDeath = GameScreen.splitBoy2.getPosition();
							GameScreen.splitBoy2.isdead = true;
						}
					}					
				}
			}
		
		if(boy.isSplit && GameScreen.splitBoy1.split_dist <= GameScreen.splitBoy1.SPLIT_DISTANCE) {
			boy.isdead = false;
			GameScreen.splitBoy1.isdead = false;
			GameScreen.splitBoy2.isdead = false;
		}
		if(!boy.isSplit && GameScreen.splitBoy1.split_dist > 0) {
			boy.isdead = false;
			GameScreen.splitBoy1.isdead = false;
			GameScreen.splitBoy2.isdead = false;
		}
		
		if(GameScreen.splitBoy1.isdead && GameScreen.splitBoy2.isdead)
			boy.isdead = true;
		
//		boy.isdead = false;
		
	}
	
}
