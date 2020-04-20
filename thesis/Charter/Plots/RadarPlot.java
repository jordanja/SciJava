package thesis.Charter.Plots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
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

	public void drawPlot(Graphics2D g, HashMap<String, HashMap<String, Double>> data, String[] uniqueRadarCategories,
			String[] uniqueValueCategories, int axisRadius, String[] ticks, PieChartMeasurements cm) {
		int categoryCount = 0;
		for (String category: uniqueRadarCategories) {
			HashMap<String, Double> categoryData = data.get(category);
			GeneralPath slice = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			
			int valueCategoryCount = 0;
			for (String valueCategory: uniqueValueCategories) {
				double angle = Math.PI * 2 * (-(double)valueCategoryCount/uniqueRadarCategories.length) + Math.PI/2;

				int x = angleToXPos(angle, data.get(category).get(valueCategory), Double.valueOf(ticks[ticks.length - 1]).intValue(), axisRadius, cm);
				int y = angleToYPos(angle, data.get(category).get(valueCategory), Double.valueOf(ticks[ticks.length - 1]).intValue(), axisRadius, cm);
				PathIterator path = slice.getPathIterator(null);
				
				if (valueCategoryCount == 0) {
					slice.moveTo(x, y);					
				} else {					
					slice.lineTo(x, y);
				}
				
				valueCategoryCount++;
			}
			System.out.println("");
			
			slice.closePath();
			g.setColor(new Color(colorPalette[categoryCount].getRed(), colorPalette[categoryCount].getGreen(), colorPalette[categoryCount].getBlue(), 100));

			g.fill(slice);
			
			categoryCount++;
		}
		
		
	}
	
	
	private int angleToXPos(double angle, double value, int maxTick, int fullRadius, PieChartMeasurements cm) {
		return (int) (fullRadius * (value/maxTick) * Math.cos(angle) + cm.imageLeftToPlotMidWidth());
	}
	
	private int angleToYPos(double angle, double value, int maxTick, int fullRadius, PieChartMeasurements cm) {
		return (int) (fullRadius * (value/maxTick) * Math.sin(angle) + cm.imageBottomToPlotMidHeight());
	}
}
