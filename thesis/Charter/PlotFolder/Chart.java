package thesis.Charter.PlotFolder;

import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
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


public abstract class Chart {
	
	protected DataFrame dataFrame;
	
	protected BufferedImage chartImage;

	protected Color imageBackgroundColor = Color.WHITE;

	
	protected String title;
	protected Font titleFont = new Font("Dialog", Font.PLAIN, 12);
	protected Color titleColor = Color.BLACK;

	
	public Chart() {
		
	}
	
	
	public Chart(DataFrame dataFrame, String chartType) {	
		this.dataFrame = dataFrame;
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

	
	
	public void setImageBackgroundColor(Color color) {
		this.imageBackgroundColor = color;
	}
	
	public String getTitle() {
		return this.title;
	}
	public Font getTitleFont() {
		return this.titleFont;
	}
	
	public abstract void Create();
	
	protected void instantiateChart(ChartMeasurements cm) {	
		this.chartImage = new BufferedImage(cm.imageWidth(), cm.imageHeight(), BufferedImage.TYPE_INT_RGB);
	}
	
	protected Graphics2D initializaGraphicsObject(ChartMeasurements cm) {
		Graphics2D g = this.chartImage.createGraphics();	
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		changeCoordFrame(g, cm); 
		
		return g;
	}

	private void changeCoordFrame(Graphics2D g, ChartMeasurements cm) {
		g.translate(0.0, cm.imageHeight());
		g.scale(1.0, -1.0);
	}
	
	protected void drawBackground(Graphics2D g, ChartMeasurements cm) {
		g.setBackground(this.imageBackgroundColor);
		g.clearRect(0, 0, cm.imageWidth(), cm.imageHeight());
	}
	
	
	protected void drawTitle(Graphics2D g, ChartMeasurements cm) {
		if (this.title != null) {			
			g.setColor(this.titleColor);
			g.setFont(this.titleFont);
			DrawString.write(g, this.title, cm.imageWidth()/2, cm.imageBottomToTitleBottomHeight(), DrawString.xAlignment.CenterAlign, DrawString.yAlignment.BottomAlign, 0, cm);
		}
	}
	
	public BufferedImage GetImage() {
		return this.chartImage;
	}
	
	public void WriteFile(String fileLoc) {
		try {
		    ImageIO.write(this.chartImage, "png", new File(fileLoc));
		} catch (IOException e) {
		   
		}
	}
	
	
}
