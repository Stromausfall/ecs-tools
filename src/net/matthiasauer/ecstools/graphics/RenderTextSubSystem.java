package net.matthiasauer.ecstools.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

class RenderTextSubSystem { 
	private final Map<String, BitmapFont> fonts = new HashMap<String, BitmapFont>();
	private final BitmapFont defaultFont = new BitmapFont(); 
	private final OrthographicCamera camera;
	private final PooledEngine pooledEngine;
	private final SpriteBatch spriteBatch;
	
	public RenderTextSubSystem(
			OrthographicCamera camera,
			PooledEngine pooledEngine,
			SpriteBatch spriteBatch) {
		this.camera = camera;
		this.pooledEngine = pooledEngine;
		this.spriteBatch = spriteBatch;
	}

	public void drawText(
			Entity renderEntity,
			RenderComponent renderComponent,
			TextRenderSpecialization specialization) {
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
		
		if (!renderComponent.renderProjected) {
			actualPositionX *= this.camera.zoom;
			actualPositionY *= this.camera.zoom;
		}
		
		BitmapFont font = this.defaultFont;
		final String textFont = specialization.textFont;
		
		if (textFont != null) {
			font = this.fonts.get(textFont);
			
			if (font == null) {
				// if we don't have a cache
				font =
						new BitmapFont(
								Gdx.files.internal(textFont + ".fnt"),
								Gdx.files.internal(textFont + ".png"),
								false);
				
				this.fonts.put(textFont, font);
			}
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
				specialization.textString,
				0,
				0);
		
		spriteBatch.end();
		this.spriteBatch.setTransformMatrix(oldMatrix);
		spriteBatch.begin();
		
		/* here also create the RENDEREDCOMPONENT !
		
		GlyphLayout xxx;
		xxx.
		this.spriteBatch.draw */
	}
}
