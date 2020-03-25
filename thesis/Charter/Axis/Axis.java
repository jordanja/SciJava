package thesis.Charter.Axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.NiceScale;
import thesis.Helpers.TypeCheckers;

public abstract class Axis {

	protected String[] xTicks;
	protected String[] yTicks;

	protected float xAxisRotation;
	protected float yAxisRotation;

	protected String xAxisLabel;
	protected String yAxisLabel;

	protected boolean drawBottomXLabel = true;
	protected boolean drawTopXLabel;
	protected boolean drawLeftYLabel = true;
	protected boolean drawRightYLabel;

	protected boolean drawBottomXAxisValues = true;
	protected boolean drawTopXAxisValues;
	protected boolean drawLeftYAxisValues = true;
	protected boolean drawRightYAxisValues;

	private boolean drawExteriorTicksDefaults = false;
	protected boolean drawExteriorBottomXAxisTicks = drawExteriorTicksDefaults;
	protected boolean drawExteriorTopXAxisTicks = drawExteriorTicksDefaults;
	protected boolean drawExteriorLeftYAxisTicks = drawExteriorTicksDefaults;
	protected boolean drawExteriorRightYAxisTicks = drawExteriorTicksDefaults;

	private boolean drawInteriorTicksDefaults = false;
	protected boolean drawInteriorBottomXAxisTicks = drawInteriorTicksDefaults;
	protected boolean drawInteriorTopXAxisTicks = drawInteriorTicksDefaults;
	protected boolean drawInteriorLeftYAxisTicks = drawInteriorTicksDefaults;
	protected boolean drawInteriorRightYAxisTicks = drawInteriorTicksDefaults;

	private Color defaultTickColor = Color.BLACK;
	protected Color bottomTickColor = this.defaultTickColor;
	protected Color leftTickColor = this.defaultTickColor;
	protected Color topTickColor = this.defaultTickColor;
	protected Color rightTickColor = this.defaultTickColor;

	private int defaultTickThickness = 1;

	protected int defaultInteriorTickThickness = this.defaultTickThickness;
	protected int interiorBottomTickThickness = this.defaultInteriorTickThickness;
	protected int interiorTopTickThickness = this.defaultInteriorTickThickness;
	protected int interiorLeftTickThickness = this.defaultInteriorTickThickness;
	protected int interiorRightTickThickness = this.defaultInteriorTickThickness;

	protected int defaultExteriorTickThickness = this.defaultTickThickness;
	protected int exteriorBottomTickThickness = this.defaultExteriorTickThickness;
	protected int exteriorTopTickThickness = this.defaultExteriorTickThickness;
	protected int exteriorLeftTickThickness = this.defaultExteriorTickThickness;
	protected int exteriorRightTickThickness = this.defaultExteriorTickThickness;

	private Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
	protected Font xAxisFont = this.defaultFont;
	protected Font yAxisFont = this.defaultFont;
	protected Font xAxisLabelFont = this.defaultFont;
	protected Font yAxisLabelFont = this.defaultFont;

	private Color defaultTextColor = Color.BLACK;
	protected Color xAxisColor = this.defaultTextColor;
	protected Color yAxisColor = this.defaultTextColor;
	protected Color xAxisLabelColor = this.defaultTextColor;
	protected Color yAxisLabelColor = this.defaultTextColor;

	public Axis() {

	}

	public void xAxisRotation(float xAxisRotation) {
		this.xAxisRotation = xAxisRotation;
	}

	public void yAxisRotation(float yAxisRotation) {
		this.yAxisRotation = yAxisRotation;
	}

	public void setXAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public void setYAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	public void setIncludeBottomXLabel(boolean includeBottomXLabel) {
		this.drawBottomXLabel = includeBottomXLabel;
	}

	public void setIncludeTopXLabel(boolean includeTopXLabel) {
		this.drawTopXLabel = includeTopXLabel;
	}

	public void setIncludeLeftYLabel(boolean includeLeftYLabel) {
		this.drawLeftYLabel = includeLeftYLabel;
	}

	public void setIncludeRightYLabel(boolean includeRightYLabel) {
		this.drawRightYLabel = includeRightYLabel;
	}

	public void setIncludeBottomXAxisValues(boolean includeBottomXAxisValues) {
		this.drawBottomXAxisValues = includeBottomXAxisValues;
	}

	public void setIncludeTopXAxisValues(boolean includeTopXAxisValues) {
		this.drawTopXAxisValues = includeTopXAxisValues;
	}

	public void setIncludeLeftYAxisValues(boolean includeLeftYAxisValues) {
		this.drawLeftYAxisValues = includeLeftYAxisValues;
	}

	public void setIncludeRightYAxisValues(boolean includeRightYAxisValues) {
		this.drawRightYAxisValues = includeRightYAxisValues;
	}

