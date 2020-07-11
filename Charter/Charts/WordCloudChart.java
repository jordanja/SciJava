package thesis.Charter.Charts;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.awt.Color;
import thesis.Charter.ChartMeasurements.OnlyPlotChartMeasurements;
import thesis.Charter.Plots.WordCloudPlot;
import thesis.Common.CommonArray;

public class WordCloudChart extends Chart {
	
	private WordCloudPlot plot;
	private OnlyPlotChartMeasurements cm;

	private String wordCloudString = "";
	private int numStringsToShow = 10;

	public WordCloudChart(String wordCloudString, int numStringsToShow) {
		this.wordCloudString = wordCloudString;
		this.numStringsToShow = numStringsToShow;

		this.plot = new WordCloudPlot();
		this.cm = new OnlyPlotChartMeasurements();
	}

	@Override
	public void create() {
		String[] wordsArr = CommonArray.splitStringOnSpaces(this.wordCloudString);
		String[] lowerCaseArr = CommonArray.lowerCase(wordsArr);
		String[] plainArr = CommonArray.removeNonLettersAndNumbers(lowerCaseArr);
		String[] uniqueArr = CommonArray.removeDuplicates(plainArr);

		int[] wordCountArr = new int[uniqueArr.length];

		for (int wordCount = 0; wordCount < uniqueArr.length; wordCount++) {
			wordCountArr[wordCount] = CommonArray.countOccurences(plainArr, uniqueArr[wordCount]);
		}
		
		int n = wordCountArr.length;
		for (int i = 1; i < n; ++i) { 
            int valueKey = wordCountArr[i]; 
            String strKey = uniqueArr[i]; 
            int j = i - 1; 
            while (j >= 0 && wordCountArr[j] < valueKey) { 
				wordCountArr[j + 1] = wordCountArr[j]; 
				uniqueArr[j + 1] = uniqueArr[j];
                j = j - 1; 
            } 
			wordCountArr[j + 1] = valueKey; 
			uniqueArr[j + 1] = strKey;
		}
		
		uniqueArr = Arrays.copyOf(uniqueArr, this.numStringsToShow);
		wordCountArr = Arrays.copyOf(wordCountArr, this.numStringsToShow);

		this.plot.placeWords(uniqueArr, wordCountArr);

		int plotWidth = this.plot.getCalculatedWidth();
		int plotHeight = this.plot.getCalculatedHeight();

		this.cm.setPlotWidth(plotWidth);
		this.cm.setPlotHeight(plotHeight);

		this.cm.calculateChartImageMetrics(this.getTitle(), this.getTitleFont());
		this.instantiateChart(this.cm);

		Graphics2D g = initializaGraphicsObject(this.cm);
		
		this.drawBackground(g, this.cm);
		
		this.plot.setPlotBackgroundColor(Color.white);
		
		this.plot.drawPlotBackground(g, this.cm);
		
		this.plot.drawPlotOutline(g, this.cm);
	
		this.plot.drawPlot(g, this.cm);
		g.setColor(Color.BLACK);
		// g.drawRect(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.getPlotWidth(), cm.getPlotHeight());
		
		this.drawTitle(g, this.cm);
	
	}


	
	public int getNumStringsToShow() {
		return this.numStringsToShow;
	}

	public void setNumStringsToShow(int numStringsToShow) {
		this.numStringsToShow = numStringsToShow;
	}

	public WordCloudPlot getPlot() {
		return plot;
	}

	public void setPlot(WordCloudPlot plot) {
		this.plot = plot;
	}

	public OnlyPlotChartMeasurements getChartMeasurements() {
		return cm;
	}

	public void setChartMeasurements(OnlyPlotChartMeasurements cm) {
		this.cm = cm;
	}
}
