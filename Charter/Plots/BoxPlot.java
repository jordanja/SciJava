package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.BoxChartAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Styles.Style;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class BoxPlot extends Plot {


	private Color outlineColor = Color.black;
	private int barOutlineWidth = 3;

	private Color barColor = colorPalette[0];

	// When there are no categories, this is the width of a bar relative to the plot
	private double singluarBarLengthPercentage = 0.8f;

	// When there are categories but no color code bars, this is the width of a bar
	private double singlularClusterBarLengthPercentage = 0.5f;

	// When there are colorCode bars, this is the total width of all bars in a
	// cluster
	private double multipleBarLengthPercentage = 0.8f;
	// When there are colorCode bars, this is the number of pixels between bars in a
	// cluster
	private int multipleBarPixelSpacing = 0;

	public void drawPlot(Graphics2D g, BoxChartAxis axis, HashMap<Object, Object> data, String[] categoryOrder,
			String typeOfData, String orientation, XYChartMeasurements cm) {

		int numberOfCategoricalTicks = axis.getCategoricalTicks().length;

		if (typeOfData == "singleCatagory") {

			int lengthOfbar = (int) (singluarBarLengthPercentage
					* (orientation == "v" ? cm.getPlotWidth() : cm.getPlotHeight()));

			double[] numericTicks = axis.getNumericTicksValues();

			double min = (double) data.get("Min");
			double Q1 = (double) data.get("Q1");
			double Q2 = (double) data.get("Q2");
			double Q3 = (double) data.get("Q3");
			double max = (double) data.get("Max");

			if (orientation == "v") {
				int xCenterOfBox = xCategoryNumToPlotX(0, 1, cm);

				int xBoxStart = xCenterOfBox - (int) (0.5f * lengthOfbar);
				int yBoxStart = yValueToPlotY(Q1, numericTicks, cm);
				int boxWidth = lengthOfbar;
				int boxHeight = yValueToPlotY(Q3, numericTicks, cm) - yBoxStart;

				int yMin = yValueToPlotY(min, numericTicks, cm);
				int yMax = yValueToPlotY(max, numericTicks, cm);
				int yMedian = yValueToPlotY(Q2, numericTicks, cm);
				drawVerticleBox(g, xBoxStart, yBoxStart, boxWidth, boxHeight, xCenterOfBox, yMin, yMax, yMedian,
						this.barColor, this.outlineColor);
			} else {
				int yCenterOfBox = yCategoryNumToPlotY(0, 1, cm);

				int xBoxStart = xValueToToPlotX(Q1, numericTicks, cm);
				int yBoxStart = yCenterOfBox - (int) (0.5f * lengthOfbar);
				int boxWidth = xValueToToPlotX(Q3, numericTicks, cm) - xBoxStart;
				int boxHeight = lengthOfbar;

				int xMin = xValueToToPlotX(min, numericTicks, cm);
				int xMax = xValueToToPlotX(max, numericTicks, cm);
				int xMedian = xValueToToPlotX(Q2, numericTicks, cm);
				
				drawHorizontalBox(g, xBoxStart, yBoxStart, boxWidth, boxHeight,yCenterOfBox, xMin, xMax, xMedian, this.barColor, this.outlineColor);
				
//				System.out.println("min: " + min + "\n" + "Q1: " + Q1 + "\n" + "Q2: " + Q2 + "\n" + "Q3: " + Q3 + "\n" + "max: " + max);
				
			}

		} else if (typeOfData == "multipleCatagoriesAndNoHueValue") {
			
			int catagoryCount = 0;
			for (String category : categoryOrder) {
				Color fillColor = this.colorPalette[catagoryCount % this.colorPalette.length];
				HashMap<Object, Double> map = (HashMap<Object, Double>) data.get(category);

				double[] numericTicks = axis.getNumericTicksValues();

				double min = map.get("Min");
				double Q1 = map.get("Q1");
				double Q2 = map.get("Q2");
				double Q3 = map.get("Q3");
				double max = map.get("Max");

				if (orientation == "v") {	
					int lengthOfbar = (int) (this.singlularClusterBarLengthPercentage * cm.getPlotWidth() / numberOfCategoricalTicks);
					int xCenterOfBox = xCategoryNumToPlotX(catagoryCount, numberOfCategoricalTicks, cm);
					
					int xBoxStart = xCenterOfBox - (int) (0.5f * lengthOfbar);
					int yBoxStart = yValueToPlotY(Q1, numericTicks, cm);
					int boxWidth = lengthOfbar;
					int boxHeight = yValueToPlotY(Q3, numericTicks, cm) - yBoxStart;
					
					int yMin = yValueToPlotY(min, numericTicks, cm);
					int yMax = yValueToPlotY(max, numericTicks, cm);
					int yMedian = yValueToPlotY(Q2, numericTicks, cm);
					drawVerticleBox(g, xBoxStart, yBoxStart, boxWidth, boxHeight, xCenterOfBox, yMin, yMax, yMedian,
							fillColor, this.outlineColor);
				} else {
					int lengthOfbar = (int) (this.singlularClusterBarLengthPercentage * cm.getPlotHeight() / numberOfCategoricalTicks);
					
					int yCenterOfBox = yCategoryNumToPlotY(catagoryCount, numberOfCategoricalTicks, cm);
					
					int xBoxStart = xValueToToPlotX(Q1, numericTicks, cm);
					int yBoxStart = yCenterOfBox - (int) (0.5f * lengthOfbar);
					int boxWidth = xValueToToPlotX(Q3, numericTicks, cm) - xBoxStart;
					int boxHeight = lengthOfbar;
					
					int xMin = xValueToToPlotX(min, numericTicks, cm);
					int xMax = xValueToToPlotX(max, numericTicks, cm);
					int xMedian = xValueToToPlotX(Q2, numericTicks, cm);
					drawHorizontalBox(g, xBoxStart, yBoxStart, boxWidth, boxHeight, yCenterOfBox, xMin, xMax, xMedian, fillColor, this.outlineColor);
				}

				catagoryCount++;
			}
		} else if (typeOfData == "multipleCatagoriesAndHueValue") {
			int catagoryCount = 0;
			for (String category : categoryOrder) {
				HashMap<Object, HashMap<Object, Double>> categoryMap = (HashMap<Object, HashMap<Object, Double>>) data
						.get(category);

				int numColorCodeValues = categoryMap.keySet().size();
				int totalSpaceInbetweenBars = (numColorCodeValues - 1) * this.multipleBarPixelSpacing;
				
				int lengthOfColorCodeBar;
				int offsetAtBarsStart;
				
				if (orientation == "v") {					
					lengthOfColorCodeBar = (int) ((((cm.getPlotWidth() / (numberOfCategoricalTicks))
							* this.multipleBarLengthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
					
					offsetAtBarsStart = xCategoryNumToPlotX(catagoryCount - 0.5f, data.keySet().size(), cm)
							+ (int) (((1 - this.multipleBarLengthPercentage) / 2) * (cm.getPlotWidth() / (numberOfCategoricalTicks)));
				} else {
					lengthOfColorCodeBar = (int) ((((cm.getPlotHeight() / (numberOfCategoricalTicks))
							* this.multipleBarLengthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
					
					offsetAtBarsStart = yCategoryNumToPlotY(catagoryCount - 0.5f, data.keySet().size(), cm)
							+ (int) (((1 - this.multipleBarLengthPercentage) / 2) * (cm.getPlotHeight() / (numberOfCategoricalTicks)));

				}
				

				int colorCodeCount = 0;
				for (Object colorCode : categoryMap.keySet()) {
					Color fillColor = this.colorPalette[colorCodeCount % this.colorPalette.length];

					HashMap<Object, Double> map = categoryMap.get(colorCode);

					double[] numericalTicks = axis.getNumericTicksValues();

					double min = map.get("Min");
					double Q1 = map.get("Q1");
					double Q2 = map.get("Q2");
					double Q3 = map.get("Q3");
					double max = map.get("Max");

					if (orientation == "v") {						
						int xBoxStart = offsetAtBarsStart
								+ ((lengthOfColorCodeBar + this.multipleBarPixelSpacing) * colorCodeCount);
						int yBoxStart = yValueToPlotY(Q1, numericalTicks, cm);
						int boxWidth = lengthOfColorCodeBar;
						int boxHeight = yValueToPlotY(Q3, numericalTicks, cm) - yBoxStart;
						
						int xCenterOfBox = xBoxStart + boxWidth / 2;
						
						int yMin = yValueToPlotY(min, numericalTicks, cm);
						int yMax = yValueToPlotY(max, numericalTicks, cm);
						int yMedian = yValueToPlotY(Q2, numericalTicks, cm);
						
						drawVerticleBox(g, xBoxStart, yBoxStart, boxWidth, boxHeight, xCenterOfBox, yMin, yMax, yMedian,
								fillColor, this.outlineColor);
					} else {
						int xBoxStart = xValueToToPlotX(Q1, numericalTicks, cm);
						int yBoxStart = offsetAtBarsStart + ((lengthOfColorCodeBar + this.multipleBarPixelSpacing) * colorCodeCount);
						int boxWidth = xValueToToPlotX(Q3, numericalTicks, cm) - xValueToToPlotX(Q1, numericalTicks, cm);
						int boxHeight = lengthOfColorCodeBar;
						
						int yCenterOfBox = yBoxStart + boxHeight / 2;
						
						int xMin = xValueToToPlotX(min, numericalTicks, cm);
						int xMax = xValueToToPlotX(max, numericalTicks, cm);
						int xMedian = xValueToToPlotX(Q2, numericalTicks, cm);
						
						drawHorizontalBox(g, xBoxStart, yBoxStart, boxWidth, boxHeight, yCenterOfBox, xMin, xMax, xMedian, fillColor, this.outlineColor);
						
//						System.out.println("min: " + min + "\n" + "Q1: " + Q1 + "\n" + "Q2: " + Q2 + "\n" + "Q3: " + Q3 + "\n" + "max: " + max + "\n");
					}

					colorCodeCount++;
				}

				catagoryCount++;
			}
		}

	}

	private void drawHorizontalBox(Graphics2D g, int xBoxStart, int yBoxStart, int boxWidth, int boxHeight,
			int yCenterOfBox, int xMin, int xMax, int xMedian, Color fillColor, Color outlineColor) {
		g.setColor(fillColor);
		g.fillRect(xBoxStart, yBoxStart, boxWidth, boxHeight);
		
		g.setColor(outlineColor);
		g.drawRect(xBoxStart, yBoxStart, boxWidth, boxHeight);
		
		// Line from left of box to the min
		g.drawLine(xBoxStart, yCenterOfBox, xMin, yCenterOfBox);
		// Line up min
		g.drawLine(xMin, yBoxStart, xMin, yBoxStart + boxHeight);
		
		// Line from the right of box to the max
		g.drawLine(xBoxStart + boxWidth, yCenterOfBox, xMax, yCenterOfBox);
		// Line up the max
		g.drawLine(xMax, yBoxStart, xMax, yBoxStart + boxHeight);
		
		// Line through median
		g.drawLine(xMedian, yBoxStart, xMedian, yBoxStart + boxHeight);
		
	}

	private void drawVerticleBox(Graphics2D g, int xBoxStart, int yBoxStart, int boxWidth, int boxHeight,
			int xCenterOfBox, int yMin, int yMax, int yMedian, Color fillColor, Color outlineColor) {
		g.setColor(fillColor);
		g.fillRect(xBoxStart, yBoxStart, boxWidth, boxHeight);

		g.setColor(outlineColor);
		g.drawRect(xBoxStart, yBoxStart, boxWidth, boxHeight);

		// Line from bottom of box down to min
		g.drawLine(xCenterOfBox, yBoxStart, xCenterOfBox, yMin);
		// Line across min
		g.drawLine(xBoxStart, yMin, xBoxStart + boxWidth, yMin);

		// Line from top of box up to max
		g.drawLine(xCenterOfBox, yBoxStart + boxHeight, xCenterOfBox, yMax);
		// Line across max
		g.drawLine(xBoxStart, yMax, xBoxStart + boxWidth, yMax);

		// Line through median
		g.drawLine(xBoxStart, yMedian, xBoxStart + boxWidth, yMedian);

	}

	private int xCategoryNumToPlotX(double xCategoryNum, int totalXCategories, XYChartMeasurements cm) {
		int widthOfXUnit = cm.getPlotWidth() / totalXCategories;
		return (int) ((xCategoryNum) * widthOfXUnit) + (int) (0.5f * widthOfXUnit) + cm.imageLeftToPlotLeftWidth();
	}

	private int xValueToToPlotX(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(),
				cm.imageLeftToPlotRightWidth());
	}

	private int yCategoryNumToPlotY(double yCategoryNum, int totalYCategories, XYChartMeasurements cm) {
		int heightOfYUnit = cm.getPlotHeight() / totalYCategories;
		return (int) ((yCategoryNum) * heightOfYUnit) + (int) (0.5f * heightOfYUnit) + cm.imageBottomToPlotBottomHeight();
	}

	private int yValueToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

	public Color[] getColorPalette() {
		return this.colorPalette;
	}

	public void setColorPalette(Color[] boxColorPalette) {
		this.colorPalette = boxColorPalette;
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

	public Color getBarColor() {
		return barColor;
	}

	public void setBarColor(Color barColor) {
		this.barColor = barColor;
	}

	public double getSingluarBarLengthPercentage() {
		return singluarBarLengthPercentage;
	}

	public void setSingluarBarWidthPercentage(double singluarBarLengthPercentage) {
		this.singluarBarLengthPercentage = singluarBarLengthPercentage;
	}

	public double getSinglularClusterBarLengthPercentage() {
		return singlularClusterBarLengthPercentage;
	}

	public void setSinglularClusterBarLengthPercentage(double singlularClusterBarLengthPercentage) {
		this.singlularClusterBarLengthPercentage = singlularClusterBarLengthPercentage;
	}

	public double getMultipleBarLengthPercentage() {
		return multipleBarLengthPercentage;
	}

	public void setMultipleBarLengthPercentage(double multipleBarLengthPercentage) {
		this.multipleBarLengthPercentage = multipleBarLengthPercentage;
	}

	public int getMultipleBarPixelSpacing() {
		return multipleBarPixelSpacing;
	}

	public void setMultipleBarPixelSpacing(int multipleBarPixelSpacing) {
		this.multipleBarPixelSpacing = multipleBarPixelSpacing;
	}

	public void setStyle(Style styleToSet) {
		super.setStyle(styleToSet);
		this.setBarColor(styleToSet.getColorPalette()[0]);
		this.setOutlineColor(styleToSet.getBoxPlotOutlineColor());
	}

}
