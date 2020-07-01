package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Arrays;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Charter.Styles.Style;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;
import thesis.DataFrame.DataItem.StorageType;
import thesis.Helpers.TypeCheckers;

public class NumericAxis extends XYAxis {

	protected String[] xTicks;
	protected String[] yTicks;
	
	String xAxisType = "";
	
	private boolean includeZeroXAxis;
	private boolean includeZeroYAxis;

	private boolean includeXAxisLinesOnPlot = true;
	private Color xAxisLinesOnPlotColor = Color.WHITE;
	private int xAxisLinesOnPlotWidth = 1;
	
	private boolean includeYAxisLinesOnPlot = true;
	private Color yAxisLinesOnPlotColor = Color.WHITE;
	private int yAxisLinesOnPlotWidth = 1;
	
	private boolean drawLeftmostXAxisValue = false;
	private boolean drawRightmostXAxisValue = false;

	public void includeXAxisLinesOnPlot(boolean includeXAxisLinesOnPlot) {
		this.includeXAxisLinesOnPlot = includeXAxisLinesOnPlot;
	}

	public void setXAxisLinesOnPlotColor(Color xAxisLinesOnPlotColor) {
		this.xAxisLinesOnPlotColor = xAxisLinesOnPlotColor;
	}
	
	public void includeYAxisLinesOnPlot(boolean includeYAxisLinesOnPlot) {
		this.includeYAxisLinesOnPlot = includeYAxisLinesOnPlot;
	}

	public void setYAxisLinesOnPlotColor(Color yAxisLinesOnPlotColor) {
		this.yAxisLinesOnPlotColor = yAxisLinesOnPlotColor;
	}

	public NumericAxis() {
		super();

	}

	public void drawAxis(Graphics2D g, XYChartMeasurements cm) {

		int xAxisStart;
		if (this.drawLeftmostXAxisValue) {
			xAxisStart = 0;
		} else {
			xAxisStart = 1;
		}
		
		int xAxisEnd;
		if (this.drawRightmostXAxisValue) {
			xAxisEnd = this.xTicks.length;
		} else {
			xAxisEnd = this.xTicks.length - 1;
		}
		
		
		for (int count = xAxisStart; count < xAxisEnd; count++) {

			int position = CommonMath.map(count, 0, xTicks.length - 1, cm.imageLeftToPlotLeftWidth(),
					cm.imageLeftToPlotRightWidth());
			String stringToDisplay = this.getXTicksFormattedForDisplay()[count];
			
			DrawString.setTextStyle(this.xAxisColor, this.xAxisFont, this.xAxisRotation);
			DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
			if (this.drawBottomXAxisValues) {
				DrawString.write(g, stringToDisplay, position, cm.imageBottomToBottomAxisMidHeight());
			}
			if (this.drawTopXAxisValues) {
				DrawString.write(g, stringToDisplay, position, cm.imageBottomToTopAxisMidHeight());
			}

			if (this.includeXAxisLinesOnPlot) {
				g.setColor(this.xAxisLinesOnPlotColor);
				g.setStroke(new BasicStroke(this.xAxisLinesOnPlotWidth));
				g.drawLine(position, cm.imageBottomToPlotBottomHeight(), position, cm.imageBottomToPlotTopHeight());
			}

		}

		String[] yAxisValues = getYTicksFormattedForDisplay();
		for (int count = 1; count < this.yTicks.length - 1; count++) {
			int position = CommonMath.map(count, 0, this.yTicks.length - 1, cm.imageBottomToPlotBottomHeight(),
					cm.imageBottomToPlotTopHeight());
			String stringToDisplay = yAxisValues[count];

			DrawString.setTextStyle(this.yAxisColor, this.yAxisFont, this.yAxisRotation);
			DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
			if (this.drawLeftYAxisValues) {
				DrawString.write(g, stringToDisplay, cm.imageLeftToLeftAxisMidWidth(), position);
			}
			if (this.drawRightYAxisValues) {
				DrawString.write(g, stringToDisplay, cm.imageLeftToRightAxisMidWidth(), position);
			}

			if (this.includeYAxisLinesOnPlot) {
				g.setColor(this.yAxisLinesOnPlotColor);
				g.setStroke(new BasicStroke(this.yAxisLinesOnPlotWidth));
				g.drawLine(cm.imageLeftToPlotLeftWidth(), position, cm.imageLeftToPlotRightWidth(), position);
			}

		}

	}

