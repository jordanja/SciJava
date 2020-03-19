package thesis.Charter.Others;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.PlotFolder.Plot;
import thesis.Charter.PlotFolder.ScatterPlot;
import thesis.Charter.StringDrawer.DrawString;

public class XYChartMeasurements {
	protected int imageBottomToBottomAxisLabelHeight = 5;
	protected int bottomAxisLabelHeight;
	protected int bottomAxisLabelToBottomAxisHeight = 5;
	protected int bottomAxisHeight;
	protected int bottomAxisToBottomTicksHeight = 5;
	protected int bottomTicksHeight = 5;
	protected int plotHeight = 400;
	protected int topTicksHeight = 5;
	protected int topTicksToTopAxisHeight = 5;
	protected int topAxisHeight;
	protected int topAxisToTopAxisLabelHeight = 5;
	protected int topAxisLabelHeight;
	protected int topAxisLabelToTitleHeight = 5;
	protected int titleHeight;
	protected int titleToImageTopHeight = 5;

	protected int imageLeftToLeftAxisLabelWidth = 5;
	protected int leftAxisLabelWidth;
	protected int leftAxisLabelToLeftAxisWidth = 5;
	protected int leftAxisWidth;
	protected int leftAxisToLeftTicksWidth = 5;
	protected int leftTicksWidth = 5;
	protected int plotWidth = 400;
	protected int rightTicksWidth = 5;
	protected int rightTicksToRightAxisWidth = 5;
	protected int rightAxisWidth;
	protected int rightAxisToRightAxisLabelWidth = 5;
	protected int rightAxisLabelWidth;
	protected int rightAxisLabelToLegendWidth = 5;
	protected int legendWidth;
	protected int legendToImageRightWidth = 5;


	public int imageBottomToBottomAxisLabelBottomHeight() {
		return this.imageBottomToBottomAxisLabelHeight;
	}

	public int imageBottomToBottomAxisLabelMidHeight() {
		return imageBottomToBottomAxisLabelBottomHeight() + this.bottomAxisLabelHeight / 2;
	}

	public int imageBottomToBottomAxisLabelTopHeight() {
		return imageBottomToBottomAxisLabelBottomHeight() + this.bottomAxisLabelHeight;
	}

	public int imageBottomToBottomAxisBottomHeight() {
		return imageBottomToBottomAxisLabelTopHeight() + this.bottomAxisLabelToBottomAxisHeight;
	}

	public int imageBottomToBottomAxisMidHeight() {
		return imageBottomToBottomAxisBottomHeight() + this.bottomAxisHeight / 2;
	}

	public int imageBottomToBottomAxisTopHeight() {
		return imageBottomToBottomAxisBottomHeight() + this.bottomAxisHeight;
	}

	public int imageBottomToBottomTicksEndHeight() {
		return imageBottomToBottomAxisTopHeight() + this.bottomAxisToBottomTicksHeight;
	}

	public int imageBottomToPlotBottomHeight() {
		return imageBottomToBottomTicksEndHeight() + this.bottomTicksHeight;
	}

	public int imageBottomToPlotMidHeight() {
		return imageBottomToPlotBottomHeight() + this.plotHeight / 2;
	}

	public int imageBottomToPlotTopHeight() {
		return imageBottomToPlotBottomHeight() + this.plotHeight;
	}

	public int imageBottomToTopTicksEndHeight() {
		return imageBottomToPlotTopHeight() + this.topTicksHeight;
	}

	public int imageBottomToTopAxisBottomHeight() {
		return imageBottomToTopTicksEndHeight() + this.topTicksToTopAxisHeight;
	}

	public int imageBottomToTopAxisMidHeight() {
		return imageBottomToTopAxisBottomHeight() + this.topAxisHeight / 2;
	}

	public int imageBottomToTopAxisTopHeight() {
		return imageBottomToTopAxisBottomHeight() + this.topAxisHeight;
	}

	public int imageBottomToTopAxisLabelBottomHeight() {
		return imageBottomToTopAxisTopHeight() + topAxisToTopAxisLabelHeight;
	}

	public int imageBottomToTopAxisLabelMidHeight() {
		return imageBottomToTopAxisLabelBottomHeight() + this.topAxisLabelHeight / 2;
	}

	public int imageBottomToTopAxisLabelTopHeight() {
		return imageBottomToTopAxisLabelBottomHeight() + this.topAxisLabelHeight;
	}

