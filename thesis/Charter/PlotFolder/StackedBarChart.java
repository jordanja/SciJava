package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.StackedBarChartAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonHashMap;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class StackedBarChart extends XYChart{

	StackedBarChartAxis axis;
	StackedBarPlot plot;
	Legend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues = new String[0]; 
	
	private String[] order = new String[0];
	
	public StackedBarChart(DataFrame dataFrame, String xAxis, String yAxis, String colorCode) {
		super(dataFrame, dataFrame.getColumnAsArray(xAxis), dataFrame.getColumnAsArray(yAxis), "StackedBar");
		
		this.axis = new StackedBarChartAxis();
		this.plot = new StackedBarPlot();
		this.legend = new Legend();
		
		cm = new XYChartMeasurements();
		
		this.colorCodeLabel = colorCode;
		this.colorCodeValues = this.dataFrame.getColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}

	@Override
	public void Create() {
		String[] uniqueHueValues = CommonArray.removeDuplicates(this.colorCodeValues);
		String[] xDataOrdered = getXDataOrdered();
		
		
		HashMap<String, HashMap<String, HashMap<String, Double>>> data = getFormattedData(xDataOrdered, uniqueHueValues);
		this.axis.setXAxis(xDataOrdered);
		this.axis.setYAxis();
		
		
		this.legend.calculateLegend(this.colorCodeLabel, uniqueHueValues);
		
		
		cm.calculateChartImageMetrics(this.axis, this.legend, getTitle(), getTitleFont());

		instantiateChart(cm);

		Graphics2D g = initializaGraphicsObject(cm);
		drawBackground(g, cm);

		this.plot.drawPlotBackground(g, cm);

		this.axis.drawAxis(g, cm);

		this.plot.drawPlotOutline(g, cm);

		this.axis.drawAxisTicks(g, cm);
		
		this.plot.drawPlot(g, this.axis, data, xDataOrdered, uniqueHueValues, cm);

		this.axis.drawXAxisLabel(g, cm);
		this.axis.drawYAxisLabel(g, cm);

		
		this.legend.drawLegend(g, cm, this.plot.getColorPalette());
		

		this.drawTitle(g, cm);
	}
	
	/*
	 * data is structured as follows:
	 * 		{
	 * 			xCategory1: {
	 * 				hueValue1: {
	 * 					value: 10,
	 * 					proportion: 0.30,
	 * 				},
	 *				hueValue2: {
	 * 					value: 33,
	 * 					proportion: 0.70,
	 * 				}
	 * 			},
	 * 			xCategory2: {...}
	 * 		}
	 * 
	 */
	private HashMap<String, HashMap<String, HashMap<String, Double>>> getFormattedData(String[] xDataOrdered, String[] uniqueHueValues) {
		
		HashMap<String, Double> totals = new HashMap<String, Double>(); 
		
		for (String xCategory: xDataOrdered) {
			totals.put(xCategory, (double)0);
		}
		for (int i = 0; i < this.xData.length; i++) {
			String xCategory = this.xData[i].getValueConvertedToString();
			totals.put(xCategory, totals.get(xCategory) + this.yData[i].getValueConvertedToDouble());
		}
		
		HashMap<String, HashMap<String, HashMap<String, Double>>> data = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
		
		for (int index = 0; index < this.xData.length; index++) {
			String xCategory = this.xData[index].getValueConvertedToString();
			if (!data.containsKey(xCategory)) {
				data.put(xCategory, new HashMap<String, HashMap<String, Double>>());
				for (String hueValue: uniqueHueValues) {
					data.get(xCategory).put(hueValue, new HashMap<String, Double>());
				}
			}
			data.get(xCategory).get(this.colorCodeValues[index]).put("value", this.yData[index].getValueConvertedToDouble());
			data.get(xCategory).get(this.colorCodeValues[index]).put("proportion", (double)0);
			data.get(xCategory).get(this.colorCodeValues[index]).put("proportion", (data.get(xCategory).get(this.colorCodeValues[index]).get("proportion") + this.yData[index].getValueConvertedToDouble())/totals.get(xCategory));
		}
		
		return data;
	}

	private String[] getXDataOrdered() {
		String[] uniqueXCatagories = CommonArray.removeDuplicates(DataItem.convertToStringList(this.xData));
		
		return CommonArray.orderArrayByOtherArray(uniqueXCatagories, this.order);
	}
	
	public StackedBarChartAxis getAxis() {
		return axis;
	}

	public StackedBarPlot getPlot() {
		return plot;
	}

	public Legend getLegend() {
		return legend;
	}


	public String getColorCodeLabel() {
		return colorCodeLabel;
	}

	public void colorCode(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.getColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}

	public String[] getColorCodeValues() {
		return colorCodeValues;
	}

	public void setColorCodeValues(String[] colorCodeValues) {
		this.colorCodeValues = colorCodeValues;
	}

	public String[] getOrder() {
		return order;
	}

	public void setOrder(String[] order) {
		this.order = order;
	}
	
	
}
