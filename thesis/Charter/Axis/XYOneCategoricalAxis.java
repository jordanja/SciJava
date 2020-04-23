package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Helpers.TypeCheckers;

public class XYOneCategoricalAxis extends XYAxis {

	protected String[] categoricalTicks;
	protected String[] numericalTicks;

	private boolean includeAxisLinesOnPlot = true;
	private Color axisLinesOnPlotColor = Color.WHITE;

	private String orientation = "v";

	public void setXAxis(String[] xData) {
		this.categoricalTicks = xData;
	}

	public void drawAxis(Graphics2D g, XYChartMeasurements cm) {
		if (this.orientation == "v") {
			drawXAxisCategorical(g, cm, this.categoricalTicks);
			drawYAxisNumerical(g, cm, getNumericTicksFormattedForDisplay());
		} else if (this.orientation == "h") {
			drawXAxisNumerical(g, cm, getNumericTicksFormattedForDisplay());
			drawYAxisCategorical(g, cm, this.categoricalTicks);
		}

	}

	private void drawYAxisCategorical(Graphics2D g, XYChartMeasurements cm, String[] ticks) {
		if (ticks.length > 0) {
			int halfWidthOfYUnit = (cm.getPlotHeight() / (2 * ticks.length));
			int count = 0;
			for (String xCatagory : ticks) {
				int yPosition = CommonMath.map(count, 0, ticks.length - 1,
						cm.imageBottomToPlotBottomHeight() + halfWidthOfYUnit,
						cm.imageBottomToPlotTopHeight() - halfWidthOfYUnit);

				DrawString.setTextStyle(this.yAxisColor, this.yAxisFont, this.yAxisRotation);
				DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
				if (this.drawLeftYAxisValues()) {
					DrawString.write(g, xCatagory, cm.imageLeftToLeftAxisMidWidth(), yPosition);
				}
				if (this.drawRightYAxisValues()) {
					DrawString.write(g, xCatagory, cm.imageLeftToRightAxisMidWidth(), yPosition);
				}
				count++;
			}
		}
	}
	
	private void drawYAxisNumerical(Graphics2D g, XYChartMeasurements cm, String[] ticks) {
		if (ticks.length > 0) {
			for (int count = 1; count < ticks.length - 1; count++) {
				int position = CommonMath.map(count, 0, ticks.length - 1,
						cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
				String stringToDisplay = ticks[count];

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
	}

	private void drawXAxisNumerical(Graphics2D g, XYChartMeasurements cm, String[] ticks) {
		if (ticks.length > 0) {
			for (int count = 1; count < ticks.length - 1; count++) {
				int position = CommonMath.map(count, 0, ticks.length - 1, cm.imageLeftToPlotLeftWidth(), cm.imageLeftToPlotRightWidth());
				String stringToDisplay = ticks[count];
				DrawString.setTextStyle(this.xAxisColor, this.xAxisFont, this.xAxisRotation);
				DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
				if (this.drawBottomXAxisValues()) {
					DrawString.write(g, stringToDisplay, position, cm.imageBottomToBottomAxisMidHeight());
				}
				if (this.drawTopXAxisValues()) {
					DrawString.write(g, stringToDisplay, position, cm.imageBottomToTopAxisMidHeight());
				}
			}
		}
	}
	
	private void drawXAxisCategorical(Graphics2D g, XYChartMeasurements cm, String[] ticks) {
		if (ticks.length > 0) {
			int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * ticks.length));
			int count = 0;
			for (String xCatagory : ticks) {
				int xPosition = CommonMath.map(count, 0, ticks.length - 1,
						cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
						cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);

				DrawString.setTextStyle(this.xAxisColor, this.xAxisFont, this.xAxisRotation);
				DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
				if (this.drawBottomXAxisValues) {
					DrawString.write(g, xCatagory, xPosition, cm.imageBottomToBottomAxisMidHeight());
				}
				if (this.drawTopXAxisValues) {
					DrawString.write(g, xCatagory, xPosition, cm.imageBottomToTopAxisMidHeight());
				}
				count++;
			}
		}
	}

	public void drawAxisTicks(Graphics2D g, XYChartMeasurements cm) {

		int numHorizontalTicks;
		int numVerticleTicks;
		if (this.orientation == "v") {
			numHorizontalTicks = this.categoricalTicks.length;
			numVerticleTicks = this.numericalTicks.length;
		} else {
			numHorizontalTicks = this.numericalTicks.length;
			numVerticleTicks = this.categoricalTicks.length;
		}
		System.out.println("drawing: " + numHorizontalTicks + " horizontal ticks");
		System.out.println("drawing: " + numVerticleTicks + " verticle ticks");
		
		if (numHorizontalTicks > 0) {
			
			int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * numHorizontalTicks));
			for (int count = 0; count < numHorizontalTicks; count++) {
				int xPosition;
				if (this.orientation == "v") {					
					xPosition = CommonMath.map(count, 0, numHorizontalTicks - 1, cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit, cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);
				} else {
					xPosition = CommonMath.map(count, 0, numHorizontalTicks - 1, cm.imageLeftToPlotLeftWidth(), cm.imageLeftToPlotRightWidth());
				}

				g.setColor(this.bottomTickColor);
				if (this.drawExteriorBottomXAxisTicks) {
					g.setStroke(new BasicStroke(this.exteriorBottomTickThickness));
					g.drawLine(xPosition, cm.imageBottomToPlotBottomHeight(), xPosition,
							cm.imageBottomToBottomTicksEndHeight());
				}
				if (this.drawInteriorBottomXAxisTicks) {
					g.setStroke(new BasicStroke(this.interiorBottomTickThickness));
					g.drawLine(xPosition, cm.imageBottomToPlotBottomHeight(), xPosition,
							cm.imageBottomToPlotBottomHeight() + cm.getBottomTicksHeight());
				}
				g.setColor(this.topTickColor);
				if (this.drawExteriorTopXAxisTicks) {
					g.setStroke(new BasicStroke(this.exteriorTopTickThickness));
					g.drawLine(xPosition, cm.imageBottomToPlotTopHeight(), xPosition,
							cm.imageBottomToTopTicksEndHeight());
				}
				if (this.drawInteriorTopXAxisTicks) {
					g.setStroke(new BasicStroke(this.interiorTopTickThickness));
					g.drawLine(xPosition, cm.imageBottomToPlotTopHeight(), xPosition,
							cm.imageBottomToPlotTopHeight() - cm.getTopTicksHeight());
				}

			}
		}

