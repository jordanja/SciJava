package thesis.Charter.Axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import thesis.Auxiliary.NiceScale;
import thesis.Charter.Others.XYChartMeasurements;

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
		return interiorBottomTickThickness;
	}

	public void setInteriorBottomTickThickness(int interiorBottomTickThickness) {
		this.interiorBottomTickThickness = interiorBottomTickThickness;
	}

	public int getInteriorTopTickThickness() {
		return interiorTopTickThickness;
	}

	public void setInteriorTopTickThickness(int interiorTopTickThickness) {
		this.interiorTopTickThickness = interiorTopTickThickness;
	}

	public int getInteriorLeftTickThickness() {
		return interiorLeftTickThickness;
	}

	public void setInteriorLeftTickThickness(int interiorLeftTickThickness) {
		this.interiorLeftTickThickness = interiorLeftTickThickness;
	}

	public int getInteriorRightTickThickness() {
		return interiorRightTickThickness;
	}

	public void setInteriorRightTickThickness(int interiorRightTickThickness) {
		this.interiorRightTickThickness = interiorRightTickThickness;
	}

	public int getExteriorBottomTickThickness() {
		return exteriorBottomTickThickness;
	}

	public void setExteriorBottomTickThickness(int exteriorBottomTickThickness) {
		this.exteriorBottomTickThickness = exteriorBottomTickThickness;
	}

	public int getExteriorTopTickThickness() {
		return exteriorTopTickThickness;
	}

	public void setExteriorTopTickThickness(int exteriorTopTickThickness) {
		this.exteriorTopTickThickness = exteriorTopTickThickness;
	}

	public int getExteriorLeftTickThickness() {
		return exteriorLeftTickThickness;
	}

	public void setExteriorLeftTickThickness(int exteriorLeftTickThickness) {
		this.exteriorLeftTickThickness = exteriorLeftTickThickness;
	}

	public int getExteriorRightTickThickness() {
		return exteriorRightTickThickness;
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
	
	public String[] getxTicks() {
		return xTicks;
	}

	public String[] getyTicks() {
		return yTicks;
	}

	public float getxAxisRotation() {
		return xAxisRotation;
	}

	public float getyAxisRotation() {
		return yAxisRotation;
	}

	public String getxAxisLabel() {
		return xAxisLabel;
	}

	public String getyAxisLabel() {
		return yAxisLabel;
	}

	public boolean drawBottomXLabel() {
		return drawBottomXLabel;
	}

	public boolean drawTopXLabel() {
		return drawTopXLabel;
	}

	public boolean drawLeftYLabel() {
		return drawLeftYLabel;
	}

	public boolean drawRightYLabel() {
		return drawRightYLabel;
	}

	public boolean drawBottomXAxisValues() {
		return drawBottomXAxisValues;
	}

	public boolean drawTopXAxisValues() {
		return drawTopXAxisValues;
	}

	public boolean drawLeftYAxisValues() {
		return drawLeftYAxisValues;
	}

	public boolean drawRightYAxisValues() {
		return drawRightYAxisValues;
	}

	public boolean drawExteriorBottomXAxisTicks() {
		return drawExteriorBottomXAxisTicks;
	}

	public boolean drawExteriorTopXAxisTicks() {
		return drawExteriorTopXAxisTicks;
	}

	public boolean drawExteriorLeftYAxisTicks() {
		return drawExteriorLeftYAxisTicks;
	}

	public boolean drawExteriorRightYAxisTicks() {
		return drawExteriorRightYAxisTicks;
	}

	public boolean drawInteriorBottomXAxisTicks() {
		return drawInteriorBottomXAxisTicks;
	}

	public boolean drawInteriorTopXAxisTicks() {
		return drawInteriorTopXAxisTicks;
	}

	public boolean drawInteriorLeftYAxisTicks() {
		return drawInteriorLeftYAxisTicks;
	}

	public boolean drawInteriorRightYAxisTicks() {
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

	public Font getxAxisFont() {
		return xAxisFont;
	}

	public Font getyAxisFont() {
		return yAxisFont;
	}

	public Font getxAxisLabelFont() {
		return xAxisLabelFont;
	}

	public Font getyAxisLabelFont() {
		return yAxisLabelFont;
	}

	public Color getxAxisColor() {
		return xAxisColor;
	}

	public Color getyAxisColor() {
		return yAxisColor;
	}

	public Color getxAxisLabelColor() {
		return xAxisLabelColor;
	}

	public Color getyAxisLabelColor() {
		return yAxisLabelColor;
	}
	
	
	
	
	
	
	
	
	
}















































