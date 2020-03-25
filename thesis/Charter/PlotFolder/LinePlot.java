package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonMath;
import thesis.Helpers.Palette;

public class LinePlot extends Plot {

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

	private boolean stepPlot = false;

	private boolean shadeUnderLine = false;

	public void drawPlot(Graphics2D g, NumericAxis axis, HashMap<Object, Object> data, XYChartMeasurements cm) {
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);

		double[] xTicks = axis.getXTicksValues();
		double[] yTicks = axis.getYTicksValues();

		if (this.dashedLine) {
			g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 9 }, 0));
		} else {
			g.setStroke(new BasicStroke(this.lineThickness));
		}

		if (hasMultipleLines) {
			String[] colorCodeValues = data.keySet().toArray(new String[0]);

			for (int lineCount = 0; lineCount < colorCodeValues.length; lineCount++) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeValues[lineCount]);
				Color lineColor = this.lineColorPalette[lineCount % this.lineColorPalette.length];
				
				Double[] xValues = lineData.keySet().toArray(new Double[0]);
				Arrays.sort(xValues);
				
				int[] xPoints = getXLinePoints(xValues, cm, xTicks);
				int[] yPoints = getYLinePoints(xValues, lineData, cm, yTicks);
				
				fillUnderLine(g, cm, yTicks, lineColor, xPoints, yPoints);
				drawLine(g, lineColor, xPoints, yPoints);
			}

		} else {
			HashMap<Double, Double> lineData = (HashMap<Double, Double>) ((Object) data);
			
			Double[] xValues = lineData.keySet().toArray(new Double[0]);
			Arrays.sort(xValues);
			
			int[] xPoints = getXLinePoints(xValues, cm, xTicks);
			int[] yPoints = getYLinePoints(xValues, lineData, cm, yTicks);
			
			fillUnderLine(g, cm, yTicks, this.lineColor, xPoints, yPoints);
			drawLine(g, this.lineColor, xPoints, yPoints);
		}
	}

	private void fillUnderLine(Graphics2D g, XYChartMeasurements cm, double[] yTicks, Color fillColor, int[] xPoints, int[] yPoints) {
		if (this.shadeUnderLine) {
			GeneralPath underCurve = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			int xStart = xPoints[0];
			int yStart = yPlotValueToYPixelValue(0, yTicks, cm);
			underCurve.moveTo(xStart, yStart);
			
			for (int i = 0; i < xPoints.length; i++) {
				underCurve.lineTo(xPoints[i], yPoints[i]);
			}
			
			int xEnd = xPoints[xPoints.length - 1];
			underCurve.lineTo(xEnd, yStart);
			
			underCurve.closePath();
			g.setColor(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 50));
			
			g.fill(underCurve);

		}
	}

	private void drawLine(Graphics2D g, Color lineColor, int[] xPoints, int[] yPoints) {
		
		g.setColor(lineColor);
		g.drawPolyline(xPoints, yPoints, xPoints.length);
		
		if (this.drawMarkerDots) {
			for (int i = 0; i < xPoints.length; i++) {
				drawMarkerDot(g, xPoints[i], yPoints[i]);
			}
		}

	}

	private int[] getXLinePoints(Double[] xValues, XYChartMeasurements cm, double[] xTicks) {
		int[] xPoints;
		if (this.stepPlot) {
			xPoints = new int[xValues.length * 2 - 1];

			for (int i = 0; i < xValues.length; i++) {
				Double currentXValue = xValues[i];
				xPoints[i * 2] = xPlotValueToXPixelValue(currentXValue, xTicks, cm);

				if (i < xValues.length - 1) {
					Double nextXValue = xValues[i + 1];
					xPoints[i * 2 + 1] = xPlotValueToXPixelValue(nextXValue, xTicks, cm);
				}
			}

		} else {
			xPoints = new int[xValues.length];

			for (int i = 0; i < xValues.length; i++) {
				Double xValue = xValues[i];
				xPoints[i] = xPlotValueToXPixelValue(xValue, xTicks, cm);
			}
		}
		return xPoints;
	}
	
	private int[] getYLinePoints(Double[] xValues, HashMap<Double, Double> lineData, XYChartMeasurements cm, double[] yTicks) {
		int[] yPoints;
		if (this.stepPlot) {
			yPoints = new int[xValues.length * 2 - 1];
			
			for (int i = 0; i < xValues.length; i++) {
				yPoints[i * 2] = yPlotValueToYPixelValue((lineData.get(xValues[i])), yTicks, cm);

				if (i < xValues.length - 1) {	
					yPoints[i * 2 + 1] = yPlotValueToYPixelValue((lineData.get(xValues[i])), yTicks, cm);
				}
			}

		} else {
			yPoints = new int[xValues.length];

			for (int i = 0; i < xValues.length; i++) {
				Double xValue = xValues[i];
				yPoints[i] = yPlotValueToYPixelValue((lineData.get(xValue)), yTicks, cm);

			}
		}
		
		return yPoints;
	}

	private void drawMarkerDot(Graphics2D g, int xCenter, int yCenter) {
		g.setColor(this.markerDotColor);
		g.fillOval(xCenter - this.markerDotRadius / 2, yCenter - this.markerDotRadius / 2, this.markerDotRadius,
				this.markerDotRadius);

		if (this.drawMarkerDotOutline) {
			g.setStroke(new BasicStroke(this.markerDotOutlineWidth));
			g.setColor(this.markerDotOutlineColor);
			g.drawOval(xCenter - this.markerDotRadius / 2, yCenter - this.markerDotRadius / 2, this.markerDotRadius,
					this.markerDotRadius);
		}

	}

	private int xPlotValueToXPixelValue(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(),
				cm.imageLeftToPlotRightWidth());
	}

	private int yPlotValueToYPixelValue(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
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

	public boolean isStepPlot() {
		return this.stepPlot;
	}

	public void setStepPlot(boolean stepPlot) {
		this.stepPlot = stepPlot;
	}

	public boolean isShadeUnderLine() {
		return shadeUnderLine;
	}

	public void setShadeUnderLine(boolean shadeUnderLine) {
		this.shadeUnderLine = shadeUnderLine;
	}

}
