package thesis.Charter.Plots;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import thesis.Charter.ChartMeasurements.OnlyPlotChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;

public class WordCloudPlot extends Plot {

    private int calculatedWidth = 0;
    private int calculatedheight = 0;

    private String fontFamilty = "Dialog";
    private int smallestFontSize = 10;
    private int largestFontSize = 30;

    private class WordLocation {
        String word;
        Font font;
        int plotX;
        int plotY;
        Rectangle rect;
    }
    private Rectangle boundingRectangle;

    private WordLocation[] placedWords;


	public void placeWords(String[] wordsArr, int[] wordCountArr) {
        int minCountWords = CommonArray.minValue(wordCountArr);
        int maxCountWords = CommonArray.maxValue(wordCountArr);
        int rangeCountWords = maxCountWords - minCountWords;

        boundingRectangle = new Rectangle(0, 0, 0, 0);
        for (int wordCount = 0; wordCount < wordsArr.length; wordCount++) {
            String currentWord = wordsArr[wordCount];
            int fontSize = CommonMath.map(wordCountArr[wordCount], minCountWords, maxCountWords, this.smallestFontSize, this.largestFontSize);
            Font fontToUse = new Font(fontFamilty, Font.PLAIN, fontSize);
            Rectangle shape = DrawString.get2DBounds(currentWord, fontToUse).getBounds();
            
        }
	}

	public int getCalculatedWidth() {
		return this.calculatedWidth;
	}

	public int getCalculatedHeight() {
		return this.calculatedheight;
	}

	public void drawPlot(Graphics2D g, OnlyPlotChartMeasurements cm) {
	}

    
}
