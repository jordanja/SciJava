package thesis.Helpers;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.awt.Color;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.LineChartAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Charter.PlotFolder.BarChart;
import thesis.Charter.PlotFolder.BarPlot;
import thesis.Charter.PlotFolder.BoxChart;
import thesis.Charter.PlotFolder.BoxPlot;
import thesis.Charter.PlotFolder.LineChart;
import thesis.Charter.PlotFolder.LinePlot;
import thesis.Charter.PlotFolder.ScatterChart;
import thesis.Charter.PlotFolder.ScatterPlot;
import thesis.DataFrame.*;
import thesis.NumJa.NumJa;


@SuppressWarnings("unused")
public class Main {

	public static void main(String[] args) {
		boxCharting();
//		lineCharting();
//		barCharting();
//		scatterCharting();
//		dfPlay();
				
		System.out.println("\n\nFINISHED EXECUTION");
	}
	
	public static void boxCharting() {
		DataFrame df = new DataFrame("Datasets/tips.csv", true);
		
//		BoxChart bc = new BoxChart(df,  "total_bill");
		BoxChart bc = new BoxChart(df, "day", "total_bill");
		BoxPlot plot = bc.getPlot();

		bc.colorCode("smoker");
		
		bc.setOrder(new String[] {"Thur", "Fri", "Sat", "Sun"});
		
		XYChartMeasurements cm = bc.getChartMeadurements();
		
		cm.setPlotWidth(800);
		
		plot.setBoxColorPalette(Palette.generateUniqueColors(10));
		
		
		bc.Create();
		bc.WriteFile("Chart Images/Box Chart.png");
	}
	
	
	public static void lineCharting() {
		DataFrame df = new DataFrame("Datasets/fmri.csv", true);
		System.out.println(df);
		
		LineChart lc = new LineChart(df, "timepoint", "signal");
		
		NumericAxis axis = (NumericAxis) lc.getAxis();
		LinePlot plot = lc.getPlot();
		
//		plot.setLineColor(Color.RED);
		plot.setLineThickness(2);
//		plot.setMarkerDotColor(Color.WHITE);
//		plot.setMarkerDotOutlineColor(Color.BLACK);
		
		plot.setLineColorPalette(Palette.generateUniqueColors(14));
		
		lc.colorCode("subject");
		
		axis.setXAxisLabel("sepal_length");
		axis.setYAxisLabel("sepal_width");
		
		lc.setTitle("sepal_length vs sepal_width");
		lc.setTitleFont(new Font("Dialog", Font.PLAIN, 20));
		
		
		lc.Create();
		lc.WriteFile("Chart Images/Line Chart.png");
		
	}
	
	
	private static void barCharting() {
		DataFrame df = new DataFrame("Datasets/tips_mod.csv", true);
	
		BarChart bc = new BarChart(df, "day", "total_bill");
		
		BarChartAxis axis = (BarChartAxis) bc.getAxis();
		BarPlot plot = bc.getPlot();
		XYChartMeasurements cm = bc.getChartMeadurements();
		
		axis.setIncludeBottomXAxisTicks(true, true);
		axis.setIncludeTopXAxisTicks(true, true);
		axis.setIncludeLeftYAxisTicks(true, true);
		axis.setIncludeRightYAxisTicks(true, true);
		
		bc.setTitle("sepal_length vs sepal_width");
		bc.setTitleFont(new Font("Dialog", Font.PLAIN, 20));
		
		bc.setOrder(new String[] {"Thur", "Fri", "Sat", "Sun"});
		bc.colorCode("sex");
		
		axis.setXAxisLabel("sepal_length");
		axis.setYAxisLabel("sepal_width");
		
		plot.setDrawBarOutline(true);
		plot.setBarOutlineColour(Color.BLUE);
		plot.setBarOutlineWidth(2);
		plot.setBarWidthPercentage(0.8f);
		
		plot.setBarColorPalette(Palette.Contrast );
		
		cm.setPlotWidth(1200);
		
		bc.Create();
		bc.WriteFile("Chart Images/Bar Chart.png");
	}
	
	
	private static void scatterCharting() {
		
		
		
		DataFrame df = new DataFrame("Datasets/iris.csv", true);
		
		ScatterChart sc = new ScatterChart(df, "sepal_length", "sepal_width");
		
		NumericAxis axis = (NumericAxis) sc.getAxis();
		ScatterPlot plot = (ScatterPlot) sc.getPlot();
		Legend legend = sc.getLegend();
		
		sc.setTitle("sepal_length vs sepal_width");
		sc.setTitleFont(new Font("Dialog", Font.PLAIN, 20));
		sc.colorCode("species");
		axis.setXAxisLabel("sepal_length");
		axis.setYAxisLabel("sepal_width");
		
		plot.includeDataPointOutline(true);
		
		XYChartMeasurements cm = sc.getChartMeasurements();
		cm.setLeftAxisLabelToLeftAxisWidth(15);
		cm.setTopAxisLabelToTitleHeight(15);
		
	
		
		cm.setPlotWidth(800);

		axis.setIncludeZeroXAxis(true);
		axis.setIncludeZeroYAxis(true);
		
		axis.setIncludeExteriorTicks(true, false, true, false);
		axis.setTickColor(Color.RED);
				
		cm.setTickLengths(20);
		axis.setTickThickness(2);
		
		
		axis.setIncludeBottomXAxisTicks(true, true);
		axis.setIncludeTopXAxisTicks(true, true);
		axis.setIncludeLeftYAxisTicks(true, true);
		axis.setIncludeRightYAxisTicks(true, true);
		
		axis.setIncludeTopXLabel(true);
		axis.setIncludeRightYLabel(true);

		axis.setIncludeTopXAxisValues(true);
		axis.setIncludeRightYAxisValues(true);

		plot.includeChartOutline(new boolean[] {true, false, true, false});
		plot.includeDataPointOutline(true);
		
		sc.setTitleFont(new Font("Dialog", Font.PLAIN, 30));

		axis.setXAxisLabel("I am x axis label");
		axis.setYAxisLabel("I am y axis label");
		cm.setLegendToImageRightWidth(20);
		cm.setImageBottomToBottomAxisLabelHeight(20);
		cm.setImageLeftToLeftAxisLabelWidth(20);
		cm.setTopAxisLabelToTitleHeight(20);
		
		cm.setTitleToImageTopHeight(20);
		
		cm.setBottomAxisLabelToBottomAxisHeight(15);
		cm.setTopAxisToTopAxisLabelHeight(15);
		cm.setLeftAxisLabelToLeftAxisWidth(15);
		cm.setRightAxisToRightAxisLabelWidth(15);
		
		cm.setRightAxisLabelToLegendWidth(40);
		
		sc.setIncludeLegend(false);
		
		
		
		sc.Create();
		sc.WriteFile("Chart Images/New Format.png");
		
	}
	
	
	
	
	

