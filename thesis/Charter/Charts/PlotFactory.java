package thesis.Charter.Charts;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Plots.Plot;
import thesis.Charter.Plots.ScatterPlot;

public class PlotFactory {
	public static Plot getPlot(String axisType) {
		if (axisType.equals("Scatter")) {
			return new ScatterPlot();
		} else {
			return null;
		}
	}
}
