package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;

public class HistogramPlot extends Plot{

	private Color barColor = new Color(155,175,206);
	
	private Color barOutlineColor = new Color(255, 255, 255);
	private int barOutlineWidth = 1;
	private boolean drawBarOutline = true;
	
	public void drawPlot(Graphics2D g, NumericAxis axis, int[] binCount, double binSize, XYChartMeasurements cm) {
		double[] yTicks = axis.getYTicksValues();

		int bottomY = binCountToYPixel(0, yTicks, cm);
		for (int i = 0; i < binCount.length; i++) {			
			int leftX = binNumToXPixel(i, binCount.length, cm);
			int rightX = binNumToXPixel(i + 1, binCount.length, cm);
			int topY = binCountToYPixel(binCount[i], yTicks, cm);
			int width = rightX - leftX;
			int height = topY - bottomY;
			
			g.setColor(this.barColor);
			g.fillRect(leftX, bottomY, width, height);
			
			if (this.drawBarOutline) {				
				g.setStroke(new BasicStroke(this.barOutlineWidth));
				g.setColor(this.barOutlineColor);
				g.drawRect(leftX, bottomY, width, height);
			}
		}
		
	}
	
	private int binNumToXPixel(double binNum, int numBins, XYChartMeasurements cm) {
		return (int)(cm.imageLeftToPlotLeftWidth() + binNum * (cm.getPlotWidth()/numBins));
	}
	
	public int binCountToYPixel(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

	public Color getBarColor() {
		return barColor;
	}

	public void setBarColor(Color barColor) {
		this.barColor = barColor;
	}

	public Color getBarOutlineColor() {
		return barOutlineColor;
	}

	public void setBarOutlineColor(Color barOutlineColor) {
		this.barOutlineColor = barOutlineColor;
	}

	public int getBarOutlineWidth() {
		return barOutlineWidth;
	}

	public void setBarOutlineWidth(int barOutlineWidth) {
		this.barOutlineWidth = barOutlineWidth;
	}

	public boolean isDrawBarOutline() {
		return drawBarOutline;
	}

	public void setDrawBarOutline(boolean drawBarOutline) {
		this.drawBarOutline = drawBarOutline;
	}

	
	
}
