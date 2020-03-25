package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;

public class StripChartAxis extends XYOneCategoricalAxis {

	public void setXAxis(String[] xData) {
		this.xTicks = xData;
	}

	public void setYAxis(Object data, String typeOfData) {

		double maxValue = Double.NEGATIVE_INFINITY;
		double minValue = Double.POSITIVE_INFINITY;

		if (typeOfData == "singleCatagory") {
			Double[] dataList = (Double[]) data;
			maxValue = CommonArray.maxValue(dataList);
			minValue = CommonArray.minValue(dataList);

		} else if (typeOfData == "multipleCatagoriesAndNoHueValue") {

			HashMap<Object, Double[]> catagoryMap = (HashMap<Object, Double[]>) data;
			for (Object catagory : catagoryMap.keySet()) {
				Double[] dataList = catagoryMap.get(catagory);
				maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
				minValue = Double.min(minValue, CommonArray.minValue(dataList));
			}

		} else if (typeOfData == "multipleCatagoriesAndHueValue") {
			HashMap<Object, HashMap<Object, Double[]>> catagoryMap = (HashMap<Object, HashMap<Object, Double[]>>) data;
			for (Object catagory : catagoryMap.keySet()) {
				HashMap<Object, Double[]> hueMap = catagoryMap.get(catagory);
				for (Object hue : hueMap.keySet()) {
					Double[] dataList = hueMap.get(hue);
					maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
					minValue = Double.min(minValue, CommonArray.minValue(dataList));
				}
			}
		}
		NiceScale yNS = new NiceScale(minValue, maxValue);

		this.yTicks = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];

		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.yTicks[i] = String.valueOf(tickValue);
		}
	}


}
