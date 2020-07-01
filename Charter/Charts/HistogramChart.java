package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.util.Arrays;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Plots.HistogramPlot;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.Styles.ChartJSStyle;
import thesis.Charter.Styles.ExcelStyle;
import thesis.Charter.Styles.KidsStyle;
import thesis.Charter.Styles.MatplotlibStyle;
import thesis.Charter.Styles.NighttimeStyle;
import thesis.Charter.Styles.SeabornStyle;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.StyleFactory;
import thesis.Charter.Styles.Styles;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class HistogramChart extends XYChart {
	
	NumericAxis axis;
	HistogramPlot plot;
	
	private int numBins;
	
	
	public HistogramChart(DataFrame dataFrame, String xAxis) {
		super(dataFrame, dataFrame.getColumnAsDataItemArray(xAxis));
		
		this.axis = new NumericAxis();
		this.plot = new HistogramPlot();
		
		this.cm = new XYChartMeasurements();

	}

	@Override
	public void create() {
		Double[] values = DataItem.convertToDoubleList(this.yData);
		Arrays.sort(values);
		
		if (this.numBins <= 0) {
			this.numBins = calculateNumBins(values);
		}
		double binSize = (values[values.length - 1] - values[0])/this.numBins;
		int[] binCount = new int[this.numBins];
		for (int valueCount = 0; valueCount < values.length; valueCount++) {
			binCount[whichBin(values[valueCount], values, binSize)]++;
		}
		
		String[] xTicks = new String[this.numBins + 1];
		for (int i = 0; i <= this.numBins; i++) {
			xTicks[i] = DrawString.formatDoubleForDisplay(values[0] + i * binSize);
			
		}
		
		this.axis.setXTicks(xTicks);
		this.axis.calculateYAxis(0.0, (double)CommonArray.maxValue(binCount));
		
		this.cm.calculateChartImageMetrics(this.axis, getTitle(), getTitleFont());
		
		expandXAxisForEdgeValues(xTicks);
		
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		this.drawBackground(g, this.cm);

		this.plot.drawPlotBackground(g, this.cm);
		
		this.axis.setDrawLeftmostXAxisValue(true);
		this.axis.setDrawRightmostXAxisValue(true);
		this.axis.drawAxis(g, this.cm);
		
		this.plot.drawPlotOutline(g, this.cm);

		this.axis.drawAxisTicks(g, this.cm);

		this.plot.drawPlot(g, this.axis, binCount, binSize, values, this.cm);

		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);

		this.drawTitle(g, this.cm);
	}

	private void expandXAxisForEdgeValues(String[] xTicks) {
		int axisPadding = 10;		
		int halfLeftMostTickWidth = DrawString.getStringWidth(xTicks[0], this.axis.getXAxisFont(), this.axis.getXAxisRotation())/2;
		if (this.cm.imageLeftToPlotLeftWidth() < halfLeftMostTickWidth + axisPadding) {			
			this.cm.setLeftAxisToLeftTicksWidth(halfLeftMostTickWidth + axisPadding);
		}
		
		int halfRightMostTickWidth = DrawString.getStringWidth(xTicks[xTicks.length - 1], this.axis.getXAxisFont(), this.axis.getXAxisRotation())/2;
		if (this.cm.getPlotWidth() - this.cm.imageLeftToPlotRightWidth() < halfRightMostTickWidth + axisPadding) {
			this.cm.setRightTicksToRightAxisWidth(halfRightMostTickWidth + axisPadding);
		}
	}

	private int whichBin(double value, Double[] values, double binSize) {
		return CommonMath.clamp((int) ((value - values[0])/binSize), 0, this.numBins - 1);
		
	}
	
	private int calculateNumBins(Double[] values) {
		double std = CommonMath.standardDeviation(values);
		double numerator = 3.49 * std;
		double denominator = Math.cbrt(values.length);
		double h = numerator / denominator;
		double range = values[values.length - 1] - values[0];
		int numBins = (int) Math.floor(range/h) + 1;
		return numBins;
	}
	
	public void setStyle(Styles style) {
		Style styleToSet = StyleFactory.getStyle(style);
		this.axis.setStyle(styleToSet);
		this.plot.setStyle(styleToSet);
		this.cm.setStyle(styleToSet);
		
		this.setTitleFont(styleToSet.getTitleFont());
		this.setTitleColor(styleToSet.getTitleColor());
		this.setImageBackgroundColor(styleToSet.getChartBackgroundColor());
	}
	/*
	 * {
	 * 	  bucketSize: 4,
	 *    histValues:
	 * 
	 * 
	 * }
	 */
	
}
