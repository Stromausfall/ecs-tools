package net.matthiasauer.abocr.graphics.camera.zoom;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraZoomSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(ZoomEventComponent.class).get();
	private final OrthographicCamera camera;
	private final ComponentMapper<ZoomEventComponent> zoomEventComponentComponentMapper;
	
	public CameraZoomSystem(OrthographicCamera camera) {
		super(family);
		
		this.camera = camera;
		this.zoomEventComponentComponentMapper =
				ComponentMapper.getFor(ZoomEventComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ZoomEventComponent zoomEvent =
				this.zoomEventComponentComponentMapper.get(entity);

		this.camera.zoom += zoomEvent.value * this.camera.zoom;

		// limit
		this.camera.zoom = Math.max(0.05f, this.camera.zoom);
		this.camera.zoom = Math.min(2.0f, this.camera.zoom);
		
		// update the camera
		this.camera.update();
	}
}
