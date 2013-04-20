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
	static OrthogonalTiledMapRenderer tiled;
	private SpriteBatch batch;
	
	private TiledMapTileLayer layer;

	private float stateTime;
	
	public static Boy boy;
	public Boy splitBoy1;
	public Boy splitBoy2;
	
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
	
	static float boyRotation = 0;
	float splitRotation = 0;
	final float ROTATION_MULTIPLIER = 20;
	
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
		
		tiled = new OrthogonalTiledMapRenderer(Resources.getInstance().map,1/32f);
		camera.setToOrtho(false, 20, 12);
		boyCam.setToOrtho(false, 20, 12);
		tiled.setView(camera);
		
		camController = new OrthoCamController(boyCam);
		Gdx.input.setInputProcessor(camController);
		
		boy = new Boy(camera.viewportWidth, camera.viewportHeight);
		splitBoy1 = new Boy(camera.viewportWidth, camera.viewportHeight);
		splitBoy2 = new Boy(camera.viewportWidth, camera.viewportHeight);
		
		middle = new Sprite(Resources.getInstance().middle);
		middle.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		middle.setSize(0.5f, 0.5f);
		middle.setOrigin(middle.getWidth()/2, middle.getHeight()/2);
		middle.setPosition(w/4,h/2);		
		
		blackFade = new Sprite(
				new Texture(Gdx.files.internal("data/black.png")));
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		
		layer = (TiledMapTileLayer) Resources.getInstance().map.getLayers().getLayer(0);
		
		tiled.getMap().getTileSets().getTileSet(0).getTile(1).getTextureRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		stateTime = 0;
		finished = false;
		fade = 1.0f;
		
		boyRotation = 0;
		splitRotation = 0;
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
		
		if(!boy.isdead) {
			if(!boy.isSplit && splitBoy1.split_dist <= 0) {
				boyRotation -= Gdx.graphics.getDeltaTime() * ROTATION_MULTIPLIER;
				splitRotation = boyRotation;
			} else {
				splitRotation -= Gdx.graphics.getDeltaTime() * ROTATION_MULTIPLIER;
			}
			camera.translate(Gdx.graphics.getDeltaTime() * 2, 0);
			camera.update();
			tiled.setView(camera);
		}
		tiled.render();
		
		if(boy.isSplit) {
			splitBoy1.normalBoy.setRotation(boyRotation);
			splitBoy2.normalBoy.setRotation(boyRotation);
			
			if(splitBoy1.split_dist <= splitBoy1.SPLIT_DISTANCE) {
				splitBoy1.normalBoy.setPosition(splitBoy1.normalBoy.getX(),splitBoy1.normalBoy.getY() + Gdx.graphics.getDeltaTime());
				splitBoy1.split_dist += Gdx.graphics.getDeltaTime();
			}
			if(splitBoy2.split_dist >= -splitBoy2.SPLIT_DISTANCE) {
				splitBoy2.normalBoy.setPosition(splitBoy2.normalBoy.getX(),splitBoy2.normalBoy.getY() - Gdx.graphics.getDeltaTime());
				splitBoy2.split_dist -= Gdx.graphics.getDeltaTime();
			}
		} else {
			if(splitBoy1.split_dist >= 0) {
				splitBoy1.normalBoy.setPosition(splitBoy1.normalBoy.getX(),splitBoy1.normalBoy.getY() - Gdx.graphics.getDeltaTime());
				splitBoy1.split_dist -= Gdx.graphics.getDeltaTime();
			}
			if(splitBoy2.split_dist <= 0) {
				splitBoy2.normalBoy.setPosition(splitBoy2.normalBoy.getX(),splitBoy2.normalBoy.getY() + Gdx.graphics.getDeltaTime());
				splitBoy2.split_dist += Gdx.graphics.getDeltaTime();
			}
		}
		
		collisionCheck();
		
		if(!boy.isdead) {
			
			Vector3 position = boy.getCorrectedPosition();
			
			if(!boy.isSplit && splitBoy1.split_dist <= 0) {
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
				boy.getCurrentFrame(0).draw(batch);
				batch.end();
			}
			else if(boy.isSplit || splitBoy1.split_dist >= 0) {
				
				//render middle
				middle.setPosition(boy.getOrigin().x / 2 + boy.getPosition().x, boy.getOrigin().y / 2 + boy.getPosition().y);

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
				position.set(splitBoy1.split_dist, splitBoy1.split_dist,0);
				position.rotate(Vector3.Z, splitRotation);
				position.add(-middle.getOriginX()*2+middle.getX(),-middle.getOriginY()*2 + middle.getY(),0);
	
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
				
				//render splitboy2
				position.set(splitBoy2.split_dist, splitBoy2.split_dist,0);
				position.rotate(Vector3.Z, splitRotation);
				position.add(-middle.getOriginX()*2+middle.getX(),-middle.getOriginY()*2 + middle.getY(),0);
	
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
//				r.circle(mid.x, mid.y, 2);
//				r.end();
			}
		}
		
		if(boy.isdead && !finished) {
			
			Vector3 pos = boy.getPosition();
			camera.project(pos);
			
			stateTime += Gdx.graphics.getDeltaTime();
//			currentFrame = boy.getCurrentFrame(stateTime);
			boy.getCurrentFrame(stateTime).getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			Sprite deadBoy = new Sprite(boy.getCurrentFrame(stateTime));
			
			deadBoy.setSize(1.5f, 1.5f);
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

	private void collisionCheck() {
		
		if(!boy.isSplit) {
			int fromX = (int) (boy.boyBounds.x + tiled.getViewBounds().x);
			int fromY = (int) (boy.boyBounds.y);
			int toX = (int) (boy.boyBounds.x + boy.boyBounds.width + tiled.getViewBounds().x);
			int toY = (int) (boy.boyBounds.y + boy.boyBounds.height);
			int middleX = (fromX + toX) /2;
			int middleY = (fromY + toY) /2;
			
			if(layer.getCell(fromX, fromY) != null)
				boy.isdead = true;
			else if(layer.getCell(toX, fromY) != null)
				boy.isdead = true;
			else if(layer.getCell(fromX, toY) != null)
				boy.isdead = true;
			else if(layer.getCell(toX, toY) != null)
				boy.isdead = true;
			else if(layer.getCell(middleX, toY) != null)
				boy.isdead = true;
			else if(layer.getCell(toX, middleY) != null)
				boy.isdead = true;
			else if(layer.getCell(middleX, middleY) != null)
				boy.isdead = true;
		}
		else {
			
			{
				//check splitboy1
				int fromX = (int) (splitBoy1.boyBounds.x + tiled.getViewBounds().x);
				int fromY = (int) (splitBoy1.boyBounds.y);
				int toX = (int) (splitBoy1.boyBounds.x + splitBoy1.boyBounds.width + tiled.getViewBounds().x);
				int toY = (int) (splitBoy1.boyBounds.y + splitBoy1.boyBounds.height);
				
				int middleX = (fromX + toX) /2;
				int middleY = (fromY + toY) /2;
				
				if(layer.getCell(fromX, fromY) != null)
					boy.isdead = true;
				else if(layer.getCell(toX, fromY) != null)
					boy.isdead = true;
				else if(layer.getCell(fromX, toY) != null)
					boy.isdead = true;
				else if(layer.getCell(toX, toY) != null)
					boy.isdead = true;
				else if(layer.getCell(middleX, toY) != null)
					boy.isdead = true;
				else if(layer.getCell(toX, middleY) != null)
					boy.isdead = true;
				else if(layer.getCell(middleX, middleY) != null)
					boy.isdead = true;
			}
			{
				//check splitboy2
				int fromX = (int) (splitBoy2.boyBounds.x + tiled.getViewBounds().x);
				int fromY = (int) (splitBoy2.boyBounds.y);
				int toX = (int) (splitBoy2.boyBounds.x + splitBoy2.boyBounds.width + tiled.getViewBounds().x);
				int toY = (int) (splitBoy2.boyBounds.y + splitBoy2.boyBounds.height);
				
//				Vector3 from = new Vector3(splitBoy2.split_dist, splitBoy2.split_dist,0);
//				Vector3 to = new Vector3(splitBoy2.split_dist, splitBoy2.split_dist,0);
//				
//				from.rotate(Vector3.Z, boyRotation);
//				to.rotate(Vector3.Z, boyRotation);
//				
//				from.add(fromX, fromY, 0);
//				to.add(toX , toY , 0);
//				
//				fromX = (int) from.x;
//				fromY = (int) from.y;
//				toX = (int) to.x;
//				toY = (int) to.y;
				
//				camera.project(from);
//				camera.project(to);
//				r.begin(ShapeType.Line);
//				r.setColor(1, 0, 0, 1);
//				r.rect(from.x, from.y, to.x - from.x, to.y - from.y);
//				r.line(from.x, from.y, to.x, to.y);
//				r.end();
				
				int middleX = (fromX + toX) /2;
				int middleY = (fromY + toY) /2;
				
		//		Gdx.app.log("", ""+ (int) newPos.x  + " " + (int) (newPos.y) );
		//		Gdx.app.log("", layer.getCell((int) newPos.x , (int)(newPos.y)) + "");
		//		Gdx.app.log("", "" + (int) boy.x + " " + (int)boy.y + " " + (int)boy.width + " " + (int)boy.height);
		//		Gdx.app.log("", "" + fromX + " " + fromY + " " + toX + " " + toY + " " + middleX + " " + middleY);
				
		//		Gdx.app.log("", tiled.getViewBounds().toString());
				
				if(layer.getCell(fromX, fromY) != null)
					boy.isdead = true;
				else if(layer.getCell(toX, fromY) != null)
					boy.isdead = true;
				else if(layer.getCell(fromX, toY) != null)
					boy.isdead = true;
				else if(layer.getCell(toX, toY) != null)
					boy.isdead = true;
				else if(layer.getCell(middleX, toY) != null)
					boy.isdead = true;
				else if(layer.getCell(toX, middleY) != null)
					boy.isdead = true;
				else if(layer.getCell(middleX, middleY) != null)
					boy.isdead = true;
			}

		}
		
		if(boy.isSplit && splitBoy1.split_dist <= splitBoy1.SPLIT_DISTANCE)
			boy.isdead = false;
		if(!boy.isSplit && splitBoy1.split_dist > 0)
			boy.isdead = false;
		
	}

	@Override
	public void resize(int width, int height) {
		boy.normalBoy.setOrigin(boy.normalBoy.getWidth()/2, boy.normalBoy.getHeight()/2);
		boy.normalBoy.setPosition(camera.viewportWidth / 4, camera.viewportHeight / 2);
		middle.setOrigin(middle.getWidth()/2, middle.getHeight()/2);
		middle.setPosition(width/4,height/2);	
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
