package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen implements ApplicationListener {
	//cam used for tile stuff
	private OrthographicCamera camera;
	//cam stays on boy
	private OrthographicCamera boyCam;
	static OrthogonalTiledMapRenderer tiled;
	private SpriteBatch batch;
	private SpriteBatch fontBatch;
	private BitmapFont font;
	
	private TiledMapTileLayer layer;

	//for dying animation
	private float stateTime;
	
	public static Boy boy;
	public static Boy splitBoy1;
	public static Boy splitBoy2;
	
	Spawner spawner;
	
	Sprite middle;
	
	Sprite blackFade;
	SpriteBatch fadeBatch;
	
//	float startTime = 0;
	float fade = 1.0f;
	boolean finished = false;

	float delta;
	
	Matrix4 temp;
	Matrix4 model;
	
	//Touch controller
	OrthoCamController camController;
	
	Vector3 lastPosition;
	public static Vector3 lastPositionBeforeDeath; 
	static float boyRotation = 0;
	float splitRotation = 0;
	final float ROTATION_MULTIPLIER = 20;
	
	public static Wormhole worm;
	public static Wormhole mirrorWorm;
	public static boolean wormHoleAffected;
	
	public static CollisionDetector collisionDetector;
	
//	public static World world;
//	Box2DDebugRenderer debugRenderer;
	
	ShapeRenderer r;
	
	@Override
	public void create() {		
		Configuration.getInstance().setConfiguration();
		
		r = new ShapeRenderer();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		temp = new Matrix4();
		model = new Matrix4();
		
		camera = new OrthographicCamera(1, h/w);
		boyCam = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		fontBatch = new SpriteBatch();
		font = Resources.getInstance().font;
		font.setScale(1);
		font.setColor(Color.RED);
		
		//measurement is 1 tile = 40x40 px
		tiled = new OrthogonalTiledMapRenderer(Resources.getInstance().map,1/40f);
		//32x20 tiles on the screen
		camera.setToOrtho(false, 32, 20);
		boyCam.setToOrtho(false, 32, 20);
		tiled.setView(camera);
		
		boy = new Boy(camera.viewportWidth, camera.viewportHeight,true);
		splitBoy1 = new Boy(camera.viewportWidth, camera.viewportHeight,true);
		splitBoy2 = new Boy(camera.viewportWidth, camera.viewportHeight,true);
		
		camController = new OrthoCamController(boyCam);
		Gdx.input.setInputProcessor(camController);
		
//		world.setContactListener(boy.bubble.contactListener);
		
		middle = new Sprite(Resources.getInstance().middle);
		middle.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		middle.setSize(0.5f, 0.5f);
		middle.setOrigin(middle.getWidth()/2, middle.getHeight()/2);
		middle.setPosition(w/4,h/2);		
		
		blackFade = new Sprite(
				new Texture(Gdx.files.internal("data/black.png")));
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		
		layer = (TiledMapTileLayer) tiled.getMap().getLayers().get(0);
		
		spawner = new Spawner(tiled.getMap().getLayers().get("movables"));
		
//		tiled.getMap().getTileSets().getTileSet(0).getTile(1).getTextureRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		stateTime = 0;
		finished = false;
		fade = 1.0f;
		
		lastPosition = new Vector3(0, 0, 0);
		
		lastPositionBeforeDeath = new Vector3(0, 0, 0);
		boyRotation = 0;
		splitRotation = 0;
		collisionDetector = new CollisionDetector(tiled.getMap().getLayers());
		
		worm = new Wormhole(new Vector3(-10,-10,-10),true);
		mirrorWorm = new Wormhole(new Vector3(-10,-10,-10),true);
		wormHoleAffected = false;
		
		Gdx.gl20.glLineWidth(10);
	}

	@Override
	public void dispose() {
		batch.dispose();
		boy.dispose();
		tiled.dispose();
		fadeBatch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		//Debugging
		fontBatch.begin();
		font.draw(fontBatch, Gdx.graphics.getFramesPerSecond() + " fps", 10, 20);
		fontBatch.end();
		
		delta = Math.min(0.1f, Gdx.graphics.getDeltaTime());	
//		world.step(delta, 60, 20);
//		world.clearForces();
		
		//debug circles of comets
//		r.begin(ShapeType.Line);
//		for(Circle c : collisionDetector.circles) {
//			r.circle(c.x * 40, c.y * 40, c.radius * 40);
//		}
//		r.end();
		
		if(!boy.isdead) {
			if(!boy.isSplit && splitBoy1.split_dist <= 0) {
				//rotate boy
				boyRotation -= delta * ROTATION_MULTIPLIER;
				splitRotation = boyRotation;
			} else {
				splitRotation -= delta * ROTATION_MULTIPLIER;
			}
//			camera.translate(delta * 2, 0);
//			camera.update();
			tiled.setView(camera);
//			debugRenderer.render(world, boyCam.combined.cpy().scale(Constants.PIXELS_PER_METER, Constants.PIXELS_PER_METER, 0));
		}
		tiled.render();
		
		
		if(!boy.isSplit && splitBoy1.split_dist <= delta) {
			// render bubble
			r.begin(ShapeType.Line);
			r.setColor(0, 0, 0, 1);
			for (int i = 0; i < boy.bubble.grads.size - 1; i++) {
				Vector3 temp1 = new Vector3();
				temp1.set(boy.bubble.grads.get(i).x, boy.bubble.grads.get(i).y,
						0);
				boyCam.project(temp1);
				Vector3 temp2 = new Vector3();
				temp2.set(boy.bubble.grads.get(i + 1).x,
						boy.bubble.grads.get(i + 1).y, 0);
				boyCam.project(temp2);
	
				r.line(temp1, temp2);
	
				if (i + 1 == boy.bubble.grads.size - 1) {
					temp1.set(boy.bubble.grads.get(0).x,
							boy.bubble.grads.get(0).y, 0);
					boyCam.project(temp1);
					r.line(temp2, temp1);
				}
			}
			r.end();
		}
		
		if(boy.isSplit) {
			splitBoy1.normalBoy.setRotation(boyRotation);
			splitBoy2.normalBoy.setRotation(boyRotation);
			
			if(splitBoy1.split_dist <= splitBoy1.SPLIT_DISTANCE) {
				splitBoy1.normalBoy.setPosition(splitBoy1.normalBoy.getX(),splitBoy1.normalBoy.getY() + delta);
				splitBoy1.split_dist += delta;
			}
			if(splitBoy2.split_dist >= -splitBoy2.SPLIT_DISTANCE) {
				splitBoy2.normalBoy.setPosition(splitBoy2.normalBoy.getX(),splitBoy2.normalBoy.getY() - delta);
				splitBoy2.split_dist -= delta;
			}
		} else {
			if(splitBoy1.split_dist >= 0) {
				splitBoy1.normalBoy.setPosition(splitBoy1.normalBoy.getX(),splitBoy1.normalBoy.getY() - delta);
				splitBoy1.split_dist -= delta;
			}
			if(splitBoy2.split_dist <= 0) {
				splitBoy2.normalBoy.setPosition(splitBoy2.normalBoy.getX(),splitBoy2.normalBoy.getY() + delta);
				splitBoy2.split_dist += delta;
			}
		}
		
		collisionDetector.collisionCheck(boy, tiled.getViewBounds());
		spawner.update(tiled.getViewBounds().x+tiled.getViewBounds().getWidth());
		
		if(!boy.isdead) {
			
			//DEBUG BOY POSITION
//			Vector3 yob = boy.getPosition().cpy();
//			yob.x += tiled.getViewBounds().x;
//			yob.z = 0;
//			camera.project(yob);
//			r.begin(ShapeType.Line);
//			r.setColor(1,0,0,1);
//			r.circle(yob.x -40, yob.y -40, 2);
//			r.end();
			
			Vector3 position = boy.getCorrectedPosition();
			
			//shrink in size
			if (boy.size > boy.targetSize) {

				boy.normalBoy.scale(-delta);
				boy.size = boy.normalBoy.getScaleX() * boy.originalSize;
				//80% of actual size
				boy.collisionSize = (boy.size * 80) / 100;

				//bubble is a little bigger than boy
				boy.bubbleSize = boy.size + 0.3f;
				boy.bubble.scale(boy.bubbleSize);
				
				boy.boundingCircle.setRadius(boy.collisionSize/2);

//				boy.normalBoy.setOrigin(boy.size / 2, boy.size / 2);
//				float offset = (boy.size - boy.targetSize) /2;
//				offset/=20;
//				System.out.println(offset);
//				boy.normalBoy.setPosition(boy.normalBoy.getX()+offset, boy.normalBoy.getY()+offset);
			}
			//grow in size
			else if(boy.size < boy.targetSize) {
				boy.normalBoy.scale(delta);
				boy.size = boy.normalBoy.getScaleX() * boy.originalSize;
				//80% of actual size
				boy.collisionSize = (boy.size * 80) / 100;

				//bubble is a little bigger than boy
				boy.bubbleSize = boy.size + 0.3f;
				boy.bubble.scale(boy.bubbleSize);
				
				boy.boundingCircle.setRadius(boy.collisionSize/2);
				
//				boy.normalBoy.setOrigin(boy.size / 2, boy.size / 2);
//				float offset = (boy.targetSize - boy.size) /2;
//				offset/=20;
//				System.out.println(offset);
//				boy.normalBoy.setPosition(boy.normalBoy.getX()-offset, boy.normalBoy.getY()-offset);
			}
			
			//correct measurements after growth/shrinking
			if(Math.abs(boy.targetSize-boy.size) < 0.1f) {
				boy.size = boy.targetSize;
				//80% of actual size
				boy.collisionSize = (boy.size * 80) / 100;
				
				boy.boundingCircle.setRadius(boy.collisionSize/2);
				
//				boy.normalBoy.setOrigin(boy.size / 2, boy.size / 2);
//				float offset = (boy.targetSize - boy.size) /2;
//				offset/=20;
//				System.out.println(offset);
//				boy.normalBoy.setPosition(boy.normalBoy.getX()-offset, boy.normalBoy.getY()-offset);
			}
			
			//normal boy // tail boy stuff
			if(!boy.isSplit && splitBoy1.split_dist <= 0) {
				
				boy.bubble.updateBubble(boy.getPosition().x,boy.getPosition().y);
				
				model.idt();
				temp.idt();
//				temp.setToScaling(boy.normalBoy.getScaleX(),boy.normalBoy.getScaleY(),0);
//				model.mul(temp);
				temp.setToTranslation(-position.x,-position.y,0);
				model.mul(temp);
				temp.setToRotation(Vector3.Z, boyRotation);
				model.mul(temp);
				temp.setToTranslation(position);
				model.mul(temp);
				
				if(boy.hasTail) {
					if(lastPosition.dst(boy.getCorrectedPosition()) > 0.5f) {
						boy.updateTail(boy.getCorrectedPosition());
						lastPosition.set(boy.getCorrectedPosition());
					}
				}
				
				//draw boy
				batch.setProjectionMatrix(boyCam.combined);
				batch.begin();
				batch.setTransformMatrix(model);
				
				boy.getCurrentFrame(0).draw(batch);
				batch.end();
				
				if(boy.hasTail) {
					
					//render tailboys (though not the first one)
//					for(int i=1;i<boy.positions.size;i++) {
//						Vector3 p = boy.positions.get(i);
//						model.idt();
//						temp.idt();
//						temp.setToTranslation(-p.x ,-p.y ,0);
//						model.mul(temp);
//						temp.setToRotation(Vector3.Z, boyRotation);
//						model.mul(temp);
//						temp.setToTranslation(position);
//						model.mul(temp);
//						batch.setProjectionMatrix(boyCam.combined);
//						batch.begin();
//						batch.setTransformMatrix(model);
//						boy.getCurrentFrame(0).draw(batch, 10 * (i+1));
//						batch.end();
//					}
					
					//render tailbubbles (though not the first one)
					
					Gdx.gl.glEnable(GL20.GL_BLEND);
					r.begin(ShapeType.Line);
					for(int j=1;j<boy.tailBubbles.size;j++) {
						
						Bubble2D b = boy.tailBubbles.get(j);
						
						float alpha = 1.0f / (j+1);

						r.setColor(0, 0, 0, alpha);
						
						for (int i = 0; i < b.grads.size - 1; i++) {
							Vector3 temp1 = new Vector3();
							temp1.set(b.grads.get(i).x, b.grads.get(i).y,
									0);
							boyCam.project(temp1);
							Vector3 temp2 = new Vector3();
							temp2.set(b.grads.get(i + 1).x,
									b.grads.get(i + 1).y, 0);
							boyCam.project(temp2);
				
							r.line(temp1, temp2);
				
							if (i + 1 == b.grads.size - 1) {
								temp1.set(b.grads.get(0).x,
										b.grads.get(0).y, 0);
								boyCam.project(temp1);
								r.line(temp2, temp1);
							}
						}
						
					}
					r.end();
					Gdx.gl.glDisable(GL20.GL_BLEND);
				}
			}
			//split boy
			else if(boy.isSplit || splitBoy1.split_dist >= 0) {
				
				//render middle
				//NO IDEA WHY 1.2f but it works
				middle.setPosition(boy.getOrigin().x / 1.2f + boy.getPosition().x, boy.getOrigin().y / 1.2f + boy.getPosition().y);

				position.set(-middle.getX() - middle.getOriginX(), -middle.getY() - middle.getOriginY(), 0);
				model.idt();
				temp.idt();
				temp.setToTranslation(-position.x,-position.y,0);
				model.mul(temp);
				temp.setToRotation(Vector3.Z, splitRotation);
				model.mul(temp);
				temp.setToTranslation(position);
				model.mul(temp);			
				
				batch.setProjectionMatrix(boyCam.combined);
				batch.begin();
				batch.setTransformMatrix(model);
				middle.draw(batch);
				batch.end();
				
				//render splitboy1
				if(!splitBoy1.isdead) {
					
					position.set(splitBoy1.split_dist, splitBoy1.split_dist,0);
					position.rotate(Vector3.Z, splitRotation);
					//NO IDEA WHY *5
					position.add(-middle.getOriginX()*5+middle.getX(),-middle.getOriginY()*5 + middle.getY(),0);

					splitBoy1.boundingCircle.setPosition(splitBoy1.getPosition().x + splitBoy1.getOrigin().x,splitBoy1.getPosition().y + + splitBoy1.getOrigin().y);
					
					splitBoy1.normalBoy.setPosition(position.x,position.y);
					splitBoy1.boyBounds.setX(position.x);
					splitBoy1.boyBounds.setY(position.y);
			
					model.idt();
					temp.idt();
					
					batch.setProjectionMatrix(boyCam.combined);
					batch.begin();
					batch.setTransformMatrix(model);
					splitBoy1.getCurrentFrame(0).draw(batch);
					batch.end();
					splitBoy1.bubble.updateBubble(splitBoy1.getPosition().x,splitBoy1.getPosition().y);
				}
				
				//render splitboy2
				if(!splitBoy2.isdead) {
					position.set(splitBoy2.split_dist, splitBoy2.split_dist,0);
					position.rotate(Vector3.Z, splitRotation);
					//NO IDEA WHY *5
					position.add(-middle.getOriginX()*5+middle.getX(),-middle.getOriginY()*5 + middle.getY(),0);
					
					splitBoy2.boundingCircle.setPosition(splitBoy2.getPosition().x + splitBoy2.getOrigin().x,splitBoy2.getPosition().y + + splitBoy2.getOrigin().y);
		
					splitBoy2.normalBoy.setPosition(position.x,position.y);
					splitBoy2.boyBounds.setX(position.x);
					splitBoy2.boyBounds.setY(position.y);
					model.idt();
					temp.idt();
					
					batch.setProjectionMatrix(boyCam.combined);
					batch.begin();
					batch.setTransformMatrix(model);
					splitBoy2.getCurrentFrame(0).draw(batch);
					batch.end();
					splitBoy2.bubble.updateBubble(splitBoy2.getPosition().x,splitBoy2.getPosition().y);
				}
				
				boy.bubble.updateBubble(boy.getPosition().x,boy.getPosition().y);
				// render bubbles
				
				r.begin(ShapeType.Line);
				r.setColor(0, 0, 0, 1);
				if(!splitBoy1.isdead) {
					for (int i = 0; i < splitBoy1.bubble.grads.size - 1; i++) {
						Vector3 temp1 = new Vector3();
						temp1.set(splitBoy1.bubble.grads.get(i).x, splitBoy1.bubble.grads.get(i).y,
								0);
						boyCam.project(temp1);
						Vector3 temp2 = new Vector3();
						temp2.set(splitBoy1.bubble.grads.get(i + 1).x,
								splitBoy1.bubble.grads.get(i + 1).y, 0);
						boyCam.project(temp2);
	
						r.line(temp1, temp2);
	
						if (i + 1 == splitBoy1.bubble.grads.size - 1) {
							temp1.set(splitBoy1.bubble.grads.get(0).x,
									splitBoy1.bubble.grads.get(0).y, 0);
							boyCam.project(temp1);
							r.line(temp2, temp1);
						}
					}
				}
				if(!splitBoy2.isdead) {
					for (int i = 0; i < splitBoy2.bubble.grads.size - 1; i++) {
						Vector3 temp1 = new Vector3();
						temp1.set(splitBoy2.bubble.grads.get(i).x, splitBoy2.bubble.grads.get(i).y,
								0);
						boyCam.project(temp1);
						Vector3 temp2 = new Vector3();
						temp2.set(splitBoy2.bubble.grads.get(i + 1).x,
								splitBoy2.bubble.grads.get(i + 1).y, 0);
						boyCam.project(temp2);
	
						r.line(temp1, temp2);
	
						if (i + 1 == splitBoy2.bubble.grads.size - 1) {
							temp1.set(splitBoy2.bubble.grads.get(0).x,
									splitBoy2.bubble.grads.get(0).y, 0);
							boyCam.project(temp1);
							r.line(temp2, temp1);
						}
					}
				}
				r.end();
				
				//DEBUG SPLIT
//				Vector3 yob = boy.getPosition().cpy();
//				yob.x += tiled.getViewBounds().x + boy.normalBoy.getOriginX() ;
//				yob.y += boy.normalBoy.getOriginY() ;
//				yob.z = 0;
//				Vector3 mid = new Vector3(middle.getX(),middle.getY(),0);
//				mid.x += tiled.getViewBounds().x + middle.getOriginX();
//				mid.y += middle.getOriginY();
//				Vector3 from = new Vector3(splitBoy1.normalBoy.getX() + splitBoy1.normalBoy.getOriginX()+ tiled.getViewBounds().x, splitBoy1.normalBoy.getY() + splitBoy1.normalBoy.getOriginY() , 0);
//				Vector3 to = new Vector3(splitBoy2.normalBoy.getX() + splitBoy2.normalBoy.getOriginX()+ tiled.getViewBounds().x, splitBoy2.normalBoy.getY()+ splitBoy1.normalBoy.getOriginY() , 0);
//				camera.project(from);
//				camera.project(to);
//				camera.project(yob);
//				camera.project(mid);
//				r.begin(ShapeType.Line);
//				r.setColor(1,0,0,1);
//				r.line(from.x,from.y,to.x,to.y);
//				r.circle(from.x, from.y, 65);
//				r.circle(yob.x, yob.y, 2);
//				r.setColor(0,1,0,1);
//				r.circle(mid.x, mid.y, 2);
//				r.end();
			}
		}
		
		//play death animation of splitboy1
		if(splitBoy1.isdead) {
			stateTime += delta;
//			currentFrame = boy.getCurrentFrame(stateTime);
			splitBoy1.getCurrentFrame(stateTime).getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			Sprite deadBoy = new Sprite(splitBoy1.getCurrentFrame(stateTime));
			
			deadBoy.setSize(splitBoy1.boyBounds.width, splitBoy1.boyBounds.height);
			deadBoy.setPosition(lastPositionBeforeDeath.x,splitBoy1.getPosition().y);
			deadBoy.setOrigin(splitBoy1.getOrigin().x, splitBoy1.getOrigin().y);
			deadBoy.setRotation(boyRotation);
			model.idt();
			
			batch.begin();
			batch.setTransformMatrix(model);
			deadBoy.draw(batch);
			batch.end();
			
			if(splitBoy1.isfinished) {
				float y = splitBoy1.getPosition().y;
				y -= 0.25f;
				
				deadBoy.setRotation(boyRotation);
				model.idt();
				splitBoy1.normalBoy.setPosition(boy.getPosition().x, y);
				
				if(splitBoy1.getPosition().y < 0) {
					splitBoy1.dispose();
					
					if(splitBoy2.isdead)
						finished = true;
				}
			}
		}
		
		//play death animation of splitboy2
		if(splitBoy2.isdead) {
			stateTime += delta;
//			currentFrame = boy.getCurrentFrame(stateTime);
			splitBoy2.getCurrentFrame(stateTime).getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			Sprite deadBoy = new Sprite(splitBoy2.getCurrentFrame(stateTime));
			
			deadBoy.setSize(splitBoy2.boyBounds.width, splitBoy2.boyBounds.height);
			deadBoy.setPosition(lastPositionBeforeDeath.x,splitBoy2.getPosition().y);
			deadBoy.setOrigin(splitBoy2.getOrigin().x, splitBoy2.getOrigin().y);
			deadBoy.setRotation(boyRotation);
			model.idt();
			
			batch.begin();
			batch.setTransformMatrix(model);
			deadBoy.draw(batch);
			batch.end();
			
			if(splitBoy2.isfinished) {
				float y = splitBoy2.getPosition().y;
				y -= 0.25f;
				
				deadBoy.setRotation(boyRotation);
				model.idt();
				splitBoy2.normalBoy.setPosition(boy.getPosition().x, y);
				
				if(splitBoy2.getPosition().y < 0)  {
					splitBoy2.dispose();
					
					if(splitBoy1.isdead)
						finished = true;
				}
			}
		}
		
		if(boy.isdead && !finished && !boy.isSplit) {
			
			Vector3 pos = boy.getPosition();
			camera.project(pos);
			
			stateTime += delta;
//			currentFrame = boy.getCurrentFrame(stateTime);
			boy.getCurrentFrame(stateTime).getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			Sprite deadBoy = new Sprite(boy.getCurrentFrame(stateTime));
			
			deadBoy.setSize(boy.boyBounds.width, boy.boyBounds.height);
			deadBoy.setPosition(boy.getPosition().x,boy.getPosition().y);
			deadBoy.setOrigin(boy.getOrigin().x, boy.getOrigin().y);
			deadBoy.setRotation(boyRotation);
			model.idt();
			
			batch.begin();
			batch.setTransformMatrix(model);
			deadBoy.draw(batch);
			batch.end();
			
			//play animation and boy moves out of screen downwards
			if(boy.isfinished) {
				float y = boy.getPosition().y;
				y -= 0.25f;
				
				deadBoy.setRotation(boyRotation);
				model.idt();
				boy.normalBoy.setPosition(boy.getPosition().x, y);
				
				if(boy.getPosition().y < 0) 
					finished = true;
			}
			
		}
		
		if (finished) {
			fade = Math.min(fade + (delta), 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
			if (fade >= 1) {
				dispose();
				create();
			}
		}
		
		if (!finished && fade > 0) {
			fade = Math.max(fade - (delta), 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}
		
		//draw wormhole
		r.begin(ShapeType.Filled);
		r.setColor(Color.RED);
		r.circle(worm.position.x, worm.position.y, worm.radius);
		r.end();
		
		//draw mirrorworm
		r.begin(ShapeType.Filled);
		r.setColor(Color.BLUE);
		boyCam.project(mirrorWorm.position);
		r.circle(mirrorWorm.position.x, mirrorWorm.position.y, mirrorWorm.radius);
		r.end();
		
		Vector3 wurm = worm.position.cpy();
		//20 because of 20 rows
		Vector3 pos = new Vector3(boy.bubble.center.x,20-boy.bubble.center.y,0);
		
		boyCam.unproject(wurm);
		
		if(pos.dst(wurm) <= Constants.MAX_REPEL_DISTANCE) {
			wormHoleAffected = true;
			Vector3 wormPos = worm.position.cpy();
			boyCam.unproject(wormPos);
			boy.manipulateBoy(worm, wormPos);
		} else
			wormHoleAffected = false;
	}



	@Override
	public void resize(int width, int height) {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		temp = new Matrix4();
		model = new Matrix4();
		
		camera = new OrthographicCamera(1, h/w);
		boyCam = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		tiled = new OrthogonalTiledMapRenderer(Resources.getInstance().map,1/40f);
		camera.setToOrtho(false, 32, 20);
		boyCam.setToOrtho(false, 32, 20);
		tiled.setView(camera);
		boy.normalBoy.setOrigin(boy.normalBoy.getWidth()/2, boy.normalBoy.getHeight()/2);
		boy.normalBoy.setPosition(camera.viewportWidth / 4, camera.viewportHeight / 2);
		middle.setOrigin(middle.getWidth()/2, middle.getHeight()/2);
		middle.setPosition(width/4,height/2);	
		
		camController = new OrthoCamController(boyCam);
		Gdx.input.setInputProcessor(camController);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
