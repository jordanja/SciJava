package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import thesis.Charter.Others.PieChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Helpers.Palette;
public class PiePlot extends Plot{

	private Color[] colorPalette = Palette.Default;
	private double[] shatter;
	private double startAngle = 0;

	private boolean includeProportionsOnPie = false;
	private Font proportionsFont = new Font("Dialog", Font.PLAIN, 20);
	
	public void drawPlot(Graphics2D g, String[] labels, Double[] values, PieChartMeasurements cm) {
		
		if (this.shatter == null) {
			this.shatter = CommonArray.initializeArrayWithValues(labels.length, 0);
		}
		
		double total = CommonMath.total(values);
		
		double[] percentageOfPie = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			percentageOfPie[i] = 360 * (values[i]/total);
		}
		
		double maxShatter = CommonArray.maxValue(this.shatter);
		
		double radius = (cm.getPlotWidth()/2) / (1 + maxShatter);
		
		double cumulativeAngle = startAngle;
		for (int sliceCount = 0; sliceCount < values.length; sliceCount++) {
			
			double angleToShatterTowards = Math.toRadians(-(cumulativeAngle + percentageOfPie[sliceCount]/2));
			
			Arc2D arc = new Arc2D.Float();
			double xCenter = cm.imageLeftToPlotMidWidth() + radius * Math.cos(angleToShatterTowards) * this.shatter[sliceCount];
			double yCenter = cm.imageBottomToPlotMidHeight() + radius * Math.sin(angleToShatterTowards) * this.shatter[sliceCount];
			
			arc.setArcByCenter(xCenter, yCenter, radius, cumulativeAngle, percentageOfPie[sliceCount], Arc2D.PIE);
			
			cumulativeAngle += percentageOfPie[sliceCount];
			
			g.setColor(this.colorPalette[sliceCount % this.colorPalette.length]);
			g.fill(arc);
			
			if (this.includeProportionsOnPie) {
				g.setColor(Color.white);

				int xPos = (int)(cm.imageLeftToPlotMidWidth() + (radius * Math.cos(angleToShatterTowards)) * (this.shatter[sliceCount] + 0.5));
				int yPos = (int)(cm.imageBottomToPlotMidHeight() + (radius * Math.sin(angleToShatterTowards)) * (this.shatter[sliceCount] + 0.5));
	
				String strToDisplay = formatStrForPlot(100 * values[sliceCount]/total) + "%";
				
				g.setFont(this.proportionsFont);
				DrawString.write(g, strToDisplay, xPos, yPos, DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, 0, cm);
				
				
			}
		}
		
	}

	public Color[] getColorPalette() {
		return this.colorPalette;
	}
	public void setColorPalette(Color[] colorPalette) {
		this.colorPalette = colorPalette;
	}
	
	public double[] getShatter() {
		return this.shatter;
	}
	public void setShatter(double[] shatter) {
		this.shatter = shatter;
	}
	
	public double getStartAngle() {
		return startAngle;
	}
	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	public boolean isIncludeProportionsOnPie() {
		return includeProportionsOnPie;
	}
	public void setIncludeProportionsOnPie(boolean includeProportionsOnPie) {
		this.includeProportionsOnPie = includeProportionsOnPie;
	}
	
	private String formatStrForPlot(double value) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_DOWN);
		
		return df.format(value);
	}

	public Font getProportionsFont() {
		return proportionsFont;
	}

	public void setProportionsFont(Font proportionsFont) {
		this.proportionsFont = proportionsFont;
	}



}
