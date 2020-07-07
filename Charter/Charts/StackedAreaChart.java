package thesis.Charter.Charts;

import java.awt.Graphics2D;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.StackedAreaPlot;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.StyleFactory;
import thesis.Charter.Styles.Styles;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;
import thesis.DataFrame.GroupBy;

public class StackedAreaChart extends XYChart {

	NumericAxis axis;
	StackedAreaPlot plot;
	CategoricalLegend legend;
	
	private String colorCodeLabel;
	private String[] colorCodeValues = new String[0]; 
	
	private String xColumnName = "";
	private String yColumnName = "";
	private String colorCodeColumnName = "";
	
	public StackedAreaChart(DataFrame dataFrame, String xAxis, String yAxis, String colorCodeLabel) {
		super(dataFrame, dataFrame.getColumnAsDataItemArray(xAxis), dataFrame.getColumnAsDataItemArray(yAxis));

		this.axis = new NumericAxis();
		
		this.axis.setXAxisLabel(xAxis);
		this.axis.setYAxisLabel(yAxis);
		
		this.xColumnName = xAxis;
		this.yColumnName = yAxis;
		this.colorCodeColumnName = colorCodeLabel;
		
		this.plot = new StackedAreaPlot();
		this.legend = new CategoricalLegend();

		this.cm = new XYChartMeasurements();
		
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.getColumnAsStringArray(this.colorCodeLabel);
		this.legend.setIncludeLegend(true);
		
	}

	@Override
	public void create() {
		Double[] xValues = DataItem.convertToDoubleList(this.xData);
		Double[] yValues = DataItem.convertToDoubleList(this.yData);
		String[] hueValues = CommonArray.removeDuplicates(this.colorCodeValues);
		
		GroupBy gb = this.dataFrame.groupBy(this.xColumnName);
		
		
		Double minX = CommonArray.minValue(xValues);
		Double maxX = CommonArray.maxValue(xValues);
		double maxYAxisValue = gb.sum().maxInColumn(this.yColumnName);
		
		this.axis.calculateXAxis(minX, maxX);
		this.axis.calculateYAxis(0.0, maxYAxisValue);
		
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
		
		this.plot.drawPlot(g, this.axis, this.dataFrame, this.xColumnName, this.yColumnName, this.colorCodeColumnName, this.cm);
	
		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);
		
		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm);
		}
		
		
		this.drawTitle(g, this.cm);
		
	}
	
	public void setStyle(Styles style) {
		Style styleToSet = StyleFactory.getStyle(style);
		this.axis.setStyle(styleToSet);
//		this.plot.setStyle(styleToSet);
		this.cm.setStyle(styleToSet);
		this.legend.setStyle(styleToSet);
		
		this.setTitleFont(styleToSet.getTitleFont());
		this.setTitleColor(styleToSet.getTitleColor());
		this.setImageBackgroundColor(styleToSet.getChartBackgroundColor());
	}
}
