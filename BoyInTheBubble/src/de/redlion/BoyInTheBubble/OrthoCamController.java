package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class OrthoCamController extends InputAdapter {

	final OrthographicCamera camera;
	final Vector2 last = new Vector2();
	final Vector3 last3D = new Vector3();
	float delta = 0f;
	
	//mouse offset from center of the bubble
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
			
			if(!GameScreen.boy.iswormHoleAffected || (GameScreen.boy.hasTail && GameScreen.boy.iswormHoleAffected)) {
				float size =  GameScreen.boy.getSize();
			
				//actual position (at the bottom left of the sprite)
				int correctedX = (int) (x - size/2 - screenOffsetX);
				int correctedY = (int) (y + size/2 + screenOffsetY);
	
				correctedX = (int) MathUtils.clamp(correctedX, 0, Gdx.graphics.getWidth() - size);
				correctedY = (int) MathUtils.clamp(correctedY, size, Gdx.graphics.getHeight());
				
				correctedX += (GameScreen.boy.getSize() - GameScreen.boy.getOriginalSize()) / 2;
				correctedY -= (GameScreen.boy.getSize() - GameScreen.boy.getOriginalSize()) / 2;
	
				
				Vector3 newPos = new Vector3(correctedX, correctedY,0);		
				camera.unproject(newPos);
				GameScreen.boy.normalBoy.setPosition(newPos.x , newPos.y );
				GameScreen.boy.boundingCircle.setPosition(newPos.x + GameScreen.boy.getOrigin().x, newPos.y + GameScreen.boy.getOrigin().y);
				GameScreen.boy.bubble.updateBubble(newPos.x,newPos.y);
			}
			
			delta = last.dst(new Vector2(x, y));
			last.set(x,y);
			last3D.set(x,y,0);
			camera.unproject(last3D);
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
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.app.getType().equals(ApplicationType.Desktop)) {
			
			normalBoy = GameScreen.boy.normalBoy.getBoundingRectangle();
			
			Vector3 pos = new Vector3(normalBoy.x,normalBoy.y,0);
			
			camera.project(pos);
			
			float w = GameScreen.boy.getSize();
			
			float h = GameScreen.boy.getSize();
			
			Rectangle box = new Rectangle(pos.x, pos.y, w,h);
			
			if(box.contains(x,y) && !GameScreen.boy.isdead) {
				touched = true;
				
				float centerX = GameScreen.boy.getPosition().x + GameScreen.boy.size / 2;
				float screenCenterX = box.x + (w / 2);
				float centerY = GameScreen.boy.getPosition().y - GameScreen.boy.size /2;
				float screenCenterY = box.y + (h / 2);
				
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
				
				newPos.x -= GameScreen.boy.size / 2;
				newPos.y += GameScreen.boy.size / 2;
				
				GameScreen.boy.normalBoy.setPosition(newPos.x -offsetX , newPos.y -offsetY);
				last.set(x,y);
				last3D.set(x,y,0);
				camera.unproject(last3D);
				
			}
		}	else if (Gdx.input.isButtonPressed(Buttons.RIGHT) && Gdx.app.getType().equals(ApplicationType.Desktop)) {
			camera.project(newPos);
			GameScreen.worm.position.set(newPos);
			
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