	private static void dfPlay() {
//		DataFrame df = play();
		DataFrame df = csvConstructor();
//		DataFrame df = hashColsConstructor();
//		DataFrame df = hashRowsConstructor();
//		DataFrame df = existingDataFrame();
//		DataFrame df = oneColumn();
//		DataFrame df = multipleColumns();
		
//		DataFrame df = arrays();
		
		System.out.println(df);
		
	}
	
	public static DataFrame play() {
		
		ArrayList<Object> arr1 = new ArrayList<Object>();
		arr1.add(1.3);
		arr1.add(2);
		arr1.add(3);
		
		ArrayList<Object> arr2 = new ArrayList<Object>();
		arr2.add(3);
		arr2.add(4);
		arr2.add(5);
		
		ArrayList<ArrayList<Object>> fin = new ArrayList<ArrayList<Object>>();
		fin.add(arr1);
		fin.add(arr2);
		
		ArrayList<String> names = new ArrayList<String>();
		names.add("one");
		names.add("two");
	
		DataFrame df = new DataFrame(names, fin, false);
		
		df.SetValue(1, 1, null);
		
		
		
		
		System.out.println(df.isNull());
		
		
		return df;
	}
	
	private static DataFrame hashColsConstructor() {
		HashMap<String,ArrayList<Object>> map = new HashMap<String,ArrayList<Object>>();
		
		ArrayList<Object> arr1 = new ArrayList<Object>();
		arr1.add(1);
		arr1.add(2);
		arr1.add(3);
		
		ArrayList<Object> arr2 = new ArrayList<Object>();
		arr2.add(3);
		arr2.add(4);
		arr2.add(5);
		
		map.put("one",arr1);
		map.put("two",arr2);
		
		DataFrame df = new DataFrame(map);
		return df;
	}

	private static DataFrame csvConstructor() {
		DataFrame df = new DataFrame("Datasets/date_data.csv",true);
		df.setColumnType(0, DataItem.StorageType.Date);
		return df;
	}
	
	private static DataFrame hashRowsConstructor() {
		HashMap<String,Object> map1 = new HashMap<String,Object>();
		map1.put("one",1);
		map1.put("two",2);
		map1.put("three",3);

		HashMap<String,Object> map2 = new HashMap<String,Object>();
		map2.put("one",10);
		map2.put("two",20);
		map2.put("three",30);
		
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		list.add(map1);
		list.add(map2);
		
		DataFrame df = new DataFrame(list);
		
		
		return df;
	}
	
	private static DataFrame existingDataFrame() {
		DataFrame df = new DataFrame(hashRowsConstructor());
		return df;
	}
	
	private static DataFrame oneColumn() {
		ArrayList<Object> arr = new ArrayList<Object>();
		arr.add(1);
		arr.add(2);
		arr.add(3);
		
		DataFrame df = new DataFrame("column", arr, true);
		return df;
	}
	
	public static DataFrame multipleColumns() {
		ArrayList<Object> arr1 = new ArrayList<Object>();
		arr1.add(1);
		arr1.add(2);
		arr1.add(3);
		
		ArrayList<Object> arr2 = new ArrayList<Object>();
		arr2.add(4);
		arr2.add(5);
		arr2.add(6);
		
		ArrayList<Object> arr3 = new ArrayList<Object>();
		arr3.add(7);
		arr3.add(8);
		arr3.add(9);
		
		ArrayList<ArrayList<Object>> combined = new ArrayList<ArrayList<Object>>();
		combined.add(arr1);
		combined.add(arr2);
		combined.add(arr3);
		
		
		ArrayList<String> names = new ArrayList<String>();
		names.add("one");
		names.add("two");
		names.add("three");
		
		DataFrame df = new DataFrame(names,combined,false);
		
		return df;
		
	}
	
	public static DataFrame arrays() {
		Integer[] list = new Integer[] {1,2,3,4,5};
		
		DataFrame df = new DataFrame("hello", list, false);
		
		return df;
	
	}
	
	
	
	
	
	

	private static void numJaPlay() {
//		NumJa numJa = new NumJa();
		
	}

}










































