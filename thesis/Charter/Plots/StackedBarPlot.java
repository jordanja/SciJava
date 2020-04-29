package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.StackedBarChartAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
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
	
	public void drawPlot(Graphics2D g, StackedBarChartAxis axis, HashMap<String, HashMap<String, HashMap<String, Double>>> data, String[] xDataOrder, String[] hueValuesOrder, String orientation, XYChartMeasurements cm) {
		double[] numericTicks = axis.getNumericTicksValues();
		
		int numCatagories = data.keySet().size();
		
		double halfCategoryWidth = (orientation == "v" ? cm.getPlotWidth() : cm.getPlotHeight()) / (2 * numCatagories);
		
		int halfWidthOfSingularBar = (int) (this.barWidthPercentage * halfCategoryWidth);
		int catagoryCount = 0;		
		for (String xCatagory : xDataOrder) {
			
			int categoryOffset; 
			if (orientation == "v") {				
				categoryOffset = xCategoryNumToPlotX(catagoryCount, numCatagories, cm) - halfWidthOfSingularBar;
			} else {
				categoryOffset = yCategoryNumToPlotY(catagoryCount, numCatagories, cm) - halfWidthOfSingularBar;
			}
			int boxWidth = 2 * halfWidthOfSingularBar;
			drawBar(g, cm, categoryOffset, boxWidth , hueValuesOrder, data.get(xCatagory), numericTicks, orientation);
			
			catagoryCount++;
		}
	}
	
	
	
	
	
	private void drawBar(Graphics2D g, XYChartMeasurements cm, int categoryOffset, int boxLength, String[] hueValuesOrder, HashMap<String, HashMap<String, Double>> categoryMap, double[] numericTicks, String orientation) {
		double currentStartProportion = 0;
		int hueCount = 0;
		
		int bottomOfPlotHeight = yValueToPlotY(0, numericTicks, cm);
		int leftOfPlotWidth = xValueToPlotX(0, numericTicks, cm);
		
		for (String hueValue: hueValuesOrder) {
			double proportion = categoryMap.get(hueValue).get("proportion");
			double value = categoryMap.get(hueValue).get("value");
			Color color = this.colorPalette[hueCount % this.colorPalette.length];

			if (orientation == "v") {				
				int yBoxStart = yValueToPlotY(currentStartProportion, numericTicks, cm);
				int boxHeight = yValueToPlotY(proportion, numericTicks, cm) - bottomOfPlotHeight;
				drawHueBar(g, categoryOffset, yBoxStart, boxLength, boxHeight, value, color, proportion, cm);
			} else {
				int xBoxStart = xValueToPlotX(currentStartProportion, numericTicks, cm);
				int boxWidth = xValueToPlotX(proportion, numericTicks, cm) - leftOfPlotWidth;
				drawHueBar(g, xBoxStart, categoryOffset, boxWidth, boxLength, value, color, proportion, cm);
			}

			
			
			currentStartProportion += proportion;
			hueCount++;
		}
	}

	private void drawHueBar(Graphics2D g, int xBoxStart, int yBoxStart, int boxWidth, int boxHeight, double value, Color color, double proportion, XYChartMeasurements cm) {
		g.setColor(color);
		g.fillRect(xBoxStart, yBoxStart, boxWidth, boxHeight);
		
		if (this.drawValuesOnBar) {			
			DrawString.setTextStyle(this.valuesColor, this.valuesFont, 0);
			DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
			DrawString.write(g, DrawString.formatDoubleForDisplay(value), xBoxStart + boxWidth/2, yBoxStart + boxHeight/2);
		}
	}
	
	

	private int xCategoryNumToPlotX(double xCategoryNum, int totalXCategories, XYChartMeasurements cm) {
		int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * totalXCategories));
		return CommonMath.map(xCategoryNum, 0, totalXCategories - 1, cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
				cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);
	}
	private int xValueToPlotX(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1],
				cm.imageLeftToPlotLeftWidth(), cm.imageLeftToPlotRightWidth());
	}

	private int yCategoryNumToPlotY(double yCategoryNum, int totalYCategories, XYChartMeasurements cm) {
		int halfHeightOfYUnit = (cm.getPlotHeight() / (2 * totalYCategories));
		return CommonMath.map(yCategoryNum, 0, totalYCategories - 1, cm.imageBottomToPlotBottomHeight() + halfHeightOfYUnit,
				cm.imageBottomToPlotTopHeight() - halfHeightOfYUnit);
	}
	private int yValueToPlotY(double yPos, double[] yTicks, XYChartMeasurements cm) {
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
