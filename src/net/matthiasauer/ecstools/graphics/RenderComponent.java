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
	private final SpriteRenderSpecialization spriteSpecialization =
			new SpriteRenderSpecialization();
	private final TextRenderSpecialization textSpecialization =
			new TextRenderSpecialization();
	private RenderSpecialization specializationType;
	
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
	
	public TextRenderSpecialization getTextSpecialization() {
		if (this.getSpecializationType() == RenderSpecialization.Text) {
			return this.textSpecialization;
		} else {
			return null;
		}
	}
	
	public SpriteRenderSpecialization getSpriteSpecialization() {
		if (this.getSpecializationType() == RenderSpecialization.Sprite) {
			return this.spriteSpecialization;
		} else {
			return null;
		}
	}
	
	public RenderSpecialization getSpecializationType() {
		return this.specializationType;
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
		
		this.specializationType = RenderSpecialization.Sprite;
		
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
		this.textSpecialization.textString = textString;
		this.textSpecialization.textFont = textFont;
		
		this.specializationType = RenderSpecialization.Text;
		
		return this;
	}

	@Override
	public void reset() {
		this.setGeneral(0, 0, 0, null, null, 0, false);
		this.spriteSpecialization.spriteTexture = null;
		this.textSpecialization.textString = null;
		this.textSpecialization.textFont = null;
		this.specializationType = null;
	}
}
