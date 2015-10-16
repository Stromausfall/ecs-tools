package net.matthiasauer.ecstools.graphics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class RenderSpriteSubSystem {
	private final OrthographicCamera camera;
	private final PooledEngine pooledEngine;
	private final SpriteBatch spriteBatch;
	
	public RenderSpriteSubSystem(
			OrthographicCamera camera,
			PooledEngine pooledEngine,
			SpriteBatch spriteBatch) {
		this.camera = camera;
		this.pooledEngine = pooledEngine;
		this.spriteBatch = spriteBatch;
	}
	
	public void drawSprite(
			Entity renderEntity,
			RenderComponent renderComponent,
			SpriteRenderSpecialization specialization) {
		float actualPositionX =
				RenderPositionUnitTranslator.translateX(
						renderComponent.position.x,
						renderComponent.position.y,
						renderComponent.positionUnit) - specialization.spriteTexture.getRegionWidth() / 2;
		float actualPositionY =
				RenderPositionUnitTranslator.translateY(
						renderComponent.position.x,
						renderComponent.position.y,
						renderComponent.positionUnit) - specialization.spriteTexture.getRegionHeight() / 2;
		float originX = specialization.spriteTexture.getRegionWidth()/2;
		float originY = specialization.spriteTexture.getRegionHeight()/2;
		float width = specialization.spriteTexture.getRegionWidth();
		float height = specialization.spriteTexture.getRegionHeight();
		
		if (!renderComponent.renderProjected) {
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
				specialization.spriteTexture,
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
		
		renderEntity.add(
				this.pooledEngine.createComponent(RenderedComponent.class).set(
						actualPositionX,
						actualPositionY,
						specialization.spriteTexture.getRegionWidth(),
						specialization.spriteTexture.getRegionHeight(),
						this.camera.zoom,
						renderComponent.renderProjected));
	}

}
