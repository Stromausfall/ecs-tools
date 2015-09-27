package net.matthiasauer.abocr.graphics.texture.archive;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderTextureArchiveEventComponent implements Component, Poolable {
	public RenderTextureArchiveEventType type = null;
	public Texture texture = null;
	
	@Override
	public void reset() {
		this.texture = null;
		this.type =  null;
	}
}
