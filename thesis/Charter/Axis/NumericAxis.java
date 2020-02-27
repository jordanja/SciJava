package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import thesis.Auxiliary.MathHelpers;
import thesis.Auxiliary.NiceScale;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;

public class NumericAxis extends XYAxis {

	private boolean includeZeroXAxis;
	private boolean includeZeroYAxis;
	
	private double smallestX;
	private double largestX;	
	private double smallestY;
	private double largestY;	
	
	private NiceScale xNS;
	private NiceScale yNS;
	
	
	
	
	public NiceScale getxNS() {
		return xNS;
	}

	public NiceScale getyNS() {
		return yNS;
	}

	private boolean includeAxisLinesOnPlot = true;
	private Color axisLinesOnPlotColor = Color.WHITE;
	
	

	public void includeAxisLinesOnPlot(boolean includeAxisLinesOnPlot) {
		this.includeAxisLinesOnPlot = includeAxisLinesOnPlot;
	}

	public void setAxisLinesOnPlotColor(Color axisLinesOnPlotColor) {
		this.axisLinesOnPlotColor = axisLinesOnPlotColor;
	}
	

	public NumericAxis() {
		super();
		
	}
	
	public void drawAxis(Graphics2D g, XYChartMeasurements cm) {
		
		double[] doubleXTicks = Arrays.stream(xTicks)
                .mapToDouble(Double::parseDouble)
                .toArray();
		double[] doubleYTicks = Arrays.stream(yTicks)
                .mapToDouble(Double::parseDouble)
                .toArray();
		
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_DOWN);

		for (int count = 1; count < this.xTicks.length - 1; count++) {

			int position = (int) MathHelpers.map(count, 0, xTicks.length - 1, cm.imageLeftToPlotLeftWidth(), cm.imageLeftToPlotRightWidth());
			String stringToDisplay = String.valueOf(df.format(doubleXTicks[count]));
			g.setColor(this.xAxisColor);
			g.setFont(this.xAxisFont);
			if (this.drawBottomXAxisValues) {				
				DrawString.drawString(g, stringToDisplay, position, cm.imageBottomToBottomAxisMidHeight(), DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.xAxisRotation, cm);
			}
			if (this.drawTopXAxisValues) {
				DrawString.drawString(g, stringToDisplay, position, cm.imageBottomToTopAxisMidHeight(), DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.xAxisRotation, cm);
			}
			
			if (this.includeAxisLinesOnPlot) {				
				g.setColor(this.axisLinesOnPlotColor);
				g.drawLine(position,cm.imageBottomToPlotBottomHeight(),position,cm.imageBottomToPlotTopHeight());
			}
			
		}
		
		
		for (int count = 1; count < this.yTicks.length - 1; count++) {
			int position = (int) MathHelpers.map(count, 0, this.yTicks.length - 1, cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
			String stringToDisplay = String.valueOf(df.format(doubleYTicks[count]));
			
			g.setColor(this.yAxisColor);
			g.setFont(this.yAxisFont);
			
			if (this.drawLeftYAxisValues) {				
				DrawString.drawString(g, stringToDisplay, cm.imageLeftToLeftAxisMidWidth(), position, DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.yAxisRotation, cm);
			}
			if (this.drawRightYAxisValues) {
				DrawString.drawString(g, stringToDisplay, cm.imageLeftToRightAxisMidWidth(), position, DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.yAxisRotation, cm);
			}
			
			if (this.includeAxisLinesOnPlot) {				
				g.setColor(this.axisLinesOnPlotColor);
				g.drawLine(cm.imageLeftToPlotLeftWidth(),position,cm.imageLeftToPlotRightWidth(),position);
			}


		}
		
		
	}
	
