package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
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
	private double donutAmount = 0;

	private boolean includeProportionsOnPie = false;
	private Font proportionsFont = new Font("Dialog", Font.PLAIN, 20);
	private Color proportionsColor = Color.WHITE;
	
	private boolean useSemiCircle = false;
	
	public void drawPlot(Graphics2D g, String[] labels, Double[] values, PieChartMeasurements cm) {
		
		if (this.shatter == null) {
			this.shatter = CommonArray.initializeArrayWithValues(labels.length, 0);
		}
		
		double total = CommonMath.total(values);
		
		double[] sliceDegrees = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			sliceDegrees[i] = 360 * (values[i]/total);
		}
		
		double maxShatter = CommonArray.maxValue(this.shatter);
		
		double radius = (cm.getPlotWidth()/2) / (1 + maxShatter);
		
		double cumulativeAngle = startAngle;
		
		for (int sliceCount = 0; sliceCount < values.length; sliceCount++) {
			
			double angleToShatterTowards = Math.toRadians(-(cumulativeAngle + sliceDegrees[sliceCount]/2));
			
			double xCenter = cm.imageLeftToPlotMidWidth() + radius * Math.cos(angleToShatterTowards) * this.shatter[sliceCount];
			double yCenter = cm.imageBottomToPlotMidHeight() + radius * Math.sin(angleToShatterTowards) * this.shatter[sliceCount];
			Color sliceColor = this.colorPalette[sliceCount % this.colorPalette.length];
			
			drawSlice(g, sliceDegrees[sliceCount], radius, cumulativeAngle, sliceColor, xCenter, yCenter);
			
			if (this.includeProportionsOnPie) {
				double value = 100 * values[sliceCount]/total;
				writeProportions(g, value, cm, radius, sliceCount, angleToShatterTowards);
				
				
			}
			
			cumulativeAngle += sliceDegrees[sliceCount];
		}
		
	}


	private void writeProportions(Graphics2D g, double value, PieChartMeasurements cm, double radius, int sliceCount, double angleToShatterTowards) {

		int xPos = (int)(cm.imageLeftToPlotMidWidth() + (radius * Math.cos(angleToShatterTowards)) * (this.shatter[sliceCount] + 0.5));
		int yPos = (int)(cm.imageBottomToPlotMidHeight() + (radius * Math.sin(angleToShatterTowards)) * (this.shatter[sliceCount] + 0.5));

		String strToDisplay = formatStrForPlot(value) + "%";
		
		g.setColor(this.proportionsColor);
		g.setFont(this.proportionsFont);
		DrawString.write(g, strToDisplay, xPos, yPos, DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign, 0, cm);
	}


	private void drawSlice(Graphics2D g, double degrees, double radius, double cumulativeAngle, Color color, double xCenter, double yCenter) {		
		int numPoints = 200;
		
		GeneralPath slice = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		
		double normalized = Math.toRadians(-cumulativeAngle);
		
		double incrementAmount = Math.toRadians(degrees / (numPoints - 1));
		
		slice.moveTo(
			Math.cos(normalized - (numPoints + 1) * incrementAmount) * radius * this.donutAmount + xCenter, 
			Math.sin(normalized - (numPoints + 1) * incrementAmount) * radius * this.donutAmount + yCenter
		);
		
		for (int i = numPoints; i > 0; i--) {
			double xPoint = Math.cos(normalized  - i * incrementAmount) * radius * this.donutAmount + xCenter;
			double yPoint = Math.sin(normalized  - i * incrementAmount) * radius * this.donutAmount + yCenter;
			
			slice.lineTo(xPoint, yPoint);
		}
		
		for (int i = 0; i < numPoints; i++) {
			
			double xPoint = Math.cos(normalized - i * incrementAmount) * radius + xCenter;
			double yPoint = Math.sin(normalized - i * incrementAmount) * radius + yCenter;
			
			slice.lineTo(xPoint, yPoint);
			
		}
		slice.closePath();

		g.setColor(color);
		g.fill(slice);
		
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


	public double getDonutAmount() {
		return donutAmount;
	}
	public void setDonutAmount(double donutAmount) {
		this.donutAmount = donutAmount;
	}


	public Color getProportionsColor() {
		return proportionsColor;
	}


	public void setProportionsColor(Color proportionsColor) {
		this.proportionsColor = proportionsColor;
	}


	public boolean getUseSemiCircle() {
		return useSemiCircle;
	}
	public void setUseSemiCircle(boolean useSemiCircle) {
		this.useSemiCircle = useSemiCircle;
	}



}
