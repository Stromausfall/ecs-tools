package net.matthiasauer.ecstools.graphics.texture;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public final class TextureLoader {
	private List<TextureAtlas> create(List<String> atlasFilePaths) {
		List<TextureAtlas> result = new LinkedList<TextureAtlas>();
		
		for (String atlasFilePath : atlasFilePaths) {
			FileHandle atlasFile = Gdx.files.internal(atlasFilePath);
			TextureAtlas textureAtlas = new TextureAtlas(atlasFile);
			result.add(textureAtlas);
		}
		
		return result;
	}
	
	private TextureLoader(List<String> atlasFilePaths) {
		this.archive = new HashMap<String, AtlasRegion>();
		this.textureAtlases =
				Collections.unmodifiableList(create(atlasFilePaths));
	}
	
	private static TextureLoader instance = null;
	private final Map<String, AtlasRegion> archive;
	private final List<TextureAtlas> textureAtlases;
	
	public static void createInstance(List<String> atlasFilePaths) {
		if (instance != null) {
			throw new NullPointerException("the create method may only be called once !");
		}
		
		instance = new TextureLoader(atlasFilePaths);
	}
	
	public static TextureLoader getInstance() {
		if (instance == null) {
			throw new NullPointerException("the TextureLoader.createInstance method was not called before requesting the instance !!!");
		}
		
		return instance;
	}
	
	/**
	 * returns the AtlasRegion with the given name,
	 * Note : it is expected that the name is unique across ALL used TextureAtlases
	 * @param name
	 * @return
	 */
	public AtlasRegion getTexture(String name) {
		AtlasRegion texture = null;
		
		if (name == null) {
			throw new NullPointerException("name mustn't be null !");
		}
		
		// if there is no entry - load it and save it
		if (!archive.containsKey(name)) {
			for (TextureAtlas textureAtlas : this.textureAtlases) {
				texture = textureAtlas.findRegion(name);
				
				if (texture != null) {
					archive.put(name, texture);
					break;
				}
			}
		}
		
		texture = archive.get(name);
		
		if (texture == null) {
			throw new NullPointerException(
					"found no region '" + name + "' in the TextureAtlases");
		}
		
		return texture;
	}
}
