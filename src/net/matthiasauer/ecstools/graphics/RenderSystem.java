package net.matthiasauer.ecstools.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSystem extends EntitySystem {
	/**
	 * 11 is the default size internally so start with that initial capacity
	 */
	private static final int sortedRenderComponentsInitialSize = 11;
	@SuppressWarnings("unchecked")
	private static final Family renderEntityFamily =
			Family.all(RenderComponent.class).get();
	public final OrthographicCamera camera;
	private final SpriteBatch spriteBatch;
	private ImmutableArray<Entity> entitiesToRender;

	private ComponentMapper<RenderComponent> baseRenderComponentMapper;
	
	private final Queue<RenderComponent> sortedRenderComponents;
	private final Map<RenderComponent, Entity> reverseRenderComponentMapper;
	private RenderSpriteSubSystem renderSpriteSubSystem;
	private RenderTextSubSystem renderTextSubSystem;

	public RenderSystem(OrthographicCamera camera) {
		this.camera = camera;
		this.spriteBatch = new SpriteBatch();
		this.reverseRenderComponentMapper = new HashMap<RenderComponent, Entity>();
		
		// create the PriorityQueue with the custom comparator and an initial size
		this.sortedRenderComponents =
				new PriorityQueue<RenderComponent>(
						sortedRenderComponentsInitialSize,
						new RenderComponentComparator());
	}

	@Override
	public void addedToEngine(Engine engine) {
		PooledEngine pooledEngine =
				(PooledEngine) engine;
		this.baseRenderComponentMapper =
				ComponentMapper.getFor(RenderComponent.class);
		this.entitiesToRender =
				engine.getEntitiesFor(renderEntityFamily);
		this.renderSpriteSubSystem =
				new RenderSpriteSubSystem(this.camera, pooledEngine, this.spriteBatch);
		this.renderTextSubSystem =
				new RenderTextSubSystem(this.camera, pooledEngine, this.spriteBatch);
		
		super.addedToEngine(engine);
	}
	
	private void changeProjection(boolean renderProjected) {
		// end
		this.spriteBatch.end();
		
		if (renderProjected) {
			this.camera.zoom = this.camera.zoom;
			this.camera.update();
			
			this.spriteBatch.setProjectionMatrix(this.camera.combined);
		} else {
			this.camera.zoom = 1;
			this.camera.update();
			
			this.spriteBatch.setProjectionMatrix(this.camera.projection);
		}
		
		// start new batch
		this.spriteBatch.begin();
	}
	
	@Override
	public void update(float deltaTime) {
		this.renderTextSubSystem.preIteration();
		
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		this.orderRenderComponents();
		
		this.spriteBatch.begin();
		this.changeProjection(true);
		boolean lastProjectedValue = true;
		final float originalZoom = this.camera.zoom;

		// iterate over the keys in order (that's what the treemap is for)
		while (!this.sortedRenderComponents.isEmpty()) {
			RenderComponent baseRenderComponent =
					this.sortedRenderComponents.poll();
			IRenderComponentSpecialization specialization =
					baseRenderComponent.getSpecialization();
			Entity renderEntity =
					this.reverseRenderComponentMapper.get(baseRenderComponent);
			
			if (lastProjectedValue != baseRenderComponent.renderProjected) {
				lastProjectedValue = baseRenderComponent.renderProjected;
				
				this.changeProjection(baseRenderComponent.renderProjected);
			}

			if (specialization instanceof SpriteRenderSpecialization) {
				this.renderSpriteSubSystem.drawSprite(
						renderEntity,
						baseRenderComponent,
						(SpriteRenderSpecialization) specialization);
				continue;
			}

			if (specialization instanceof TextRenderSpecialization) {
				this.renderTextSubSystem.drawText(
						renderEntity,
						baseRenderComponent,
						(TextRenderSpecialization) specialization);
				continue;
			}
			
			throw new NullPointerException(
					"Unknown specialization of the BaseRenderComponent !");
		}
		
		this.spriteBatch.end();
		
		this.camera.zoom = originalZoom;
		this.camera.update();
	}
	
	private void orderRenderComponents() {
		this.sortedRenderComponents.clear();
		this.reverseRenderComponentMapper.clear();
		
		for (Entity renderEntity : this.entitiesToRender) {
			RenderComponent baseRenderComponent =
					this.baseRenderComponentMapper.get(renderEntity);
			
			this.sortedRenderComponents.add(baseRenderComponent);
			this.reverseRenderComponentMapper.put(baseRenderComponent, renderEntity);
		}
	}
}
