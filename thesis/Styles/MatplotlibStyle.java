package thesis.Styles;

import java.awt.Color;
import java.awt.Image;

public class MatplotlibStyle extends Style{

	private static final boolean drawBottomPlotOutline = true; 
	private static final boolean drawLeftPlotOutline = true;
	private static final boolean drawTopPlotOutline = true;
	private static final boolean drawRightPlotOutline = true;
	
	private static final Color bottomPlotOutlineColor = Color.BLACK;
	private static final Color leftPlotOutlineColor = Color.BLACK;
	private static final Color topPlotOutlineColor = Color.BLACK;
	private static final Color rightPlotOutlineColor = Color.BLACK;
	
	private static final int bottomPlotOutlineWidth = 2;
	private static final int leftPlotOutlineWidth = 2;
	private static final int topPlotOutlineWidth = 2;
	private static final int rightPlotOutlineWidth = 2;
	
	private static final Color plotBackgroundColor = Color.WHITE;
	private static final Image plotBackgroundImage = null;
	
	@Override
	public boolean getDrawBottomPlotOutline() {
		return drawBottomPlotOutline;
	}

	@Override
	public boolean getDrawLeftPlotOutline() {
		return drawLeftPlotOutline;
	}

	@Override
	public boolean getDrawTopPlotOutline() {
		return drawTopPlotOutline;
	}

	@Override
	public boolean getDrawRightPlotOutline() {
		return drawRightPlotOutline;
	}

	@Override
	public Color getBottomPlotOutlineColor() {
		return bottomPlotOutlineColor;
	}

	@Override
	public Color getLeftPlotOutlineColor() {
		return leftPlotOutlineColor;
	}

	@Override
	public Color getTopPlotOutlineColor() {
		return topPlotOutlineColor;
	}

	@Override
	public Color getRightPlotOutlineColor() {
		return rightPlotOutlineColor;
	}

	@Override
	public int getBottomPlotOutlineWidth() {
		return bottomPlotOutlineWidth;
	}

	@Override
	public int getLeftPlotOutlineWidth() {
		return leftPlotOutlineWidth;
	}

	@Override
	public int getTopPlotOutlineWidth() {
		return topPlotOutlineWidth;
	}

	@Override
	public int getRightPlotOutlineWidth() {
		return rightPlotOutlineWidth;
	}

	@Override
	public Color getPlotBackgroundColor() {
		return plotBackgroundColor;
	}

	@Override
	public Image getPlotBackgroundImage() {
		return plotBackgroundImage;
	}

}
