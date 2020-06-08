package thesis.Charter.Legend;

import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.ChartMeasurements;

public abstract class Legend {

	protected boolean includeLegend;
	protected Color backgroundColor = Color.WHITE;
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean getIncludeLegend() {
		return includeLegend;
	}
	public void setIncludeLegend(boolean includeLegend) {
		this.includeLegend = includeLegend;
	}

	public abstract int getLegendWidth();
	public abstract void calculateLegend();
	public abstract void drawLegend(Graphics2D g, ChartMeasurements cm);
}
