package thesis.Charter.Axis;

import java.util.HashMap;

import thesis.Common.CommonArray;
import thesis.Common.NiceScale;

public class StripChartAxis extends XYOneCategoricalAxis {

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

		this.numericalTicks = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];
		
		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.numericalTicks[i] = String.valueOf(tickValue);
		}
	}


}
