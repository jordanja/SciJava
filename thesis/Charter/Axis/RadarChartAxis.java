package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.PieChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.NiceScale;

public class RadarChartAxis {
	
	private String[] numericalTicks;
	private String[] categories;
	
	private Font font = new Font("Dialog", Font.PLAIN, 12);
	
	private int plotToAxisSpacing = 5;
	private int axisRadius;
	
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
	
	public void calculateAxisRadius(PieChartMeasurements cm) {
		int widestCategory = DrawString.maxWidthOfStringInList(categories, this.font, 0);
		
		this.axisRadius = (cm.getPlotWidth() - 2 * (widestCategory + plotToAxisSpacing))/2;
	}

	public void drawAxis(Graphics2D g, PieChartMeasurements cm) {
			
		g.setColor(Color.darkGray);
		g.setStroke(new BasicStroke(1));
		
		int xMid = cm.imageLeftToPlotMidWidth();
		int yMid = cm.imageBottomToPlotMidHeight();
		for (int categoryCount = 0; categoryCount < this.categories.length; categoryCount++) {
			double angle = Math.PI * 2 * ((double)categoryCount/this.categories.length) + Math.PI/2;
			int x = (int) (this.axisRadius * Math.cos(angle) + cm.imageLeftToPlotMidWidth());
			int y = (int) (this.axisRadius * Math.sin(angle) + cm.imageBottomToPlotMidHeight());
			
			g.drawLine(xMid, yMid, x, y);
			
		}
	}
	
	public int getAxisRadius() {
		return this.axisRadius;
	}
}
