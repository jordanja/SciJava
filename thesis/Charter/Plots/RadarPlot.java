package thesis.Charter.Plots;

import java.awt.Color;

import thesis.Helpers.Palette;

public class RadarPlot extends Plot{
	
	private Color[] colorPalette = Palette.Default;
	
	public Color[] getColorPalette() {
		return this.colorPalette;
	}

	public void setColorPalette(Color[] palette) {
		this.colorPalette = palette;
	}
}
