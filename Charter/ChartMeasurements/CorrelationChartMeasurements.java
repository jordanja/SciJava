package thesis.Charter.ChartMeasurements;

public class CorrelationChartMeasurements extends ChartMeasurements{
	
	protected int imageBottomToBottomAxisLabelHeight = 5;
	protected int bottomAxisLabelHeight;
	protected int bottomAxisLabelToBottomAxisHeight = 5;
	protected int bottomAxisHeight;
	protected int bottomAxisToBottomTicksHeight = 5;
	protected int bottomTicksHeight = 5;
	protected int plotHeight = 400;
	protected int topTicksHeight = 5;
	protected int topTicksToTopAxisHeight = 5;
	protected int topAxisHeight;
	protected int topAxisToTopAxisLabelHeight = 5;
	protected int topAxisLabelHeight;
	protected int topAxisLabelToTitleHeight = 5;
	protected int titleHeight;
	protected int titleToImageTopHeight = 5;
	
	protected int imageLeftToLeftAxisLabelWidth = 5;
	protected int leftAxisLabelWidth;
	protected int leftAxisLabelToLeftAxisWidth = 5;
	protected int leftAxisWidth;
	protected int leftAxisToLeftTicksWidth = 10;
	protected int leftTicksWidth = 5;
	protected int plotWidth = 400;
	protected int rightTicksWidth = 5;
	protected int rightTicksToRightAxisWidth = 5;
	protected int rightAxisWidth;
	protected int rightAxisToRightAxisLabelWidth = 5;
	protected int rightAxisLabelWidth;
	protected int rightAxisLabelToColorBarWidth = 5;
	protected int colorBarWith = 15;
	protected int colorBarTicksWidth = 10;
	protected int colorBarTicksToColorBarValuesWidth = 5;
	protected int colorBarValuesWidth;
	protected int colorBarValuesToPlotRightWidth = 5;
	
	
	public int imageLeftToAxisLabelLeftWidth() {
		return this.imageLeftToLeftAxisLabelWidth;
	}

	public int imageLeftToLeftAxisLabelMidWidth() {
		return imageLeftToAxisLabelLeftWidth() + this.leftAxisLabelWidth / 2;
	}

	public int imageLeftToLeftAxisLabelRightWidth() {
		return imageLeftToAxisLabelLeftWidth() + this.leftAxisLabelWidth;
	}

	public int imageLeftToLeftAxisLeftWidth() {
		return imageLeftToLeftAxisLabelRightWidth() + leftAxisLabelToLeftAxisWidth;
	}

	public int imageLeftToLeftAxisMidWidth() {
		return imageLeftToLeftAxisLeftWidth() + this.leftAxisWidth / 2;
	}

	public int imageLeftToLeftAxisRightWidth() {
		return imageLeftToLeftAxisLeftWidth() + this.leftAxisWidth;
	}

	public int imageLeftToLeftTicksEndWidth() {
		return imageLeftToLeftAxisRightWidth() + this.leftAxisToLeftTicksWidth;
	}

	public int imageLeftToPlotLeftWidth() {
		return imageLeftToLeftTicksEndWidth() + this.leftTicksWidth;
	}

	public int imageLeftToPlotMidWidth() {
		return imageLeftToPlotLeftWidth() + this.plotWidth / 2;
	}

	public int imageLeftToPlotRightWidth() {
		return imageLeftToPlotLeftWidth() + this.plotWidth;
	}

	public int imageLeftToRightTicksEndWidth() {
		return imageLeftToPlotRightWidth() + this.rightTicksWidth;
	}

	public int imageLeftToRightAxisLeftWidth() {
		return imageLeftToRightTicksEndWidth() + rightTicksToRightAxisWidth;
	}

	public int imageLeftToRightAxisMidWidth() {
		return imageLeftToRightAxisLeftWidth() + this.rightAxisWidth / 2;
	}

	public int imageLeftToRightAxisRightWidth() {
		return imageLeftToRightAxisLeftWidth() + this.rightAxisWidth;
	}

