package thesis.Common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import thesis.DataFrame.DataItem;

public class CommonMath {
	
	public static int indexOfMedian(int leftIndex, int rightIndex){ 
		int gapSize = rightIndex - leftIndex + 1; 
		int halfGapSize = (gapSize + 1) / 2 - 1; 
		return leftIndex + halfGapSize; 
	}
	
	public static double average(float[] dataPoints) {
		double sum = 0;
		for (int i = 0; i < dataPoints.length; i++) {
			sum += dataPoints[i];
		}
		
		return sum/dataPoints.length;
		
	}
	
	public static double average(DataItem[] dataPoints) {
		double sum = 0;
		for (int i = 0; i < dataPoints.length; i++) {
			sum +=  dataPoints[i].getValueConvertedToDouble();
		}
		
		return sum/dataPoints.length;
		
	}

	public static double variance(DataItem[] dataPoints, double average) {	
		return covariance(dataPoints, dataPoints, average, average);
	}
	
	public static double variance(DataItem[] dataPoints) {
		return variance(dataPoints, average(dataPoints));
	}
	
	
	
	public static double covariance(DataItem[] dataPoints1, DataItem[] dataPoints2) {
		return covariance(dataPoints1, dataPoints2, average(dataPoints1), average(dataPoints2));
	}
	
	public static double covariance(DataItem[] dataPoints1, DataItem[] dataPoints2, double average1, double average2) {
		if (dataPoints1.length != dataPoints2.length) {
			System.out.println("Error: Data points lengths must be equal");
			return 0;
		}
		
		double sum = 0;
		for (int i = 0; i < dataPoints1.length; i++) {
			sum += dataPoints1[i].getValueConvertedToDouble() * dataPoints2[i].getValueConvertedToDouble();
		}
		
		double result = (sum/dataPoints1.length) - (average1 * average2);
		
		return result;
	}
	
	public static Double minimumValue(DataItem[] column) {
		Double minValue = (Double) column[0].getValueConvertedToDouble();

		for (int i = 0; i < column.length; i++){
			if ((Double) (column[i].getValueConvertedToDouble()) < minValue) {
				minValue = (Double) (column[i].getValueConvertedToDouble());
			}
		}

		
		return minValue;
	}
	public static Double maximumValue(DataItem[] column) {
		Double maxValue = (Double) column[0].getValueConvertedToDouble();

		for (int i = 0; i < column.length; i++){
			if ((Double) (column[i].getValueConvertedToDouble()) > maxValue) {
				maxValue = (Double) (column[i].getValueConvertedToDouble());
			}
		}

		return maxValue;
	}
	
	public static int map (double value, double origLow, double origHigh, double newLow, double newHigh) {
	    return (int)((value - origLow) / (origHigh - origLow) * (newHigh - newLow) + newLow);

	}
	
	public static <T> int elementNumInArray (T[] array, T value) {
		int elementNum = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				elementNum = i;
			} 
		}
		return elementNum;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}