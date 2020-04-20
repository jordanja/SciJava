package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.RadarChartAxis;
import thesis.Charter.ChartMeasurements.PieChartMeasurements;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Plots.PiePlot;
import thesis.Charter.Plots.RadarPlot;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class RadarChart extends Chart{
	
	private String[] radarCategories;
	private String[] valueCategories;
	private Double[] values;
	
	private String legendLabel = "";
	
	private PieChartMeasurements cm;
	
	private RadarChartAxis axis;
	private RadarPlot plot;
	private Legend legend;
	
	public RadarChart(DataFrame df, String radarCategories, String valueCategories, String values) {
		this.radarCategories = DataItem.convertToStringList(df.getColumnAsArray(radarCategories));
		this.valueCategories = DataItem.convertToStringList(df.getColumnAsArray(valueCategories));
		this.values = DataItem.convertToDoubleList(df.getColumnAsArray(values));
		
		this.legendLabel = radarCategories;
		
		this.axis = new RadarChartAxis();
		this.plot = new RadarPlot();
		this.legend = new Legend();
		this.cm = new PieChartMeasurements();
	}

	@Override
	public void Create() {
		
		String[] uniqueRadarCategories = CommonArray.removeDuplicates(this.radarCategories);
		String[] uniqueValueCategories = CommonArray.removeDuplicates(this.valueCategories);
		HashMap<String, HashMap<String, Double>> data = calculateData(uniqueRadarCategories, uniqueValueCategories);
		
		this.axis.setAxis(this.values);
		this.axis.setCategories(uniqueValueCategories);
		this.axis.calculateAxisRadius(cm);
		
		this.legend.calculateLegend(this.legendLabel, uniqueValueCategories);
		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(Color.white);
		
		this.plot.drawPlotBackground(g, this.cm);
		
		this.axis.drawAxis(g, this.cm);
		
		this.plot.drawPlotOutline(g, this.cm);
		
		this.plot.drawPlot(data, uniqueRadarCategories, uniqueValueCategories, this.axis.getAxisRadius(), this.cm);
		

		if (this.legend.getIncludeLegend()) {
			this.legend.drawLegend(g, this.cm, this.plot.getColorPalette());
		}

		this.drawTitle(g, this.cm);
		
	}

	private HashMap<String, HashMap<String, Double>> calculateData(String[] uniqueRadarCategories, String[] uniqueValueCategories) {
		HashMap<String, HashMap<String, Double>> data = new HashMap<String, HashMap<String, Double>>();
		for (String radarCategory: uniqueRadarCategories) {
			data.put(radarCategory, new HashMap<String, Double>());
		}
		for (int rowCount = 0; rowCount < this.radarCategories.length; rowCount++) {
			data.get(this.radarCategories[rowCount]).put(this.valueCategories[rowCount], this.values[rowCount]);
		}
		return data;
	}
	
	
	public PieChartMeasurements getChartMeasurements() {
		return this.cm;
	}
	public void setChartMeasurements(PieChartMeasurements cm) {
		this.cm = cm;
	}


	public RadarPlot getPlot() {
		return plot;
	}
	public void setPlot(RadarPlot plot) {
		this.plot = plot;
	}


	public Legend getLegend() {
		return legend;
	}
	public void setLegend(Legend legend) {
		this.legend = legend;
	}
	
}