	public int imageLeftToRightAxisLabelLeftWidth() {
		return imageLeftToRightAxisRightWidth() + rightAxisToRightAxisLabelWidth;
	}

	public int imageLeftToRightAxisLabelMidWidth() {
		return imageLeftToRightAxisLabelLeftWidth() + this.rightAxisLabelWidth / 2;
	}

	public int imageLeftToRightAxisLabelRightWidth() {
		return imageLeftToRightAxisLabelLeftWidth() + this.rightAxisLabelWidth;
	}
	
	public int imageLeftToColorBarLeftWidth() {
		return imageLeftToRightAxisLabelRightWidth() + this.rightAxisLabelToColorBarWidth;
	}
	
	public int imageLeftToColorBarRightWidth() {
		return imageLeftToColorBarLeftWidth() + this.colorBarWith;
	}
	
	public int imageLeftToColorBarTicksRightWidth() {
		return imageLeftToColorBarRightWidth() + this.colorBarTicksWidth;
	}
	
	public int imageLeftToColorBarValuesLeftWidth() {
		return imageLeftToColorBarTicksRightWidth() + this.colorBarTicksToColorBarValuesWidth;
	}
	
	public int imageLeftToRightmostColorBarValue() {
		return imageLeftToColorBarValuesLeftWidth() + this.colorBarValuesWidth;
	}
	
	public int imageWidth() {
		return imageLeftToRightmostColorBarValue() + this.colorBarValuesToPlotRightWidth;
	}
	
	// Height functions
	public int imageBottomToBottomAxisLabelBottomHeight() {
		return this.imageBottomToBottomAxisLabelHeight;
	}
	
	public int imageBottomToBottomAxisLabelMidHeight() {
		return imageBottomToBottomAxisLabelBottomHeight() + this.bottomAxisLabelHeight / 2;
	}

	public int imageBottomToBottomAxisLabelTopHeight() {
		return imageBottomToBottomAxisLabelBottomHeight() + this.bottomAxisLabelHeight;
	}

	public int imageBottomToBottomAxisBottomHeight() {
		return imageBottomToBottomAxisLabelTopHeight() + this.bottomAxisLabelToBottomAxisHeight;
	}

	public int imageBottomToBottomAxisMidHeight() {
		return imageBottomToBottomAxisBottomHeight() + this.bottomAxisHeight / 2;
	}

	public int imageBottomToBottomAxisTopHeight() {
		return imageBottomToBottomAxisBottomHeight() + this.bottomAxisHeight;
	}

	public int imageBottomToBottomTicksEndHeight() {
		return imageBottomToBottomAxisTopHeight() + this.bottomAxisToBottomTicksHeight;
	}

	public int imageBottomToPlotBottomHeight() {
		return imageBottomToBottomTicksEndHeight() + this.bottomTicksHeight;
	}

	public int imageBottomToPlotMidHeight() {
		return imageBottomToPlotBottomHeight() + this.plotHeight / 2;
	}

	public int imageBottomToPlotTopHeight() {
		return imageBottomToPlotBottomHeight() + this.plotHeight;
	}

	public int imageBottomToTopTicksEndHeight() {
		return imageBottomToPlotTopHeight() + this.topTicksHeight;
	}

	public int imageBottomToTopAxisBottomHeight() {
		return imageBottomToTopTicksEndHeight() + this.topTicksToTopAxisHeight;
	}

	public int imageBottomToTopAxisMidHeight() {
		return imageBottomToTopAxisBottomHeight() + this.topAxisHeight / 2;
	}

	public int imageBottomToTopAxisTopHeight() {
		return imageBottomToTopAxisBottomHeight() + this.topAxisHeight;
	}

	public int imageBottomToTopAxisLabelBottomHeight() {
		return imageBottomToTopAxisTopHeight() + topAxisToTopAxisLabelHeight;
	}

	public int imageBottomToTopAxisLabelMidHeight() {
		return imageBottomToTopAxisLabelBottomHeight() + this.topAxisLabelHeight / 2;
	}

