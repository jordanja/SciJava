package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;

import thesis.Charter.Others.PieChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Helpers.Palette;
public class PiePlot extends Plot{

	private Color[] colorPalette = Palette.Default;
	
	private double[] shatter;
	
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

		Rectangle boundingRect = new Rectangle(cm.getImageLeftToPlotLeftWidth(), cm.getImageBottomToPlotBottomHeight(), cm.getPlotWidth(), cm.getPlotHeight());
		g.setColor(Color.GREEN);
		g.draw(boundingRect);
		
		double cumulativeAngle = 0;
		for (int sliceCount = 0; sliceCount < values.length; sliceCount++) {
			g.setColor(this.colorPalette[sliceCount % this.colorPalette.length]);
			
			Arc2D arc = new Arc2D.Float(Arc2D.PIE);
			arc.setAngleStart(cumulativeAngle);
			arc.setAngleExtent(percentageOfPie[sliceCount]);
			arc.setFrame(boundingRect); 
			
			cumulativeAngle += percentageOfPie[sliceCount];
			
			g.fill(arc);
		}
		

	}

	public Color[] getColorPalette() {
		return this.colorPalette;
	}


	public double[] getShatter() {
		return this.shatter;
	}
	
	public void setShatter(double[] shatter) {
		this.shatter = shatter;
	}


}
