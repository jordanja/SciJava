package thesis.Charter.Legend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonArray;

public class Legend {

	private boolean includeLegend;

	
	// Hue
	private int hueLabelWidth;				// done
	private int widestHueValueWidth;		// done
	private int dataPointDiameter = 10;		// done
	
	private int maxHueValueHeight;			// done
	private int hueLabelHeight;				// done
	
	// Size
	private int sizeLabelWidth;				// done
	private int widestSizeValueWidth;		// done
	private int largestDataPointDiameter;
	
	private int maxSizeValueHeight;			// done
	private int sizeLabelHeight;			// done

	
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

	private Color backgroundColor = Color.WHITE;
	
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
			this.sizeLabelHeight = DrawString.getStringHeight(this.legendData.getColorLabel(), this.labelFont);
			largestSizeDataPointDiameter = CommonArray.maxValue(legendData.getSizeData().values().toArray(new Integer[0]));
			
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
		getLegendBottomToSizeValueBottomHeight(0);	
	}
	
	public void drawLegend(Graphics2D g, ChartMeasurements cm) {
		
		int legendBottom = cm.imageBottomToPlotMidHeight() - this.getLegendHeight()/2;

		if (this.backgroundColor != null) {
			g.setColor(this.backgroundColor);
			g.fillRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());
		}
		
		
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);
		
		g.drawRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());

		if (this.legendData.includeColorInLegend()) {			
			DrawString.setTextStyle(Color.BLACK, this.labelFont, 0);
			DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
			DrawString.write(g, this.legendData.getColorLabel(), cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(), legendBottom + this.getLegendBottomToHueLabelBottomHeight());
			
			int hueCount = 0;
			for (String hueValue: this.legendData.getColorData().keySet()) {
				DrawString.setTextStyle(Color.BLACK, this.valueFont, 0);
				DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
				DrawString.write(g, hueValue, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(), legendBottom + getLegendBottomToHueValueBottomHeight(hueCount));
	
				g.setColor(this.legendData.getColorData().get(hueValue));
				
				int x = cm.imageLeftToLegendLeftWidth() + getLegendLeftToDataPointsMidWidth() - this.dataPointDiameter/2;
				int y = legendBottom + getLegendBottomToHueValueBottomHeight(hueCount);
				g.fillOval(x, y, this.dataPointDiameter, this.dataPointDiameter);
				hueCount++;
			}
			
		}

		if (this.legendData.includeSizeInLegend()) {
			DrawString.setTextStyle(Color.BLACK, this.labelFont, 0);
			DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
			DrawString.write(g, this.legendData.getSizeLabel(), cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(), legendBottom + this.getLegendBottomToSizeLabelBottomHeight());
			
			String[] keys = this.legendData.getSizeData().keySet().toArray(new String[0]);
			Integer[] diameters = new Integer[keys.length];
			
			for (int i = 0; i < diameters.length; i++) {
				diameters[i] = this.legendData.getSizeData().get(keys[i]);
			}
			
			for (int count = 0; count < keys.length; count++) {
				DrawString.setTextStyle(Color.BLACK, this.valueFont, 0);
				DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
				DrawString.write(g, keys[count], cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeft(), legendBottom + getLegendBottomToSizeValueBottomHeight(count));
			}
			
		}
		
		
//		for (int i = 0; i < this.hueValues.length; i++) {
//
//			DrawString.setTextStyle(Color.BLACK, this.hueValueFont, 0);
//			DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
//			DrawString.write(g, this.hueValues[i], cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), legendBottom + getBottomLegendToHueValueBottomHeight(i));
//
//			int height = DrawString.getStringHeight(this.hueValues[i], this.hueValueFont);
//			
//			g.setColor(colors[i % colors.length]);
//			g.fillOval(cm.imageLeftToLegendLeftWidth() + legendLeftToDataPointWidth, legendBottom + getBottomLegendToHueValueBottomHeight(i) + height/2 - dataPointDiameter/2, dataPointDiameter, dataPointDiameter);
//		}
////		drawDebugLines(g,cm);

	}


