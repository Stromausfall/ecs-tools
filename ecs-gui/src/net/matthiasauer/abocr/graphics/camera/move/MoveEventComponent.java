package net.matthiasauer.abocr.graphics.camera.move;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MoveEventComponent implements Component, Poolable {
	public final Vector2 translate = new Vector2();

	@Override
	public void reset() {
		this.set(0, 0);
	}
	
	public MoveEventComponent set(float x, float y) {
		this.translate.x = x;
		this.translate.y = y;
		
		return this;
	}
}
