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
	
	Spawner spawner;
	int lastRow;
	Array<Circle> circles; 
	
	public CollisionDetector(MapLayers layers) {
		spawner = new Spawner(layers.get("movables"));
		lastRow = -1;
		
		circles = new Array<Circle>(layers.get("collisions").getObjects().getCount());
		
		for(MapObject c : layers.get("collisions").getObjects()) {
			
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
	
	public boolean bubbleCheck(Circle boundingCircle, Vector2 pos) {
		
		for (Circle c : circles) {
			if(boundingCircle.overlaps(c)) {
				if(c.contains(pos))
					return true;
			}
		}
		return false;
	}

	public void collisionCheck(Boy boy, TiledMapTileLayer layer, Rectangle viewBounds) {
		
		spawner.update(viewBounds.x+viewBounds.getWidth());

		if(!boy.isSplit) {
			int fromX = (int) (boy.boyBounds.x + viewBounds.x);
			int fromY = (int) (boy.boyBounds.y);
			int toX = (int) (boy.boyBounds.x + boy.boyBounds.width + viewBounds.x);
			int toY = (int) (boy.boyBounds.y + boy.boyBounds.height);
			
//			for (Circle c : circles) {
//				if(boy.boundingCircle.overlaps(c)) {
//					boy.isdead = false;
//				}
//			}
			
//			for (int i = 0; i < boy.bubble.grads.size; i++) {
//				if(boy.bubble.grads.get(i).dst(boy.bubble.center) < boy.collisionSize / 2)
//					boy.isdead = true;
//			}

			
//			Vector3 from = new Vector3(boy.boyBounds.x + viewBounds.x,boy.boyBounds.y,0);
//			Vector3 to = new Vector3(toX,toY,0);
//			
//							camera.project(from);
//							camera.project(to);
//
//							r.begin(ShapeType.Line);
//							r.setColor(1, 0, 0, 1);
//							r.rect(from.x, from.y, 240,240);
//							r.end();
			
//			for(int x = fromX; x<=toX;x++) {
//				for(int y = fromY; y<=toY;y++) {
//					if(layer.getCell(x, y) != null)
////						boy.isdead = true;
//						boy.isdead = false;
//				}
//			}
			
			if(boy.hasTail) {
				for(Vector3 p : boy.positions) {
					fromX = (int) (-p.x + viewBounds.x);
					fromY = (int) (-p.y);
					toX = (int) (-p.x - boy.getOrigin().x + boy.boyBounds.width + viewBounds.x);
					toY = (int) (-p.y - boy.getOrigin().y + boy.boyBounds.height);
					
					for(int x = fromX; x<=toX;x++) {
						for(int y = fromY; y<=toY;y++) {
							if(layer.getCell(x, y) != null)
								boy.isdead = true;
						}
					}
				}
			}
			
		}
		else {
			
			{
				//check GameScreen.splitBoy1
				if(!GameScreen.splitBoy1.isdead) {
					int fromX = (int) (GameScreen.splitBoy1.boyBounds.x + viewBounds.x);
					int fromY = (int) (GameScreen.splitBoy1.boyBounds.y);
					int toX = (int) (GameScreen.splitBoy1.boyBounds.x + GameScreen.splitBoy1.boyBounds.width + viewBounds.x);
					int toY = (int) (GameScreen.splitBoy1.boyBounds.y + GameScreen.splitBoy1.boyBounds.height);
					
					for(int x = fromX; x<=toX;x++) {
						for(int y = fromY; y<=toY;y++) {
							if(layer.getCell(x, y) != null) {
								GameScreen.splitBoy1.isdead = true;
								GameScreen.lastPositionBeforeDeath = GameScreen.splitBoy1.getPosition();
							}
						}
					}
				}
			}
			{
				//check GameScreen.splitBoy2
				if(!GameScreen.splitBoy2.isdead) {
					int fromX = (int) (GameScreen.splitBoy2.boyBounds.x + viewBounds.x);
					int fromY = (int) (GameScreen.splitBoy2.boyBounds.y);
					int toX = (int) (GameScreen.splitBoy2.boyBounds.x + GameScreen.splitBoy2.boyBounds.width + viewBounds.x);
					int toY = (int) (GameScreen.splitBoy2.boyBounds.y + GameScreen.splitBoy2.boyBounds.height);
					
	//				Vector3 from = new Vector3(GameScreen.splitBoy2.split_dist, GameScreen.splitBoy2.split_dist,0);
	//				Vector3 to = new Vector3(GameScreen.splitBoy2.split_dist, GameScreen.splitBoy2.split_dist,0);
	//				
	//				from.rotate(Vector3.Z, boyRotation);
	//				to.rotate(Vector3.Z, boyRotation);
	//				
	//				from.add(fromX, fromY, 0);
	//				to.add(toX , toY , 0);
	//				
	//				fromX = (int) from.x;
	//				fromY = (int) from.y;
	//				toX = (int) to.x;
	//				toY = (int) to.y;
					
	//				camera.project(from);
	//				camera.project(to);
	//				r.begin(ShapeType.Line);
	//				r.setColor(1, 0, 0, 1);
	//				r.rect(from.x, from.y, to.x - from.x, to.y - from.y);
	//				r.line(from.x, from.y, to.x, to.y);
	//				r.end();
					
			//		Gdx.app.log("", ""+ (int) newPos.x  + " " + (int) (newPos.y) );
			//		Gdx.app.log("", layer.getCell((int) newPos.x , (int)(newPos.y)) + "");
			//		Gdx.app.log("", "" + (int) boy.x + " " + (int)boy.y + " " + (int)boy.width + " " + (int)boy.height);
			//		Gdx.app.log("", "" + fromX + " " + fromY + " " + toX + " " + toY + " " + middleX + " " + middleY);
					
			//		Gdx.app.log("", viewBounds.toString());
					
					for(int x = fromX; x<=toX;x++) {
						for(int y = fromY; y<=toY;y++) {
							if(layer.getCell(x, y) != null) {
								GameScreen.splitBoy2.isdead = true;
								GameScreen.lastPositionBeforeDeath = GameScreen.splitBoy2.getPosition();
							}
						}
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
