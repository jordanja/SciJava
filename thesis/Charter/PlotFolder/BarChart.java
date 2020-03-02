package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.BarChartMeasurements;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class BarChart extends XYChart{

	BarChartAxis axis;
	BarPlot plot;
	Legend legend;
	
	
	
	public BarChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Bar");
		
		this.axis = new BarChartAxis();
		this.plot = new BarPlot();
		this.legend = new Legend();
		
		cm = new BarChartMeasurements();

	}

	

	@Override
	public void Create() {
		
		String[] xDataFormatted = getXDataFormatted();
		double[] yDataFormatted = getYDataFormatted(xDataFormatted);
		
		
		this.axis.setXAxis(xDataFormatted);
		this.axis.setYAxis(yDataFormatted);
		
		
		if (this.legend.getIncludeLegend()) {
			Object[] hueValies = {"a", "b"};
			this.legend.calculateLegend("Hello", hueValies);
		}
		
		cm.calculateChartImageMetrics(this.axis, this.plot, this.legend, getTitle(), getTitleFont());
		
		instantiateChart(cm);
		
		Graphics2D g = initializaGraphicsObject(cm);
		drawBackground(g, cm);
		
		this.plot.drawChartBackground(g, cm);
		
		this.axis.drawAxis(g, cm);
		
		this.plot.drawPlotOutline(g, cm);
		
		this.axis.drawAxisTicks(g, cm);
		
		this.plot.drawPlot(g, this.axis, xDataFormatted, yDataFormatted, cm);
		
		this.axis.drawXAxisLabel(g, cm);
		this.axis.drawYAxisLabel(g, cm);
		
//		if (this.legend.getIncludeLegend()) {
//			this.legend.drawLegend(g, cm, this.plot.getColorPalette());
//		}
		
		this.drawTitle(g, cm);
	}



	private double[] getYDataFormatted(String[] xDataFormatted) {
		HashMap<String, Double> runningTotals = new HashMap<String, Double>();
		HashMap<String, Integer> runningCount = new HashMap<String, Integer>();
		for (String xCatagory: xDataFormatted) {
			runningTotals.put(xCatagory, (double) 0);
			runningCount.put(xCatagory, 0);
		}
		
		for (int i = 0; i < this.xData.length; i++) {
			String xValue = this.xData[i].getValueConvertedToString();
			double yValue = this.yData[i].getValueConvertedToDouble();
			runningTotals.put(xValue, runningTotals.get(xValue) + yValue);
			runningCount.put(xValue, runningCount.get(xValue) + 1);
		}
		
		double[] yDataFormatted = new double[this.yData.length];
		for (String i: runningTotals.keySet()) {
			yDataFormatted[new ArrayList<String>(Arrays.asList(xDataFormatted)).indexOf(i)] = runningTotals.get(i)/runningCount.get(i);
		}
		return yDataFormatted;
	}



	private String[] getXDataFormatted() {
		ArrayList<String> foundXCatagories = new ArrayList<String>();
		for (DataItem xValue: this.xData) {
			if (!foundXCatagories.contains(xValue.getValueConvertedToString()) ) {
				foundXCatagories.add(xValue.getValueConvertedToString());
			}
		}
		String[] xDataFormatted = new String[foundXCatagories.size()];
		xDataFormatted = foundXCatagories.toArray(xDataFormatted);
		return xDataFormatted;
	}
	
	public Axis getAxis() {
		return this.axis;
	}
	
	
	public BarPlot getPlot() {
		return this.plot;
	}
	
	public Legend getLegend() {
		return this.legend;
	}

}
