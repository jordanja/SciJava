package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.USAMapPlot;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.StyleFactory;
import thesis.Charter.Styles.Styles;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class USAMapChart extends Chart {

	private NoAxisChartMeasurements cm;
	
	private USAMapPlot plot;
	private CategoricalLegend legend;
	
	private String legendLabel = "";
	
	private DataFrame dataFrame;
	private String statesColumnName; 
	private String valuesColumnName;
	
	public enum usaMapType {Gradient, Category};
	private usaMapType chartType;
	
	public USAMapChart(DataFrame dataFrame, String statesColumnName, String valuesColumnName, usaMapType charttType) {
		this.dataFrame = dataFrame;
		this.statesColumnName = statesColumnName;
		this.valuesColumnName = valuesColumnName;
		this.chartType = charttType;
		
		this.cm = new NoAxisChartMeasurements();
		this.plot = new USAMapPlot();
		
		this.legend = new CategoricalLegend();
		this.legendLabel = valuesColumnName;
	}
	
	@Override
	public void create() {
		DataFrame dataDF = this.dataFrame.clone();
		dataDF.dropColumn("color");
		dataDF.appendColumn("color");
		
		String[] legendValues = new String[0];
		Color[] legendColors = new Color[0];
		
		if (this.chartType == usaMapType.Gradient) {
			double maxValue = this.dataFrame.maxInColumn(this.valuesColumnName);
			double minValue = this.dataFrame.minInColumn(this.valuesColumnName);
			
			int valueIndex = this.dataFrame.getColumnNames().indexOf(this.valuesColumnName);
			double[] values = this.dataFrame.getColumnAsDoubleArray(valueIndex);
			
			Color baseColor = this.plot.getColorPalette()[0];
			for (int rowIndex = 0; rowIndex < this.dataFrame.getNumRows(); rowIndex++) {
				int opacity = (int)((values[rowIndex] - minValue) * (255/(maxValue - minValue)));
				Color colorToSet = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), opacity);
				dataDF.setValue("color", rowIndex, colorToSet);
			}
			
			int numLegendValues = 5;
			legendValues = new String[numLegendValues];
			legendColors = new Color[numLegendValues];
			
			double valueRange = maxValue - minValue;
			
			for (int legendValueCount = 0; legendValueCount < numLegendValues; legendValueCount++) {
				double percentage = ((double)legendValueCount/(numLegendValues - 1));
				legendValues[legendValueCount] = DrawString.formatDoubleForDisplay(minValue + valueRange * percentage);
				int opacity = (int) (255 * percentage);
				legendColors[legendValueCount] = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), opacity);
			}
			
			
		} else if (this.chartType == usaMapType.Category) {
			String[] uniqueCategories = CommonArray.removeDuplicates(this.dataFrame.getColumnAsStringArray(this.valuesColumnName));
		}
		
		LegendData legendData = new LegendData();
		legendData.setColorData(legendValues, legendColors);
		legendData.setColorLabel(this.legendLabel);
		this.legend.setLegendData(legendData);
		this.legend.setDrawHueValueOutline(true);
		this.legend.setHueValueOutlienColor(Color.BLACK);
		this.legend.setHueValueOutlineWidth(1);
		this.legend.calculateLegend();
		
		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(this.plot.getPlotBackgroundColor());
		
		this.plot.setPlotBackgroundColor(Color.WHITE);
		this.plot.drawPlotBackground(g, this.cm);
				
		this.plot.drawPlotOutline(g, this.cm);
		
		this.plot.drawPlot(g, dataDF, statesColumnName, valuesColumnName, this.cm);
		
		this.legend.drawLegend(g, this.cm);

		this.drawTitle(g, this.cm);
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
