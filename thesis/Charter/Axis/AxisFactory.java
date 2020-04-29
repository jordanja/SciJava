package thesis.Charter.Axis;

public class AxisFactory {

	public static BaseAxis getAxis(String axisType) {
		if (axisType.equals("Scatter")) {
			return new NumericAxis();
		} else {
			return null;
		}
	}
}
