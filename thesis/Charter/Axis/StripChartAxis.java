package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.Others.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;

public class StripChartAxis extends XYAxis {
	private NiceScale yNS;
	private boolean includeAxisLinesOnPlot = true;
	private Color axisLinesOnPlotColor = Color.WHITE;

	public void setXAxis(String[] xData) {
		this.xTicks = xData;
	}

	public void setYAxis(Object data, String typeOfData) {

		double maxValue = Double.NEGATIVE_INFINITY;
		double minValue = Double.POSITIVE_INFINITY;

		if (typeOfData == "singleCatagory") {
			Double[] dataList = (Double[]) data;
			maxValue = CommonArray.maxValue(dataList);
			minValue = CommonArray.minValue(dataList);

		} else if (typeOfData == "multipleCatagoriesAndNoHueValue") {

			HashMap<Object, Object> catagoryMap = (HashMap<Object, Object>) data;
			for (Object catagory : catagoryMap.keySet()) {
				Double[] dataList = (Double[]) catagoryMap.get(catagory);
				maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
				minValue = Double.min(minValue, CommonArray.minValue(dataList));
			}

		} else if (typeOfData == "multipleCatagoriesAndHueValue") {
			HashMap<Object, HashMap<Object, Object>> catagoryMap = (HashMap<Object, HashMap<Object, Object>>) data;
			for (Object catagory : catagoryMap.keySet()) {
				HashMap<Object, Object> hueMap = catagoryMap.get(catagory);
				for (Object hue : hueMap.keySet()) {
					Double[] dataList = (Double[]) hueMap.get(hue);
					maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
					minValue = Double.min(minValue, CommonArray.minValue(dataList));
				}
			}
		}

		this.yNS = new NiceScale(minValue, maxValue);

		this.yTicks = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];

		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.yTicks[i] = String.valueOf(tickValue);
		}
	}

	public NiceScale getyNS() {
		return yNS;
	}

	public void drawAxis(Graphics2D g, XYChartMeasurements cm) {
		if (this.xTicks.length > 0) {
			int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * this.xTicks.length));
			int count = 0;
			for (String xCatagory : this.xTicks) {
				int xPosition = CommonMath.map(count, 0, this.xTicks.length - 1,
						cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
						cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);

				g.setColor(this.xAxisColor);
				g.setFont(this.xAxisFont);
				if (this.drawBottomXAxisValues) {
					DrawString.drawString(g, xCatagory, xPosition, cm.imageBottomToBottomAxisMidHeight(),
							DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.xAxisRotation,
							cm);
				}
				if (this.drawTopXAxisValues) {
					DrawString.drawString(g, xCatagory, xPosition, cm.imageBottomToTopAxisMidHeight(),
							DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.xAxisRotation,
							cm);
				}
				count++;
			}
		}

		for (int count = 1; count < this.yTicks.length - 1; count++) {
			int position = CommonMath.map(count, 0, this.yTicks.length - 1, cm.imageBottomToPlotBottomHeight(),
					cm.imageBottomToPlotTopHeight());
			String stringToDisplay = this.getYTicksFormattedForDisplay()[count];

			g.setColor(this.yAxisColor);
			g.setFont(this.yAxisFont);

			if (this.drawLeftYAxisValues) {
				DrawString.drawString(g, stringToDisplay, cm.imageLeftToLeftAxisMidWidth(), position,
						DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.yAxisRotation, cm);
			}
			if (this.drawRightYAxisValues) {
				DrawString.drawString(g, stringToDisplay, cm.imageLeftToRightAxisMidWidth(), position,
						DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, this.yAxisRotation, cm);
			}

			if (this.includeAxisLinesOnPlot) {
				g.setColor(this.axisLinesOnPlotColor);
				g.drawLine(cm.imageLeftToPlotLeftWidth(), position, cm.imageLeftToPlotRightWidth(), position);
			}

		}

	}

	public void drawAxisTicks(Graphics2D g, XYChartMeasurements cm) {

		if (this.xTicks.length > 0) {
			int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * this.xTicks.length));
			for (int count = 0; count < this.xTicks.length; count++) {
				int xPosition = CommonMath.map(count, 0, xTicks.length - 1,
						cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
						cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);

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

	public void drawXAxisLabel(Graphics2D g, XYChartMeasurements cm) {

		g.setColor(this.xAxisLabelColor);
		g.setFont(this.xAxisLabelFont);

		if (this.drawBottomXLabel()) {
			DrawString.drawString(g, this.xAxisLabel, cm.imageLeftToPlotMidWidth(),
					cm.imageBottomToBottomAxisLabelMidHeight(), DrawString.xAlignment.CenterAlign,
					DrawString.yAlignment.MiddleAlign, 0, cm);
		}
		if (this.drawTopXLabel()) {
			DrawString.drawString(g, this.xAxisLabel, cm.imageLeftToPlotMidWidth(),
					cm.imageBottomToTopAxisLabelMidHeight(), DrawString.xAlignment.CenterAlign,
					DrawString.yAlignment.MiddleAlign, 0, cm);
		}

	}

	public void drawYAxisLabel(Graphics2D g, XYChartMeasurements cm) {
		g.setColor(this.yAxisLabelColor);
		g.setFont(this.yAxisLabelFont);

		if (this.drawLeftYLabel()) {
			DrawString.drawString(g, this.yAxisLabel, cm.imageLeftToLeftAxisLabelMidWidth(),
					cm.imageBottomToPlotMidHeight(), DrawString.xAlignment.CenterAlign,
					DrawString.yAlignment.MiddleAlign, -90, cm);
		}
		if (this.drawRightYLabel()) {
			DrawString.drawString(g, this.yAxisLabel, cm.imageLeftToRightAxisLabelMidWidth(),
					cm.imageBottomToPlotMidHeight(), DrawString.xAlignment.CenterAlign,
					DrawString.yAlignment.MiddleAlign, -90, cm);
		}

	}
}