		if (numVerticleTicks > 0) {
			int halfHeightOfYUnit = (cm.getPlotHeight() / (2 * numVerticleTicks));
			for (int count = 0; count < numVerticleTicks; count++) {
				int yPosition; 
				if (this.orientation == "v") {
					yPosition = CommonMath.map(count, 0, numVerticleTicks - 1, cm.imageBottomToPlotBottomHeight(), cm.imageBottomToPlotTopHeight());
				} else {
					yPosition = CommonMath.map(count, 0, numVerticleTicks - 1, cm.imageBottomToPlotBottomHeight() + halfHeightOfYUnit, cm.imageBottomToPlotTopHeight() - halfHeightOfYUnit);
				}

				g.setColor(this.leftTickColor);
				if (this.drawExteriorLeftYAxisTicks) {
					g.setStroke(new BasicStroke(this.exteriorLeftTickThickness));
					g.drawLine(cm.imageLeftToPlotLeftWidth(), yPosition, cm.imageLeftToLeftTicksEndWidth(), yPosition);
				}
				if (this.drawInteriorLeftYAxisTicks) {
					g.setStroke(new BasicStroke(this.interiorLeftTickThickness));
					g.drawLine(cm.imageLeftToPlotLeftWidth(), yPosition,
							cm.imageLeftToPlotLeftWidth() + cm.getLeftTicksWidth(), yPosition);
				}
				g.setColor(this.rightTickColor);
				if (this.drawExteriorRightYAxisTicks) {
					g.setStroke(new BasicStroke(this.exteriorRightTickThickness));
					g.drawLine(cm.imageLeftToPlotRightWidth(), yPosition, cm.imageLeftToRightTicksEndWidth(), yPosition);
				}
				if (this.drawInteriorRightYAxisTicks) {
					g.setStroke(new BasicStroke(this.interiorRightTickThickness));
					g.drawLine(cm.imageLeftToPlotRightWidth(), yPosition,
							cm.imageLeftToPlotRightWidth() - cm.getRightTicksWidth(), yPosition);
				}

			}
		}
	}

	public boolean isIncludeAxisLinesOnPlot() {
		return includeAxisLinesOnPlot;
	}

	public void setIncludeAxisLinesOnPlot(boolean includeAxisLinesOnPlot) {
		this.includeAxisLinesOnPlot = includeAxisLinesOnPlot;
	}

	public Color getAxisLinesOnPlotColor() {
		return axisLinesOnPlotColor;
	}

	public void setAxisLinesOnPlotColor(Color axisLinesOnPlotColor) {
		this.axisLinesOnPlotColor = axisLinesOnPlotColor;
	}

	public String[] getCategoricalTicks() {
		return categoricalTicks;
	}

	public void setCategoricalTicks(String[] categoricalTicks) {
		this.categoricalTicks = categoricalTicks;
	}

	public String[] getNumericalTicks() {
		return numericalTicks;
	}

	public void setNumericalTicks(String[] numericalTicks) {
		this.numericalTicks = numericalTicks;
	}

	public double[] getNumericTicksValues() {
		return Arrays.stream(getNumericalTicks()).mapToDouble(Double::parseDouble).toArray();
	}

	public String[] getXTicksFormattedForDisplay() {

		if (this.orientation == "v") {
			return this.categoricalTicks;
		} else if (this.orientation == "h") {
			String[] formattedXTicks = new String[this.numericalTicks.length];

			if (this.numericalTicks.length > 0) {
				if (TypeCheckers.isNumeric(this.numericalTicks[0])) {
					double[] xTicksValues = this.getNumericTicksValues();

					DecimalFormat df = new DecimalFormat("#.##");
					df.setRoundingMode(RoundingMode.HALF_DOWN);

					for (int i = 0; i < formattedXTicks.length; i++) {
						formattedXTicks[i] = df.format(xTicksValues[i]);
					}
					return formattedXTicks;
				} else {
					return this.numericalTicks;
				}
			}
		}

		return null;
	}

	public String[] getYTicksFormattedForDisplay() {

		if (this.orientation == "v") {
			return getNumericTicksFormattedForDisplay();
		} else if (this.orientation == "h"){
			return this.categoricalTicks;
		}
		
		return null;
	}

	public String[] getNumericTicksFormattedForDisplay() {
		if (this.numericalTicks.length > 0) {
			if (TypeCheckers.isNumeric(this.numericalTicks[0])) {
				String[] formattedXTicks = new String[this.numericalTicks.length];

				double[] xTicksValues = this.getNumericTicksValues();

				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.HALF_DOWN);

				for (int i = 0; i < formattedXTicks.length; i++) {
					formattedXTicks[i] = df.format(xTicksValues[i]);
				}
				return formattedXTicks;
			} else {
				return this.numericalTicks;
			}
		}
		return null;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
}