	public void drawAxisTicks(Graphics2D g, XYChartMeasurements cm) {
		
//		g.setStroke(new BasicStroke(1));
		for (int count = 0; count < this.xTicks.length; count++) {
			int position = (int) MathHelpers.map(count, 0, xTicks.length - 1, cm.imageLeftToPlotLeftWidth(), cm.imageLeftToPlotRightWidth());
			
			g.setColor(this.bottomTickColor);
			if (this.drawExteriorBottomXAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorBottomTickThickness));
				g.drawLine(position, cm.imageBottomToPlotBottomHeight(), position, cm.imageBottomToBottomTicksEndHeight());
			}
			if (this.drawInteriorBottomXAxisTicks) {	
				g.setStroke(new BasicStroke(this.interiorBottomTickThickness));
				g.drawLine(position, cm.imageBottomToPlotBottomHeight(), position, cm.imageBottomToPlotBottomHeight() + cm.getBottomTicksHeight());
			}
			g.setColor(this.topTickColor);
			if (this.drawExteriorTopXAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorTopTickThickness));
				g.drawLine(position, cm.imageBottomToPlotTopHeight(), position, cm.imageBottomToTopTicksEndHeight());
			}
			if (this.drawInteriorTopXAxisTicks) {
				g.setStroke(new BasicStroke(this.interiorTopTickThickness));
				g.drawLine(position, cm.imageBottomToPlotTopHeight(), position, cm.imageBottomToPlotTopHeight() - cm.getTopTicksHeight());
			}
			
		}
		
		for (int count = 0; count < this.yTicks.length; count++) {
			int position = (int) MathHelpers.map(count, 0, this.yTicks.length - 1, cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
			
			g.setColor(this.leftTickColor);
			if (this.drawExteriorLeftYAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorLeftTickThickness));
				g.drawLine(cm.imageLeftToPlotLeftWidth(), position, cm.imageLeftToLeftTicksEndWidth(), position);
			}
			if (this.drawInteriorLeftYAxisTicks) {
				g.setStroke(new BasicStroke(this.interiorLeftTickThickness));
				g.drawLine(cm.imageLeftToPlotLeftWidth(), position, cm.imageLeftToPlotLeftWidth() + cm.getLeftTicksWidth(), position);
			}
			g.setColor(this.rightTickColor);
			if (this.drawExteriorRightYAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorRightTickThickness));
				g.drawLine(cm.imageLeftToPlotRightWidth(), position, cm.imageLeftToRightTicksEndWidth(), position);
			}
			if (this.drawInteriorRightYAxisTicks) {
				g.setStroke(new BasicStroke(this.interiorRightTickThickness));
				g.drawLine(cm.imageLeftToPlotRightWidth(), position, cm.imageLeftToPlotRightWidth() - cm.getRightTicksWidth(), position);
			}

		}
	}
	
	
	
	public void drawXAxisLabel(Graphics2D g, XYChartMeasurements cm) {
		
		g.setColor(this.xAxisLabelColor);
		g.setFont(this.xAxisLabelFont);
		
		if (this.xAxisLabel != null ) {			
			if (this.drawBottomXLabel ) {			
				DrawString.drawString(g, this.xAxisLabel, cm.imageLeftToPlotMidWidth(), cm.imageBottomToBottomAxisLabelMidHeight(), DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, 0, cm);
			}
			if (this.drawTopXLabel) {
				DrawString.drawString(g, this.xAxisLabel, cm.imageLeftToPlotMidWidth(), cm.imageBottomToTopAxisLabelMidHeight(), DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, 0, cm);
			}
		}
		
	}
	
	public void drawYAxisLabel(Graphics2D g, XYChartMeasurements cm) {
		g.setColor(this.yAxisLabelColor);
		g.setFont(this.yAxisLabelFont);
		
		if (this.yAxisLabel != null) {
			if (this.drawLeftYLabel) {
				DrawString.drawString(g, this.yAxisLabel, cm.imageLeftToLeftAxisLabelMidWidth(), cm.imageBottomToPlotMidHeight(), DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, -90, cm);
			}
			if (this.drawRightYLabel) {
				DrawString.drawString(g, this.yAxisLabel, cm.imageLeftToRightAxisLabelMidWidth(), cm.imageBottomToPlotMidHeight(), DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, -90, cm);
			}
		}
	}
	
	
	
	
	

	public void calculateXAxis(Double minimum, Double maximum) {

		
		this.smallestX = minimum;
		this.largestX = maximum;
		
		if (this.includeZeroXAxis) {
			xNS = new NiceScale(Math.min(0, minimum), largestX);
		} else {
			xNS = new NiceScale(smallestX, largestX);
		}
		
		
		super.xTicks = new String[(int)Math.ceil((((largestX - xNS.getNiceMin()) + xNS.getTickSpacing())/xNS.getTickSpacing()) + 1)];
		int count = 0;

		for (double xTickNum = xNS.getNiceMin(); count < this.xTicks.length; xTickNum += xNS.getTickSpacing()) {

			this.xTicks[count] = String.valueOf(xTickNum);
			count++;
		}
		
		
	}



	public void calculateYAxis(Double minimum, Double maximum) {
		
		this.smallestY = minimum;
		this.largestY = maximum;
		
		if (this.includeZeroYAxis) {
			yNS = new NiceScale(Math.min(0, minimum), largestY);
		} else {
			yNS = new NiceScale(smallestY, largestY);
		}
		
		super.yTicks = new String[(int)Math.ceil((((largestY - yNS.getNiceMin()) + yNS.getTickSpacing())/yNS.getTickSpacing()) + 1)];
		int count = 0;
		for (double yTickNum = yNS.getNiceMin(); count < this.yTicks.length; yTickNum += yNS.getTickSpacing()) {
			this.yTicks[count] = String.valueOf(yTickNum);
			count++;
			
		}
	}

	public boolean isIncludeZeroXAxis() {
		return includeZeroXAxis;
	}

	public void setIncludeZeroXAxis(boolean includeZeroXAxis) {
		this.includeZeroXAxis = includeZeroXAxis;
	}

	public boolean isIncludeZeroYAxis() {
		return includeZeroYAxis;
	}

	public void setIncludeZeroYAxis(boolean includeZeroYAxis) {
		this.includeZeroYAxis = includeZeroYAxis;
	}

	

	
	
	
	
}