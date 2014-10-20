package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
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
	
	public float interpolationFactor = 1.0f;
	
	public final float SPLIT_DISTANCE = 1.9f;
//	public final float MAX_GROWTH_MOD = 1.03f;
//	public final float MAX_SHRINK_MOD = 1.03f;
	
	public boolean hasTail = false;
	//Max tail positions
	public final int MAX_POSITIONS = 10;
	//Max distace between two tail positions
	public final float MAX_DISTANCE = 0.7f;
	public Array<Vector3> positions = new Array<Vector3>(MAX_POSITIONS);
	//used for wormholes
	public Vector3 tailDirection = new Vector3(0,0,0);
	
	Bubble2D bubble;
	
	//bubbles for tail positions
	public Array<Bubble2D> tailBubbles = new Array<Bubble2D>(MAX_POSITIONS);
	public boolean iswormHoleAffected;
	
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
			b.updateBubble(-pos.x - size/2, -pos.y - size/2);		
		}
		
		correctTail();
	}
	
	//clamp distance between tail positions
	public void correctTail() {
		if(positions.size == MAX_POSITIONS) {
			for(int i=0;i<MAX_POSITIONS;i++) {
				if(i+1==MAX_POSITIONS)
					break;
				
				Vector3 current = positions.get(i).cpy();
				Vector3 next = positions.get(i+1).cpy();
				if(current.dst(next) != MAX_DISTANCE) {
					//a+(b-a));
					Vector3 direction = next.sub(current);
					if(iswormHoleAffected)
						direction.set(tailDirection);

					direction.clamp(0, MAX_DISTANCE);
					positions.get(i+1).set(current.add(direction));
				}
				
			}
		}
	}
	
	//draw or repel boy
	public void manipulateBoy(Wormhole worm, Vector3 pos3D) {
		
		//20 because of number of rows
		Vector3 pos = new Vector3(bubble.center.x,20-bubble.center.y,0);
		Vector3 wormPos = pos3D.cpy();
		
		// b+(a+(b-a)) with some scaling because the directions are all wrong
		Vector3 direction = wormPos.sub(pos);
		direction.scl(1, -1, 1);
		Vector3 end = wormPos.add(direction);
		end.scl(-1);
		// clamp so it's the same distance
		end.clamp(Constants.MAX_REPEL_DISTANCE - pos.dst(pos3D.cpy()), Constants.MAX_REPEL_DISTANCE - pos.dst(pos3D.cpy()));
		Vector3 mirror = this.getCorrectedPosition().cpy().scl(-1);
		mirror.add(end);
		GameScreen.mirrorWorm.position.set(mirror);

		//reverse linear interpolation to get alpha
		Vector3 position = new Vector3(bubble.center.x,bubble.center.y,0);
		Vector3 wurm = pos3D.cpy();
		wurm.y = 20-wurm.y;
		Vector3 spiegel = mirror.cpy();

		float t  = (position.sub(spiegel).len()) / (wurm.sub(spiegel).len());

		interpolationFactor = 1-t;

		//linear interpolation towards mirror point
		if(interpolationFactor <= 1.0f) {
			interpolationFactor+=Constants.REPEL_VELOCITY;
			Vector3 newPos = new Vector3(bubble.center.x,20-bubble.center.y,0);
			pos3D.y = 20 - pos3D.y;
			pos3D.lerp(mirror, interpolationFactor);
			newPos.set(pos3D);
			newPos.sub(getOrigin().x, getOrigin().y, 0);
			
			//clamp so it doesn't move outside screen
			newPos.x = MathUtils.clamp(newPos.x,0,29);
			newPos.y = MathUtils.clamp(newPos.y,0,17);
			
			bubble.updateBubble(newPos.x,newPos.y);
			normalBoy.setPosition(newPos.x, newPos.y);
			boundingCircle.setPosition(newPos.x,newPos.y);
		}
	}
	
	//draw or repel tail
	public void manipulateTail(Wormhole worm, Vector3 pos3D) {
			
		Vector3 pos = new Vector3(bubble.center.x,20-bubble.center.y,0);
		Vector3 wormPos = pos3D.cpy();
		
		Vector3 direction = wormPos.sub(pos);
		direction.scl(1, -1, 1);
		tailDirection.set(direction);
		Vector3 end = wormPos.add(direction);
		end.scl(-1);
		end.clamp(Constants.MAX_REPEL_DISTANCE - pos.dst(pos3D.cpy()), Constants.MAX_REPEL_DISTANCE - pos.dst(pos3D.cpy()));
		Vector3 mirror = this.getCorrectedPosition().cpy().scl(-1);
		mirror.add(end);
		GameScreen.mirrorWorm.position.set(mirror);
		
		correctTail();

	}
	
}
