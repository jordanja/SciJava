package thesis.Charter.Charts;

import java.awt.Color;
import java.util.ArrayList;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.Legend.CategoricalLegend;
import thesis.Charter.Plots.USAMapPlot;
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
		dataDF.appendColumn("color");
		
		if (this.chartType == usaMapType.Gradient) {
			double maxValue = this.dataFrame.maxInColumn(this.valuesColumnName);
			double minValue = this.dataFrame.minInColumn(this.valuesColumnName);
			
			int valueIndex = this.dataFrame.getColumnNames().indexOf(this.valuesColumnName);
			double[] values = this.dataFrame.getColumnAsDoubleArray(valueIndex);
			int count = 0;
			Color baseColor = this.plot.getColorPalette()[0];
			for (ArrayList<DataItem> row: dataDF) {
				int opacity = (int)((values[count] - minValue) * (255/(maxValue - minValue)));
				dataDF.setValue("color", count, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), opacity));
				count++;
			}
			
			System.out.println(dataDF);
		} else if (this.chartType == usaMapType.Category) {
			String[] uniqueCategories = CommonArray.removeDuplicates(this.dataFrame.getColumnAsStringArray(this.valuesColumnName));
		}
		
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
