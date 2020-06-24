package thesis.Charter.Styles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import thesis.Helpers.Palette;

public class NighttimeStyle extends Style {
	private static final Color lightLines = new Color(60, 60, 60);
	private static final Color darkLines = new Color(42, 42, 42);
	private static final Color background = new Color(33, 33, 33);
	
	// Plot
	private static final boolean drawBottomPlotOutline = true; 
	private static final boolean drawLeftPlotOutline = true;
	private static final boolean drawTopPlotOutline = false;
	private static final boolean drawRightPlotOutline = false;

	private static final Color bottomPlotOutlineColor = lightLines;
	private static final Color leftPlotOutlineColor = lightLines;
	private static final Color topPlotOutlineColor = lightLines;
	private static final Color rightPlotOutlineColor = lightLines;

	private static final int bottomPlotOutlineWidth = 2;
	private static final int leftPlotOutlineWidth = 2;
	private static final int topPlotOutlineWidth = 2;
	private static final int rightPlotOutlineWidth = 2;

	private static final Color plotBackgroundColor = background;
	private static final Image plotBackgroundImage = null;

	private static final Color[] colorPalette = Palette.Neon;

	// Axis
	private static final float xAxisRotation = 0;
	private static final float yAxisRotation = 0;

	private static final boolean drawBottomXLabel = true;
	private static final boolean drawTopXLabel = false;
	private static final boolean drawLeftYLabel = true;
	private static final boolean drawRightYLabel = false;

	private static final boolean drawBottomXAxisValues = true;
	private static final boolean drawTopXAxisValues = false;
	private static final boolean drawLeftYAxisValues = true;
	private static final boolean drawRightYAxisValues = false;

	private static final boolean drawExteriorBottomXAxisTicks = false;
	private static final boolean drawExteriorTopXAxisTicks = false;
	private static final boolean drawExteriorLeftYAxisTicks = false;
	private static final boolean drawExteriorRightYAxisTicks = false;

	private static final boolean drawInteriorBottomXAxisTicks = false;
	private static final boolean drawInteriorTopXAxisTicks = false;
	private static final boolean drawInteriorLeftYAxisTicks = false;
	private static final boolean drawInteriorRightYAxisTicks = false;

	private static final Color bottomTickColor = Color.BLACK;
	private static final Color leftTickColor = Color.BLACK;
	private static final Color topTickColor = Color.BLACK;
	private static final Color rightTickColor = Color.BLACK;

	private static final int interiorBottomTickThickness = 1;
	private static final int interiorTopTickThickness = 1;
	private static final int interiorLeftTickThickness = 1;
	private static final int interiorRightTickThickness = 1;

	private static final int exteriorBottomTickThickness = 1;
	private static final int exteriorTopTickThickness = 1;
	private static final int exteriorLeftTickThickness = 1;
	private static final int exteriorRightTickThickness = 1;

	private static final Font xAxisFont = new Font("Dialog", Font.PLAIN, 12);
	private static final Font yAxisFont = new Font("Dialog", Font.PLAIN, 12);
	private static final Font xAxisLabelFont = new Font("Dialog", Font.PLAIN, 12);
	private static final Font yAxisLabelFont = new Font("Dialog", Font.PLAIN, 12);

	private static final Color xAxisColor = Color.WHITE;
	private static final Color yAxisColor = Color.WHITE;
	private static final Color xAxisLabelColor = Color.WHITE;
	private static final Color yAxisLabelColor = Color.WHITE;
	
	private static final boolean includeXAxisLinesOnPlot = true;
	private static final Color xAxisLinesOnPlotColor = darkLines;
	private static final int xAxisLinesOnPlotWidth = 1;
	
	private static final boolean includeYAxisLinesOnPlot = true;
	private static final Color yAxisLinesOnPlotColor = darkLines;
	private static final int yAxisLinesOnPlotWidth = 1;
	
	// Chart Measurements
	private static final int getBottomTickHeight = 0;
	private static final int getLeftTickWidth = 0;
	private static final int getTopTickHeight = 0;
	private static final int getRightTickWidth = 0;
	
