package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Helpers.Palette;

public class PolarAreaPlot extends Plot{

	private Color[] colorPalette = Palette.Default;
	private int fillOpacity = 150;
	
	private boolean outlines = true;
	private int outlineWidth = 2;
	
	public void drawPlot(Graphics2D g, String[] categories, Double[] values, String[] ticks, NoAxisChartMeasurements cm) {
		double currentAngle = -90;
		double angleDelta = ((double)360) / values.length;
		
		int xMid = cm.imageLeftToPlotMidWidth();
		int yMid = cm.imageBottomToPlotMidHeight();
		
		double maxTick = Double.valueOf(ticks[ticks.length - 1]);
		
		for (int categoryCount = 0; categoryCount < values.length; categoryCount++) {
			int radius = valueToRadius(values[categoryCount], maxTick, cm.getPlotWidth()/2);
			
			Color color = this.colorPalette[categoryCount];
			g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), this.fillOpacity));

			Arc2D arc = new Arc2D.Double(xMid - radius, yMid - radius, radius * 2, radius * 2, (int)currentAngle, (int)angleDelta, Arc2D.PIE);
			g.fill(arc);
			
			
			if (this.outlines) {				
				g.setColor(color);
				g.setStroke(new BasicStroke(this.outlineWidth));
				g.draw(arc);
			}
			
			currentAngle +=  angleDelta;
		}
		
	}
	
	private int valueToRadius(double value, double maxTick, int fullRadius) {
		return (int)(fullRadius * ((double)value/maxTick));
	}

	public Color[] getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(Color[] colorPalette) {
		this.colorPalette = colorPalette;
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
	
	
	
}
