package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.GroupBy;

public class StackedAreaPlot extends Plot {

	private int lineThickness = 2;
	private boolean drawLine = true;
	
	private boolean fillUnderLine = true;
	private float fillOpacity = 100;

	
	public void drawPlot(Graphics2D g, NumericAxis axis, DataFrame df, String xColumnName, String yColumnName,
			String colorCodeColumnName, String[] lineOrder, XYChartMeasurements cm) {
		
		double[] xTicks = axis.getXTicksValues();
		double[] yTicks = axis.getYTicksValues();
		
		GroupBy gb = df.groupBy(xColumnName);
		String[] colorCodeValuesOrder = CommonArray.getUniqueValues(df.getColumnAsStringArray(colorCodeColumnName));
		
		String[] xAxisValuesStrings = gb.getKeysAsStrings();
		double[] xAxisValuesDoubles = gb.getKeysAsDoubles();
		
		String[] xAxisOrderedValuesStrings = sortArrayByOtherArray(xAxisValuesStrings, xAxisValuesDoubles);
		
		/*
		 * {
		 *   "1": {
		 *   	"Lakers": 80,
		 *   	"Bulls": 55,
		 *   },
		 *   "2": {..}
		 * }
		 */
		HashMap<String, HashMap<String, Double>> data = new HashMap<String, HashMap<String, Double>>();
		
		for (String xAxisValue: xAxisOrderedValuesStrings) {
			DataFrame xAxisDF = gb.getGroups().get(xAxisValue) ;
			
			data.put(xAxisValue, new HashMap<String, Double>());
			double runningTotal = 0;
			
			String[] categoryColumn = xAxisDF.getColumnAsStringArray(colorCodeColumnName);
			double[] yaxisValueColumn = xAxisDF.getColumnAsDoubleArray(yColumnName);
			
			for (String lineKey: lineOrder) {
				int indexOfLineKey = CommonArray.indexOf(categoryColumn, lineKey);
				runningTotal += yaxisValueColumn[indexOfLineKey];
				data.get(xAxisValue).put(lineKey, runningTotal);
			}
		}
		
		int lineCount = 0;
		int[] xLastLinePoints = new int[data.keySet().size()];
		int[] yLastLinePoints = new int[data.keySet().size()];
	    for (String line: lineOrder) {
			int[] xLinePoints = new int[data.keySet().size()];
			int[] yLinePoints = new int[data.keySet().size()];
			
			GeneralPath fillUnderLine = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			
			int linePointCount = 0;
	    	for (String xAxisValue: xAxisOrderedValuesStrings) {
	    		double xAxisValueDouble = Double.parseDouble(xAxisValue);
	    		
	    		xLinePoints[linePointCount] = xPlotValueToXPixelValue(xAxisValueDouble, xTicks, cm);
	    		yLinePoints[linePointCount] = yPlotValueToYPixelValue(data.get(xAxisValue).get(line), yTicks, cm);
	    		
	    		if (linePointCount == 0) {
	    			fillUnderLine.moveTo(xLinePoints[linePointCount], yLinePoints[linePointCount]);					
				} else {					
					fillUnderLine.lineTo(xLinePoints[linePointCount], yLinePoints[linePointCount]);
				}
	    		
	    		linePointCount++;
	    	}

	    	if (lineCount == 0) {
	    		fillUnderLine.lineTo(xLinePoints[xLinePoints.length - 1], yPlotValueToYPixelValue(yTicks[0], yTicks, cm));
	    		fillUnderLine.lineTo(xPlotValueToXPixelValue(xTicks[0], xTicks, cm), yPlotValueToYPixelValue(yTicks[0], yTicks, cm));
	    	} else {
	    		for (int index = data.keySet().size() - 1; index >= 0; index--) {
	    			fillUnderLine.lineTo(xLastLinePoints[index], yLastLinePoints[index]);
	    		}
	    	}
	    	
	    	if (this.drawLine) {
	    		g.setColor(this.colorPalette[lineCount]);
	    		g.setStroke(new BasicStroke(this.lineThickness));
	    		g.drawPolyline(xLinePoints, yLinePoints, data.keySet().size());
	    	}
	    	
	    	if (this.fillUnderLine) {
	    		Color c = this.colorPalette[lineCount];
	    		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)this.fillOpacity));
	    		g.fill(fillUnderLine);
	    	}
	    	
	    	xLastLinePoints = xLinePoints;
	    	yLastLinePoints = yLinePoints;
	    	
	    	lineCount += 1;
	    }
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

	public int getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}

	public boolean isDrawLine() {
		return drawLine;
	}

	public void setDrawLine(boolean drawLine) {
		this.drawLine = drawLine;
	}

	public boolean isFillUnderLine() {
		return fillUnderLine;
	}

	public void setFillUnderLine(boolean fillUnderLine) {
		this.fillUnderLine = fillUnderLine;
	}

	public float getFillOpacity() {
		return fillOpacity;
	}

	public void setFillOpacity(float fillOpacity) {
		this.fillOpacity = fillOpacity;
	}

}
