package thesis.Charter.ChartMeasurements;

import java.awt.Font;

import thesis.Charter.Charts.Chart;
import thesis.Charter.Image.WholeImage;
import thesis.Charter.StringDrawer.DrawString;

public class MultiChartMeasurements {
	private int imageLeftToLeftmostChartLeftWidth = 10;
	private int[][] chartWidths;
	private int chartSpacingWidth = 10;
	private int rightmostChartRightToImageRightWidth = 10;
	
	
	private int imageBottomToBottommostChartBottomHeight = 10;
	private int[][] chartHeights;
	private int chartSpacingHeight = 10;
	private int topmostChartTopToTitleBottomHeight = 10;
	private int titleHeight;
	private int titleTopToImageTop = 10;
	
	
	public int imageLeftToLeftmostChartLeftWidth() {
		return this.imageLeftToLeftmostChartLeftWidth;
	}
	
	public int imageLeftToChartLeftWidth(int columnIndex, int rowIndex) {
		int xPixel = this.imageLeftToLeftmostChartLeftWidth();
		for (int chartCount = 0; chartCount < columnIndex; chartCount++) {
			xPixel += this.widestChartInColumnWidth(chartCount);
			xPixel += chartSpacingWidth;
		}
		xPixel += this.widestChartInColumnWidth(columnIndex)/2;
		xPixel -= this.getChartWidth(columnIndex, rowIndex)/2;
		
		return xPixel;
	}
	
	public int imageLeftToChartRightWidth(int columnIndex, int rowIndex) {
		return this.imageLeftToChartLeftWidth(columnIndex, rowIndex) + this.getChartWidth(columnIndex, rowIndex);
	}
	
	private int widestChartInColumnWidth(int chartCount) {
		int widest = Integer.MIN_VALUE;
		for (int rowCount = 0; rowCount < chartWidths.length; rowCount++) {
			widest = Integer.max(widest, this.getChartWidth(chartCount, rowCount));
		}
		return widest;
	}
	
	public int imageLeftToRightmostChartRightWidth() {
		int xPixel = this.imageLeftToLeftmostChartLeftWidth;
		for (int chartCount = 0; chartCount < this.getNumHorizontalCharts(); chartCount++) {
			xPixel += this.widestChartInColumnWidth(chartCount);
		}
		xPixel += this.chartSpacingWidth *(this.getNumHorizontalCharts() - 1);
		return xPixel;
	}
	
	public int imageWidth() {
		return this.imageLeftToRightmostChartRightWidth() + this.rightmostChartRightToImageRightWidth;
	}
	
	public int imageBottomToBottomMostChartBottomHeight() {
		return this.imageBottomToBottommostChartBottomHeight;
	}
	
	public int imageBottomToChartBottomHeight(int columnIndex, int rowIndex) {
		int yPixel = this.imageBottomToBottomMostChartBottomHeight();
		for (int rowCount = 0; rowCount < rowIndex; rowCount++) {
			yPixel += this.tallestChartInRowHeight(rowCount);
			yPixel += this.chartSpacingHeight;
		}
		yPixel += this.tallestChartInRowHeight(rowIndex)/2;
		yPixel -= this.getChartHeight(columnIndex, rowIndex)/2;
		
		return yPixel;
	}
	
	public int imageBottomToChartTopHeight(int columnIndex, int rowIndex) {
		return this.imageBottomToChartBottomHeight(columnIndex, rowIndex) + this.getChartHeight(columnIndex, rowIndex);
	}
	
	private int tallestChartInRowHeight(int rowIndex) {
		int tallest = Integer.MIN_VALUE;
		for (int columnCount = 0; columnCount < this.chartHeights[rowIndex].length; columnCount++) {
			tallest = Integer.max(tallest, this.getChartHeight(columnCount, rowIndex));
		}
		return tallest;
	}
	
	public int imageBottomToTopmostChartTopHeight() {
		int yPixel = this.imageBottomToBottommostChartBottomHeight;
		for (int chartCount = 0; chartCount < this.getNumVerticleCharts(); chartCount++) {
			yPixel += this.tallestChartInRowHeight(chartCount);
		}
		yPixel += this.chartSpacingHeight * (this.getNumVerticleCharts() - 1);
		return yPixel;
	}
	
