package net.matthiasauer.ecstools.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderComponent implements Component, Poolable {
	public final Vector2 position = new Vector2();
	public float rotation;
	public RenderPositionUnit positionUnit;
	public Color tint;
	public int renderOrder;
	public boolean renderProjected;
	/**
	 * We already have the object for the specialization (it is already allocated
	 * this is just to make it more friendly to the memory - for the poolable BaseRenderComponent
	 */
	private IRenderComponentSpecialization specialization;
	private final SpriteRenderSpecialization spriteSpecialization =
			new SpriteRenderSpecialization();
	private final TextRenderSpecialization textRenderSpecialization =
			new TextRenderSpecialization();
	
	private void setGeneral(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			Color tint,
			int renderOrder,
			boolean renderProjected) {
		this.position.set(positionX, positionY);
		this.rotation = rotation;
		this.positionUnit = positionUnit;
		this.tint = tint;
		this.renderOrder = renderOrder;
		this.renderProjected = renderProjected;
	}
	
	public IRenderComponentSpecialization getSpecialization() {
		return this.specialization;
	}
	
	public RenderComponent setSprite(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			Color tint,
			int renderOrder,
			boolean renderProjected,
			AtlasRegion spriteTexture) {
		this.setGeneral(positionX, positionY, rotation, positionUnit, tint, renderOrder, renderProjected);
		this.spriteSpecialization.spriteTexture = spriteTexture;
		
		this.specialization = this.spriteSpecialization;
		
		return this;
	}
	
	public RenderComponent setText(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			Color tint,
			int renderOrder,
			boolean renderProjected,
			String textString,
			String textFont) {
		this.setGeneral(positionX, positionY, rotation, positionUnit, tint, renderOrder, renderProjected);
		this.textRenderSpecialization.textString = textString;
		this.textRenderSpecialization.textFont = textFont;
		
		this.specialization = this.textRenderSpecialization;
		
		return this;
	}

	@Override
	public void reset() {
		this.setGeneral(0, 0, 0, null, null, 0, false);
		this.spriteSpecialization.spriteTexture = null;
		this.textRenderSpecialization.textString = null;
		this.textRenderSpecialization.textFont = null;
	}
}
