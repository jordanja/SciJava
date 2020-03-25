package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.StackedBarChartAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.CommonHashMap;
import thesis.Common.CommonMath;
import thesis.Helpers.Palette;

public class StackedBarPlot  extends Plot{

	private Color[] colorPalette = Palette.Default;
	
	private double barWidthPercentage = 0.5f;


	private boolean drawValuesOnBar = true;
	private Color valuesColor =  Color.WHITE;
	private Font valuesFont = new Font("Dialog", Font.PLAIN, 20);
	
	public void drawPlot(Graphics2D g, StackedBarChartAxis axis, HashMap<String, HashMap<String, HashMap<String, Double>>> data, String[] xDataOrder, String[] hueValuesOrder, XYChartMeasurements cm) {
		double[] yTicks = axis.getYTicksValues();
		
		int numXCatagories = data.keySet().size();
		int xCatagoryCount = 0;
		
		int halfWidthOfSingularBar = (int) (this.barWidthPercentage * cm.getPlotWidth() / (2 * numXCatagories));
		
		for (String xCatagory : xDataOrder) {
			int xBoxStart = xTickNumToPlotX(xCatagoryCount, data.keySet().size(), cm) - halfWidthOfSingularBar;
			int boxWidth = 2 * halfWidthOfSingularBar;
			drawBar(g, cm, xBoxStart, boxWidth , hueValuesOrder, data.get(xCatagory), yTicks);
			
			xCatagoryCount++;
		}
	}
	
	
	
	
	
	private void drawBar(Graphics2D g, XYChartMeasurements cm, int xBoxStart, int boxWidth, String[] hueValuesOrder, HashMap<String, HashMap<String, Double>> xCategoryMap, double[] yTicks) {
		double currentStartProportion = 0;
		int hueCount = 0;
		
		int bottomOfPlotHeight = yTickNumToPlotY(0, yTicks, cm);
		
		for (String hueValue: hueValuesOrder) {
			double proportion = xCategoryMap.get(hueValue).get("proportion");
			double value = xCategoryMap.get(hueValue).get("value");
			
			int yBoxStart = yTickNumToPlotY(currentStartProportion, yTicks, cm);
			int boxHeight = yTickNumToPlotY(proportion, yTicks, cm) - bottomOfPlotHeight;

			Color color = this.colorPalette[hueCount % this.colorPalette.length];
			
			drawHueBar(g, xBoxStart, yBoxStart, boxWidth, boxHeight, value, color, proportion, cm);
			
			currentStartProportion += proportion;
			hueCount++;
		}
	}

	private void drawHueBar(Graphics2D g, int xBoxStart, int yBoxStart, int boxWidth, int boxHeight, double value, Color color, double proportion, XYChartMeasurements cm) {
		g.setColor(color);
		g.fillRect(xBoxStart, yBoxStart, boxWidth, boxHeight);

		g.setFont(this.valuesFont);
		g.setColor(this.valuesColor);
		DrawString.write(g, DrawString.formatDoubleForDisplay(value), xBoxStart + boxWidth/2, yBoxStart + boxHeight/2, DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, 0, cm);
	}


	private int xTickNumToPlotX(double xTickNum, int totalxTicks, XYChartMeasurements cm) {
		int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * totalxTicks));
		return CommonMath.map(xTickNum, 0, totalxTicks - 1, cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
				cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);
	}

	private int yTickNumToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1],
				cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
	}


	public Color[] getColorPalette() {
		return colorPalette;
	}
	public void setColorPalette(Color[] colorPalette) {
		this.colorPalette = colorPalette;
	}


	public boolean isDrawValuesOnBar() {
		return drawValuesOnBar;
	}
	public void setDrawValuesOnBar(boolean drawValuesOnBar) {
		this.drawValuesOnBar = drawValuesOnBar;
	}

	public Color getValuesColor() {
		return valuesColor;
	}
	public void setValuesColor(Color valuesColor) {
		this.valuesColor = valuesColor;
	}

	public Font getValuesFont() {
		return valuesFont;
	}
	public void setValuesFont(Font valuesFont) {
		this.valuesFont = valuesFont;
	}


	public double getBarWidthPercentage() {
		return barWidthPercentage;
	}
	public void setBarWidthPercentage(double barWidthPercentage) {
		this.barWidthPercentage = barWidthPercentage;
	}

}
