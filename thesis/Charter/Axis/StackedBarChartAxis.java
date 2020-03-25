package thesis.Charter.Axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import thesis.Charter.ChartMeasurements.XYChartMeasurements;
import thesis.Charter.StringDrawer.DrawString;
import thesis.Charter.StringDrawer.DrawString.xAlignment;
import thesis.Charter.StringDrawer.DrawString.yAlignment;
import thesis.Common.CommonArray;
import thesis.Common.CommonHashMap;
import thesis.Common.CommonMath;
import thesis.Common.NiceScale;
import thesis.DataFrame.DataItem;

public class StackedBarChartAxis extends XYOneCategoricalAxis {
	
	
	public void setXAxis(String[] xData) {
		this.xTicks = xData;
	}

	public void setYAxis() {		
		this.yTicks = new String[] {"0", "0.2", "0.4", "0.6", "0.8", "1"};
	}


}
