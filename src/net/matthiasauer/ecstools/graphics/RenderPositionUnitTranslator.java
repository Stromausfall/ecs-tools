package net.matthiasauer.ecstools.graphics;

import com.badlogic.gdx.Gdx;

/**
 * Looks cumbersome but primitives are better for the GarbageCollector than a Vector2 !
 */
public class RenderPositionUnitTranslator {
	private static final int TILE_SIZE = 128;
	
	public static float translateX(float x, float y, RenderPositionUnit positionUnit) {
		float result = x;
		
		if (positionUnit == RenderPositionUnit.Tiles) {
			result = x * TILE_SIZE;
			boolean shiftXCoordinate =
					(y % 2) == 1;

			if (shiftXCoordinate) {
				result += TILE_SIZE / 2;
			}
		}
		if (positionUnit == RenderPositionUnit.Percent) {
			result =
					x * Gdx.graphics.getWidth() / 200;
		}
		
		return result;
	}
	
	public static float translateY(float x, float y, RenderPositionUnit positionUnit) {
		float result = y;
		
		if (positionUnit == RenderPositionUnit.Tiles) {
			result = y * TILE_SIZE;
		}
		if (positionUnit == RenderPositionUnit.Percent) {
			result =
					y * Gdx.graphics.getHeight() / 200;
		}
		
		return result;
	}
}