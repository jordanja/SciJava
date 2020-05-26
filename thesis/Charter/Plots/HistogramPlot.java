package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;

public class HistogramPlot extends Plot{

	private Color barColor = new Color(155,175,206);
	
	private Color barOutlineColor = new Color(255, 255, 255);
	private int barOutlineWidth = 1;
	private boolean drawBarOutline = true;
	
	private boolean drawRugLines = false;
	private Color rugLineColor = new Color(60, 93, 160);
	private int rugLineWidth = 1;
	private int rugLineHeight = 10;
	
	public void drawPlot(Graphics2D g, NumericAxis axis, int[] binCount, double binSize, Double[] values, XYChartMeasurements cm) {
		double[] yTicks = axis.getYTicksValues();
		double[] xTicks = axis.getXTicksValues();

		int bottomY = binCountToYPixel(0, yTicks, cm);
		for (int i = 0; i < binCount.length; i++) {			
			int leftX = binNumToXPixel(i, binCount.length, cm);
			int rightX = binNumToXPixel(i + 1, binCount.length, cm);
			int topY = binCountToYPixel(binCount[i], yTicks, cm);
			int width = rightX - leftX;
			int height = topY - bottomY;
			
			g.setColor(this.barColor);
			g.fillRect(leftX, bottomY, width, height);
			
			if (this.drawBarOutline) {				
				g.setStroke(new BasicStroke(this.barOutlineWidth));
				g.setColor(this.barOutlineColor);
				g.drawRect(leftX, bottomY, width, height);
			}
			
		}
		
		if (this.drawRugLines) {
			for (int valueCount = 0; valueCount < values.length; valueCount++) {			
				g.setColor(this.rugLineColor);
				g.setStroke(new BasicStroke(this.rugLineWidth));
				int rugX = xValueToXPixel(values[valueCount], xTicks, cm);
				int rugBottomY = cm.imageBottomToPlotBottomHeight() + 1;
				int rugTopY = rugBottomY + this.rugLineHeight;
				g.drawLine(rugX, rugBottomY, rugX, rugTopY);	
			}
		}
		
	}
	public static int diffValues(Double[] numArray){
	    int numOfDifferentVals = 0;

	    ArrayList<Double> diffNum = new ArrayList<>();

	    for(int i=0; i<numArray.length; i++){
	        if(!diffNum.contains(numArray[i])){
	            diffNum.add(numArray[i]);
	        }
	    }

	    if(diffNum.size()==1){
	            numOfDifferentVals = 0;
	    }
	    else{
	          numOfDifferentVals = diffNum.size();
	        } 

	   return numOfDifferentVals;
	}
	private int xValueToXPixel(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(),
				cm.imageLeftToPlotRightWidth());
	}
	
	private int binNumToXPixel(double binNum, int numBins, XYChartMeasurements cm) {
		return (int)(cm.imageLeftToPlotLeftWidth() + binNum * (cm.getPlotWidth()/numBins));
	}
	
	public int binCountToYPixel(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

	public Color getBarColor() {
		return barColor;
	}

	public void setBarColor(Color barColor) {
		this.barColor = barColor;
	}

	public Color getBarOutlineColor() {
		return barOutlineColor;
	}

	public void setBarOutlineColor(Color barOutlineColor) {
		this.barOutlineColor = barOutlineColor;
	}

	public int getBarOutlineWidth() {
		return barOutlineWidth;
	}

	public void setBarOutlineWidth(int barOutlineWidth) {
		this.barOutlineWidth = barOutlineWidth;
	}

	public boolean isDrawBarOutline() {
		return drawBarOutline;
	}

	public void setDrawBarOutline(boolean drawBarOutline) {
		this.drawBarOutline = drawBarOutline;
	}

	public boolean isDrawRugLines() {
		return drawRugLines;
	}

	public void setDrawRugLines(boolean drawRugLines) {
		this.drawRugLines = drawRugLines;
	}

	public Color getRugLineColor() {
		return rugLineColor;
	}

	public void setRugLineColor(Color rugLineColor) {
		this.rugLineColor = rugLineColor;
	}

	public int getRugLineWidth() {
		return rugLineWidth;
	}

	public void setRugLineWidth(int rugLineWidth) {
		this.rugLineWidth = rugLineWidth;
	}

	public int getRugLineHeight() {
		return rugLineHeight;
	}

	public void setRugLineHeight(int rugLineHeight) {
		this.rugLineHeight = rugLineHeight;
	}

	
	
}
