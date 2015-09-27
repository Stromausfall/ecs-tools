package net.matthiasauer.abocr.graphics.texture.archive;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderTextureArchiveEntryComponent implements Component, Poolable {
	public Texture texture = null;
	public Pixmap pixmap = null;
	
	@Override
	public void reset() {
		this.texture = null;
		this.pixmap = null;
	}
}
