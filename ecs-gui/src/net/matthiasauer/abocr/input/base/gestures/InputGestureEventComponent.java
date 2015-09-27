package net.matthiasauer.abocr.input.base.gestures;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InputGestureEventComponent implements Component, Poolable {
	public InputGestureEventType type;
	public float argument;
	public Vector2 vector = new Vector2();
	
	@Override
	public void reset() {
		this.set(null, 0, 0, 0);
	}
	
	public InputGestureEventComponent set(
			InputGestureEventType type,
			float argument,
			float vectorX,
			float vectorY) {
		this.argument = argument;
		this.type = type;
		this.vector.x = vectorX;
		this.vector.y = vectorY;
		
		return this;
	}
}
