package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Common.MathHelpers;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class LinePlot extends Plot{

	private Color[] lineColorPalette = Palette.Default;

	private Color lineColor = Color.black;
	private int lineThickness = 2;
	
	private boolean drawMarkerDots = false;
	private Color markerDotColor = Color.black;
	private int markerDotRadius = 5;
	private boolean drawMarkerDotOutline = false;
	private Color markerDotOutlineColor = Color.white;
	private int markerDotOutlineWidth = 2;
	
	private boolean dashedLine = false;


	public void drawPlot(Graphics2D g, NumericAxis axis, HashMap<Object, Object> data, XYChartMeasurements cm) {
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);

		double[] xTicks = axis.getXTicksValues();
		double[] yTicks = axis.getYTicksValues();
		
		if (this.dashedLine) {
	        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0));
		} else {			
			g.setStroke(new BasicStroke(this.lineThickness));
		}
		
		if (hasMultipleLines) {
			String[] colorCodeValues = data.keySet().toArray(new String[0]);
			
			for (int lineCount = 0; lineCount < colorCodeValues.length; lineCount++) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeValues[lineCount]);
				g.setColor(this.lineColorPalette[lineCount % this.lineColorPalette.length]);
				drawLine(g, axis, lineData, cm, xTicks, yTicks);
			}
			
		} else {
			g.setColor(this.lineColor);
			drawLine(g, axis, (HashMap<Double, Double>)((Object)data), cm, xTicks, yTicks);
		}
	}





	private void drawLine(Graphics2D g, NumericAxis axis, HashMap<Double, Double> lineData, XYChartMeasurements cm, double[] xTicks, double[] yTicks) {
		Double[] xValues = lineData.keySet().toArray(new Double[0]);
		Arrays.sort(xValues);
		int[] xPoints = new int[xValues.length];
		int[] yPoints = new int[xValues.length];
		for (int i = 0; i < xValues.length; i++) {
			Double xValue = xValues[i];
			
			int xPos = (int)xPlotValueToXPixelValue(xValue, xTicks, cm);
			int yPos = (int)yPlotValueToYPixelValue((lineData.get(xValue)), yTicks, cm);
			
			xPoints[i] = xPos;
			yPoints[i] = yPos;
			
		}
		
		g.drawPolyline(xPoints, yPoints, xPoints.length);
		if (this.drawMarkerDots) {
			
			for (int i = 0; i < xValues.length; i++) {
				Double xValue = xValues[i];
				Double yValue = lineData.get(xValue);
				
				double xPos = xPlotValueToXPixelValue(xValue, xTicks, cm);
				double yPos = yPlotValueToYPixelValue(yValue, yTicks, cm);
				
				drawMarkerDot(g, (int)xPos, (int)yPos);
			}
		}
	}


	


	private void drawMarkerDot(Graphics2D g, int xCenter, int yCenter) {
		g.setColor(this.markerDotColor);
		g.fillOval(xCenter - this.markerDotRadius/2, yCenter - this.markerDotRadius/2, this.markerDotRadius, this.markerDotRadius);
		
		if (this.drawMarkerDotOutline) {
			g.setStroke(new BasicStroke(this.markerDotOutlineWidth));
			g.setColor(this.markerDotOutlineColor);
			g.drawOval(xCenter - this.markerDotRadius/2, yCenter - this.markerDotRadius/2, this.markerDotRadius, this.markerDotRadius);
		}
		
	}
	
	private double xPlotValueToXPixelValue(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(), cm.imageLeftToPlotRightWidth());
	}
	
	private double yPlotValueToYPixelValue(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(yPos, yTicks[0], yTicks[yTicks.length - 1 ], cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
	}
	
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	public Color getLineColor() {
		return this.lineColor;
	}
	
	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}
	public int getLineThickness() {
		return this.lineThickness;
	}
	
	public void setDrawMarkerDots(boolean drawMarkerDots) {
		this.drawMarkerDots = drawMarkerDots;
	}
	public boolean getDrawMarkerDots() {
		return this.drawMarkerDots;
	}
	
	public void setMarkerDotColor(Color markerDotColor) {
		this.drawMarkerDots = true;
		this.markerDotColor = markerDotColor;
	}
	public Color getMarkerDotColor() {
		return this.markerDotColor;
	}

	public void setMarkerDotRadius(int markerDotRadius) {
		this.drawMarkerDots = true;
		this.markerDotRadius = markerDotRadius;
	}
	public int getMarkerDotRadius() {
		return this.markerDotRadius;
	}
	
	public Color[] getLineColorPalette() {
		return lineColorPalette;
	}
	public void setLineColorPalette(Color[] lineColorPalette) {
		this.lineColorPalette = lineColorPalette;
	}
	
	public boolean getDrawMarkerDotOutline() {
		return drawMarkerDotOutline;
	}
	public void setDrawMarkerDotOutline(boolean drawMarkerDotOutline) {
		this.drawMarkerDotOutline = drawMarkerDotOutline;
	}

	public Color getMarkerDotOutlineColor() {
		return markerDotOutlineColor;
	}
	public void setMarkerDotOutlineColor(Color markerDotOutlineColor) {
		this.markerDotOutlineColor = markerDotOutlineColor;
		this.drawMarkerDotOutline = true;
	}

	public int getMarkerDotOutlineWidth() {
		return markerDotOutlineWidth;
	}
	public void setMarkerDotOutlineWidth(int markerDotOutlineWidth) {
		this.markerDotOutlineWidth = markerDotOutlineWidth;
		this.drawMarkerDotOutline = true;
	}
	
	public boolean getDashedLine() {
		return this.dashedLine;
	}
	public void setDashedLine(boolean dashedLine) {
		this.dashedLine = dashedLine;
	}

}
