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

public class PolarAreaChartAxis {
	
	private String[] numericalTicks;
	private Font tickFont = new Font("Dialog", Font.PLAIN, 12);
	private Color tickColor = Color.BLACK;
	
	private Color axisColor = Color.BLACK;
	private int axisWeight = 1;
	private Color axisTextBackgroundColor = Color.WHITE;
	
	public PolarAreaChartAxis() {
		
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

	public void drawAxis(Graphics2D g, NoAxisChartMeasurements cm) {
 		
		int xMid = cm.imageLeftToPlotMidWidth();
		int yMid = cm.imageBottomToPlotMidHeight();
		
		
		for (int tickCount = 1; tickCount < this.numericalTicks.length; tickCount++) {
			double proportion = (double)tickCount/(this.numericalTicks.length - 1);
			int diameter = (int) (cm.getPlotWidth() * proportion);
			g.setColor(this.axisColor);
			g.setStroke(new BasicStroke(axisWeight));
			g.drawArc(xMid - diameter/2, yMid - diameter/2, diameter, diameter, 0, 360);

			
			String tick = this.numericalTicks[tickCount];
			
			int width = DrawString.getStringWidth(tick, this.tickFont);
			int height = DrawString.getStringHeight(tick, this.tickFont);
			
			
			int x =  cm.imageLeftToPlotMidWidth();
			int y = (int) (diameter/2 + cm.imageBottomToPlotMidHeight());

			g.setColor(this.axisTextBackgroundColor);
			g.fillRect(x - width/2 - 2, y - height/2 - 2, width + 4, height + 4);
			
			DrawString.setAlignment(xAlignment.CenterAlign, yAlignment.MiddleAlign);
			DrawString.setTextStyle(this.tickColor, this.tickFont, 0);
			DrawString.write(g, tick, x, y);
			
		}
	}
	public String[] getTicks() {
		return this.numericalTicks;
	}
	
	public Font getTickFont() {
		return tickFont;
	}

	public void setTickFont(Font tickFont) {
		this.tickFont = tickFont;
	}

	public Color getTickColor() {
		return tickColor;
	}

	public void setTickColor(Color tickColor) {
		this.tickColor = tickColor;
	}

	public Color getAxisColor() {
		return axisColor;
	}

	public void setAxisColor(Color axisColor) {
		this.axisColor = axisColor;
	}

	public int getAxisWeight() {
		return axisWeight;
	}

	public void setAxisWeight(int axisWeight) {
		this.axisWeight = axisWeight;
	}

	public void setStyle(Style styleToSet) {
		this.axisColor = styleToSet.getXAxisColor();
		this.tickFont = styleToSet.getXAxisFont();
		this.tickColor = styleToSet.getXAxisColor();
		this.axisTextBackgroundColor = styleToSet.getPlotBackgroundColor();
	}

}
