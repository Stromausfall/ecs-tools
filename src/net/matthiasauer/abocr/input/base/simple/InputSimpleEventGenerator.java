package net.matthiasauer.abocr.input.base.simple;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputSimpleEventGenerator extends EntitySystem implements InputProcessor {
	private final InputMultiplexer inputMultiplexer;
	private PooledEngine engine;
	private InputSimpleEventComponent lastEvent;
	private Entity inputSimpleEventContainerEntity;

	public InputSimpleEventGenerator(InputMultiplexer inputMultiplexer) {
		this.lastEvent = null;
		this.inputMultiplexer = inputMultiplexer;
	}
		
	@Override
	public void update(float deltaTime) {
		// remove any previous event
		this.inputSimpleEventContainerEntity.remove(InputSimpleEventComponent.class);
		
		if (this.lastEvent != null) {
			this.inputSimpleEventContainerEntity.add(this.lastEvent);
			this.lastEvent = null;
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = (PooledEngine)engine;
		this.inputMultiplexer.addProcessor(this);
		this.inputSimpleEventContainerEntity = this.engine.createEntity();		
		this.engine.addEntity(this.inputSimpleEventContainerEntity);

		super.addedToEngine(engine);
	}
	
	private void saveEvent(InputSimpleEventType inputType, int argument) {
		this.lastEvent =
				this.engine.createComponent(InputSimpleEventComponent.class).set(
						inputType,
						argument);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		this.inputMultiplexer.removeProcessor(this);
		
		super.removedFromEngine(engine);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		this.saveEvent(InputSimpleEventType.Scrolled, amount);
		return false;
	}
}
