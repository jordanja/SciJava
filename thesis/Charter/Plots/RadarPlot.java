package thesis.Charter.Plots;

import java.awt.Color;
import java.util.HashMap;

import thesis.Charter.ChartMeasurements.PieChartMeasurements;
import thesis.Helpers.Palette;

public class RadarPlot extends Plot{
	
	private Color[] colorPalette = Palette.Default;
	
	public Color[] getColorPalette() {
		return this.colorPalette;
	}

	public void setColorPalette(Color[] palette) {
		this.colorPalette = palette;
	}

	public void drawPlot(HashMap<String, HashMap<String, Double>> data, String[] uniqueRadarCategories,
			String[] uniqueValueCategories, int axisRadius, PieChartMeasurements cm) {
		System.out.println("draw plot");
	}
}
