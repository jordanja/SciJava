package thesis.Charter.Charts;

import thesis.DataFrame.DataFrame;

public class CorrelationChart extends XYChart {

//	CorrelationChartAxis axis;
//	CorrelationPlot plot;
//	CorrelationCHartLegend legend;
	
	
	public CorrelationChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.getColumnAsDataItemArray(xAxis), dataFrame.getColumnAsDataItemArray(yAxis));
		

	}
	
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

}
