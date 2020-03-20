package thesis.Charter.PlotFolder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import thesis.Charter.Axis.Axis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.Others.ChartMeasurements;
import thesis.Charter.Others.XYChartMeasurements;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;

public abstract class Plot {
	
	
	
	private boolean bottomChartOutline;
	private boolean leftChartOutline;
	private boolean topChartOutline;
	private boolean rightChartOutline;
	
	
	private Color defaultChartOutlineColor = Color.BLACK;
	private Color bottomChartOutlineColor = this.defaultChartOutlineColor;
	private Color leftChartOutlineColor = this.defaultChartOutlineColor;
	private Color topChartOutlineColor = this.defaultChartOutlineColor;
	private Color rightChartOutlineColor = this.defaultChartOutlineColor;
	private float defaultChartOutlineWidth = 2f;
	private float bottomChartOutlineWidth = this.defaultChartOutlineWidth;
	private float leftChartOutlineWidth = this.defaultChartOutlineWidth;
	private float topChartOutlineWidth = this.defaultChartOutlineWidth;
	private float rightChartOutlineWidth = this.defaultChartOutlineWidth;
	
	
	private Color chartBackgroundColor = new Color(229,229,239);;
	private Image backgroundImage = null;
	
	public void setChartBackgroundImage(Image image) {
		this.backgroundImage = image;
	}
	public void setChartBackgroundImage(String imagePath) {
		try {
			this.backgroundImage = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			System.out.println("No such path");
			e.printStackTrace();
		}
	}
	
	public void setChartBackgroundColor(Color chartBackgroundColor) {
		this.chartBackgroundColor = chartBackgroundColor;
	}
	
	public void setChartOutlineColors(Color[] chartOutlineColors) {
		if (chartOutlineColors.length != 4) {
			System.out.println("Error: Must have four values for chart outline colors");
			return;
		}
		this.bottomChartOutlineColor = chartOutlineColors[0];
		this.leftChartOutlineColor = chartOutlineColors[1];
		this.topChartOutlineColor = chartOutlineColors[2];
		this.rightChartOutlineColor = chartOutlineColors[3];
	}
	
	public void setChartOutlineColor(Color outlineColor) {
		this.bottomChartOutlineColor = outlineColor;
		this.leftChartOutlineColor = outlineColor;
		this.topChartOutlineColor = outlineColor;
		this.rightChartOutlineColor = outlineColor;
	}
	
	public void setChartOutlineWidth(float[] chartOutlineWidths) {
		if (chartOutlineWidths.length != 4) {
			System.out.println("Error: Must have four values for chart outline widths");
			return;
		}
		this.bottomChartOutlineWidth = chartOutlineWidths[0];
		this.leftChartOutlineWidth = chartOutlineWidths[1];
		this.topChartOutlineWidth = chartOutlineWidths[2];
		this.rightChartOutlineWidth = chartOutlineWidths[3];
	}
	public void setChartOutlineWidth(float outlineWidth) {
		this.bottomChartOutlineWidth = outlineWidth;
		this.leftChartOutlineWidth = outlineWidth;
		this.topChartOutlineWidth = outlineWidth;
		this.rightChartOutlineWidth = outlineWidth;
	}
	
	/*
	 *  0: Bottom
	 *  1: Left
	 *  2: Top
	 *  3: Right
	 */
	public void includeChartOutline(boolean[] chartOutlines) {
		if (chartOutlines.length != 4) {
			System.out.println("Error: Must have four values for chart outlines");
			return;
		}
		this.bottomChartOutline = chartOutlines[0];
		this.leftChartOutline = chartOutlines[1];
		this.topChartOutline = chartOutlines[2];
		this.rightChartOutline = chartOutlines[3];
		
	}
	
	public void drawPlotBackground(Graphics2D g, ChartMeasurements cm) {

		if (this.backgroundImage == null) {
			
			g.setColor(this.chartBackgroundColor);
			
			g.fillRect(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.getPlotWidth(), cm.getPlotHeight());
		} else {
			g.drawImage(this.backgroundImage, cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotTopHeight(), 0, 0, this.backgroundImage.getWidth(null), this.backgroundImage.getHeight(null), null);

		}
		
		
	}
	
	public void drawPlotOutline(Graphics2D g, ChartMeasurements cm) {
		
		if (this.bottomChartOutline) {
			g.setStroke(new BasicStroke(this.bottomChartOutlineWidth));
			g.setColor(this.bottomChartOutlineColor);
			g.drawLine(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotBottomHeight());
		}
		
		if (this.leftChartOutline) {
			g.setStroke(new BasicStroke(this.leftChartOutlineWidth));
			g.setColor(this.leftChartOutlineColor);
			g.drawLine(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotTopHeight());
		}
		
		if (this.topChartOutline) {
			g.setStroke(new BasicStroke(this.topChartOutlineWidth));
			g.setColor(this.topChartOutlineColor);
			g.drawLine(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotTopHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotTopHeight());
		}
		
		if (this.rightChartOutline) {
			g.setStroke(new BasicStroke(this.rightChartOutlineWidth));
			g.setColor(this.rightChartOutlineColor);
			g.drawLine(cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotTopHeight());
		}
	}
	
	
}
