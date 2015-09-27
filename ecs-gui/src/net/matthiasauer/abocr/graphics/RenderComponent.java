package net.matthiasauer.abocr.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderComponent implements Component, Poolable {
	public final Vector2 position = new Vector2();
	public float rotation;
	public RenderPositionUnit positionUnit;
	public RenderLayer layer;
	public Color tint;
	public RenderType renderType;

	public AtlasRegion spriteTexture;
	
	public String textString;
	public String textFont;
//	public int textSize;
	
	public RenderComponent() {
		this.reset();
	}
	
	public RenderComponent setText(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			RenderLayer layer,
			String textString,
			String textFont,
//			int textSize,
			Color tint) {
		this.setGeneral(positionX, positionY, rotation, positionUnit, layer, tint);
		
		this.textFont = textFont;
//		this.textSize = textSize;
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
			RenderLayer layer,
			Color tint) {
		this.setGeneral(positionX, positionY, rotation, positionUnit, layer, tint);
		
		this.spriteTexture = texture;
		this.renderType = RenderType.Sprite;
		
		return this;
	}
	
	private void setGeneral(
			float positionX,
			float positionY,
			float rotation,
			RenderPositionUnit positionUnit,
			RenderLayer layer,
			Color tint) {
		this.position.x = positionX;
		this.position.y = positionY;
		this.rotation = rotation;
		this.positionUnit = positionUnit;
		this.layer = layer;
		this.tint = tint;

		this.spriteTexture = null;
		this.textFont = null;
//		this.textSize = -1;
		this.textString = null;
		this.renderType = null;
	}
	
	@Override
	public void reset() {
		this.setSprite(
				0,
				0,
				0,
				null,
				null,
				null,
				null);
	}
}