//	private void drawDebugLines(Graphics2D g, XYChartMeasurements cm) {
//		g.setStroke(new BasicStroke(1));
//		g.setColor(Color.GRAY);
//		
//		int imageBottomToLegendBottom = cm.imageBottomToPlotMidHeight() - this.getLegendHeight()/2;
//		int imageBottomToLegendTop = cm.imageBottomToPlotMidHeight() + this.getLegendHeight()/2;
//		
//		g.drawLine(cm.imageLeftToLegendLeftWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth(), imageBottomToLegendTop);
//		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.legendLeftToDataPointWidth, imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.legendLeftToDataPointWidth, imageBottomToLegendTop);
//		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToDataPointRightWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToDataPointRightWidth(), imageBottomToLegendTop);
//		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), imageBottomToLegendTop);
//		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextRightWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextRightWidth(), imageBottomToLegendTop);
//		
//	}


	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean getIncludeLegend() {
		return includeLegend;
	}
	public void setIncludeLegend(boolean includeLegend) {
		this.includeLegend = includeLegend;
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
		return getLegendLeftToLargestDataPointLeftWidth() + 
			   this.largestDataPointDiameter/2;
	}
	
	public int getLegendLeftToLargestDataPointRightWidth() {
		return getLegendLeftToLargestDataPointLeftWidth() + 
			   this.largestDataPointDiameter;
	}
	
	public int getLegendLeftToTextLeft() {
		return getLegendLeftToLargestDataPointRightWidth() +
			   this.dataPointToTextWidth;
	}

	public int getLegendLeftToWidestTextRight() {
		return getLegendLeftToTextLeft() +
			   this.widestTextWidth;
	}
	
	public int getLegendWidth() {
		return getLegendLeftToWidestTextRight() +
			   this.textRightToLegendRightWidth;
	}

////	public int getimageBottomToLegendBottom() {
////		return cm.imageBottomToChartMidHeight() - this.getLegendHeight()/2;
////	}
	
	public int getLegendBottomToSizeValuesBottomHeight() {
		return this.legendBottomToBottomValue;
	}
	
	public int getLegendBottomToSizeValuesTopHeight() {
		int heightTotal = 0;
		Integer[] diameters = legendData.getSizeData().values().toArray(new Integer[0]);
		for (int count = 0; count < diameters.length; count++) {
			heightTotal += Integer.max(diameters[count], this.maxSizeValueHeight);
		}
		heightTotal += (diameters.length - 1) * this.valueSpacingHeight;
		return getLegendBottomToSizeValuesBottomHeight() + 
			   heightTotal;
	}
	
	public int getLegendBottomToSizeLabelBottomHeight() {
		return getLegendBottomToSizeValuesTopHeight() + 
			   this.valuesTopToLabelHeight;
	}
	
	public int getLegendBottomToSizeLabelMidHeight() {
		return getLegendBottomToSizeLabelBottomHeight() +
			   this.sizeLabelHeight/2;
	}
	
	public int getLegendBottomToSizeLabelTopHeight() {
		return getLegendBottomToSizeLabelBottomHeight() +
			   this.sizeLabelHeight;
	}
	
	public int getLegendBottomToHueValuesBottomHeight() {
		return getLegendBottomToSizeLabelTopHeight() +
			   this.topSizeLabelToBottomHueHeight;
	}
	
	public int getLegendBottomToHueValuesTopHeight() {
		int numHueValues = this.legendData.getColorData().keySet().size();
		return getLegendBottomToHueValuesBottomHeight() +
			   ((numHueValues - 1) * this.valueSpacingHeight) +
			   (numHueValues * Integer.max(this.dataPointDiameter, this.maxHueValueHeight));
	}
	
	public int getLegendBottomToHueLabelBottomHeight() {
		return getLegendBottomToHueValuesTopHeight() +
			   this.valuesTopToLabelHeight;
	}
	
	public int getLegendBottomToHueLabelMidHeight() {
		return getLegendBottomToHueLabelBottomHeight() +
			   this.hueLabelHeight/2;
	}
	
	public int getLegendBottomToHueLabelTopHeight() {
		return getLegendBottomToHueLabelBottomHeight() +
			   this.hueLabelHeight;
	}
	
	public int getLegendHeight() {
		return getLegendBottomToHueLabelTopHeight() +
			   this.topLabelToLegendTopHeight;
	}
	
	// i starts from the top
	public int getLegendBottomToSizeValueBottomHeight(int i) {
		HashMap<String, Integer> sizeData = this.legendData.getSizeData();
		int numSizeValues = sizeData.keySet().size();
		
		int baseHeight = getLegendBottomToBottomValue();
		
		Integer[] diameters = sizeData.values().toArray(new Integer[0]);
		Arrays.sort(diameters);
		
		int runningTotal = 0;
		
		for (int count = numSizeValues - 1; count >= i; count--) {
			runningTotal += Integer.max(diameters[count], this.maxSizeValueHeight);
		}

		runningTotal += this.valueSpacingHeight * (numSizeValues - i - 1);
		
		return baseHeight + runningTotal;
	}
	
	public int getLegendBottomToHueValueBottomHeight(int i) {
		return getLegendBottomToHueValuesBottomHeight() + 
			   ((this.legendData.getColorData().values().size() - 1 - i) * (this.valueSpacingHeight + this.maxHueValueHeight));
	}
	
	
}