	public int imageBottomToTitleBottomHeight() {
		return imageBottomToTopAxisLabelTopHeight() + this.topAxisLabelToTitleHeight;
	}

	public int imageBottomToTitleMidHeight() {
		return imageBottomToTitleBottomHeight() + this.titleHeight / 2;
	}

	public int imageBottomToTitleTopHeight() {
		return imageBottomToTitleBottomHeight() + this.titleHeight;
	}

	public int imageHeight() {
		return imageBottomToTitleTopHeight() + this.titleToImageTopHeight;
	}

	public int imageLeftToAxisLabelLeftWidth() {
		return this.imageLeftToLeftAxisLabelWidth;
	}

	public int imageLeftToLeftAxisLabelMidWidth() {
		return imageLeftToAxisLabelLeftWidth() + this.leftAxisLabelWidth / 2;
	}

	public int imageLeftToLeftAxisLabelRightWidth() {
		return imageLeftToAxisLabelLeftWidth() + this.leftAxisLabelWidth;
	}

	public int imageLeftToLeftAxisLeftWidth() {
		return imageLeftToLeftAxisLabelRightWidth() + leftAxisLabelToLeftAxisWidth;
	}

	public int imageLeftToLeftAxisMidWidth() {
		return imageLeftToLeftAxisLeftWidth() + this.leftAxisWidth / 2;
	}

	public int imageLeftToLeftAxisRightWidth() {
		return imageLeftToLeftAxisLeftWidth() + this.leftAxisWidth;
	}

	public int imageLeftToLeftTicksEndWidth() {
		return imageLeftToLeftAxisRightWidth() + this.leftAxisToLeftTicksWidth;
	}

	public int imageLeftToPlotLeftWidth() {
		return imageLeftToLeftTicksEndWidth() + this.leftTicksWidth;
	}

	public int imageLeftToPlotMidWidth() {
		return imageLeftToPlotLeftWidth() + this.plotWidth / 2;
	}

	public int imageLeftToPlotRightWidth() {
		return imageLeftToPlotLeftWidth() + this.plotWidth;
	}

	public int imageLeftToRightTicksEndWidth() {
		return imageLeftToPlotRightWidth() + this.rightTicksWidth;
	}

	public int imageLeftToRightAxisLeftWidth() {
		return imageLeftToRightTicksEndWidth() + rightTicksToRightAxisWidth;
	}

	public int imageLeftToRightAxisMidWidth() {
		return imageLeftToRightAxisLeftWidth() + this.rightAxisWidth / 2;
	}

	public int imageLeftToRightAxisRightWidth() {
		return imageLeftToRightAxisLeftWidth() + this.rightAxisWidth;
	}

	public int imageLeftToRightAxisLabelLeftWidth() {
		return imageLeftToRightAxisRightWidth() + rightAxisToRightAxisLabelWidth;
	}

	public int imageLeftToRightAxisLabelMidWidth() {
		return imageLeftToRightAxisLabelLeftWidth() + this.rightAxisLabelWidth / 2;
	}

	public int imageLeftToRightAxisLabelRightWidth() {
		return imageLeftToRightAxisLabelLeftWidth() + this.rightAxisLabelWidth;
	}

	public int imageLeftToLegendLeftWidth() {
		return imageLeftToRightAxisLabelRightWidth() + this.rightAxisLabelToLegendWidth;
	}

	public int imageLeftToLegendMidWidth() {
		return imageLeftToLegendLeftWidth() + this.legendWidth / 2;
	}

	public int imageLeftToLegendRightWidth() {
		return imageLeftToLegendLeftWidth() + this.legendWidth;
	}

	public int imageWidth() {
		return imageLeftToLegendRightWidth() + this.legendToImageRightWidth;
	}

	public int getImageBottomToBottomAxisLabelHeight() {
		return imageBottomToBottomAxisLabelHeight;
	}

	public void setImageBottomToBottomAxisLabelHeight(int imageBottomToBottomAxisLabelHeight) {
		this.imageBottomToBottomAxisLabelHeight = imageBottomToBottomAxisLabelHeight;
	}

	public int getBottomAxisLabelToBottomAxisHeight() {
		return bottomAxisLabelToBottomAxisHeight;
	}

	public void setBottomAxisLabelToBottomAxisHeight(int bottomAxisLabelToBottomAxisHeight) {
		this.bottomAxisLabelToBottomAxisHeight = bottomAxisLabelToBottomAxisHeight;
	}

