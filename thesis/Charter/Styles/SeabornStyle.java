package thesis.Charter.Styles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import thesis.Helpers.Palette;

public class SeabornStyle extends Style{

	// Plot
	private static final boolean drawBottomPlotOutline = false;
	private static final boolean drawLeftPlotOutline = false;
	private static final boolean drawTopPlotOutline = false;
	private static final boolean drawRightPlotOutline = false;

	private static final Color bottomPlotOutlineColor = Color.WHITE;
	private static final Color leftPlotOutlineColor = Color.WHITE;
	private static final Color topPlotOutlineColor = Color.WHITE;
	private static final Color rightPlotOutlineColor = Color.WHITE;

	private static final int bottomPlotOutlineWidth = 1;
	private static final int leftPlotOutlineWidth = 1;
	private static final int topPlotOutlineWidth = 1;
	private static final int rightPlotOutlineWidth = 1;

	private static final Color plotBackgroundColor = new Color(229,229,239);
	private static final Image plotBackgroundImage = null;

	private static final Color[] colorPalette = Palette.Default;

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

	private static final Color bottomTickColor = Color.WHITE;
	private static final Color leftTickColor = Color.WHITE;
	private static final Color topTickColor = Color.WHITE;
	private static final Color rightTickColor = Color.WHITE;

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

	private static final Color xAxisColor = Color.BLACK;
	private static final Color yAxisColor = Color.BLACK;
	private static final Color xAxisLabelColor = Color.BLACK;
	private static final Color yAxisLabelColor = Color.BLACK;

	private static final boolean includeXAxisLinesOnPlot = true;
	private static final Color xAxisLinesOnPlotColor = Color.WHITE;
	private static final int xAxisLinesOnPlotWidth = 1;

	private static final boolean includeYAxisLinesOnPlot = true;
	private static final Color yAxisLinesOnPlotColor = Color.WHITE;
	private static final int yAxisLinesOnPlotWidth = 1;

	// Chart Measurements
	private static final int getBottomTickHeight = 0;
	private static final int getLeftTickWidth = 0;
	private static final int getTopTickHeight = 0;
	private static final int getRightTickWidth = 0;	
	
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
}
