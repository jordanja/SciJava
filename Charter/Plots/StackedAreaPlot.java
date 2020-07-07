package thesis.Charter.Plots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.GroupBy;

public class StackedAreaPlot extends Plot {

	private Color lineColor = Color.black;
	private int lineThickness = 2;

	private boolean drawMarkerDots = false;
	private Color markerDotColor = Color.black;
	private int markerDotRadius = 5;
	private boolean drawMarkerDotOutline = false;
	private Color markerDotOutlineColor = Color.white;
	private int markerDotOutlineWidth = 2;

	private boolean dashedLine = false;
	
	String[] order = new String[0];
	
	public void drawPlot(Graphics2D g, NumericAxis axis, DataFrame df, String xColumnName, String yColumnName,
			String colorCodeColumnName, XYChartMeasurements cm) {
		
		GroupBy gb = df.groupBy(xColumnName);
		String[] colorCodeValuesOrder = CommonArray.getUniqueValues(df.getColumnAsStringArray(colorCodeColumnName));
		
		String[] xAxisValuesStrings = gb.getKeysAsStrings();
		double[] xAxisValuesDoubles = gb.getKeysAsDoubles();
		
		String[] xAxisOrderedValuesStrings = sortArrayByOtherArray(xAxisValuesStrings, xAxisValuesDoubles);
		
		for (String xAxisValue: gb.getGroups().keySet()) {
			System.out.println(xAxisValue);
			System.out.println(gb.getGroups().get(xAxisValue));
		}
		
	    CommonArray.printArray(xAxisValuesStrings);
	}
	
	private String[] sortArrayByOtherArray(String[] xAxisValuesStrings, double[] xAxisValues) {
		final List<String> stringListCopy = Arrays.asList(xAxisValuesStrings);
	    ArrayList<String> sortedList = new ArrayList<String>(stringListCopy);
	    Collections.sort(sortedList, (left, right) -> (int)(xAxisValues[stringListCopy.indexOf(left)] - xAxisValues[stringListCopy.indexOf(right)]));

	    return sortedList.toArray(new String[0]);
	}
	
	private int xPlotValueToXPixelValue(double xPos, double[] xTicks, XYChartMeasurements cm) {
		return CommonMath.map(xPos, xTicks[0], xTicks[xTicks.length - 1], cm.imageLeftToPlotLeftWidth(),
				cm.imageLeftToPlotRightWidth());
	}

	private int yPlotValueToYPixelValue(double yPos, double[] yTicks, XYChartMeasurements cm) {
		return CommonMath.map(yPos, yTicks[0], yTicks[yTicks.length - 1], cm.imageBottomToPlotBottomHeight(),
				cm.imageBottomToPlotTopHeight());
	}

}
