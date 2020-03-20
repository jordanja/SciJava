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
		cm = new PieChartMeasurements();
		
	}
	
	
	@Override
	public void Create() {
		this.legend.calculateLegend(this.legendLabel, CommonArray.convertStringArrayToObjectArray(labels));
		cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		
		instantiateChart(cm);

		Graphics2D g = initializaGraphicsObject(cm);
		
		drawBackground(g, cm);
		
		this.plot.setChartBackgroundColor(Color.white);
		
		this.plot.drawPlotBackground(g, cm);
		this.plot.drawPlotOutline(g, cm);
		
		this.plot.drawPlot(g, this.labels, this.values, cm);
		
		this.legend.drawLegend(g, cm, this.plot.getColorPalette());
		
		this.drawTitle(g, cm);
	}

}
