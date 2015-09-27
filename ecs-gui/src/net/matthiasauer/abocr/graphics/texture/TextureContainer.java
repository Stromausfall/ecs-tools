package net.matthiasauer.abocr.graphics.texture;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class TextureContainer<S extends Enum<?>> {
	private Map<S, AtlasRegion> container =
			new HashMap<S, AtlasRegion>();
	
	public void add(S id, String textureName) {
		if (id == null) {
			throw new NullPointerException("id mustn't be null !");
		}
		if (textureName == null) {
			throw new NullPointerException("texture name mustn't be null !");
		}
		if (this.container.containsKey(id)) {
			throw new NullPointerException("a texture with id '" + id + "' has already been added !");
		}
		
		AtlasRegion texture =
				TextureLoader.getInstance().getTexture(textureName);
		
		this.container.put(id, texture);	
	}
	
	public AtlasRegion get(S id) {
		if (id == null) {
			throw new NullPointerException("id mustn't be null !");
		}
		if (!this.container.containsKey(id)) {
			throw new NullPointerException("no texture with id '" + id + "' in container !");
		}
		
		return this.container.get(id);
	}
}
