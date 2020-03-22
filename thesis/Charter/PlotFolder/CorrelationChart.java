package thesis.Charter.PlotFolder;

import thesis.DataFrame.DataFrame;

public class CorrelationChart extends XYChart {

//	CorrelationChartAxis axis;
//	CorrelationPlot plot;
//	CorrelationCHartLegend legend;
	
	
	public CorrelationChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsArray(xAxis), dataFrame.getColumnAsArray(yAxis), "Box");
		

	}
	
	
	@Override
	public void Create() {
		// TODO Auto-generated method stub
		
	}

}
