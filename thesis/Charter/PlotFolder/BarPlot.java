package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import thesis.Auxiliary.MathHelpers;
import thesis.Auxiliary.NiceScale;
import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class BarPlot extends Plot {
	
	private Color[] colorPalette = Palette.Default;

	
	public void drawPlot(Graphics2D g, BarChartAxis axis, DataItem[] xData, DataItem[] yData, XYChartMeasurements cm) {
		String[] xTicks = axis.getxTicks();
		double[] yTicks = Arrays.stream(axis.getyTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
	
		g.setColor(Color.BLACK);
		
		NiceScale yNS = axis.getyNS();
		
		int quaterWidthOfXUnit = (cm.getPlotWidth()/(4 * xData.length));
		
		for (int barCount = 0; barCount < xTicks.length; barCount++) {
			g.fillRect(
				worldXNumToPlotXPos(barCount, xData.length, cm) - quaterWidthOfXUnit, 
				worldYPosToPlotYPos(0, yNS, yTicks, cm), 
				2 * quaterWidthOfXUnit, 
				worldYPosToPlotYPos(yData[barCount].getValueConvertedToDouble(), yNS, yTicks, cm) - worldYPosToPlotYPos(0, yNS, yTicks, cm)
			);
		}
		
	}
	
	


	
	
	private int worldXNumToPlotXPos(int xTickNum, int totalxTicks, XYChartMeasurements cm) {
		int halfWidthOfXUnit = (cm.getPlotWidth()/(2 * totalxTicks));
		return (int) MathHelpers.map(xTickNum, 0, totalxTicks - 1, cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit, cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);
	}
	
	private int worldYPosToPlotYPos(double yPos, NiceScale yNS, double[] yTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(yPos, yNS.getNiceMin(), yTicks[yTicks.length - 1 ], cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
	}






	@Override
	public void drawPlot(Graphics2D g, Axis axis, DataItem[] xData, DataItem[] yData, Object[] colorCodeValues,
			XYChartMeasurements cm) {
		// TODO Auto-generated method stub
		
	}
	
	
}
