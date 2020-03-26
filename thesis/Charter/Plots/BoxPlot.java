package thesis.Charter.Plots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BoxChartAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class BoxPlot extends Plot {

	private Color[] boxColorPalette = Palette.Default;

	private Color outlineColor = Color.black;
	private int barOutlineWidth = 3;

	private Color barColor = boxColorPalette[0];

	// When there are no categories, this is the width of a bar releive to the plot
	private double singluarBarWidthPercentage = 0.8f;

	// When there are categories but no color code bars, this is the width of a bar
	private double singlularClusterBarWidthPercentage = 0.5f;

	// When there are colorCode bars, this is the total width of all bars in a
	// cluster
	private double multipleBarWidthPercentage = 0.8f;
	// When there are colorCode bars, this is the number of pixels between bars in a
	// cluster
	private int multipleBarPixelSpacing = 0;

	public void drawPlot(Graphics2D g, BoxChartAxis axis, HashMap<Object, Object> data, String[] xDataOrdered,
			String typeOfData, XYChartMeasurements cm) {
		int numberOfXTicks = axis.getCategoricalTicks().length;

		if (typeOfData == "singleCatagory") {
			int widthOfbar = (int) (singluarBarWidthPercentage * cm.getPlotWidth());

			double[] yTicks = axis.getNumericTicksValues();

			double min = (Double) data.get("Min");
			double Q1 = (Double) data.get("Q1");
			double Q2 = (Double) data.get("Q2");
			double Q3 = (Double) data.get("Q3");
			double max = (Double) data.get("Max");

			int xBoxStart = xCategoryNumToPlotX(0, 1, cm) - (int) (0.5f * widthOfbar);
			int yBoxStart = yTickNumToPlotY(Q1, yTicks, cm);
			int boxWidth = widthOfbar;
			int boxHeight = yTickNumToPlotY(Q3, yTicks, cm) - yBoxStart;

			int xCenterOfBox = xCategoryNumToPlotX(0, 1, cm);

			int yMin = yTickNumToPlotY(min, yTicks, cm);
			int yMax = yTickNumToPlotY(max, yTicks, cm);
			int yMedian = yTickNumToPlotY(Q2, yTicks, cm);
			drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, xCenterOfBox, yMin, yMax, yMedian, this.barColor,
					this.outlineColor);

		} else if (typeOfData == "multipleCatagoriesAndNoHueValue") {
			int widthOfbar = (int) (this.singlularClusterBarWidthPercentage * cm.getPlotWidth() / numberOfXTicks);
			int xCatagoryCount = 0;
			for (String xCategory : xDataOrdered) {
				Color fillColor = this.boxColorPalette[xCatagoryCount % this.boxColorPalette.length];
				HashMap<Object, Double> map = (HashMap<Object, Double>) data.get(xCategory);

				double[] yTicks = axis.getNumericTicksValues();

				double min = map.get("Min");
				double Q1 = map.get("Q1");
				double Q2 = map.get("Q2");
				double Q3 = map.get("Q3");
				double max = map.get("Max");

				int xBoxStart = xCategoryNumToPlotX(xCatagoryCount, numberOfXTicks, cm) - (int) (0.5f * widthOfbar);
				int yBoxStart = yTickNumToPlotY(Q1, yTicks, cm);
				int boxWidth = widthOfbar;
				int boxHeight = yTickNumToPlotY(Q3, yTicks, cm) - yBoxStart;

				int xCenterOfBox = xCategoryNumToPlotX(xCatagoryCount, numberOfXTicks, cm);

				int yMin = yTickNumToPlotY(min, yTicks, cm);
				int yMax = yTickNumToPlotY(max, yTicks, cm);
				int yMedian = yTickNumToPlotY(Q2, yTicks, cm);
				drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, xCenterOfBox, yMin, yMax, yMedian, fillColor,
						this.outlineColor);

				xCatagoryCount++;
			}
		} else if (typeOfData == "multipleCatagoriesAndHueValue") {
			int xCatagoryCount = 0;
			for (String xCategory : xDataOrdered) {
				HashMap<Object, HashMap<Object, Double>> category = (HashMap<Object, HashMap<Object, Double>>) data.get(xCategory);

				int numColorCodeValues = category.keySet().size();
				int totalSpaceInbetweenBars = (numColorCodeValues - 1) * this.multipleBarPixelSpacing;
				int widthOfColorCodeBar = (int) ((((cm.getPlotWidth() / (numberOfXTicks))
						* this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);

				int positionAtBarsStart = xCategoryNumToPlotX(xCatagoryCount - 0.5f, data.keySet().size(), cm)
						+ (int) (((1 - this.multipleBarWidthPercentage) / 2) * (cm.getPlotWidth() / (numberOfXTicks)));

				int colorCodeCount = 0;
				for (Object colorCode : category.keySet()) {
					Color fillColor = this.boxColorPalette[colorCodeCount % this.boxColorPalette.length];

					HashMap<Object, Double> map = category.get(colorCode);

					double[] yTicks = axis.getNumericTicksValues();

					double min = map.get("Min");
					double Q1 = map.get("Q1");
					double Q2 = map.get("Q2");
					double Q3 = map.get("Q3");
					double max = map.get("Max");
//					System.out.println("min: " + min + "\n" + "Q1: " + Q1 + "\n" + "Q2: " + Q2 + "\n" + "Q3: " + Q3 + "\n" + "max: " + max);

					int xBoxStart = positionAtBarsStart
							+ ((widthOfColorCodeBar + multipleBarPixelSpacing) * colorCodeCount);
					int yBoxStart = yTickNumToPlotY(Q1, yTicks, cm);
					int boxWidth = widthOfColorCodeBar;
					int boxHeight = yTickNumToPlotY(Q3, yTicks, cm) - yBoxStart;

					int xCenterOfBox = xBoxStart + boxWidth / 2;

					int yMin = yTickNumToPlotY(min, yTicks, cm);
					int yMax = yTickNumToPlotY(max, yTicks, cm);
					int yMedian = yTickNumToPlotY(Q2, yTicks, cm);

					drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, xCenterOfBox, yMin, yMax, yMedian, fillColor,
							this.outlineColor);

					colorCodeCount++;
				}

				xCatagoryCount++;
			}
		}

	}

	private void drawBar(Graphics2D g, int xBoxStart, int yBoxStart, int boxWidth, int boxHeight, int xCenterOfBox,
			int yMin, int yMax, int yMedian, Color fillColor, Color outlineColor) {
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

	private int xCategoryNumToPlotX(double xCatagoryNum, int totalXCategories, XYChartMeasurements cm) {
		int widthOfXUnit = (cm.getPlotWidth() / (totalXCategories));

		return (int) ((xCatagoryNum) * widthOfXUnit) + (int) (0.5f * widthOfXUnit) + cm.imageLeftToPlotLeftWidth();
	}

	private int yTickNumToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

	public Color[] getBoxColorPalette() {
		return this.boxColorPalette;
	}

	public void setBoxColorPalette(Color[] boxColorPalette) {
		this.boxColorPalette = boxColorPalette;
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

	public double getSingluarBarWidthPercentage() {
		return singluarBarWidthPercentage;
	}

	public void setSingluarBarWidthPercentage(double singluarBarWidthPercentage) {
		this.singluarBarWidthPercentage = singluarBarWidthPercentage;
	}

	public double getSinglularClusterBarWidthPercentage() {
		return singlularClusterBarWidthPercentage;
	}

	public void setSinglularClusterBarWidthPercentage(double singlularClusterBarWidthPercentage) {
		this.singlularClusterBarWidthPercentage = singlularClusterBarWidthPercentage;
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

}
