package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.Axis.PolarAreaChartAxis;
import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Plots.PolarAreaPlot;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class PolarAreaChart extends Chart{

	private String[] categories;
	private Double[] values;
	
	private String legendLabel = "";
	
	private NoAxisChartMeasurements cm;
	private PolarAreaPlot plot;
	private PolarAreaChartAxis axis;
	private Legend legend;

	public PolarAreaChart(DataFrame df, String categories, String values) {
		this.categories = DataItem.convertToStringList(df.getColumnAsArray(categories));
		this.values = DataItem.convertToDoubleList(df.getColumnAsArray(values));
		
		this.legendLabel = categories;
		
		this.plot = new PolarAreaPlot();
		this.legend = new Legend();
		this.axis = new PolarAreaChartAxis();
		this.cm = new NoAxisChartMeasurements();
		
	}

	@Override
	public void Create() {
		this.legend.calculateLegend(this.legendLabel, CommonArray.convertStringArrayToObjectArray(categories));
		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		this.axis.setAxis(this.values);

		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(Color.white);
		
		this.plot.drawPlotBackground(g, this.cm);
		
		this.axis.drawAxis(g, this.cm);

		
		this.plot.drawPlotOutline(g, this.cm);
		
		this.plot.drawPlot(g, this.categories, this.values, this.axis.getTicks(), this.cm);
		
		this.legend.drawLegend(g, this.cm, this.plot.getColorPalette());
		
		this.drawTitle(g, this.cm);
		
	}

	public String getLegendLabel() {
		return legendLabel;
	}

	public void setLegendLabel(String legendLabel) {
		this.legendLabel = legendLabel;
	}

	public NoAxisChartMeasurements getChartMeasurements() {
		return cm;
	}

	public void getChartMeasurements(NoAxisChartMeasurements cm) {
		this.cm = cm;
	}

	public PolarAreaPlot getPlot() {
		return plot;
	}

	public void setPlot(PolarAreaPlot plot) {
		this.plot = plot;
	}

	public PolarAreaChartAxis getAxis() {
		return axis;
	}

	public void setAxis(PolarAreaChartAxis axis) {
		this.axis = axis;
	}

	public Legend getLegend() {
		return legend;
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}
	
	
	
}
