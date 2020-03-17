package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.StripChartAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Common.MathHelpers;
import thesis.Helpers.Palette;

public class StripPlot extends Plot{
	private Color[] colorPalette = Palette.Fire;

	private Color pointColor = colorPalette[0];
	private int pointRadius = 5;
	private double pointTransparency = 1;
	
	private boolean pointOutline = false;
	private Color outlineColor = Color.black;
	private int barOutlineWidth = 1;


	private double jitter = 0.2;
	
	private boolean dodge = false;


	public void drawPlot(Graphics2D g, StripChartAxis axis, Object data, String[] xDataOrdered, String typeOfData, XYChartMeasurements cm) {
		Random r = new Random();
		if (typeOfData == "singleCatagory") {
			Double[] allValues = (Double[])data;
			int maxJitter = (int) (this.jitter * cm.getPlotWidth()/2);
			int minJitter = (int) (-this.jitter * cm.getPlotWidth()/2);
			for (int i = 0; i < allValues.length; i++) {
				int x = xCategoryNumToPlotX(0, 1, cm);
				int y = yTickNumToPlotY(allValues[i], axis.getYTicksValues(), cm);
				
				int jitterAmount = r.nextInt((maxJitter - minJitter) + 1) + minJitter;
				
				drawDataPoint(g, x + jitterAmount, y, this.pointColor);
			}
			
		} else if (typeOfData == "multipleCatagoriesAndNoHueValue") {
			int columnWidth = cm.getPlotWidth()/xDataOrdered.length;
			int maxJitter = (int) (this.jitter * columnWidth/2);
			int minJitter = (int) (-this.jitter * columnWidth/2);
			
			HashMap<Object, Double[]> categoryMap =  (HashMap<Object, Double[]>)data; 
			
			int xCatagoryCount = 0;
			for (String xCategory : xDataOrdered) {
				Double[] values = categoryMap.get(xCategory);
				
				for (int valueCount = 0; valueCount < values.length; valueCount++) {
					
					int jitterAmount = r.nextInt((maxJitter - minJitter) + 1) + minJitter;
					int x = xCategoryNumToPlotX(xCatagoryCount, xDataOrdered.length, cm);
					int y = yTickNumToPlotY(values[valueCount], axis.getYTicksValues(), cm);
					drawDataPoint(g, x + jitterAmount, y, this.colorPalette[xCatagoryCount % this.colorPalette.length]);
				}
				
				
				xCatagoryCount++;
			}
			
		} else if (typeOfData == "multipleCatagoriesAndHueValue") {
			
		}
		
	}
	
	private void drawDataPoint(Graphics2D g, int xCenter, int yCenter, Color dataPointColor) {
		
		g.setColor(dataPointColor);
		g.fillOval(xCenter - this.pointRadius/2, yCenter - this.pointRadius/2, this.pointRadius, this.pointRadius);
		
		if (this.pointOutline) {			
			g.setColor(this.outlineColor);
			g.drawOval(xCenter - this.pointRadius/2, yCenter - this.pointRadius/2, this.pointRadius, this.pointRadius);
		}
	}
	
	private int xCategoryNumToPlotX(double xCatagoryNum, int totalXCategories, XYChartMeasurements cm) {
		int widthOfXUnit = (cm.getPlotWidth() / (totalXCategories));

		return (int) ((xCatagoryNum) * widthOfXUnit) + (int) (0.5f * widthOfXUnit) + cm.imageLeftToPlotLeftWidth();
	}

	private int yTickNumToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}
	
	
	
}
