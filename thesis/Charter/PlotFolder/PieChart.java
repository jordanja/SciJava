package thesis.Charter.PlotFolder;

import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.PieChartMeasurements;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class PieChart extends Chart {

	String[] labels;
	Double[] values;
	
	String legendLabel = "";
	
	PieChartMeasurements cm;
	
	PiePlot plot;
	Legend legend;
	
	public PieChart(DataFrame df, String labels, String values) {
		this.labels = DataItem.convertToStringList(df.getColumnAsArray(labels));
		this.values = DataItem.convertToDoubleList(df.getColumnAsArray(values));
		
		this.legendLabel = labels;
		
		this.plot = new PiePlot();
		this.legend = new Legend();
		this.cm = new PieChartMeasurements();
		
	}
	
	
	@Override
	public void Create() {
		this.legend.calculateLegend(this.legendLabel, CommonArray.convertStringArrayToObjectArray(labels));
		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(Color.white);
		
		this.plot.drawPlotBackground(g, this.cm);
		this.plot.drawPlotOutline(g, this.cm);
		
		this.plot.drawPlot(g, this.labels, this.values, this.cm);
		
		this.legend.drawLegend(g, this.cm, this.plot.getColorPalette());
		
		this.drawTitle(g, this.cm);
	}


	public PieChartMeasurements getChartMeasurements() {
		return this.cm;
	}


	public void setChartMeasurements(PieChartMeasurements cm) {
		this.cm = cm;
	}


	public PiePlot getPlot() {
		return plot;
	}


	public void setPlot(PiePlot plot) {
		this.plot = plot;
	}


	public Legend getLegend() {
		return legend;
	}


	public void setLegend(Legend legend) {
		this.legend = legend;
	}

}
