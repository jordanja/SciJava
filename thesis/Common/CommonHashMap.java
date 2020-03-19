package thesis.Common;

import java.util.HashMap;

public class CommonHashMap {

	public static Double maxValueInHashMap(HashMap<Object, Object> map) {
		Double[] values = map.values().toArray(new Double[0]);
		return CommonArray.maxValue(values);
	}
	
	public static Double maxValueIn2DHashMap(HashMap<Object, HashMap<Object, Object>> map) {
		double max = Double.MIN_VALUE;
		for (Object xCatagory: map.keySet()) {
			HashMap<Object, Object> map2 = map.get(xCatagory);
			max = Double.max(max, CommonHashMap.maxValueInHashMap(map2));
		}
		return max;
	}
	
	public static Double maxValueInBlind2DHashMap(HashMap<Object, Object> map) {
		double max = Double.MIN_VALUE;
		for (Object xCatagory: map.keySet()) {
			HashMap<Object, Object> map2 = (HashMap<Object, Object>) map.get(xCatagory);
			max = Double.max(max, CommonHashMap.maxValueInHashMap(map2));
		}
		return max;
	}
}
