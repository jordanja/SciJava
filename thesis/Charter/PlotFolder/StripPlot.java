package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.StripChartAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Helpers.Palette;

public class StripPlot extends Plot{
	private Color[] colorPalette = Palette.Default;

	private Color pointColor = colorPalette[0];
	private int pointRadius = 5;
	private double pointTransparency = 1;
	
	private boolean pointOutline = false;
	private Color outlineColor = Color.black;
	private int barOutlineWidth = 1;

	// When there are colorCode bars, this is the total width of all bars in a
	// cluster
	private double multipleBarWidthPercentage = 0.8f;
	// When there are colorCode bars, this is the number of pixels between bars in a
	// cluster
	private int multipleBarPixelSpacing = 0;

	private double jitter = 0.8;
	
	private boolean dodge = true;


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
			HashMap<Object, HashMap<Object, Double[]>> categoryMap = (HashMap<Object, HashMap<Object, Double[]>>)data; 
			
			int xCatagoryCount = 0;
			for (String xCategory : xDataOrdered) {
				HashMap<Object, Double[]> hueMap = categoryMap.get(xCategory);
				
				int numColorCodeValues = hueMap.keySet().size();
				int totalSpaceInbetweenBars = (numColorCodeValues - 1) * this.multipleBarPixelSpacing;
				int widthOfColorCodeBar = (int) ((((cm.getPlotWidth() / (xDataOrdered.length))
						* this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);

				int positionAtBarsStart = xCategoryNumToPlotX(xCatagoryCount - 0.5f, categoryMap.keySet().size(), cm)
						+ (int) (((1 - this.multipleBarWidthPercentage) / 2) * (cm.getPlotWidth() / (xDataOrdered.length))) + widthOfColorCodeBar/2;

				int colorCodeCount = 0;
				
				
				int maxJitter = (int) (this.jitter * widthOfColorCodeBar/2);
				int minJitter = (int) (-this.jitter * widthOfColorCodeBar/2);
				
				
				
				int dataPointCount = 0;
				if (this.dodge) {
					colorCodeCount = 0;
					for (Object colorCode : hueMap.keySet()) {
						Color fillColor = this.colorPalette[colorCodeCount % this.colorPalette.length];

						Double[] values = hueMap.get(colorCode);
						
						for (int valueCount = 0; valueCount < values.length; valueCount++) {
							int jitterAmount = r.nextInt((maxJitter - minJitter) + 1) + minJitter;
							int x = positionAtBarsStart + ((widthOfColorCodeBar + multipleBarPixelSpacing) * colorCodeCount);
							int y = yTickNumToPlotY(values[valueCount], axis.getYTicksValues(), cm);
							drawDataPoint(g, x + jitterAmount, y, fillColor);
						}
						colorCodeCount++;
					}
				} else {
					
					int numDataPointsInCategory = numDataPointsInCategory(hueMap);
					Color[] fillColor = new Color[numDataPointsInCategory];
					Double[] values = new Double[numDataPointsInCategory];
					
					for (Object colorCode: hueMap.keySet()) {
						Double[] valuesToAdd = hueMap.get(colorCode);
						for (Double value: valuesToAdd) {
							fillColor[dataPointCount] = this.colorPalette[colorCodeCount % this.colorPalette.length];
							values[dataPointCount] = value;
							dataPointCount++;
						}
						colorCodeCount++;
					}
					
					List<Integer> indexArray = IntStream.rangeClosed(0, values.length - 1)
						    .boxed().collect(Collectors.toList());
					Collections.shuffle(indexArray);

					
					for (int valueCount = 0; valueCount < values.length; valueCount++) {
						int jitterAmount = r.nextInt((maxJitter - minJitter) + 1) + minJitter;
						int x = xCategoryNumToPlotX(xCatagoryCount, xDataOrdered.length, cm);
						int y = yTickNumToPlotY(values[indexArray.get(valueCount)], axis.getYTicksValues(), cm);
						drawDataPoint(g, x + jitterAmount, y, fillColor[indexArray.get(valueCount)]);
					}
				}
				
				
				xCatagoryCount++;
			}
		}
		
	}
	
	private int numDataPointsInCategory(HashMap<Object, Double[]> hueMap) {
		int countDataPoints = 0;
		for (Object key: hueMap.keySet()) {
			countDataPoints += hueMap.get(key).length;
		}
		return countDataPoints;
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
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

	public Color[] getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(Color[] colorPalette) {
		this.colorPalette = colorPalette;
	}

	public Color getPointColor() {
		return pointColor;
	}

	public void setPointColor(Color pointColor) {
		this.pointColor = pointColor;
	}

	public int getPointRadius() {
		return pointRadius;
	}

	public void setPointRadius(int pointRadius) {
		this.pointRadius = pointRadius;
	}

	public double getPointTransparency() {
		return pointTransparency;
	}

	public void setPointTransparency(double pointTransparency) {
		this.pointTransparency = pointTransparency;
	}

	public boolean isPointOutline() {
		return pointOutline;
	}

	public void setPointOutline(boolean pointOutline) {
		this.pointOutline = pointOutline;
	}

	public Color getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	public int getBarOutlineWidth() {
		return barOutlineWidth;
	}

	public void setBarOutlineWidth(int barOutlineWidth) {
		this.barOutlineWidth = barOutlineWidth;
	}

	public double getMultipleBarWidthPercentage() {
		return multipleBarWidthPercentage;
	}

	public void setMultipleBarWidthPercentage(double multipleBarWidthPercentage) {
		this.multipleBarWidthPercentage = multipleBarWidthPercentage;
	}

	public int getMultipleBarPixelSpacing() {
		return multipleBarPixelSpacing;
	}

	public void setMultipleBarPixelSpacing(int multipleBarPixelSpacing) {
		this.multipleBarPixelSpacing = multipleBarPixelSpacing;
	}

	public double getJitter() {
		return jitter;
	}

	public void setJitter(double jitter) {
		this.jitter = jitter;
	}

	public boolean isDodge() {
		return dodge;
	}

	public void setDodge(boolean dodge) {
		this.dodge = dodge;
	}
	
	
	
}
