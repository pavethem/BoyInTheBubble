package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class OrthoCamController extends InputAdapter {

	final OrthographicCamera camera;
	final Vector3 curr = new Vector3();
	final Vector2 last = new Vector2(0, 0);
	final Vector2 delta = new Vector2();
	
	float offsetX = 0;
	float screenOffsetX = 0;
	float offsetY = 0;
	float screenOffsetY = 0;
	
	boolean touched = false;
	
	private Rectangle normalBoy;
	
	private int SCREEN_BIAS_X;
	private int SCREEN_BIAS_Y;
	
	public OrthoCamController (OrthographicCamera camera) {
		this.camera = camera;
		SCREEN_BIAS_X = (int) (Gdx.graphics.getWidth() / 40);
		SCREEN_BIAS_Y = (int) (Gdx.graphics.getHeight() / 20);
	}
	
	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		
		if(GameScreen.boy.isdead)
			return true;
		
		x = MathUtils.clamp(x, 0, Gdx.graphics.getWidth());
		y = MathUtils.clamp(y, 0, Gdx.graphics.getHeight());
		
		if(touched) {
			
			//TODO: bias definieren, da er oben und rechts manchmal noch rausrutscht...
			
			if(screenOffsetX > 0) {
				if(x + ((Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2) - screenOffsetX) < Resources.getInstance().boyTextures.getRegions().get(0).originalWidth)
					x = (int) last.x;
				else if(x + ((Resources.getInstance().boyTextures.getRegions().get(0).originalWidth) - screenOffsetX) >= Gdx.graphics.getWidth() + SCREEN_BIAS_X)
					x = (int) last.x;
			}
			else {
				if(x - ((Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2) + screenOffsetX) < 0)
					x = (int) last.x;
				else if(x - screenOffsetX + (Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2) >= Gdx.graphics.getWidth())
					x = (int) last.x;
			}
			
			if(screenOffsetY > 0) {
				if(y + ((Resources.getInstance().boyTextures.getRegions().get(0).originalHeight) + screenOffsetY) >= Gdx.graphics.getHeight() + SCREEN_BIAS_Y)
					y = (int) last.y;
				else if(y + ((Resources.getInstance().boyTextures.getRegions().get(0).originalHeight) + screenOffsetY) < Resources.getInstance().boyTextures.getRegions().get(0).originalHeight + SCREEN_BIAS_Y)
					y = (int) last.y;
			}
			else {
				if(y + ((Resources.getInstance().boyTextures.getRegions().get(0).originalHeight) + screenOffsetY) >= Gdx.graphics.getHeight() + + SCREEN_BIAS_Y)
					y = (int) last.y;
				else if(y + screenOffsetY - Resources.getInstance().boyTextures.getRegions().get(0).originalHeight / 2 < 0) {
					y = (int) last.y;
				}
			}
			
			Vector3 newPos = new Vector3(x, y,0);		
			camera.unproject(newPos);
			
			normalBoy = GameScreen.boy.normalBoy.getBoundingRectangle();
				
			newPos.x -= GameScreen.boy.getOrigin().x;
			newPos.y += GameScreen.boy.getOrigin().y;
			
			
			GameScreen.boy.normalBoy.setPosition(newPos.x  - offsetX, newPos.y  - offsetY);
			last.set(x,y);
			
		}
		
		return true;
	}
	
	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		touched = false;
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
		
		float w = Resources.getInstance().boyTextures.getRegions().get(0).originalWidth;
		w += 0.1f * w;
		float h = Resources.getInstance().boyTextures.getRegions().get(0).originalHeight;
		h += 0.1f * h;
		
		Rectangle box = new Rectangle(pos.x, pos.y, w,h);
		
		if(box.contains(x,y) && !GameScreen.boy.isdead) {
			touched = true;
			
			float centerX = normalBoy.getX() + normalBoy.getWidth() / 2;
			float screenCenterX = box.x + Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2;
			float centerY = normalBoy.getY() - normalBoy.getHeight()/2;
			float screenCenterY = box.y + Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2;
			
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
		}
		
		return true;
	}
	
	
}
