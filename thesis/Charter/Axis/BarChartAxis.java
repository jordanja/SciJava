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
import thesis.Common.CommonArray;
import thesis.Common.CommonHashMap;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;

public class BarChartAxis extends XYOneCategoricalAxis {
	
	public void setXAxis(String[] xData) {
		this.xTicks = xData;
	}

	public void setYAxis(HashMap<Object, Object> data) {		
		boolean haveColorCodeValues = (data.get(data.keySet().iterator().next()) instanceof HashMap);
			
		double maxY = 0;
		if (haveColorCodeValues) {
			maxY = CommonHashMap.maxValueInBlind2DHashMap(data);
		} else {
			maxY = CommonHashMap.maxValueInHashMap(data);
		}
		
		NiceScale yNS = new NiceScale(0, maxY);
		
		
		this.yTicks = new String[1 + (int)(Math.ceil(yNS.getNiceMax()/yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.yTicks[i] = String.valueOf(tickValue);
		}
	}


}
