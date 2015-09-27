package net.matthiasauer.abocr.graphics.camera.move;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import net.matthiasauer.abocr.graphics.RenderComponent;
import net.matthiasauer.abocr.graphics.RenderedComponent;

public class CameraMoveSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family =
			Family.all(MoveEventComponent.class).get();
	private ImmutableArray<Entity> renderedEntities;
	private final ComponentMapper<RenderedComponent> renderedComponentMapper;
	private final ComponentMapper<RenderComponent> renderComponentMapper;
	private final ComponentMapper<MoveEventComponent> moveEventComponentMapper;
	private final OrthographicCamera camera;
	private final Vector2 translateCamera;
	
	public CameraMoveSystem(OrthographicCamera camera) {
		super(family);
		this.camera = camera;
		this.translateCamera = new Vector2();
		this.moveEventComponentMapper =
				ComponentMapper.getFor(MoveEventComponent.class);
		this.renderComponentMapper =
				ComponentMapper.getFor(RenderComponent.class);
		this.renderedComponentMapper =
				ComponentMapper.getFor(RenderedComponent.class);
		
		this.camera.position.set(0, 0, 0);
		this.camera.update();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.renderedEntities =
				engine.getEntitiesFor(
						Family.all(RenderComponent.class, RenderedComponent.class).get());
		
		super.addedToEngine(engine);
	}
	
	private void limitCameraPos(Vector2 translation) {
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		double collectedEntities = 0;
		float newCameraX = translation.x + this.camera.position.x;
		float newCameraY = translation.y + this.camera.position.y;
		
		// get values
		for (Entity entity : this.renderedEntities) {
			RenderComponent renderComponent =
					this.renderComponentMapper.get(entity);
			RenderedComponent renderedComponent =
					this.renderedComponentMapper.get(entity);
			
			// it only makes sense to collect the positions of projected
			// textures !
			if (renderComponent.layer.projected == true) {				
				minX = Math.min(minX, renderedComponent.renderedTarget.x);
				minY = Math.min(minY, renderedComponent.renderedTarget.y);
	
				maxX = Math.max(maxX, renderedComponent.renderedTarget.x + renderComponent.spriteTexture.getRegionWidth());
				maxY = Math.max(maxY, renderedComponent.renderedTarget.y + renderComponent.spriteTexture.getRegionHeight());
				
				collectedEntities += 1;
			}
		}

		// if there is no entity to render - then the min and max values
		// would be too large/too little - hence cover that case
		// otherwise we can get a bug when rendering starts
		if (collectedEntities == 0) {
			minX = -10;
			minY = -10;
			maxX = 10;
			maxY = 10;
		}
		
		if (newCameraX > maxX) {
			translateCamera.add(
					new Vector2(
							- (newCameraX - maxX),
							0));
		}
		if (newCameraY > maxY) {
			translateCamera.add(
					new Vector2(
							0,
							- (newCameraY - maxY)));
		}
		if (newCameraX < minX) {
			translateCamera.add(
					new Vector2(
							- (newCameraX - minX),
							0));
		}
		if (newCameraY < minY) {
			translateCamera.add(
					new Vector2(
							0,
							- (newCameraY - minY)));
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		MoveEventComponent moveEvent =
				this.moveEventComponentMapper.get(entity);

		translateCamera.set(
				moveEvent.translate);
		
		// by scaling it with the zoom - the movement is adapted to the zoom !
		translateCamera.scl(this.camera.zoom);

		// don't shoot over the edge !
		this.limitCameraPos(this.translateCamera);

		this.camera.translate(translateCamera);
		this.camera.update();
	}

}
