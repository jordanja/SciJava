package thesis.Helpers;

public class CommonMath {
	
	public static int indexOfMedian(int leftIndex, int rightIndex){ 
		int gapSize = rightIndex - leftIndex + 1; 
		int halfGapSize = (gapSize + 1) / 2 - 1; 
		return leftIndex + halfGapSize; 
	}

}