	public void setIncludeExteriorTicks(boolean includeExteriorBottomXAxisTicks, boolean includeExteriorTopXAxisTicks,
			boolean includeExteriorLeftYAxisTicks, boolean includeExteriorRightYAxisTicks) {
		setIncludeBottomXAxisTicks(includeExteriorBottomXAxisTicks, this.drawInteriorBottomXAxisTicks);
		setIncludeTopXAxisTicks(includeExteriorTopXAxisTicks, this.drawInteriorTopXAxisTicks);
		setIncludeLeftYAxisTicks(includeExteriorLeftYAxisTicks, this.drawInteriorLeftYAxisTicks);
		setIncludeRightYAxisTicks(includeExteriorRightYAxisTicks, this.drawInteriorRightYAxisTicks);
	}

	public void setIncludeInteriorTicks(boolean includeInteriorBottomXAxisTicks, boolean includeInteriorTopXAxisTicks,
			boolean includeInteriorLeftYAxisTicks, boolean includeInteriorRightYAxisTicks) {
		setIncludeBottomXAxisTicks(this.drawExteriorBottomXAxisTicks, includeInteriorBottomXAxisTicks);
		setIncludeTopXAxisTicks(this.drawExteriorTopXAxisTicks, includeInteriorTopXAxisTicks);
		setIncludeLeftYAxisTicks(this.drawExteriorLeftYAxisTicks, includeInteriorLeftYAxisTicks);
		setIncludeRightYAxisTicks(this.drawExteriorRightYAxisTicks, includeInteriorRightYAxisTicks);

	}

	public void setIncludeTicks(boolean includeExteriorBottomXAxisTicks, boolean includeInteriorBottomXAxisTicks,
			boolean includeExteriorTopXAxisTicks, boolean includeInteriorTopXAxisTicks,
			boolean includeExteriorLeftYAxisTicks, boolean includeInteriorLeftYAxisTicks,
			boolean includeExteriorRightYAxisTicks, boolean includeInteriorRightYAxisTicks) {
		setIncludeBottomXAxisTicks(includeExteriorBottomXAxisTicks, includeInteriorBottomXAxisTicks);
		setIncludeTopXAxisTicks(includeExteriorTopXAxisTicks, includeInteriorTopXAxisTicks);
		setIncludeLeftYAxisTicks(includeExteriorLeftYAxisTicks, includeInteriorLeftYAxisTicks);
		setIncludeRightYAxisTicks(includeExteriorRightYAxisTicks, includeInteriorRightYAxisTicks);
	}

	public void setIncludeBottomXAxisTicks(boolean includeExteriorBottomXAxisTicks,
			boolean includeInteriorBottomXAxisTicks) {
		this.drawExteriorBottomXAxisTicks = includeExteriorBottomXAxisTicks;
		this.drawInteriorBottomXAxisTicks = includeInteriorBottomXAxisTicks;
	}

	public void setIncludeTopXAxisTicks(boolean includeExteriorTopXAxisTicks, boolean includeInteriorTopXAxisTicks) {
		this.drawExteriorTopXAxisTicks = includeExteriorTopXAxisTicks;
		this.drawInteriorTopXAxisTicks = includeInteriorTopXAxisTicks;
	}

	public void setIncludeLeftYAxisTicks(boolean includeExteriorLeftYAxisTicks, boolean includeInteriorLeftYAxisTicks) {
		this.drawExteriorLeftYAxisTicks = includeExteriorLeftYAxisTicks;
		this.drawInteriorLeftYAxisTicks = includeInteriorLeftYAxisTicks;
	}

	public void setIncludeRightYAxisTicks(boolean includeExteriorRightYAxisTicks,
			boolean includeInteriorRightYAxisTicks) {
		this.drawExteriorRightYAxisTicks = includeExteriorRightYAxisTicks;
		this.drawInteriorRightYAxisTicks = includeInteriorRightYAxisTicks;
	}

	public void setTickThickness(int thickness) {
		setInteriorTickThickness(thickness);
		setExteriorTickThickness(thickness);
	}

	public void setInteriorTickThickness(int thickness) {
		setInteriorBottomTickThickness(thickness);
		setInteriorTopTickThickness(thickness);
		setInteriorLeftTickThickness(thickness);
		setInteriorRightTickThickness(thickness);

	}

	public void setExteriorTickThickness(int thickness) {
		setExteriorBottomTickThickness(thickness);
		setExteriorTopTickThickness(thickness);
		setExteriorLeftTickThickness(thickness);
		setExteriorRightTickThickness(thickness);

	}

	public int getInteriorBottomTickThickness() {
		return this.interiorBottomTickThickness;
	}

	public void setInteriorBottomTickThickness(int interiorBottomTickThickness) {
		this.interiorBottomTickThickness = interiorBottomTickThickness;
	}

