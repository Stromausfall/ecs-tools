package net.matthiasauer.ecstools.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.matthiasauer.ecstools.utils.Pair;

class RenderTextSubSystem { 
	private final Map<String, BitmapFont> fonts = new HashMap<String, BitmapFont>();
	private final BitmapFont defaultFont = new BitmapFont(); 
	private final OrthographicCamera camera;
	private final PooledEngine pooledEngine;
	private final SpriteBatch spriteBatch;
	private Map<Pair<String, String>, GlyphLayout> current;
	private Map<Pair<String, String>, GlyphLayout> last;
	
	public RenderTextSubSystem(
			OrthographicCamera camera,
			PooledEngine pooledEngine,
			SpriteBatch spriteBatch) {
		this.camera = camera;
		this.pooledEngine = pooledEngine;
		this.spriteBatch = spriteBatch;
		this.current = new HashMap<Pair<String,String>, GlyphLayout>();
		this.last = new HashMap<Pair<String,String>, GlyphLayout>();
	}
	
	public void preIteration() {
		// switch current and last !
		// then clear the last one !
		// in this iteration we then fill the last one
		// therefore we can reuse objects from last turn and forget
		// them if they are not used !
		Map<Pair<String, String>, GlyphLayout> temp = this.last;
		this.last = this.current;
		this.current = temp;
		this.current.clear();
	}
	
	private GlyphLayout getCachedGlyphLayout(TextRenderSpecialization specialization, BitmapFont font) {
		Pair<String, String> key =
				Pair.of(specialization.textFont, specialization.textString);
		GlyphLayout glyphLayout = this.last.get(key);
		
		if (glyphLayout == null) {
			glyphLayout = new GlyphLayout(font, specialization.textString);
			this.current.put(key, glyphLayout);
		}
		
		return glyphLayout;
	}
	
	private BitmapFont getCachedBitmapFont(TextRenderSpecialization specialization) {
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
		
		return font;
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
		
		BitmapFont font = this.getCachedBitmapFont(specialization);
		GlyphLayout glyphLayout = this.getCachedGlyphLayout(specialization, font);
		
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

		renderEntity.add(
				this.pooledEngine.createComponent(RenderedComponent.class).set(
						actualPositionX,
						actualPositionY - glyphLayout.height,
						glyphLayout.width,
						glyphLayout.height,
						this.camera.zoom,
						renderComponent.renderProjected));
	}
}
