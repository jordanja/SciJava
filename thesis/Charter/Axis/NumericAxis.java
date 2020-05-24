package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.Helpers.TypeCheckers;

public class NumericAxis extends XYAxis {

	protected String[] xTicks;
	protected String[] yTicks;
	
	private boolean includeZeroXAxis;
	private boolean includeZeroYAxis;

	private double smallestX;
	private double largestX;
	private double smallestY;
	private double largestY;

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

		for (int count = 1; count < this.xTicks.length - 1; count++) {

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

			if (this.includeAxisLinesOnPlot) {
				g.setColor(this.axisLinesOnPlotColor);
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

			if (this.includeAxisLinesOnPlot) {
				g.setColor(this.axisLinesOnPlotColor);
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

	public void calculateXAxis(Double minimum, Double maximum) {
		NiceScale xNS;
		
		this.smallestX = minimum;
		this.largestX = maximum;

		if (this.includeZeroXAxis) {
			xNS = new NiceScale(Math.min(0, minimum), largestX);
		} else {
			xNS = new NiceScale(smallestX, largestX);
		}

		this.xTicks = new String[(int) Math
				.ceil((((largestX - xNS.getNiceMin()) + xNS.getTickSpacing()) / xNS.getTickSpacing()) + 1)];
		int count = 0;

		for (double xTickNum = xNS.getNiceMin(); count < this.xTicks.length; xTickNum += xNS.getTickSpacing()) {

			this.xTicks[count] = String.valueOf(xTickNum);
			count++;
		}

	}

	public void calculateYAxis(Double minimum, Double maximum) {
		
		NiceScale yNS;
		
		this.smallestY = minimum;
		this.largestY = maximum;

		if (this.includeZeroYAxis) {
			yNS = new NiceScale(Math.min(0, minimum), largestY);
		} else {
			yNS = new NiceScale(smallestY, largestY);
		}

		this.yTicks = new String[(int) Math
				.ceil((((largestY - yNS.getNiceMin()) + yNS.getTickSpacing()) / yNS.getTickSpacing()) + 1)];
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
	
}
