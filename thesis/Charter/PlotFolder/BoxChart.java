package thesis.Charter.PlotFolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.BarChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class BoxChart extends XYChart{

	BarChartAxis axis;
	BoxPlot plot;
	Legend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues;
	
	public BoxChart(DataFrame dataFrame, String xAxis) {
		super(dataFrame,dataFrame.GetColumnAsArray(xAxis), "Box");
		initialize();
	}
	
	public BoxChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Box");
		initialize();
	}
	
	private void initialize() {
		this.axis = new BarChartAxis();
		this.plot = new BoxPlot();
		this.legend = new Legend();
		
		cm = new BarChartMeasurements();
	}

	@Override
	public void Create() {
		HashMap<Object, Object> data;
		if ((this.yData == null) && (this.colorCodeValues == null)) {
			// Single axis, no hue
			data = calculateSingleBoxPlotData(DataItem.convertToDoubleList(this.xData));
		} else if ((this.yData == null) && (this.colorCodeValues != null)) {
			// Single axis, hue
			System.out.println("Single axis, hue. Error!!!");
		} else if ((this.yData != null) && (this.colorCodeValues == null)) {
			// 2 axis, no hue
			data = calculateXYBoxPlotData(DataItem.convertToStringList(this.xData), DataItem.convertToDoubleList(this.yData));
		} else if ((this.yData != null) && (this.colorCodeValues != null)) {
			// 2 axis, hue
			data = calculateXYHueBoxPlotData(DataItem.convertToStringList(this.xData), DataItem.convertToDoubleList(this.yData),this.colorCodeValues);
		}
		
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
	private HashMap<Object, Object> calculateXYHueBoxPlotData(String[] catagoricalData, double[] values, String[] hueValues) {
		HashMap<String, HashMap<String, ArrayList<Double>>> origFormattedValues = new HashMap<String, HashMap<String, ArrayList<Double>>>();
		String[] uniqueCatagories = getUniqueList(catagoricalData);
		String[] uniqueHues = getUniqueList(hueValues);
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
				double[] doubleList = arrayListToArray(origFormattedValues.get(catagory).get(hue));
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
	private HashMap<Object, Object> calculateXYBoxPlotData(String[] catagoricalData, double[] values) {
		String[] uniqueCatagories = getUniqueList(catagoricalData);
		HashMap<String, ArrayList<Double>> origSortedValues = new HashMap<String, ArrayList<Double>>();
		for (int i = 0; i < catagoricalData.length; i++) {
			if (!origSortedValues.containsKey(catagoricalData[i])) {
				origSortedValues.put(catagoricalData[i],new ArrayList<Double>());
			} 

			origSortedValues.get(catagoricalData[i]).add(values[i]);
		}
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		for (String catagory: origSortedValues.keySet()) {
			double[] doubleList = arrayListToArray(origSortedValues.get(catagory));
			
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
	private HashMap<Object, Object> calculateSingleBoxPlotData(double[] values) {
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		Arrays.sort(values);
		
		int middleIndex = indexOfMedian(0, values.length); 
		 
	    double Q1 = values[indexOfMedian(0, middleIndex)]; 
	    double Q2 = values[middleIndex];
	    double Q3 = values[indexOfMedian(middleIndex + 1, values.length)]; 
	    
	    data.put("Min", values[0]);
	    data.put("Q1", Q1);
	    data.put("Q2", Q2);
	    data.put("Q3", Q3);
	    data.put("Max", values[values.length - 1]);

	    return data;
	}

	private int indexOfMedian(int leftIndex, int rightIndex){ 
		int gapSize = rightIndex - leftIndex + 1; 
		int halfGapSize = (gapSize + 1) / 2 - 1; 
		return leftIndex + halfGapSize; 
	} 
	
	private String[] getUniqueList(String[] origList) {
		Set<String> uniqueList = new HashSet<String>();

		if (origList != null) {
			
			for (String nextElem : origList) {
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
	
	private double[] arrayListToArray(ArrayList<Double> origList) {
		double[] doubleList = new double[origList.size()];
		for (int i = 0; i < doubleList.length; i++) {
			doubleList[i] = origList.get(i);
		}
		return doubleList;
	}
	
	public BarChartAxis getAxis() {
		return axis;
	}
	public void setAxis(BarChartAxis axis) {
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

}
