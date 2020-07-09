package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import thesis.Charter.ChartMeasurements.NoAxisChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame;

public class MapPlot extends Plot {

	
	
	private int squareSize = 40;
	private int insetPixels = 3;
	private int squareCurve = 15;
	
	private boolean drawSquareOutline = true;
	private int squareOutlineWidth = 2;
	
	private boolean drawStateNames = true;
	private Font stateNameFont = new Font("Dialog", Font.PLAIN, 20);
	private Color stateNameColor = Color.BLACK;
	
	public void drawPlot(Graphics2D g, DataFrame dataDF, String statesColumnName, String[][] map,  NoAxisChartMeasurements cm) {
		String[] states = dataDF.getColumnAsStringArray(statesColumnName);
		Color[] colors = dataDF.getColumnAsColorArray("color");
		
		for (int rowIndex = 0; rowIndex < map.length; rowIndex++) {
			for (int columnIndex = 0; columnIndex < map[rowIndex].length; columnIndex++) {
				int x = cm.getImageLeftToPlotLeftWidth() + columnIndex * this.squareSize + insetPixels;
				int y = cm.imageBottomToPlotTopHeight() - (rowIndex + 1) * this.squareSize + insetPixels;
				int width = this.squareSize - insetPixels;
				int height = this.squareSize - insetPixels;
				String currentState = map[rowIndex][columnIndex];
				int index = CommonArray.indexOf(states, currentState);
				
				if (index >= 0) {
					
					g.setColor(colors[index]);
					g.fillRoundRect(x, y, width, height, this.squareCurve, this.squareCurve);
					
					if (this.drawSquareOutline) {
						g.setColor(new Color(colors[index].getRed(), colors[index].getGreen(), colors[index].getBlue()));
						g.setStroke(new BasicStroke(this.squareOutlineWidth));
						g.drawRoundRect(x, y, width, height, this.squareCurve, this.squareCurve);	
					}
					
					if (this.drawStateNames) {
						DrawString.setTextStyle(this.stateNameColor, this.stateNameFont, 0);
						DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.MiddleAlign);
						int textX = cm.getImageLeftToPlotLeftWidth() + columnIndex * this.squareSize + this.squareSize/2;
						int textY = cm.imageBottomToPlotTopHeight() - (rowIndex + 1) * this.squareSize + this.squareSize/2;
						DrawString.write(g, currentState, textX, textY);
					}
				}
					
			}	
		}
	}

	public int getSquareSize() {
		return squareSize;
	}

	public void setSquareSize(int squareSize) {
		this.squareSize = squareSize;
	}

	public int getInsetPixels() {
		return insetPixels;
	}

	public void setInsetPixels(int insetPixels) {
		this.insetPixels = insetPixels;
	}

	public int getSquareCurve() {
		return squareCurve;
	}

	public void setSquareCurve(int squareCurve) {
		this.squareCurve = squareCurve;
	}

	public boolean isDrawStateNames() {
		return drawStateNames;
	}

	public void setDrawStateNames(boolean drawStateNames) {
		this.drawStateNames = drawStateNames;
	}

	public Font getStateNameFont() {
		return stateNameFont;
	}

	public void setStateNameFont(Font stateNameFont) {
		this.stateNameFont = stateNameFont;
	}

	public Color getStateNameColor() {
		return stateNameColor;
	}

	public void setStateNameColor(Color stateNameColor) {
		this.stateNameColor = stateNameColor;
	}

	public boolean isDrawSquareOutline() {
		return drawSquareOutline;
	}

	public void setDrawSquareOutline(boolean drawSquareOutline) {
		this.drawSquareOutline = drawSquareOutline;
	}

	public int getSquareOutlineWidth() {
		return squareOutlineWidth;
	}

	public void setSquareOutlineWidth(int squareOutlineWidth) {
		this.squareOutlineWidth = squareOutlineWidth;
	}
	
	

}
