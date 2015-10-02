package net.matthiasauer.ecstools.graphics.camera.move;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.ecstools.input.base.gestures.InputGestureEventComponent;
import net.matthiasauer.ecstools.input.base.gestures.InputGestureEventType;
import net.matthiasauer.ecstools.utils.ContainerEntities;

public class InputGestureToMoveEventSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(InputGestureEventComponent.class).get();
	private ComponentMapper<InputGestureEventComponent> inputGestureEventComponentComponentMapper;
	private PooledEngine engine;
	private ContainerEntities container;

	public InputGestureToMoveEventSystem() {
		super(family);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine) engine;
		this.inputGestureEventComponentComponentMapper =
				ComponentMapper.getFor(InputGestureEventComponent.class);
		
		this.container = new ContainerEntities(this.engine);
		
		super.addedToEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		// remove the old event !
		this.container.clear();
		
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputGestureEventComponent event =
				this.inputGestureEventComponentComponentMapper.get(entity);

		if (event.type == InputGestureEventType.Pan) {
			this.container.add(
					this.engine.createComponent(MoveEventComponent.class).set(
							-event.vector.x,
							event.vector.y));
		}
	}
}
