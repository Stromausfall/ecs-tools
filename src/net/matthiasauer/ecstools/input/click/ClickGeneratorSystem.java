package net.matthiasauer.ecstools.input.click;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.matthiasauer.ecstools.input.base.gestures.InputGestureEventComponent;
import net.matthiasauer.ecstools.input.base.gestures.InputGestureEventType;
import net.matthiasauer.ecstools.input.base.touch.InputTouchEventComponent;

public class ClickGeneratorSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family touchEventFamily =
			Family.all(InputTouchEventComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family tappedFamily =
			Family.all(InputGestureEventComponent.class).get();
	@SuppressWarnings("unchecked")
	private static final Family clickedFamily =
			Family.all(ClickedComponent.class).get();
	private PooledEngine pooledEngine;
	private Entity noClickableEntity;
	private ImmutableArray<Entity> touchEventEntities;
	private ImmutableArray<Entity> clickedEntities;
	private ComponentMapper<InputGestureEventComponent> inputGestureEventComponentComponentMapper;
	private ComponentMapper<InputTouchEventComponent> inputTouchEventComponentComponentMapper;

	public ClickGeneratorSystem() {
		super(tappedFamily);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.pooledEngine = (PooledEngine) engine;
		this.inputTouchEventComponentComponentMapper =
				ComponentMapper.getFor(InputTouchEventComponent.class);
		this.inputGestureEventComponentComponentMapper =
				ComponentMapper.getFor(InputGestureEventComponent.class);
		this.touchEventEntities =
				this.pooledEngine.getEntitiesFor(touchEventFamily);
		this.clickedEntities =
				this.pooledEngine.getEntitiesFor(clickedFamily);
		this.noClickableEntity =
				this.pooledEngine.createEntity();
		this.pooledEngine.addEntity(this.noClickableEntity);
	}
	
	@Override
	public void update(float deltaTime) {
		for (Entity entity : this.clickedEntities) {
			entity.remove(ClickedComponent.class);
		}
		
		// now continue - this will in term call the processEntity method !
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputGestureEventComponent gestureEventComponent =
				this.inputGestureEventComponentComponentMapper.get(entity);
		
		// make sure it's a Tap event !
		if (gestureEventComponent.type != InputGestureEventType.Tap) {
			return;
		}
		
		InputTouchEventComponent inputTouchEventComponent =
				this.getInputTouchEventComponent();
		
		// make sure we have information about the entity 'UNDER' the mouse
		if (inputTouchEventComponent == null) {
			return;
		}
		
		Entity targetEntity =
				inputTouchEventComponent.target;

		// make sure there is an entity under the mouse !
		if (targetEntity == null) {
			// NO entity 'under' the mouse
			noClickableEntity.add(this.pooledEngine.createComponent(ClickedComponent.class));
			return;
		}
		
		// add it to the entity UNDER the mouse
		targetEntity.add(this.pooledEngine.createComponent(ClickedComponent.class));
	}
	
	private InputTouchEventComponent getInputTouchEventComponent() {
		Entity touchEventEntity =
				touchEventEntities.first();
		
		if (touchEventEntity == null) {
			// could happen - but shouldn't
			return null;
		}
		
		return
				this.inputTouchEventComponentComponentMapper.get(touchEventEntity);
	}
}
