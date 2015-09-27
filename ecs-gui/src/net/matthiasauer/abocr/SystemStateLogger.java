package net.matthiasauer.abocr;

import com.badlogic.gdx.Gdx;

public class SystemStateLogger<T extends Enum<?>> {
	private final Class<?> clazz;
	private T currentState = null;
	
	public SystemStateLogger(T initialState, Class<?> clazz) {
		this.changeState(initialState);
		this.clazz = clazz;
	}
	
	public void changeState(T state) {
		this.currentState = state;
		
		Gdx.app.debug(
				"SystemStateLogger for " + this.clazz,
				"changed state to : " + this.currentState); 
	}
	
	public T getState() {
		return this.currentState;
	}
}
