package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;

public class BoxChartAxis extends XYOneCategoricalAxis {
	
	private double maxValueInHashMap(HashMap<Object, Object> map) {
		return (double) map.get("Max");
	}
	
	private double minValueInHashMap(HashMap<Object, Object> map) {
		return (double) map.get("Min");
	}
	
	public void setXAxis(String[] xData) {
		this.xTicks = xData;
	}

	public void setYAxis(HashMap<Object, Object> data, String typeOfData) {		
		
		double maxValue = Double.NEGATIVE_INFINITY;
		double minValue = Double.POSITIVE_INFINITY;
		
		if (typeOfData == "singleCatagory") {
			maxValue = maxValueInHashMap(data);
			minValue = minValueInHashMap(data);

		} else if (typeOfData == "multipleCatagoriesAndNoHueValue") {
			for (Object catagory: data.keySet()) {
				HashMap<Object, Object> catagoryMap = (HashMap<Object, Object>) data.get(catagory);
				maxValue = Double.max(maxValue, maxValueInHashMap(catagoryMap));
				minValue = Double.min(minValue, minValueInHashMap(catagoryMap));
			}
			
		} else if (typeOfData == "multipleCatagoriesAndHueValue") {
			for (Object catagory: data.keySet()) {
				HashMap<Object, Object> catagoryMap = (HashMap<Object, Object>) data.get(catagory);
				for (Object hue: catagoryMap.keySet()) {
					HashMap<Object, Object> hueMap = (HashMap<Object, Object>) catagoryMap.get(hue);
					maxValue = Double.max(maxValue, maxValueInHashMap(hueMap));
					minValue = Double.min(minValue, minValueInHashMap(hueMap));
				}
			}
		}
		
		NiceScale yNS = new NiceScale(minValue, maxValue);
		
		
		this.yTicks = new String[1 + (int)(Math.ceil(yNS.getNiceMax()/yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.yTicks[i] = String.valueOf(tickValue);
		}
	}

}
