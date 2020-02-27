package thesis.Charter.PlotFolder;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;

public class PlotFactory {
	public static Plot getPlot(String axisType) {
		if (axisType.equals("Scatter")) {
			return new ScatterPlot();
		} else {
			return null;
		}
	}
}
