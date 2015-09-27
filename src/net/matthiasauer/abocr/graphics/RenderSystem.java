package net.matthiasauer.abocr.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class RenderSystem extends EntitySystem {
	public final OrthographicCamera camera;
	private final SpriteBatch spriteBatch;
	private ImmutableArray<Entity> entitiesToRender;
	private ComponentMapper<RenderComponent> renderComponentMapper;
	private final Map<RenderLayer, List<RenderComponent>> sortedComponents;
	private final Map<RenderComponent, Entity> reverseRenderComponentMapper;
	private PooledEngine pooledEngine;

	public RenderSystem(OrthographicCamera camera) {
		this.camera = camera;
		this.spriteBatch = new SpriteBatch();
		this.sortedComponents = new HashMap<RenderLayer, List<RenderComponent>>();
		this.reverseRenderComponentMapper = new HashMap<RenderComponent, Entity>();
		
		for (RenderLayer layer : RenderLayer.values()) {
			this.sortedComponents.put(layer, new ArrayList<RenderComponent>());
		}
	}
	
	private void clearSortedComponents() {
		for (RenderLayer layer : this.sortedComponents.keySet()) {
			this.sortedComponents.get(layer).clear();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		this.pooledEngine =
				(PooledEngine) engine;
		this.renderComponentMapper =
				ComponentMapper.getFor(RenderComponent.class);
		this.entitiesToRender =
				engine.getEntitiesFor(
						Family.all(RenderComponent.class).get());
		
		super.addedToEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		this.orderRenderComponents();

		this.spriteBatch.begin();
		Boolean lastProjectedValue = null;
		final float originalZoom = this.camera.zoom;
		
		// iterate over the enum in order
		for (RenderLayer layer : RenderLayer.values()) {
			if (lastProjectedValue != (Boolean)layer.projected) {
				lastProjectedValue = layer.projected;
				
				// end
				this.spriteBatch.end();
				
				if (layer.projected) {
					this.camera.zoom = originalZoom;
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
			
			for (RenderComponent renderComponent : this.sortedComponents.get(layer)) {
				switch (renderComponent.renderType) {
				case Sprite:
					this.drawSprite(renderComponent);
					break;
				case Text:
					this.drawText(renderComponent);
					break;
				default:
					throw new NullPointerException("Unknown RenderType ! : " + renderComponent.renderType);
				}				
			}
		}
		
		this.spriteBatch.end();
		
		this.camera.zoom = originalZoom;
		this.camera.update();
	}
	
	private final BitmapFont font = new BitmapFont(); 
	private void drawText(RenderComponent renderComponent) {
		float actualPositionX =
				RenderPositionUnitTranslator.translateX(
						renderComponent.position.x,
						renderComponent.position.y,
						renderComponent.positionUnit);
		float actualPositionY =
				RenderPositionUnitTranslator.translateY(
						renderComponent.position.x,
						renderComponent.position.y,
						renderComponent.positionUnit);
		
		if (!renderComponent.layer.projected) {
			actualPositionX *= this.camera.zoom;
			actualPositionY *= this.camera.zoom;
		}
		
		if (renderComponent.tint != null) {
			font.setColor(renderComponent.tint);
		} else {
			font.setColor(Color.BLACK);
		}

	     Matrix4 rotationMatrix = new Matrix4();
	     Matrix4 oldMatrix = this.spriteBatch.getTransformMatrix().cpy();
	     rotationMatrix.idt();
	     
	     Vector3 centerOfRotation =
	    		 new Vector3(
	    				 actualPositionX,
	    				 actualPositionY,
	    				 0);
	     
	     rotationMatrix.rotate(new Vector3(0, 0, 1), renderComponent.rotation);
	     rotationMatrix.trn(centerOfRotation);

	     spriteBatch.end();
	     this.spriteBatch.setTransformMatrix(rotationMatrix);
	     spriteBatch.begin();

		
		font.draw(
				this.spriteBatch,
				renderComponent.textString,
				0,
				0);


	     spriteBatch.end();
	     this.spriteBatch.setTransformMatrix(oldMatrix);
	     spriteBatch.begin();
	}
	
	private void drawSprite(RenderComponent renderComponent) {
		float actualPositionX =
				RenderPositionUnitTranslator.translateX(
						renderComponent.position.x,
						renderComponent.position.y,
						renderComponent.positionUnit) - renderComponent.spriteTexture.getRegionWidth() / 2;
		float actualPositionY =
				RenderPositionUnitTranslator.translateY(
						renderComponent.position.x,
						renderComponent.position.y,
						renderComponent.positionUnit) - renderComponent.spriteTexture.getRegionHeight() / 2;
		float originX = renderComponent.spriteTexture.getRegionWidth()/2;
		float originY = renderComponent.spriteTexture.getRegionHeight()/2;
		float width = renderComponent.spriteTexture.getRegionWidth();
		float height = renderComponent.spriteTexture.getRegionHeight();
		
		if (!renderComponent.layer.projected) {
			actualPositionX *= this.camera.zoom;
			actualPositionY *= this.camera.zoom;
			originX *= this.camera.zoom;
			originY *= this.camera.zoom;
			width *= this.camera.zoom;
			height *= this.camera.zoom;
		}
		
		Color base = this.spriteBatch.getColor();
		
		if (renderComponent.tint != null) {
			this.spriteBatch.setColor(renderComponent.tint);
		}

		this.spriteBatch.draw(
				renderComponent.spriteTexture,
				actualPositionX,
				actualPositionY,
				originX,
				originY,
				width,
				height,
				1,
				1,
				renderComponent.rotation);
		
		if (renderComponent.tint != null) {
			this.spriteBatch.setColor(base);
		}
		
		this.reverseRenderComponentMapper.get(renderComponent).add(
				this.pooledEngine.createComponent(RenderedComponent.class).set(
						actualPositionX,
						actualPositionY,
						renderComponent.spriteTexture.getRegionWidth(),
						renderComponent.spriteTexture.getRegionHeight(),
						this.camera.zoom));
	}
	
	private void orderRenderComponents() {
		this.clearSortedComponents();
		this.reverseRenderComponentMapper.clear();
		
		for (Entity renderEntity : this.entitiesToRender) {
			RenderComponent data =
					this.renderComponentMapper.get(renderEntity);
			
			this.sortedComponents.get(data.layer).add(data);
			this.reverseRenderComponentMapper.put(data, renderEntity);
		}
	}
}
