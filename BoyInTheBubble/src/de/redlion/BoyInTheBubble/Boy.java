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
	public float sizeModifier;
	
	public final float SPLIT_DISTANCE = 1.4f;
	public final float MAX_GROWTH_MOD = 1.03f;
	public final float MAX_SHRINK_MOD = 1.03f;
	
	public boolean hasTail = false;
	public final int MAX_POSITIONS = 10;
	public Array<Vector3> positions = new Array<Vector3>(MAX_POSITIONS);
	
	public Boy(float width, float height) {
		
		boyTex = Resources.getInstance().boyTextures;
		boySprites = boyTex.createSprites();
		
		death = new Animation(0.06f, boyTex.getRegions());
		death.setPlayMode(Animation.NORMAL);
		
		normalBoy = boySprites.get(0);
		
		normalBoy.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalBoy.setSize(1.5f, 1.5f);
		normalBoy.setOrigin(normalBoy.getWidth()/2, normalBoy.getHeight()/2);
		normalBoy.setPosition(width / 4, height / 2);
		
		boyBounds = normalBoy.getBoundingRectangle();
		
		split_dist = 0;
		sizeModifier = 1.0f;
		
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
		
		if(isBig)
			normalBoy.setSize(1.5f, 1.5f);
		return isBig;
	}
	
	public boolean shrink() {
		isSmall = !isSmall;
		isBig = false;
		
		if(isSmall)
			normalBoy.setSize(1.5f, 1.5f);
		
		return isSmall;
	}
	
	public boolean tail() {
		hasTail = !hasTail;
		positions.clear();
		return hasTail;
	}
	
	public void updateTail(Vector3 position) {
		if(positions.size < MAX_POSITIONS) {
			positions.insert(0, position);
		}
		else {
			positions.pop();
			positions.insert(0, position);
		}
	}
	
}
