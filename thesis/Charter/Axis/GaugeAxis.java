package thesis.Charter.Axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.OnlyPlotChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonArray;
import thesis.Common.CommonDouble;
import thesis.Common.NiceScale;

public class GaugeAxis {

	private String[] axisValuesString;
	private double[] axisValues;
	
	private Font axisFont = new Font("Dialog", Font.PLAIN, 12);
	private Color axisValueColor = Color.BLACK;
	
	
	private boolean drawTicks = true;
	private int tickLength = 10;
	private int tickWeight = 2;
	private Color tickColor = Color.BLACK;
	
	private int maxTicks = 5;
	
	private int edgeBuffer = 2;
	
	public void setAxis(double value) {
		NiceScale yNS = new NiceScale(0, 1);
		yNS.setMaxTicks(this.maxTicks);
		this.axisValuesString = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		this.axisValues = new double[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			double decimal = tickValue * 100;
			
			this.axisValuesString[i] = DrawString.formatDoubleForDisplay(decimal) + "%";
			this.axisValues[i] = tickValue;
		}
	}
	
	public void setAxis(double value, double min, double max) {
		NiceScale yNS = new NiceScale(min, max);
		yNS.setMaxTicks(this.maxTicks);
		this.axisValuesString = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		this.axisValues = new double[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.axisValuesString[i] = DrawString.formatDoubleForDisplay(tickValue);
			this.axisValues[i] = tickValue;
		}
	}

	public void drawAxis(Graphics2D g, OnlyPlotChartMeasurements cm) {
		int numAxisValues = axisValuesString.length;
		int fullRadius = cm.getPlotWidth()/2 - edgeBuffer;
		
		int xMid = cm.imageLeftToPlotMidWidth();
		int yMid = cm.imageBottomToPlotMidHeight();
		
		double currentAngle = Math.PI;
		double angleDelta = Math.PI/(numAxisValues - 1);
		
		for (int axisValueCount = 0; axisValueCount < numAxisValues; axisValueCount++) {
			int x = (int) (xMid + Math.cos(currentAngle) * fullRadius);
			int y = (int) (yMid + Math.sin(currentAngle) * fullRadius);
			
			String axisValue = this.axisValuesString[axisValueCount];
			
			g.setColor(this.axisValueColor);
			setTextAlignment(currentAngle);
			DrawString.setTextStyle(Color.black, this.axisFont, 0);
			DrawString.write(g, axisValue, x, y);
			
			currentAngle -= angleDelta;
		}
	}

	private void setTextAlignment(Double currentAngle) {
		if (currentAngle < 0) {
			currentAngle += 2 * Math.PI;
		}
		
		if (CommonDouble.approxEqual(currentAngle, Math.PI)) {
			// Left
			DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.MiddleAlign);
		} else if (CommonDouble.approxEqual(currentAngle, Math.PI/2)) {
			// Top
			DrawString.setAlignment(xAlignment.CenterAlign, yAlignment.BottomAlign);
		} else if (CommonDouble.approxEqual(currentAngle, 0)) {
			// Right
			DrawString.setAlignment(xAlignment.RightAlign, yAlignment.MiddleAlign);
		} else if ((currentAngle < Math.PI) && (currentAngle > Math.PI/2)) {
			// Top Left
			DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.TopAlign);
		} else if ((currentAngle < Math.PI/2) && (currentAngle > 0)) {
			// Top Right
			DrawString.setAlignment(xAlignment.RightAlign, yAlignment.TopAlign);
		}
	}

	public int getMaxTicks() {
		return maxTicks;
	}


	public void setMaxTicks(int maxTicks) {
		this.maxTicks = maxTicks;
	}


	public String[] getAxisStringValues() {
		return axisValuesString;
	}
	
	public double[] getAxisValues() {
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

	public int getEdgeBuffer() {
		return edgeBuffer;
	}

	public void setEdgeBuffer(int edgeBuffer) {
		this.edgeBuffer = edgeBuffer;
	}

	
	

}
