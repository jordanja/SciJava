package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.ScatterPlot;
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
import thesis.Common.CommonMath;
import thesis.DataFrame.*;

public class ScatterChart extends XYChart {

	protected NumericAxis axis;
	protected ScatterPlot plot;
	protected CategoricalLegend legend;

	private String colorCodeLabel;
	private String[] colorCodeValues;
	
	private String bubbleSizeLabel;
	private Double[] bubbleSizeValues;

	public ScatterChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsDataItemArray(xAxis), dataFrame.getColumnAsDataItemArray(yAxis));
		
		this.axis = new NumericAxis();
		this.plot = new ScatterPlot();
		this.legend = new CategoricalLegend();

		this.cm = new XYChartMeasurements();

	}

	public void setXAxis(String xAxis) {
		this.xData = this.dataFrame.getColumnAsDataItemArray(xAxis);
	}

	public void setYAxis(String yAxis) {
		this.yData = this.dataFrame.getColumnAsDataItemArray(yAxis);
	}

	public void setIncludeLegend(boolean includeLegend) {
		this.legend.setIncludeLegend(includeLegend);
	}

	// If using native Java data structures
	public void setXAxis(Number[] xData) {
		this.xData = DataItem.convertToDataItemList(xData);
	}

	public void setXAxis(List<Number> xData) {
		this.xData = DataItem.convertToDataItemList(xData.toArray(new Number[xData.size()]));
	}

	public void setYAxis(Number[] yData) {
		this.yData = DataItem.convertToDataItemList(yData);
	}

	public void setYAxis(List<Number> yData) {
		this.xData = DataItem.convertToDataItemList(yData.toArray(new Number[yData.size()]));
	}

	public void colorCode(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.getColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
	}

	public void colorCode(String[] colorCodeData) {
		this.colorCodeValues = colorCodeData;
		this.legend.setIncludeLegend(true);

	}

	public void colorCode(List<String> colorCodeData) {
		this.colorCodeValues = colorCodeData.toArray(new String[0]);
		this.legend.setIncludeLegend(true);
	}
	
	public void setBubbleSize(String bubbleSizeLabel) {
		this.bubbleSizeLabel = bubbleSizeLabel;
		this.bubbleSizeValues = CommonArray.primitiveDoubletoObjectDouble(this.dataFrame.getColumnAsDoubleArray(bubbleSizeLabel));
		System.out.println(this.bubbleSizeValues[0]);
				//DataItem.convertToDoubleList(dataFrame.getColumnAsDataItemArray(this.bubbleSizeLabel));
		this.legend.setIncludeLegend(true);
	}

	public void create() {
		double minX = CommonMath.minimumValue(this.xData);
		double maxX = CommonMath.maximumValue(this.xData);
		double minY = CommonMath.minimumValue(this.yData);
		double maxY = CommonMath.maximumValue(this.yData);
		
		this.axis.calculateXAxis(minX, maxX);
		this.axis.calculateYAxis(minY, maxY);

		Map<String, Object>[] data = calculteData();

		if (this.legend.getIncludeLegend()) {
			LegendData legendData = new LegendData();
			if (this.colorCodeLabel != null) {				
				legendData.setColorData(CommonArray.removeDuplicates(this.colorCodeValues), this.plot.getColorPalette());
				legendData.setColorLabel(this.colorCodeLabel);
			}
			if (this.bubbleSizeLabel != null) {
				legendData.setSizeDataMaxMin(this.bubbleSizeValues, this.plot.getSmallestRadius(), this.plot.getLargestRadius());
				legendData.setSizeLabel(this.bubbleSizeLabel);
			}
			this.legend.setLegendData(legendData);
			this.legend.calculateLegend();
		}

		this.cm.calculateChartImageMetrics(this.axis, this.legend, getTitle(), getTitleFont());

		this.instantiateChart(this.cm);
		Graphics2D g = initializaGraphicsObject(this.cm);

		this.drawBackground(g, this.cm);

		this.plot.drawPlotBackground(g, this.cm);
		this.axis.drawAxis(g, this.cm);

		this.plot.drawLinearRegression(g, this.axis, this.xData, this.yData, this.cm);

		this.plot.drawPlotOutline(g, this.cm);

		this.axis.drawAxisTicks(g, this.cm);

		
		this.plot.drawPlot(g, this.axis, data, this.cm);

		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);

		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm);
		}

		this.drawTitle(g, this.cm);

//		this.drawXDebugLines(g, cm);
//		this.drawYDebugLines(g, cm);

		g.dispose();
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
	
	private Map<String, Object>[] calculteData() {
		Double[] xValues = DataItem.convertToDoubleList(this.xData);
		Double[] yValues = DataItem.convertToDoubleList(this.yData);
		
		@SuppressWarnings("unchecked")
		Map<String, Object>[] data = (Map<String, Object>[]) new Map[xValues.length];
		for (int rowCount = 0; rowCount < xValues.length; rowCount++) {
			data[rowCount] = new HashMap<String, Object>();
			data[rowCount].put("x", xValues[rowCount]);
			data[rowCount].put("y", yValues[rowCount]);
			
			if (this.colorCodeLabel != null) {				
				data[rowCount].put("color", this.colorCodeValues[rowCount]);
			}
			if (this.bubbleSizeLabel != null) {
				data[rowCount].put("size", this.bubbleSizeValues[rowCount]);
			}
			
		}
		return data;		
	}

	public NumericAxis getAxis() {
		return this.axis;
	}

	public ScatterPlot getPlot() {
		return this.plot;
	}

	public Legend getLegend() {
		return this.legend;
	}

}
