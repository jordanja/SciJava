package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.BarChartMeasurements;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class BarChart extends XYChart{

	BarChartAxis axis;
	BarPlot plot;
	Legend legend;
	
	
	
	public BarChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Bar");
		
		this.axis = new BarChartAxis();
		this.plot = new BarPlot();
		this.legend = new Legend();
		
		cm = new BarChartMeasurements();

	}

	@Override
	public void Create() {
		
		this.axis.setXAxis(this.xData);
		this.axis.setYAxis(this.xData, this.yData);
		
		
		if (this.legend.getIncludeLegend()) {
			Object[] hueValies = {"a", "b"};
			this.legend.calculateLegend("Hello", hueValies);
		}
		
		cm.calculateChartImageMetrics(this.axis, this.plot, this.legend, getTitle(), getTitleFont());
		
		instantiateChart(cm);
		
		Graphics2D g = initializaGraphicsObject(cm);
		drawBackground(g, cm);
		
		this.plot.drawChartBackground(g, cm);
		
		this.axis.drawAxis(g, cm);
		
		this.plot.drawPlotOutline(g, cm);
		
		this.axis.drawAxisTicks(g, cm);
		
		this.plot.drawPlot(g, this.axis, xData, yData, cm);
		
		this.axis.drawXAxisLabel(g, cm);
		this.axis.drawYAxisLabel(g, cm);
		
//		if (this.legend.getIncludeLegend()) {
//			this.legend.drawLegend(g, cm, this.plot.getColorPalette());
//		}
		
		this.drawTitle(g, cm);
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

}