	public void drawAxisTicks(Graphics2D g, XYChartMeasurements cm) {

		for (int count = 0; count < this.xTicks.length; count++) {
			int position = CommonMath.map(count, 0, xTicks.length - 1, cm.imageLeftToPlotLeftWidth(),
					cm.imageLeftToPlotRightWidth());

			g.setColor(this.bottomTickColor);
			if (this.drawExteriorBottomXAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorBottomTickThickness));
				g.drawLine(position, cm.imageBottomToPlotBottomHeight(), position,
						cm.imageBottomToBottomTicksEndHeight());
			}
			if (this.drawInteriorBottomXAxisTicks) {
				g.setStroke(new BasicStroke(this.interiorBottomTickThickness));
				g.drawLine(position, cm.imageBottomToPlotBottomHeight(), position,
						cm.imageBottomToPlotBottomHeight() + cm.getBottomTicksHeight());
			}
			g.setColor(this.topTickColor);
			if (this.drawExteriorTopXAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorTopTickThickness));
				g.drawLine(position, cm.imageBottomToPlotTopHeight(), position, cm.imageBottomToTopTicksEndHeight());
			}
			if (this.drawInteriorTopXAxisTicks) {
				g.setStroke(new BasicStroke(this.interiorTopTickThickness));
				g.drawLine(position, cm.imageBottomToPlotTopHeight(), position,
						cm.imageBottomToPlotTopHeight() - cm.getTopTicksHeight());
			}

		}

		for (int count = 0; count < this.yTicks.length; count++) {
			int position = CommonMath.map(count, 0, this.yTicks.length - 1, cm.imageBottomToPlotBottomHeight(),
					cm.imageBottomToPlotTopHeight());

			g.setColor(this.leftTickColor);
			if (this.drawExteriorLeftYAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorLeftTickThickness));
				g.drawLine(cm.imageLeftToPlotLeftWidth(), position, cm.imageLeftToLeftTicksEndWidth(), position);
			}
			if (this.drawInteriorLeftYAxisTicks) {
				g.setStroke(new BasicStroke(this.interiorLeftTickThickness));
				g.drawLine(cm.imageLeftToPlotLeftWidth(), position,
						cm.imageLeftToPlotLeftWidth() + cm.getLeftTicksWidth(), position);
			}
			g.setColor(this.rightTickColor);
			if (this.drawExteriorRightYAxisTicks) {
				g.setStroke(new BasicStroke(this.exteriorRightTickThickness));
				g.drawLine(cm.imageLeftToPlotRightWidth(), position, cm.imageLeftToRightTicksEndWidth(), position);
			}
			if (this.drawInteriorRightYAxisTicks) {
				g.setStroke(new BasicStroke(this.interiorRightTickThickness));
				g.drawLine(cm.imageLeftToPlotRightWidth(), position,
						cm.imageLeftToPlotRightWidth() - cm.getRightTicksWidth(), position);
			}

		}
	}
	
	
	
	public void calculateXAxis(DataItem[] values) {
		StorageType type = values[0].getType();
		if (type == StorageType.LocalDate) {
			LocalDate max = CommonArray.maxLocalDate(values);
			LocalDate min = CommonArray.minLocalDate(values);
			System.out.println("max: " + max);
			System.out.println("min: " + min);
		} else if (type == StorageType.Integer) {
			
		} else if (type == StorageType.Double) {
			
		}
	}
	
	public void calculateXAxis(DataItem minimum, DataItem maximum) {
		
	}
	
	public void calculateXAxis(LocalDate minimum, LocalDate maximum) {
		this.xAxisType = "LocalDate";
		
		long minInt = minimum.toEpochDay();
		long maxInt = maximum.toEpochDay();
		
		NiceScale xNS = new NiceScale(minInt, maxInt);
		this.xTicks = new String[(int) Math
 				.ceil((((maxInt - xNS.getNiceMin()) + xNS.getTickSpacing()) / xNS.getTickSpacing()) + 1)];
 		int count = 0;

 		for (long xTickNum = (long) xNS.getNiceMin(); count < this.xTicks.length; xTickNum += xNS.getTickSpacing()) {

 			this.xTicks[count] = String.valueOf(LocalDate.ofEpochDay(xTickNum));
 			System.out.println(this.xTicks[count]);
 			count++;
 		}
		
		
	}

	public void calculateXAxis(Double minimum, Double maximum) {
		this.xAxisType = "Double";
		NiceScale xNS;
		
		if (this.includeZeroXAxis) {
			xNS = new NiceScale(Math.min(0, minimum), maximum);
		} else {
			xNS = new NiceScale(minimum, maximum);
		}

		this.xTicks = new String[(int) Math
				.ceil((((maximum - xNS.getNiceMin()) + xNS.getTickSpacing()) / xNS.getTickSpacing()) + 1)];
		int count = 0;

		for (double xTickNum = xNS.getNiceMin(); count < this.xTicks.length; xTickNum += xNS.getTickSpacing()) {

			this.xTicks[count] = String.valueOf(xTickNum);
			count++;
		}

	}

	public void calculateYAxis(Double minimum, Double maximum) {
		
		NiceScale yNS;
		

		if (this.includeZeroYAxis) {
			yNS = new NiceScale(Math.min(0, minimum), maximum);
		} else {
			yNS = new NiceScale(minimum, maximum);
		}

		this.yTicks = new String[(int) Math
				.ceil((((maximum - yNS.getNiceMin()) + yNS.getTickSpacing()) / yNS.getTickSpacing()) + 1)];
		int count = 0;
		for (double yTickNum = yNS.getNiceMin(); count < this.yTicks.length; yTickNum += yNS.getTickSpacing()) {
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_DOWN);
			
			this.yTicks[count] = df.format(yTickNum);
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

	public String[] getXTicks() {
		return this.xTicks;
	}

	public void setXTicks(String[] xTicks) {
		this.xTicks = xTicks;
	}
	
	public String[] getYTicks() {
		return this.yTicks;
	}
	
	public void setYTicks(String[] yTicks) {
		this.yTicks = yTicks;
	}

	public double[] getXTicksValues() {
		return Arrays.stream(getXTicks()).mapToDouble(Double::parseDouble).toArray();
	}

	public double[] getYTicksValues() {
		return Arrays.stream(getYTicks()).mapToDouble(Double::parseDouble).toArray();
	}
	

	public boolean isDrawLeftmostXAxisValue() {
		return drawLeftmostXAxisValue;
	}

	public void setDrawLeftmostXAxisValue(boolean drawLeftmostXAxisValue) {
		this.drawLeftmostXAxisValue = drawLeftmostXAxisValue;
	}

	public boolean isDrawRightmostXAxisValue() {
		return drawRightmostXAxisValue;
	}

	public void setDrawRightmostXAxisValue(boolean drawRightmostXAxisValue) {
		this.drawRightmostXAxisValue = drawRightmostXAxisValue;
	}

	public String[] getXTicksFormattedForDisplay() {
		String[] formattedXTicks = new String[this.xTicks.length];

		if (this.xTicks.length > 0) {
			if (TypeCheckers.isNumeric(this.xTicks[0])) {
				double[] xTicksValues = this.getXTicksValues();

				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.HALF_DOWN);

				for (int i = 0; i < formattedXTicks.length; i++) {
					formattedXTicks[i] = df.format(xTicksValues[i]);
				}
				return formattedXTicks;
			} else {
				return this.xTicks;
			}
		}
		return null;
	}

	public String[] getYTicksFormattedForDisplay() {
		String[] formattedYTicks = new String[this.yTicks.length];

		if (TypeCheckers.isNumeric(this.yTicks[0])) {
			double[] yTicksValues = this.getYTicksValues();

			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_DOWN);

			for (int i = 0; i < formattedYTicks.length; i++) {
				formattedYTicks[i] = df.format(yTicksValues[i]);
			}
			return formattedYTicks;
		} else {
			return this.yTicks;
		}
	}

	public void setStyle(Style style) {
		this.xAxisRotation = style.getXAxisRotation();
		this.yAxisRotation = style.getYAxisRotation();
		
		this.drawBottomXLabel = style.getDrawBottomXLabel();
		this.drawTopXLabel = style.getDrawTopXLabel();
		this.drawLeftYLabel = style.getDrawLeftYLabel();
		this.drawRightYLabel = style.getDrawRightYLabel();
		
		this.drawBottomXAxisValues = style.getDrawBottomXAxisValues();
		this.drawTopXAxisValues = style.getDrawTopXAxisValues();
		this.drawLeftYAxisValues = style.getDrawLeftYAxisValues();
		this.drawRightYAxisValues = style.getDrawRightYAxisValues();
		
		this.drawExteriorBottomXAxisTicks = style.getDrawExteriorBottomXAxisTicks();
		this.drawExteriorTopXAxisTicks = style.getDrawExteriorTopXAxisTicks();
		this.drawExteriorLeftYAxisTicks = style.getDrawExteriorLeftYAxisTicks();
		this.drawExteriorRightYAxisTicks = style.getDrawExteriorRightYAxisTicks();
		
		this.drawInteriorBottomXAxisTicks = style.getDrawInteriorBottomXAxisTicks();
		this.drawInteriorTopXAxisTicks = style.getDrawInteriorTopXAxisTicks();
		this.drawInteriorLeftYAxisTicks = style.getDrawInteriorLeftYAxisTicks();
		this.drawInteriorRightYAxisTicks = style.getDrawInteriorRightYAxisTicks();
		
		this.bottomTickColor = style.getBottomTickColor();
		this.topTickColor = style.getTopTickColor();
		this.leftTickColor = style.getLeftTickColor();
		this.rightTickColor = style.getRightTickColor();
		
		this.interiorBottomTickThickness = style.getInteriorBottomTickThickness();
		this.interiorTopTickThickness = style.getInteriorTopTickThickness();
		this.interiorLeftTickThickness = style.getInteriorLeftTickThickness();
		this.interiorRightTickThickness = style.getInteriorRightTickThickness();
		
		this.exteriorBottomTickThickness = style.getExteriorBottomTickThickness();
		this.exteriorTopTickThickness = style.getExteriorTopTickThickness();
		this.exteriorLeftTickThickness = style.getExteriorLeftTickThickness();
		this.exteriorRightTickThickness = style.getExteriorRightTickThickness();
		
		this.xAxisFont = style.getXAxisFont();
		this.yAxisFont = style.getYAxisFont();
		this.xAxisLabelFont = style.getXAxisLabelFont();
		this.yAxisLabelFont = style.getYAxisLabelFont();
		
		this.xAxisColor = style.getXAxisColor();
		this.yAxisColor = style.getYAxisColor();
		this.xAxisLabelColor = style.getXAxisLabelColor();
		this.yAxisLabelColor = style.getYAxisLabelColor();
		
		this.includeXAxisLinesOnPlot = style.getIncludeXAxisLinesOnPlot();
		this.xAxisLinesOnPlotColor = style.getXAxisLinesOnPlotColor();
		this.xAxisLinesOnPlotWidth = style.getXAxisLinesOnPlotWidth();
		
		this.includeYAxisLinesOnPlot = style.getIncludeYAxisLinesOnPlot();
		this.yAxisLinesOnPlotColor = style.getYAxisLinesOnPlotColor();
		this.yAxisLinesOnPlotWidth = style.getYAxisLinesOnPlotWidth();
	}
	
}
