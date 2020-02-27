package thesis.Charter.StringDrawer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import thesis.Charter.Others.XYChartMeasurements;


public class DrawString {
	public enum xAlignment {LeftAlign, CenterAlign, RightAlign};
	public enum yAlignment {TopAlign, MiddleAlign, BottomAlign};
	
	public static void drawString(Graphics2D g, String string, int x, int y, xAlignment xAlign, yAlignment yAlign, float rotation, XYChartMeasurements cm) {
		AffineTransform orig = g.getTransform();
		
		g.scale(1.0, -1.0);
		g.translate(0, -cm.imageHeight());
		
		
		int xPosition = x;
		int yPosition = cm.imageHeight() - y;
		Rectangle bounds = getShapeOfText(g.getFont(), string).getBounds();
		
		if (xAlign == xAlignment.LeftAlign) {
			xPosition = x;
		} else if (xAlign == xAlignment.CenterAlign) {
			xPosition = x - g.getFontMetrics().stringWidth(string)/2;
		} else if (xAlign == xAlignment.RightAlign) {
			xPosition = x - g.getFontMetrics().stringWidth(string);
		}
		
		if (yAlign == yAlignment.TopAlign) {
			yPosition = cm.imageHeight() - y + bounds.height;
		} else if (yAlign == yAlignment.MiddleAlign) {
			yPosition = cm.imageHeight() - y + bounds.height/2;
		} else if (yAlign == yAlignment.BottomAlign) {
			yPosition = cm.imageHeight() - y;
		}
	
		AffineTransform affineTransform = new AffineTransform();
		
		affineTransform.rotate(Math.toRadians(rotation), bounds.width/2, -bounds.height/2);
		Font rotatedFont = g.getFont().deriveFont(affineTransform);
		g.setFont(rotatedFont);
		
		g.drawString(string, xPosition, yPosition);
//		g.drawRect(xPosition, yPosition - bounds.height, bounds.width, bounds.height);
		
		affineTransform.rotate(Math.toRadians(-rotation), bounds.width/2, -bounds.height/2);
		Font regularFont = g.getFont().deriveFont(affineTransform);
		g.setFont(regularFont);
		
		g.setTransform(orig);
		

	}
	
	public static Shape getShapeOfText(Font font, String msg) {
        BufferedImage bi = new BufferedImage(
                1, 1, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bi.createGraphics();

        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, msg);

        return gv.getOutline();
    }
}
