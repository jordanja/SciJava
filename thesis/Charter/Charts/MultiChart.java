package thesis.Charter.Charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.ChartMeasurements.MultiChartMeasurements;
import thesis.Charter.Image.WholeImage;
import thesis.Charter.StringDrawer.DrawString;

public class MultiChart {
	
	protected BufferedImage multiChartImage;
	private List<List<Chart>> charts;
	protected Graphics2D g;
	
	protected String title;
	protected Font titleFont = new Font("Dialog", Font.PLAIN, 12);
	protected Color titleColor = Color.BLACK;
	
	private Color defaultImageBackgroundColor = Color.WHITE;
	
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
				chart.create();
			}
		}
		// Calculate chart metrics (size of each chart etc)
		Chart[][] chartArray = new Chart[this.charts.size()][];
		for (int i = 0; i < this.charts.size(); i++) {
		    List<Chart> row = this.charts.get(i);
		    chartArray[i] = row.toArray(new Chart[row.size()]);
		}
		this.cm.calculateImageMetrics(title, titleFont, chartArray);
		
		// Instantiate chart
		this.instantiateChart();
		
		// Initialize graphics object
		this.g = initializaGraphicsObject();
		
		// Draw image background
		this.drawBackground();
		
		// Draw title
		this.drawTitle();
		
		// Insert each chart image onto the multi-chart
		for (int rowCount = 0; rowCount < this.charts.size(); rowCount++) {
			for (int columnCount = 0; columnCount < this.charts.get(rowCount).size(); columnCount++) {
				BufferedImage img = this.charts.get(rowCount).get(columnCount).getImage();
				
				// Not sure why I need this
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -img.getHeight(null));
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				img = op.filter(img, null);
				
				g.drawImage(img, this.cm.imageLeftToChartLeftWidth(columnCount, rowCount), this.cm.imageBottomToChartBottomHeight(columnCount, rowCount), null);
				g.drawRect(this.cm.imageLeftToChartLeftWidth(columnCount, rowCount), this.cm.imageBottomToChartBottomHeight(columnCount, rowCount), this.cm.getChartWidth(columnCount, rowCount), this.cm.getChartHeight(columnCount, rowCount));
			}
		}
	}
	
	private void drawBackground() {
		this.g.setBackground(this.defaultImageBackgroundColor);
		this.g.clearRect(0, 0, this.cm.imageWidth(), this.cm.imageHeight());		
	}

	private Graphics2D initializaGraphicsObject() {
		g = this.multiChartImage.createGraphics();	
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		changeCoordFrame(g, cm); 
		
		return g;
	}
	
	private void drawTitle() {
		if (this.title != null) {	
			
			DrawString.setTextStyle(this.titleColor, this.titleFont, 0);
			DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.BottomAlign);
			DrawString.write(this.g, this.title, this.cm.imageWidth()/2, this.cm.imageBottomToTitleBottomHeight());
		}
	}
	
	private void changeCoordFrame(Graphics2D g, MultiChartMeasurements cm) {
		g.translate(0.0, cm.imageHeight());
		g.scale(1.0, -1.0);
	}
	
	private void instantiateChart() {
		this.multiChartImage = new BufferedImage(cm.imageWidth(), cm.imageHeight(), BufferedImage.TYPE_INT_RGB);		
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

	public void WriteFile(String fileLoc) {
		try {
		    ImageIO.write(this.multiChartImage, "png", new File(fileLoc));
		} catch (IOException e) {
		   
		}		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	public Color getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}
	
	
	
}
 