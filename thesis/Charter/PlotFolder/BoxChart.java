package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.BoxChartAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.BarChartMeasurements;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;
import thesis.Helpers.CommonArray;
import thesis.Helpers.CommonMath;

public class BoxChart extends XYChart{

	BoxChartAxis axis;
	BoxPlot plot;
	Legend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues = new String[0];
	
	private String[] order = new String[0];
	
	public BoxChart(DataFrame dataFrame, String yAxis) {
		super(dataFrame,dataFrame.GetColumnAsArray(yAxis), "Box");
		initialize();
	}
	
	public BoxChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Box");
		initialize();
	}
	
	private void initialize() {
		this.axis = new BoxChartAxis();
		this.plot = new BoxPlot();
		this.legend = new Legend();
		
		cm = new BarChartMeasurements();
	}

	@Override
	public void Create() {
		HashMap<Object, Object> data = calculateData();
		String typeOfData = getTypeOfPlot(data);
		String[] xDataOrdered = getXDataOrdered(typeOfData);
		Object[] hueValues = CommonArray.removeDuplicates(this.colorCodeValues);
		
		
		this.axis.setXAxis(xDataOrdered);
		this.axis.setYAxis(data, typeOfData);
		
		if (this.legend.getIncludeLegend()) {
			this.legend.calculateLegend(this.colorCodeLabel, hueValues);
		}

		cm.calculateChartImageMetrics(this.axis, this.plot, this.legend, getTitle(), getTitleFont());

		instantiateChart(cm);

		Graphics2D g = initializaGraphicsObject(cm);
		drawBackground(g, cm);

		this.plot.drawChartBackground(g, cm);

		this.axis.drawAxis(g, cm);
		
		this.plot.drawPlotOutline(g, cm);

		this.axis.drawAxisTicks(g, cm);
		
		this.plot.drawPlot(g, this.axis, data, xDataOrdered, typeOfData, cm);

		this.axis.drawXAxisLabel(g, cm);
		this.axis.drawYAxisLabel(g, cm);

		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, cm, this.plot.getBoxColorPalette());
		}

		this.drawTitle(g, cm);
	}

	private String[] getXDataOrdered(String typeOfData) {
		if (typeOfData != "singleCatagory") {
			
			String[] uniqueXCatagories = CommonArray.removeDuplicates(DataItem.convertToStringList(this.xData));
		
			return CommonArray.orderArrayByOtherArray(uniqueXCatagories, this.order);
		} else {
			return new String[0];
		}
	}

	private HashMap<Object, Object> calculateData() {
		HashMap<Object, Object> data = null;
		if ((this.xData == null) && (this.colorCodeValues.length == 0)) {
			
			// Single axis, no hue
			data = calculateSingleBoxPlotData(DataItem.convertToDoubleList(this.yData));
		} else if ((this.yData == null) && (this.colorCodeValues.length != 0)) {
			// Single axis, hue
			System.out.println("Single axis, hue. Error!!!");
		} else if ((this.yData != null) && (this.colorCodeValues.length == 0)) {
			// 2 axis, no hue
			data = calculateXYBoxPlotData(DataItem.convertToStringList(this.xData), DataItem.convertToDoubleList(this.yData));
		} else if ((this.yData != null) && (this.colorCodeValues.length != 0)) {
			// 2 axis, hue
			data = calculateXYHueBoxPlotData(DataItem.convertToStringList(this.xData), DataItem.convertToDoubleList(this.yData),this.colorCodeValues);
		}
		return data;
	}
	
	/*
	 * Creates a data object in the following shape:
	 *		{
	 *			"Catagory 1": {
	 *				"Color Code 1": {
	 *					"Min": 1,
	 *					...,
	 *					"Max": 100
	 *				},
	 *				"Color Code 2": {
	 *					"Min": 1,
	 *					...,
	 *					"Max": 100
	 *				}
	 *			},
	 *			"Catagory 2": {...},
	 *			...,
	 *		} 
	 */
	private HashMap<Object, Object> calculateXYHueBoxPlotData(String[] catagoricalData, Double[] values, String[] hueValues) {
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
				((HashMap<Object, Object>) data.get(catagory)).put(hue, calculateSingleBoxPlotData(doubleList));
			}
		}
		return data;
	}

	/*
	 * Creates a data object in the following shape:
	 *		{
	 *			"Catagory 1": {
	 *				"Min": 1,
	 *				...,
	 *				"Max": 100
	 *			},
	 *			"Catagory 2": {...},
	 *			...,
	 *		} 
	 */
	private HashMap<Object, Object> calculateXYBoxPlotData(String[] catagoricalData, Double[] values) {
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
			
			HashMap<Object, Object> singlePlotData = calculateSingleBoxPlotData(doubleList);
			data.put(catagory, singlePlotData);
		}
		
		return data;
	}

	/*
	 * Creates a data object in the following shape:
	 * 		{
	 * 			"Min": 1,
	 * 			...,
	 * 			"Max": 100,
	 * 		}
	 */
	private HashMap<Object, Object> calculateSingleBoxPlotData(Double[] values) {
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		Arrays.sort(values);
		
		int middleIndex = CommonMath.indexOfMedian(0, values.length); 
		 
	    double Q1 = values[CommonMath.indexOfMedian(0, middleIndex)]; 
	    double Q2 = values[middleIndex];
	    double Q3 = values[CommonMath.indexOfMedian(middleIndex + 1, values.length)]; 
	    
	    data.put("Min", values[0]);
	    data.put("Q1", Q1);
	    data.put("Q2", Q2);
	    data.put("Q3", Q3);
	    data.put("Max", values[values.length - 1]);
	    return data;
	}
	

	public String getTypeOfPlot(HashMap<Object, Object> data) {
		boolean singleValue = (data.get(data.keySet().iterator().next()) instanceof Double);
		
		if (singleValue) {
			return "singleCatagory";
		} else {
			HashMap<Object, Object> catagory = (HashMap<Object, Object>) (data.get(data.keySet().iterator().next()));
			
			boolean noHueValue = (catagory.get(catagory.keySet().iterator().next()) instanceof Double);
			if (noHueValue) {
				return "multipleCatagoriesAndNoHueValue";
			} else {
				return "multipleCatagoriesAndHueValue";
			}
		}
		
	} 

	
	public BoxChartAxis getAxis() {
		return axis;
	}
	public void setAxis(BoxChartAxis axis) {
		this.axis = axis;
	}

	public BoxPlot getPlot() {
		return plot;
	}
	public void setPlot(BoxPlot plot) {
		this.plot = plot;
	}

	public Legend getLegend() {
		return legend;
	}
	public void setLegend(Legend legend) {
		this.legend = legend;
	}
	
	public void colorCode(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.GetColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}
	
	public XYChartMeasurements getChartMeadurements() {
		return this.cm;
	}
	
	public String[] getOrder() {
		return order;
	}

	public void setOrder(String[] order) {
		this.order = order;
	}

}