	public int getInteriorTopTickThickness() {
		return this.interiorTopTickThickness;
	}

	public void setInteriorTopTickThickness(int interiorTopTickThickness) {
		this.interiorTopTickThickness = interiorTopTickThickness;
	}

	public int getInteriorLeftTickThickness() {
		return this.interiorLeftTickThickness;
	}

	public void setInteriorLeftTickThickness(int interiorLeftTickThickness) {
		this.interiorLeftTickThickness = interiorLeftTickThickness;
	}

	public int getInteriorRightTickThickness() {
		return this.interiorRightTickThickness;
	}

	public void setInteriorRightTickThickness(int interiorRightTickThickness) {
		this.interiorRightTickThickness = interiorRightTickThickness;
	}

	public int getExteriorBottomTickThickness() {
		return this.exteriorBottomTickThickness;
	}

	public void setExteriorBottomTickThickness(int exteriorBottomTickThickness) {
		this.exteriorBottomTickThickness = exteriorBottomTickThickness;
	}

	public int getExteriorTopTickThickness() {
		return this.exteriorTopTickThickness;
	}

	public void setExteriorTopTickThickness(int exteriorTopTickThickness) {
		this.exteriorTopTickThickness = exteriorTopTickThickness;
	}

	public int getExteriorLeftTickThickness() {
		return this.exteriorLeftTickThickness;
	}

	public void setExteriorLeftTickThickness(int exteriorLeftTickThickness) {
		this.exteriorLeftTickThickness = exteriorLeftTickThickness;
	}

	public int getExteriorRightTickThickness() {
		return this.exteriorRightTickThickness;
	}

	public void setExteriorRightTickThickness(int exteriorRightTickThickness) {
		this.exteriorRightTickThickness = exteriorRightTickThickness;
	}

	public void setTickColor(Color color) {
		setBottomTickColor(color);
		setLeftTickColor(color);
		setTopTickColor(color);
		setRightTickColor(color);

	}

	public void setBottomTickColor(Color color) {
		this.bottomTickColor = color;
	}

	public void setLeftTickColor(Color color) {
		this.leftTickColor = color;
	}

	public void setTopTickColor(Color color) {
		this.topTickColor = color;
	}

	public void setRightTickColor(Color color) {
		this.rightTickColor = color;
	}

	public void setXAxisFont(Font xAxisFont) {
		this.xAxisFont = xAxisFont;
	}

	public void setYAxisFont(Font yAxisFont) {
		this.yAxisFont = yAxisFont;
	}

	public void setXAxisLabelFont(Font xAxisLabelFont) {
		this.xAxisLabelFont = xAxisLabelFont;
	}

	public void setYAxisLabelFont(Font yAxisLabelFont) {
		this.yAxisLabelFont = yAxisLabelFont;
	}

	public void setDefaultTextColor(Color defaultTextColor) {
		this.defaultTextColor = defaultTextColor;
	}

	public void setXAxisColor(Color xAxisColor) {
		this.xAxisColor = xAxisColor;
	}

