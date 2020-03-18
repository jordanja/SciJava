package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.MathHelpers;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class BarPlot extends Plot {

	private Color[] barColorPalette = Palette.Fire;
	private boolean drawBarOutline = false;
	private Color barOutlineColour = Color.black;
	private int barOutlineWidth = 1;

	private Font valuesFont = new Font("Dialog", Font.PLAIN, 12);

	
	// When there are no color code bars, this is the width of a bar
	private double singlularBarWidthPercentage = 0.5f;

	// When there are colorCode bars, this is the total width of all bars in a cluster
	private double multipleBarWidthPercentage = 0.8f;
	// When there are colorCode bars, this is the number of pixels between bars in a cluster
	private int multipleBarPixelSpacing = 10;

	private Color barColor = barColorPalette[0];

	boolean singleColor = true;

	boolean drawBarValue = true;

	public void drawPlot(Graphics2D g, BarChartAxis axis, HashMap<String, Object> data, String[] xDataOrdered, XYChartMeasurements cm) {
		// Are there color code values
		boolean haveColorCodeValues = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		double[] yTicks = axis.getYTicksValues();

		
		int numXCatagories = data.keySet().size();
		int xCatagoryCount = 0;
		
		if (haveColorCodeValues) {
		
			for (String xCatagory : xDataOrdered) {

				HashMap<String, Double> colorCodeValues = (HashMap<String, Double>) data.get(xCatagory);

				int numColorCodeValues = colorCodeValues.keySet().size();
				int totalSpaceInbetweenBars = (numColorCodeValues - 1) * this.multipleBarPixelSpacing;
				int widthOfColorCodeBar = (int) ((((cm.getPlotWidth() / (numXCatagories))
						* this.multipleBarWidthPercentage) - totalSpaceInbetweenBars) / numColorCodeValues);

				int positionAtBarsStart = xTickNumToPlotX(xCatagoryCount - 0.5f, data.keySet().size(), cm)
						+ (int) (((1 - this.multipleBarWidthPercentage) / 2) * (cm.getPlotWidth() / (numXCatagories)));

				int colorCodeCount = 0;
				for (String colorCode : colorCodeValues.keySet()) {
					Color boxColor = this.barColorPalette[colorCodeCount % this.barColorPalette.length];
					
					int xBoxStart = positionAtBarsStart
							+ ((widthOfColorCodeBar + multipleBarPixelSpacing) * colorCodeCount);
					int yBoxStart = yTickNumToPlotY(0, yTicks, cm);
					int boxWidth = widthOfColorCodeBar;
					int boxHeight = yTickNumToPlotY(colorCodeValues.get(colorCode), yTicks, cm)
							- yTickNumToPlotY(0, yTicks, cm);
					
					drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, boxColor, (double) colorCodeValues.get(colorCode), cm);

					colorCodeCount++;
				}
				xCatagoryCount++;

			}
		} else {
			int halfWidthOfSingularBar = (int) (this.singlularBarWidthPercentage * cm.getPlotWidth()
					/ (2 * numXCatagories));
			for (String xCatagory : xDataOrdered) {
				Color boxColor;
				if (this.singleColor) {
					boxColor = this.barColor;
				} else {
					boxColor = this.barColorPalette[xCatagoryCount % this.barColorPalette.length];
				}
				int xBoxStart = xTickNumToPlotX(xCatagoryCount, data.keySet().size(), cm) - halfWidthOfSingularBar;
				int yBoxStart = yTickNumToPlotY(0, yTicks, cm);
				int boxWidth = 2 * halfWidthOfSingularBar;
				int boxHeight = yTickNumToPlotY((double) data.get(xCatagory), yTicks, cm)
						- yTickNumToPlotY(0, yTicks, cm);

				drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, boxColor, (double) data.get(xCatagory), cm);
				xCatagoryCount++;
			}
		}

	}

	private int xTickNumToPlotX(double xTickNum, int totalxTicks, XYChartMeasurements cm) {
		int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * totalxTicks));
		return (int) MathHelpers.map(xTickNum, 0, totalxTicks - 1, cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
				cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);
	}

	private int yTickNumToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(yPos, yTicks[0], yTicks[yTicks.length - 1],
				cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
	}

	private void drawBar(Graphics2D g, int xBoxStart, int yBoxStart, int boxWidth, int boxHeight, Color barColor, double heightValue, XYChartMeasurements cm) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		
		g.setColor(barColor);
		g.fillRect(xBoxStart, yBoxStart, boxWidth, boxHeight);

		if (this.drawBarOutline) {
			g.setStroke(new BasicStroke(this.barOutlineWidth));
			g.setColor(this.barOutlineColour);
			g.drawRect(xBoxStart, yBoxStart, boxWidth, boxHeight);
		}

		if (this.drawBarValue) {
			g.setColor(Color.black);
			g.setFont(this.valuesFont);
			DrawString.drawString(g, df.format(heightValue), xBoxStart + boxWidth / 2,
					yBoxStart + boxHeight - 10, xAlignment.CenterAlign, yAlignment.TopAlign, 0, cm);
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

	public Color[] getBarColorPalette() {
		return this.barColorPalette;
	}

	public void setBarColorPalette(Color[] palette) {
		this.barColorPalette = palette;
		this.singleColor = false;
	}
}
