package thesis.Charter.Plots;

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
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Helpers.Palette;

public class StripPlot extends Plot{

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

	private double jitter = 0.3;
	
	private boolean dodge = true;


	public void drawPlot(Graphics2D g, StripChartAxis axis, Object data, String[] categoryOrder, String typeOfData, String orientation, XYChartMeasurements cm) {
		Random r = new Random();
		if (typeOfData == "singleCategory") {
			Double[] allValues = (Double[])data;
			for (int i = 0; i < allValues.length; i++) {
				
				int x = 0;
				int y = 0;
				if (orientation == "v") {					
					int maxJitter = (int) (this.jitter * cm.getPlotWidth()/2);
					int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
					x = xCategoryNumToPlotX(0, 1, cm) + jitterAmount;
					y = yValueToPlotY(allValues[i], axis.getNumericTicksValues(), cm);				
				} else {
					int maxJitter = (int) (this.jitter * cm.getPlotHeight()/2);
					int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
					x = xValueNumToPlotX(allValues[i], axis.getNumericTicksValues(), cm);
					y = yCategoryNumToPlotY(0, 1, cm) + jitterAmount;
				}
				drawDataPoint(g, x, y, this.pointColor);
			}
			
		} else if (typeOfData == "multipleCategoriesAndNoHueValue") {
			
			HashMap<Object, Double[]> categoryMap =  (HashMap<Object, Double[]>)data; 
			
			int categoryCount = 0;
			for (String category : categoryOrder) {
				Double[] values = categoryMap.get(category);
				
				for (int valueCount = 0; valueCount < values.length; valueCount++) {
					int x = 0;
					int y = 0;
					if (orientation == "v") {
						int columnWidth = cm.getPlotWidth()/categoryOrder.length;
						int maxJitter = (int) (this.jitter * columnWidth/2);
						int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
						x = xCategoryNumToPlotX(categoryCount, categoryOrder.length, cm) + jitterAmount;
						y = yValueToPlotY(values[valueCount], axis.getNumericTicksValues(), cm);
					} else {
						int rowWidth = cm.getPlotHeight()/categoryOrder.length;
						int maxJitter = (int) (this.jitter * rowWidth/2);
						int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
						x = xValueNumToPlotX(values[valueCount], axis.getNumericTicksValues(), cm);
						y = yCategoryNumToPlotY(categoryCount, categoryOrder.length, cm) + jitterAmount;
					}
					drawDataPoint(g, x, y, this.colorPalette[categoryCount % this.colorPalette.length]);
				}
				
				
				categoryCount++;
			}
			
		} else if (typeOfData == "multipleCategoriesAndHueValue") {			
			HashMap<Object, HashMap<Object, Double[]>> categoryMap = (HashMap<Object, HashMap<Object, Double[]>>)data; 
			
			int categoryCount = 0;
			for (String xCategory : categoryOrder) {
				HashMap<Object, Double[]> hueMap = categoryMap.get(xCategory);
				
				int numColorCodeValues = hueMap.keySet().size();
				int totalSpaceInbetweenBars = (numColorCodeValues - 1) * this.multipleBarPixelSpacing;

				int colorCodeCount = 0;
				
				
				
				int dataPointCount = 0;
				if (this.dodge) {
					colorCodeCount = 0;
					for (Object colorCode : hueMap.keySet()) {
						Color fillColor = this.colorPalette[colorCodeCount % this.colorPalette.length];

						Double[] values = hueMap.get(colorCode);
						
						for (int valueCount = 0; valueCount < values.length; valueCount++) {
							int x = 0;
							int y = 0;
							if (orientation == "v") {								
								int widthOfColorCodeBar = (int) ((((cm.getPlotWidth() / (categoryOrder.length)) * this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
								int positionAtBarsStart = xCategoryNumToPlotX(categoryCount - 0.5f, categoryMap.keySet().size(), cm) + (int) (((1 - this.multipleBarWidthPercentage) / 2) * (cm.getPlotWidth() / (categoryOrder.length))) + widthOfColorCodeBar/2;
								int maxJitter = (int) (this.jitter * widthOfColorCodeBar/2);
								int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
								x = positionAtBarsStart + ((widthOfColorCodeBar + multipleBarPixelSpacing) * colorCodeCount) + jitterAmount;
								y = yValueToPlotY(values[valueCount], axis.getNumericTicksValues(), cm);
							} else {
								int widthOfColorCodeBar = (int) ((((cm.getPlotHeight() / (categoryOrder.length)) * this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
								int positionAtBarsStart = yCategoryNumToPlotY(categoryCount - 0.5f, categoryMap.keySet().size(), cm) + (int) (((1 - this.multipleBarWidthPercentage) / 2) * (cm.getPlotHeight() / (categoryOrder.length))) + widthOfColorCodeBar/2;
								int maxJitter = (int) (this.jitter * widthOfColorCodeBar/2);
								int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
								x = xValueNumToPlotX(values[valueCount], axis.getNumericTicksValues(), cm);
								y = positionAtBarsStart + ((widthOfColorCodeBar + multipleBarPixelSpacing) * colorCodeCount) + jitterAmount;
								
							}
							drawDataPoint(g, x, y, fillColor);
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
						int x = 0;
						int y = 0;
						if (orientation == "v") {							
							int widthOfColorCodeBar = (int) ((((cm.getPlotWidth() / (categoryOrder.length)) * this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
							int maxJitter = (int) (this.jitter * widthOfColorCodeBar/2);
							int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
							x = xCategoryNumToPlotX(categoryCount, categoryOrder.length, cm) + jitterAmount;
							y = yValueToPlotY(values[indexArray.get(valueCount)], axis.getNumericTicksValues(), cm);
						} else {
							int widthOfColorCodeBar = (int) ((((cm.getPlotHeight() / (categoryOrder.length)) * this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
							int maxJitter = (int) (this.jitter * widthOfColorCodeBar/2);
							int jitterAmount = r.nextInt((2 * maxJitter) + 1) - maxJitter;
							x = xValueNumToPlotX(values[indexArray.get(valueCount)], axis.getNumericTicksValues(), cm);
							y = yCategoryNumToPlotY(categoryCount, categoryOrder.length, cm) + jitterAmount;
						}
						drawDataPoint(g, x, y, fillColor[indexArray.get(valueCount)]);
					}
				}
				
				
				categoryCount++;
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
	
	private int xCategoryNumToPlotX(double xCategoryNum, int totalXCategories, XYChartMeasurements cm) {
		int widthOfXUnit = (cm.getPlotWidth() / (totalXCategories));
		return (int) ((xCategoryNum) * widthOfXUnit) + (int) (0.5f * widthOfXUnit) + cm.imageLeftToPlotLeftWidth();
	}
	private int xValueNumToPlotX(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(),
				cm.imageLeftToPlotRightWidth());
	}

	private int yCategoryNumToPlotY(double yCategoryNum, int totalYCategories, XYChartMeasurements cm) {
		int widthOfYUnit = (cm.getPlotHeight() / (totalYCategories));
		return (int) ((yCategoryNum) * widthOfYUnit) + (int) (0.5f * widthOfYUnit) + cm.imageBottomToPlotBottomHeight();
	}
	private int yValueToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
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
