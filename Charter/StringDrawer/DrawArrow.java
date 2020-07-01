package thesis.Charter.StringDrawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class DrawArrow {

	public static Color color = Color.BLACK;
	public static int lineWeight = 1;
	
	
	public static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
		int deltaX = x2 - x1;
		int deltaY = y2 - y1;
		
		double angle = Math.atan2(deltaY, deltaX);
		
		
		double arrowLength = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		
		double arrowPointLength = arrowLength / 6;
		
		double pointAngle1 = angle + Math.PI + Math.PI/4;
		double pointAngle2 = angle + Math.PI - Math.PI/4;
		
		int point1X = x2 + (int) (arrowPointLength * Math.cos(pointAngle1));
		int point1Y = y2 + (int) (arrowPointLength * Math.sin(pointAngle1));
		
		int point2X = x2 + (int) (arrowPointLength * Math.cos(pointAngle2));
		int point2Y = y2 + (int) (arrowPointLength * Math.sin(pointAngle2));

		
		g.setColor(DrawArrow.color);
		g.setStroke(new BasicStroke(DrawArrow.lineWeight));
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x2, y2, point1X, point1Y);
		g.drawLine(x2, y2, point2X, point2Y);
		
		
	}


	public static Color getColor() {
		return color;
	}


	public static void setColor(Color color) {
		DrawArrow.color = color;
	}


	public static int getLineWeight() {
		return lineWeight;
	}


	public static void setLineWeight(int lineWeight) {
		DrawArrow.lineWeight = lineWeight;
	}
	
}
