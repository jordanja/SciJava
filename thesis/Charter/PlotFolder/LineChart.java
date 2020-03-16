package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
	
	private String colorCodeLabel;
	private String[] colorCodeValues; 

	public LineChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Bar");

		this.axis = new NumericAxis();
		this.plot = new LinePlot();
		this.legend = new Legend();

		cm = new LineChartMeasurements();

	}

	@Override
	public void Create() {
		Double[] xValues = DataItem.convertToDoubleList(this.xData);
		Double[] yValues = DataItem.convertToDoubleList(this.yData);
		String[] uniqueColorCodeValues = getUniqueColorCodeValues();
		

		HashMap<Object, Object> data = calculateLineData(xValues, yValues, uniqueColorCodeValues);

		
		Double minX = minimumXValue(data).doubleValue();
		Double maxX = maximumXValue(data).doubleValue();
		Double minY = minimumYValue(data).doubleValue();
		Double maxY = maximumYValue(data).doubleValue();
		
		this.axis.calculateXAxis(minX, maxX);
		this.axis.calculateYAxis(minY, maxY);


		if (this.legend.getIncludeLegend()) {
			Object[] hueValies = uniqueColorCodeValues;
			this.legend.calculateLegend(this.colorCodeLabel, hueValies);
		}

		cm.calculateChartImageMetrics(this.axis, this.plot, this.legend, getTitle(), getTitleFont());

		instantiateChart(cm);

		Graphics2D g = initializaGraphicsObject(cm);
		drawBackground(g, cm);

		this.plot.drawChartBackground(g, cm);
		
		this.axis.drawAxis(g, cm);
		
		this.plot.drawPlotOutline(g, cm);
		
		this.axis.drawAxisTicks(g, cm);
		
		this.plot.drawPlot(g, this.axis, data, cm);
	
		this.axis.drawXAxisLabel(g, cm);
		this.axis.drawYAxisLabel(g, cm);
		
		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, cm, this.plot.getLineColorPalette());
		}
		
		this.drawTitle(g, cm);
	}

	private String[] getUniqueColorCodeValues() {
		Set<String> uniqueList = new HashSet<String>();

		if (this.colorCodeValues != null) {
			
			for (String nextElem : this.colorCodeValues) {
				uniqueList.add(nextElem);
			}
			
			if (uniqueList.size() == 1) {
				return new String[0];
			}
			
			return uniqueList.stream().toArray(String[]::new);
		} else {
			return new String[0];
		}
	}
	
	private Double minValueInList(Double[] arr) {
		Double min = null;
		
		for (Double value : arr) {
			if ((min == null) || (value < min)) {
				min = value;
			}
		}
		return min;
	}
	
	private Double maxValueInList(Double[] arr) {
		Double max = null;
		
		for (Double value : arr) {
			if ((max == null) || (value > max)) {
				max = value;
			}
		}
		return max;
	}
	
	private Double minimumXValue(HashMap<Object, Object> data) {
		Double min = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);

		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeCatagory);
				Double minValueInLine = minValueInList(lineData.keySet().toArray(new Double[0]));
				
				if ((min == null) || (minValueInLine.doubleValue() < min.doubleValue())){
					min = minValueInLine;
				}
			}
		} else {
			min = minValueInList(data.keySet().toArray(new Double[0]));
		}

		return min;

	}

	private Double maximumXValue(HashMap<Object, Object> data) {
		Double max = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeCatagory);
				Double maxValueInLine = maxValueInList(lineData.keySet().toArray(new Double[0]));
				
				if ((max == null) || (maxValueInLine.doubleValue() > max.doubleValue())){
					max = maxValueInLine;
				}
			}
		} else {
			max = maxValueInList(data.keySet().toArray(new Double[0]));
		}

		return max;

	}
	
	private Double minimumYValue(HashMap<Object, Object> data) { 
		Double min = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeCatagory);
				Double minValueInLine = minValueInList(lineData.values().toArray(new Double[0]));
				if ((min == null) || (minValueInLine.doubleValue() < min.doubleValue())) {
					min = minValueInLine;
				}
			}
		} else {
			min = minValueInList(data.values().toArray(new Double[0]));
		}
		
		
		return min;
	}

	private Double maximumYValue(HashMap<Object, Object> data) { 
		Double max = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeCatagory);
				Double maxValueInLine = maxValueInList(lineData.values().toArray(new Double[0]));
				if ((max == null) || (maxValueInLine.doubleValue() > max.doubleValue())) {
					max = maxValueInLine;
				}
			}
		} else {
			max = maxValueInList(data.values().toArray(new Double[0]));
		}
		
		return max;
	}
	
	private HashMap<Object, Object> calculateLineData(Double[] xValues, Double[] yValues, String[] uniqueColorCodeValues) {
		HashMap<Object, Object> data = new HashMap<Object, Object>();

		if (uniqueColorCodeValues.length == 0) {
			Double[] uniqueXValues = removeDuplicates(xValues);
			
			HashMap<Double, Double> runningTotals = new HashMap<Double, Double>();
			HashMap<Double, Integer> runningCount = new HashMap<Double, Integer>();
			
			for (Double xValue : uniqueXValues) {
				runningTotals.put(xValue, Double.valueOf(0));
				runningCount.put(xValue, 0);
			}
			
			for (int i = 0; i < xValues.length; i++) {
				runningTotals.put(xValues[i], runningTotals.get(xValues[i]) + yValues[i]);
				runningCount.put(xValues[i], runningCount.get(xValues[i]) + 1);
			}
			
			for (Double xValue : uniqueXValues) {
				data.put(xValue, runningTotals.get(xValue).doubleValue() / runningCount.get(xValue).doubleValue());
			}
		} else {
			
			HashMap<String, HashMap<Double, Double>> runningTotals = new HashMap<String, HashMap<Double, Double>>(); 
			HashMap<String, HashMap<Double, Integer>> runningCounts = new HashMap<String, HashMap<Double, Integer>>(); 
			
			for (String uniqueColorCodeValue: uniqueColorCodeValues) {
				HashMap<Double, Double> runningTotal = new HashMap<Double, Double>();
				HashMap<Double, Integer> runningCount = new HashMap<Double, Integer>();
				runningTotals.put(uniqueColorCodeValue, runningTotal);
				runningCounts.put(uniqueColorCodeValue, runningCount);
			}
			
			for (int i = 0; i < xValues.length; i++) {
				if (!runningTotals.get(this.colorCodeValues[i]).containsKey(xValues[i])) {
					runningTotals.get(this.colorCodeValues[i]).put(xValues[i], yValues[i]);
					runningCounts.get(this.colorCodeValues[i]).put(xValues[i], 1);
				} else {
					runningTotals.get(this.colorCodeValues[i]).put(xValues[i], yValues[i].doubleValue() + runningTotals.get(this.colorCodeValues[i]).get(xValues[i]).doubleValue());
					runningCounts.get(this.colorCodeValues[i]).put(xValues[i], 1 + runningCounts.get(this.colorCodeValues[i]).get(xValues[i]));
				}
			}
			
			for (int colorCodeCounter = 0; colorCodeCounter < uniqueColorCodeValues.length; colorCodeCounter++) {
				String colorCodeValue = uniqueColorCodeValues[colorCodeCounter];
				HashMap<Double, Double> colorCodeLine = new HashMap<Double, Double>();
				
				for (Double xValue: runningTotals.get(colorCodeValue).keySet()) {
					colorCodeLine.put(xValue, runningTotals.get(colorCodeValue).get(xValue)/runningCounts.get(colorCodeValue).get(xValue));	
				}
				data.put(colorCodeValue, colorCodeLine);
				
			}
			
			
		}
		

		return data;
	}

	private Double[] removeDuplicates(Double[] list) {
		ArrayList<Double> newList = new ArrayList<Double>();
		for (int i = 0; i < list.length; i++) {
			if (!newList.contains(list[i])) {
				newList.add(list[i]);
			}
		}
		return newList.toArray(new Double[0]);

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
	
	public void colorCode(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.GetColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}

}
