package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements ApplicationListener {
	private OrthographicCamera camera;
	private OrthographicCamera boyCam;
	private OrthogonalTiledMapRenderer tiled;
	private SpriteBatch batch;
	private TextureAtlas boyTex;
	private Array<Sprite> boy;
	public static Sprite normalBoy;
	private Sprite deadBoy;
	private Rectangle boyBounds;
	private TiledMapTileLayer layer;
	private Animation death;
	private float stateTime;
	private TextureRegion currentFrame;
	
	Sprite blackFade;
	SpriteBatch fadeBatch;
	
	public static boolean dead;
	
	float startTime = 0;
	float fade = 1.0f;
	boolean finished = false;

	float delta;
	
	Matrix4 temp;
	Matrix4 model;
	
	OrthoCamController camController;
	
	float boyRotation = 0;
	final float ROTATION_MULTIPLIER = 20;
	ShapeRenderer r;
	
	@Override
	public void create() {		
		r = new ShapeRenderer();
		Configuration.getInstance().setConfiguration();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		temp = new Matrix4();
		model = new Matrix4();
		
		camera = new OrthographicCamera(1, h/w);
		boyCam = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		tiled = new OrthogonalTiledMapRenderer(Resources.getInstance().map,1/32f);
		camera.setToOrtho(false, 20, 12);
		boyCam.setToOrtho(false, 20, 12);
		tiled.setView(camera);
		
		camController = new OrthoCamController(boyCam);
		Gdx.input.setInputProcessor(camController);
		
		boyTex = Resources.getInstance().boyTextures;
		boy = boyTex.createSprites();
		
//		for(Sprite s : boy) {
//		
//			s.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
//			s.setSize(1.5f, 1.5f);
//			s.setOrigin(s.getWidth()/2, s.getHeight()/2);
//			s.setPosition(camera.viewportWidth / 2, camera.viewportHeight / 2);
//		}
		
		blackFade = new Sprite(
				new Texture(Gdx.files.internal("data/black.png")));
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		
		death = new Animation(0.06f, boyTex.getRegions());
		death.setPlayMode(Animation.NORMAL);
		
		normalBoy = boy.get(0);
		
		normalBoy.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalBoy.setSize(1.5f, 1.5f);
		normalBoy.setOrigin(normalBoy.getWidth()/2, normalBoy.getHeight()/2);
		normalBoy.setPosition(camera.viewportWidth / 4, camera.viewportHeight / 2);
		
		deadBoy = normalBoy;
		
		boyBounds = normalBoy.getBoundingRectangle();
		
		layer = (TiledMapTileLayer) Resources.getInstance().map.getLayers().getLayer(0);
		
		tiled.getMap().getTileSets().getTileSet(0).getTile(1).getTextureRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		dead = false;
		
		stateTime = 0;
		finished = false;
		fade = 1.0f;
	}

	@Override
	public void dispose() {
		batch.dispose();
		boyTex.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		delta = Math.min(0.1f, Gdx.graphics.getDeltaTime());
		
		if(!dead)
			boyRotation -= Gdx.graphics.getDeltaTime() * ROTATION_MULTIPLIER;
		Vector3 position = new Vector3(-normalBoy.getX() - normalBoy.getOriginX(),-normalBoy.getY() - normalBoy.getOriginY(),0);
		
		if(!dead) {
			camera.translate(Gdx.graphics.getDeltaTime() * 2, 0);
			camera.update();
			tiled.setView(camera);
		}
		tiled.render();
		
		int fromX = (int) (boyBounds.x + tiled.getViewBounds().x);
		int fromY = (int) (boyBounds.y);
		int toX = (int) (boyBounds.x + boyBounds.width + tiled.getViewBounds().x);
		int toY = (int) (boyBounds.y + boyBounds.height);
		int middleX = (fromX + toX) /2;
		int middleY = (fromY + toY) /2;
		
//		Gdx.app.log("", ""+ (int) newPos.x  + " " + (int) (newPos.y) );
//		Gdx.app.log("", layer.getCell((int) newPos.x , (int)(newPos.y)) + "");
//		Gdx.app.log("", "" + (int) boy.x + " " + (int)boy.y + " " + (int)boy.width + " " + (int)boy.height);
//		Gdx.app.log("", "" + fromX + " " + fromY + " " + toX + " " + toY + " " + middleX + " " + middleY);
		
//		Gdx.app.log("", tiled.getViewBounds().toString());
		
		if(layer.getCell(fromX, fromY) != null)
			dead = true;
		else if(layer.getCell(toX, fromY) != null)
			dead = true;
		else if(layer.getCell(fromX, toY) != null)
			dead = true;
		else if(layer.getCell(toX, toY) != null)
			dead = true;
		else if(layer.getCell(middleX, toY) != null)
			dead = true;
		else if(layer.getCell(toX, middleY) != null)
			dead = true;
		else if(layer.getCell(middleX, middleY) != null)
			dead = true;
		
		if(!dead) {
			model.idt();
			temp.idt();
			temp.setToTranslation(-position.x,-position.y,0);
			model.mul(temp);
			temp.setToRotation(Vector3.Z, boyRotation);
			model.mul(temp);
			temp.setToTranslation(position);
			model.mul(temp);
			
			batch.setProjectionMatrix(boyCam.combined);
			batch.begin();
			batch.setTransformMatrix(model);
			normalBoy.draw(batch);
			batch.end();
		}
		
		if(dead && !finished) {
			
			Vector3 pos = new Vector3(normalBoy.getX(),normalBoy.getY(),0);
			camera.project(pos);
			
			stateTime += Gdx.graphics.getDeltaTime();
			currentFrame = death.getKeyFrame(stateTime, false);
			currentFrame.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			deadBoy = boy.get(death.getKeyFrameIndex(stateTime));
			
			deadBoy.setSize(1.5f, 1.5f);
			deadBoy.setPosition(normalBoy.getX(), normalBoy.getY());
			deadBoy.setOrigin(normalBoy.getOriginX(), normalBoy.getOriginY());
			deadBoy.setRotation(boyRotation);
			model.idt();
			
			batch.begin();
			batch.setTransformMatrix(model);
			deadBoy.draw(batch);
			batch.end();
			
			if(death.isAnimationFinished(stateTime)) {
				float y = normalBoy.getY();
				y -= 0.25f;
				
				deadBoy.setRotation(boyRotation);
				model.idt();
				normalBoy.setPosition(normalBoy.getX(), y);
				
				if(normalBoy.getY() < 0) 
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
		normalBoy.setOrigin(normalBoy.getWidth()/2, normalBoy.getHeight()/2);
		normalBoy.setPosition(camera.viewportWidth / 4, camera.viewportHeight / 2);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
