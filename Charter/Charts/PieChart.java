package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.PiePlot;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.StyleFactory;
import thesis.Charter.Styles.Styles;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class PieChart extends Chart {

	String[] categories;
	Double[] values;

	String legendLabel = "";

	NoAxisChartMeasurements cm;

	PiePlot plot;
	CategoricalLegend legend;

	public PieChart(DataFrame df, String categories, String values) {
		this.categories = df.getColumnAsStringArray(categories);
		this.values = DataItem.convertToDoubleList(df.getColumnAsDataItemArray(values));

		this.legendLabel = categories;

		this.plot = new PiePlot();
		this.legend = new CategoricalLegend();
		this.cm = new NoAxisChartMeasurements();

	}

	@Override
	public void create() {
		LegendData legendData = new LegendData();
		legendData.setColorData(CommonArray.convertObjectArrayToStringArray(CommonArray.removeDuplicates(categories)),
				this.plot.getColorPalette());
		legendData.setColorLabel(this.legendLabel);
		this.legend.setLegendData(legendData);
		this.legend.calculateLegend();

		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());

		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);

		this.drawBackground(g, this.cm);
		this.plot.setPlotBackgroundColor(this.plot.getPlotBackgroundColor());
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

	public void setLegend(CategoricalLegend legend) {
		this.legend = legend;
	}

	public void setStyle(Styles style) {
		Style styleToSet = StyleFactory.getStyle(style);
		this.plot.setStyle(styleToSet);
		this.legend.setStyle(styleToSet);

		this.setTitleFont(styleToSet.getTitleFont());
		this.setTitleColor(styleToSet.getTitleColor());
		this.setImageBackgroundColor(styleToSet.getChartBackgroundColor());
	}

}
