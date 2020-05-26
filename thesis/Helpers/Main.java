package thesis.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.awt.Color;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.BarChartAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Charts.BarChart;
import thesis.Charter.Charts.BoxChart;
import thesis.Charter.Charts.GaugeChart;
import thesis.Charter.Charts.HistogramChart;
import thesis.Charter.Charts.LineChart;
import thesis.Charter.Charts.PieChart;
import thesis.Charter.Charts.PolarAreaChart;
import thesis.Charter.Charts.RadarChart;
import thesis.Charter.Charts.ScatterChart;
import thesis.Charter.Charts.StackedBarChart;
import thesis.Charter.Charts.StripChart;
import thesis.Charter.Legend.Legend;
import thesis.Charter.Plots.BarPlot;
import thesis.Charter.Plots.BoxPlot;
import thesis.Charter.Plots.LinePlot;
import thesis.Charter.Plots.PiePlot;
import thesis.Charter.Plots.ScatterPlot;
import thesis.Charter.Plots.StackedBarPlot;
import thesis.Charter.Plots.StripPlot;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonArray;
import thesis.DataFrame.*;
import thesis.NumJa.NumJa;

@SuppressWarnings("unused")
public class Main {

	public static void main(String[] args) {
		histogram();
//		stackedAreaChart();
//		pieChart();
//		stripCharting();
//		boxCharting();
//		lineCharting();
//		barCharting();
//		scatterCharting();
//		bubbleChart();
//		radarChart();
//		polarAreaChart();
//		gaugeChart();
//		dfPlay();
//		scatterChartingDiamond();

		System.out.println("\n\nFINISHED EXECUTION");
	}

	private static void histogram() {
		DataFrame df = new DataFrame("Datasets/histogram.csv", true);
		HistogramChart hc = new HistogramChart(df, "tree_heights");
		
		hc.getXYChartMeasurements().setPlotWidth(1000);
		
		hc.Create();
		hc.WriteFile("Chart Images/Histogram Chart.png");
		
	}

	private static void gaugeChart() {
		GaugeChart gc = new GaugeChart(0.3);
		
		gc.setTitleFont(new Font("Dialog", Font.PLAIN, 60));
		gc.setTitle("Gauge Chart");
		
		gc.getPlot().setArcColors(new Color[] {Color.RED, Color.ORANGE, 
				Color.GREEN});
		
		gc.getPlot().setInnerRadiusDifference(100);
		
		gc.Create();
		
		gc.write("Danger Zone", 210, 150, 
				xAlignment.LeftAlign, yAlignment.MiddleAlign, 
				Color.BLACK, new Font("Dialog", Font.PLAIN, 30), 0);
		gc.drawArrow(200, 150, 100, 250, Color.BLACK, 2);
		
		gc.WriteFile("Chart Images/Gauge Chart.png");
	}

	private static void polarAreaChart() {
		DataFrame df = new DataFrame("Datasets/own.csv", true);
		PolarAreaChart pac = new PolarAreaChart(df, "Fruit", "Quantity");
		
		pac.setTitleFont(new Font("Dialog", Font.PLAIN, 60));
		pac.setTitle("Polar Area Chart");
		
		pac.getPlot().setOutlineWidth(10);
		
		pac.Create();
		pac.WriteFile("Chart Images/Polar Area Chart.png");
	}

	private static void radarChart() {
		DataFrame df = new DataFrame("Datasets/radarchart.csv", true);
		
		RadarChart rc = new RadarChart(df, "Fruit", "Supermarket", "Quantity");
		
		rc.setTitleFont(new Font("Dialog", Font.PLAIN, 40));
		rc.setTitle("Supermarket Quantities of Fruit");
		
		rc.getChartMeasurements().setPlotWidth(600);
		rc.getChartMeasurements().setPlotHeight(600);
		
		rc.Create();
		rc.WriteFile("Chart Images/Radar Chart.png");
	}

	private static void stackedAreaChart() {
		DataFrame df = new DataFrame("Datasets/stacked.csv", true);
		System.out.println(df);
		
		StackedBarChart sbc = new StackedBarChart(df, "Quarter", "Sales", "Region");
		StackedBarPlot plot = sbc.getPlot();
		plot.setDrawValuesOnBar(true);
		
		sbc.Create();
		sbc.WriteFile("Chart Images/Stacked Bar Chart.png");
	}

