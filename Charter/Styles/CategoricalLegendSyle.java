package thesis.Charter.Styles;

import java.awt.Color;

public interface CategoricalLegendSyle {
	// Legend
	public abstract boolean getDrawLegendOutline();
	public abstract Color getLegendOutlineColor();
	public abstract int getLegendOutlineWidth();
	public abstract Color getLegendTextColor();
	public abstract Color getLegendBackgroundColor();
}
