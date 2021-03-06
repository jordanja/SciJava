package thesis.Charter.Plots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import thesis.Charter.Axis.GaugeAxis;
import thesis.Charter.ChartMeasurements.OnlyPlotChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;

public class GaugePlot extends Plot {

	private int numPointsPerArc = 50;
	private Color[] arcColors = new Color[] { Color.RED, Color.YELLOW, Color.GREEN };
	private int innerRadiusDifference = 50;

	private Color needleColor = Color.BLUE;
	
	private Color valueColor = Color.BLACK;
	private Font valueFont = new Font("Dialog", Font.PLAIN, 50);
	private boolean writeValue = true;

	public void drawPlot(Graphics2D g, double value, GaugeAxis axis, OnlyPlotChartMeasurements cm) {
		String[] axisStringValues = axis.getAxisStringValues();
		double[] axisValues = axis.getAxisValues();

		int widestAxisValue = DrawString.maxWidthOfStringInList(axisStringValues, axis.getAxisFont(), 0);
		int heighestAxisValue = DrawString.maxHeightOfStringInList(axisStringValues, axis.getAxisFont(), 0);
		int axisValueIndent = Integer.max(widestAxisValue, heighestAxisValue);

		int xMid = cm.imageLeftToPlotMidWidth();
		int yMid = cm.imageBottomToPlotMidHeight();

		int outerRadius = (cm.getPlotWidth() / 2) - axis.getEdgeBuffer() - axisValueIndent;
		int innerRadius = outerRadius - this.innerRadiusDifference;

		drawGauge(g, xMid, yMid, outerRadius, innerRadius);

		drawNeedle(g, value, axisValues, xMid, yMid, outerRadius, innerRadius);
		
		if (this.writeValue) {			
			String formattedValue = DrawString.formatDoubleForDisplay(value);
			DrawString.setAlignment(xAlignment.CenterAlign, yAlignment.MiddleAlign);
			DrawString.setTextStyle(this.valueColor, this.valueFont, 0);
			DrawString.write(g, formattedValue, cm.imageLeftToPlotMidWidth(), cm.imageBottomToPlotMidHeight() - 20);
		}

	}

	private void drawNeedle(Graphics2D g, double value, double[] axisValues, int xMid, int yMid, int outerRadius,
			int innerRadius) {
		double needleProportion = value / axisValues[axisValues.length - 1];
		double needleAngle = Math.PI - (needleProportion * Math.PI);

		double middleRadius = (innerRadius + outerRadius) / 2;

		double perpendicularRightAngle = needleAngle + (Math.PI / 2);
		double perpendicularLeftAngle = needleAngle - (Math.PI / 2);

		int halfNeedleBaseWidth = 6;

		int needlePointX = (int) (xMid + middleRadius * Math.cos(needleAngle));
		int needlePointY = (int) (yMid + middleRadius * Math.sin(needleAngle));

		int rightBaseX = (int) (xMid + halfNeedleBaseWidth * Math.cos(perpendicularRightAngle));
		int rightBaseY = (int) (yMid + halfNeedleBaseWidth * Math.sin(perpendicularRightAngle));

		int leftBaseX = (int) (xMid + halfNeedleBaseWidth * Math.cos(perpendicularLeftAngle));
		int leftBaseY = (int) (yMid + halfNeedleBaseWidth * Math.sin(perpendicularLeftAngle));

		GeneralPath needle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		needle.moveTo(needlePointX, needlePointY);
		needle.lineTo(leftBaseX, leftBaseY);
		needle.lineTo(rightBaseX, rightBaseY);

		g.setColor(this.needleColor);
		g.fill(needle);

		int baseDiameter = halfNeedleBaseWidth * 2;

		g.setColor(this.needleColor);
		g.fillOval(xMid - baseDiameter / 2, yMid - baseDiameter / 2, baseDiameter, baseDiameter);

		g.setColor(Color.WHITE);
		g.drawOval(xMid - baseDiameter / 2, yMid - baseDiameter / 2, baseDiameter, baseDiameter);
	}

	private void drawGauge(Graphics2D g, int xMid, int yMid, int outerRadius, int innerRadius) {
		int numArcs = this.arcColors.length;
		for (int arcCount = 0; arcCount < numArcs; arcCount++) {

			GeneralPath arc = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

			for (int arcPoints = 0; arcPoints <= numPointsPerArc; arcPoints++) {
				double proportion = ((double) arcPoints / numPointsPerArc);

				double angle = Math.PI - ((proportion * Math.PI) / numArcs) - arcCount * Math.PI / numArcs;

				int x = (int) (xMid + outerRadius * Math.cos(angle));
				int y = (int) (yMid + outerRadius * Math.sin(angle));

				if (arcPoints == 0) {
					arc.moveTo(x, y);
				} else {
					arc.lineTo(x, y);
				}
				
			}

			for (int arcPoints = numPointsPerArc; arcPoints >= 0; arcPoints--) {
				double proportion = ((double) arcPoints / numPointsPerArc);

				double angle = Math.PI - ((proportion * Math.PI) / numArcs) - arcCount * Math.PI / numArcs;

				int x = (int) (xMid + innerRadius * Math.cos(angle));
				int y = (int) (yMid + innerRadius * Math.sin(angle));

				arc.lineTo(x, y);

			}
			g.setColor(this.arcColors[arcCount]);
			g.fill(arc);
		}
	}

	public Color[] getArcColors() {
		return arcColors;
	}

	public void setArcColors(Color[] arcColors) {
		this.arcColors = arcColors;
	}

	public int getInnerRadiusDifference() {
		return innerRadiusDifference;
	}

	public void setInnerRadiusDifference(int innerRadiusDifference) {
		this.innerRadiusDifference = innerRadiusDifference;
	}

	public Color getNeedleColor() {
		return needleColor;
	}

	public void setNeedleColor(Color needleColor) {
		this.needleColor = needleColor;
	}

	public Color getValueColor() {
		return valueColor;
	}

	public void setValueColor(Color valueColor) {
		this.valueColor = valueColor;
	}

	public Font getValueFont() {
		return valueFont;
	}

	public void setValueFont(Font valueFont) {
		this.valueFont = valueFont;
	}

	public boolean isWriteValue() {
		return writeValue;
	}

	public void setWriteValue(boolean writeValue) {
		this.writeValue = writeValue;
	}

}
