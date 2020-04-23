package thesis.Charter.StringDrawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import thesis.Charter.ChartMeasurements.ChartMeasurements;
import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;

public class DrawString {

	private static Font font = new Font("Dialog", Font.PLAIN, 20);
	private static Color color = Color.BLACK;
	private static float rotation = 0;
	private static xAlignment xTextAlignment = xAlignment.LeftAlign;
	private static yAlignment yTextAlignment = yAlignment.BottomAlign;

	public enum xAlignment {
		LeftAlign, CenterAlign, RightAlign
	};

	public enum yAlignment {
		TopAlign, MiddleAlign, BottomAlign
	};

	public static void write(Graphics2D g, String string, int x, int y) {
		AffineTransform orig = g.getTransform();
		int screenHeight = g.getDeviceConfiguration().getBounds().height;
		
		g.scale(1.0, -1.0);
		g.translate(0, -screenHeight);

		int xPosition = x;
		int yPosition = screenHeight - y;
		g.setFont(DrawString.font);
		Rectangle bounds = get2DBounds(string, g.getFont()).getBounds();

		if (DrawString.xTextAlignment == xAlignment.LeftAlign) {
			xPosition = x;
		} else if (DrawString.xTextAlignment == xAlignment.CenterAlign) {
			xPosition = x - bounds.width / 2;
		} else if (DrawString.xTextAlignment == xAlignment.RightAlign) {
			xPosition = x - bounds.width;
		}

		if (DrawString.yTextAlignment == yAlignment.TopAlign) {
			yPosition = screenHeight - y + bounds.height;
		} else if (DrawString.yTextAlignment == yAlignment.MiddleAlign) {
			yPosition = screenHeight - y + bounds.height / 2;
		} else if (DrawString.yTextAlignment == yAlignment.BottomAlign) {
			yPosition = screenHeight - y;
		}

		AffineTransform affineTransform = new AffineTransform();

		affineTransform.rotate(Math.toRadians(DrawString.rotation), bounds.width / 2, -bounds.height / 2);

		Font rotatedFont = g.getFont().deriveFont(affineTransform);
		g.setFont(rotatedFont);

		g.setColor(DrawString.color);
		g.drawString(string, xPosition, yPosition);
//		g.drawRect(xPosition, yPosition - bounds.height, bounds.width, bounds.height);

//		affineTransform.rotate(Math.toRadians(-DrawString.rotation), bounds.width / 2, -bounds.height / 2);
//		Font regularFont = g.getFont().deriveFont(affineTransform);
//		g.setFont(regularFont);

		g.setTransform(orig);

	}

	public static String formatDoubleForDisplay(Double num) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_DOWN);
		return df.format(num);
	}

	public static Shape get2DBounds(String msg, Font font, float rotation) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bi.createGraphics();

		FontRenderContext frc = g.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, msg);

		AffineTransform rotationTransform = AffineTransform.getRotateInstance(Math.toRadians(rotation), 0, 0);

		return rotationTransform.createTransformedShape(gv.getOutline()).getBounds2D();
	}

	public static Shape get2DBounds(String msg, Font font) {
		return get2DBounds(msg, font, 0);
	}

	public static int getStringHeight(String str, Font font, float rotation) {
		return get2DBounds(str, font, rotation).getBounds().height;
	}

	public static int getStringHeight(String str, Font font) {
		return getStringHeight(str, font, 0);
	}

	public static int getStringWidth(String str, Font font, float rotation) {
		return get2DBounds(str, font, rotation).getBounds().width;
	}

	public static int getStringWidth(String str, Font font) {
		return getStringWidth(str, font, 0);
	}

	public static int maxHeightOfStringInList(String[] arr, Font font, float rotation) {
		if (arr != null) {
			double maxHeight = Double.MIN_VALUE;

			for (String str : arr) {
				double strHeight = DrawString.getStringHeight(str, font, rotation);
				maxHeight = Double.max(strHeight, maxHeight);
			}
			return (int) Math.ceil(maxHeight);
		}
		return 0;
	}

	public static int maxWidthOfStringInList(String[] arr, Font font, float rotation) {
		if (arr != null) {

			double maxWidth = Double.MIN_VALUE;

			for (String str : arr) {
				double strWidth = DrawString.getStringWidth(str, font, rotation);
				maxWidth = Double.max(strWidth, maxWidth);
			}
			return (int) Math.ceil(maxWidth);
		}
		return 0;
	}

	public static Font getFont() {
		return DrawString.font;
	}

	public static void setFont(Font font) {
		DrawString.font = font;
	}

	public static void setFontSize(int fontSize) {
		DrawString.font = new Font(DrawString.font.getName(), DrawString.font.getStyle(), fontSize);
	}

	public static Color getColor() {
		return DrawString.color;
	}

	public static void setColor(Color color) {
		DrawString.color = color;
	}

	public static float getRotation() {
		return DrawString.rotation;
	}

	public static void setRotation(float rotation) {
		DrawString.rotation = rotation;
	}

	public static xAlignment getXTextAlignment() {
		return DrawString.xTextAlignment;
	}

	public static void setXTextAlignment(xAlignment xTextAlignment) {
		DrawString.xTextAlignment = xTextAlignment;
	}

	public static yAlignment getYTextAlignment() {
		return DrawString.yTextAlignment;
	}

	public static void setYTextAlignment(yAlignment yTextAlignment) {
		DrawString.yTextAlignment = yTextAlignment;
	}
	
	public static void setAlignment(xAlignment xTextAlignment, yAlignment yTextAlignment) {
		DrawString.xTextAlignment = xTextAlignment;
		DrawString.yTextAlignment = yTextAlignment;
	}
	
	public static void setTextStyle(Color textColor, Font font, float rotation) {
		DrawString.color = textColor;
		DrawString.font = font;
		DrawString.rotation = rotation;
	}
	
//	public static void setStringStyle(Color color, Font font, float rotation, xAlignment xAlign, yAlignment yAlign) {
//		
//	}
	

}
