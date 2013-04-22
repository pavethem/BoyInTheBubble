package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Boy {

	public TextureAtlas boyTex;
	public Array<Sprite> boySprites;
	public Sprite normalBoy;
	public Rectangle boyBounds;
	public Animation death;
	public boolean isdead;
	public boolean isfinished;
	public boolean isSplit;
	public boolean isBig;
	public boolean isSmall;
	public float split_dist;
//	public float splitBoy2;
	public final float SPLIT_DISTANCE = 1.4f;
	
	public Boy(float width, float height) {
		
		boyTex = Resources.getInstance().boyTextures;
		boySprites = boyTex.createSprites();
		
//		for(Sprite s : boy) {
//		
//			s.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
//			s.setSize(1.5f, 1.5f);
//			s.setOrigin(s.getWidth()/2, s.getHeight()/2);
//			s.setPosition(camera.viewportWidth / 2, camera.viewportHeight / 2);
//		}
		
		death = new Animation(0.06f, boyTex.getRegions());
		death.setPlayMode(Animation.NORMAL);
		
		normalBoy = boySprites.get(0);
		
		normalBoy.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalBoy.setSize(1.5f, 1.5f);
		normalBoy.setOrigin(normalBoy.getWidth()/2, normalBoy.getHeight()/2);
		normalBoy.setPosition(width / 4, height / 2);
		
		boyBounds = normalBoy.getBoundingRectangle();
		
		split_dist = 0;
		
		isdead = false;
		isfinished = false;
		isSplit = false;
	}

	public Vector3 getCorrectedPosition() {
		return new Vector3(-normalBoy.getX() - normalBoy.getOriginX(),-normalBoy.getY() - normalBoy.getOriginY(),0);
	}
	

	public Vector3 getPosition() {
		return new Vector3(normalBoy.getX(),normalBoy.getY(),0);
	}
	

	public Vector2 getOrigin() {
		return new Vector2(normalBoy.getOriginX(),normalBoy.getOriginY());
	}

	public Sprite getCurrentFrame(float statetime) {
		
		if(death.isAnimationFinished(statetime))
			isfinished = true;
		
		if(!isdead)
			return normalBoy;
		else
			return new Sprite(death.getKeyFrame(statetime));
	}

	public void dispose() {
		boySprites.clear();
	}
	
	public boolean split() {
		
		isSplit = !isSplit;
		
		return isSplit;
		
	}
	
	public boolean enlarge() {
		isBig = !isBig;
		isSmall = false;
		
		if(isBig) {
			normalBoy.setBounds(normalBoy.getX(), normalBoy.getY(), normalBoy.getWidth() * 2, normalBoy.getHeight() * 2);
			normalBoy.setOrigin(normalBoy.getOriginX() * 2, normalBoy.getOriginY() * 2);
			boyBounds.set(normalBoy.getBoundingRectangle());
		} else {
			normalBoy.setBounds(normalBoy.getX(), normalBoy.getY(), normalBoy.getWidth() / 2, normalBoy.getHeight() / 2);
			normalBoy.setOrigin(normalBoy.getOriginX() / 2, normalBoy.getOriginY() / 2);
			boyBounds.set(normalBoy.getBoundingRectangle());
		}
		
		return isBig;
	}
	
	public boolean shrink() {
		isSmall = !isSmall;
		isBig = false;
		
		if(isSmall) {
			normalBoy.setBounds(normalBoy.getX(), normalBoy.getY(), normalBoy.getWidth() / 2, normalBoy.getHeight() / 2);
			normalBoy.setOrigin(normalBoy.getOriginX() / 2, normalBoy.getOriginY() / 2);
			boyBounds.set(normalBoy.getBoundingRectangle());
		} else {
			normalBoy.setBounds(normalBoy.getX(), normalBoy.getY(), normalBoy.getWidth() * 2, normalBoy.getHeight() * 2);
			normalBoy.setOrigin(normalBoy.getOriginX() * 2, normalBoy.getOriginY() * 2);
			boyBounds.set(normalBoy.getBoundingRectangle());
		}
		
		return isSmall;
	}
	
}