	// Chart
	private static final Font titleFont = new Font("Dialog", Font.PLAIN, 12);
	private static final Color titleColor = Color.WHITE;
	private static final Color chartBackgroundColor = background;
	
	// Legend
	private static final boolean drawlegendOutline = true;
	private static final Color legendOutlineColor = Color.WHITE;
	private static final int legendOutlineWidth = 1;
	private static final Color legendTextColor = Color.WHITE;
	private static final Color legendBackgroundColor = background;
	
	// Plot
	@Override
	public boolean getDrawBottomPlotOutline() {
		return drawBottomPlotOutline;
	}

	@Override
	public boolean getDrawLeftPlotOutline() {
		return drawLeftPlotOutline;
	}

	@Override
	public boolean getDrawTopPlotOutline() {
		return drawTopPlotOutline;
	}

	@Override
	public boolean getDrawRightPlotOutline() {
		return drawRightPlotOutline;
	}

	@Override
	public Color getBottomPlotOutlineColor() {
		return bottomPlotOutlineColor;
	}

	@Override
	public Color getLeftPlotOutlineColor() {
		return leftPlotOutlineColor;
	}

	@Override
	public Color getTopPlotOutlineColor() {
		return topPlotOutlineColor;
	}

	@Override
	public Color getRightPlotOutlineColor() {
		return rightPlotOutlineColor;
	}

	@Override
	public int getBottomPlotOutlineWidth() {
		return bottomPlotOutlineWidth;
	}

	@Override
	public int getLeftPlotOutlineWidth() {
		return leftPlotOutlineWidth;
	}

	@Override
	public int getTopPlotOutlineWidth() {
		return topPlotOutlineWidth;
	}

	@Override
	public int getRightPlotOutlineWidth() {
		return rightPlotOutlineWidth;
	}

	@Override
	public Color getPlotBackgroundColor() {
		return plotBackgroundColor;
	}

	@Override
	public Image getPlotBackgroundImage() {
		return plotBackgroundImage;
	}

	@Override
	public Color[] getColorPalette() {
		return colorPalette;
	}
	
	// Axis
	@Override
	public float getXAxisRotation() {
		return xAxisRotation;
	}

	@Override
	public float getYAxisRotation() {
		return yAxisRotation;
	}

	@Override
	public boolean getDrawBottomXLabel() {
		return drawBottomXLabel;
	}

	@Override
	public boolean getDrawTopXLabel() {
		return drawTopXLabel;
	}

	@Override
	public boolean getDrawLeftYLabel() {
		return drawLeftYLabel;
	}

	@Override
	public boolean getDrawRightYLabel() {
		return drawRightYLabel;
	}

	@Override
	public boolean getDrawBottomXAxisValues() {
		return drawBottomXAxisValues;
	}

	@Override
	public boolean getDrawTopXAxisValues() {
		return drawTopXAxisValues;
	}

	@Override
	public boolean getDrawLeftYAxisValues() {
		return drawLeftYAxisValues;
	}

	@Override
	public boolean getDrawRightYAxisValues() {
		return drawRightYAxisValues;
	}

	@Override
	public boolean getDrawExteriorBottomXAxisTicks() {
		return drawExteriorBottomXAxisTicks;
	}

	@Override
	public boolean getDrawExteriorTopXAxisTicks() {
		return drawExteriorTopXAxisTicks;
	}

	@Override
	public boolean getDrawExteriorLeftYAxisTicks() {
		return drawExteriorLeftYAxisTicks;
	}

	@Override
	public boolean getDrawExteriorRightYAxisTicks() {
		return drawExteriorRightYAxisTicks;
	}

	@Override
	public boolean getDrawInteriorBottomXAxisTicks() {
		return drawInteriorBottomXAxisTicks;
	}

	@Override
	public boolean getDrawInteriorTopXAxisTicks() {
		return drawInteriorTopXAxisTicks;
	}