	public int imageBottomToTopAxisLabelTopHeight() {
		return imageBottomToTopAxisLabelBottomHeight() + this.topAxisLabelHeight;
	}

	public int imageBottomToTitleBottomHeight() {
		return imageBottomToTopAxisLabelTopHeight() + this.topAxisLabelToTitleHeight;
	}

	public int imageBottomToTitleMidHeight() {
		return imageBottomToTitleBottomHeight() + this.titleHeight / 2;
	}

	public int imageBottomToTitleTopHeight() {
		return imageBottomToTitleBottomHeight() + this.titleHeight;
	}

	public int imageHeight() {
		return imageBottomToTitleTopHeight() + this.titleToImageTopHeight;
	}
	
	
	
	public int getImageBottomToBottomAxisLabelHeight() {
		return imageBottomToBottomAxisLabelHeight;
	}
	public void setImageBottomToBottomAxisLabelHeight(int imageBottomToBottomAxisLabelHeight) {
		this.imageBottomToBottomAxisLabelHeight = imageBottomToBottomAxisLabelHeight;
	}
	public int getBottomAxisLabelToBottomAxisHeight() {
		return bottomAxisLabelToBottomAxisHeight;
	}
	public void setBottomAxisLabelToBottomAxisHeight(int bottomAxisLabelToBottomAxisHeight) {
		this.bottomAxisLabelToBottomAxisHeight = bottomAxisLabelToBottomAxisHeight;
	}
	public int getBottomAxisToBottomTicksHeight() {
		return bottomAxisToBottomTicksHeight;
	}
	public void setBottomAxisToBottomTicksHeight(int bottomAxisToBottomTicksHeight) {
		this.bottomAxisToBottomTicksHeight = bottomAxisToBottomTicksHeight;
	}
	public int getBottomTicksHeight() {
		return bottomTicksHeight;
	}
	public void setBottomTicksHeight(int bottomTicksHeight) {
		this.bottomTicksHeight = bottomTicksHeight;
	}
	public int getPlotHeight() {
		return plotHeight;
	}
	public void setPlotHeight(int plotHeight) {
		this.plotHeight = plotHeight;
	}
	public int getTopTicksHeight() {
		return topTicksHeight;
	}
	public void setTopTicksHeight(int topTicksHeight) {
		this.topTicksHeight = topTicksHeight;
	}
	public int getTopTicksToTopAxisHeight() {
		return topTicksToTopAxisHeight;
	}
	public void setTopTicksToTopAxisHeight(int topTicksToTopAxisHeight) {
		this.topTicksToTopAxisHeight = topTicksToTopAxisHeight;
	}
	public int getTopAxisToTopAxisLabelHeight() {
		return topAxisToTopAxisLabelHeight;
	}
	public void setTopAxisToTopAxisLabelHeight(int topAxisToTopAxisLabelHeight) {
		this.topAxisToTopAxisLabelHeight = topAxisToTopAxisLabelHeight;
	}
	public int getTopAxisLabelToTitleHeight() {
		return topAxisLabelToTitleHeight;
	}
	public void setTopAxisLabelToTitleHeight(int topAxisLabelToTitleHeight) {
		this.topAxisLabelToTitleHeight = topAxisLabelToTitleHeight;
	}
	public int getTitleToImageTopHeight() {
		return titleToImageTopHeight;
	}
	public void setTitleToImageTopHeight(int titleToImageTopHeight) {
		this.titleToImageTopHeight = titleToImageTopHeight;
	}
	public int getImageLeftToLeftAxisLabelWidth() {
		return imageLeftToLeftAxisLabelWidth;
	}
	public void setImageLeftToLeftAxisLabelWidth(int imageLeftToLeftAxisLabelWidth) {
		this.imageLeftToLeftAxisLabelWidth = imageLeftToLeftAxisLabelWidth;
	}
	public int getLeftAxisLabelToLeftAxisWidth() {
		return leftAxisLabelToLeftAxisWidth;
	}
	public void setLeftAxisLabelToLeftAxisWidth(int leftAxisLabelToLeftAxisWidth) {
		this.leftAxisLabelToLeftAxisWidth = leftAxisLabelToLeftAxisWidth;
	}
	public int getLeftAxisToLeftTicksWidth() {
		return leftAxisToLeftTicksWidth;
	}
	public void setLeftAxisToLeftTicksWidth(int leftAxisToLeftTicksWidth) {
		this.leftAxisToLeftTicksWidth = leftAxisToLeftTicksWidth;
	}
	public int getLeftTicksWidth() {
		return leftTicksWidth;
	}
	public void setLeftTicksWidth(int leftTicksWidth) {
		this.leftTicksWidth = leftTicksWidth;
	}
	public int getPlotWidth() {
		return plotWidth;
	}
	public void setPlotWidth(int plotWidth) {
		this.plotWidth = plotWidth;
	}
	public int getRightTicksWidth() {
		return rightTicksWidth;
	}
	public void setRightTicksWidth(int rightTicksWidth) {
		this.rightTicksWidth = rightTicksWidth;
	}
	public int getRightTicksToRightAxisWidth() {
		return rightTicksToRightAxisWidth;
	}
	public void setRightTicksToRightAxisWidth(int rightTicksToRightAxisWidth) {
		this.rightTicksToRightAxisWidth = rightTicksToRightAxisWidth;
	}
	public int getRightAxisToRightAxisLabelWidth() {
		return rightAxisToRightAxisLabelWidth;
	}
	public void setRightAxisToRightAxisLabelWidth(int rightAxisToRightAxisLabelWidth) {
		this.rightAxisToRightAxisLabelWidth = rightAxisToRightAxisLabelWidth;
	}
	public int getRightAxisLabelToColorBarWidth() {
		return rightAxisLabelToColorBarWidth;
	}
	public void setRightAxisLabelToColorBarWidth(int rightAxisLabelToColorBarWidth) {
		this.rightAxisLabelToColorBarWidth = rightAxisLabelToColorBarWidth;
	}
	public int getColorBarWith() {
		return colorBarWith;
	}
	public void setColorBarWith(int colorBarWith) {
		this.colorBarWith = colorBarWith;
	}
	public int getColorBarTicksWidth() {
		return colorBarTicksWidth;
	}
	public void setColorBarTicksWidth(int colorBarTicksWidth) {
		this.colorBarTicksWidth = colorBarTicksWidth;
	}
	public int getColorBarTicksToColorBarValuesWidth() {
		return colorBarTicksToColorBarValuesWidth;
	}
	public void setColorBarTicksToColorBarValuesWidth(int colorBarTicksToColorBarValuesWidth) {
		this.colorBarTicksToColorBarValuesWidth = colorBarTicksToColorBarValuesWidth;
	}
	public int getColorBarValuesToPlotRightWidth() {
		return colorBarValuesToPlotRightWidth;
	}
	public void setColorBarValuesToPlotRightWidth(int colorBarValuesToPlotRightWidth) {
		this.colorBarValuesToPlotRightWidth = colorBarValuesToPlotRightWidth;
	}
	public int getBottomAxisLabelHeight() {
		return bottomAxisLabelHeight;
	}
	public int getBottomAxisHeight() {
		return bottomAxisHeight;
	}
	public int getTopAxisHeight() {
		return topAxisHeight;
	}
	public int getTopAxisLabelHeight() {
		return topAxisLabelHeight;
	}
	public int getTitleHeight() {
		return titleHeight;
	}
	public int getLeftAxisLabelWidth() {
		return leftAxisLabelWidth;
	}
	public int getLeftAxisWidth() {
		return leftAxisWidth;
	}
	public int getRightAxisWidth() {
		return rightAxisWidth;
	}
	public int getRightAxisLabelWidth() {
		return rightAxisLabelWidth;
	}
	public int getColorBarValuesWidth() {
		return colorBarValuesWidth;
	}

	@Override
	public int imageLeftToLegendLeftWidth() {
		return 0;
	}
	

}
