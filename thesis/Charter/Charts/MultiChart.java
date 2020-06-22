package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import thesis.Charter.ChartMeasurements.MultiChartMeasurements;
import thesis.Charter.Image.WholeImage;

public class MultiChart {

	private List<List<Chart>> charts;
	protected Graphics2D g;
	
	protected String title;
	protected Font titleFont = new Font("Dialog", Font.PLAIN, 12);
	protected Color titleColor = Color.BLACK;
	
	private MultiChartMeasurements cm;
	
	public MultiChart(int numColumns, int numRows) {
		this.charts = new ArrayList<List<Chart>>();
		for (int rowCount = 0; rowCount < numRows; rowCount++) {
			this.charts.add(new ArrayList<Chart>());
			List<Chart> row = this.charts.get(this.charts.size() - 1);
			for (int columnCount = 0; columnCount< numColumns; columnCount++) {
				row.add(null);
			}
		}
		
		this.cm = new MultiChartMeasurements();
	}

	public void create() {
		// 'create' each chart
		for (List<Chart> chartRow: charts) {
			for (Chart chart: chartRow) {
				chart.Create();
			}
		}
		// Calculate chart metrics (size of each chart etc)
		Chart[][] chartArray = new Chart[this.charts.size()][];
		for (int i = 0; i < this.charts.size(); i++) {
		    List<Chart> row = this.charts.get(i);
		    chartArray[i] = row.toArray(new Chart[row.size()]);
		}
		cm.calculateImageMetrics(title, titleFont, chartArray);
		
		// Instantiate chart
		
		// Initialize graphics object
		
		// Draw image background
		
		// Draw title
		
		// Insert each chart image onto the multi-chart
		
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
 