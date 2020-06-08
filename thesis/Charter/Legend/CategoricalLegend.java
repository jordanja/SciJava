package thesis.Charter.Legend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonArray;

public class CategoricalLegend extends Legend {
	// Hue
	private int hueLabelWidth;
	private int widestHueValueWidth;
	private int dataPointDiameter = 10;

	private int maxHueValueHeight;
	private int hueLabelHeight;

	// Size
	private int sizeLabelWidth;
	private int widestSizeValueWidth;
	private int largestDataPointDiameter;

	private int maxSizeValueHeight;
	private int sizeLabelHeight;

	// Common
	private int legendLeftToDataPointLeftWidth = 10;
	private int dataPointToTextWidth = 8;
	private int textRightToLegendRightWidth = 10;
	private int legendBottomToBottomValue = 10;
	private int valueSpacingHeight = 5;
	private int valuesTopToLabelHeight = 10;
	private int topSizeLabelToBottomHueHeight = 10;
	private int topLabelToLegendTopHeight = 10;

	// Helpers
	private int widestTextWidth;

	private Font labelFont = new Font("Dialog", Font.BOLD, 20);
	private Font valueFont = new Font("Dialog", Font.PLAIN, 20);

	private Color sizeBallColor = Color.RED;

	private LegendData legendData;

	public void setLegendData(LegendData legendData) {
		this.legendData = legendData;
	}

	public void calculateLegend() {
		this.includeLegend = true;

		if (this.legendData.includeColorInLegend()) {
			this.hueLabelWidth = DrawString.getStringWidth(this.legendData.getColorLabel(), this.labelFont);
			this.hueLabelHeight = DrawString.getStringHeight(this.legendData.getColorLabel(), this.labelFont);

			String[] colorValues = this.legendData.getColorData().keySet().toArray(new String[0]);
			this.widestHueValueWidth = DrawString.maxWidthOfStringInList(colorValues, this.valueFont, 0);
			this.maxHueValueHeight = DrawString.maxHeightOfStringInList(colorValues, this.valueFont, 0);
		} else {
			this.hueLabelWidth = 0;
			this.hueLabelHeight = 0;
			this.widestHueValueWidth = 0;
			this.maxHueValueHeight = 0;
			this.dataPointDiameter = 0;
		}

		int largestSizeDataPointDiameter = 0;

		if (this.legendData.includeSizeInLegend()) {
			this.sizeLabelWidth = DrawString.getStringWidth(this.legendData.getSizeLabel(), this.labelFont);
			this.sizeLabelHeight = DrawString.getStringHeight(this.legendData.getSizeLabel(), this.labelFont);
			largestSizeDataPointDiameter = CommonArray
					.maxValue(legendData.getSizeData().values().toArray(new Integer[0]));

			String[] sizeValues = this.legendData.getSizeData().keySet().toArray(new String[0]);
			this.widestSizeValueWidth = DrawString.maxWidthOfStringInList(sizeValues, this.valueFont, 0);
			this.maxSizeValueHeight = DrawString.maxHeightOfStringInList(sizeValues, this.valueFont, 0);
		} else {
			this.sizeLabelWidth = 0;
			this.sizeLabelHeight = 0;
			this.widestSizeValueWidth = 0;
			this.maxSizeValueHeight = 0;
			this.largestDataPointDiameter = 0;
		}

		this.largestDataPointDiameter = Integer.max(this.dataPointDiameter, largestSizeDataPointDiameter);
		int widestValueTextWidth = Integer.max(this.widestHueValueWidth, this.widestSizeValueWidth);
		int widestLabelTextWidth = Integer.max(this.hueLabelWidth, this.sizeLabelWidth);
		this.widestTextWidth = Integer.max(widestValueTextWidth, widestLabelTextWidth);

	}

	public void drawLegend(Graphics2D g, ChartMeasurements cm) {

		int legendBottom = cm.imageBottomToPlotMidHeight() - this.getLegendHeight() / 2;

		if (this.backgroundColor != null) {
			g.setColor(this.backgroundColor);
			g.fillRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());
		}

