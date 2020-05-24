package thesis.Charter.Plots;

import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;

public class HistogramPlot extends Plot{

	public void drawPlot(Graphics2D g, NumericAxis axis, int[] binCount, double binSize, XYChartMeasurements cm) {
		double[] yTicks = axis.getYTicksValues();

		g.setColor(Color.BLACK);
		int bottomY = binCountToYPixel(0, yTicks, cm);
		for (int i = 0; i < binCount.length; i++) {			
			int leftX = binNumToXPixel(i, binCount.length, cm);
			int rightX = binNumToXPixel(i + 1, binCount.length, cm);
			int topY = binCountToYPixel(binCount[i], yTicks, cm);
			
			g.fillRect(leftX, bottomY, rightX - leftX, topY - bottomY);
		}
		
	}
	
	private int binNumToXPixel(double binNum, int numBins, XYChartMeasurements cm) {
		return (int)(cm.imageLeftToPlotLeftWidth() + binNum * (cm.getPlotWidth()/numBins));
	}
	
	public int binCountToYPixel(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

}
