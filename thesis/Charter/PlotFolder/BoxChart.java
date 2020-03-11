package thesis.Charter.PlotFolder;

import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.BarChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class BoxChart extends XYChart{

	BarChartAxis axis;
	BoxPlot plot;
	Legend legend;
	
	public BoxChart(DataFrame dataFrame, String xAxis) {
		super(dataFrame,dataFrame.GetColumnAsArray(xAxis), "Box");
	}
	
	public BoxChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Box");
		
		this.axis = new BarChartAxis();
		this.plot = new BoxPlot();
		this.legend = new Legend();
		
		cm = new BarChartMeasurements();
	}

	@Override
	public void Create() {

		
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

}
