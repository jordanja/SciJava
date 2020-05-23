package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.PiePlot;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class PieChart extends Chart {

	String[] categories;
	Double[] values;
	
	String legendLabel = "";
	
	NoAxisChartMeasurements cm;
	
	PiePlot plot;
	Legend legend;
	
	public PieChart(DataFrame df, String categories, String values) {
		this.categories = DataItem.convertToStringList(df.getColumnAsArray(categories));
		this.values = DataItem.convertToDoubleList(df.getColumnAsArray(values));
		
		this.legendLabel = categories;
		
		this.plot = new PiePlot();
		this.legend = new Legend();
		this.cm = new NoAxisChartMeasurements();
		
	}
	
	
	@Override
	public void Create() {
		LegendData legendData = new LegendData();
		legendData.setColorData(CommonArray.convertObjectArrayToStringArray(CommonArray.removeDuplicates(categories)), this.plot.getColorPalette());
		legendData.setColorLabel(this.legendLabel);
		this.legend.setLegendData(legendData);
		this.legend.calculateLegend();
		
		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(Color.white);
		
		this.plot.drawPlotBackground(g, this.cm);
		this.plot.drawPlotOutline(g, this.cm);
		
		this.plot.drawPlot(g, this.categories, this.values, this.cm);
		
		this.legend.drawLegend(g, this.cm);
		
		this.drawTitle(g, this.cm);
	}


	public NoAxisChartMeasurements getChartMeasurements() {
		return this.cm;
	}


	public void setChartMeasurements(NoAxisChartMeasurements cm) {
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
