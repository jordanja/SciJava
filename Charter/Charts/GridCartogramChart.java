package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Legend.LegendData;
import thesis.Charter.Plots.GridCartogramPlot;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.Styles.Style;
import thesis.Charter.Styles.StyleFactory;
import thesis.Charter.Styles.Styles;
import thesis.Common.CommonArray;
import thesis.Common.CommonGridCartograms;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class GridCartogramChart extends Chart {

	private NoAxisChartMeasurements cm;
	
	private GridCartogramPlot plot;
	private CategoricalLegend legend;
	
	private String legendLabel = "";
	
	private DataFrame dataFrame;
	private String localitiesColumnName; 
	private String valuesColumnName;
	
	public enum ChartType {Gradient, Category};
	private ChartType chartType;
	
	public enum MapType {USAStates, WorldCountries};
	private MapType mapType;
	
	public GridCartogramChart(DataFrame dataFrame, String localitiesColumnName, String valuesColumnName, MapType mapType, ChartType chartType) {
		this.dataFrame = dataFrame;
		this.localitiesColumnName = localitiesColumnName;
		this.valuesColumnName = valuesColumnName;
		this.mapType = mapType;
		this.chartType = chartType;
		
		this.cm = new NoAxisChartMeasurements();
		this.plot = new GridCartogramPlot();
		
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
		
		if (this.chartType == ChartType.Gradient) {
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
			
			
		} else if (this.chartType == ChartType.Category) {
			legendValues = CommonArray.removeDuplicates(this.dataFrame.getColumnAsStringArray(this.valuesColumnName));
			legendColors = Arrays.copyOf(this.plot.getColorPalette(), legendValues.length);
			
			String[] values = this.dataFrame.getColumnAsStringArray(this.valuesColumnName);
			for (int rowIndex = 0; rowIndex < this.dataFrame.getNumRows(); rowIndex++) {
				dataDF.setValue("color", rowIndex, legendColors[CommonArray.indexOf(legendValues, values[rowIndex])]);
			}
		}
		
		LegendData legendData = new LegendData();
		legendData.setColorData(legendValues, legendColors);
		legendData.setColorLabel(this.legendLabel);
		this.legend.setLegendData(legendData);
		this.legend.setDrawHueValueOutline(true);
		this.legend.setHueValueOutlienColor(Color.BLACK);
		this.legend.setHueValueOutlineWidth(1);
		this.legend.calculateLegend();
		
		String[][] map = new String[0][0];
		if (this.mapType == MapType.USAStates) {
			map = CommonGridCartograms.USAStatesMap;
		} else if (this.mapType == MapType.WorldCountries) {
			map = CommonGridCartograms.worldCountriesMap;
		}
		
		this.cm.setPlotWidth(this.plot.getSquareSize() * map[0].length);
		this.cm.setPlotHeight(this.plot.getSquareSize() * map.length);
		
		this.cm.calculateChartImageMetrics(this.legend, this.getTitle(), this.getTitleFont());
		
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(this.plot.getPlotBackgroundColor());
		
		this.plot.setPlotBackgroundColor(Color.WHITE);
		this.plot.drawPlotBackground(g, this.cm);
				
		this.plot.drawPlotOutline(g, this.cm);
		
		this.plot.drawPlot(g, dataDF, this.localitiesColumnName, map, this.cm);
		
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
