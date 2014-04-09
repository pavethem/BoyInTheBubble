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
	Array<Vector2> circles;
	Circle boundingCircle;
	
	float dist;
	
	private float camWidth = Gdx.graphics.getWidth() / (1/GameScreen.tiled.getUnitScale());
	private float camHeight = Gdx.graphics.getHeight() / (1/GameScreen.tiled.getUnitScale());
	
	public Bubble2D(float distance, Vector3 pos) {
		
		distance/=2;
		dist = distance;
		
		circles = new Array<Vector2>();
		
		center = new Vector2(-pos.x, -pos.y);
		
		for(int i=0;i<NUM_SEGMENTS;i++){
	        float x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * distance;
	        float y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * distance;
	        
	        Vector2 position = new Vector2(center.cpy().add(x, y));
	        circles.add(position);
		}
		
		boundingCircle = new Circle(center, dist);
		
	}
	
	public void updateTarget(float x, float y) {
		
		x += GameScreen.boy.getOrigin().x;
		y += GameScreen.boy.getOrigin().y;
		
		center.set(x, y);
		boundingCircle.setPosition(center);
		
		for(int i=0;i<NUM_SEGMENTS;i++){
			boolean toucheswall = false;
			
			boolean touchesleftright = false;
			if(circles.get(i).x <= 0.1f || circles.get(i).x >= camWidth -0.1f) {
				touchesleftright = true;
				toucheswall = true;
			}
			boolean touchestopbottom = false;
			if(circles.get(i).y <= 0.1f || circles.get(i).y >= camHeight-0.1f) {
				touchestopbottom = true;
				toucheswall = true;
			}
			
			if(toucheswall) {
				if(circles.get(i).dst(center) >= dist) {
					toucheswall = false;
					touchesleftright = false;
					touchestopbottom = false;
				}
			}
			
			x = MathUtils.cosDeg(360/NUM_SEGMENTS*i) * dist;
	        y = MathUtils.sinDeg(360/NUM_SEGMENTS*i) * dist;

			if(!toucheswall)
				circles.get(i).set(center.cpy().add(x,y));
			else {
				if(touchesleftright) {
					x = circles.get(i).x;
					y = center.cpy().add(x,y).y;
				}
				else if (touchestopbottom) {
					x = center.cpy().add(x,y).x;
					y = circles.get(i).y;
				}
				circles.get(i).set(x,y);
			}
		}
	}
}
