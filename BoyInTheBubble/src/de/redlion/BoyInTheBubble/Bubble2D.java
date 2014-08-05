package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Bubble2D {

	final int NUM_SEGMENTS = 24;
	
	Vector2 center;
	Array<Vector2> grads;
	Circle bubbleCircle;
	
	float dist;
	
	private float camWidth = Gdx.graphics.getWidth() / (1/GameScreen.tiled.getUnitScale());
	private float camHeight = Gdx.graphics.getHeight() / (1/GameScreen.tiled.getUnitScale());
	
	public Bubble2D(float distance, Vector3 pos) {
		
		distance/=2;
		dist = distance;
		
		grads = new Array<Vector2>();
		
		center = new Vector2(-pos.x, -pos.y);
		
		for(int i=0;i<NUM_SEGMENTS;i++){
	        float x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * distance;
	        float y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * distance;
	        
	        Vector2 position = new Vector2(center.cpy().add(x, y));
	        grads.add(position);
		}
		
		bubbleCircle = new Circle(center, dist);
		
	}
	
	public void scale(float newSize) {
		dist = newSize / 2;
		
		grads = new Array<Vector2>();	
		
		for(int i=0;i<NUM_SEGMENTS;i++){
	        float x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * dist;
	        float y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * dist;
	        
	        Vector2 position = new Vector2(center.cpy().add(x, y));
	        grads.add(position);
		}
		
		bubbleCircle = new Circle(center, dist);
		
	}
	
	public void updateTarget(float x, float y) {
		
		x += GameScreen.boy.getOrigin().x;
		y += GameScreen.boy.getOrigin().y;
		
		center.set(x, y);
		bubbleCircle.setPosition(center);
		
		for(int i=0;i<NUM_SEGMENTS;i++){
			boolean toucheswall = false;
			
			boolean touchesleftright = false;
			if(grads.get(i).x <= 0.1f || grads.get(i).x >= camWidth -0.1f) {
				touchesleftright = true;
				toucheswall = true;
			}
			boolean touchestopbottom = false;
			if(grads.get(i).y <= 0.1f || grads.get(i).y >= camHeight-0.1f) {
				touchestopbottom = true;
				toucheswall = true;
			}
			
			if(toucheswall) {
				if(grads.get(i).dst(center) >= dist) {
					toucheswall = false;
					touchesleftright = false;
					touchestopbottom = false;
				}
			}
			
			x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * dist;
	        y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * dist;
	        
	        float cx = grads.get(i).x;
	        float cy = grads.get(i).y;
	        
	        boolean collides = GameScreen.collisionDetector.bubbleCheck(bubbleCircle, center.cpy().add(x,y));
	        
			//check for collisions with objects
	        if(!collides) {
	        	grads.get(i).set(center.cpy().add(x,y));
			}

			if(toucheswall){
				if(touchesleftright) {
					x = cx;
					y = center.cpy().add(x,y).y;
				}
				else if (touchestopbottom) {
					x = center.cpy().add(x,y).x;
					y = cy;
				}
				grads.get(i).set(x,y);
			}
			
		}
	}
}
