package thesis.Charter.ChartMeasurements;

import java.awt.Font;

import thesis.Charter.Legend.Legend;
import thesis.Charter.StringDrawer.DrawString;

public class OnlyPlotChartMeasurements extends ChartMeasurements{

	protected int imageLeftToPlotLeftWidth = 20;
	protected int plotWidth = 400;
	protected int plotRightToChartRightWidth = 20;
	
	protected int imageBottomToPlotBottomHeight = 20;
	protected int plotHeight = 400;
	protected int plotTopToTitleBottomHeight = 20;
	protected int titleHeight;
	protected int titleTopToImageTopHeight = 20;
	
	public int imageLeftToPlotLeftWidth() {
		return this.imageLeftToPlotLeftWidth;
	}
	
	public int imageLeftToPlotMidWidth() {
		return this.imageLeftToPlotLeftWidth() + this.plotWidth/2;
	}
	
	public int imageLeftToPlotRightWidth() {
		return this.imageLeftToPlotLeftWidth() + this.plotWidth;
	}
	
	public int imageWidth() {
		return this.imageLeftToPlotRightWidth() + this.plotRightToChartRightWidth;
	}
	
	
	public int imageBottomToPlotBottomHeight() {
		return this.imageBottomToPlotBottomHeight;
	}
	
	public int imageBottomToPlotMidHeight() {
		return this.imageBottomToPlotBottomHeight() + this.plotHeight/2;
	}
	
	public int imageBottomToPlotTopHeight() {
		return this.imageBottomToPlotBottomHeight() + this.plotHeight;
	}
	
	public int imageBottomToTitleBottomHeight() {
		return this.imageBottomToPlotTopHeight() + this.plotTopToTitleBottomHeight;
	}
	
	public int imageBottomToTitleMidHeight() {
		return this.imageBottomToTitleBottomHeight() + this.titleHeight/2;
	}
	
	public int imageBottomToTitleTopHeight() {
		return this.imageBottomToTitleBottomHeight() + this.titleHeight;
	}
	
	public int imageHeight() {
		return this.imageBottomToTitleTopHeight() + this.titleTopToImageTopHeight;
	}

	public int getImageLeftToPlotLeftWidth() {
		return imageLeftToPlotLeftWidth;
	}

	public void setImageLeftToPlotLeftWidth(int imageLeftToPlotLeftWidth) {
		this.imageLeftToPlotLeftWidth = imageLeftToPlotLeftWidth;
	}

	public int getPlotWidth() {
		return plotWidth;
	}

	public void setPlotWidth(int plotWidth) {
		this.plotWidth = plotWidth;
	}

	public int getPlotRightToChartRightWidth() {
		return plotRightToChartRightWidth;
	}

	public void setPlotRightToChartRightWidth(int plotRightToChartRightWidth) {
		this.plotRightToChartRightWidth = plotRightToChartRightWidth;
	}

	public int getImageBottomToPlotBottomHeight() {
		return imageBottomToPlotBottomHeight;
	}

	public void setImageBottomToPlotBottomHeight(int imageBottomToPlotBottomHeight) {
		this.imageBottomToPlotBottomHeight = imageBottomToPlotBottomHeight;
	}

	public int getPlotHeight() {
		return plotHeight;
	}

	public void setPlotHeight(int plotHeight) {
		this.plotHeight = plotHeight;
	}

	public int getPlotTopToTitleBottomHeight() {
		return plotTopToTitleBottomHeight;
	}

	public void setPlotTopToTitleBottomHeight(int plotTopToTitleBottomHeight) {
		this.plotTopToTitleBottomHeight = plotTopToTitleBottomHeight;
	}

	public int getTitleHeight() {
		return titleHeight;
	}

	public void setTitleHeight(int titleHeight) {
		this.titleHeight = titleHeight;
	}

	public int getTitleTopToImageTopHeight() {
		return titleTopToImageTopHeight;
	}

	public void setTitleTopToImageTopHeight(int titleTopToImageTopHeight) {
		this.titleTopToImageTopHeight = titleTopToImageTopHeight;
	}

	public int imageLeftToLegendLeftWidth() {
		return 0;
	}
	
	public void calculateChartImageMetrics(String title, Font titleFont) {
		if (title != null) {
			this.titleHeight = DrawString.getStringHeight(title, titleFont);
		} else {
			this.titleHeight = 0;
		}

	}
	
}
