package net.matthiasauer.abocr.input.click;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;

import net.matthiasauer.abocr.SystemStateLogger;
import net.matthiasauer.abocr.input.base.touch.InputTouchEventComponent;
import net.matthiasauer.abocr.input.base.touch.InputTouchEventType;

public class ClickGeneratorSystem extends IteratingSystem {
	private enum SystemState {
		Initial,
		WaitingForTouchUp,
		FinishedWithCorrectTouchUp
	}
	
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(InputTouchEventComponent.class).get();
	private final SystemStateLogger<SystemState> systemStateLogger;
	private PooledEngine pooledEngine;
	private ComponentMapper<ClickableComponent> unitSelectableComponentMapper;
	private ComponentMapper<InputTouchEventComponent> inputTouchEventComponentComponentMapper;
	private Entity lastClickedEntity = null;

	/**
	 * This entity might be selected - because there was a touchDown event on it..
	 * now we have to keep track if the up event is also on it !
	 */
	private Entity touchDownEntity;
	
	public ClickGeneratorSystem() {
		super(family);
		
		this.systemStateLogger =
				new SystemStateLogger<SystemState>(
						SystemState.Initial,
						ClickGeneratorSystem.class);
		this.touchDownEntity = null;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		this.pooledEngine = (PooledEngine) engine;
		this.unitSelectableComponentMapper =
				ComponentMapper.getFor(ClickableComponent.class);
		this.inputTouchEventComponentComponentMapper =
				ComponentMapper.getFor(InputTouchEventComponent.class);
	}
	
	private void handle(InputTouchEventComponent inputTouchEventComponent) {
		Entity targetEntity =
				inputTouchEventComponent.target;
		

		if (targetEntity == null) {
			// NO entity 'under' the mouse
			return;
		}
		
		// get the entity 'under' the mouse
		ClickableComponent unitSelectableComponent =
					this.unitSelectableComponentMapper.get(targetEntity);

		if (unitSelectableComponent == null) {
			// the entity under the mouse is NOT selectable
			return;
		}
		
		if (inputTouchEventComponent.inputType == InputTouchEventType.TouchDown) {
			this.handleTouchDown(targetEntity);
		}
		
		if (inputTouchEventComponent.inputType == InputTouchEventType.TouchUp) {
			this.handleTouchUp(targetEntity);
		}
	}
	
	private void handleTouchDown(Entity targetEntity) {
		// mouse down - note on which entity
		this.touchDownEntity = targetEntity;
		this.systemStateLogger.changeState(SystemState.WaitingForTouchUp);
	}
	
	private void handleTouchUp(Entity targetEntity) {
		if (this.touchDownEntity == null) {
			// we DIDN'T click down on another entity before this TouchUp
			return;
		}
		
		if (!this.touchDownEntity.equals(targetEntity)) {
			// the entity we performed a TouchDown is NOT the same as the TouchUp entity !
			return;
		}

		this.systemStateLogger.changeState(SystemState.FinishedWithCorrectTouchUp);
		targetEntity.add(this.pooledEngine.createComponent(ClickedComponent.class));
		
		// note this entity - so we can remove the component in the next frame !
		this.lastClickedEntity = targetEntity;
	}
	
	@Override
	public void update(float deltaTime) {
		// first remove the component form the old 'clicked' entity
		if (this.lastClickedEntity != null) {
			this.lastClickedEntity.remove(ClickedComponent.class);
			this.lastClickedEntity = null;
		}
		
		// now continue - this will in term call the processEntity method !
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputTouchEventComponent inputTouchEventComponent =
				this.inputTouchEventComponentComponentMapper.get(entity);
		
		this.handle(inputTouchEventComponent);
		
		// no matter how we handled the InputTouchEvent - if we got a ToucUp 
		// we have to reset our progress !
		if (inputTouchEventComponent.inputType == InputTouchEventType.TouchUp) {
			this.systemStateLogger.changeState(SystemState.Initial);
			this.touchDownEntity = null;
		}
	}
}
