package thesis.Common;

import java.util.List;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import thesis.DataFrame.DataItem;

public class CommonArray {
	
	public static Double[] removeDuplicates(Double[] list) {
		ArrayList<Double> newList = new ArrayList<Double>();
		for (int i = 0; i < list.length; i++) {
			if (!newList.contains(list[i])) {
				newList.add(list[i]);
			}
		}
		return newList.toArray(new Double[0]);
	}
	
	public static Object[] removeDuplicates(Object[] list) {
		ArrayList<Object> newList = new ArrayList<Object>();
		for (int i = 0; i < list.length; i++) {
			if (!newList.contains(list[i])) {
				newList.add(list[i]);
			}
		}
		return newList.toArray(new Object[0]);
	}
	
	public static String[] removeDuplicates(String[] list) {
		ArrayList<String> newList = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			if (!newList.contains(list[i])) {
				newList.add(list[i]);
			}
		}
		return newList.toArray(new String[0]);

	}
	
	public static double average(double[] arr) {
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum +=  arr[i];
		}
		
		return sum/arr.length;
	}
	
	public static double average(int[] arr) {
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum +=  (double)arr[i];
		}
		
		return sum/(double)arr.length;
	}
	
	public static int sum(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}
	
	public static double sum(double[] arr) {
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}
	
	public static int product(int[] arr) {
		int sum = arr[0];
		for (int i = 1; i < arr.length; i++) {
			sum *= arr[i];
		}
		return sum;
	}
	
	public static double product(double[] arr) {
		double sum = arr[0];
		for (int i = 1; i < arr.length; i++) {
			sum *= arr[i];
		}
		return sum;
	}
	
	// inspirtion taken from here https://stackoverflow.com/a/29373490/6122201
	public static int mode(int[] arr) {
	    HashMap<Integer,Integer> countOccourences = new HashMap<Integer,Integer>();
	    
	    int maxOccourences  = 1;
	    int mostCommonValue = 0;
	    for(int valueCount = 0; valueCount < arr.length; valueCount++) {

	        if (countOccourences.get(arr[valueCount]) != null) {

	            int count = countOccourences.get(arr[valueCount]);
	            count++;
	            countOccourences.put(arr[valueCount], count);

	            if(count > maxOccourences) {
	                maxOccourences  = count;
	                mostCommonValue = arr[valueCount];
	            }
	        } else {	        	
	        	countOccourences.put(arr[valueCount],1);
	        }
	    }
	    return mostCommonValue;
	}
	
	public static double mode(double[] arr) {
	    HashMap<Double,Integer> countOccourences = new HashMap<Double,Integer>();
	    
	    int maxOccourences  = 1;
	    double mostCommonValue = 0;
	    for(int valueCount = 0; valueCount < arr.length; valueCount++) {

	        if (countOccourences.get(arr[valueCount]) != null) {

	            int count = countOccourences.get(arr[valueCount]);
	            count++;
	            countOccourences.put(arr[valueCount], count);

	            if(count > maxOccourences) {
	                maxOccourences  = count;
	                mostCommonValue = arr[valueCount];
	            }
	        } else {	        	
	        	countOccourences.put(arr[valueCount],1);
	        }
	    }
	    return mostCommonValue;
	}
	
	public static double median(int arr[]) { 
		int length = arr.length;
		Arrays.sort(arr);  

		if (length % 2 != 0) { 			
			return (double)arr[length / 2]; 
		} else {			
			return (double)(arr[(length - 1) / 2] + arr[length / 2]) / 2.0; 
		}
	}
	
	public static double median(double arr[]) { 
		int length = arr.length;
		Arrays.sort(arr);  

		if (length % 2 != 0) { 			
			return arr[length / 2]; 
		} else {			
			return (arr[(length - 1) / 2] + arr[length / 2]) / 2.0; 
		}
	}
	
	public static Double minValue(Double[] arr) {
		Double min = Double.MAX_VALUE;
		
		for (Double value : arr) {
			min = Double.min(min, value);
		}
		return min;
	}
	
	public static Double maxValue(Double[] arr) {
		Double max = Double.MIN_VALUE;
		
		for (Double value : arr) {
			max = Double.max(max, value);
		}
		return max;
	}
	
	public static Integer maxValue(Integer[] arr) {
		Integer max = Integer.MIN_VALUE;
		
		for (Integer value : arr) {
			max = Integer.max(max, value);
		}
		return max;
	}
	
	public static int maxValue(int[] arr) {
		int max = Integer.MIN_VALUE;
		
		for (int value : arr) {
			max = Integer.max(max, value);
		}
		return max;
	}
	
	public static double maxValue(double[] arr) {
		double max = Double.MIN_VALUE;
		
		for (double value : arr) {
			max = Double.max(max, value);
		}
		return max;
	}
	
	public static Double minValue(double[] arr) {
		Double min = Double.MAX_VALUE;
		
		for (Double value : arr) {
			min = Double.min(min, value);
		}
		return min;
	}
	
	public static int indexOf(String[] arr, String element) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(element)) {
				return i;
			}
		}
		return -1;
	}
	
	public static String[] orderArrayByOtherArray(String[] arrToOrder, String[] order) {
		int nextIndex = 0;
		for (int i = 0; i < order.length; i++) {
			String catagoryToBeOrdered = order[i];
			int indexOfNextToOrder = CommonArray.indexOf(arrToOrder, catagoryToBeOrdered);
			if (indexOfNextToOrder != -1) {				
				for (int reorderIndex = indexOfNextToOrder; reorderIndex > nextIndex; reorderIndex--) {
					arrToOrder[reorderIndex] = arrToOrder[reorderIndex-1];
				}
				arrToOrder[nextIndex] = catagoryToBeOrdered;
				nextIndex++;
			}
		}
		return arrToOrder;
	}

	
	public static boolean valuesUnique(ArrayList<String> values) {
		Set<String> set = new HashSet<String>(values);
		
		if(set.size() < values.size()){
			return false;
		}
		return true;
	}
	
	public static boolean anyNullValues(ArrayList<String> rowNamesToAdd) {
		for (int i = 0; i < rowNamesToAdd.size(); i++) {
			if (rowNamesToAdd.get(i) == null) {
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList<String> generateIncreasingSequence(int size) {
		ArrayList<String> rowNames = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			rowNames.add(String.valueOf(i));
		}
		return rowNames;
	}
	
	public static Object[] convertStringArrayToObjectArray(String[] arr) {
		Object[] objArr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			objArr[i] = (Object) arr[i];
		}
		return objArr;
	}
	
	public static String[] convertObjectArrayToStringArray(Object[] arr) {
		String[] objArr = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			objArr[i] = arr[i].toString();
		}
		return objArr;
	}
	
	public static ArrayList<String> convertStringArrayToArrayList(String[] arr) {
		ArrayList<String> newArr = new ArrayList<String>();
		for (String value: arr) {
			newArr.add(value);
		}
		return newArr;
	}
	
	public static int[] convertIntegerArrayListToArray(ArrayList<Integer> arr) {
		int[] newArr = new int[arr.size()];
		for (int index = 0; index < arr.size(); index++) {
			newArr[index] = arr.get(index);
		}
		return newArr;
	}
	
	public static ArrayList<Object> convertStringArrayToObjectArrayList(String[] arr) {
		ArrayList<Object> newArr = new ArrayList<Object>();
		for (String value: arr) {
			newArr.add(value);
		}
		return newArr;
	}
	
	public static List<Object> convertArrayToObjectList(int[] arr) {
		return Arrays.stream(arr).boxed().collect(Collectors.toList());
	}
	
	public static List<Object> convertArrayToObjectList(double[] arr) {
		return Arrays.stream(arr).boxed().collect(Collectors.toList());
	}
	
	public static List<Object> convertArrayToObjectList(boolean[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (boolean element: arr) {
			list.add(element);
		}
		return list;
	}

	public static List<Object> convertArrayToObjectList(LocalDate[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (LocalDate element: arr) {
			list.add(element);
		}
		return list;
	}
	
	public static List<Object> convertArrayToObjectList(LocalDateTime[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (LocalDateTime element: arr) {
			list.add(element);
		}
		return list;
	}
	
	public static List<Object> convertArrayToObjectList(LocalTime[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (LocalTime element: arr) {
			list.add(element);
		}
		return list;
	}
	
	public static List<Object> convertArrayToObjectList(Period[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Period element: arr) {
			list.add(element);
		}
		return list;
	}
	
	public static List<Object> convertArrayToObjectList(Duration[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Duration element: arr) {
			list.add(element);
		}
		return list;
	}
	
	public static List<Object> convertArrayToObjectList(BigDecimal[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (BigDecimal element: arr) {
			list.add(element);
		}
		return list;
	}
	
	public static List<Object> convertArrayToObjectList(String[] arr) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (String element: arr) {
			list.add(element);
		}
		return list;
	}

	public static double[] convertFloatArrayToDoubleArray(float[] arr) {
		double[] newArr = new double[arr.length];
		for (int counter = 0; counter < arr.length; counter++) {
			newArr[counter] = arr[counter];
		}
		return newArr;
	}
	
	
	
	public static double[] initializeArrayWithValues(int length, int value) {
		double[] arr = new double[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static int[] initializeIntArrayWithValues(int length, int value) {
		int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static double[] initializeDoubleArrayWithValues(int length, double value) {
		double[] arr = new double[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static float[] initializeFloatArrayWithValues(int length, float value) {
		float[] arr = new float[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static boolean[] initializeBooleanArrayWithValues(int length, boolean value) {
		boolean[] arr = new boolean[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static String[] initializeStringArrayWithValues(int length, String value) {
		String[] arr = new String[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static LocalDate[] initializeLocalDateArrayWithValues(int length, LocalDate value) {
		LocalDate[] arr = new LocalDate[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static LocalDateTime[] initializeLocalDateTimeArrayWithValues(int length, LocalDateTime value) {
		LocalDateTime[] arr = new LocalDateTime[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static LocalTime[] initializeLocalTimeArrayWithValues(int length, LocalTime value) {
		LocalTime[] arr = new LocalTime[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static Period[] initializePeriodArrayWithValues(int length, Period value) {
		Period[] arr = new Period[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static Duration[] initializeDurationArrayWithValues(int length, Duration value) {
		Duration[] arr = new Duration[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static BigDecimal[] initializeBigDecimalArrayWithValues(int length, BigDecimal value) {
		BigDecimal[] arr = new BigDecimal[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static Object[] initializeObjectArrayWithValues(int length, Object value) {
		Object[] arr = new Object[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}
	
	public static DataItem[] initializeDataItemArrayWithValues(int length, DataItem value) {
		DataItem[] arr = new DataItem[length];
		for (int i = 0; i < length; i++) {
			arr[i] = value.clone();
		}
		return arr;
	}
	
	public static <T> T[] initializeGenericArrayWithValues(int length, T value) {
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(value.getClass(), length);
		for (int i = 0; i < length; i++) {
			arr[i] = value;
		}
		return arr;
	}

	public static <T> void printArray(List<T> arr) {
		for (int i = 0; i < arr.size(); i++) {
			System.out.println(i + ": " + arr.get(i));
		}
	}
	
	public static void printArray(Object[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(i + ": " + arr[i]);
		}
	}
	public static void printStringArrayList(ArrayList<String> arr) {
		for (int i = 0; i < arr.size(); i++) {
			System.out.println(i + ": " + arr.get(i));
		}
	}
	
	public static void printObjectArrayList(ArrayList<Object> arr) {
		for (int i = 0; i < arr.size(); i++) {
			System.out.println(i + ": " + arr.get(i));
		}
	}
	
	public static void printIntegerArrayList(ArrayList<Integer> arr) {
		for (int i = 0; i < arr.size(); i++) {
			System.out.println(i + ": " + arr.get(i));
		}
	}
	
	public static void printArray(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(i + ": " + arr[i]);
		}
	}

	public static void printArray(float[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(i + ": " + arr[i]);
		}
	}
	public static void printArray(double[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(i + ": " + arr[i]);
		}
	}
	
	public static void printArray(boolean[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(i + ": " + arr[i]);
		}
	}
	
	public static <T> void print2DList(List<List<T>> list) {
		for (int outerCount = 0; outerCount < list.size(); outerCount++) {
			for (int innerCount = 0; innerCount < list.get(outerCount).size(); innerCount++) {
				System.out.print(list.get(outerCount).get(innerCount));
				if (innerCount < list.get(outerCount).size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
		
	}
	
	public static void print2DArray(Object[][] arr) {
		for (int outerCount = 0; outerCount < arr.length; outerCount++) {
			for (int innerCount = 0; innerCount < arr[outerCount].length; innerCount++) {
				System.out.print(arr[outerCount][innerCount]);
				if (innerCount < arr[outerCount].length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
		
	}
	
	public static void print2DArray(boolean[][] arr) {
		for (int outerCount = 0; outerCount < arr.length; outerCount++) {
			for (int innerCount = 0; innerCount < arr[outerCount].length; innerCount++) {
				System.out.print(arr[outerCount][innerCount]);
				if (innerCount < arr[outerCount].length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}
	
	public static void print2DArray(float[][] arr) {
		for (int outerCount = 0; outerCount < arr.length; outerCount++) {
			for (int innerCount = 0; innerCount < arr[outerCount].length; innerCount++) {
				System.out.print(arr[outerCount][innerCount]);
				if (innerCount < arr[outerCount].length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}
	
	public static void print2DArray(int[][] arr) {
		for (int outerCount = 0; outerCount < arr.length; outerCount++) {
			for (int innerCount = 0; innerCount < arr[outerCount].length; innerCount++) {
				System.out.print(arr[outerCount][innerCount]);
				if (innerCount < arr[outerCount].length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}
	
	public static void print2DArray(double[][] arr) {
		for (int outerCount = 0; outerCount < arr.length; outerCount++) {
			for (int innerCount = 0; innerCount < arr[outerCount].length; innerCount++) {
				System.out.print(arr[outerCount][innerCount]);
				if (innerCount < arr[outerCount].length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}

	public static String[] getAllValuesOfKey(Map<String, Object>[] listOfMaps, String key) {
		String[] list = new String[listOfMaps.length];
		for (int mapCount = 0; mapCount < listOfMaps.length; mapCount++) {
			if (listOfMaps[mapCount].containsKey(key)) {
				list[mapCount] = (String) listOfMaps[mapCount].get(key);
			}
		}
		return list;
	}

	public static Double[] getAllDoubleValuesOfKey(Map<String, Object>[] listOfMaps, String key) {
		Double[] list = new Double[listOfMaps.length];
		for (int mapCount = 0; mapCount < listOfMaps.length; mapCount++) {
			if (listOfMaps[mapCount].containsKey(key)) {
				list[mapCount] = (Double) listOfMaps[mapCount].get(key);
			}
		}
		return list;
	}

	public static String[] mangle(List<String> arr) {
		return mangle(arr.toArray(new String[0]));
	}
	
	public static String[] mangle(String[] columnNamesProvided) {
		int[] numberOfSameValueBefore = CommonArray.initializeIntArrayWithValues(columnNamesProvided.length, 0);
		for (int columnCount = 0; columnCount < columnNamesProvided.length; columnCount++) {
			int countSame = 0;
			for (int inner = 0; inner < columnCount; inner++) {
				if (columnNamesProvided[inner].equals(columnNamesProvided[columnCount])) {
					countSame++;
				}
			}
			numberOfSameValueBefore[columnCount] = countSame;
		}
		for (int columnCount = 0; columnCount < columnNamesProvided.length; columnCount++) {
			if (numberOfSameValueBefore[columnCount] > 0) {
				columnNamesProvided[columnCount] = columnNamesProvided[columnCount] + "." + numberOfSameValueBefore[columnCount];
			}
		}
		return columnNamesProvided;
	}
	
	public static String getNewMangleName(ArrayList<String> arr, String potentialName) {
		return getNewMangleName(arr.toArray(new String[0]), potentialName);
	}
	
	public static String getNewMangleName(String[] arr, String potentialName) {
		if (!Arrays.stream(arr).anyMatch(potentialName::equals)) {
			return potentialName;
		}
		boolean found = false;
		int suffix = 1;
		while (found == false) {
			String nameToCheck = potentialName + "." + suffix;
			
			if (!Arrays.stream(arr).anyMatch(nameToCheck::equals)) {
				return nameToCheck;
			}
			suffix++;
		}
		return "error";
	}
	
	public static int[] getIndicesOfStringsInArray(String[] arr, String[] elements) {
		int[] indices = new int[elements.length];
		
		for(int elementCount = 0; elementCount < elements.length; elementCount++) {
			indices[elementCount] = CommonArray.indexOf(arr, elements[elementCount]);
		}
		
		return indices;
	}
	
	public static int[] getIndicesOfStringsInArray(ArrayList<String> arr, String[] elements) {
		return getIndicesOfStringsInArray(arr.toArray(new String[0]), elements);
	}
	
	public static int[] getIndicesOfStringsInArray(ArrayList<String> arr, ArrayList<String> elements) {
		return getIndicesOfStringsInArray(arr.toArray(new String[0]), elements.toArray(new String[0]));
	}
	
	public static boolean contains(String[] arr, String element) {
		for (String str: arr) {
			if (str.equals(element)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean contains(int[] arr, int element) {
		for (int str: arr) {
			if (str == element) {
				return true;
			}
		}
		return false;
	}
	
	public static int[] reverse(int[] arr) {
		for(int i = 0; i < arr.length/2; i++) {
		    int temp = arr[i];
		    arr[i] = arr[arr.length - i - 1];
		    arr[arr.length - i - 1] = temp;
		}
		return arr;
	}

	// inspiration taken from here https://stackoverflow.com/a/20536597/6122201
	public static String randomString(int length) {
		String chars = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM";
        StringBuilder str = new StringBuilder();
        Random rnd = new Random();
        while (str.length() < length) { 
            int index = (int) (rnd.nextFloat() * chars.length());
            str.append(chars.charAt(index));
        }
        return str.toString();
	}
	
	public static ArrayList<String> removeDuplicates(ArrayList<String> arr1, ArrayList<String> arr2) {
		ArrayList<String> removedDuplicates = new ArrayList<String>();
		for (String str: arr1) {
			if (!removedDuplicates.contains(str)) {
				removedDuplicates.add(str);
			}
		}
		for (String str: arr2) {
			if (!removedDuplicates.contains(str)) {
				removedDuplicates.add(str);
			}
		}
		return removedDuplicates;
		
	}
	
	public static ArrayList<String> commonStrings(ArrayList<String> arr1, ArrayList<String> arr2) {
		ArrayList<String> commonElements = new ArrayList<String>();

        for(int arr1Counter = 0; arr1Counter < arr1.size(); arr1Counter++) {
            for(int arr2Counter = 0; arr2Counter < arr2.size(); arr2Counter++) {
            	String str = arr1.get(arr1Counter);
	            if(str.equals(arr2.get(arr2Counter))) {  
	                if(!commonElements.contains(str)) {
	                    commonElements.add(str);
	                }
	            }
            }
        }
        return commonElements;
	}
	
	public static ArrayList<String> uncommonStrings(ArrayList<String> arr1, ArrayList<String> arr2) {
		ArrayList<String> uncommonElements = new ArrayList<String>();

		for (String str: arr1) {
			if (!arr2.contains(str)) {
				if (!uncommonElements.contains(str)) {
					uncommonElements.add(str);
				}
			}
		}
        return uncommonElements;
	}
	
	public static int numberOfCommonStrings(ArrayList<String> arr1, ArrayList<String> arr2) {
		return commonStrings(arr1, arr2).size();
	}

	public static double[] cumulativeMax(double[] arr) {
		double[] cumulativeMax = new double[arr.length];
		double currentMax = arr[0];
		for (int index = 0; index < arr.length; index++) {
			currentMax = Double.max(currentMax, arr[index]);
			cumulativeMax[index] = currentMax;
		}
		return cumulativeMax;
	}
	
	public static int[] cumulativeMax(int[] arr) {
		int[] cumulativeMax = new int[arr.length];
		int currentMax = arr[0];
		for (int index = 0; index < arr.length; index++) {
			currentMax = Integer.max(currentMax, arr[index]);
			cumulativeMax[index] = currentMax;
		}
		return cumulativeMax;
	}
	
	public static double[] cumulativeMin(double[] arr) {
		double[] cumulativeMin = new double[arr.length];
		double currentMin = arr[0];
		for (int index = 0; index < arr.length; index++) {
			currentMin = Double.min(currentMin, arr[index]);
			cumulativeMin[index] = currentMin;
		}
		return cumulativeMin;
	}
	
	public static int[] cumulativeMin(int[] arr) {
		int[] cumulativeMin = new int[arr.length];
		int currentMin = arr[0];
		for (int index = 0; index < arr.length; index++) {
			currentMin = Integer.min(currentMin, arr[index]);
			cumulativeMin[index] = currentMin;
		}
		return cumulativeMin;
	}
	
	public static double[] cumulativeSum(double[] arr) {
		double[] cumulativeSum = new double[arr.length];
		double currentSum = 0;
		for (int index = 0; index < arr.length; index++) {
			currentSum += arr[index];
			cumulativeSum[index] += currentSum;
		}
		return cumulativeSum;
	}
	
	public static int[] cumulativeSum(int[] arr) {
		int[] cumulativeSum = new int[arr.length];
		int currentSum = 0;
		for (int index = 0; index < arr.length; index++) {
			currentSum += arr[index];
			cumulativeSum[index] += currentSum;
		}
		return cumulativeSum;
	}
	
	public static double[] cumulativeProduct(double[] arr) {
		double[] cumulativeProduct = new double[arr.length];
		double currentProduct = 1;
		for (int index = 0; index < arr.length; index++) {
			currentProduct *= arr[index];
			cumulativeProduct[index] = currentProduct;
		}
		return cumulativeProduct;
	}
	
	public static int[] cumulativeProduct(int[] arr) {
		int[] cumulativeProduct = new int[arr.length];
		int currentProduct = 1;
		for (int index = 0; index < arr.length; index++) {
			currentProduct *= arr[index];
			cumulativeProduct[index] = currentProduct;
		}
		return cumulativeProduct;
	}
	
	public static double[] percentageChange(double[] arr) {
		double[] percentageChange = new double[arr.length];
		percentageChange[0] = 0;
		for (int index = 1; index < arr.length; index++) {
			percentageChange[index] = (arr[index] - arr[index-1])/arr[index-1];
		}
		return percentageChange;
	}
	
	public static double[] percentageChange(int[] arr) {
		double[] percentageChange = new double[arr.length];
		percentageChange[0] = 0;
		for (int index = 1; index < arr.length; index++) {
			percentageChange[index] = ((double)arr[index] - (double)arr[index-1])/(double)arr[index-1];
		}
		return percentageChange;
	}
	
	public static int numUnique(int[] arr) {
		return (int) IntStream.of(arr).distinct().count();
	}
	
	public static int numUnique(double[] arr) {
		return (int) DoubleStream.of(arr).distinct().count();
	}

	public static int[] elementsOfTrues(boolean[] getColumn) {
		ArrayList<Integer> indicesOfTrues = new ArrayList<Integer>();
		for (int index = 0; index < getColumn.length; index++) {
			if (getColumn[index]) indicesOfTrues.add(index);
		}
		
		return CommonArray.convertIntegerArrayListToArray(indicesOfTrues);
	}
	
	public static ArrayList<String> doesntContain(ArrayList<String> arr1, ArrayList<String> arr2) {
		// Strings present in arr1, but not arr2
		ArrayList<String> finalArr = new ArrayList<String>();
		for (String element: arr1) {
			if (!arr2.contains(element)) {
				finalArr.add(element);
			}
		}
		
		return finalArr;
	}

	public static boolean dataItemInList(ArrayList<DataItem> arr, DataItem item) {
		for (DataItem element: arr) {
			if (element.equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	public static DataItem[] getUniqueValues(DataItem[] arr) {
		ArrayList<DataItem> uniqueValues = new ArrayList<DataItem>();
		for (DataItem value: arr) {
			if (!dataItemInList(uniqueValues, value)) {
				uniqueValues.add(value);
			}
		}
		return uniqueValues.toArray(new DataItem[0]);
	}
	
	public static String[] getUniqueValues(String[] arr) {
		ArrayList<String> uniqueValues = new ArrayList<String>();
		for (String value: arr) {
			if (!uniqueValues.contains(value)) {
				uniqueValues.add(value);
			}
		}
		return uniqueValues.toArray(new String[0]);
	}

	public static int[] indicesOf(String[] column, String value) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int index = 0; index < column.length; index++) {
			if (column[index].equals(value)) {
				indices.add(index);
			}
		}
		return indices.stream().mapToInt(Integer::intValue).toArray();
	}
	
	public static int[] getUniqueInts(int lowInclusive, int highExclusive) {
		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = lowInclusive; i < highExclusive; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return CommonArray.convertIntegerArrayListToArray(list);
	}
	
	public static double[] getUniqueDoubles(int numvalues, double lowInclusive, double highExlusive) {
		return new Random().doubles()
                .distinct()
                .map(d -> lowInclusive + d * highExlusive)
                .limit(numvalues)
                .toArray();
		
	}

	public static String[] getUniqueStrings(int numValues, int stringLength) {
		ArrayList<String> uniqueStrings = new ArrayList<String>();
		while (uniqueStrings.size() < numValues) {
			String randomString = CommonArray.randomString(stringLength);
			if (!uniqueStrings.contains(randomString)) {
				uniqueStrings.add(randomString);
			}
		}
		return uniqueStrings.toArray(new String[0]);
	}
	
	public static LocalDate[] getUniqueLocalDates(int numValues, LocalDate minLocalDate, LocalDate maxLocalDate) {
		ArrayList<LocalDate> uniqueLocalDates = new ArrayList<LocalDate>();
		while (uniqueLocalDates.size() < numValues) {
			long minDay = minLocalDate.toEpochDay();
		    long maxDay = maxLocalDate.toEpochDay();
		    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
			LocalDate randomLocalDate = LocalDate.ofEpochDay(randomDay);
			if (!uniqueLocalDates.contains(randomLocalDate)) {
				uniqueLocalDates.add(randomLocalDate);
			}
		}
		return uniqueLocalDates.toArray(new LocalDate[0]);
	}
	
	public static LocalDateTime[] getUniqueLocalDateTimes(int numValues, LocalDateTime minLocalDateTime, LocalDateTime maxLocalDateTime) {
		ArrayList<LocalDateTime> uniqueLocalDateTimes = new ArrayList<LocalDateTime>();
		while (uniqueLocalDateTimes.size() < numValues) {
			long minDateTime = minLocalDateTime.toEpochSecond(ZoneOffset.UTC);
		    long maxDateTime =  maxLocalDateTime.toEpochSecond(ZoneOffset.UTC);
		    long randomDayTime = ThreadLocalRandom.current().nextLong(minDateTime, maxDateTime);
			LocalDateTime randomDateTime = LocalDateTime.ofEpochSecond(randomDayTime, 0, ZoneOffset.UTC);
			
			if (!uniqueLocalDateTimes.contains(randomDateTime)) {
				uniqueLocalDateTimes.add(randomDateTime);
			}
		}
		return uniqueLocalDateTimes.toArray(new LocalDateTime[0]);
	}
	
	public static LocalTime[] getUniqueLocalTimes(int numValues, LocalTime minLocalTime, LocalTime maxLocalTime) {
		ArrayList<LocalTime> uniqueLocalTime = new ArrayList<LocalTime>();
		while (uniqueLocalTime.size() < numValues) {
			long minTime = minLocalTime.toSecondOfDay();
		    long maxTime = maxLocalTime.toSecondOfDay();
		    long randomTimeLong = ThreadLocalRandom.current().nextLong(minTime, maxTime);
		    LocalTime randomTime = LocalTime.ofSecondOfDay(randomTimeLong);
			
			if (!uniqueLocalTime.contains(randomTime)) {
				uniqueLocalTime.add(randomTime);
			}
		}
		return uniqueLocalTime.toArray(new LocalTime[0]);
	}
	
	public static Period[] getUniquePeriods(int numValues, Period minPeriod, Period maxPeriod) {
		ArrayList<Period> uniquePeriods = new ArrayList<Period>();
		while (uniquePeriods.size() < numValues) {
			
			long minDays = minPeriod.toTotalMonths();
			long maxDays = maxPeriod.toTotalMonths();
			
			long randomMonths = ThreadLocalRandom.current().nextLong(minDays, maxDays);
			Period numMonths = Period.ofMonths((int)randomMonths);
			
			if (!uniquePeriods.contains(numMonths)) {
				uniquePeriods.add(numMonths);
			}
		}
		return uniquePeriods.toArray(new Period[0]);
	}
	
	public static Duration[] getUniqueDurations(int numValues, Duration minDuration, Duration maxDuration) {
		ArrayList<Duration> uniqueDurations = new ArrayList<Duration>();
		while (uniqueDurations.size() < numValues) {
			long minSecs = minDuration.getSeconds();
			long maxSecs = maxDuration.getSeconds();
			long randomSecs = ThreadLocalRandom.current().nextLong(minSecs, maxSecs);
			Duration secs = Duration.ofSeconds(randomSecs);
			
			if (!uniqueDurations.contains(secs)) {
				uniqueDurations.add(secs);
			}
		}
		return uniqueDurations.toArray(new Duration[0]);
	}

	public static Double[] primitiveDoubletoObjectDouble(double[] columnAsDoubleArray) {
		Double[] arr = new Double[columnAsDoubleArray.length];
		for (int i = 0; i < columnAsDoubleArray.length; i++) {
			arr[i] = columnAsDoubleArray[i];
		}
		return arr;
	}

	public static LocalDate maxLocalDate(DataItem[] values) {
		LocalDate max = LocalDate.MIN;
		for (DataItem value: values) {
			if (value.after(max)) {
				max = value.getDateValue();
			}
		}
		return max;
	}
	public static LocalDate minLocalDate(DataItem[] values) {
		LocalDate min = LocalDate.MAX;
		for (DataItem value: values) {
			if (value.before(min)) {
				min = value.getDateValue();
			}
		}
		return min;
	}

	public static List<Float> convertFloatArrayToFloatList(float[] columnAsFloatArray) {
		List<Float> list = new ArrayList<Float>();
		for (float val: columnAsFloatArray) {
			list.add(val);
		}
		return list;
	}
	
	public static List<Boolean> convertBooleanArrayToBooleanList(boolean[] columnAsFloatArray) {
		List<Boolean> list = new ArrayList<Boolean>();
		for (boolean val: columnAsFloatArray) {
			list.add(val);
		}
		return list;
	}

	public static <T> List<List<T>> convert2DArrayTo2DArrayList(T[][] arrays) {
		List<List<T>> lists = new ArrayList<List<T>>();
		for (T[] array: arrays) {
			lists.add(Arrays.asList(array));
		}
		return lists;
	}
	
	public static List<List<Integer>> convert2DArrayTo2DArrayList(int[][] arrays) {
		List<List<Integer>> lists = new ArrayList<List<Integer>>();
		for (int[] array: arrays) {
			lists.add(Arrays.stream(array).boxed().collect(Collectors.toList()));
		}
		return lists;
	}
	
	public static List<List<Double>> convert2DArrayTo2DArrayList(double[][] arrays) {
		List<List<Double>> lists = new ArrayList<List<Double>>();
		for (double[] array: arrays) {
			lists.add(Arrays.stream(array).boxed().collect(Collectors.toList()));
		}
		return lists;
	}
	
	public static List<List<Float>> convert2DArrayTo2DArrayList(float[][] arrays) {
		List<List<Float>> lists = new ArrayList<List<Float>>();
		for (int i = 0; i < arrays.length; i++) {
			lists.add(new ArrayList<Float>());
			for (int j = 0; j < arrays[i].length; j++) {
				lists.get(i).add(arrays[i][j]);
			}	
		}
		return lists;
	}

	public static List<List<Boolean>> convert2DArrayTo2DArrayList(boolean[][] arrays) {
		List<List<Boolean>> lists = new ArrayList<List<Boolean>>();
		for (int i = 0; i < arrays.length; i++) {
			lists.add(new ArrayList<Boolean>());
			for (int j = 0; j < arrays[i].length; j++) {
				lists.get(i).add(arrays[i][j]);
			}	
		}
		return lists;
	}
	
	public static int[] getIndicesOfListThatMatchRegex(ArrayList<String> list, Pattern regex) {
		List<Integer> indexMatches = new ArrayList<Integer>();
		for (int columnIndex = 0; columnIndex < list.size(); columnIndex++) {
			if (regex.matcher(list.get(columnIndex)).find()) {
				indexMatches.add(columnIndex);
			}
		}
		return indexMatches.stream().mapToInt(i -> i).toArray();
	}
}
