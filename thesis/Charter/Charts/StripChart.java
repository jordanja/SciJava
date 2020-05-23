package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.BoxChartAxis;
import thesis.Charter.Axis.StripChartAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.StripPlot;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class StripChart extends XYChart{

	StripChartAxis axis;
	StripPlot plot;
	Legend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues = new String[0]; 
	
	private String[] order = new String[0];
	
	public StripChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsArray(xAxis), dataFrame.getColumnAsArray(yAxis));
		
		initialize();

	}
	
	public StripChart(DataFrame dataFrame, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsArray(yAxis), "Strip");
		initialize();
	}
	
	private void initialize() {
		this.axis = new StripChartAxis();
		this.plot = new StripPlot();
		this.legend = new Legend();
		
		this.cm = new XYChartMeasurements();
	}

	@Override
	public void Create() {
		Object data = calculateData();
		String typeOfData = getTypeOfPlot(data);
		
		String[] xDataOrdered = getXDataOrdered(typeOfData);
		Object[] hueValues = CommonArray.removeDuplicates(this.colorCodeValues);
		
		
		this.axis.setXAxis(xDataOrdered);
		this.axis.setYAxis(data, typeOfData);
		
		
		if (this.legend.getIncludeLegend()) {
			LegendData legendData = new LegendData();
			legendData.setColorData(CommonArray.removeDuplicates(this.colorCodeValues), this.plot.getColorPalette());
			legendData.setColorLabel(this.colorCodeLabel);
			this.legend.setLegendData(legendData);
			this.legend.calculateLegend();
		}

		this.cm.calculateChartImageMetrics(this.axis, this.legend, getTitle(), getTitleFont());

		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		this.drawBackground(g, this.cm);

		this.plot.drawPlotBackground(g, this.cm);

		this.axis.drawAxis(g, this.cm);
		
		this.plot.drawPlotOutline(g, this.cm);

		this.axis.drawAxisTicks(g, this.cm);
		
		this.plot.drawPlot(g, this.axis, data, xDataOrdered, typeOfData, this.axis.getOrientation(), this.cm);

		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);

		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm);
		}

		this.drawTitle(g, this.cm);
		
	}
	
	
	public String getTypeOfPlot(Object data) {
		
		if (data instanceof Double[]) {
			return "singleCategory";
		} else {
			
			HashMap<Object, Object> dataMap = (HashMap<Object, Object>)data;
			
			boolean categoriesNoHue = (dataMap.get(dataMap.keySet().iterator().next()) instanceof HashMap);
			if (categoriesNoHue) {
				return "multipleCategoriesAndHueValue";
			} else {
				return "multipleCategoriesAndNoHueValue";
			}
		}
		
	} 
	
	
	
	private String[] getXDataOrdered(String typeOfData) {
		if (typeOfData != "singleCategory") {
			
			String[] uniqueXCategories = CommonArray.removeDuplicates(DataItem.convertToStringList(this.xData));
		
			return CommonArray.orderArrayByOtherArray(uniqueXCategories, this.order);
		} else {
			return new String[0];
		}
	}
	/*
	 * This method returns the data structure that will be used for creating a bar chart.
	 * If no color code attribute is specified, then the returned object will be structured:
	 * 		{
	 * 			"X Category 1": Y Value 1,
	 * 			"X Category 2": Y Value 2,
	 * 			... 
	 * 		} 
	 * 
	 * If a color code attribute is specified, then the returned object will be structured:
	 * 		{
	 * 			"X Category 1": {
	 * 								"Color code attribute 1": color code value 1,
	 * 								"Color code attribute 2": color code value 2,
	 * 								...
	 * 							}
	 * 			"X Category 2": {
	 * 								"Color code attribute 1": color code value 1,
	 * 								...
	 * 							}
	 * 		},
	 */
	private Object calculateData() {
		Object data = null;
		if ((this.xData == null) && (this.colorCodeValues.length == 0)) {
			
			// Single axis, no hue
			data = DataItem.convertToDoubleList(this.yData);
		} else if ((this.yData == null) && (this.colorCodeValues.length != 0)) {
			// Single axis, hue
			System.out.println("Single axis, hue. Error!!!");
		} else if ((this.yData != null) && (this.colorCodeValues.length == 0)) {
			// 2 axis, no hue
			data = calculateXYStripPlotData(DataItem.convertToStringList(this.xData), DataItem.convertToDoubleList(this.yData));
		} else if ((this.yData != null) && (this.colorCodeValues.length != 0)) {
			// 2 axis, hue
			data = calculateXYHueStripPlotData(DataItem.convertToStringList(this.xData), DataItem.convertToDoubleList(this.yData),this.colorCodeValues);
		}
		return data;
		
		
	}
	
	/*
	 * Creates a data object in the following shape:
	 *		{
	 *			"Category 1": {
	 *				"Color Code 1": {
	 *					[value1, value2, ..., valueN]
	 *				},
	 *				"Color Code 2": {
	 *					[value1, value2, ..., valueN]
	 *				}
	 *			},
	 *			"Category 2": {...},
	 *			...,
	 *		} 
	 */
	private HashMap<Object, Object> calculateXYHueStripPlotData(String[] categoricalData, Double[] values, String[] hueValues) {
		HashMap<String, HashMap<String, ArrayList<Double>>> origFormattedValues = new HashMap<String, HashMap<String, ArrayList<Double>>>();
		String[] uniqueCategories = CommonArray.removeDuplicates(categoricalData);
		String[] uniqueHues = CommonArray.removeDuplicates(hueValues); 
		for (String category: uniqueCategories) {
			origFormattedValues.put(category, new HashMap<String, ArrayList<Double>>());
			for (String hue: uniqueHues) {
				origFormattedValues.get(category).put(hue, new ArrayList<Double>());
			}
		}
		for (int i = 0; i < categoricalData.length; i++) {
			origFormattedValues.get(categoricalData[i]).get(hueValues[i]).add(values[i]);
		}
		
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		for (String category: origFormattedValues.keySet()) {
			data.put(category, new HashMap<String, HashMap<String, Double>>());
			for (String hue: origFormattedValues.get(category).keySet()) {
				Double[] doubleList = origFormattedValues.get(category).get(hue).toArray(new Double[0]);
				((HashMap<Object, Object>) data.get(category)).put(hue, doubleList);
			}
		}
		return data;
	}
	
	/*
	 * Creates a data object in the following shape:
	 *		{
	 *			"Category 1": {
	 *				[value1, value2, ..., valueN]
	 *			},
	 *			"Category 2": {...},
	 *			...,
	 *		} 
	 */
	private HashMap<Object, Object> calculateXYStripPlotData(String[] categoricalData, Double[] values) {
		HashMap<String, ArrayList<Double>> origSortedValues = new HashMap<String, ArrayList<Double>>();
		for (int i = 0; i < categoricalData.length; i++) {
			if (!origSortedValues.containsKey(categoricalData[i])) {
				origSortedValues.put(categoricalData[i],new ArrayList<Double>());
			} 

			origSortedValues.get(categoricalData[i]).add(values[i]);
		}
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		for (String category: origSortedValues.keySet()) {
			Double[] doubleList = origSortedValues.get(category).toArray(new Double[0]);
			data.put(category, doubleList);
		}
		
		return data;
	}
	
	
	public StripChartAxis getAxis() {
		return this.axis;
	}

	public StripPlot getPlot() {
		return this.plot;
	}

	public Legend getLegend() {
		return this.legend;
	}

	public void colorCode(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.getColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}
	
	public String[] getOrder() {
		return order;
	}

	public void setOrder(String[] order) {
		this.order = order;
	}
	
}
