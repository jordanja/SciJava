package thesis.DataFrame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import thesis.Helpers.TypeCheckers;

public class DataItem {
	private String strValue;
	private Integer intValue;
	private Double doubleValue;
	private LocalDate dateValue;

	public enum StorageType {
		String, Integer, Double, Null, Date
	};

	StorageType type;

	public DataItem() {
		this.type = StorageType.Null;
	}

	public DataItem(Object value, StorageType type) {
		initialize(type, String.valueOf(value));
	}

	public DataItem(Object value) {
//		System.out.println(value.getClass());
//		String strValue = String.valueOf(value);

		StorageType typeOfObject = null;
		if (value == null) {
			type = StorageType.Null;
		} else if (value instanceof Integer) {
			typeOfObject = StorageType.Integer;
		} else if (value instanceof Double) {
			typeOfObject = StorageType.Double;
		} else if (value instanceof LocalDate) {
			typeOfObject = StorageType.Date;
		} else {
			typeOfObject = StorageType.String;
		}

		initialize(typeOfObject, value);
	}

	// String Value
	public DataItem(String value) {
		this.strValue = value;
		this.type = StorageType.String;
	}

	// Integer Value
	public DataItem(Integer value) {
		this.intValue = value;
		this.type = StorageType.Integer;
	}

	public DataItem(int value) {
		this.intValue = value;
		this.type = StorageType.Integer;
	}

	// Double Value
	public DataItem(Double value) {
		this.doubleValue = value;
		this.type = StorageType.Double;
	}

	public DataItem(double value) {
		this.doubleValue = value;
		this.type = StorageType.Double;
	}

	public DataItem(LocalDate value) {
		this.dateValue = value;
		this.type = StorageType.Date;
	}

	public DataItem(StorageType typeToUse, Object value) {
		initialize(typeToUse, value.toString());
	}

	private void initialize(StorageType typeToUse, Object value) {
		setType(typeToUse);
		if (this.type == StorageType.String) {
			this.strValue = value.toString();
		} else if (this.type == StorageType.Integer) {
			this.intValue = (Integer) value;
		} else if (this.type == StorageType.Double) {
			this.doubleValue = (Double) value;
		} else if (this.type == StorageType.Date) {
			this.dateValue = (LocalDate) value;

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

	public static Double[] convertToDoubleList(DataItem[] dataItemList) {
		Double[] doubleList = new Double[dataItemList.length];
		for (int i = 0; i < dataItemList.length; i++) {
			doubleList[i] = dataItemList[i].getValueConvertedToDouble();
		}
		return doubleList;
	}

	public static String[] convertToStringList(DataItem[] dataItemList) {
		String[] stringList = new String[dataItemList.length];
		for (int i = 0; i < dataItemList.length; i++) {
			stringList[i] = dataItemList[i].getValueConvertedToString();
		}
		return stringList;
	}

	public static Object[] convertToObjectList(DataItem[] dataItemList) {
		Object[] stringList = new String[dataItemList.length];
		for (int i = 0; i < dataItemList.length; i++) {
			stringList[i] = dataItemList[i].getObjectValue();
		}
		return stringList;
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
		} else if (this.type == StorageType.Integer) {
			return this.intValue;
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

	public Integer getIntegerValue() {
		return this.intValue;
	}

	public double getDoubleValue() {
		return this.doubleValue;
	}

	public LocalDate getDateValue() {
		return this.dateValue;
	}

	public String getValueConvertedToString() {
		return getObjectValue().toString();
	}

	public Double getValueConvertedToDouble() {
		try {
			return Double.parseDouble(String.valueOf(getObjectValue()));
		} catch (Exception e) {
			return null;
		}
	}

	public Number getValueConvertedToNumber() {
		if (this.type == StorageType.Integer) {
			return this.intValue;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue;
		} else
			return null;
	}

	@Override
	public String toString() {
		return getValueConvertedToString();
	}

}
