package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

import thesis.Charter.Axis.RadarChartAxis;
import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.PiePlot;
import thesis.Charter.Plots.RadarPlot;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.StyleFactory;
import thesis.Charter.Styles.Styles;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class RadarChart extends Chart{
	
	private String[] radarCategories;
	private String[] valueCategories;
	private Double[] values;
	
	private String legendLabel = "";
	
	private NoAxisChartMeasurements cm;
	
	private RadarChartAxis axis;
	private RadarPlot plot;
	private CategoricalLegend legend;
	
	public RadarChart(DataFrame df, String radarCategories, String valueCategories, String values) {
		this.radarCategories = df.getColumnAsStringArray(radarCategories);
		this.valueCategories = df.getColumnAsStringArray(valueCategories);
		this.values = DataItem.convertToDoubleList(df.getColumnAsDataItemArray(values));
		
		this.legendLabel = radarCategories;
		
		this.axis = new RadarChartAxis();
		this.plot = new RadarPlot();
		this.legend = new CategoricalLegend();
		this.cm = new NoAxisChartMeasurements();
	}

	@Override
	public void create() {
		
		String[] uniqueRadarCategories = CommonArray.removeDuplicates(this.radarCategories);
		String[] uniqueValueCategories = CommonArray.removeDuplicates(this.valueCategories);
		HashMap<String, HashMap<String, Double>> data = calculateData(uniqueRadarCategories, uniqueValueCategories);
		
		this.axis.setAxis(this.values);
		this.axis.setCategories(uniqueValueCategories);
		this.axis.calculateAxisRadius(cm);
		
		LegendData legendData = new LegendData();
		legendData.setColorData(CommonArray.removeDuplicates(uniqueRadarCategories), this.plot.getColorPalette());
		legendData.setColorLabel(this.legendLabel);
		this.legend.setLegendData(legendData);
		this.legend.calculateLegend();
		
		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(this.plot.getPlotBackgroundColor());
		
		this.plot.drawPlotBackground(g, this.cm);
		
		this.axis.drawAxis(g, this.cm);
		
		this.plot.drawPlotOutline(g, this.cm);
		
		this.plot.drawPlot(g, data, uniqueRadarCategories, uniqueValueCategories, this.axis.getAxisRadius(), this.axis.getTicks(), this.cm);
		

		this.legend.drawLegend(g, this.cm);
		

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
	
	
	public NoAxisChartMeasurements getChartMeasurements() {
		return this.cm;
	}
	public void setChartMeasurements(NoAxisChartMeasurements cm) {
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
	public void setLegend(CategoricalLegend legend) {
		this.legend = legend;
	}
	
	public void setStyle(Styles style) {
		Style styleToSet = StyleFactory.getStyle(style);
		this.plot.setStyle(styleToSet);
		this.legend.setStyle(styleToSet);
		this.axis.setStyle(styleToSet);
		
		this.setTitleFont(styleToSet.getTitleFont());
		this.setTitleColor(styleToSet.getTitleColor());
		this.setImageBackgroundColor(styleToSet.getChartBackgroundColor());
	}
	
}
