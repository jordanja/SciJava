package thesis.Charter.Styles;

import java.awt.Color;
import java.awt.Font;

public interface NumericAxisStyle {
	// Axis
		public abstract float getXAxisRotation();
		public abstract float getYAxisRotation();

		public abstract boolean getDrawBottomXLabel();
		public abstract boolean getDrawTopXLabel();
		public abstract boolean getDrawLeftYLabel();
		public abstract boolean getDrawRightYLabel();

		public abstract boolean getDrawBottomXAxisValues();
		public abstract boolean getDrawTopXAxisValues();
		public abstract boolean getDrawLeftYAxisValues();
		public abstract boolean getDrawRightYAxisValues();

		public abstract boolean getDrawExteriorBottomXAxisTicks();
		public abstract boolean getDrawExteriorTopXAxisTicks();
		public abstract boolean getDrawExteriorLeftYAxisTicks();
		public abstract boolean getDrawExteriorRightYAxisTicks();

		public abstract boolean getDrawInteriorBottomXAxisTicks();
		public abstract boolean getDrawInteriorTopXAxisTicks();
		public abstract boolean getDrawInteriorLeftYAxisTicks();
		public abstract boolean getDrawInteriorRightYAxisTicks();

		public abstract Color getBottomTickColor();
		public abstract Color getLeftTickColor();
		public abstract Color getTopTickColor();
		public abstract Color getRightTickColor();

		public abstract int getInteriorBottomTickThickness();
		public abstract int getInteriorTopTickThickness();
		public abstract int getInteriorLeftTickThickness();
		public abstract int getInteriorRightTickThickness();
		
		public abstract int getExteriorBottomTickThickness();
		public abstract int getExteriorTopTickThickness();
		public abstract int getExteriorLeftTickThickness();
		public abstract int getExteriorRightTickThickness();

		public abstract Font getXAxisFont();
		public abstract Font getYAxisFont();
		public abstract Font getXAxisLabelFont();
		public abstract Font getYAxisLabelFont();

		public abstract Color getXAxisColor();
		public abstract Color getYAxisColor();
		public abstract Color getXAxisLabelColor();
		public abstract Color getYAxisLabelColor();
		
		public abstract boolean getIncludeXAxisLinesOnPlot();
		public abstract Color getXAxisLinesOnPlotColor();
		public abstract int getXAxisLinesOnPlotWidth();
		
		public abstract boolean getIncludeYAxisLinesOnPlot();
		public abstract Color getYAxisLinesOnPlotColor();
		public abstract int getYAxisLinesOnPlotWidth();
}
