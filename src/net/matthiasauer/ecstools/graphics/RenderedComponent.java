package net.matthiasauer.ecstools.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderedComponent implements Component, Poolable {
	public final Rectangle renderedTarget = new Rectangle();
	public float zoomFactor;
	public boolean projected;
	
	public RenderedComponent set(float x, float y, float width, float height, float zoomFactor, boolean projected) {
		this.renderedTarget.x = x;
		this.renderedTarget.y = y;
		this.renderedTarget.width = width;
		this.renderedTarget.height = height;
		this.zoomFactor = zoomFactor;
		this.projected = projected;
		
		return this;
	}

	@Override
	public void reset() {
		this.set(0, 0, 0, 0, 1, false);
	}
}
