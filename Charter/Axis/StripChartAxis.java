package thesis.Charter.Axis;

import java.util.HashMap;

import thesis.Common.CommonArray;
import thesis.Common.NiceScale;

public class StripChartAxis extends XYOneCategoricalAxis {

	public void setYAxis(Object data, String typeOfData) {

		double maxValue = Double.NEGATIVE_INFINITY;
		double minValue = Double.POSITIVE_INFINITY;

		if (typeOfData == "singleCategory") {
			Double[] dataList = (Double[]) data;
			maxValue = CommonArray.maxValue(dataList);
			minValue = CommonArray.minValue(dataList);
		} else if (typeOfData == "multipleCategoriesAndNoHueValue") {

			@SuppressWarnings("unchecked")
			HashMap<Object, Double[]> categoryMap = (HashMap<Object, Double[]>) data;
			for (Object category : categoryMap.keySet()) {
				Double[] dataList = categoryMap.get(category);
				maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
				minValue = Double.min(minValue, CommonArray.minValue(dataList));
			}

		} else if (typeOfData == "multipleCategoriesAndHueValue") {
			@SuppressWarnings("unchecked")
			HashMap<Object, HashMap<Object, Double[]>> categoryMap = (HashMap<Object, HashMap<Object, Double[]>>) data;
			for (Object category : categoryMap.keySet()) {
				HashMap<Object, Double[]> hueMap = categoryMap.get(category);
				for (Object hue : hueMap.keySet()) {
					Double[] dataList = hueMap.get(hue);
					maxValue = Double.max(maxValue, CommonArray.maxValue(dataList));
					minValue = Double.min(minValue, CommonArray.minValue(dataList));
				}
			}
		}
		NiceScale yNS = new NiceScale(minValue, maxValue);

		this.numericalTicks = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.numericalTicks[i] = String.valueOf(tickValue);
		}
	}


}
