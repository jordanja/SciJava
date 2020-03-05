package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Auxiliary.MathHelpers;
import thesis.Auxiliary.NiceScale;
import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public class BarPlot extends Plot {

	private Color[] barColorPalette = Palette.Fire;
	private boolean drawBarOutline = false;
	private Color barOutlineColour = Color.black;
	private int barOutlineWidth = 1;

	// When there are no color code bars, this is the width of a bar
	private double singlularBarWidthPercentage = 0.5f;

	// When there are colorCode bars, this is the total width of all bars in a
	// cluster
	private double multipleBarWidthPercentage = 0.8f;
	// When there are colorCode bars, this is the number of pixels between bars in a
	// cluster
	private int multipleBarPixelSpacing = 10;

	private Color barColor = barColorPalette[0];

	private String[] order = {};

	boolean singleColor = true;

	boolean drawBarValue = true;

	public void drawPlot(Graphics2D g, BarChartAxis axis, HashMap<String, Object> data, XYChartMeasurements cm) {
		// Are there color code values
		boolean haveColorCodeValues = (data.get(data.keySet().iterator().next()) instanceof HashMap);
		
		double[] yTicks = Arrays.stream(axis.getyTicks()).mapToDouble(Double::parseDouble).toArray();

		NiceScale yNS = axis.getyNS();
		
		int numXCatagories = data.keySet().size();
		int xCatagoryCount = 0;
		
		if (haveColorCodeValues) {
		
			for (String xCatagory : data.keySet()) {

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
					int yBoxStart = yTickNumToPlotY(0, yNS, yTicks, cm);
					int boxWidth = widthOfColorCodeBar;
					int boxHeight = yTickNumToPlotY((double) colorCodeValues.get(colorCode), yNS, yTicks, cm)
							- yTickNumToPlotY(0, yNS, yTicks, cm);
					
					drawBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, boxColor, (double) colorCodeValues.get(colorCode), cm);

					colorCodeCount++;
				}
				xCatagoryCount++;

			}
		} else {
			int halfWidthOfSingularBar = (int) (this.singlularBarWidthPercentage * cm.getPlotWidth()
					/ (2 * numXCatagories));
			for (String xCatagory : data.keySet()) {
				Color boxColor;
				if (this.singleColor) {
					boxColor = this.barColor;
				} else {
					boxColor = this.barColorPalette[xCatagoryCount % this.barColorPalette.length];
				}
				int xBoxStart = xTickNumToPlotX(xCatagoryCount, data.keySet().size(), cm) - halfWidthOfSingularBar;
				int yBoxStart = yTickNumToPlotY(0, yNS, yTicks, cm);
				int boxWidth = 2 * halfWidthOfSingularBar;
				int boxHeight = yTickNumToPlotY((double) data.get(xCatagory), yNS, yTicks, cm)
						- yTickNumToPlotY(0, yNS, yTicks, cm);

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

	private int yTickNumToPlotY(double yPos, NiceScale yNS, double[] yTicks, XYChartMeasurements cm) {
		return (int) MathHelpers.map(yPos, yNS.getNiceMin(), yTicks[yTicks.length - 1],
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
			DrawString.drawString(g, df.format(heightValue), xBoxStart + boxWidth / 2,
					yBoxStart + boxHeight - 10, xAlignment.CenterAlign, yAlignment.TopAlign, 0, cm);
		}
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
		return singlularBarWidthPercentage;
	}

	public void setBarWidthPercentage(double barWidthPercentage) {
		this.singlularBarWidthPercentage = barWidthPercentage;
	}

	public String[] getOrder() {
		return this.order;
	}

	public void setOrder(String[] order) {
		this.order = order;
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
