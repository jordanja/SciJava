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
	private String hueLabel;
	private String[] hueValues;
	
	private int hueLabelWidth;
	private int widestHueValueWidth;
	private int legendLeftToDataPointWidth = 10;
	private int dataPointDiameter = 10;
	private int dataPointToHueValueWidth = 8;
	private int textRightToLegendRightWidth = 10;
	private int bottomLegendToBottomHueValue = 10;
	private int hueValueHeight;
	private int hueValueSpacingHeight = 5;
	private int hueValuesTopToHueLabelHeight = 10;
	private int hueLabelHeight;
	private int hueLabelToLegendTopHeight = 10;
	
	private Font hueLabelFont = new Font("Dialog", Font.BOLD, 20);
	private Font hueValueFont = new Font("Dialog", Font.PLAIN, 20);

	private Color backgroundColor = Color.WHITE;
	
	private int numOfBubbleSizeValues = 3;
	
	/*
	 * 	[
	 *		{
	 *			"label": "color-label",
	 *			"type": "color",
	 *			"data": {
	 *				"label-1": Color.red,
	 *				"label-2": Color.blue,
	 *			}
	 *		},
	 *		{
	 *			"label": "size-label",
	 *			"type": "size",
	 *			"data": {
	 *				"smallest-value": radius,
	 *				"middle-value": radius,
	 *				"largest-value": radius,
	 *			}
	 *		}
	 * 	]
	 */
	
	public void calculateLegend(String hueLabel, Object[] hueValues) {
		this.includeLegend = true;
		this.hueLabel = hueLabel;
		this.hueValues = CommonArray.convertObjectArrayToStringArray(hueValues); 
		
		this.hueValueHeight = DrawString.maxHeightOfStringInList(this.hueValues, this.hueValueFont, 0);
		this.widestHueValueWidth = DrawString.maxWidthOfStringInList(this.hueValues, this.hueValueFont, 0);
				
		this.hueLabelHeight = DrawString.getStringHeight(hueLabel, this.hueLabelFont);
		this.hueLabelWidth = DrawString.getStringWidth(hueLabel, this.hueLabelFont);
		
	}
	
	public void drawLegend(Graphics2D g, ChartMeasurements cm, Color[] colors) {
		
		int legendBottom = cm.imageBottomToPlotMidHeight() - this.getLegendHeight()/2;

		if (this.backgroundColor != null) {
			g.setColor(this.backgroundColor);
			g.fillRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());
		}
		
		
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);
		
		g.drawRect(cm.imageLeftToLegendLeftWidth(), legendBottom, this.getLegendWidth(), this.getLegendHeight());
		
		DrawString.setTextStyle(Color.BLACK, this.hueLabelFont, 0);
		DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
		DrawString.write(g, this.hueLabel, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), legendBottom + this.getBottomlegentToHueLabelBottomHeight());

		
		for (int i = 0; i < this.hueValues.length; i++) {

			DrawString.setTextStyle(Color.BLACK, this.hueValueFont, 0);
			DrawString.setAlignment(DrawString.xAlignment.LeftAlign, DrawString.yAlignment.BottomAlign);
			DrawString.write(g, this.hueValues[i], cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), legendBottom + getBottomLegendToHueValueBottomHeight(i));

			int height = DrawString.getStringHeight(this.hueValues[i], this.hueValueFont);
			
			g.setColor(colors[i % colors.length]);
			g.fillOval(cm.imageLeftToLegendLeftWidth() + legendLeftToDataPointWidth, legendBottom + getBottomLegendToHueValueBottomHeight(i) + height/2 - dataPointDiameter/2, dataPointDiameter, dataPointDiameter);
		}
