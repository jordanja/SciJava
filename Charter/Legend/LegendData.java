package thesis.Charter.Legend;

import java.awt.Color;
import java.util.HashMap;

import thesis.Common.CommonArray;

public class LegendData {
	/*
	 * 	[
	 *		{
	 *			"label": "color-label",
	 *			"type": "color",
	 *			"data": {
	 *				"label-1": Color.red,
	 *				"label-2": Color.blue,
	 *			}
	 *		},
	 *		{
	 *			"label": "size-label",
	 *			"type": "size",
	 *			"color": Color.blue,
	 *			"data": {
	 *				"smallest-value": radius,
	 *				"middle-value": radius,
	 *				"largest-value": radius,
	 *			}
	 *		}
	 * 	]
	 */
	private String colorLabel;
	private HashMap<String, Color> colorData;
	private String[] colorValueOrder;
	
	private String sizeLabel;
	private HashMap<String, Integer> sizeData;
	private Color sizePointsColor = Color.BLUE;
	
	
	public boolean includeColorInLegend() {
		return ((this.colorLabel != null) && (this.colorData != null));
	}
	public boolean includeSizeInLegend() {
		return ((this.sizeLabel != null) && (this.sizeData != null));
	}
	
	
	public String getColorLabel() {
		return this.colorLabel;
	}
	public void setColorLabel(String colorLabel) {
		this.colorLabel = colorLabel;
	}
	
	public HashMap<String, Color> getColorData() {
		return this.colorData;
	}
	public void setColorData(HashMap<String, Color> colorData) {
		this.colorData = colorData;
	}
	
	public String[] getColorDataLabels() {
		return this.colorValueOrder;
	}
	
	public void setColorData(String[] values, Color[] colors) {
		this.colorValueOrder = values;
		this.colorData = new HashMap<String, Color>();
		for (int valueCount = 0; valueCount < values.length; valueCount++) {
			this.colorData.put(values[valueCount], colors[valueCount % colors.length]);
		}
	}
	
	public String getSizeLabel() {
		return this.sizeLabel;
	}
	public void setSizeLabel(String sizeLabel) {
		this.sizeLabel = sizeLabel;
	}
	
	public HashMap<String, Integer> getSizeData() {
		return this.sizeData;
	}
	public void setSizeData(HashMap<String, Integer> sizeData) {
		this.sizeData = sizeData;
	}
	public void setSizeData(String[] values, Integer[] radii) {
		this.sizeData = new HashMap<String, Integer>();
		for (int valueCount = 0; valueCount < values.length; valueCount++) {
			this.sizeData.put(values[valueCount], radii[valueCount]);
		}
	}
	public void setSizeDataMaxMin(Double[] values, int minRadius, int maxRadius) {
		Double maxValue = CommonArray.maxValue(values);
		Double minValue = CommonArray.minValue(values);
		this.sizeData = new HashMap<String, Integer>();
		this.sizeData.put(minValue.toString(), minRadius);
		this.sizeData.put(maxValue.toString(), maxRadius);
	}
	
	public Color getSizePointsColor() {
		return this.sizePointsColor;
	}
	public void setSizePointsColor(Color sizePointsColor) {
		this.sizePointsColor = sizePointsColor;
	}
	
	
	
}
