package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class OrthoCamController extends InputAdapter {

	final OrthographicCamera camera;
	final Vector3 curr = new Vector3();
	final Vector2 last = new Vector2();
	float delta = 0f;
	
	float offsetX = 0;
	float screenOffsetX = 0;
	float offsetY = 0;
	float screenOffsetY = 0;
	
	boolean touched = false;
	
	private Rectangle normalBoy;
	
	public OrthoCamController (OrthographicCamera camera) {
		this.camera = camera;
		Vector3 pos = GameScreen.boy.getCorrectedPosition();
		camera.project(pos);
		last.set(-pos.x, Gdx.graphics.getHeight()+pos.y);
	}
	
	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		
		if(GameScreen.boy.isdead)
			return true;
			
		if(touched) {
			
			float size =  GameScreen.boy.size;
			
			int correctedX = (int) (x - size/2 - screenOffsetX);
			int correctedY = (int) (y + size/2 + screenOffsetY);
			
			correctedX = (int) MathUtils.clamp(correctedX, 0, Gdx.graphics.getWidth() - size);
			correctedY = (int) MathUtils.clamp(correctedY, size, Gdx.graphics.getHeight());
			
			Vector3 newPos = new Vector3(correctedX, correctedY,0);		
			camera.unproject(newPos);
			normalBoy = GameScreen.boy.normalBoy.getBoundingRectangle();
			
			GameScreen.boy.normalBoy.setPosition(newPos.x , newPos.y );
			GameScreen.boy.boundingCircle.setPosition(newPos.x + GameScreen.boy.getOrigin().x, newPos.y + GameScreen.boy.getOrigin().y);
			GameScreen.boy.bubble.updateTarget(newPos.x,newPos.y);
			
			delta = last.dst(new Vector2(x, y));
			last.set(x,y);
		}
		return true;
	}
	
	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		touched = false;
//		GameScreen.boy.bubble.destroyMouseJoint();
		return true;
	}
	
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
	
		Vector3 newPos = new Vector3(x, y,0);
		camera.unproject(newPos);

		y = -y + Gdx.graphics.getHeight();
		
		normalBoy = GameScreen.boy.normalBoy.getBoundingRectangle();
		
		Vector3 pos = new Vector3(normalBoy.x,normalBoy.y,0);
		
		camera.project(pos);
		
		float w = GameScreen.boy.size;

		float h = GameScreen.boy.size;
		
		Rectangle box = new Rectangle(pos.x, pos.y, w,h);
		
		if(box.contains(x,y) && !GameScreen.boy.isdead) {
			touched = true;
//			GameScreen.boy.bubble.createMouseJoint();
//			float sizemod = 1.0f;
//			if(GameScreen.boy.isBig)
//				sizemod = 1 + GameScreen.boy.normalBoy.getWidth() - 1.5f;
//			else if(GameScreen.boy.isSmall)
//				sizemod = 1.5f - GameScreen.boy.normalBoy.getWidth();
			
			float centerX = normalBoy.getX() + normalBoy.getWidth() / 2;
			float screenCenterX = box.x + (w) / 2;
			float centerY = normalBoy.getY() - normalBoy.getHeight()/2;
			float screenCenterY = box.y + (h) / 2;
			
			if(centerX > newPos.x) {
				offsetX = -centerX + newPos.x;
				screenOffsetX = -screenCenterX + x;
			}
			else {
				offsetX = newPos.x - centerX;
				screenOffsetX = x - screenCenterX;
			}
			if(centerY > newPos.y) {
				offsetY = centerY + newPos.y;
				screenOffsetY = screenCenterY + y;
			}
			else {
				offsetY = newPos.y - centerY;
				screenOffsetY = y - screenCenterY;
			}
			
			
			
			newPos.x -= GameScreen.boy.getOrigin().x;
			newPos.y += GameScreen.boy.getOrigin().y;
			
			GameScreen.boy.normalBoy.setPosition(newPos.x -offsetX , newPos.y -offsetY);
//			last.set(x,y);
		}
		
		return true;
	}
	
	@Override
	public boolean keyUp (int keycode) {
		
		if(keycode == Keys.S)
			GameScreen.boy.split();
		if(keycode == Keys.A)
			GameScreen.boy.enlarge();
		if(keycode == Keys.D)
			GameScreen.boy.shrink();
		if(keycode == Keys.W)
			GameScreen.boy.tail();
		return true;
	}
	
	
}
