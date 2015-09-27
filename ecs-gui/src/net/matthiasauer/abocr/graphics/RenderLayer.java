package net.matthiasauer.abocr.graphics;

public enum RenderLayer {
	Tiles(1, true),
	TileOwner(25, true),
	Cities(85, true),
	UnitType(100, true),
	Unsupplied(125, true),
	UnitSelection(150, true),
	UI(1000, false);

	public final Integer order;
	public final boolean projected;
	
	private RenderLayer(int order, boolean projected) {
		this.order = order;
		this.projected = projected;
	}
}
