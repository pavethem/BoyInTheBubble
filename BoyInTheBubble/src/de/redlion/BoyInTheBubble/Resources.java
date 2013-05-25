package de.redlion.BoyInTheBubble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Resources {
	
	public static Resources instance;
	
	public TextureAtlas boyTextures;
	public Texture middle;
//	public Texture boyTex;
	public TiledMap map;

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();
		}
		return instance;
	}

	public Resources() {		
		reInit();	
	}

	public void reInit() {		
		boyTextures = new TextureAtlas(Gdx.files.internal("data/boy.pack"));
		middle = new Texture(Gdx.files.internal("data/middle.png"));
//		boyTex = boyTextures.getRegions().get(0).getTexture();
		map = new TmxMapLoader().load(Gdx.files.internal("data/test.tmx").toString(),true);
	}

	public void dispose() {
		boyTextures.dispose();
		map.dispose();
	}
}
