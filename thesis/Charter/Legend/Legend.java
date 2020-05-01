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
import java.util.HashSet;

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
	private int dataPointToValueWidth = 8;
	private int valuesRightToLegendRightWidth = 10;
	private int legendBottomToBottomValue = 10;
	private int valueSpacingHeight = 5;
	private int valuesTopToLabelHeight = 10;
	private int topSizeLabelToBottomHueHeight = 10;
	private int topLabelToLegendTopHeight = 10;
	
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
		
		if (this.legendData.includeSizeInLegend()) {
			this.sizeLabelWidth = DrawString.getStringWidth(this.legendData.getSizeLabel(), this.labelFont);
			this.sizeLabelHeight = DrawString.getStringHeight(this.legendData.getColorLabel(), this.labelFont);
			
			String[] sizeValues = this.legendData.getSizeData().keySet().toArray(new String[0]);
			this.widestSizeValueWidth = DrawString.maxWidthOfStringInList(sizeValues, this.valueFont, 0);
			this.maxSizeValueHeight = DrawString.maxHeightOfStringInList(sizeValues, this.valueFont, 0);
			this.largestDataPointDiameter = CommonArray.maxValue(legendData.getSizeData().values().toArray(new Integer[0]));
		} else {
			this.sizeLabelWidth = 0;
			this.sizeLabelHeight = 0;
			this.widestSizeValueWidth = 0;
			this.maxSizeValueHeight = 0;
			this.largestDataPointDiameter = 0;
		}
		
		