	@Override
	public boolean getDrawInteriorLeftYAxisTicks() {
		return drawInteriorLeftYAxisTicks;
	}

	@Override
	public boolean getDrawInteriorRightYAxisTicks() {
		return drawInteriorRightYAxisTicks;
	}

	@Override
	public Color getBottomTickColor() {
		return bottomTickColor;
	}

	@Override
	public Color getLeftTickColor() {
		return leftTickColor;
	}

	@Override
	public Color getTopTickColor() {
		return topTickColor;
	}

	@Override
	public Color getRightTickColor() {
		return rightTickColor;
	}

	@Override
	public int getBottomTickHeight() {
		return getBottomTickHeight;
	}
	
	@Override
	public int getLeftTickWidth() {
		return getLeftTickWidth;
	}
	
	@Override
	public int getTopTickHeight() {
		return getTopTickHeight;
	}
	
	@Override
	public int getRightTickWidth() {
		return getRightTickWidth;
	}
	
	@Override
	public int getInteriorBottomTickThickness() {
		return interiorBottomTickThickness;
	}

	@Override
	public int getInteriorTopTickThickness() {
		return interiorTopTickThickness;
	}

	@Override
	public int getInteriorLeftTickThickness() {
		return interiorLeftTickThickness;
	}

	@Override
	public int getInteriorRightTickThickness() {
		return interiorRightTickThickness;
	}

	@Override
	public int getExteriorBottomTickThickness() {
		return exteriorBottomTickThickness;
	}

	@Override
	public int getExteriorTopTickThickness() {
		return exteriorTopTickThickness;
	}

	@Override
	public int getExteriorLeftTickThickness() {
		return exteriorLeftTickThickness;
	}

	@Override
	public int getExteriorRightTickThickness() {
		return exteriorRightTickThickness;
	}

	@Override
	public Font getXAxisFont() {
		return xAxisFont;
	}

	@Override
	public Font getYAxisFont() {
		return yAxisFont;
	}

	@Override
	public Font getXAxisLabelFont() {
		return xAxisLabelFont;
	}

	@Override
	public Font getYAxisLabelFont() {
		return yAxisLabelFont;
	}

	@Override
	public Color getXAxisColor() {
		return xAxisColor;
	}

	@Override
	public Color getYAxisColor() {
		return yAxisColor;
	}

	@Override
	public Color getXAxisLabelColor() {
		return xAxisLabelColor;
	}

	@Override
	public Color getYAxisLabelColor() {
		return yAxisLabelColor;
	}

	@Override
	public boolean getIncludeXAxisLinesOnPlot() {
		return includeXAxisLinesOnPlot;
	}

	@Override
	public Color getXAxisLinesOnPlotColor() {
		return xAxisLinesOnPlotColor;
	}

	@Override
	public boolean getIncludeYAxisLinesOnPlot() {
		return includeYAxisLinesOnPlot;
	}

	@Override
	public Color getYAxisLinesOnPlotColor() {
		return yAxisLinesOnPlotColor;
	}

	@Override
	public int getXAxisLinesOnPlotWidth() {
		return xAxisLinesOnPlotWidth;
	}

	@Override
	public int getYAxisLinesOnPlotWidth() {
		return yAxisLinesOnPlotWidth;
	}

	@Override
	public Font getTitleFont() {
		return titleFont;
	}

	@Override
	public Color getTitleColor() {
		return titleColor;
	}
	
	@Override
	public Color getChartBackgroundColor() {
		return chartBackgroundColor;
	}
	
	// Legend
	@Override
	public boolean getDrawLegendOutline() {
		return drawlegendOutline;
	}

	@Override
	public Color getLegendOutlineColor() {
		return legendOutlineColor;
	}

	@Override
	public int getLegendOutlineWidth() {
		return legendOutlineWidth;
	}

	@Override
	public Color getLegendTextColor() {
		return legendTextColor;
	}
	
	@Override
	public Color getLegendBackgroundColor() {
		return legendBackgroundColor;
	}
}
