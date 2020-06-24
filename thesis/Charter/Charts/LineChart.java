package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.LinePlot;
import thesis.Charter.Styles.ExcelStyle;
import thesis.Charter.Styles.MatplotlibStyle;
import thesis.Charter.Styles.SeabornStyle;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.Style.Styles;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class LineChart extends XYChart {

	NumericAxis axis;
	LinePlot plot;
	CategoricalLegend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues = new String[0]; 
	
	

	public LineChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsDataItemArray(xAxis), dataFrame.getColumnAsDataItemArray(yAxis));

		this.axis = new NumericAxis();
		
		this.axis.setXAxisLabel(xAxis);
		this.axis.setYAxisLabel(yAxis);
		
		this.plot = new LinePlot();
		this.legend = new CategoricalLegend();

		this.cm = new XYChartMeasurements();
		
	}

	@Override
	public void create() {
		Double[] xValues = DataItem.convertToDoubleList(this.xData);
		Double[] yValues = DataItem.convertToDoubleList(this.yData);
		String[] hueValues = CommonArray.removeDuplicates(this.colorCodeValues);
		

		HashMap<Object, Object> data = calculateLineData(xValues, yValues, hueValues);

		
		Double minX = CommonArray.minValue(xValues);
		Double maxX = CommonArray.maxValue(xValues);
		Double minY = minimumYValue(data);
		Double maxY = maximumYValue(data);
//		
		this.axis.calculateXAxis(minX, maxX);
		this.axis.calculateYAxis(minY, maxY);
//		this.axis.calculateXAxis(this.xData);


		if (this.legend.getIncludeLegend()) {
			LegendData legendData = new LegendData();
			legendData.setColorData(CommonArray.removeDuplicates(this.colorCodeValues), this.plot.getColorPalette());
			legendData.setColorLabel(this.colorCodeLabel);
			this.legend.setLegendData(legendData);
			this.legend.calculateLegend();
		}

		this.cm.calculateChartImageMetrics(this.axis, this.legend, getTitle(), getTitleFont());

		instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		drawBackground(g, this.cm);

		this.plot.drawPlotBackground(g, this.cm);
		
		this.axis.drawAxis(g, this.cm);
		
		this.plot.drawPlotOutline(g, this.cm);
		
		this.axis.drawAxisTicks(g, this.cm);
		
		this.plot.drawPlot(g, this.axis, data, this.cm);
	
		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);
		
		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm);
		}
		
		this.drawTitle(g, this.cm);
	}
	
	private Double minimumYValue(HashMap<Object, Object> data) { 
		Double min = Double.MAX_VALUE;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeCatagory);
				Double minValueInLine = CommonArray.minValue(lineData.values().toArray(new Double[0]));
				if (minValueInLine < min) {
					min = minValueInLine;
				}
			}
		} else {
			min = CommonArray.minValue(data.values().toArray(new Double[0]));
		}
		
		
		return min;
	}

	private Double maximumYValue(HashMap<Object, Object> data) { 
		Double max = Double.MIN_VALUE;
		
		boolean hasMultipleLines = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		if (hasMultipleLines) {
			for (String colorCodeCatagory: data.keySet().toArray(new String[0])) {
				HashMap<Double, Double> lineData = (HashMap<Double, Double>) data.get(colorCodeCatagory);
				Double maxValueInLine = CommonArray.maxValue(lineData.values().toArray(new Double[0]));
				if (maxValueInLine > max) {
					max = maxValueInLine;
				}
			}
		} else {
			max = CommonArray.maxValue(data.values().toArray(new Double[0]));
		}
		
		return max;
	}
	
	private HashMap<Object, Object> calculateLineData(Double[] xValues, Double[] yValues, String[] uniqueColorCodeValues) {
		HashMap<Object, Object> data = new HashMap<Object, Object>();

		if (uniqueColorCodeValues.length == 0) {
			Double[] uniqueXValues = CommonArray.removeDuplicates(xValues);
			
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
				data.put(xValue, runningTotals.get(xValue) / runningCount.get(xValue));
			}
		} else {
			
			HashMap<String, HashMap<Double, Double>> runningTotals = new HashMap<String, HashMap<Double, Double>>(); 
			HashMap<String, HashMap<Double, Integer>> runningCounts = new HashMap<String, HashMap<Double, Integer>>(); 
			
			for (String uniqueColorCodeValue: uniqueColorCodeValues) {
				runningTotals.put(uniqueColorCodeValue, new HashMap<Double, Double>());
				runningCounts.put(uniqueColorCodeValue, new HashMap<Double, Integer>());
			}
			
			for (int i = 0; i < xValues.length; i++) {
				if (!runningTotals.get(this.colorCodeValues[i]).containsKey(xValues[i])) {
					runningTotals.get(this.colorCodeValues[i]).put(xValues[i], yValues[i]);
					runningCounts.get(this.colorCodeValues[i]).put(xValues[i], 1);
				} else {
					runningTotals.get(this.colorCodeValues[i]).put(xValues[i], yValues[i] + runningTotals.get(this.colorCodeValues[i]).get(xValues[i]));
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

	public void setStyle(Styles style) {
		Style styleToSet = null;
		if (style == Styles.Matplotlib) {
			styleToSet = new MatplotlibStyle();
		} else if (style == Styles.Seaborn) {
			styleToSet = new SeabornStyle();
		} else if (style == Styles.Excel) {
			styleToSet = new ExcelStyle();
		}
		this.axis.setStyle(styleToSet);
		this.plot.setStyle(styleToSet);
		this.cm.setStyle(styleToSet);
	}

	public BaseAxis getAxis() {
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
		this.colorCodeValues = this.dataFrame.getColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}

	public String getColorCodeLabel() {
		return colorCodeLabel;
	}

	public void setColorCodeLabel(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
	}

	public void setAxis(NumericAxis axis) {
		this.axis = axis;
	}

	public void setPlot(LinePlot plot) {
		this.plot = plot;
	}

	public void setLegend(CategoricalLegend legend) {
		this.legend = legend;
	}

}
