package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Boy {

	public TextureAtlas boyTex;
	public Array<Sprite> boySprites;
	public Sprite normalBoy;
	public Rectangle boyBounds;
	public Circle boundingCircle;
	public Animation death;
	public boolean isdead;
	public boolean isfinished;
	public boolean isSplit;
	public boolean isBig;
	public boolean isSmall;
	public float split_dist;
//	public float sizeModifier;
	
	public float collisionSize = 2.4f; //have a small margin before the bubble bursts (20%)
	public float originalSize = 3f; // macht 120px
	public float bubbleSize = 3.3f; // bisschen größer
	public float size = originalSize;
	public float targetSize = originalSize;
	public float smallSize = 1.5f;
	public float bigSize = 5f;
	
	public final float SPLIT_DISTANCE = 1.9f;
//	public final float MAX_GROWTH_MOD = 1.03f;
//	public final float MAX_SHRINK_MOD = 1.03f;
	
	public boolean hasTail = false;
	//Max tail positions
	public final int MAX_POSITIONS = 10;
	//Max distace between two tail positions
	public final float MAX_DISTANCE = 0.7f;
	public Array<Vector3> positions = new Array<Vector3>(MAX_POSITIONS);
	
	Bubble2D bubble;
	
	//bubbles for tail positions
	public Array<Bubble2D> tailBubbles = new Array<Bubble2D>(MAX_POSITIONS);
	
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
		
		boundingCircle = new Circle(-getCorrectedPosition().x, -getCorrectedPosition().y, collisionSize / 2);
	}

	//returns middle position of the sprite, not the actual position
	public Vector3 getCorrectedPosition() {
		return new Vector3(-normalBoy.getX() - normalBoy.getOriginX(),-normalBoy.getY() - normalBoy.getOriginY(),0);
	}
	
	public Vector3 getPosition() {
		return new Vector3(normalBoy.getX(),normalBoy.getY(),0);
	}
	
	public float getSize() {
		return size * (1/GameScreen.tiled.getUnitScale());
	}

	public float getOriginalSize() {
		return originalSize * (1/GameScreen.tiled.getUnitScale());
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
			targetSize = bigSize;
		else
			targetSize = originalSize;

		return isBig;
	}
	
	public boolean shrink() {
		isSmall = !isSmall;
		isBig = false;
		
		if(isSmall) {
			targetSize = smallSize;
		}
		else {
			targetSize = originalSize;
		}
		
		return isSmall;
	}
	
	public boolean tail() {
		hasTail = !hasTail;
		positions.clear();
		tailBubbles.clear();
		
		return hasTail;
	}
	
	public void updateTail(Vector3 position) {
		if(positions.size < MAX_POSITIONS) {
			positions.insert(0, position);
			Bubble2D tempBubble = new Bubble2D(bubbleSize, position);
			tailBubbles.insert(0, tempBubble);
		}
		else {
			positions.pop();
			tailBubbles.pop();
			positions.insert(0, position);
			Bubble2D tempBubble = new Bubble2D(bubbleSize, position);
			tailBubbles.insert(0, tempBubble);
		}
		
		for (int i = 0; i < tailBubbles.size; i++) {
			Bubble2D b = tailBubbles.get(i);
			Vector3 pos = positions.get(i);
			b.updateTarget(-pos.x - size/2, -pos.y - size/2);		
		}
		
		//clamp distance between tail positions
		if(positions.size == MAX_POSITIONS) {
			for(int i=0;i<MAX_POSITIONS;i++) {
				if(i+1==MAX_POSITIONS)
					break;
				
				Vector3 current = positions.get(i).cpy();
				Vector3 next = positions.get(i+1).cpy();
				
				if(current.dst(next) >= MAX_DISTANCE) {
				
					System.out.println(i + " to " + String.valueOf(i+1) + ":");
					System.out.println(current.dst(next));
					
					//a+(b-a)
					Vector3 direction = next.sub(current);
					direction.clamp(0, MAX_DISTANCE);
					System.out.println(direction);
					positions.get(i+1).set(current.add(direction));
				}
				
			}
		}
	}
	
}
