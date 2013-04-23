package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
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
	
	public OrthoCamController (OrthographicCamera camera) {
		this.camera = camera;
	}
	
	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		
		if(GameScreen.boy.isdead)
			return true;
		
		x = MathUtils.clamp(x, 0, Gdx.graphics.getWidth());
		y = MathUtils.clamp(y, 0, Gdx.graphics.getHeight());
		
		if(touched) {
			
			float sizemod = 1.0f;
			if(GameScreen.boy.isBig)
				sizemod = (GameScreen.boy.normalBoy.getWidth() / 1.5f);
			else if(GameScreen.boy.isSmall)
				sizemod = (GameScreen.boy.normalBoy.getWidth() / 1.5f);
			
			int size = (int) (Resources.getInstance().boyTextures.getRegions().get(0).originalWidth * sizemod); 
			
//			if(!GameScreen.boy.isSplit) {
				if(screenOffsetX > 0) {
					if(x - screenOffsetX - (size/2) <= 0)
						x = (int) last.x;
					else if(x +(size/2 - screenOffsetX) >= Gdx.graphics.getWidth())
						x = (int) last.x;
				}
				else {
					if(x - ((size / 2) + screenOffsetX) < 0)
						x = (int) last.x;
					else if(x - screenOffsetX + (size / 2) >= Gdx.graphics.getWidth())
						x = (int) last.x;
				}
				
				if(screenOffsetY > 0) {
					if(y + ((size/2) + screenOffsetY) >= Gdx.graphics.getHeight() )
						y = (int) last.y;
					else if(y - ((size/2) - screenOffsetY) < 0)
						y = (int) last.y;
				}
				else {
					if(y + ((size/2) + screenOffsetY) >= Gdx.graphics.getHeight() )
						y = (int) last.y;
					else if(y + screenOffsetY - size / 2 < 0) {
						y = (int) last.y;
					}
				}
//			}
//			else {
//				TODO: splitboy stuff wieder reinnehmen? sodass keiner der splitboys rausrutschen kann? dann müsste man aber touch wieder neu machen....	
//				
//				Vector3 proj = new Vector3(GameScreen.boy.splitBoy1, GameScreen.boy.splitBoy1, 0);
//				proj.rotate(Vector3.Z, GameScreen.boyRotation);
//				int fromX = (int) (GameScreen.boy.boyBounds.x);
//				int fromY = (int) (GameScreen.boy.boyBounds.y);
//				proj.add(fromX, fromY, 0);
//				camera.project(proj);
//				
//				int x1 = (int) proj.x;
//				int y1 = (int) proj.y;
//				
//				System.out.println(proj.toString());
//				
//				if(screenOffsetX > 0) {
//					if(x1 + ((Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2) - screenOffsetX) < Resources.getInstance().boyTextures.getRegions().get(0).originalWidth)
//						x+=20;
//					else if(x1 + ((Resources.getInstance().boyTextures.getRegions().get(0).originalWidth) - screenOffsetX) >= Gdx.graphics.getWidth() + SCREEN_BIAS_X)
//						x+=20;
//				}
//				else {
//					if(x1 - ((Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2) + screenOffsetX) < 0)
//						x+=20;
//					else if(x1 - screenOffsetX + (Resources.getInstance().boyTextures.getRegions().get(0).originalWidth / 2) >= Gdx.graphics.getWidth())
//						x+=20;
//				}
//				
//				if(screenOffsetY > 0) {
//					if(y1 + ((Resources.getInstance().boyTextures.getRegions().get(0).originalHeight) + screenOffsetY) >= Gdx.graphics.getHeight() + SCREEN_BIAS_Y)
//						y = (int) last.y;
//					else if(y1 + ((Resources.getInstance().boyTextures.getRegions().get(0).originalHeight) + screenOffsetY) < Resources.getInstance().boyTextures.getRegions().get(0).originalHeight + SCREEN_BIAS_Y)
//						y = (int) last.y;
//				}
//				else {
//					if(y1 + ((Resources.getInstance().boyTextures.getRegions().get(0).originalHeight) + screenOffsetY) >= Gdx.graphics.getHeight() + SCREEN_BIAS_Y)
//						y = (int) last.y;
//					else if(y1 + screenOffsetY - Resources.getInstance().boyTextures.getRegions().get(0).originalHeight / 2 < 0) {
//						y = (int) last.y;
//					}
//				}
//			}
			
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
		if(GameScreen.boy.isBig)
			w *= 2;
		else if(GameScreen.boy.isSmall)
			w /= 2;
		w += 0.2f * w;
		float h = Resources.getInstance().boyTextures.getRegions().get(0).originalHeight;
		if(GameScreen.boy.isBig)
			h *= 2;
		else if(GameScreen.boy.isSmall)
			h /= 2;
		h += 0.2f * h;
		
		Rectangle box = new Rectangle(pos.x, pos.y, w,h);
		
		if(box.contains(x,y) && !GameScreen.boy.isdead) {
			touched = true;
			
			float sizemod = 1.0f;
			if(GameScreen.boy.isBig)
				sizemod = 1 + GameScreen.boy.normalBoy.getWidth() - 1.5f;
			else if(GameScreen.boy.isSmall)
				sizemod = 1.5f - GameScreen.boy.normalBoy.getWidth();
			
			float centerX = normalBoy.getX() + (normalBoy.getWidth() * GameScreen.boy.sizeModifier) / 2;
			float screenCenterX = box.x + (Resources.getInstance().boyTextures.getRegions().get(0).originalWidth * sizemod) / 2;
			float centerY = normalBoy.getY() - (normalBoy.getHeight() * GameScreen.boy.sizeModifier)/2;
			float screenCenterY = box.y + (Resources.getInstance().boyTextures.getRegions().get(0).originalHeight * sizemod) / 2;
			
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
