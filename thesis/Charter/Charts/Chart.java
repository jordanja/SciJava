package thesis.Charter.Charts;

import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Image.WholeImage;
import thesis.Charter.StringDrawer.DrawArrow;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public abstract class Chart extends WholeImage {
	
	
	protected DataFrame dataFrame;
	private xAlignment defaultTextXAlign = xAlignment.CenterAlign;
	private yAlignment defaultTextYAlignment = yAlignment.MiddleAlign;
	private Color defautTextColor = Color.black;
	private Font defaultTextFont = new Font("Dialog", Font.PLAIN, 12);
	private float defaultTextRotation = 0;
	
	private Color defaultArrowColor = Color.BLACK;
	private int defaultArrowWeight = 1;
	
	protected String title;
	protected Font titleFont = new Font("Dialog", Font.PLAIN, 12);
	protected Color titleColor = Color.BLACK;
	
	public Chart() {
		super();
	}
	
	
	public Chart(DataFrame dataFrame) {	
		super();
		this.dataFrame = dataFrame;
	}
	
	public void write(String text, int x, int y, xAlignment xAlign, yAlignment yAlign, Color color, Font font, float rotation) {
		DrawString.setAlignment(xAlign, yAlign);
		DrawString.setTextStyle(color, font, rotation);
		DrawString.write(g, text, x, y);
	}
	
	public void write(String text, int x, int y, Color color) {
		write(text, x, y, this.defaultTextXAlign, this.defaultTextYAlignment, color, this.defaultTextFont, this.defaultTextRotation);
	}
	public void write(String text, int x, int y, Font font) {
		write(text, x, y, this.defaultTextXAlign, this.defaultTextYAlignment, this.defautTextColor, font, this.defaultTextRotation);
	}
	public void write(String text, int x, int y, float rotation) {
		write(text, x, y, this.defaultTextXAlign, this.defaultTextYAlignment, this.defautTextColor, this.defaultTextFont, rotation);
	}
	public void write(String text, int x, int y, xAlignment xAlign, yAlignment yAlign) {
		write(text, x, y, xAlign, yAlign, this.defautTextColor, this.defaultTextFont, this.defaultTextRotation);
	}
	
	public void write(String text, int x, int y) {
		write(text, x, y, this.defaultTextXAlign, this.defaultTextYAlignment, this.defautTextColor, this.defaultTextFont, this.defaultTextRotation);
	}
	
	public void drawArrow(int x1, int y1, int x2, int y2) {
		DrawArrow.setColor(this.defaultArrowColor);
		DrawArrow.setLineWeight(this.defaultArrowWeight);
		DrawArrow.drawArrow(g, x1, y1, x2, y2);
	}
	public void drawArrow(int x1, int y1, int x2, int y2, Color lineColor, int lineWeight) {
		DrawArrow.setColor(lineColor);
		DrawArrow.setLineWeight(lineWeight);
		DrawArrow.drawArrow(g, x1, y1, x2, y2);
	}
	
	 //If using DataFrame
	public void setDataFrame(DataFrame df) {
		this.dataFrame = df;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}
	
	public String getTitle() {
		return this.title;
	}

	public Font getTitleFont() {
		return this.titleFont;
	}	
	
	protected void drawTitle(Graphics2D g, ChartMeasurements cm) {
		if (this.title != null) {	
			
			DrawString.setTextStyle(this.titleColor, this.titleFont, 0);
			DrawString.setAlignment(DrawString.xAlignment.CenterAlign, DrawString.yAlignment.BottomAlign);
			DrawString.write(g, this.title, cm.imageWidth()/2, cm.imageBottomToTitleBottomHeight());
		}
	}
	
	public abstract void Create();
	
	
	
}
