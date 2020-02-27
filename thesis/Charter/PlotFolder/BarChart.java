package thesis.Charter.PlotFolder;

import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.LegendPackage.Legend;
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
		
		cm = new XYChartMeasurements();

	}

	@Override
	public void Create() {
		
		
		this.axis.setXAxis(this.xData);
		this.axis.setYAxis(this.xData, this.yData);
		
	}

}
