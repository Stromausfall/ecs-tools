package net.matthiasauer.abocr.input.base.touch;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InputTouchEventComponent implements Component, Poolable {
	public Entity target;
	public InputTouchEventType inputType;
	public final Vector2 unprojectedPosition = new Vector2();
	public final Vector2 projectedPosition = new Vector2();
	public int argument;
	
	@Override
	public void reset() {
		this.target = null;
		this.inputType = null;
		this.argument = 0;
		this.projectedPosition.x = 0;
		this.projectedPosition.y = 0;
		this.unprojectedPosition.x = 0;
		this.unprojectedPosition.y = 0;
	}
}
