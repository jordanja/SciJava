package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.LineChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class LineChart extends XYChart {

	NumericAxis axis;
	LinePlot plot;
	Legend legend;

	public LineChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Bar");

		this.axis = new NumericAxis();
		this.plot = new LinePlot();
		this.legend = new Legend();

		cm = new LineChartMeasurements();

	}

	@Override
	public void Create() {
		Number[] xValues = convertDataItemListIntoNumberList(this.xData);
		Number[] yValues = convertDataItemListIntoNumberList(this.yData);

		HashMap<Number, Number> data = calculateLineData(xValues, yValues);

		Double minX = minimumXValue(data).doubleValue();
		Double maxX = maximumXValue(data).doubleValue();
		Double minY = minimumYValue(data).doubleValue();
		Double maxY = maximumYValue(data).doubleValue();
		
		this.axis.calculateXAxis(minX, maxX);
		this.axis.calculateYAxis(minY, maxY);

		
//		if (this.legend.getIncludeLegend()) {
//			Object[] hueValies = uniqueColorCodeValues;
//			this.legend.calculateLegend(this.colorCodeLabel, hueValies);
//		}

		cm.calculateChartImageMetrics(this.axis, this.plot, this.legend, getTitle(), getTitleFont());

		instantiateChart(cm);

		Graphics2D g = initializaGraphicsObject(cm);
		drawBackground(g, cm);

		this.plot.drawChartBackground(g, cm);
		
		this.axis.drawAxis(g, cm);
		
		this.plot.drawPlotOutline(g, cm);
		
		this.axis.drawAxisTicks(g, cm);
		
		this.plot.drawPlot(g, this.axis, data, cm);
	}

	private Number minimumXValue(HashMap<Number, Number> data) {
		Number min = null;

		for (Number xValue : data.keySet()) {
			if ((min == null) || (xValue.doubleValue() < min.doubleValue())) {
				min = xValue;
			}
		}

		return min;

	}

	private Number maximumXValue(HashMap<Number, Number> data) {
		Number max = null;

		for (Number xValue : data.keySet()) {
			if ((max == null) || (xValue.doubleValue() > max.doubleValue())) {
				max = xValue;
			}
		}

		return max;

	}
	
	private Number minimumYValue(HashMap<Number, Number> data) { 
		Number min = null;
		for (Number xValue: data.keySet()) {
			if ((min == null) || (data.get(xValue).doubleValue() < min.doubleValue())) {
				min = data.get(xValue);
			}
		}
		
		return min;
	}

	private Number maximumYValue(HashMap<Number, Number> data) { 
		Number max = null;
		for (Number xValue: data.keySet()) {
			if ((max == null) || (data.get(xValue).doubleValue() > max.doubleValue())) {
				max = data.get(xValue);
			}
		}
		
		return max;
	}
	
	private HashMap<Number, Number> calculateLineData(Number[] xValues, Number[] yValues) {
		Number[] uniqueXValues = removeDuplicates(xValues);
		HashMap<Number, Number> runningTotals = new HashMap<Number, Number>();
		HashMap<Number, Number> runningCount = new HashMap<Number, Number>();

		for (Number xValue : uniqueXValues) {
			runningTotals.put(xValue, 0);
			runningCount.put(xValue, 0);
		}

		for (int i = 0; i < xValues.length; i++) {
			runningTotals.put(xValues[i], runningTotals.get(xValues[i]).doubleValue() + yValues[i].doubleValue());
			runningCount.put(xValues[i], runningCount.get(xValues[i]).intValue() + 1);
		}

		HashMap<Number, Number> data = new HashMap<Number, Number>();
		for (Number xValue : uniqueXValues) {
			data.put(xValue, runningTotals.get(xValue).doubleValue() / runningCount.get(xValue).doubleValue());
		}

		return data;
	}

	private Number[] convertDataItemListIntoNumberList(DataItem[] list) {
		Number[] numberList = new Number[list.length];
		for (int i = 0; i < list.length; i++) {
			numberList[i] = list[i].getValueConvertedToNumber();
		}

		return numberList;
	}

	private String[] convertDataItemListToStringList(DataItem[] list) {
		String[] stringList = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			stringList[i] = list[i].getValueConvertedToString();
		}

		return stringList;
	}

	private Number[] removeDuplicates(Number[] list) {
		ArrayList<Number> newList = new ArrayList<Number>();
		for (int i = 0; i < list.length; i++) {
			if (!newList.contains(list[i])) {
				newList.add(list[i]);
			}
		}
		return newList.toArray(new Number[0]);

	}

	public Axis getAxis() {
		return this.axis;
	}

	public LinePlot getPlot() {
		return this.plot;
	}

	public Legend getLegend() {
		return this.legend;
	}

}