	public int getBottomAxisToBottomTicksHeight() {
		return bottomAxisToBottomTicksHeight;
	}

	public void setBottomAxisToBottomTicksHeight(int bottomAxisToBottomTicksHeight) {
		this.bottomAxisToBottomTicksHeight = bottomAxisToBottomTicksHeight;
	}

	public int getBottomTicksHeight() {
		return this.bottomTicksHeight;
	}

	public void setBottomTicksHeight(int bottomTicksHeight) {
		this.bottomTicksHeight = bottomTicksHeight;
	}

	public int getPlotHeight() {
		return this.plotHeight;
	}

	public void setPlotHeight(int plotHeight) {
		this.plotHeight = plotHeight;
	}

	public int getTopTicksHeight() {
		return this.topTicksHeight;
	}

	public void setTopTicksHeight(int topTicksHeight) {
		this.topTicksHeight = topTicksHeight;
	}

	public int getTopTicksToTopAxisHeight() {
		return topTicksToTopAxisHeight;
	}

	public void setTopTicksToTopAxisHeight(int topTicksToTopAxisHeight) {
		this.topTicksToTopAxisHeight = topTicksToTopAxisHeight;
	}

	public int getTopAxisToTopAxisLabelHeight() {
		return topAxisToTopAxisLabelHeight;
	}

	public void setTopAxisToTopAxisLabelHeight(int topAxisToTopAxisLabelHeight) {
		this.topAxisToTopAxisLabelHeight = topAxisToTopAxisLabelHeight;
	}

	public int getTopAxisLabelToTitleHeight() {
		return topAxisLabelToTitleHeight;
	}

	public void setTopAxisLabelToTitleHeight(int topAxisLabelToTitleHeight) {
		this.topAxisLabelToTitleHeight = topAxisLabelToTitleHeight;
	}

	public int getTitleToImageTopHeight() {
		return titleToImageTopHeight;
	}

	public void setTitleToImageTopHeight(int titleToImageTopHeight) {
		this.titleToImageTopHeight = titleToImageTopHeight;
	}

	public int getImageLeftToLeftAxisLabelWidth() {
		return imageLeftToLeftAxisLabelWidth;
	}

	public void setImageLeftToLeftAxisLabelWidth(int imageLeftToLeftAxisLabelWidth) {
		this.imageLeftToLeftAxisLabelWidth = imageLeftToLeftAxisLabelWidth;
	}

	public int getLeftAxisLabelToLeftAxisWidth() {
		return leftAxisLabelToLeftAxisWidth;
	}

	public void setLeftAxisLabelToLeftAxisWidth(int leftAxisLabelToLeftAxisWidth) {
		this.leftAxisLabelToLeftAxisWidth = leftAxisLabelToLeftAxisWidth;
	}

	public int getLeftAxisToLeftTicksWidth() {
		return leftAxisToLeftTicksWidth;
	}

	public void setLeftAxisToLeftTicksWidth(int leftAxisToLeftTicksWidth) {
		this.leftAxisToLeftTicksWidth = leftAxisToLeftTicksWidth;
	}

	public int getLeftTicksWidth() {
		return this.leftTicksWidth;
	}

	public void setLeftTicksWidth(int leftTicksWidth) {
		this.leftTicksWidth = leftTicksWidth;
	}

	public int getPlotWidth() {
		return this.plotWidth;
	}

	public void setPlotWidth(int plotWidth) {
		this.plotWidth = plotWidth;
	}

	public int getRightTicksWidth() {
		return rightTicksWidth;
	}

	public void setRightTicksWidth(int rightTicksWidth) {
		this.rightTicksWidth = rightTicksWidth;
	}

	public int getRightTicksToRightAxisWidth() {
		return rightTicksToRightAxisWidth;
	}

	public void setRightTicksToRightAxisWidth(int rightTicksToRightAxisWidth) {
		this.rightTicksToRightAxisWidth = rightTicksToRightAxisWidth;
	}

	public int getRightAxisToRightAxisLabelWidth() {
		return rightAxisToRightAxisLabelWidth;
	}

	public void setRightAxisToRightAxisLabelWidth(int rightAxisToRightAxisLabelWidth) {
		this.rightAxisToRightAxisLabelWidth = rightAxisToRightAxisLabelWidth;
	}

