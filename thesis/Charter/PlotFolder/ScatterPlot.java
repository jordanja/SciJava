package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Common.MathHelpers;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class ScatterPlot extends Plot {

	
	private boolean includeLinearRegression;
	private Color linearRegLineColor = Color.BLACK;
	
	private int dataPointRadius = 10;
	private boolean includeDataPointOutline;
	private int outlineWidth = 2;
	private Color dataPointOutlineColor = Color.WHITE;
	
	private Color dataPointColor = Color.BLACK;
	private float dataPointTransparency = 1;
	
	private Color[] colorPalette = Palette.Default;

	public ScatterPlot() {
		
	}

	public void setDataPointColor(Color color) {
		this.dataPointColor = color;
	}
	
	public void setDataPointTransparency(float dataPointTransparency) {
		this.dataPointTransparency = dataPointTransparency;
	}
	
	public void setDataPointRadius(int radius) {
		this.dataPointRadius = radius;
	}
	
	public void includeDataPointOutline(boolean includeDataPointOutline) {
		this.includeDataPointOutline = includeDataPointOutline;
	}
	
	public void setOutlineColor(Color dataPointOutlineColor) {
		this.dataPointOutlineColor = dataPointOutlineColor;
	}
	
	public void setOutlineWidth(int width) {
		this.outlineWidth = width;
	}

	public Color[] getColorPalette() {
		return this.colorPalette;
	}
	public void setColorPalette(Color[] colorPalette) {
		this.colorPalette = colorPalette;
	}
	
	public void includeLinearRegression(boolean includeLinearRegression) {
		this.includeLinearRegression = includeLinearRegression;
	}

	public void setLinearRegressionLineColor(Color linearRegLineColor) {
		this.linearRegLineColor = linearRegLineColor;
	}
	
	
	
	public boolean getIncludeLinearRegression() {
		return this.includeLinearRegression;
	}
	
	
	
	public void drawLinearRegression(Graphics2D g, NumericAxis axis, DataItem[] xData, DataItem[] yData, XYChartMeasurements cm) {
		
		if (this.includeLinearRegression != true) {
			return;
		}
		double[] xTicks = Arrays.stream(axis.getXTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
		double[] yTicks = Arrays.stream(axis.getYTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
		
		NiceScale xNS = axis.getxNS();
		NiceScale yNS = axis.getyNS();
		
		double meanX = MathHelpers.average(xData);
		
		double meanY = MathHelpers.average(yData);
		
		double varianceX = MathHelpers.variance(xData);
		
		double covarianceXY = MathHelpers.covariance(xData, yData, meanX, meanY);
		
		double w1 = covarianceXY/varianceX;
		
		double w0 = meanY - w1 * meanX;
		// y = w0 + w1 * x
		
		double x0 = xNS.getNiceMin();
		double y0 = w0 + w1 * xNS.getNiceMin();
		
		double x1 = xTicks[xTicks.length - 1];
		double y1 = w0 + w1 * xTicks[xTicks.length - 1];
		
		int xPos0 = (int)worldXPosToPlotXPos(x0, xNS, xTicks, cm);
		int yPos0 = (int)worldYPosToPlotYPos(y0, yNS, yTicks, cm);
		
		int xPos1 = (int)worldXPosToPlotXPos(x1, xNS, xTicks, cm);
		int yPos1 = (int)worldYPosToPlotYPos(y1, yNS, yTicks, cm);

		
		g.setColor(this.linearRegLineColor);
		g.drawLine(Math.max(xPos0, cm.imageLeftToPlotLeftWidth()), Math.max(yPos0, cm.imageBottomToPlotBottomHeight()), xPos1, yPos1);
		
	}
	

	public void drawPlot(Graphics2D g, NumericAxis axis, DataItem[] xData, DataItem[] yData, Object[] colorCodeValues, XYChartMeasurements cm) {
		double[] xTicks = Arrays.stream(axis.getXTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
		double[] yTicks = Arrays.stream(axis.getYTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
		
		g.setStroke(new BasicStroke(this.outlineWidth));

		for (int dataPointNumber = 0; dataPointNumber < xData.length; dataPointNumber++) {
			
			int xPos = (int)worldXPosToPlotXPos(xData[dataPointNumber].getDoubleValue(), axis.getxNS(), xTicks,cm);
			int yPos = (int)worldYPosToPlotYPos(yData[dataPointNumber].getDoubleValue(), axis.getyNS(), yTicks,cm);
//			System.out.println(xPos + " " + yPos);
			drawDataPoint(g, xPos, yPos, dataPointNumber, colorCodeValues);
			
		}
	}
	
	private void drawDataPoint(Graphics2D g, int xCenter, int yCenter, int dataPointNumber, Object[] colorCodeValues) {
		
		if (colorCodeValues == null) {		
			
			g.setColor(new Color(this.dataPointColor.getRed(), this.dataPointColor.getGreen(), this.dataPointColor.getBlue(), Math.round(this.dataPointTransparency*255)));
		} else {
			String[] uniquecolorCodeValues = new HashSet<>(Arrays.asList(colorCodeValues)).toArray(new String[0]);

			
			int colorCodeValue = MathHelpers.elementNumInArray(uniquecolorCodeValues, colorCodeValues[dataPointNumber]) % (this.colorPalette.length - 1);
			g.setColor(this.colorPalette[colorCodeValue]);
			
		}
		
		g.fillOval(xCenter - this.dataPointRadius/2, yCenter - this.dataPointRadius/2, this.dataPointRadius, this.dataPointRadius);
		
		
		if (this.includeDataPointOutline) {			
			g.setColor(this.dataPointOutlineColor);
			g.drawOval(xCenter - this.dataPointRadius/2, yCenter - this.dataPointRadius/2, this.dataPointRadius, this.dataPointRadius);
		}
	}
	
	private double worldXPosToPlotXPos(double xPos, NiceScale xNS, double[] xTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(xPos, xNS.getNiceMin(), xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(), cm.imageLeftToPlotRightWidth());
	}
	
	private double worldYPosToPlotYPos(double yPos, NiceScale yNS, double[] yTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(yPos, yNS.getNiceMin(), yTicks[yTicks.length - 1 ], cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
	}


}
