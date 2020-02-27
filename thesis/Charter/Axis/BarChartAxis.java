package thesis.Charter.Axis;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import thesis.Auxiliary.NiceScale;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.DataFrame.DataItem;

public class BarChartAxis extends XYAxis {
	
	
	
	
	public void setXAxis(DataItem[] xData) {
		ArrayList<String> uniqueXValues = new ArrayList<String>();
		for (DataItem value: xData) {
			String strValue = (String) value.getStringValue();
			if (!uniqueXValues.contains(strValue)) {
				uniqueXValues.add(strValue);
			}
		}
		this.xTicks = new String[uniqueXValues.size()];
		this.xTicks = uniqueXValues.toArray(new String[uniqueXValues.size()]);
	}

	public void setYAxis(DataItem[] xData, DataItem[] yData) {
//		if (this.xTicks != null) {
//			// have access to xticks
//		} else {
//			// calculate xticks
//		}
		
		HashMap<String, Double> totals = new HashMap<String, Double>();
		for (int i = 0; i < yData.length; i++) {
			String key = (String) xData[i].getStringValue();
//			System.out.println(key);
			if (totals.containsKey(key)) {
				totals.put(key, totals.get(key) + yData[i].getDoubleValue());
			} else {
				totals.put(key, yData[i].getDoubleValue());
			}
		}
		
		double maxValue = 0;
		for (String key : totals.keySet()) {
		    if (totals.get(key) > maxValue) {
		    	maxValue = totals.get(key);
		    }
		}
		
		NiceScale yNS = new NiceScale(0, maxValue);
		
		this.yTicks = new String[1 + (int)(Math.ceil(yNS.getNiceMax()/yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.yTicks[i] = String.valueOf(tickValue);
		}
		
	}
	

	@Override
	public void drawAxis(Graphics2D g, XYChartMeasurements cm) {


		
	}

}
