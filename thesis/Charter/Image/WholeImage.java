package thesis.Charter.Image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import thesis.Charter.ChartMeasurements.ChartMeasurements;

public class WholeImage {

	protected BufferedImage chartImage;

	protected Color imageBackgroundColor = Color.WHITE;

	protected Graphics2D g;
	
	public WholeImage() {
		
	}
	
	
	public void setImageBackgroundColor(Color color) {
		this.imageBackgroundColor = color;
	}
	
	
	protected void instantiateChart(ChartMeasurements cm) {	
		this.chartImage = new BufferedImage(cm.imageWidth(), cm.imageHeight(), BufferedImage.TYPE_INT_RGB);
	}
	
	protected Graphics2D initializaGraphicsObject(ChartMeasurements cm) {
		g = this.chartImage.createGraphics();	
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
	public BufferedImage getImage() {
		return this.chartImage;
	}
	
	public void setImage(BufferedImage image) {
		this.chartImage = image;
	}
	
	public int getImageWidth() {
		return this.chartImage.getWidth();
	}
	
	public int getImageHeight() {
		return this.chartImage.getHeight();
	}
	
	public void WriteFile(String fileLoc) {
		try {
		    ImageIO.write(this.chartImage, "png", new File(fileLoc));
		} catch (IOException e) {
		   
		}
	}
}
