package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.BarPlot;
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
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class BarChart extends XYChart{

	BarChartAxis axis;
	BarPlot plot;
	CategoricalLegend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues = new String[0]; 
	
	private String[] order = new String[0];
	

	public BarChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsDataItemArray(xAxis), dataFrame.getColumnAsDataItemArray(yAxis));
		
		this.axis = new BarChartAxis();
		this.axis.setXAxisLabel(xAxis);
		this.axis.setYAxisLabel(yAxis);
		
		this.plot = new BarPlot();
		this.legend = new CategoricalLegend();
		
		this.cm = new XYChartMeasurements();

	}

	

	@Override
	public void create() {
		
		String[] hueValues = CommonArray.removeDuplicates(this.colorCodeValues);
		String[] xDataOrdered = getXDataOrdered();
		
		HashMap<Object, Object> data = getFormattedData(xDataOrdered, hueValues);
		
		this.axis.setXAxis(xDataOrdered);
		this.axis.setYAxis(data);

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

		this.plot.drawPlot(g, this.axis, data, xDataOrdered, this.axis.getOrientation(), this.cm);

		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);

		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm);
		}

		this.drawTitle(g, this.cm);
		
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
	private HashMap<Object, Object> getFormattedData(String[] xCatagories, String[] uniqueColorCodeValues) {
		
		
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		
		if (uniqueColorCodeValues.length > 0) {
			HashMap<String, HashMap<String, Double>> runningTotals = new HashMap<String, HashMap<String, Double>>();
			HashMap<String, HashMap<String, Integer>> runningCount = new HashMap<String, HashMap<String, Integer>>();
			for (String xCatagory : xCatagories) {
				HashMap<String, Double> colorCodeValueMap = new HashMap<String, Double>();
				HashMap<String, Integer> colorCodeRunningCountMap = new HashMap<String, Integer>();
				for (String colorCodeValue : uniqueColorCodeValues) {
					colorCodeValueMap.put(colorCodeValue, (double) 0);
					colorCodeRunningCountMap.put(colorCodeValue, 0);
				}
				
				runningTotals.put(xCatagory, colorCodeValueMap);
				runningCount.put(xCatagory, colorCodeRunningCountMap);
			}
			
			for (int i = 0; i < this.xData.length; i++) {
				String xValue = this.xData[i].getValueConvertedToString();
				double yValue = this.yData[i].getValueConvertedToDouble();
				String colorCodeValue = this.colorCodeValues[i];
				
				HashMap<String, Double> colorCodeTotalsMap = runningTotals.get(xValue);
				colorCodeTotalsMap.put(colorCodeValue, colorCodeTotalsMap.get(colorCodeValue) + yValue);
				
				HashMap<String, Integer> colorCodeRunningCountMap = runningCount.get(xValue);
				colorCodeRunningCountMap.put(colorCodeValue, colorCodeRunningCountMap.get(colorCodeValue) + 1);
				
				runningTotals.put(xValue, colorCodeTotalsMap);
				runningCount.put(xValue, colorCodeRunningCountMap);
			}
			
			
			for (String xValue : runningTotals.keySet()) {
				HashMap<String, Double> colorCodeMap = new HashMap<String, Double>();
				
				HashMap<String, Double> colorCodeTotalsMap = runningTotals.get(xValue);
				HashMap<String, Integer> colorCodeRunningCountMap = runningCount.get(xValue);
				for (String colorCodeValue: colorCodeTotalsMap.keySet()) {
					double averageValue = colorCodeTotalsMap.get(colorCodeValue)/colorCodeRunningCountMap.get(colorCodeValue);
					colorCodeMap.put(colorCodeValue, averageValue);
				}
				
				
				data.put(xValue, colorCodeMap);
			}
			
		} else {
			
			HashMap<String, Double> runningTotals = new HashMap<String, Double>();
			HashMap<String, Integer> runningCount = new HashMap<String, Integer>();
			
			for (int i = 0; i < this.xData.length; i++) {
				String xValue = this.xData[i].getValueConvertedToString();
				double yValue = this.yData[i].getValueConvertedToDouble();
				if (runningTotals.containsKey(xValue)) {					
					runningTotals.put(xValue, runningTotals.get(xValue) + yValue);
				} else {
					runningTotals.put(xValue, (double) 0);
				}
				
				if (runningCount.containsKey(xValue)) {					
					runningCount.put(xValue, runningCount.get(xValue) + 1);
				} else {
					runningCount.put(xValue, 0);
				}
			}
			
			for (String xValue : runningTotals.keySet()) {
				
				double averageValue = runningTotals.get(xValue)/runningCount.get(xValue);
				
				data.put(xValue, averageValue);
			}
			
		}
		

		return data;
	}

	private String[] getXDataOrdered() {
		String[] uniqueXCatagories = CommonArray.removeDuplicates(DataItem.convertToStringList(this.xData));
		
		return CommonArray.orderArrayByOtherArray(uniqueXCatagories, this.order);
	}


	public BarChartAxis getAxis() {
		return this.axis;
	}

	public BarPlot getPlot() {
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
	
	

	public void setStyle(Styles style) {
		Style styleToSet = StyleFactory.getStyle(style);
		this.axis.setStyle(styleToSet);
		this.plot.setStyle(styleToSet);
		this.cm.setStyle(styleToSet);
		this.legend.setStyle(styleToSet);
		
		this.setTitleFont(styleToSet.getTitleFont());
		this.setTitleColor(styleToSet.getTitleColor());
		this.setImageBackgroundColor(styleToSet.getChartBackgroundColor());
	}
	
}
