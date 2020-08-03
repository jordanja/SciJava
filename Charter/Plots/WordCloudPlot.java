package thesis.Charter.Plots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import thesis.Charter.ChartMeasurements.OnlyPlotChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.Common.CommonMath;

public class WordCloudPlot extends Plot {

    private String fontFamilty = "Dialog";
    private int smallestFontSize = 30;
    private int largestFontSize = 60;

    int xOffset = 0;
    int yOffset = 0;

    private class WordLocation {
        
        String word;
        Font font;
        int plotX;
        int plotY;
        Rectangle rect;

        public WordLocation(String word, Font font, int plotX, int plotY, Rectangle rect) {
            this.word = word;
            this.font = font;
            this.plotX = plotX;
            this.plotY = plotY;
            this.rect = rect;
        }
    }
    private Rectangle boundingRectangle;

    private List<WordLocation> placedWords;

    public enum direction {Up, Left, Down, Right};
    private int movementAmount = 5;

	public void placeWords(String[] wordsArr, int[] wordCountArr) {
        int minCountWords = CommonArray.minValue(wordCountArr);
        int maxCountWords = CommonArray.maxValue(wordCountArr);

        placedWords = new ArrayList<WordLocation>();

        direction currentDirection = direction.Up;
        int lastPlacedX = 0;
        int lastPlacedY = 0;

        boundingRectangle = new Rectangle(0, 0, 1, 1);
        for (int wordCount = 0; wordCount < wordsArr.length; wordCount++) {
            String currentWord = wordsArr[wordCount];
            int fontSize = CommonMath.map(wordCountArr[wordCount], minCountWords, maxCountWords, this.smallestFontSize, this.largestFontSize);
            Font fontToUse = new Font(fontFamilty, Font.PLAIN, fontSize);
            Rectangle wordShape = DrawString.get2DBounds(currentWord, fontToUse).getBounds();
            if ((lastPlacedX == 0) && (lastPlacedY == 0)) {
                wordShape.setLocation((int) (-wordShape.getWidth()/2), (int) (-wordShape.getHeight()/2));
            } else {
                wordShape.setLocation(lastPlacedX, lastPlacedY);
            }
            
            while (intersects(wordShape)) {
                if (currentDirection == direction.Up) {
                    // System.out.println("Moving \"" + currentWord + "\" Up");
                    wordShape.translate(0, movementAmount);
                } else if (currentDirection == direction.Left) {
                    wordShape.translate(-movementAmount, 0);
                } else if (currentDirection == direction.Down) {
                    wordShape.translate(0, -movementAmount);
                } else if (currentDirection == direction.Right) {
                    wordShape.translate(movementAmount, 0);
                }
            } 
            WordLocation newWord = new WordLocation(currentWord, fontToUse, (int)wordShape.getCenterX(), (int)wordShape.getCenterY(), wordShape);
            placedWords.add(newWord);
            currentDirection = changeDirection(currentDirection);
            lastPlacedX = newWord.plotX;
            lastPlacedY = newWord.plotY;
            boundingRectangle = boundingRectangle.createUnion(newWord.rect).getBounds();
        }
	}

    private boolean intersects(Rectangle wordShape) {
        for (WordLocation word: placedWords) {
            if (word.rect.intersects(wordShape)) {
                return true;
            }
        }
        return false;
    }

    private direction changeDirection(direction currentDirection) {
        if (currentDirection == direction.Up) {
            return direction.Left;
        } else if (currentDirection == direction.Left) {
            return direction.Down;
        } else if (currentDirection == direction.Down) {
            return direction.Right;
        } else {
            return direction.Up;
        }
    }

	public int getCalculatedWidth() {
		return this.boundingRectangle.width;
	}

	public int getCalculatedHeight() {
		return this.boundingRectangle.height;
	}

    public int getXOffset() {
        return this.xOffset;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }
	public void drawPlot(Graphics2D g, OnlyPlotChartMeasurements cm) {
        int plotWidth = cm.getPlotWidth();
        int plotHeight = cm.getPlotHeight();
        int plotXStart = cm.imageLeftToPlotLeftWidth();
        int plotYStart = cm.imageBottomToPlotBottomHeight();

        int wordCount = 0;

        for (WordLocation word: placedWords) {

            DrawString.setTextStyle(this.colorPalette[wordCount % this.colorPalette.length], word.font, 0);
            DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
            DrawString.write(g, word.word, plotXStart + word.plotX + plotWidth/2 + this.xOffset, plotYStart + word.plotY + plotHeight/2 + this.yOffset);
            
            wordCount++;
        }
	}

    public String getFontFamilty() {
        return fontFamilty;
    }

    public void setFontFamilty(String fontFamilty) {
        this.fontFamilty = fontFamilty;
    }

    public int getSmallestFontSize() {
        return smallestFontSize;
    }

    public void setSmallestFontSize(int smallestFontSize) {
        this.smallestFontSize = smallestFontSize;
    }

    public int getLargestFontSize() {
        return largestFontSize;
    }

    public void setLargestFontSize(int largestFontSize) {
        this.largestFontSize = largestFontSize;
    }

    
}
