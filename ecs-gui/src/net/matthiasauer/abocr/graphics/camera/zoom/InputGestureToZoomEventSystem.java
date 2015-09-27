package net.matthiasauer.abocr.graphics.camera.zoom;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

import net.matthiasauer.abocr.input.base.gestures.InputGestureEventComponent;

public class InputGestureToZoomEventSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(InputGestureEventComponent.class).get();
	private ComponentMapper<InputGestureEventComponent> inputGestureEventComponentComponentMapper;
	private PooledEngine engine;
	private Entity containerEntity;

	public InputGestureToZoomEventSystem() {
		super(family);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine) engine;
		this.inputGestureEventComponentComponentMapper =
				ComponentMapper.getFor(InputGestureEventComponent.class);
		
		this.containerEntity = this.engine.createEntity();
		this.engine.addEntity(this.containerEntity);
		
		super.addedToEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		// remove the old event !
		this.containerEntity.remove(ZoomEventComponent.class);
		
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputGestureEventComponent event =
				this.inputGestureEventComponentComponentMapper.get(entity);
		
		// by dividing by the screen size - the speed becomes independent of the screen size !
		float scaledZoomDistance =
				- event.argument / Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.containerEntity.add(
				this.engine.createComponent(ZoomEventComponent.class).set(scaledZoomDistance));
	}
}
