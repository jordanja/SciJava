package thesis.Charter.Axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Arrays;

import thesis.Charter.Others.XYChartMeasurements;
import thesis.Common.NiceScale;

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
	
	public void setIncludeExteriorTicks(boolean includeExteriorBottomXAxisTicks, boolean includeExteriorTopXAxisTicks, boolean includeExteriorLeftYAxisTicks, boolean includeExteriorRightYAxisTicks) {
		setIncludeBottomXAxisTicks(includeExteriorBottomXAxisTicks, this.drawInteriorBottomXAxisTicks);
		setIncludeTopXAxisTicks(includeExteriorTopXAxisTicks, this.drawInteriorTopXAxisTicks);
		setIncludeLeftYAxisTicks(includeExteriorLeftYAxisTicks, this.drawInteriorLeftYAxisTicks);
		setIncludeRightYAxisTicks(includeExteriorRightYAxisTicks, this.drawInteriorRightYAxisTicks);
	}
	
	public void setIncludeInteriorTicks(boolean includeInteriorBottomXAxisTicks, boolean includeInteriorTopXAxisTicks, boolean includeInteriorLeftYAxisTicks, boolean includeInteriorRightYAxisTicks) {
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
	
	public void setIncludeBottomXAxisTicks(boolean includeExteriorBottomXAxisTicks, boolean includeInteriorBottomXAxisTicks) {
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
	
	public void setIncludeRightYAxisTicks(boolean includeExteriorRightYAxisTicks, boolean includeInteriorRightYAxisTicks) {
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
		return Arrays.stream(getXTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
	}

	public double[] getYTicksValues() {
		return Arrays.stream(getYTicks())
                .mapToDouble(Double::parseDouble)
                .toArray();
	}

	public float getxAxisRotation() {
		return this.xAxisRotation;
	}

	public float getyAxisRotation() {
		return this.yAxisRotation;
	}

	public String getxAxisLabel() {
		return this.xAxisLabel;
	}

	public String getyAxisLabel() {
		return this.yAxisLabel;
	}

	public boolean drawBottomXLabel() {
		return this.drawBottomXLabel;
	}

	public boolean drawTopXLabel() {
		return this.drawTopXLabel;
	}

	public boolean drawLeftYLabel() {
		return this.drawLeftYLabel;
	}

	public boolean drawRightYLabel() {
		return this.drawRightYLabel;
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

	public Font getxAxisFont() {
		return this.xAxisFont;
	}

	public Font getyAxisFont() {
		return this.yAxisFont;
	}

	public Font getxAxisLabelFont() {
		return this.xAxisLabelFont;
	}

	public Font getyAxisLabelFont() {
		return this.yAxisLabelFont;
	}

	public Color getxAxisColor() {
		return this.xAxisColor;
	}

	public Color getyAxisColor() {
		return this.yAxisColor;
	}

	public Color getxAxisLabelColor() {
		return this.xAxisLabelColor;
	}

	public Color getyAxisLabelColor() {
		return this.yAxisLabelColor;
	}
	
	
	
	
	
	
	
	
	
}















































