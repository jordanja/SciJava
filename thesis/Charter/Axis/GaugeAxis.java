package thesis.Charter.Axis;

import java.awt.Color;
import java.awt.Font;

import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.NiceScale;

public class GaugeAxis {

	private String[] axisValues;
	private Font axisFont = new Font("Dialog", Font.PLAIN, 12);
	private Color axisValueColor = Color.BLACK;
	private int axisSpacingFromPlot = 8;
	
	private boolean drawTicks = true;
	private int tickLength = 10;
	private int tickWeight = 2;
	private Color tickColor = Color.BLACK;
	
	private int maxTicks = 5;
	
	public void setAxis(double value, double min, double max) {
		NiceScale yNS = new NiceScale(min, max);
		yNS.setMaxTicks(this.maxTicks);
		this.axisValues = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.axisValues[i] = DrawString.formatDoubleForDisplay(tickValue);
		}
	}


	public int getMaxTicks() {
		return maxTicks;
	}


	public void setMaxTicks(int maxTicks) {
		this.maxTicks = maxTicks;
	}


	public String[] getAxisValues() {
		return axisValues;
	}


	public Font getAxisFont() {
		return axisFont;
	}


	public void setAxisFont(Font axisFont) {
		this.axisFont = axisFont;
	}


	public Color getAxisValueColor() {
		return axisValueColor;
	}


	public void setAxisValueColor(Color axisValueColor) {
		this.axisValueColor = axisValueColor;
	}


	public boolean isDrawTicks() {
		return drawTicks;
	}


	public void setDrawTicks(boolean drawTicks) {
		this.drawTicks = drawTicks;
	}


	public int getTickLength() {
		return tickLength;
	}


	public void setTickLength(int tickLength) {
		this.tickLength = tickLength;
	}


	public int getTickWeight() {
		return tickWeight;
	}


	public void setTickWeight(int tickWeight) {
		this.tickWeight = tickWeight;
	}


	public Color getTickColor() {
		return tickColor;
	}


	public void setTickColor(Color tickColor) {
		this.tickColor = tickColor;
	}


	public int getAxisSpacingFromPlot() {
		return axisSpacingFromPlot;
	}


	public void setAxisSpacingFromPlot(int axisSpacingFromPlot) {
		this.axisSpacingFromPlot = axisSpacingFromPlot;
	}
	
	

}
