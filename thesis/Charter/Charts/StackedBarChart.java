package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.StackedBarChartAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.StackedBarPlot;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.StyleFactory;
import thesis.Charter.Styles.Styles;
import thesis.Common.CommonArray;
import thesis.Common.CommonHashMap;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class StackedBarChart extends XYChart{

	StackedBarChartAxis axis;
	StackedBarPlot plot;
	CategoricalLegend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues = new String[0]; 
	
	private String[] order = new String[0];
	
	public StackedBarChart(DataFrame dataFrame, String xAxis, String yAxis, String colorCode) {
		super(dataFrame, dataFrame.getColumnAsDataItemArray(xAxis), dataFrame.getColumnAsDataItemArray(yAxis));
		
		this.axis = new StackedBarChartAxis();
		this.plot = new StackedBarPlot();
		this.legend = new CategoricalLegend();
		
		this.cm = new XYChartMeasurements();
		
		this.colorCodeLabel = colorCode;
		this.colorCodeValues = this.dataFrame.getColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}

	@Override
	public void create() {
		String[] uniqueHueValues = CommonArray.removeDuplicates(this.colorCodeValues);
		String[] xDataOrdered = getXDataOrdered();
		
		
		HashMap<String, HashMap<String, HashMap<String, Double>>> data = getFormattedData(xDataOrdered, uniqueHueValues);
		this.axis.setXAxis(xDataOrdered);
		this.axis.setYAxis();
		
		
		LegendData legendData = new LegendData();
		legendData.setColorData(CommonArray.removeDuplicates(this.colorCodeValues), this.plot.getColorPalette());
		legendData.setColorLabel(this.colorCodeLabel);
		this.legend.setLegendData(legendData);
		this.legend.calculateLegend();
		
		
		this.cm.calculateChartImageMetrics(this.axis, this.legend, getTitle(), getTitleFont());

		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		this.drawBackground(g, this.cm);

		this.plot.drawPlotBackground(g, this.cm);

		this.axis.drawAxis(g, this.cm);

		this.plot.drawPlotOutline(g, this.cm);

		this.axis.drawAxisTicks(g, this.cm);
		
		this.plot.drawPlot(g, this.axis, data, xDataOrdered, uniqueHueValues, this.axis.getOrientation(), this.cm);

		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);

		
		this.legend.drawLegend(g, this.cm);
		

		this.drawTitle(g, this.cm);
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