		g.setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);

		g.drawRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());

		if (this.legendData.includeColorInLegend()) {
			DrawString.setTextStyle(Color.BLACK, this.labelFont, 0);
			DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.BottomAlign);
			DrawString.write(g, this.legendData.getColorLabel(),
					cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(),
					legendBottom + this.getLegendBottomToHueLabelBottomHeight());

			int hueCount = 0;
			for (String hueValue : this.legendData.getColorData().keySet()) {
				DrawString.setTextStyle(Color.BLACK, this.valueFont, 0);
				DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.BottomAlign);
				DrawString.write(g, hueValue, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(),
						legendBottom + getLegendBottomToHueValueBottomHeight(hueCount));

				g.setColor(this.legendData.getColorData().get(hueValue));

				int x = cm.imageLeftToLegendLeftWidth() + getLegendLeftToDataPointsMidWidth()
						- this.dataPointDiameter / 2;
				int y = legendBottom + getLegendBottomToHueValueBottomHeight(hueCount);
				g.fillOval(x, y, this.dataPointDiameter, this.dataPointDiameter);
				hueCount++;
			}

		}

		if (this.legendData.includeSizeInLegend()) {
			DrawString.setTextStyle(Color.BLACK, this.labelFont, 0);
			DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.BottomAlign);
			DrawString.write(g, this.legendData.getSizeLabel(),
					cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(),
					legendBottom + this.getLegendBottomToSizeLabelBottomHeight());

			String[] keys = this.legendData.getSizeData().keySet().toArray(new String[0]);
			Integer[] diameters = new Integer[keys.length];

			for (int i = 0; i < diameters.length; i++) {
				diameters[i] = this.legendData.getSizeData().get(keys[i]);
			}

			for (int count = 0; count < keys.length; count++) {
				drawSizeText(g, cm, legendBottom, keys, count);
				drawSizeBall(g, keys, cm, legendBottom, count);
			}

		}