//		this.hueLabel = hueLabel;
//		this.hueValues = CommonArray.convertObjectArrayToStringArray(hueValues); 
//		
//		this.hueValueHeight = DrawString.maxHeightOfStringInList(this.hueValues, this.hueValueFont, 0);
//		this.widestHueValueWidth = DrawString.maxWidthOfStringInList(this.hueValues, this.hueValueFont, 0);
//				
//		this.hueLabelHeight = DrawString.getStringHeight(hueLabel, this.hueLabelFont);
//		this.hueLabelWidth = DrawString.getStringWidth(hueLabel, this.hueLabelFont);
		
	}
	
	public void drawLegend(Graphics2D g, ChartMeasurements cm, Color[] colors) {
		
//		int legendBottom = cm.imageBottomToPlotMidHeight() - this.getLegendHeight()/2;
//
//		if (this.backgroundColor != null) {
//			g.setColor(this.backgroundColor);
//			g.fillRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());
//		}
//		
//		
//		g.setStroke(new BasicStroke(1));
//		g.setColor(Color.BLACK);
//		
//		g.drawRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());
//		
//		DrawString.setTextStyle(Color.BLACK, this.hueLabelFont, 0);
//		DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
//		DrawString.write(g, this.hueLabel, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), legendBottom + this.getBottomlegentToHueLabelBottomHeight());
//
//		
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

	public int getDataPointToValueWidth() {
		return dataPointToValueWidth;
	}

	public void setDataPointToValueWidth(int dataPointToValueWidth) {
		this.dataPointToValueWidth = dataPointToValueWidth;
	}

	public int getValuesRightToLegendRightWidth() {
		return valuesRightToLegendRightWidth;
	}

	public void setValuesRightToLegendRightWidth(int valuesRightToLegendRightWidth) {
		this.valuesRightToLegendRightWidth = valuesRightToLegendRightWidth;
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

	//	public int getTextRightToImageRightWidth() {
//		return textRightToLegendRightWidth;
//	}
//	public void setTextRightToImageRightWidth(int textRightToImageRightWidth) {
//		this.textRightToLegendRightWidth = textRightToImageRightWidth;
//	}
//
//
//	public int getDataPointDiameter() {
//		return dataPointDiameter;
//	}
//	public void setDataPointDiameter(int dataPointDiameter) {
//		this.dataPointDiameter = dataPointDiameter;
//	}
//
//
//	public int getBottomLegendToBottomHueValue() {
//		return bottomLegendToBottomHueValue;
//	}
//	public void setBottomLegendToBottomHueValue(int bottomLegendToBottomHueValue) {
//		this.bottomLegendToBottomHueValue = bottomLegendToBottomHueValue;
//	}
//	
//	
//	public int getHueValuesTopToHueLabelHeight() {
//		return hueValuesTopToHueLabelHeight;
//	}
//	public void setHueValuesTopToHueLabelHeight(int hueValuesTopToHueLabelHeight) {
//		this.hueValuesTopToHueLabelHeight = hueValuesTopToHueLabelHeight;
//	}
//
//
//	public int getHueLabelHeight() {
//		return hueLabelHeight;
//	}
//	public void setHueLabelHeight(int hueLabelHeight) {
//		this.hueLabelHeight = hueLabelHeight;
//	}
//
//
//	public int getHueLabelToLegendTopHeight() {
//		return hueLabelToLegendTopHeight;
//	}
//	public void setHueLabelToLegendTopHeight(int hueLabelToLegendTopHeight) {
//		this.hueLabelToLegendTopHeight = hueLabelToLegendTopHeight;
//	}
//	
//	public Font getHueLabelFont() {
//		return hueLabelFont;
//	}
//	public void setHueLabelFont(Font hueLabelFont) {
//		this.hueLabelFont = hueLabelFont;
//	}
//
//
//	public Font getHueValueFont() {
//		return hueValueFont;
//	}
//	public void setHueValueFont(Font hueValueFont) {
//		this.hueValueFont = hueValueFont;
//	}
//	
//	public int getLongestTextLength() {
//		if (this.hueLabelWidth > this.widestHueValueWidth) {
//			return this.hueLabelWidth;
//		} else {
//			return this.widestHueValueWidth;
//		}
//	}
//	
////	public int getImageLeftToLegendLeft() {
////		
////	}
//	
//	public int getLegendLeftToDataPointMidWidth() {
//		return getLegendLeftToDataPointWidth() + 
//			   this.dataPointDiameter/2;
//	}
//	public int getLegendLeftToDataPointRightWidth() {
//		return getLegendLeftToDataPointWidth() + 
//			   this.dataPointDiameter;
//	}
//	public int getLegendLeftToTextLeftWidth() {
//		return getLegendLeftToDataPointRightWidth() + 
//			   this.dataPointToHueValueWidth;
//	}
//	public int getLegendLeftToTextRightWidth() {
//		return getLegendLeftToTextLeftWidth() +
//			   this.getLongestTextLength();
//	}
	public int getLegendWidth() {
		return 100;
//		return getLegendLeftToTextRightWidth() +
//			   this.textRightToLegendRightWidth;
	}
//	
//	
////	public int getimageBottomToLegendBottom() {
////		return cm.imageBottomToChartMidHeight() - this.getLegendHeight()/2;
////	}
//	
//	public int getBottomLegendToTopHueValueHeight() {
//		return getBottomLegendToBottomHueValue() + 
//			   ((this.hueValues.length - 1) * this.hueValueSpacingHeight) + 
//			   ((this.hueValues.length) * this.hueValueHeight);
//	}
//	public int getBottomlegentToHueLabelBottomHeight() {
//		return getBottomLegendToTopHueValueHeight() + 
//			   this.hueValuesTopToHueLabelHeight;
//	}
//	public int getBottomlegentToHueLabelMidHeight() {
//		return getBottomlegentToHueLabelBottomHeight() + 
//			   this.hueLabelHeight/2;
//	}
//	public int getBottomlegentToHueLabelTopHeight() {
//		return getBottomlegentToHueLabelBottomHeight() + 
//			   this.hueLabelHeight;
//	}
//	public int getLegendHeight() {
//		return getBottomlegentToHueLabelTopHeight() +
//			   this.hueLabelToLegendTopHeight;
//	}
//	
//	public int getBottomLegendToHueValueBottomHeight(int i) {
//		return getBottomLegendToBottomHueValue() + 
//			   ((this.hueValues.length - 1 - i) * (this.hueValueSpacingHeight + this.hueValueHeight));
//	}
//	
//	public int getBottomLegendToHueValueMidHeight(int i) {
//		return getBottomLegendToHueValueBottomHeight(i) + 
//			   this.hueValueHeight/2;
//	}
	
	
	
}
