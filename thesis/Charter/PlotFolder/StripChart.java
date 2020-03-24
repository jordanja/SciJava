package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.BoxChartAxis;
import thesis.Charter.Axis.StripChartAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.XYChartMeasurements;
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
		super(dataFrame, dataFrame.getColumnAsArray(xAxis), dataFrame.getColumnAsArray(yAxis), "Strip");
		
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
			this.legend.calculateLegend(this.colorCodeLabel, hueValues);
		}

		this.cm.calculateChartImageMetrics(this.axis, this.legend, getTitle(), getTitleFont());

		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		this.drawBackground(g, this.cm);

		this.plot.drawPlotBackground(g, this.cm);

		this.axis.drawAxis(g, this.cm);
		
		this.plot.drawPlotOutline(g, this.cm);

		this.axis.drawAxisTicks(g, this.cm);
		
		this.plot.drawPlot(g, this.axis, data, xDataOrdered, typeOfData, this.cm);

		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);

		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm, this.plot.getColorPalette());
		}

		this.drawTitle(g, this.cm);
		
	}
	
	
	public String getTypeOfPlot(Object data) {
		
		if (data instanceof Double[]) {
			return "singleCatagory";
		} else {
			
			HashMap<Object, Object> dataMap = (HashMap<Object, Object>)data;
			
			boolean catagoriesNoHue = (dataMap.get(dataMap.keySet().iterator().next()) instanceof HashMap);
			if (catagoriesNoHue) {
				return "multipleCatagoriesAndHueValue";
			} else {
				return "multipleCatagoriesAndNoHueValue";
			}
		}
		
	} 
	
	
	
	private String[] getXDataOrdered(String typeOfData) {
		if (typeOfData != "singleCatagory") {
			
			String[] uniqueXCatagories = CommonArray.removeDuplicates(DataItem.convertToStringList(this.xData));
		
			return CommonArray.orderArrayByOtherArray(uniqueXCatagories, this.order);
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
	 *			"Catagory 1": {
	 *				"Color Code 1": {
	 *					[value1, value2, ..., valueN]
	 *				},
	 *				"Color Code 2": {
	 *					[value1, value2, ..., valueN]
	 *				}
	 *			},
	 *			"Catagory 2": {...},
	 *			...,
	 *		} 
	 */
	private HashMap<Object, Object> calculateXYHueStripPlotData(String[] catagoricalData, Double[] values, String[] hueValues) {
		HashMap<String, HashMap<String, ArrayList<Double>>> origFormattedValues = new HashMap<String, HashMap<String, ArrayList<Double>>>();
		String[] uniqueCatagories = CommonArray.removeDuplicates(catagoricalData);
		String[] uniqueHues = CommonArray.removeDuplicates(hueValues); 
		for (String catagory: uniqueCatagories) {
			origFormattedValues.put(catagory, new HashMap<String, ArrayList<Double>>());
			for (String hue: uniqueHues) {
				origFormattedValues.get(catagory).put(hue, new ArrayList<Double>());
			}
		}
		for (int i = 0; i < catagoricalData.length; i++) {
			origFormattedValues.get(catagoricalData[i]).get(hueValues[i]).add(values[i]);
		}
		
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		for (String catagory: origFormattedValues.keySet()) {
			data.put(catagory, new HashMap<String, HashMap<String, Double>>());
			for (String hue: origFormattedValues.get(catagory).keySet()) {
				Double[] doubleList = CommonArray.arrayListToArray(origFormattedValues.get(catagory).get(hue));
				((HashMap<Object, Object>) data.get(catagory)).put(hue, doubleList);
			}
		}
		return data;
	}
	
	/*
	 * Creates a data object in the following shape:
	 *		{
	 *			"Catagory 1": {
	 *				[value1, value2, ..., valueN]
	 *			},
	 *			"Catagory 2": {...},
	 *			...,
	 *		} 
	 */
	private HashMap<Object, Object> calculateXYStripPlotData(String[] catagoricalData, Double[] values) {
		String[] uniqueCatagories = CommonArray.removeDuplicates(catagoricalData);
		HashMap<String, ArrayList<Double>> origSortedValues = new HashMap<String, ArrayList<Double>>();
		for (int i = 0; i < catagoricalData.length; i++) {
			if (!origSortedValues.containsKey(catagoricalData[i])) {
				origSortedValues.put(catagoricalData[i],new ArrayList<Double>());
			} 

			origSortedValues.get(catagoricalData[i]).add(values[i]);
		}
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		for (String catagory: origSortedValues.keySet()) {
			Double[] doubleList = CommonArray.arrayListToArray(origSortedValues.get(catagory));
			data.put(catagory, doubleList);
		}
		
		return data;
	}
	
	
	public Axis getAxis() {
		return this.axis;
	}

	public StripPlot getPlot() {
		return this.plot;
	}

	public Legend getLegend() {
		return this.legend;
	}
	
	public XYChartMeasurements getChartMeadurements() {
		return this.cm;
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