//			drawDebug(g, cm);

	}

	private void drawSizeText(Graphics2D g, ChartMeasurements cm, int legendBottom, String[] keys, int count) {
		int rowSize = getHeightOfSizeValue(count);

		DrawString.setTextStyle(Color.BLACK, this.valueFont, 0);
		DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.MiddleAlign);
		DrawString.write(g, keys[count], cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(),
				legendBottom + getLegendBottomToSizeValueBottomHeight(count) + rowSize / 2);
	}

	private void drawSizeBall(Graphics2D g, String[] keys, ChartMeasurements cm, int legendBottom, int count) {
		int diameter = this.legendData.getSizeData().get(keys[count]);

		int rowSize = getHeightOfSizeValue(count);

		int circleLeft = cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToDataPointsMidWidth() - diameter / 2;
		int circleMid = legendBottom + this.getLegendBottomToSizeValueBottomHeight(count) + rowSize / 2;

		g.setColor(this.sizeBallColor);

		g.fillOval(circleLeft, circleMid - diameter / 2, diameter, diameter);
	}

	private void drawDebug(Graphics2D g, ChartMeasurements cm) {
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.GRAY);
		int legendBottom = cm.imageBottomToPlotMidHeight() - this.getLegendHeight() / 2;
		int legendTop = cm.imageBottomToPlotMidHeight() + this.getLegendHeight() / 2;
		int legendLeft = cm.imageLeftToLegendLeftWidth();
		int legendRight = cm.imageLeftToLegendLeftWidth() + this.getLegendWidth();

		g.drawLine(legendLeft, legendBottom + this.getLegendBottomToBottomValue(), legendRight,
				legendBottom + this.getLegendBottomToBottomValue());
		g.drawLine(legendLeft, legendBottom + this.getLegendBottomToSizeLabelBottomHeight(), legendRight,
				legendBottom + this.getLegendBottomToSizeLabelBottomHeight());
	}

	public int getDataPointDiameter() {
		return dataPointDiameter;
	}

	public void setDataPointDiameter(int dataPointDiameter) {
		this.dataPointDiameter = dataPointDiameter;
	}

	public int getLegendLeftToDataPointLeftWidth() {
		return legendLeftToDataPointLeftWidth;
	}

	public void setLegendLeftToDataPointLeftWidth(int legendLeftToDataPointLeftWidth) {
		this.legendLeftToDataPointLeftWidth = legendLeftToDataPointLeftWidth;
	}

	public int getDataPointToTextWidth() {
		return dataPointToTextWidth;
	}

	public void setDataPointToTextWidth(int dataPointToTextWidth) {
		this.dataPointToTextWidth = dataPointToTextWidth;
	}

	public int getTextRightToLegendRightWidth() {
		return textRightToLegendRightWidth;
	}

	public void setTextRightToLegendRightWidth(int textRightToLegendRightWidth) {
		this.textRightToLegendRightWidth = textRightToLegendRightWidth;
	}

	public int getLegendBottomToBottomValue() {
		return legendBottomToBottomValue;
	}

	public void setLegendBottomToBottomValue(int legendBottomToBottomValue) {
		this.legendBottomToBottomValue = legendBottomToBottomValue;
	}

	public int getValueSpacingHeight() {
		return valueSpacingHeight;
	}

	public void setValueSpacingHeight(int valueSpacingHeight) {
		this.valueSpacingHeight = valueSpacingHeight;
	}

	public int getValuesTopToLabelHeight() {
		return valuesTopToLabelHeight;
	}

	public void setValuesTopToLabelHeight(int valuesTopToLabelHeight) {
		this.valuesTopToLabelHeight = valuesTopToLabelHeight;
	}

	public int getTopSizeLabelToBottomHueHeight() {
		return topSizeLabelToBottomHueHeight;
	}

	public void setTopSizeLabelToBottomHueHeight(int topSizeLabelToBottomHueHeight) {
		this.topSizeLabelToBottomHueHeight = topSizeLabelToBottomHueHeight;
	}

	public int getTopLabelToLegendTopHeight() {
		return topLabelToLegendTopHeight;
	}

	public void setTopLabelToLegendTopHeight(int topLabelToLegendTopHeight) {
		this.topLabelToLegendTopHeight = topLabelToLegendTopHeight;
	}

	public Font getLabelFont() {
		return labelFont;
	}

	public void setLabelFont(Font labelFont) {
		this.labelFont = labelFont;
	}

	public Font getValueFont() {
		return valueFont;
	}

	public void setValueFont(Font valueFont) {
		this.valueFont = valueFont;
	}

	public LegendData getLegendData() {
		return legendData;
	}

	public int getHueLabelWidth() {
		return hueLabelWidth;
	}

	public int getWidestHueValueWidth() {
		return widestHueValueWidth;
	}

	public int getMaxHueValueHeight() {
		return maxHueValueHeight;
	}

	public int getHueLabelHeight() {
		return hueLabelHeight;
	}

	public int getSizeLabelWidth() {
		return sizeLabelWidth;
	}

	public int getWidestSizeValueWidth() {
		return widestSizeValueWidth;
	}

	public int getLargestDataPointDiameter() {
		return largestDataPointDiameter;
	}

	public int getMaxSizeValueHeight() {
		return maxSizeValueHeight;
	}

	public int getSizeLabelHeight() {
		return sizeLabelHeight;
	}

	// Worker functions
	public int getLegendLeftToLargestDataPointLeftWidth() {
		return this.legendLeftToDataPointLeftWidth;
	}

	public int getLegendLeftToDataPointsMidWidth() {
		return getLegendLeftToLargestDataPointLeftWidth() + this.largestDataPointDiameter / 2;
	}

	public int getLegendLeftToLargestDataPointRightWidth() {
		return getLegendLeftToLargestDataPointLeftWidth() + this.largestDataPointDiameter;
	}

	public int getLegendLeftToTextLeft() {
		return getLegendLeftToLargestDataPointRightWidth() + this.dataPointToTextWidth;
	}

	public int getLegendLeftToWidestTextRight() {
		return getLegendLeftToTextLeft() + this.widestTextWidth;
	}

	public int getLegendWidth() {
		return getLegendLeftToWidestTextRight() + this.textRightToLegendRightWidth;
	}

