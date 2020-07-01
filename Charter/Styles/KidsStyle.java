package thesis.Charter.Styles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import thesis.Helpers.Palette;

public class KidsStyle extends Style implements 
	PlotStyle, NumericAxisStyle, XYChartMeasurementsStyle, ChartStyle, CategoricalLegendSyle {
	
	// Plot
	private static final boolean drawBottomPlotOutline = true; 
	private static final boolean drawLeftPlotOutline = true;
	private static final boolean drawTopPlotOutline = true;
	private static final boolean drawRightPlotOutline = true;

	private static final Color bottomPlotOutlineColor = Color.RED;
	private static final Color leftPlotOutlineColor = Color.BLUE;
	private static final Color topPlotOutlineColor = Color.GREEN;
	private static final Color rightPlotOutlineColor = Color.MAGENTA;

	private static final int bottomPlotOutlineWidth = 3;
	private static final int leftPlotOutlineWidth = 2;
	private static final int topPlotOutlineWidth = 4;
	private static final int rightPlotOutlineWidth = 1;

	private static final Color plotBackgroundColor = Color.WHITE;
	private static final Image plotBackgroundImage = null;

	private static final Color[] colorPalette = Palette.Matplotlib;

	// Axis
	private static final float xAxisRotation = 45;
	private static final float yAxisRotation = 45;

	private static final boolean drawBottomXLabel = true;
	private static final boolean drawTopXLabel = true;
	private static final boolean drawLeftYLabel = true;
	private static final boolean drawRightYLabel = true;

	private static final boolean drawBottomXAxisValues = true;
	private static final boolean drawTopXAxisValues = true;
	private static final boolean drawLeftYAxisValues = true;
	private static final boolean drawRightYAxisValues = true;

	private static final boolean drawExteriorBottomXAxisTicks = true;
	private static final boolean drawExteriorTopXAxisTicks = true;
	private static final boolean drawExteriorLeftYAxisTicks = true;
	private static final boolean drawExteriorRightYAxisTicks = true;

	private static final boolean drawInteriorBottomXAxisTicks = true;
	private static final boolean drawInteriorTopXAxisTicks = true;
	private static final boolean drawInteriorLeftYAxisTicks = true;
	private static final boolean drawInteriorRightYAxisTicks = true;

	private static final Color bottomTickColor = Color.BLUE;
	private static final Color leftTickColor = Color.PINK;
	private static final Color topTickColor = Color.ORANGE;
	private static final Color rightTickColor = Color.YELLOW;

	private static final int interiorBottomTickThickness = 1;
	private static final int interiorTopTickThickness = 2;
	private static final int interiorLeftTickThickness = 3;
	private static final int interiorRightTickThickness = 4;

	private static final int exteriorBottomTickThickness = 4;
	private static final int exteriorTopTickThickness = 3;
	private static final int exteriorLeftTickThickness = 2;
	private static final int exteriorRightTickThickness = 1;

	private static final Font xAxisFont = new Font("Dialog", Font.PLAIN, 24);
	private static final Font yAxisFont = new Font("Dialog", Font.PLAIN, 30);
	private static final Font xAxisLabelFont = new Font("Dialog", Font.PLAIN, 20);
	private static final Font yAxisLabelFont = new Font("Dialog", Font.PLAIN, 12);

	private static final Color xAxisColor = Color.RED;
	private static final Color yAxisColor = Color.GREEN;
	private static final Color xAxisLabelColor = Color.BLUE;
	private static final Color yAxisLabelColor = Color.ORANGE;
	
	private static final boolean includeXAxisLinesOnPlot = true;
	private static final Color xAxisLinesOnPlotColor = Color.RED;
	private static final int xAxisLinesOnPlotWidth = 1;
	
	private static final boolean includeYAxisLinesOnPlot = true;
	private static final Color yAxisLinesOnPlotColor = Color.CYAN;
	private static final int yAxisLinesOnPlotWidth = 1;
	
	// Chart Measurements
	private static final int getBottomTickHeight = 20;
	private static final int getLeftTickWidth = 15;
	private static final int getTopTickHeight = 10;
	private static final int getRightTickWidth = 5;
	
	// Chart
	private static final Font titleFont = new Font("Dialog", Font.BOLD, 40);
	private static final Color titleColor = Color.RED;
	private static final Color chartBackgroundColor = Color.LIGHT_GRAY;
	
	// Legend
	private static final boolean drawlegendOutline = true;
	private static final Color legendOutlineColor = Color.CYAN;
	private static final int legendOutlineWidth = 1;
	private static final Color legendTextColor = Color.RED;
	private static final Color legendBackgroundColor = Color.YELLOW;
	
	// Box Plot
	private static final Color boxPlotOutlineColor = Color.BLACK;
	
	// Plot
	public boolean getDrawBottomPlotOutline() {
		return drawBottomPlotOutline;
	}

	public boolean getDrawLeftPlotOutline() {
		return drawLeftPlotOutline;
	}

	public boolean getDrawTopPlotOutline() {
		return drawTopPlotOutline;
	}

	public boolean getDrawRightPlotOutline() {
		return drawRightPlotOutline;
	}

	public Color getBottomPlotOutlineColor() {
		return bottomPlotOutlineColor;
	}

	public Color getLeftPlotOutlineColor() {
		return leftPlotOutlineColor;
	}

	public Color getTopPlotOutlineColor() {
		return topPlotOutlineColor;
	}

	public Color getRightPlotOutlineColor() {
		return rightPlotOutlineColor;
	}

	public int getBottomPlotOutlineWidth() {
		return bottomPlotOutlineWidth;
	}

	public int getLeftPlotOutlineWidth() {
		return leftPlotOutlineWidth;
	}

	public int getTopPlotOutlineWidth() {
		return topPlotOutlineWidth;
	}

	public int getRightPlotOutlineWidth() {
		return rightPlotOutlineWidth;
	}

	public Color getPlotBackgroundColor() {
		return plotBackgroundColor;
	}

	public Image getPlotBackgroundImage() {
		return plotBackgroundImage;
	}

	public Color[] getColorPalette() {
		return colorPalette;
	}
	
	// Axis
	public float getXAxisRotation() {
		return xAxisRotation;
	}

	public float getYAxisRotation() {
		return yAxisRotation;
	}

	public boolean getDrawBottomXLabel() {
		return drawBottomXLabel;
	}

	public boolean getDrawTopXLabel() {
		return drawTopXLabel;
	}

	public boolean getDrawLeftYLabel() {
		return drawLeftYLabel;
	}

	public boolean getDrawRightYLabel() {
		return drawRightYLabel;
	}

	public boolean getDrawBottomXAxisValues() {
		return drawBottomXAxisValues;
	}

	public boolean getDrawTopXAxisValues() {
		return drawTopXAxisValues;
	}

	public boolean getDrawLeftYAxisValues() {
		return drawLeftYAxisValues;
	}

	public boolean getDrawRightYAxisValues() {
		return drawRightYAxisValues;
	}

	public boolean getDrawExteriorBottomXAxisTicks() {
		return drawExteriorBottomXAxisTicks;
	}

	public boolean getDrawExteriorTopXAxisTicks() {
		return drawExteriorTopXAxisTicks;
	}

	public boolean getDrawExteriorLeftYAxisTicks() {
		return drawExteriorLeftYAxisTicks;
	}

	public boolean getDrawExteriorRightYAxisTicks() {
		return drawExteriorRightYAxisTicks;
	}

	public boolean getDrawInteriorBottomXAxisTicks() {
		return drawInteriorBottomXAxisTicks;
	}

	public boolean getDrawInteriorTopXAxisTicks() {
		return drawInteriorTopXAxisTicks;
	}

	public boolean getDrawInteriorLeftYAxisTicks() {
		return drawInteriorLeftYAxisTicks;
	}

	public boolean getDrawInteriorRightYAxisTicks() {
		return drawInteriorRightYAxisTicks;
	}

	public Color getBottomTickColor() {
		return bottomTickColor;
	}

	public Color getLeftTickColor() {
		return leftTickColor;
	}

	public Color getTopTickColor() {
		return topTickColor;
	}

	public Color getRightTickColor() {
		return rightTickColor;
	}

	public int getBottomTickHeight() {
		return getBottomTickHeight;
	}
	
	public int getLeftTickWidth() {
		return getLeftTickWidth;
	}
	
	public int getTopTickHeight() {
		return getTopTickHeight;
	}
	
	public int getRightTickWidth() {
		return getRightTickWidth;
	}
	
	public int getInteriorBottomTickThickness() {
		return interiorBottomTickThickness;
	}

	public int getInteriorTopTickThickness() {
		return interiorTopTickThickness;
	}

	public int getInteriorLeftTickThickness() {
		return interiorLeftTickThickness;
	}

	public int getInteriorRightTickThickness() {
		return interiorRightTickThickness;
	}

	public int getExteriorBottomTickThickness() {
		return exteriorBottomTickThickness;
	}

	public int getExteriorTopTickThickness() {
		return exteriorTopTickThickness;
	}

	public int getExteriorLeftTickThickness() {
		return exteriorLeftTickThickness;
	}

	public int getExteriorRightTickThickness() {
		return exteriorRightTickThickness;
	}

	public Font getXAxisFont() {
		return xAxisFont;
	}

	public Font getYAxisFont() {
		return yAxisFont;
	}

	public Font getXAxisLabelFont() {
		return xAxisLabelFont;
	}

	public Font getYAxisLabelFont() {
		return yAxisLabelFont;
	}

	public Color getXAxisColor() {
		return xAxisColor;
	}

	public Color getYAxisColor() {
		return yAxisColor;
	}

	public Color getXAxisLabelColor() {
		return xAxisLabelColor;
	}

	public Color getYAxisLabelColor() {
		return yAxisLabelColor;
	}

	public boolean getIncludeXAxisLinesOnPlot() {
		return includeXAxisLinesOnPlot;
	}

	public Color getXAxisLinesOnPlotColor() {
		return xAxisLinesOnPlotColor;
	}

	public boolean getIncludeYAxisLinesOnPlot() {
		return includeYAxisLinesOnPlot;
	}

	public Color getYAxisLinesOnPlotColor() {
		return yAxisLinesOnPlotColor;
	}

	public int getXAxisLinesOnPlotWidth() {
		return xAxisLinesOnPlotWidth;
	}

	public int getYAxisLinesOnPlotWidth() {
		return yAxisLinesOnPlotWidth;
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public Color getTitleColor() {
		return titleColor;
	}
	
	public Color getChartBackgroundColor() {
		return chartBackgroundColor;
	}

	// Legend
	public boolean getDrawLegendOutline() {
		return drawlegendOutline;
	}

	public Color getLegendOutlineColor() {
		return legendOutlineColor;
	}

	public int getLegendOutlineWidth() {
		return legendOutlineWidth;
	}

	public Color getLegendTextColor() {
		return legendTextColor;
	}

	public Color getLegendBackgroundColor() {
		return legendBackgroundColor;
	}

	@Override
	public Color getBoxPlotOutlineColor() {
		return boxPlotOutlineColor;
	}
	

}
