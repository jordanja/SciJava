package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Styles.Style;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
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

	
	private int smallestRadius = 6;
	private int largestRadius = 40;

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

		if (!this.includeLinearRegression) {
			return;
		}
		double[] xTicks = axis.getXTicksValues();
		double[] yTicks = axis.getYTicksValues();

		double meanX = CommonMath.average(xData);

		double meanY = CommonMath.average(yData);

		double varianceX = CommonMath.variance(xData);

		double covarianceXY = CommonMath.covariance(xData, yData, meanX, meanY);

		double w1 = covarianceXY / varianceX;

		double w0 = meanY - w1 * meanX;
		// y = w0 + w1 * x

		double x0 = xTicks[0];
		double y0 = w0 + w1 * xTicks[0];

		double x1 = xTicks[xTicks.length - 1];
		double y1 = w0 + w1 * xTicks[xTicks.length - 1];

		int xPos0 = worldXPosToPlotXPos(x0, xTicks, cm);
		int yPos0 = worldYPosToPlotYPos(y0, yTicks, cm);

		int xPos1 = worldXPosToPlotXPos(x1, xTicks, cm);
		int yPos1 = worldYPosToPlotYPos(y1, yTicks, cm);

		g.setColor(this.linearRegLineColor);
		g.drawLine(Math.max(xPos0, cm.imageLeftToPlotLeftWidth()), Math.max(yPos0, cm.imageBottomToPlotBottomHeight()), xPos1, yPos1);

	}

	public void drawPlot(Graphics2D g, NumericAxis axis, Map<String, Object>[] data, XYChartMeasurements cm) {
		double[] xTicks = axis.getXTicksValues();
		double[] yTicks = axis.getYTicksValues();
		
		boolean hasColorCode = data[0].containsKey("color");
		boolean hasSize = data[0].containsKey("size");

		String[] uniqueColorCodeValus = null;
		if (hasColorCode) {
			uniqueColorCodeValus = CommonArray.removeDuplicates(CommonArray.getAllValuesOfKey(data, "color"));
		}
		
		for (int dataPointNumber = 0; dataPointNumber < data.length; dataPointNumber++) {
			
			int xPos = worldXPosToPlotXPos((double)data[dataPointNumber].get("x"), xTicks, cm);
			int yPos = worldYPosToPlotYPos((double)data[dataPointNumber].get("y"), yTicks, cm);

			Color dataPointColor;
			
			if (hasColorCode) {
				int colorCodeValue = CommonMath.elementNumInArray(uniqueColorCodeValus, data[dataPointNumber].get("color")) % (this.colorPalette.length - 1);
				dataPointColor = this.colorPalette[colorCodeValue];
			} else {			
				dataPointColor = new Color(this.dataPointColor.getRed(), this.dataPointColor.getGreen(), this.dataPointColor.getBlue(), Math.round(this.dataPointTransparency * 255));
			}
			
			if (hasSize) {
				Double[] sizeValues = CommonArray.getAllDoubleValuesOfKey(data, "size");
				double minSize = CommonArray.minValue(sizeValues);
				double maxSize = CommonArray.maxValue(sizeValues);
				int radius = CommonMath.map((double)data[dataPointNumber].get("size"), minSize, maxSize, (double)this.smallestRadius, (double)this.largestRadius);
				drawDataPoint(g, xPos, yPos, dataPointNumber, dataPointColor, radius);

			} else {
				drawDataPoint(g, xPos, yPos, dataPointNumber, dataPointColor, this.dataPointRadius);

			}
			

		}
	}

	private void drawDataPoint(Graphics2D g, int xCenter, int yCenter, int dataPointNumber, Color dataPointColor, int radius) {

		g.setColor(dataPointColor);

		g.fillOval(xCenter - radius / 2, yCenter - radius / 2, radius, radius);

		if (this.includeDataPointOutline) {
			g.setColor(this.dataPointOutlineColor);
			g.setStroke(new BasicStroke(this.outlineWidth));
			g.drawOval(xCenter - radius / 2, yCenter - radius / 2, radius, radius);
		}
	}

	private int worldXPosToPlotXPos(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(),
				cm.imageLeftToPlotRightWidth());
	}

	private int worldYPosToPlotYPos(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

	public int getSmallestRadius() {
		return smallestRadius;
	}

	public void setSmallestRadius(int smallestRadius) {
		this.smallestRadius = smallestRadius;
	}

	public int getLargestRadius() {
		return largestRadius;
	}

	public void setLargestRadius(int largestRadius) {
		this.largestRadius = largestRadius;
	}

	public void setStyle(Style styleToSet) {
		super.setStyle(styleToSet);
		this.setDataPointColor(styleToSet.getColorPalette()[0]);		
	}
	

}
