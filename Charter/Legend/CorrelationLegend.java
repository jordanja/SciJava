package thesis.Charter.Legend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;

public class CorrelationLegend extends Legend{

	private int colorBarWidth = 15;
	private int colorBarTicksWidth = 10;
	private int colorBarTicksToColorBarValuesWidth = 5;
	private int colorBarValuesWidth;
	private int colorBarValuesRightToLegendRight = 10;
	
	private Color bottomColor = new Color(249, 224, 195);
	private Color topColor = new Color(7, 8, 21);
	
	private Double[] axisValues;
	private Font axisFont = new Font("Dialog", Font.PLAIN, 12);
	private Color axisColor = Color.BLACK;
	
	private Color tickColor = Color.BLACK;
	private int tickWidth = 1;
	
	
	@Override
	public void calculateLegend() {
		this.includeLegend = true;
		String[] formattedAxisValues = formatAxisValuesForDisplay();
		this.colorBarValuesWidth = DrawString.maxWidthOfStringInList(formattedAxisValues, this.axisFont, 0);
	}

	private String[] formatAxisValuesForDisplay() {
		String[] formattedAxisValues = new String[this.axisValues.length];
		for (int axisValueCount = 0; axisValueCount < formattedAxisValues.length; axisValueCount++) {
			formattedAxisValues[axisValueCount] = DrawString.formatDoubleForDisplay(axisValues[axisValueCount]);
		}
		return formattedAxisValues;
	}

	@Override
	public void drawLegend(Graphics2D g, ChartMeasurements cm) {
		// TODO Auto-generated method stub
		
	}

	public int getLegendLeftToTicksRight() {
		return this.colorBarWidth + this.colorBarTicksWidth;
	}
	
	public int getLegendLeftToAxisValuesLeft() {
		return getLegendLeftToTicksRight() + this.colorBarTicksToColorBarValuesWidth;
	}
	
	public int getLegendLeftToRightmostAxisValueRight() {
		return this.getLegendLeftToAxisValuesLeft() + this.colorBarValuesWidth;
	}
	
	@Override
	public int getLegendWidth() {
		return getLegendLeftToRightmostAxisValueRight() + this.colorBarValuesRightToLegendRight;
	}

	
	public int getColorBarWidth() {
		return colorBarWidth;
	}

	public void setColorBarWidth(int colorBarWidth) {
		this.colorBarWidth = colorBarWidth;
	}

	public int getColorBarTicksWidth() {
		return colorBarTicksWidth;
	}

	public void setColorBarTicksWidth(int colorBarTicksWidth) {
		this.colorBarTicksWidth = colorBarTicksWidth;
	}

	public int getColorBarTicksToColorBarValuesWidth() {
		return colorBarTicksToColorBarValuesWidth;
	}

	public void setColorBarTicksToColorBarValuesWidth(int colorBarTicksToColorBarValuesWidth) {
		this.colorBarTicksToColorBarValuesWidth = colorBarTicksToColorBarValuesWidth;
	}

	public int getColorBarValuesRightToLegendRight() {
		return colorBarValuesRightToLegendRight;
	}

	public void setColorBarValuesRightToLegendRight(int colorBarValuesRightToLegendRight) {
		this.colorBarValuesRightToLegendRight = colorBarValuesRightToLegendRight;
	}

	public Color getBottomColor() {
		return bottomColor;
	}

	public void setBottomColor(Color bottomColor) {
		this.bottomColor = bottomColor;
	}

	public Color getTopColor() {
		return topColor;
	}

	public void setTopColor(Color topColor) {
		this.topColor = topColor;
	}

	public Double[] getAxisValues() {
		return axisValues;
	}

	public void setAxisValues(Double[] axisValues) {
		this.axisValues = axisValues;
	}

	public Font getAxisFont() {
		return axisFont;
	}

	public void setAxisFont(Font axisFont) {
		this.axisFont = axisFont;
	}

	public Color getAxisColor() {
		return axisColor;
	}

	public void setAxisColor(Color axisColor) {
		this.axisColor = axisColor;
	}

	public Color getTickColor() {
		return tickColor;
	}

	public void setTickColor(Color tickColor) {
		this.tickColor = tickColor;
	}

	public int getTickWidth() {
		return tickWidth;
	}

	public void setTickWidth(int tickWidth) {
		this.tickWidth = tickWidth;
	}

	
}