	public static void pieChart() {
		DataFrame df = new DataFrame("Datasets/own.csv", true);
		PieChart pc = new PieChart(df, "Fruit", "Quantity");
		PiePlot plot = pc.getPlot();
		
		pc.setTitleFont(new Font("Dialog", Font.PLAIN, 50));
		pc.setTitle("Quantity of Fruit");
		
		plot.setShatter(new double[] {0.1, 0, 0, 0.3, 0});
		
		plot.setIncludeProportionsOnPie(true);
		plot.setProportionsColor(Color.WHITE);
		
		pc.Create();
		pc.WriteFile("Chart Images/Pie Chart.png");
	}
	
	
	public static void stripCharting() {
		DataFrame df = new DataFrame("Datasets/tips.csv", true);

//		StripChart sc = new StripChart(df,  "total_bill");
		StripChart sc = new StripChart(df, "day", "total_bill");
		sc.colorCode("smoker");

//		StripChart sc = new StripChart(df, "sex", "total_bill");
//		sc.colorCode("day");

		sc.setOrder(new String[] {"Thur", "Fri", "Sat", "Sun"});
		
		BaseAxis axis = sc.getAxis();
		axis.setXAxisFont(new Font("Dialog", Font.PLAIN, 80));

		StripPlot plot = sc.getPlot();
		
		XYChartMeasurements cm = sc.getXYChartMeasurements();
		cm.setPlotWidth(900);
		
		sc.Create();
		sc.WriteFile("Chart Images/Strip Chart.png");
	}

	public static void boxCharting() {
		DataFrame df = new DataFrame("Datasets/tips.csv", true);

//		BoxChart bc = new BoxChart(df,  "total_bill");
		BoxChart bc = new BoxChart(df, "day", "total_bill");

		bc.colorCode("smoker"); 

		bc.setOrder(new String[] { "Thur", "Fri", "Sat", "Sun" });

		XYChartMeasurements cm = bc.getXYChartMeasurements();

		cm.setPlotWidth(800);

		BoxPlot plot = bc.getPlot();
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

 		plot.setShadeUnderLine(true);
		
		plot.setLineColor(Color.RED);
		plot.setLineThickness(2);
		plot.setMarkerDotColor(Color.WHITE);
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
		XYChartMeasurements cm = bc.getXYChartMeasurements();

		axis.setIncludeBottomXAxisTicks(true, true);
		axis.setIncludeTopXAxisTicks(true, true);
		axis.setIncludeLeftYAxisTicks(true, true);
		axis.setIncludeRightYAxisTicks(true, true);

		bc.setTitle("sepal_length vs sepal_width");
		bc.setTitleFont(new Font("Dialog", Font.PLAIN, 20));

		bc.setOrder(new String[] { "Thur", "Fri", "Sat", "Sun" });
		bc.colorCode("sex");

		axis.setXAxisLabel("sepal_length");
		axis.setYAxisLabel("sepal_width");

		axis.setXAxisFont(new Font("Dialog", Font.PLAIN, 80));
		axis.setYAxisFont(new Font("Dialog", Font.PLAIN, 80));

		plot.setDrawBarOutline(true);
		plot.setBarOutlineColour(Color.BLUE);
		plot.setBarOutlineWidth(2);
		plot.setBarWidthPercentage(0.8f);

		plot.setBarColorPalette(Palette.Contrast);

		cm.setPlotWidth(1200);
		
		axis.setOrientation("h");
		

		bc.Create();
		bc.WriteFile("Chart Images/Bar Chart.png");
	}
	
	private static void scatterChartingDiamond() {
		DataFrame dfDiamonds = new DataFrame("Datasets/diamonds.csv", true);

		ScatterChart themeCategorical = new ScatterChart(dfDiamonds, "carat", "price");

		themeCategorical.colorCode("clarity");

		themeCategorical.setTitle("Diamon Analysis by clarity");
		themeCategorical.setTitleFont(new Font("Dialog", Font.PLAIN, 20));
		
		themeCategorical.Create();
		themeCategorical.WriteFile("Chart Images/Scatter Chart (diamond).png");
	}
	
	private static void bubbleChart() {
		DataFrame df = new DataFrame("Datasets/bubble.csv", true);

		ScatterChart sc = new ScatterChart(df, "age", "height");

		NumericAxis axis = sc.getAxis();
		ScatterPlot plot = sc.getPlot();
		Legend legend = sc.getLegend();
		
		sc.colorCode("gender");
		sc.setBubbleSize("wealth");
		
		sc.Create();
		sc.WriteFile("Chart Images/Bubble Chart.png");

	}

