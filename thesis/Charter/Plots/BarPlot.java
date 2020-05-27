package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class BarPlot extends Plot {

	private Color[] colorPalette = Palette.Fire;
	private boolean drawBarOutline = false;
	private Color barOutlineColour = Color.black;
	private int barOutlineWidth = 1;

	private Font valuesFont = new Font("Dialog", Font.PLAIN, 12);

	// When there are no color code bars, this is the width of a bar
	private double singlularBarWidthPercentage = 0.5f;

	// When there are colorCode bars, this is the total width of all bars in a
	// cluster
	private double multipleBarWidthPercentage = 0.8f;
	// When there are colorCode bars, this is the number of pixels between bars in a
	// cluster
	private int multipleBarPixelSpacing = 10;

	private Color barColor = colorPalette[0];

	private boolean singleColor = true;

	private boolean drawBarValue = true;

	public void drawPlot(Graphics2D g, BarChartAxis axis, HashMap<Object, Object> data, String[] categoryOrder, String orientation,
			XYChartMeasurements cm) {
		// Are there color code values
		boolean haveColorCodeValues = (data.get(data.keySet().iterator().next()) instanceof HashMap);

		double[] numericalTicks = axis.getNumericTicksValues();

		int numCategories = data.keySet().size();
		int categoryCount = 0;
		for (String category : categoryOrder) {
			if (haveColorCodeValues) {

				HashMap<String, Double> colorCodeValues = (HashMap<String, Double>) data.get(category);
	
				int numColorCodeValues = colorCodeValues.keySet().size();
				int totalSpaceInbetweenBars = (numColorCodeValues - 1) * this.multipleBarPixelSpacing;
	
				int colorCodeCount = 0;
				for (String colorCode : colorCodeValues.keySet()) {
					Color boxColor = this.colorPalette[colorCodeCount % this.colorPalette.length];
					
					if (orientation == "v") {						
						int widthOfColorCodeBar = (int) ((((cm.getPlotWidth() / (numCategories)) * this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
						int xPositionAtBarsStart = xCategoryNumToPlotX(categoryCount - 0.5f, numCategories, cm) + (int) (((1 - this.multipleBarWidthPercentage) / 2) * (cm.getPlotWidth() / (numCategories)));
						int xBoxStart = xPositionAtBarsStart + ((widthOfColorCodeBar + this.multipleBarPixelSpacing) * colorCodeCount);
						int yBoxStart = yValueToPlotY(0, numericalTicks, cm);
						int boxWidth = widthOfColorCodeBar;
						int boxHeight = yValueToPlotY(colorCodeValues.get(colorCode), numericalTicks, cm) - yBoxStart;
						System.out.println("in v");
						drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, boxColor, colorCodeValues.get(colorCode), orientation);
					} else {
						int widthOfColorCodeBar = (int) ((((cm.getPlotHeight() / (numCategories)) * this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);
						int yPositionAtBarsStart = yCategoryNumToPlotY(categoryCount - 0.5f, numCategories, cm) + (int) (((1 - this.multipleBarWidthPercentage) / 2) * (cm.getPlotHeight() / (numCategories)));

						int xBoxStart = xValueToPlotX(0, numericalTicks, cm);
						int yBoxStart = yPositionAtBarsStart + ((widthOfColorCodeBar + this.multipleBarPixelSpacing) * colorCodeCount);
						int boxWidth = xValueToPlotX(colorCodeValues.get(colorCode), numericalTicks, cm) - xBoxStart;
						int boxHeight = widthOfColorCodeBar;
						
						drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, boxColor, colorCodeValues.get(colorCode), orientation);
					}
	
					colorCodeCount++;
				}
				categoryCount++;

			} else {
				
				Color boxColor;
				if (this.singleColor) {
					boxColor = this.barColor;
				} else {
					boxColor = this.colorPalette[categoryCount % this.colorPalette.length];
				}
				
				if (orientation == "v") {					
					int halfWidthOfSingularBar = (int) (this.singlularBarWidthPercentage * cm.getPlotWidth() / (2 * numCategories));
					int xBoxStart = xCategoryNumToPlotX(categoryCount, numCategories, cm) - halfWidthOfSingularBar;
					int yBoxStart = yValueToPlotY(0, numericalTicks, cm);
					int boxWidth = 2 * halfWidthOfSingularBar;
					int boxHeight = yValueToPlotY((double) data.get(category), numericalTicks, cm) - yValueToPlotY(0, numericalTicks, cm);
					
					drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, boxColor, (double) data.get(category), orientation);
				} else {
					int halfWidthOfSingularBar = (int) (this.singlularBarWidthPercentage * cm.getPlotHeight() / (2 * numCategories));
					int xBoxStart = xValueToPlotX(0, numericalTicks, cm);
					int yBoxStart = yCategoryNumToPlotY(categoryCount, numCategories, cm) - halfWidthOfSingularBar;
					int boxWidth = xValueToPlotX((double) data.get(category), numericalTicks, cm) - xBoxStart;
					int boxHeight = 2 * halfWidthOfSingularBar;
					
					drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, boxColor, (double) data.get(category), orientation);
				}
				categoryCount++;
				
			}
		}

	}

	private int xCategoryNumToPlotX(double xCategoryNum, int totalXCategories, XYChartMeasurements cm) {
		int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * totalXCategories));
		return CommonMath.map(xCategoryNum, 0, totalXCategories - 1, cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
				cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);
	}

	private int xValueToPlotX(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(),
				cm.imageLeftToPlotRightWidth());
	}

	private int yCategoryNumToPlotY(double yCategoryNum, int totalYCategories, XYChartMeasurements cm) {
		int halfHeightOfYUnit = (cm.getPlotHeight() / (2 * totalYCategories));
		return CommonMath.map(yCategoryNum, 0, totalYCategories - 1,
				cm.imageBottomToPlotBottomHeight() + halfHeightOfYUnit,
				cm.imageBottomToPlotTopHeight() - halfHeightOfYUnit);
	}

	private int yValueToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

	private void drawBar(Graphics2D g, int xBoxStart, int yBoxStart, int boxWidth, int boxHeight, Color barColor, double value, String orientation) {

		g.setColor(barColor);
		g.fillRect(xBoxStart, yBoxStart, boxWidth, boxHeight);

		if (this.drawBarOutline) {
			g.setStroke(new BasicStroke(this.barOutlineWidth));
			g.setColor(this.barOutlineColour);
			g.drawRect(xBoxStart, yBoxStart, boxWidth, boxHeight);
		}

		if (this.drawBarValue) {
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.CEILING);
			DrawString.setTextStyle(Color.BLACK, this.valuesFont, 0);
			if (orientation == "v") {				
				DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.TopAlign);
				DrawString.write(g, df.format(value), xBoxStart + boxWidth / 2, yBoxStart + boxHeight - 10);
			} else {
				DrawString.setAlignment(DrawString.xAlignment.RightAlign, DrawString.yAlignment.MiddleAlign);
				DrawString.write(g, df.format(value), xBoxStart + boxWidth - 10, yBoxStart + (boxHeight / 2));
			}
		}
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
		return singlularBarWidthPercentage;
	}

	public void setBarWidthPercentage(double barWidthPercentage) {
		this.singlularBarWidthPercentage = barWidthPercentage;
	}

	public Color getBarColor() {
		return this.barColor;
	}

	public void setBarColor(Color color) {
		this.barColor = color;
		this.singleColor = true;
	}

	public Color[] getColorPalette() {
		return this.colorPalette;
	}

	public void setColorPalette(Color[] palette) {
		this.colorPalette = palette;
		this.singleColor = false;
	}
}
