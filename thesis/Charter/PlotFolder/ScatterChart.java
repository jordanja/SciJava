package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import thesis.Auxiliary.MathHelpers;
import thesis.Auxiliary.NiceScale;

import java.awt.Font;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.AxisFactory;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.ScatterChartMeasurements;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.*;
import thesis.Helpers.Palette;

public class ScatterChart extends XYChart {

	
	protected NumericAxis axis;
	protected ScatterPlot plot;
	protected Legend legend;

	private String colorCodeLabel;
	private Object[] colorCodeValues; 
		
	public ScatterChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis),"Scatter");
		
		
		
		this.axis = (NumericAxis) AxisFactory.getAxis("Scatter");
		this.plot = (ScatterPlot) PlotFactory.getPlot("Scatter");
		this.legend = new Legend();
		
		cm = new ScatterChartMeasurements();


	}
	
	public void setXAxis(String xAxis) {
		this.xData = this.dataFrame.GetColumnAsArray(xAxis);
	}
	
	public void setYAxis(String yAxis) {
		this.yData = this.dataFrame.GetColumnAsArray(yAxis);
	}
	
	
	public void setIncludeLegend(boolean includeLegend) {
		this.legend.setIncludeLegend(includeLegend);
	}
	
	// If using native Java data structures
	public void setXAxis(Number[] xData) {
		this.xData = DataItem.convertToDataItemList(xData);
	}
	
	public void setXAxis(List<Number> xData) {
		this.xData =  DataItem.convertToDataItemList(xData.toArray(new Number[xData.size()]));
	}
	
	public void setYAxis(Number[] yData) {
		this.yData = DataItem.convertToDataItemList(yData);
	}
	
	public void setYAxis(List<Number> yData) {
		this.xData = DataItem.convertToDataItemList(yData.toArray(new Number[yData.size()]));
	}
	
	public void colorCode(String colorCodeLabel) {
		this.colorCodeLabel = colorCodeLabel;
		this.colorCodeValues = this.dataFrame.GetColumnAsStringArray(this.colorCodeLabel);	
		this.legend.setIncludeLegend(true);
	}
	public void colorCode(Object[] colorCodeData) {
		this.colorCodeValues = colorCodeData;
		this.legend.setIncludeLegend(true);

	}

	public void colorCode(List<Object> colorCodeData) {
		this.colorCodeValues = colorCodeData.toArray();
		this.legend.setIncludeLegend(true);

	}
	
	public void Create() {
		this.axis.calculateXAxis(MathHelpers.minimumValue(xData), MathHelpers.maximumValue(xData));
		this.axis.calculateYAxis(MathHelpers.minimumValue(yData), MathHelpers.maximumValue(yData));
		
		if (this.legend.getIncludeLegend()) {
			this.legend.calculateLegend(this.colorCodeLabel, this.colorCodeValues);
		}
		
		cm.calculateChartImageMetrics(this.axis, this.plot, this.legend, getTitle(), getTitleFont());
		
		
		instantiateChart(cm);
		Graphics2D g = initializaGraphicsObject(cm);
		
		drawBackground(g, cm);

		this.plot.drawChartBackground(g, cm);
		
		this.axis.drawAxis(g, cm);
		
		
		this.plot.drawLinearRegression(g, this.axis, xData, yData, cm);
		
		
		this.plot.drawPlotOutline(g, cm);
		
		this.axis.drawAxisTicks(g, cm);
		
		this.plot.drawPlot(g, this.axis, xData, yData, colorCodeValues, cm);
		
		this.axis.drawXAxisLabel(g, cm);
		this.axis.drawYAxisLabel(g, cm);
		
		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, cm, this.plot.getColorPalette());
		}
		
		this.drawTitle(g, cm);
		
//		this.drawXDebugLines(g, cm);
//		this.drawYDebugLines(g, cm);
		
		
		
		g.dispose();
	}

	public Axis getAxis() {
		return this.axis;
	}
	
	
	public Plot getPlot() {
		return this.plot;
	}
	
	public Legend getLegend() {
		return this.legend;
	}
	

	public XYChartMeasurements getChartMeasurements() {
		return this.cm;
	}
	
	
	
	
	
	

	
	
	
}



