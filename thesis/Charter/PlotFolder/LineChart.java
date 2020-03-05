package thesis.Charter.PlotFolder;

import java.util.ArrayList;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.LineChartMeasurements;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public class LineChart extends XYChart{

	
	NumericAxis axis;
	LinePlot plot;
	Legend legend;
	
	public LineChart(DataFrame dataFrame, String xAxis, String yAxis) {
		super(dataFrame, dataFrame.GetColumnAsArray(xAxis), dataFrame.GetColumnAsArray(yAxis), "Bar");
		
		this.axis = new NumericAxis();
		this.plot = new LinePlot();
		this.legend = new Legend();
		
		cm = new LineChartMeasurements();

	}

	@Override
	public void Create() {
		String[] xValues = convertDataItemListToStringList(this.xData);
		
		
		
		
//		this.axis.calculateXAxis(0, 10);
	}
	
	private String[] convertDataItemListToStringList(DataItem[] list) {
		String[] stringList = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			stringList[i] = list[i].getValueConvertedToString();
		}
		
		return stringList;
	}

	private String[] getXDataOrdered() {
		ArrayList<String> foundXCatagories = new ArrayList<String>();
		for (DataItem xValue : this.xData) {
			if (!foundXCatagories.contains(xValue.getValueConvertedToString())) {
				foundXCatagories.add(xValue.getValueConvertedToString());
			}
		}
		
		String[] xDataFormatted = new String[foundXCatagories.size()];
		xDataFormatted = foundXCatagories.toArray(xDataFormatted);
		return xDataFormatted;
	}
	
	public Axis getAxis() {
		return this.axis;
	}

	public LinePlot getPlot() {
		return this.plot;
	}

	public Legend getLegend() {
		return this.legend;
	}

}
