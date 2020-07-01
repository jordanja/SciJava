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
			@SuppressWarnings("unchecked")
			HashMap<Object, Object> map2 = (HashMap<Object, Object>) map.get(xCatagory);
			max = Double.max(max, CommonHashMap.maxValueInHashMap(map2));
		}
		return max;
	}
	
	public static void print3LevelHashMap(HashMap<String, HashMap<String, HashMap<String, Double>>> map) {
		for (String outerKey: map.keySet()) {
			System.out.println(outerKey + ": ");
			for (String midKey: map.get(outerKey).keySet()) {
				System.out.println("  " + midKey + ": ");
				for (String innerKey: map.get(outerKey).get(midKey).keySet()) {
					System.out.println("    " + innerKey + ": " + map.get(outerKey).get(midKey).get(innerKey));
				}
			}
		}
	}
	
	public static void print2LevelHashMap(HashMap<String, HashMap<String, Double>> map) {
		for (String outerKey: map.keySet()) {
			System.out.println(outerKey + ":");
			for (String innerKey: map.get(outerKey).keySet()) {
				System.out.println("  " + innerKey + ": " + map.get(outerKey).get(innerKey));
			}
			
		}
	}
}
