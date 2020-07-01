package thesis.Common;

public class CommonDouble {

	public static boolean approxEqual(double num1, double num2) {
		double delta = 0.001;
		return Math.abs(num1 - num2) < delta;
	}
	
}
