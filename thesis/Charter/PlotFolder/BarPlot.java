package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
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
	
	private Color[] colorPalette = Palette.Fire;
	private boolean drawBarOutline = false;
	private Color barOutlineColour = Color.black;
	private int barOutlineWidth = 1;
	
	private double barWidthPercentage = 0.5f;
	
	private Color barColor = colorPalette[0];
	
	private String[] order = {};

	
	
	
	
	public void drawPlot(Graphics2D g, BarChartAxis axis, DataItem[] xData, DataItem[] yData, XYChartMeasurements cm) {
		String[] xTicks = axis.getxTicks();
		double[] yTicks = Arrays.stream(axis.getyTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
		
		
		
		NiceScale yNS = axis.getyNS();
		
		int quaterWidthOfXUnit = (int)(this.barWidthPercentage * cm.getPlotWidth()/(2 * xData.length));
		
		for (int barCount = 0; barCount < xTicks.length; barCount++) {
			g.setColor(this.barColor);
			int xBoxStart = worldXNumToPlotXPos(barCount, xData.length, cm) - quaterWidthOfXUnit;
			int yBoxStart = worldYPosToPlotYPos(0, yNS, yTicks, cm);
			int boxWidth = 2 * quaterWidthOfXUnit;
			int boxHeight = worldYPosToPlotYPos(yData[barCount].getValueConvertedToDouble(), yNS, yTicks, cm) - worldYPosToPlotYPos(0, yNS, yTicks, cm);
			g.fillRect(xBoxStart, yBoxStart, boxWidth, boxHeight);
			
			if (this.drawBarOutline) {
				g.setStroke(new BasicStroke(this.barOutlineWidth));
				g.setColor(this.barOutlineColour);
				g.drawRect(xBoxStart, yBoxStart, boxWidth, boxHeight);
			}
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
	

	
	public boolean isDrawBarOutline() {
		return drawBarOutline;
	}

	public void setDrawBarOutline(boolean drawBarOutline) {
		this.drawBarOutline = drawBarOutline;
	}

	public Color getBarOutlineColour() {
		return barOutlineColour;
	}

	public void setBarOutlineColour(Color barOutlineColour) {
		this.barOutlineColour = barOutlineColour;
	}

	public int getBarOutlineWidth() {
		return barOutlineWidth;
	}
	
	public void setBarOutlineWidth(int barOutlineWidth) {
		this.barOutlineWidth = barOutlineWidth;
	}
	
	public double getBarWidthPercentage() {
		return barWidthPercentage;
	}

	public void setBarWidthPercentage(double barWidthPercentage) {
		this.barWidthPercentage = barWidthPercentage;
	}
	
	public String[] getOrder() {
		return this.order;
	}
	
	public void setOrder(String[] order) {
		this.order = order;
	}
	
}
