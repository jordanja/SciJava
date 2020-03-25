package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.List;


import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.AxisFactory;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.LegendPackage.Legend;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.DataFrame.*;

public class ScatterChart extends XYChart {

	protected NumericAxis axis;
	protected ScatterPlot plot;
	protected Legend legend;

	private String colorCodeLabel;
	private String[] colorCodeValues;

	public ScatterChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsArray(xAxis), dataFrame.getColumnAsArray(yAxis), "Scatter");

		this.axis = (NumericAxis) AxisFactory.getAxis("Scatter");
		this.plot = (ScatterPlot) PlotFactory.getPlot("Scatter");
		this.legend = new Legend();

		this.cm = new XYChartMeasurements();

	}

	public void setXAxis(String xAxis) {
		this.xData = this.dataFrame.getColumnAsArray(xAxis);
	}

	public void setYAxis(String yAxis) {
		this.yData = this.dataFrame.getColumnAsArray(yAxis);
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

	public void Create() {
		this.axis.calculateXAxis(CommonMath.minimumValue(this.xData), CommonMath.maximumValue(this.xData));
		this.axis.calculateYAxis(CommonMath.minimumValue(this.yData), CommonMath.maximumValue(this.yData));

		if (this.legend.getIncludeLegend()) {
			this.legend.calculateLegend(this.colorCodeLabel, this.colorCodeValues);
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

		this.plot.drawPlot(g, this.axis, this.xData, this.yData, this.colorCodeValues, this.cm);

		this.axis.drawXAxisLabel(g, this.cm);
		this.axis.drawYAxisLabel(g, this.cm);

		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm, this.plot.getColorPalette());
		}

		this.drawTitle(g, this.cm);

//		this.drawXDebugLines(g, cm);
//		this.drawYDebugLines(g, cm);

		g.dispose();
	}

	public Axis getAxis() {
		return this.axis;
	}

	public Plot getPlot() {
		return this.plot;
	}

	public Legend getLegend() {
		return this.legend;
	}

	public XYChartMeasurements getChartMeasurements() {
		return this.cm;
	}

}
