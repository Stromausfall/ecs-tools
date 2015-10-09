package net.matthiasauer.ecstools.graphics;

import java.util.Comparator;

/**
 * Order according primarily according to the renderOrder, if a draw - then 
 * using renderProjected
 */
final class RenderComponentComparator implements Comparator<RenderComponent> {
	@Override
	public int compare(final RenderComponent o1, final RenderComponent o2) {
		final int result = Integer.compare(o1.renderOrder, o2.renderOrder);
		
		if (result != 0) {
			// if comparing the order was enough
			return result;
		}
		
		// otherwise they have the same order !
		return Boolean.compare(o1.renderProjected, o2.renderProjected);
	}
}
