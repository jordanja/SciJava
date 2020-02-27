package thesis.DataFrame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import thesis.Helpers.TypeCheckers;

public class DataItem {
	private String strValue;
	private Boolean boolValue;
	private Integer intValue;
	private Long longValue;
	private Float floatValue;
	private Double doubleValue;
	private Date dateValue;

	
	public enum StorageType {String, Boolean, Integer, Long, Float, Double, Null, Date, Time, DateTime };
	StorageType type;
	
	public DataItem() {
		this.type = StorageType.Null;
	}
	
	public DataItem(Object value, StorageType type) {
		initialize(type, String.valueOf(value));
	}
	
	public DataItem(Object value) {
		String strValue = String.valueOf(value);
		
		StorageType typeOfObject = null;
		if (value == null) {
			type = StorageType.Null;
		} else {
			if (TypeCheckers.isInteger(strValue)) {
				typeOfObject = StorageType.Integer;
			} else if (TypeCheckers.isDouble(strValue)) {
				typeOfObject = StorageType.Double;
			} else {
				typeOfObject = StorageType.String;
			}
			
//			if (value.getClass().equals(String.class)) {
//				typeOfObject = StorageType.String;
//			} else if (value.getClass().equals(Boolean.class)) {
//				typeOfObject = StorageType.Boolean;
//			} else if (value.getClass().equals(Integer.class)) {
//				typeOfObject = StorageType.Integer;
//			} else if (value.getClass().equals(Long.class)) {
//				typeOfObject = StorageType.Long;
//			} else if (value.getClass().equals(Double.class)) {
//				typeOfObject = StorageType.Double;
//			}  else if (value.getClass().equals(Date.class)) {
//				typeOfObject = StorageType.Date;
//			} 
			
			initialize(typeOfObject, strValue);
		}
	}

	// String Value
	public DataItem(String value) {
		initialize(StorageType.String, value);
	}
	
	// Boolean Value
	public DataItem(Boolean value) {
//		initialize(StorageType.Boolean, value);
	}
	public DataItem(boolean value) {
//		initialize(StorageType.Boolean, value);
	}
	
	// Integer Value
	public DataItem(Integer value) {
//		initialize(StorageType.Integer, value);
	}
	public DataItem(int value) {
//		initialize(StorageType.Integer, value);
	}
	
	// Long Value
	public DataItem(Long value) {
//		initialize(StorageType.Long, value);
	}
	public DataItem(long value) {
//		initialize(StorageType.Long, value);
	}
	
	// Float Value
	public DataItem(Float value) {
//		initialize(StorageType.Float, value);
	}
	public DataItem(float value) {
//		initialize(StorageType.Float, value);
	}
	
	// Double Value
	public DataItem(Double value) {
//		initialize(StorageType.Double, value);
	}
	public DataItem(double value) {
//		initialize(StorageType.Double, value);
	}
	
	public DataItem(Date value) {
//		initialize(StorageType.Date, value);
	}
	
	public DataItem(StorageType typeToUse, Object value) {
//		initialize(typeToUse, value);
	}
	
	private void initialize(StorageType typeToUse, String value) {
		setType(typeToUse);
		if (this.type == StorageType.String) {
			this.strValue = (String)value;
		} else if (this.type == StorageType.Boolean) {
//			this.boolValue = (Boolean) value;
		} else if (this.type == StorageType.Integer) {
			this.intValue = Integer.parseInt(value);
		} else if (this.type == StorageType.Long) {
//			this.longValue = (Long) value;
		} else if (this.type == StorageType.Float) {
//			this.floatValue = (Float) value;
		} else if (this.type == StorageType.Double) {
			this.doubleValue = Double.parseDouble((value));
		} else if (this.type == StorageType.Date) {
			
			SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
			try {
				this.dateValue = parser.parse((String) value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		} else {
			System.out.println("You have entered an incompatible type: " + value.getClass());
		}
	}
	
	public static DataItem[] convertToDataItemList(Object[] values) {
		DataItem[] dataItems = new DataItem[values.length];
		for (int i = 0; i < values.length; i++) {
			dataItems[i] = new DataItem(values[i]);
		}
		return dataItems;
	}
	
	public void setType(StorageType typeToUse) {
		this.type = typeToUse;
	}
	
	public StorageType getType() {
		return this.type;
	}
	
	public Object getObjectValue() {
		
		if (this.type == StorageType.String) {
			return this.strValue;
		} else if (this.type == StorageType.Boolean) {
			return this.boolValue;
		} else if (this.type == StorageType.Integer) {
			return this.intValue;
		} else if (this.type == StorageType.Long) {
			return this.longValue;
		} else if (this.type == StorageType.Float) {
			return this.floatValue;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue;
		} else if (this.type == StorageType.Date) {
			return this.dateValue;
		}
		return null;
	}
	
	public String getStringValue() {
		return this.strValue;
	}
	public Boolean getBooleanValue() {
		return this.boolValue;
	}
	public Integer getIntegerValue() {
		return this.intValue;
	}
	public Long getLongValue() {
		return this.longValue;
	}
	public Float getFloatValue() {
		return this.floatValue;
	}
	public double getDoubleValue() {
		return this.doubleValue;
	}
	public Date getDateValue() {
		return this.dateValue;
	}
	
	public String getValueConvertedToString() {
		if (this.type == StorageType.Date) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String stringDate = formatter.format(this.dateValue);
			return stringDate;
		} else {			
			return String.valueOf(getObjectValue());
		}
	}
	
	
	@Override
	public String toString() {
		return getValueConvertedToString();
	}
	
	

	
}
