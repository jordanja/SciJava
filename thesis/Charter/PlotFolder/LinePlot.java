package thesis.Charter.PlotFolder;

import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataItem;

public class LinePlot extends Plot{

	public void drawPlot(Graphics2D g, NumericAxis axis, HashMap<Number, Number> data, XYChartMeasurements cm) {

		System.out.println("use this");
	}

	@Override
	public void drawPlot(Graphics2D g, Axis axis, DataItem[] xData, DataItem[] yData, Object[] colorCodeValues,
			XYChartMeasurements cm) {
		// TODO Auto-generated method stub
		
	}


}
