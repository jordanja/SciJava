package thesis.Charter.Axis;

public class AxisFactory {

	public static Axis getAxis(String axisType) {
		if (axisType.equals("Scatter")) {
			return new NumericAxis();
		} else {
			return null;
		}
	}
}