//		drawDebugLines(g,cm);

	}


	private void drawDebugLines(Graphics2D g, XYChartMeasurements cm) {
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.GRAY);
		
		int imageBottomToLegendBottom = cm.imageBottomToPlotMidHeight() - this.getLegendHeight()/2;
		int imageBottomToLegendTop = cm.imageBottomToPlotMidHeight() + this.getLegendHeight()/2;
		
		g.drawLine(cm.imageLeftToLegendLeftWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth(), imageBottomToLegendTop);
		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.legendLeftToDataPointWidth, imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.legendLeftToDataPointWidth, imageBottomToLegendTop);
		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToDataPointRightWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToDataPointRightWidth(), imageBottomToLegendTop);
		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextLeftWidth(), imageBottomToLegendTop);
		g.drawLine(cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextRightWidth(), imageBottomToLegendBottom, cm.imageLeftToLegendLeftWidth() + this.getLegendLeftToTextRightWidth(), imageBottomToLegendTop);
		
	}


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

	
	public String getHueLabel() {
		return hueLabel;
	}
	public void setHueLabel(String hueLabel) {
		this.hueLabel = hueLabel;
	}

	
	public String[] getHueValues() {
		return hueValues;
	}
	public void setHueValues(String[] hueValues) {
		this.hueValues = hueValues;
	}
	
	
	public int getHueValueSpacingHeight() {
		return hueValueSpacingHeight;
	}
	public void setHueValueSpacingHeight(int hueValueSpacingHeight) {
		this.hueValueSpacingHeight = hueValueSpacingHeight;
	}

	
	public int getDataPointToHueValueWidth() {
		return this.dataPointToHueValueWidth;
	}
	public void setDataPointToHueValueWidth(int dataPointToHueValueWidth) {
		this.dataPointToHueValueWidth = dataPointToHueValueWidth;
	}

	
	public int getLegendLeftToDataPointWidth() {
		return legendLeftToDataPointWidth;
	}
	public void setLegendLeftToDataPointWidth(int legendLeftToDataPointWidth) {
		this.legendLeftToDataPointWidth = legendLeftToDataPointWidth;
	}


	public int getTextRightToImageRightWidth() {
		return textRightToLegendRightWidth;
	}
	public void setTextRightToImageRightWidth(int textRightToImageRightWidth) {
		this.textRightToLegendRightWidth = textRightToImageRightWidth;
	}


	public int getDataPointDiameter() {
		return dataPointDiameter;
	}
	public void setDataPointDiameter(int dataPointDiameter) {
		this.dataPointDiameter = dataPointDiameter;
	}


	public int getBottomLegendToBottomHueValue() {
		return bottomLegendToBottomHueValue;
	}
	public void setBottomLegendToBottomHueValue(int bottomLegendToBottomHueValue) {
		this.bottomLegendToBottomHueValue = bottomLegendToBottomHueValue;
	}
	
	
	public int getHueValuesTopToHueLabelHeight() {
		return hueValuesTopToHueLabelHeight;
	}
	public void setHueValuesTopToHueLabelHeight(int hueValuesTopToHueLabelHeight) {
		this.hueValuesTopToHueLabelHeight = hueValuesTopToHueLabelHeight;
	}


	public int getHueLabelHeight() {
		return hueLabelHeight;
	}
	public void setHueLabelHeight(int hueLabelHeight) {
		this.hueLabelHeight = hueLabelHeight;
	}


	public int getHueLabelToLegendTopHeight() {
		return hueLabelToLegendTopHeight;
	}
	public void setHueLabelToLegendTopHeight(int hueLabelToLegendTopHeight) {
		this.hueLabelToLegendTopHeight = hueLabelToLegendTopHeight;
	}
	
	public Font getHueLabelFont() {
		return hueLabelFont;
	}
	public void setHueLabelFont(Font hueLabelFont) {
		this.hueLabelFont = hueLabelFont;
	}


	public Font getHueValueFont() {
		return hueValueFont;
	}
	public void setHueValueFont(Font hueValueFont) {
		this.hueValueFont = hueValueFont;
	}
	
	public int getLongestTextLength() {
		if (this.hueLabelWidth > this.widestHueValueWidth) {
			return this.hueLabelWidth;
		} else {
			return this.widestHueValueWidth;
		}
	}
	
//	public int getImageLeftToLegendLeft() {
//		
//	}
	
	public int getLegendLeftToDataPointMidWidth() {
		return getLegendLeftToDataPointWidth() + 
			   this.dataPointDiameter/2;
	}
	public int getLegendLeftToDataPointRightWidth() {
		return getLegendLeftToDataPointWidth() + 
			   this.dataPointDiameter;
	}
	public int getLegendLeftToTextLeftWidth() {
		return getLegendLeftToDataPointRightWidth() + 
			   this.dataPointToHueValueWidth;
	}
	public int getLegendLeftToTextRightWidth() {
		return getLegendLeftToTextLeftWidth() +
			   this.getLongestTextLength();
	}
	public int getLegendWidth() {
		return getLegendLeftToTextRightWidth() +
			   this.textRightToLegendRightWidth;
	}
	
	
//	public int getimageBottomToLegendBottom() {
//		return cm.imageBottomToChartMidHeight() - this.getLegendHeight()/2;
//	}
	
	public int getBottomLegendToTopHueValueHeight() {
		return getBottomLegendToBottomHueValue() + 
			   ((this.hueValues.length - 1) * this.hueValueSpacingHeight) + 
			   ((this.hueValues.length) * this.hueValueHeight);
	}
	public int getBottomlegentToHueLabelBottomHeight() {
		return getBottomLegendToTopHueValueHeight() + 
			   this.hueValuesTopToHueLabelHeight;
	}
	public int getBottomlegentToHueLabelMidHeight() {
		return getBottomlegentToHueLabelBottomHeight() + 
			   this.hueLabelHeight/2;
	}
	public int getBottomlegentToHueLabelTopHeight() {
		return getBottomlegentToHueLabelBottomHeight() + 
			   this.hueLabelHeight;
	}
	public int getLegendHeight() {
		return getBottomlegentToHueLabelTopHeight() +
			   this.hueLabelToLegendTopHeight;
	}
	
	public int getBottomLegendToHueValueBottomHeight(int i) {
		return getBottomLegendToBottomHueValue() + 
			   ((this.hueValues.length - 1 - i) * (this.hueValueSpacingHeight + this.hueValueHeight));
	}
	
	public int getBottomLegendToHueValueMidHeight(int i) {
		return getBottomLegendToHueValueBottomHeight(i) + 
			   this.hueValueHeight/2;
	}
	
	
	
}
