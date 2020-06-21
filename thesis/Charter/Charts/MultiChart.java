package thesis.Charter.Charts;

import java.util.ArrayList;
import java.util.List;

import thesis.Charter.Image.WholeImage;

public class MultiChart {

	private List<List<Chart>> charts;
	
	public MultiChart(int numColumns, int numRows) {
		this.charts = new ArrayList<List<Chart>>();
		for (int rowCount = 0; rowCount < numRows; rowCount++) {
			this.charts.add(new ArrayList<Chart>());
			List<Chart> row = this.charts.get(this.charts.size() - 1);
			for (int columnCount = 0; columnCount< numColumns; columnCount++) {
				row.add(new GaugeChart(0.5));
			}
		}
	}

	public int getNumRows() {
		return this.charts.size();
	}
	
	public int getNumColumns() {
		return this.charts.get(0).size();
	}
	
	public void setCharts(ArrayList<List<Chart>> charts) {
		this.charts = charts;
	}
	
	public void setChart(int columnIndex, int rowIndex, Chart chart) {
		this.charts.get(rowIndex).set(columnIndex, chart);
	}
	
}
 