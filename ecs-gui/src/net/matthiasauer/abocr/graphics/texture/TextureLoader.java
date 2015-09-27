package net.matthiasauer.abocr.graphics.texture;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public final class TextureLoader {
	private TextureLoader() {
		this.archive = new HashMap<String, AtlasRegion>();
		this.textureAtlas =
				new TextureAtlas(
						Gdx.files.internal(TEXTURE_ATLAS_FILE));
	}
	
	private static final String TEXTURE_ATLAS_FILE = "data1.atlas";
	private static TextureLoader instance = null;
	private final Map<String, AtlasRegion> archive;
	private final TextureAtlas textureAtlas;
	
	public static TextureLoader getInstance() {
		if (instance == null) {
			instance = new TextureLoader();
		}
		
		return instance;
	}
	
	public AtlasRegion getTexture(String name) {
		AtlasRegion texture = null;
		
		if (name == null) {
			throw new NullPointerException("name mustn't be null !");
		}
		
		// if there is no entry - load it and save it
		if (!archive.containsKey(name)) {
			texture = textureAtlas.findRegion(name);
			
			if (texture != null) {
				archive.put(name, texture);
			}
		}
		
		texture = archive.get(name);
		
		if (texture == null) {
			throw new NullPointerException(
					"found no region '" + name + "' in TextureAtlas '" + TEXTURE_ATLAS_FILE + "'");
		}
		
		return texture;
	}
}