	public int imageBottomToTitleBottomHeight() {
		return this.imageBottomToTopmostChartTopHeight() + this.topmostChartTopToTitleBottomHeight;
	}
	
	public int imageBottomToTitleMidHeight() {
		return this.imageBottomToTitleBottomHeight() + this.titleHeight/2;
	}
	
	public int imageBottomToTitleTopHeight() {
		return this.imageBottomToTitleBottomHeight() + this.titleHeight;
	}

	public int imageHeight() {
		return this.imageBottomToTitleTopHeight() + this.titleTopToImageTop;
	}
	
	public int getNumHorizontalCharts() {
		return this.chartWidths[0].length;
	}
	public int getNumVerticleCharts() {
		return this.chartWidths.length;
	}
	public int getImageLeftToLeftmostChartLeftWidth() {
		return imageLeftToLeftmostChartLeftWidth;
	}
	public void setImageLeftToLeftmostChartLeftWidth(int imageLeftToLeftmostChartLeftWidth) {
		this.imageLeftToLeftmostChartLeftWidth = imageLeftToLeftmostChartLeftWidth;
	}
	public int[][] getChartWidths() {
		return this.chartWidths;
	}
	public int getChartWidth(int columnNum, int rowNum) {
		return this.chartWidths[rowNum][columnNum];
	}
	public int getChartSpacingWidth() {
		return chartSpacingWidth;
	}
	public void setChartSpacingWidth(int chartSpacingWidth) {
		this.chartSpacingWidth = chartSpacingWidth;
	}
	public int getRightmostChartRightToImageRightWidth() {
		return rightmostChartRightToImageRightWidth;
	}
	public void setRightmostChartRightToImageRightWidth(int rightmostChartRightToImageRightWidth) {
		this.rightmostChartRightToImageRightWidth = rightmostChartRightToImageRightWidth;
	}
	public int getImageBottomToBottommostChartBottomHeight() {
		return imageBottomToBottommostChartBottomHeight;
	}
	public void setImageBottomToBottommostChartBottomHeight(int imageBottomToBottommostChartBottomHeight) {
		this.imageBottomToBottommostChartBottomHeight = imageBottomToBottommostChartBottomHeight;
	}
	public int[][] getChartHeights() {
		return this.chartHeights;
	}
	public int getChartHeight(int columnNum, int rowNum) {
		return this.chartHeights[rowNum][columnNum];
	}
	public int getChartSpacingHeight() {
		return chartSpacingHeight;
	}
	public void setChartSpacingHeight(int chartSpacingHeight) {
		this.chartSpacingHeight = chartSpacingHeight;
	}
	public int getTopmostChartTopToTitleBottomHeight() {
		return topmostChartTopToTitleBottomHeight;
	}
	public void setTopmostChartTopToTitleBottomHeight(int topmostChartTopToTitleBottomHeight) {
		this.topmostChartTopToTitleBottomHeight = topmostChartTopToTitleBottomHeight;
	}
	public int getTitleTopToImageTop() {
		return titleTopToImageTop;
	}
	public void setTitleTopToImageTop(int titleTopToImageTop) {
		this.titleTopToImageTop = titleTopToImageTop;
	}
	public int getTitleHeight() {
		return titleHeight;
	}
	
	
	public void calculateImageMetrics(String title, Font titleFont, Chart[][] charts) {
		this.chartWidths = new int[charts.length][charts[0].length];
		this.chartHeights = new int[charts.length][charts[0].length];
		for (int rowCount = 0; rowCount < charts.length; rowCount++) {
			for (int columnCount = 0; columnCount < charts[0].length; columnCount++) {
				if (charts[rowCount][columnCount] != null) {
					this.chartWidths[rowCount][columnCount] = charts[rowCount][columnCount].getImageWidth();
					this.chartHeights[rowCount][columnCount] = charts[rowCount][columnCount].getImageHeight();
				} else {
					this.chartWidths[rowCount][columnCount] = 0;
					this.chartHeights[rowCount][columnCount] = 0;
				}
			}	
		}
		
		
		if (title != null) {
			this.titleHeight = DrawString.getStringHeight(title, titleFont);
		} else {
			this.titleHeight = 0;
		}

	}
	
	
}
