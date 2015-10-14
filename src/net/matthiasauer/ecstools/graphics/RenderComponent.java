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
	public RenderType renderType;
	public int renderOrder;
	public boolean renderProjected;

	public AtlasRegion spriteTexture;
	
	public String textString;
	public String textFont;
	
	public RenderComponent() {
		this.reset();
	}
	
	public RenderComponent setText(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			int renderOrder,
			boolean renderProjected,
			String textString,
			String textFont,
			Color tint) {
		this.setGeneral(positionX, positionY, rotation, positionUnit, renderOrder, renderProjected, tint);
		
		this.textFont = textFont;
		this.textString = textString;
		this.renderType = RenderType.Text;
		
		return this;
	}
	
	public RenderComponent setSprite(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			AtlasRegion texture,
			int renderOrder,
			boolean renderProjected,
			Color tint) {
		this.setGeneral(positionX, positionY, rotation, positionUnit, renderOrder, renderProjected, tint);
		
		this.spriteTexture = texture;
		this.renderType = RenderType.Sprite;
		
		return this;
	}
	
	private void setGeneral(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			int renderOrder,
			boolean renderProjected,
			Color tint) {
		this.position.x = positionX;
		this.position.y = positionY;
		this.rotation = rotation;
		this.positionUnit = positionUnit;
		this.renderOrder = renderOrder;
		this.renderProjected = renderProjected;
		this.tint = tint;

		this.spriteTexture = null;
		this.textFont = null;
		this.textString = null;
		this.renderType = null;
	}
	
	@Override
	public void reset() {
		this.setGeneral(0, 0, 0, null, 0, false, null);
	}
}
