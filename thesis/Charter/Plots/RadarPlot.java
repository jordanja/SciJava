package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.HashMap;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Helpers.Palette;

public class RadarPlot extends Plot{
	
	private Color[] colorPalette = Palette.Default;
	private int fillOpacity = 25;
	private boolean fill = true;
	
	private boolean outlines = true;
	private int outlineWidth = 2;
	
	private boolean markerDots = true;
	private int markerDotRadius = 10;
	


	public void drawPlot(Graphics2D g, HashMap<String, HashMap<String, Double>> data, String[] uniqueRadarCategories,
			String[] uniqueValueCategories, int axisRadius, String[] ticks, NoAxisChartMeasurements cm) {
		int categoryCount = 0;
		for (String category: uniqueRadarCategories) {
			HashMap<String, Double> categoryData = data.get(category);
			GeneralPath radar = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			
			int valueCategoryCount = 0;
			for (String valueCategory: uniqueValueCategories) {
				double angle = Math.PI * 2 * (-(double)valueCategoryCount/uniqueRadarCategories.length) + Math.PI/2;

				int x = angleToXPos(angle, data.get(category).get(valueCategory), Double.valueOf(ticks[ticks.length - 1]).intValue(), axisRadius, cm);
				int y = angleToYPos(angle, data.get(category).get(valueCategory), Double.valueOf(ticks[ticks.length - 1]).intValue(), axisRadius, cm);
				PathIterator path = radar.getPathIterator(null);
				
				if (valueCategoryCount == 0) {
					radar.moveTo(x, y);					
				} else {					
					radar.lineTo(x, y);
				}
				
				valueCategoryCount++;
			}
			
			radar.closePath();
			
			Color color = colorPalette[categoryCount];
			
			if (this.fill) {
				g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillOpacity));
				g.fill(radar);				
			}
			
			if (this.outlines) {
				g.setStroke(new BasicStroke(this.outlineWidth));
				g.setColor(color);
				g.draw(radar);				
			}
			
			if (this.markerDots) {
				PathIterator path = radar.getPathIterator(null);
				while(!path.isDone()) {
					double[] coordinates = new double[6];
				    int type = path.currentSegment(coordinates);
				    int x = (int) coordinates[0];
				    int y = (int) coordinates[1];
				    
				    drawDataPoint(g, x, y, color);
				    path.next();
				    
				}

				

			}
			
			categoryCount++;
		}
		
		
	}
	
	private void drawDataPoint(Graphics2D g, int xCenter, int yCenter, Color dataPointColor) {
		
		g.setColor(dataPointColor);
		g.fillOval(xCenter - this.markerDotRadius/2, yCenter - this.markerDotRadius/2, this.markerDotRadius, this.markerDotRadius);
		

	}
	
	private int angleToXPos(double angle, double value, int maxTick, int fullRadius, NoAxisChartMeasurements cm) {
		return (int) (fullRadius * (value/maxTick) * Math.cos(angle) + cm.imageLeftToPlotMidWidth());
	}
	private int angleToYPos(double angle, double value, int maxTick, int fullRadius, NoAxisChartMeasurements cm) {
		return (int) (fullRadius * (value/maxTick) * Math.sin(angle) + cm.imageBottomToPlotMidHeight());
	}

	public Color[] getColorPalette() {
		return this.colorPalette;
	}

	public void setColorPalette(Color[] palette) {
		this.colorPalette = palette;
	}
	
	
	public boolean isFill() {
		return fill;
	}
	public void setFill(boolean fill) {
		this.fill = fill;
	}


	public int getFillOpacity() {
		return fillOpacity;
	}
	public void setFillOpacity(int fillOpacity) {
		this.fillOpacity = fillOpacity;
	}

	public boolean isOutlines() {
		return outlines;
	}
	public void setOutlines(boolean outlines) {
		this.outlines = outlines;
	}

	public int getOutlineWidth() {
		return outlineWidth;
	}
	public void setOutlineWidth(int outlineWidth) {
		this.outlineWidth = outlineWidth;
	}

	public boolean isMarkerDots() {
		return markerDots;
	}
	public void setMarkerDots(boolean markerDots) {
		this.markerDots = markerDots;
	}

	public int getMarkerDotRadius() {
		return markerDotRadius;
	}
	public void setMarkerDotRadius(int markerDotRadius) {
		this.markerDotRadius = markerDotRadius;
	}
	
	
}
