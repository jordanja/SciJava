package thesis.Charter.Axis;

import java.util.HashMap;

import thesis.Common.CommonHashMap;
import thesis.Common.NiceScale;

public class BarChartAxis extends XYOneCategoricalAxis {

	public void setYAxis(HashMap<Object, Object> data) {
		boolean haveColorCodeValues = (data.get(data.keySet().iterator().next()) instanceof HashMap);

		double maxY = 0;
		if (haveColorCodeValues) {
			maxY = CommonHashMap.maxValueInBlind2DHashMap(data);
		} else {
			maxY = CommonHashMap.maxValueInHashMap(data);
		}

		NiceScale yNS = new NiceScale(0, maxY);

		this.numericalTicks = new String[1 + (int) (Math.ceil(yNS.getNiceMax() / yNS.getTickSpacing()))];

		for (int i = 0; i * yNS.getTickSpacing() <= yNS.getNiceMax(); i++) {
			double tickValue = i * yNS.getTickSpacing();
			this.numericalTicks[i] = String.valueOf(tickValue);
		}
	}

}