	public void setYAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
	}

	public void setXAxisLabelColor(Color xAxisLabelColor) {
		this.xAxisLabelColor = xAxisLabelColor;
	}

	public void setYAxisLabelColor(Color yAxisLabelColor) {
		this.yAxisLabelColor = yAxisLabelColor;
	}

	public String[] getXTicks() {
		return this.xTicks;
	}

	public String[] getYTicks() {
		return this.yTicks;
	}

	public double[] getXTicksValues() {
		return Arrays.stream(getXTicks()).mapToDouble(Double::parseDouble).toArray();
	}

	public double[] getYTicksValues() {
		return Arrays.stream(getYTicks()).mapToDouble(Double::parseDouble).toArray();
	}

	public String[] getXTicksFormattedForDisplay() {
		String[] formattedXTicks = new String[this.xTicks.length];

		if (this.xTicks.length > 0) {
			if (TypeCheckers.isNumeric(this.xTicks[0])) {
				double[] xTicksValues = this.getXTicksValues();

				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.HALF_DOWN);

				for (int i = 0; i < formattedXTicks.length; i++) {
					formattedXTicks[i] = df.format(xTicksValues[i]);
				}
				return formattedXTicks;
			} else {
				return this.xTicks;
			}
		}
		return null;
	}

	public String[] getYTicksFormattedForDisplay() {
		String[] formattedYTicks = new String[this.yTicks.length];

		if (TypeCheckers.isNumeric(this.yTicks[0])) {
			double[] yTicksValues = this.getYTicksValues();

			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_DOWN);

			for (int i = 0; i < formattedYTicks.length; i++) {
				formattedYTicks[i] = df.format(yTicksValues[i]);
			}
			return formattedYTicks;
		} else {
			return this.yTicks;
		}
	}

	public float getXAxisRotation() {
		return this.xAxisRotation;
	}

	public float getYAxisRotation() {
		return this.yAxisRotation;
	}

	public String getXAxisLabel() {
		return this.xAxisLabel;
	}

	public String getYAxisLabel() {
		return this.yAxisLabel;
	}

	public boolean drawBottomXLabel() {
		return this.drawBottomXLabel && this.xAxisLabel != null;
	}

	public boolean drawTopXLabel() {
		return this.drawTopXLabel && this.xAxisLabel != null;
	}

	public boolean drawLeftYLabel() {
		return this.drawLeftYLabel && this.yAxisLabel != null;
	}

	public boolean drawRightYLabel() {
		return this.drawRightYLabel && this.yAxisLabel != null;
	}

	public boolean drawBottomXAxisValues() {
		return this.drawBottomXAxisValues;
	}

	public boolean drawTopXAxisValues() {
		return this.drawTopXAxisValues;
	}

	public boolean drawLeftYAxisValues() {
		return this.drawLeftYAxisValues;
	}

	public boolean drawRightYAxisValues() {
		return this.drawRightYAxisValues;
	}

	public boolean drawExteriorBottomXAxisTicks() {
		return this.drawExteriorBottomXAxisTicks;
	}

	public boolean drawExteriorTopXAxisTicks() {
		return this.drawExteriorTopXAxisTicks;
	}

	public boolean drawExteriorLeftYAxisTicks() {
		return this.drawExteriorLeftYAxisTicks;
	}

	public boolean drawExteriorRightYAxisTicks() {
		return this.drawExteriorRightYAxisTicks;
	}

	public boolean drawInteriorBottomXAxisTicks() {
		return this.drawInteriorBottomXAxisTicks;
	}

	public boolean drawInteriorTopXAxisTicks() {
		return this.drawInteriorTopXAxisTicks;
	}

	public boolean drawInteriorLeftYAxisTicks() {
		return this.drawInteriorLeftYAxisTicks;
	}

	public boolean drawInteriorRightYAxisTicks() {
		return this.drawInteriorRightYAxisTicks;
	}

	public Color getBottomTickColor() {
		return this.bottomTickColor;
	}

	public Color getLeftTickColor() {
		return this.leftTickColor;
	}

	public Color getTopTickColor() {
		return this.topTickColor;
	}

	public Color getRightTickColor() {
		return this.rightTickColor;
	}

	public Font getXAxisFont() {
		return this.xAxisFont;
	}

	public Font getYAxisFont() {
		return this.yAxisFont;
	}

	public Font getXAxisLabelFont() {
		return this.xAxisLabelFont;
	}

	public Font getYAxisLabelFont() {
		return this.yAxisLabelFont;
	}

	public Color getXAxisColor() {
		return this.xAxisColor;
	}

	public Color getYAxisColor() {
		return this.yAxisColor;
	}

	public Color getXAxisLabelColor() {
		return this.xAxisLabelColor;
	}

	public Color getYAxisLabelColor() {
		return this.yAxisLabelColor;
	}

	public void drawXAxisLabel(Graphics2D g, XYChartMeasurements cm) {

		g.setColor(this.xAxisLabelColor);
		g.setFont(this.xAxisLabelFont);

		if (this.xAxisLabel != null) {
			DrawString.setColor(this.xAxisLabelColor);
			DrawString.setFont(this.xAxisLabelFont);
			DrawString.setRotation(0);
			DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
			if (this.drawBottomXLabel) {
				DrawString.write(g, this.xAxisLabel, cm.imageLeftToPlotMidWidth(),
						cm.imageBottomToBottomAxisLabelMidHeight());
			}
			if (this.drawTopXLabel) {
				DrawString.write(g, this.xAxisLabel, cm.imageLeftToPlotMidWidth(),
						cm.imageBottomToTopAxisLabelMidHeight());
			}
		}

	}

	public void drawYAxisLabel(Graphics2D g, XYChartMeasurements cm) {
		g.setColor(this.yAxisLabelColor);
		g.setFont(this.yAxisLabelFont);

		if (this.yAxisLabel != null) {
			DrawString.setColor(this.yAxisLabelColor);
			DrawString.setFont(this.yAxisLabelFont);
			DrawString.setRotation(-90);
			DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
			if (this.drawLeftYLabel) {
				DrawString.write(g, this.yAxisLabel, cm.imageLeftToLeftAxisLabelMidWidth(),
						cm.imageBottomToPlotMidHeight());
			}
			if (this.drawRightYLabel) {
				DrawString.write(g, this.yAxisLabel, cm.imageLeftToRightAxisLabelMidWidth(),
						cm.imageBottomToPlotMidHeight());
			}
		}
	}

}
