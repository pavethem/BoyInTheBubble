package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
//	public float sizeModifier;
	
	public float originalSize = 3f; // macht 120px
	public float bubbleSize = 3.3f; // bisschen größer
	public int size = 120;
	public int targetSize = 120;
	public float smallSize = 1.5f;
	public float bigSize = 5f;
	
	public final float SPLIT_DISTANCE = 1.9f;
//	public final float MAX_GROWTH_MOD = 1.03f;
//	public final float MAX_SHRINK_MOD = 1.03f;
	
	public boolean hasTail = false;
	public final int MAX_POSITIONS = 10;
	public Array<Vector3> positions = new Array<Vector3>(MAX_POSITIONS);
	
	Bubble2D bubble;
	
	public Boy(float width, float height, boolean createBubble) {
		
		boyTex = Resources.getInstance().boyTextures;
		boySprites = boyTex.createSprites();
		
		death = new Animation(0.06f, boyTex.getRegions());
		death.setPlayMode(Animation.NORMAL);
		
		normalBoy = boySprites.get(4);
		
		normalBoy.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalBoy.setSize(originalSize, originalSize);
		normalBoy.setOrigin(normalBoy.getWidth()/2, normalBoy.getHeight()/2);
		normalBoy.setPosition(width / 4, height / 2);
		
		boyBounds = normalBoy.getBoundingRectangle();
		
		split_dist = 0;
//		sizeModifier = 1.0f;
		
		isdead = false;
		isfinished = false;
		isSplit = false;
		
		if(createBubble)
			bubble = new Bubble2D(bubbleSize, getCorrectedPosition());
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
			targetSize = 200;
		else
			targetSize = 120;

		return isBig;
	}
	
	public boolean shrink() {
		isSmall = !isSmall;
		isBig = false;
		
		if(isSmall) {
			targetSize = 60;
		}
		else {
			targetSize = 120;
		}
		
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