	private static void scatterCharting() {

		DataFrame df = new DataFrame("Datasets/tips.csv", true);

		ScatterChart sc = new ScatterChart(df, "total_bill", "tip");

		NumericAxis axis = sc.getAxis();
		ScatterPlot plot = sc.getPlot();
		Legend legend = sc.getLegend();

		sc.setTitle("sepal_length vs sepal_width");
		sc.setTitleFont(new Font("Dialog", Font.PLAIN, 20));
		sc.colorCode("sex");
		axis.setXAxisLabel("sepal_length");
		axis.setYAxisLabel("sepal_width");

//		axis.setXAxisFont(new Font("Dialog", Font.BOLD, 30));
//		axis.setYAxisFont(new Font("Dialog", Font.BOLD, 30));
//		axis.setXAxisLabelFont(new Font("Dialog", Font.BOLD, 30));
//		axis.setYAxisLabelFont(new Font("Dialog", Font.BOLD, 30));
		
		plot.includeDataPointOutline(true);
		plot.includeLinearRegression(true);
		
		XYChartMeasurements cm = sc.getXYChartMeasurements();
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

		plot.includePlotOutline(new boolean[] { true, false, true, false });
		plot.includeDataPointOutline(true);

//		axis.xAxisRotation(30);
//		axis.yAxisRotation(45);
		
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

		sc.setIncludeLegend(true);

		sc.Create();
		sc.WriteFile("Chart Images/Scatter Chart.png");

	}

	private static void dfPlay() {
//		DataFrame df = play();
//		DataFrame df = csvConstructor();
//		DataFrame df = hashColsConstructor();
//		DataFrame df = hashRowsConstructor();
//		DataFrame df = existingDataFrame();
//		DataFrame df = oneColumn();
//		DataFrame df = multipleColumns();
//		DataFrame df = arrays();

		DataFrame df = time();
		
		System.out.println(df);

	}

	public static DataFrame time() {
		DataFrame df = new DataFrame("Datasets/time.csv", true);
		df.setColumnType(0, DataItem.StorageType.Date);
		df.setColumnType(1, DataItem.StorageType.Integer);
		System.out.println(df.getValue(0, 0).getType());
		System.out.println(df.getValue(1, 0).getType());
		
		return df;
	}
	
	public static DataFrame play() {

		HashMap<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>();

		ArrayList<Object> arr1 = new ArrayList<Object>();
		arr1.add(1);
		arr1.add(2);
		arr1.add(3);

		ArrayList<Object> arr2 = new ArrayList<Object>();
		arr2.add(LocalDate.now());
		arr2.add(LocalDate.now().plusDays(1));
		arr2.add(LocalDate.now().plusDays(2));

		map.put("one", arr1);
		map.put("two", arr2);

		DataFrame df = new DataFrame(map);

		Integer[] newCol = new Integer[] { 100, 200, 300 };
		df.insertColumn(1, "newCol", newCol);

		return df;
	}

	private static DataFrame hashColsConstructor() {
		HashMap<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>();

		ArrayList<Object> arr1 = new ArrayList<Object>();
		arr1.add(1);
		arr1.add(2);
		arr1.add(3);

		ArrayList<Object> arr2 = new ArrayList<Object>();
		arr2.add(3);
		arr2.add(4);
		arr2.add(5);

		map.put("one", arr1);
		map.put("two", arr2);

		DataFrame df = new DataFrame(map);
		return df;
	}

	private static DataFrame csvConstructor() {
		DataFrame df = new DataFrame("Datasets/date_data.csv", true);
		df.setColumnType(0, DataItem.StorageType.Date);
		return df;
	}

	private static DataFrame hashRowsConstructor() {
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("one", 1);
		map1.put("two", 2);
		map1.put("three", 3);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("one", 10);
		map2.put("two", 20);
		map2.put("three", 30);

		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
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

		DataFrame df = new DataFrame("hello", arr, true);
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

		DataFrame df = new DataFrame(names, combined, true);

		return df;

	}

	public static DataFrame arrays() {
		Integer[] list = new Integer[] { 1, 2, 3, 4, 5 };

		DataFrame df = new DataFrame("hello", list, true);

		return df;

	}

	private static void numJaPlay() {
//		NumJa numJa = new NumJa();

	}

}
