package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;

public class StripChartAxis extends XYAxis {

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

			HashMap<Object, Double[]> catagoryMap = (HashMap<Object, Double[]>) data;
			for (Object catagory : catagoryMap.keySet()) {
				Double[] dataList = catagoryMap.get(catagory);
				maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
				minValue = Double.min(minValue, CommonArray.minValue(dataList));
			}

		} else if (typeOfData == "multipleCatagoriesAndHueValue") {
			HashMap<Object, HashMap<Object, Double[]>> catagoryMap = (HashMap<Object, HashMap<Object, Double[]>>) data;
			for (Object catagory : catagoryMap.keySet()) {
				HashMap<Object, Double[]> hueMap = catagoryMap.get(catagory);
				for (Object hue : hueMap.keySet()) {
					Double[] dataList = hueMap.get(hue);
					maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
					minValue = Double.min(minValue, CommonArray.minValue(dataList));
				}
			}
		}
		NiceScale yNS = new NiceScale(minValue, maxValue);

		this.yTicks = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];

		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.yTicks[i] = String.valueOf(tickValue);
		}
	}

	public void drawAxis(Graphics2D g, XYChartMeasurements cm) {
		if (this.xTicks.length > 0) {
			int halfWidthOfXUnit = (cm.getPlotWidth() / (2 * this.xTicks.length));
			int count = 0;
			for (String xCatagory : this.xTicks) {
				int xPosition = CommonMath.map(count, 0, this.xTicks.length - 1,
						cm.imageLeftToPlotLeftWidth() + halfWidthOfXUnit,
						cm.imageLeftToPlotRightWidth() - halfWidthOfXUnit);

				DrawString.setColor(this.xAxisColor);
				DrawString.setFont(this.xAxisFont);
				DrawString.setRotation(this.xAxisRotation);
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

		for (int count = 1; count < this.yTicks.length - 1; count++) {
			int position = CommonMath.map(count, 0, this.yTicks.length - 1, cm.imageBottomToPlotBottomHeight(),
					cm.imageBottomToPlotTopHeight());
			String stringToDisplay = this.getYTicksFormattedForDisplay()[count];

			DrawString.setColor(this.yAxisColor);
			DrawString.setFont(this.yAxisFont);
			DrawString.setRotation(this.yAxisRotation);
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

}
