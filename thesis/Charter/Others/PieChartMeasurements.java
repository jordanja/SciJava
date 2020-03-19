package thesis.Charter.Others;

import java.awt.Font;

import thesis.Charter.Axis.Axis;
import thesis.Charter.LegendPackage.Legend;
import thesis.Charter.StringDrawer.DrawString;

public class PieChartMeasurements {

	protected int imageLeftToPlotLeftWidth = 20;
	protected int plotWidth = 400;
	protected int plotRightToLegendLeftWidth = 20;
	protected int legendWidth;
	protected int legendRightToChartRightWidth = 20;
	
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
	
	public int imageLeftToLegendLeftWidth() {
		return this.imageLeftToPlotRightWidth() + this.plotRightToLegendLeftWidth;
	}
	
	public int imageLeftToLegendMidWidth() {
		return this.imageLeftToLegendLeftWidth() + this.legendWidth/2;
	}
	
	public int imageLeftToLegendRightWidth() {
		return this.imageLeftToLegendLeftWidth() + this.legendWidth;
	}
	
	public int imageWidth() {
		return this.imageLeftToLegendRightWidth() + this.legendRightToChartRightWidth;
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

	public int getPlotRightToLegendLeftWidth() {
		return plotRightToLegendLeftWidth;
	}

	public void setPlotRightToLegendLeftWidth(int plotRightToLegendLeftWidth) {
		this.plotRightToLegendLeftWidth = plotRightToLegendLeftWidth;
	}

	public int getLegendWidth() {
		return legendWidth;
	}

	public void setLegendWidth(int legendWidth) {
		this.legendWidth = legendWidth;
	}

	public int getLegendRightToChartRightWidth() {
		return legendRightToChartRightWidth;
	}

	public void setLegendRightToChartRightWidth(int legendRightToChartRightWidth) {
		this.legendRightToChartRightWidth = legendRightToChartRightWidth;
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
	
	
	public void calculateChartImageMetrics(Legend legend, String title, Font titleFont) {
		if (title != null) {
			this.titleHeight = DrawString.getStringHeight(title, titleFont);
		} else {
			this.titleHeight = 0;
		}
		
		if (legend.getIncludeLegend()) {
			this.legendWidth = legend.getLegendWidth();
		} else {
			this.legendWidth = 0;
		}
	}
}
