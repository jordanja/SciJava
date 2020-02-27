package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public abstract class XYChart extends Chart {

	protected DataItem[] xData;
	protected DataItem[] yData;
	
	protected XYChartMeasurements cm;
	
	
	public XYChart(DataFrame dataFrame, DataItem[] xData, DataItem[] yData, String chartType) {
		super(dataFrame, xData, yData, chartType);	
		this.xData = xData;
		this.yData = yData;
	}
	
	
	
	
	protected void drawXDebugLines(Graphics2D g, XYChartMeasurements cm) {
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.GRAY);
		g.drawLine(0, cm.imageBottomToBottomAxisLabelBottomHeight(), cm.imageWidth(), cm.imageBottomToBottomAxisLabelBottomHeight());
		g.drawLine(0, cm.imageBottomToBottomAxisLabelTopHeight(), cm.imageWidth(), cm.imageBottomToBottomAxisLabelTopHeight());
		g.drawLine(0, cm.imageBottomToBottomAxisBottomHeight(), cm.imageWidth(), cm.imageBottomToBottomAxisBottomHeight());
		g.drawLine(0, cm.imageBottomToBottomAxisTopHeight(), cm.imageWidth(), cm.imageBottomToBottomAxisTopHeight());
		g.drawLine(0, cm.imageBottomToBottomTicksEndHeight(), cm.imageWidth(), cm.imageBottomToBottomTicksEndHeight());
		g.drawLine(0, cm.imageBottomToPlotBottomHeight(), cm.imageWidth(), cm.imageBottomToPlotBottomHeight());
		g.drawLine(0, cm.imageBottomToPlotTopHeight(), cm.imageWidth(), cm.imageBottomToPlotTopHeight());
		g.drawLine(0, cm.imageBottomToTopAxisBottomHeight(), cm.imageWidth(), cm.imageBottomToTopAxisBottomHeight());
		g.drawLine(0, cm.imageBottomToTopAxisTopHeight(), cm.imageWidth(), cm.imageBottomToTopAxisTopHeight());
		g.drawLine(0, cm.imageBottomToTopAxisLabelBottomHeight(), cm.imageWidth(), cm.imageBottomToTopAxisLabelBottomHeight());
		g.drawLine(0, cm.imageBottomToTopAxisLabelTopHeight(), cm.imageWidth(), cm.imageBottomToTopAxisLabelTopHeight());
		g.drawLine(0, cm.imageBottomToTitleBottomHeight(), cm.imageWidth(), cm.imageBottomToTitleBottomHeight());
		g.drawLine(0, cm.imageBottomToTitleTopHeight(), cm.imageWidth(), cm.imageBottomToTitleTopHeight());
	}
	
	protected void drawYDebugLines(Graphics2D g, XYChartMeasurements cm) {
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.GRAY);
		g.drawLine(cm.imageLeftToAxisLabelLeftWidth(), 0, cm.imageLeftToAxisLabelLeftWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToLeftAxisLabelRightWidth(), 0, cm.imageLeftToLeftAxisLabelRightWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToLeftAxisLeftWidth(), 0, cm.imageLeftToLeftAxisLeftWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToLeftAxisRightWidth(), 0, cm.imageLeftToLeftAxisRightWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToLeftTicksEndWidth(), 0, cm.imageLeftToLeftTicksEndWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToPlotLeftWidth(), 0, cm.imageLeftToPlotLeftWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToPlotRightWidth(), 0, cm.imageLeftToPlotRightWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToRightTicksEndWidth(), 0, cm.imageLeftToRightTicksEndWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToRightAxisLeftWidth(), 0, cm.imageLeftToRightAxisLeftWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToRightAxisRightWidth(), 0, cm.imageLeftToRightAxisRightWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToRightAxisLabelLeftWidth(), 0, cm.imageLeftToRightAxisLabelLeftWidth(), cm.imageHeight());
		g.drawLine(cm.imageLeftToRightAxisLabelRightWidth(), 0, cm.imageLeftToRightAxisLabelRightWidth(), cm.imageHeight());
	}

}