////		public int getimageBottomToLegendBottom() {
////			return cm.imageBottomToChartMidHeight() - this.getLegendHeight()/2;
////		}

	public int getLegendBottomToSizeValuesBottomHeight() {
		return this.legendBottomToBottomValue;
	}

	public int getLegendBottomToSizeValuesTopHeight() {
		if (this.legendData.includeSizeInLegend()) {
			int heightTotal = 0;
			Integer[] diameters = legendData.getSizeData().values().toArray(new Integer[0]);
			for (int count = 0; count < diameters.length; count++) {
				heightTotal += Integer.max(diameters[count], this.maxSizeValueHeight);
			}
			heightTotal += (diameters.length - 1) * this.valueSpacingHeight;
			return getLegendBottomToSizeValuesBottomHeight() + heightTotal;
		}
		return getLegendBottomToSizeValuesBottomHeight();
	}

	public int getLegendBottomToSizeLabelBottomHeight() {
		return getLegendBottomToSizeValuesTopHeight() + this.valuesTopToLabelHeight;
	}

	public int getLegendBottomToSizeLabelMidHeight() {
		return getLegendBottomToSizeLabelBottomHeight() + this.sizeLabelHeight / 2;
	}

	public int getLegendBottomToSizeLabelTopHeight() {
		return getLegendBottomToSizeLabelBottomHeight() + this.sizeLabelHeight;
	}

	public int getLegendBottomToHueValuesBottomHeight() {
		return getLegendBottomToSizeLabelTopHeight() + this.topSizeLabelToBottomHueHeight;
	}

	public int getLegendBottomToHueValuesTopHeight() {
		if (this.legendData.includeColorInLegend()) {
			int numHueValues = this.legendData.getColorData().keySet().size();
			return getLegendBottomToHueValuesBottomHeight() + ((numHueValues - 1) * this.valueSpacingHeight)
					+ (numHueValues * Integer.max(this.dataPointDiameter, this.maxHueValueHeight));
		}
		return getLegendBottomToHueValuesBottomHeight();
	}

	public int getLegendBottomToHueLabelBottomHeight() {
		return getLegendBottomToHueValuesTopHeight() + this.valuesTopToLabelHeight;
	}

	public int getLegendBottomToHueLabelMidHeight() {
		return getLegendBottomToHueLabelBottomHeight() + this.hueLabelHeight / 2;
	}

	public int getLegendBottomToHueLabelTopHeight() {
		return getLegendBottomToHueLabelBottomHeight() + this.hueLabelHeight;
	}

	public int getLegendHeight() {
		return getLegendBottomToHueLabelTopHeight() + this.topLabelToLegendTopHeight;
	}

	// i starts from the top
	public int getLegendBottomToSizeValueBottomHeight(int i) {
		HashMap<String, Integer> sizeData = this.legendData.getSizeData();
		int numSizeValues = sizeData.keySet().size();

		int baseHeight = getLegendBottomToBottomValue();

		Integer[] diameters = sizeData.values().toArray(new Integer[0]);
		Arrays.sort(diameters);

		int runningTotal = 0;

		for (int count = numSizeValues - 1; count > i; count--) {
			runningTotal += Integer.max(diameters[count], this.maxSizeValueHeight);
//				System.out.println("max(" + diameters[count] + ", " + this.maxSizeValueHeight + ")");
		}

		runningTotal += this.valueSpacingHeight * (numSizeValues - i - 1);

//			System.out.println("\n");
		return baseHeight + runningTotal;
	}

	// i starts from top
	public int getHeightOfSizeValue(int i) {
		HashMap<String, Integer> sizeData = this.legendData.getSizeData();
		Integer[] diameters = sizeData.values().toArray(new Integer[0]);
		Arrays.sort(diameters);
		return Integer.max(diameters[i], this.maxSizeValueHeight);
	}

	public int getLegendBottomToHueValueBottomHeight(int i) {
		return getLegendBottomToHueValuesBottomHeight() + ((this.legendData.getColorData().values().size() - 1 - i)
				* (this.valueSpacingHeight + this.maxHueValueHeight));
	}

	public Color getSizeBallColor() {
		return sizeBallColor;
	}

	public void setSizeBallColor(Color sizeBallColor) {
		this.sizeBallColor = sizeBallColor;
	}

}
