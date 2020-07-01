package thesis.Charter.Plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import thesis.Charter.Axis.BaseAxis;
import thesis.Charter.Axis.NumericAxis;
import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.Styles.Style;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataFrame;
import thesis.DataFrame.DataItem;
import thesis.Helpers.Palette;

public abstract class Plot {
	
	private boolean bottomPlotOutline;
	private boolean leftPlotOutline;
	private boolean topPlotOutline;
	private boolean rightPlotOutline;
	
	
	private Color defaultPlotOutlineColor = Color.BLACK;
	private Color bottomPlotOutlineColor = this.defaultPlotOutlineColor;
	private Color leftPlotOutlineColor = this.defaultPlotOutlineColor;
	private Color topPlotOutlineColor = this.defaultPlotOutlineColor;
	private Color rightPlotOutlineColor = this.defaultPlotOutlineColor;
	private float defaultPlotOutlineWidth = 2f;
	private float bottomPlotOutlineWidth = this.defaultPlotOutlineWidth;
	private float leftPlotOutlineWidth = this.defaultPlotOutlineWidth;
	private float topPlotOutlineWidth = this.defaultPlotOutlineWidth;
	private float rightPlotOutlineWidth = this.defaultPlotOutlineWidth;
	
	
	private Color plotBackgroundColor = new Color(229,229,239);
	private Image backgroundImage = null;
	
	protected Color[] colorPalette = Palette.Default;
	
	public void setStyle(Style styleToSet) {
		this.setPlotBackgroundColor(styleToSet.getPlotBackgroundColor());
		this.includePlotOutline(new boolean[] {
			styleToSet.getDrawBottomPlotOutline(),
			styleToSet.getDrawLeftPlotOutline(),
			styleToSet.getDrawTopPlotOutline(),
			styleToSet.getDrawRightPlotOutline()
		});
		this.setPlotOutlineColors(new Color[] {
			styleToSet.getBottomPlotOutlineColor(),
			styleToSet.getLeftPlotOutlineColor(),
			styleToSet.getTopPlotOutlineColor(),
			styleToSet.getRightPlotOutlineColor()
		});
		this.setPlotOutlineWidth(new float[] {
			styleToSet.getBottomPlotOutlineWidth(),
			styleToSet.getLeftPlotOutlineWidth(),
			styleToSet.getTopPlotOutlineWidth(),
			styleToSet.getRightPlotOutlineWidth()
		});
		this.setPlotBackgroundImage(styleToSet.getPlotBackgroundImage());
		this.colorPalette = styleToSet.getColorPalette();
	}
	
	public void setPlotBackgroundImage(Image image) {
		this.backgroundImage = image;
	}
	public void setPlotBackgroundImage(String imagePath) {
		try {
			this.backgroundImage = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			System.out.println("No such path");
			e.printStackTrace();
		}
	}
	
	public void setPlotBackgroundColor(Color plotBackgroundColor) {

		this.plotBackgroundColor = plotBackgroundColor;
	}
	
	public Color getPlotBackgroundColor() {
		return this.plotBackgroundColor;
	}
	
	public void setPlotOutlineColors(Color[] plotOutlineColors) {
		if (plotOutlineColors.length != 4) {
			System.out.println("Error: Must have four values for plot outline colors");
			return;
		}
		this.bottomPlotOutlineColor = plotOutlineColors[0];
		this.leftPlotOutlineColor = plotOutlineColors[1];
		this.topPlotOutlineColor = plotOutlineColors[2];
		this.rightPlotOutlineColor = plotOutlineColors[3];
	}
	
	public void setPlotOutlineColor(Color outlineColor) {
		this.bottomPlotOutlineColor = outlineColor;
		this.leftPlotOutlineColor = outlineColor;
		this.topPlotOutlineColor = outlineColor;
		this.rightPlotOutlineColor = outlineColor;
	}
	
	public void setPlotOutlineWidth(float[] plotOutlineWidths) {
		if (plotOutlineWidths.length != 4) {
			System.out.println("Error: Must have four values for plot outline widths");
			return;
		}
		this.bottomPlotOutlineWidth = plotOutlineWidths[0];
		this.leftPlotOutlineWidth = plotOutlineWidths[1];
		this.topPlotOutlineWidth = plotOutlineWidths[2];
		this.rightPlotOutlineWidth = plotOutlineWidths[3];
	}
	public void setPlotOutlineWidth(float outlineWidth) {
		this.bottomPlotOutlineWidth = outlineWidth;
		this.leftPlotOutlineWidth = outlineWidth;
		this.topPlotOutlineWidth = outlineWidth;
		this.rightPlotOutlineWidth = outlineWidth;
	}
	
	/*
	 *  0: Bottom
	 *  1: Left
	 *  2: Top
	 *  3: Right
	 */
	public void includePlotOutline(boolean[] plotOutlines) {
		if (plotOutlines.length != 4) {
			System.out.println("Error: Must have four values for plot outlines");
			return;
		}
		
		this.bottomPlotOutline = plotOutlines[0];
		this.leftPlotOutline = plotOutlines[1];
		this.topPlotOutline = plotOutlines[2];
		this.rightPlotOutline = plotOutlines[3];
		
	}
	
	public void drawPlotBackground(Graphics2D g, ChartMeasurements cm) {

		if (this.backgroundImage == null) {
			g.setColor(this.plotBackgroundColor);
			
			g.fillRect(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.getPlotWidth(), cm.getPlotHeight());
		} else {
			g.drawImage(this.backgroundImage, cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotTopHeight(), 0, 0, this.backgroundImage.getWidth(null), this.backgroundImage.getHeight(null), null);

		}
		
		
	}
	
	public void drawPlotOutline(Graphics2D g, ChartMeasurements cm) {
		if (this.bottomPlotOutline) {
			g.setStroke(new BasicStroke(this.bottomPlotOutlineWidth));
			g.setColor(this.bottomPlotOutlineColor);
			g.drawLine(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotBottomHeight());
		}
		
		if (this.leftPlotOutline) {
			g.setStroke(new BasicStroke(this.leftPlotOutlineWidth));
			g.setColor(this.leftPlotOutlineColor);
			g.drawLine(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotTopHeight());
		}
		
		if (this.topPlotOutline) {
			g.setStroke(new BasicStroke(this.topPlotOutlineWidth));
			g.setColor(this.topPlotOutlineColor);
			g.drawLine(cm.imageLeftToPlotLeftWidth(), cm.imageBottomToPlotTopHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotTopHeight());
		}
		
		if (this.rightPlotOutline) {
			g.setStroke(new BasicStroke(this.rightPlotOutlineWidth));
			g.setColor(this.rightPlotOutlineColor);
			g.drawLine(cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotBottomHeight(), cm.imageLeftToPlotRightWidth(), cm.imageBottomToPlotTopHeight());
		}
	}
	
	
}
