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
		Number[] xValues = convertDataItemListIntoNumberList(this.xData);
		Number[] yValues = convertDataItemListIntoNumberList(this.yData);
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
	
	private Number minimumXValue(HashMap<Object, Object> data) {
		Number min = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);

		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Number, Number> lineData = (HashMap<Number, Number>) data.get(colorCodeCatagory);
				for (Number xValue : lineData.keySet()) {
					if ((min == null) || (xValue.doubleValue() < min.doubleValue())) {
						min = xValue;
					}
				}
			}
		} else {
			for (Number xValue : data.keySet().toArray(new Number[0])) {
				if ((min == null) || (xValue.doubleValue() < min.doubleValue())) {
					min = xValue;
				}
			}
		}

		return min;

	}

	private Number maximumXValue(HashMap<Object, Object> data) {
		Number max = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Number, Number> lineData = (HashMap<Number, Number>) data.get(colorCodeCatagory);
				for (Number xValue : lineData.keySet()) {
					if ((max == null) || (xValue.doubleValue() > max.doubleValue())) {
						max = xValue;
					}
				}
			}
		} else {
			
			for (Number xValue : data.keySet().toArray(new Number[0])) {
				if ((max == null) || (xValue.doubleValue() > max.doubleValue())) {
					max = xValue;
				}
			}
		}

		return max;

	}
	
	private Number minimumYValue(HashMap<Object, Object> data) { 
		Number min = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Number, Number> lineData = (HashMap<Number, Number>) data.get(colorCodeCatagory);
				for (Number xValue: lineData.keySet()) {
					if ((min == null) || (lineData.get(xValue).doubleValue() < min.doubleValue())) {
						min = lineData.get(xValue);
					}
				}
			}
		} else {			
			for (Number xValue: data.keySet().toArray(new Number[0])) {
				if ((min == null) || (((Number)data.get(xValue)).doubleValue() < min.doubleValue())) {
					min = (Number)data.get(xValue);
				}
			}
		}
		
		
		return min;
	}

	private Number maximumYValue(HashMap<Object, Object> data) { 
		Number max = null;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Number, Number> lineData = (HashMap<Number, Number>) data.get(colorCodeCatagory);
				for (Number xValue: lineData.keySet()) {
					if ((max == null) || (lineData.get(xValue).doubleValue() > max.doubleValue())) {
						max = lineData.get(xValue);
					}
				}
			}
		} else {
			
			for (Number xValue: data.keySet().toArray(new Number[0])) {
				if ((max == null) || (((Number)data.get(xValue)).doubleValue() > max.doubleValue())) {
					max = (Number)data.get(xValue);
				}
			}
		}
		
		return max;
	}
	
	private HashMap<Object, Object> calculateLineData(Number[] xValues, Number[] yValues, String[] uniqueColorCodeValues) {
		HashMap<Object, Object> data = new HashMap<Object, Object>();

		if (uniqueColorCodeValues.length == 0) {
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
			
			for (Number xValue : uniqueXValues) {
				data.put(xValue, runningTotals.get(xValue).doubleValue() / runningCount.get(xValue).doubleValue());
			}
		} else {
			
			HashMap<String, HashMap<Number, Number>> runningTotals = new HashMap<String, HashMap<Number, Number>>(); 
			HashMap<String, HashMap<Number, Number>> runningCounts = new HashMap<String, HashMap<Number, Number>>(); 
			
			for (String uniqueColorCodeValue: uniqueColorCodeValues) {
				HashMap<Number, Number> runningTotal = new HashMap<Number, Number>();
				HashMap<Number, Number> runningCount = new HashMap<Number, Number>();
				runningTotals.put(uniqueColorCodeValue, runningTotal);
				runningCounts.put(uniqueColorCodeValue, runningCount);
			}
			
			for (int i = 0; i < xValues.length; i++) {
				if (!runningTotals.get(this.colorCodeValues[i]).containsKey(xValues[i])) {
					runningTotals.get(this.colorCodeValues[i]).put(xValues[i], yValues[i]);
					runningCounts.get(this.colorCodeValues[i]).put(xValues[i], 1);
				} else {
					runningTotals.get(this.colorCodeValues[i]).put(xValues[i], yValues[i].doubleValue() + runningTotals.get(this.colorCodeValues[i]).get(xValues[i]).doubleValue());
					runningCounts.get(this.colorCodeValues[i]).put(xValues[i], 1 + runningCounts.get(this.colorCodeValues[i]).get(xValues[i]).intValue());
				}
			}
			
			for (int colorCodeCounter = 0; colorCodeCounter < uniqueColorCodeValues.length; colorCodeCounter++) {
				String colorCodeValue = uniqueColorCodeValues[colorCodeCounter];
				HashMap<Number, Number> colorCodeLine = new HashMap<Number, Number>();
				
				for (Number xValue: runningTotals.get(colorCodeValue).keySet()) {
					colorCodeLine.put(xValue, runningTotals.get(colorCodeValue).get(xValue).doubleValue()/runningCounts.get(colorCodeValue).get(xValue).doubleValue());	
				}
				data.put(colorCodeValue, colorCodeLine);
				
			}
			
			
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
	
	public void colorCode(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.GetColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}

}
