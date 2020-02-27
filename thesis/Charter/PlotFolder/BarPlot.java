package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class BarPlot extends Plot {
	
	
	private Color[] colorPalette = Palette.Default;

	
	
	public void drawPlot(Graphics2D g, BarChartAxis axis, DataItem[] xData, DataItem[] yData, XYChartMeasurements cm) {
		
	}



	@Override
	public void drawPlot(Graphics2D g, NumericAxis axis, DataItem[] xData, DataItem[] yData, Object[] colorCodeValues,
			XYChartMeasurements cm) {
		// TODO Auto-generated method stub
		
	}
	
	
}
