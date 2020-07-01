package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Charter.Styles.Style;
import thesis.Common.CommonArray;
import thesis.Common.NiceScale;

public class RadarChartAxis {
	
	private String[] numericalTicks;
	private String[] categories;
	
	private Font categoryFont = new Font("Dialog", Font.PLAIN, 12);
	private Font tickFont = new Font("Dialog", Font.PLAIN, 12);
	
	private int plotToAxisSpacing = 8;
	private int axisRadius;
	
	private Color axisSpikeColor = Color.DARK_GRAY;
	private Color axisOutlineColor = Color.BLACK;
	private Color axisTextColor = Color.BLACK;
	private Color axisTextBackgroundColor = Color.BLACK;
	
	public RadarChartAxis() {
		
	}

	public void setAxis(Double[] values) {
		double maxValue = CommonArray.maxValue(values);
		NiceScale yNS = new NiceScale(0, maxValue);
		this.numericalTicks = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.numericalTicks[i] = String.valueOf(tickValue);
		}
	}
	
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	
	public void calculateAxisRadius(NoAxisChartMeasurements cm) {
		int widestCategory = DrawString.maxWidthOfStringInList(categories, this.categoryFont, 0);
		
		this.axisRadius = (cm.getPlotWidth() - 2 * (widestCategory + plotToAxisSpacing))/2;
	}

	public void drawAxis(Graphics2D g, NoAxisChartMeasurements cm) {
			
		g.setColor(this.axisSpikeColor);
		g.setStroke(new BasicStroke(1));
		
		int xMid = cm.imageLeftToPlotMidWidth();
		int yMid = cm.imageBottomToPlotMidHeight();
		
		for (int categoryCount = 0; categoryCount < this.categories.length; categoryCount++) {
			double angle = Math.PI * 2 * (-(double)categoryCount/this.categories.length) + Math.PI/2;
			int x = (int) (this.axisRadius * Math.cos(angle) + cm.imageLeftToPlotMidWidth());
			int y = (int) (this.axisRadius * Math.sin(angle) + cm.imageBottomToPlotMidHeight());
			
			g.drawLine(xMid, yMid, x, y);
			
			double proportion = (double)categoryCount/this.categories.length;
			setTextAlignment(proportion);
			
			int stringX = (int) (x + this.plotToAxisSpacing * Math.cos(angle));
			int stringY = (int) (y + this.plotToAxisSpacing * Math.sin(angle));
			
			DrawString.setTextStyle(this.axisTextColor, categoryFont, 0);
			DrawString.write(g, this.categories[categoryCount], stringX, stringY);
			
		}
		
		for (int tickCount = 1; tickCount < this.numericalTicks.length; tickCount++) {
			double proportion = (double)tickCount/(this.numericalTicks.length - 1);
			int radius = (int) (this.axisRadius * proportion);
			for (int categoryCount = 0; categoryCount < this.categories.length; categoryCount++) {
				double angle1 = Math.PI * 2 * ((double)categoryCount/this.categories.length) + Math.PI/2;
				int x1 = (int) (radius * Math.cos(angle1) + cm.imageLeftToPlotMidWidth());
				int y1 = (int) (radius * Math.sin(angle1) + cm.imageBottomToPlotMidHeight());
				
				double angle2 = Math.PI * 2 * ((double)(categoryCount + 1)/this.categories.length) + Math.PI/2;
				int x2 = (int) (radius * Math.cos(angle2) + cm.imageLeftToPlotMidWidth());
				int y2 = (int) (radius * Math.sin(angle2) + cm.imageBottomToPlotMidHeight());
				
				g.setColor(this.axisOutlineColor);
				g.setStroke(new BasicStroke(1));
				
				g.drawLine(x1, y1, x2, y2);
			}
			
			g.setColor(this.axisTextBackgroundColor);
			String tick = this.numericalTicks[tickCount];
			
			int width = DrawString.getStringWidth(tick, this.tickFont);
			int height = DrawString.getStringHeight(tick, this.tickFont);
			
			int x =  cm.imageLeftToPlotMidWidth();
			int y = (int) (radius + cm.imageBottomToPlotMidHeight());
			g.fillRect(x - width/2 - 2, y - height/2 - 2, width + 4, height + 4);
			DrawString.setAlignment(xAlignment.CenterAlign, yAlignment.MiddleAlign);
			DrawString.setTextStyle(this.axisTextColor, this.tickFont, 0);
			DrawString.write(g, tick, x, y);
		}
	}

	private void setTextAlignment(double proportion) {
		if (proportion == 0) {
			// Top
			DrawString.setAlignment(xAlignment.CenterAlign, yAlignment.BottomAlign);
		} else if (proportion == 0.25) {
			// Right
			DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.MiddleAlign);				
		} else if (proportion == 0.5) {
			// Bottom
			DrawString.setAlignment(xAlignment.CenterAlign, yAlignment.TopAlign);
		} else if (proportion == 0.75) {
			// Left
			DrawString.setAlignment(xAlignment.RightAlign, yAlignment.MiddleAlign);
		} else if ((proportion > 0) && (proportion < 0.25)) {
			// Top Right
			DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.BottomAlign);
		} else if ((proportion > 0.25) && (proportion < 0.5)) {
			// Bottom Right
			DrawString.setAlignment(xAlignment.LeftAlign, yAlignment.TopAlign);
		} else if ((proportion > 0.5) && (proportion < 0.75)) {
			// Bottom Left
			DrawString.setAlignment(xAlignment.RightAlign, yAlignment.TopAlign);
		} else if ((proportion > 0.75) && (proportion < 1)) {
			// Top Left
			DrawString.setAlignment(xAlignment.RightAlign, yAlignment.BottomAlign);
		}
	}
	
	public int getAxisRadius() {
		return this.axisRadius;
	}
	
	public String[] getTicks() {
		return this.numericalTicks;
	}

	public Font getCategoryFont() {
		return categoryFont;
	}

	public void setCategoryFont(Font categoryFont) {
		this.categoryFont = categoryFont;
	}

	public Font getTickFont() {
		return tickFont;
	}

	public void setTickFont(Font tickFont) {
		this.tickFont = tickFont;
	}

	public int getPlotToAxisSpacing() {
		return plotToAxisSpacing;
	}

	public void setPlotToAxisSpacing(int plotToAxisSpacing) {
		this.plotToAxisSpacing = plotToAxisSpacing;
	}

	public void setStyle(Style styleToSet) {
		this.axisSpikeColor = styleToSet.getXAxisColor();
		this.axisOutlineColor = styleToSet.getYAxisColor();
		this.axisTextColor = styleToSet.getXAxisColor();
		this.axisTextBackgroundColor = styleToSet.getPlotBackgroundColor();
	}
	
}
