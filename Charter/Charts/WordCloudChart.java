package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.awt.Color;
import thesis.Charter.ChartMeasurements.OnlyPlotChartMeasurements;
import thesis.Charter.Plots.WordCloudPlot;

public class WordCloudChart extends Chart {
	
	private WordCloudPlot plot;
	private OnlyPlotChartMeasurements cm;

	private String wordCloudString = "";

	public WordCloudChart(String wordCloudString) {
		this.wordCloudString = wordCloudString;

		this.plot = new WordCloudPlot();
		this.cm = new OnlyPlotChartMeasurements();
	}

	@Override
	public void create() {
		this.cm.calculateChartImageMetrics(this.getTitle(), this.getTitleFont());
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(Color.white);
		
		this.plot.drawPlotBackground(g, this.cm);

		
		this.plot.drawPlotOutline(g, this.cm);
	
		// this.plot.drawPlot(g, this.value, this.axis, this.cm);
		
		this.drawTitle(g, this.cm);
	
	}


	
}