	public int getRightAxisLabelToLegendWidth() {
		return rightAxisLabelToLegendWidth;
	}

	public void setRightAxisLabelToLegendWidth(int rightAxisLabelToImageRightWidth) {
		this.rightAxisLabelToLegendWidth = rightAxisLabelToImageRightWidth;
	}

	public int getLegendToImageRightWidth() {
		return this.legendToImageRightWidth;
	}

	public void setLegendToImageRightWidth(int legendToImageRight) {
		this.legendToImageRightWidth = legendToImageRight;
	}

	public void setTickLengths(int length) {
		this.bottomTicksHeight = length;
		this.topTicksHeight = length;
		this.leftTicksWidth = length;
		this.rightTicksWidth = length;
	}

	public void calculateChartImageMetrics(Axis axis, Legend legend, String title, Font titleFont) {
		
		String[] xTicks = axis.getXTicksFormattedForDisplay();
		String[] yTicks = axis.getYTicksFormattedForDisplay();

		double heightOfXAxis = DrawString.maxHeightOfStringInList(xTicks, axis.getXAxisFont(), axis.getXAxisRotation());
		double widthOfYAxis = DrawString.maxWidthOfStringInList(yTicks, axis.getYAxisFont(), axis.getYAxisRotation());

		if ((axis.drawBottomXLabel())) {
			this.bottomAxisLabelHeight = DrawString.getStringHeight(axis.getXAxisLabel(), axis.getXAxisLabelFont());
		} else {
			this.bottomAxisLabelHeight = 0;
			this.bottomAxisLabelToBottomAxisHeight = 0;
		}

		if (axis.drawBottomXAxisValues()) {
			this.bottomAxisHeight = (int) Math.ceil(heightOfXAxis);
		} else {
			this.bottomAxisHeight = 0;
			this.bottomAxisToBottomTicksHeight = 0;

		}

		if (!axis.drawExteriorBottomXAxisTicks()) {
			this.bottomTicksHeight = 0;
		}

		if (!axis.drawExteriorTopXAxisTicks()) {
			this.topTicksHeight = 0;
		}

		if (axis.drawTopXAxisValues()) {
			this.topAxisHeight = (int) Math.ceil(heightOfXAxis);
		} else {
			this.topAxisHeight = 0;
			this.topTicksToTopAxisHeight = 0;
		}

		if (axis.drawTopXLabel()) {
			this.topAxisLabelHeight = DrawString.getStringHeight(axis.getXAxisLabel(), axis.getXAxisLabelFont());

		} else {
			this.topAxisLabelHeight = 0;
			this.topAxisToTopAxisLabelHeight = 0;

		}

		if (title != null) {
			this.titleHeight = DrawString.getStringHeight(title, titleFont);
		} else {
			this.titleHeight = 0;
			this.topAxisLabelToTitleHeight = 0;

		}

		if ((axis.drawLeftYLabel())) {
			this.leftAxisLabelWidth = DrawString.getStringHeight(axis.getYAxisLabel(), axis.getYAxisLabelFont());

		} else {
			this.leftAxisLabelWidth = 0;
			this.leftAxisLabelToLeftAxisWidth = 0;

		}

		if (axis.drawLeftYAxisValues()) {
			this.leftAxisWidth = (int) Math.ceil(widthOfYAxis);
		} else {
			this.leftAxisWidth = 0;
			this.leftAxisToLeftTicksWidth = 0;
		}

		if (!axis.drawExteriorLeftYAxisTicks()) {
			this.leftTicksWidth = 0;
		}

		if (!axis.drawExteriorRightYAxisTicks()) {
			this.rightTicksWidth = 0;
		}

		if (axis.drawRightYAxisValues()) {
			this.rightAxisWidth = (int) Math.ceil(widthOfYAxis);
		} else {
			this.rightAxisWidth = 0;
			this.rightTicksToRightAxisWidth = 0;
		}

		if (axis.drawRightYLabel()) {
			this.rightAxisLabelWidth = DrawString.getStringHeight(axis.getYAxisLabel(), axis.getYAxisLabelFont());
		} else {
			this.rightAxisToRightAxisLabelWidth = 0;
			this.rightAxisLabelWidth = 0;
		}

		if (legend.getIncludeLegend()) {
			this.legendWidth = legend.getLegendWidth();
		} else {
			this.legendWidth = 0;
			this.rightAxisLabelToLegendWidth = 0;
		}

	}

}
