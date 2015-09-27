package net.matthiasauer.abocr.graphics.camera.zoom;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ZoomEventComponent implements Component, Poolable {
	public float value;

	@Override
	public void reset() {
		this.set(0);
	}
	
	public ZoomEventComponent set(float value) {
		this.value = value;
		
		return this;
	}
}
