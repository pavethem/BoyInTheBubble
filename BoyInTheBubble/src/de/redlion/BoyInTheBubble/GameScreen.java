package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen implements ApplicationListener {
	private OrthographicCamera camera;
	private OrthographicCamera boyCam;
	static OrthogonalTiledMapRenderer tiled;
	private SpriteBatch batch;
	
	private TiledMapTileLayer layer;

	private float stateTime;
	
	public static Boy boy;
	public static Boy splitBoy1;
	public static Boy splitBoy2;
	
	Sprite middle;
	
	Sprite blackFade;
	SpriteBatch fadeBatch;
	
	float startTime = 0;
	float fade = 1.0f;
	boolean finished = false;

	float delta;
	
	Matrix4 temp;
	Matrix4 model;
	
	OrthoCamController camController;
	
	Vector3 lastPosition;
	public static Vector3 lastPositionBeforeDeath; 
	static float boyRotation = 0;
	float splitRotation = 0;
	final float ROTATION_MULTIPLIER = 20;
	
	public static CollisionDetector collisionDetector;
	
	public static World world;
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
		
		tiled = new OrthogonalTiledMapRenderer(Resources.getInstance().map,1/40f);
		camera.setToOrtho(false, 32, 20);
		boyCam.setToOrtho(false, 32, 20);
		tiled.setView(camera);


		
//		world = new World(new Vector2(0,0), true);
//		debugRenderer = new Box2DDebugRenderer();
		
//		BodyDef walls = new BodyDef();
//		walls.type = BodyType.StaticBody;
//		walls.position.set(0,0);
		
//		Body wallBody = world.createBody(walls);
		
		boy = new Boy(camera.viewportWidth, camera.viewportHeight,true);
		splitBoy1 = new Boy(camera.viewportWidth, camera.viewportHeight,false);
		splitBoy2 = new Boy(camera.viewportWidth, camera.viewportHeight,false);
		
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
		
//		tiled.getMap().getTileSets().getTileSet(0).getTile(1).getTextureRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		stateTime = 0;
		finished = false;
		fade = 1.0f;
		
//		wallBody.setUserData("wall");
//		ChainShape borders = new ChainShape();
//		Vector2[] vertices = {new Vector2(0,0),new Vector2(camera.viewportWidth / Constants.PIXELS_PER_METER,0),
//				new Vector2(camera.viewportWidth/Constants.PIXELS_PER_METER,camera.viewportHeight/Constants.PIXELS_PER_METER),
//				new Vector2(0,camera.viewportHeight/Constants.PIXELS_PER_METER)};
//		borders.createLoop(vertices);
//		borders.setRadius(0);
//		wallBody.createFixture(borders,100f).setFriction(0);
//		borders.dispose();
		
		lastPosition = new Vector3(0, 0, 0);
		
		lastPositionBeforeDeath = new Vector3(0, 0, 0);
		boyRotation = 0;
		splitRotation = 0;
		collisionDetector = new CollisionDetector(tiled.getMap().getLayers());
		
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
		
		collisionDetector.collisionCheck(boy, layer, tiled.getViewBounds());
		
		if(!boy.isdead) {
			
			Vector3 position = boy.getCorrectedPosition();
			
			if (boy.size > boy.targetSize) {
				boy.size -= Gdx.graphics.getDeltaTime();
				if(boy.isSmall)
					boy.normalBoy.setSize(((float)boy.size/boy.targetSize)*boy.smallSize, ((float)boy.size/boy.targetSize)*boy.smallSize);
				else
					boy.normalBoy.setSize(((float)boy.size/boy.targetSize)*boy.originalSize, ((float)boy.size/boy.targetSize)*boy.originalSize);
				
				boy.normalBoy.setOrigin(boy.normalBoy.getWidth()/2,boy.normalBoy.getHeight()/2 );
			}
			else if(boy.size < boy.targetSize) {
				boy.size += Gdx.graphics.getDeltaTime() * 100;
				if(boy.isBig)
					boy.normalBoy.setSize(((float)boy.size/boy.targetSize)*boy.bigSize, ((float)boy.size/boy.targetSize)*boy.bigSize);
				else
					boy.normalBoy.setSize(((float)boy.size/boy.targetSize)*boy.originalSize, ((float)boy.size/boy.targetSize)*boy.originalSize);
				
				boy.normalBoy.setOrigin(boy.normalBoy.getWidth()/2,boy.normalBoy.getHeight()/2 );
			}
			
			if(!boy.isSplit && splitBoy1.split_dist <= 0) {
				model.idt();
				temp.idt();
//				temp.setToScaling(boy.targetSize/boy.size, boy.targetSize/boy.size, 0);
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
				
				batch.setProjectionMatrix(boyCam.combined);
				batch.begin();
				batch.setTransformMatrix(model);
				
				boy.getCurrentFrame(0).draw(batch);
				batch.end();
				
				if(boy.hasTail) {
					for(Vector3 p : boy.positions) {
						model.idt();
						temp.idt();
						temp.setToTranslation(-p.x ,-p.y ,0);
						model.mul(temp);
						temp.setToRotation(Vector3.Z, boyRotation);
						model.mul(temp);
						temp.setToTranslation(position);
						model.mul(temp);
						batch.setProjectionMatrix(boyCam.combined);
						batch.begin();
						batch.setTransformMatrix(model);
						boy.getCurrentFrame(0).draw(batch, 10 * (boy.positions.indexOf(p, false) + 1));
						batch.end();
					}
				}
			}
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
				}
				
				//render splitboy2
				if(!splitBoy2.isdead) {
					position.set(splitBoy2.split_dist, splitBoy2.split_dist,0);
					position.rotate(Vector3.Z, splitRotation);
					//NO IDEA WHY *5
					position.add(-middle.getOriginX()*5+middle.getX(),-middle.getOriginY()*5 + middle.getY(),0);
		
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
				}
				
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
//				r.circle(yob.x, yob.y, 2);
//				r.setColor(0,1,0,1);
//				r.circle(mid.x, mid.y, 2);
//				r.end();
			}
		}
		
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
