package thesis.DataFrame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

import thesis.Common.CommonMath;


public class DataItem {

	public enum StorageType { 
		String, 
		Integer, 
		Double, 
		Null, 
		Boolean, 
		LocalDate,
		LocalTime,
		LocalDateTime, 
		Period, 
		Duration 
	};
	
	private StorageType type;

	private String stringValue;
	private Integer intValue;
	private Double doubleValue;
	private Boolean booleanValue;
	private LocalDate localDateValue;
	private LocalTime localTimeValue;
	private LocalDateTime localDateTimeValue;
	private Period periodValue;
	private Duration durationValue;

	public DataItem() {
		this.type = StorageType.Null;
	}

	public DataItem(Object value, StorageType type) {
		initialize(type, value);
	}

	public DataItem(Object value) {
		StorageType typeOfObject = null;
		if (value == null) {
			this.type = StorageType.Null;
			typeOfObject = StorageType.Null;
		} else if (value instanceof Integer) {
			typeOfObject = StorageType.Integer;
		} else if (value instanceof Double) {
			typeOfObject = StorageType.Double;
		} else if (value instanceof Boolean) {
			typeOfObject = StorageType.Boolean;
		} else if (value instanceof LocalDate) {
			typeOfObject = StorageType.LocalDate;
		} else if (value instanceof LocalTime) {
			typeOfObject = StorageType.LocalTime;
		} else if (value instanceof LocalDateTime) {
			typeOfObject = StorageType.LocalDateTime;
		} else if (value instanceof Period) {
			typeOfObject = StorageType.Period;
		} else if (value instanceof Duration) {
			typeOfObject = StorageType.Duration;
		} else {
			typeOfObject = StorageType.String;
		}
		
		initialize(typeOfObject, value);
	}

	
	public DataItem(DataItem item) {
		replicateProperties(item);
	}
	
	// String Value
	public DataItem(String value) {
		this.stringValue = value;
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
	
	// Boolean Value
	public DataItem(Boolean value) {
		this.booleanValue = value;
		this.type = StorageType.Boolean;
	}
	
	public DataItem(boolean value) {
		this.booleanValue = value;
		this.type = StorageType.Boolean;
	}
	
	// Date Value
	public DataItem(LocalDate value) {
		if (value != null) {
			this.localDateValue = value;
			this.type = StorageType.LocalDate;
		} else {
			this.type = StorageType.Null;
		}
	}
	
	// Time Value
	public DataItem(LocalTime value) {
		if (value != null) {
			this.localTimeValue = value;
			this.type = StorageType.LocalTime;
		} else {
			this.type = StorageType.Null;
		}
	}
	
	// DateTime Value
	public DataItem(LocalDateTime value) {
		if (value != null) {
			this.localDateTimeValue = value;
			this.type = StorageType.LocalDateTime;
		} else {
			this.type = StorageType.Null;
		}
	}

	// DateTime Value
	public DataItem(Period value) {
		if (value != null) {
			this.periodValue = value;
			this.type = StorageType.Period;
		} else {
			this.type = StorageType.Null;
		}
	}
	
	// DateTime Value
	public DataItem(Duration value) {
		if (value != null) {
			this.durationValue = value;
			this.type = StorageType.Duration;
		} else {
			this.type = StorageType.Null;
		}
	}
	

	private void replicateProperties(DataItem item) {
		this.type = item.getType();
		if (this.type == StorageType.Integer) {			
			this.intValue = item.getIntegerValue().intValue();
		} else if (this.type == StorageType.Double) {			
			this.doubleValue = item.getDoubleValue();
		} else if (this.type == StorageType.String) {
			this.stringValue = item.getStringValue();
		} else if (this.type == StorageType.Boolean) {
			this.booleanValue = item.getBooleanValue().booleanValue();
		} else if (this.type == StorageType.LocalDate) {
			this.localDateValue = item.getDateValue();
		} else if (this.type == StorageType.LocalTime) {
			this.localTimeValue = item.getTimeValue();
		} else if (this.type == StorageType.LocalDateTime) {
			this.localDateTimeValue = item.getDateTimeValue();
		} else if (this.type == StorageType.Period) {
			this.periodValue = item.getPeriodValue();
		} else if (this.type == StorageType.Duration) {
			this.durationValue = item.getDurationValue();
		}
	}
	
	private void initialize(StorageType typeToUse, Object value) {
		this.type = typeToUse;
		if (this.type == StorageType.String) {
			this.stringValue = value.toString();
		} else if (this.type == StorageType.Integer) {
			this.intValue = Integer.valueOf(value.toString());
		} else if (this.type == StorageType.Double) {
			this.doubleValue = Double.valueOf(value.toString());
		} else if (this.type == StorageType.Boolean) {
			this.booleanValue = parseBoolean(value.toString());
		} else if (this.type == StorageType.LocalDate) {
			this.localDateValue = LocalDate.parse(value.toString());
		} else if (this.type == StorageType.LocalTime) {
			this.localTimeValue = LocalTime.parse(value.toString());
		} else if (this.type == StorageType.LocalDateTime) {
			this.localDateTimeValue = LocalDateTime.parse(value.toString());
		} else if (this.type == StorageType.Period) {
			this.periodValue = Period.parse(value.toString());
		} else if (this.type == StorageType.Duration) {
			this.durationValue = Duration.parse(value.toString());
		} else if (this.type == StorageType.Null) {
		
		} else {
			System.out.println("You have entered an incompatible type: " + value.getClass());
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void setType(StorageType typeToUse) {
		
		if (this.type == typeToUse) {
			return;
		}
		
		if (this.type == StorageType.String) {
			// Current type is String
			switch (typeToUse) {
				case Integer:
					// Convert String to Integer
					this.intValue = Integer.parseInt(this.stringValue);
					this.stringValue = null;
					break;
				case Double:
					// Convert String to Double
					this.doubleValue = Double.parseDouble(this.stringValue);
					this.stringValue = null;
					break;
				case Boolean:
					// Convert String to Boolean
					this.booleanValue = parseBoolean(this.stringValue);
					this.stringValue = null;
					break;
				case LocalDate:
					// Convert String to LocalDate
					this.localDateValue = LocalDate.parse(this.stringValue);
					this.stringValue = null;
					break;
				case LocalTime:
					// Convert String to LocalTime
					this.localTimeValue = LocalTime.parse(this.stringValue);
					this.stringValue = null;
					break;
				case LocalDateTime:
					// Convert String to LocalDateTime
					this.localDateTimeValue = LocalDateTime.parse(this.stringValue);
					this.stringValue = null;
					break;
				case Period:
					// Convert String to Period
					this.periodValue = Period.parse(this.stringValue);
					this.stringValue = null;
					break;
				case Duration:
					// Convert String to LocalDateTime
					this.durationValue = Duration.parse(this.stringValue);
					this.stringValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
			
		} else if (this.type == StorageType.Integer) {
			// Current type is Integer
			switch(typeToUse) {
				case String:
					// Convert from Integer to String
					this.stringValue = this.intValue.toString();
					this.intValue = null;
					break;
				case Double:
					// Convert from Integer to Double
					this.doubleValue = this.intValue.doubleValue();
					this.intValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.Double) {
			// Current type is Double
			switch(typeToUse) {
				case String:
					// Convert from Double to String
					this.stringValue = this.doubleValue.toString();
					this.doubleValue = null;
					break;
				case Integer:
					// Convert from Double to Integer
					this.intValue = this.doubleValue.intValue();
					this.doubleValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.Boolean) {
			// Current type is Boolean
			switch(typeToUse) {
				case Integer:
					// Convert from Boolean to Integer
					this.intValue = this.booleanValue ? 1 : 0;
					this.booleanValue = null;
					break;
				case Double:
					// Convert from Boolean to Double
					this.doubleValue = this.booleanValue ? 1.0 : 0.0;
					this.booleanValue = null;
					break;
				case String:
					// Convert from Boolean to String
					this.stringValue = this.booleanValue.toString();
					this.booleanValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.LocalDate) {
			// Current type is LocalDate
			switch(typeToUse) {
				case String:
					// Convert LocalDate to String
					this.stringValue = this.localDateValue.toString();
					this.localDateValue = null;
					break;
				case LocalDateTime:
					// Convert LocalDate to LocalDateTime
					this.localDateTimeValue = this.localDateValue.atStartOfDay();
					this.localDateValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.LocalTime) {
			// Current type is LocalTime
			switch(typeToUse) {
				case String:
					// Convert LocalTime to String
					this.stringValue = this.localTimeValue.toString();
					this.localTimeValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.LocalDateTime) {
			// Current type is LocalDateTime
			switch(typeToUse) {
				case String:
					// Convert LocalDateTime to String
					this.stringValue = this.localDateTimeValue.toString();
					this.localDateTimeValue = null;
					break;
				case LocalDate:
					// Convert LocalDateTime to LocalDate
					this.localDateValue = this.localDateTimeValue.toLocalDate();
					this.localDateTimeValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.Period) {
			// Current type is Period
			switch(typeToUse) {
				case String:
					// Convert from Period to String;
					this.stringValue = this.periodValue.toString();
					this.periodValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.Duration) {
			// Current type is Duration
			switch(typeToUse) {
				case String:
					// Convert from Duration to String;
					this.stringValue = this.durationValue.toString();
					this.durationValue = null;
					break;
				default:
					System.out.println("Can't conver from " + this.type + " to " + typeToUse);
			}
		} else if (this.type == StorageType.Null) {
			this.stringValue = null;
			this.intValue = null;
			this.doubleValue = null;
			this.booleanValue = null;
			this.localDateValue = null;
			this.localTimeValue = null;
			this.localDateTimeValue = null;
			this.periodValue = null;
			this.durationValue = null;
		}
		this.type = typeToUse;

	}

	public StorageType getType() {
		return this.type;
	}

	public Object getObjectValue() {
		if (this.type == StorageType.String) {
			return this.stringValue;
		} else if (this.type == StorageType.Integer) {
			return this.intValue;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue;
		} else if (this.type == StorageType.Boolean) {
			return this.booleanValue;
		} else if (this.type == StorageType.LocalDate) {
			return this.localDateValue;
		} else if (this.type == StorageType.LocalTime) {
			return this.localTimeValue;
		} else if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue;
		} else if (this.type == StorageType.Period) {
			return this.periodValue;
		} else if (this.type == StorageType.Duration) {
			return this.durationValue;
		} else if (this.type == StorageType.Null) {
			return "null";
		}
		return null;
	}

	private Boolean parseBoolean(String str) {
		return (str.equals("True") || str.equals("true"));
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
	
	public static double[] convertToPrimitiveDoubleList(DataItem[] dataItemList) {
		double[] doubleList = new double[dataItemList.length];
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
	
	public String getStringValue() {
		return this.stringValue;
	}

	public Integer getIntegerValue() {
		return this.intValue;
	}

	public double getDoubleValue() {
		return this.doubleValue;
	}

	public Boolean getBooleanValue() {
		return this.booleanValue;
	}
	
	public LocalDate getDateValue() {
		return this.localDateValue;
	}
	
	public LocalTime getTimeValue() {
		return this.localTimeValue;
	}
	
	public LocalDateTime getDateTimeValue() {
		return this.localDateTimeValue;
	}

	public Period getPeriodValue() {
		return this.periodValue;
	}
	
	public Duration getDurationValue() {
		return this.durationValue;
	}
	
	public String getValueConvertedToString() {
		return getObjectValue().toString();
	}

	public boolean isNumber() {
		return this.type == StorageType.Integer || this.type == StorageType.Double;
	}
	
	public Double getValueConvertedToDouble() {
		if (this.type == StorageType.Integer) {
			return this.intValue.doubleValue();
		} else if (this.type == StorageType.Double) {
			return this.doubleValue.doubleValue();
		}
		return null;
	}
	
	public Integer getValueConvertedToInt() {
		if (this.type == StorageType.Integer) {
			return this.intValue;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue.intValue();
		}
		
		return null;
	
	}
	
	public Boolean getValueConvertedToBoolean() {
		if (this.type == StorageType.Boolean) {
			return this.booleanValue;
		} else if (this.type == StorageType.String) {
			return parseBoolean(this.stringValue);
		}
		
		return null;
	}

	public Number getValueConvertedToNumber() {
		if (this.type == StorageType.Integer) {
			return this.intValue;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue;
		} else
			return null;
	}
	
	public void add(int value) {
		if (this.type == StorageType.Integer) {
			this.intValue += value;
		} else if (this.type == StorageType.Double) {
			this.doubleValue += value;
		}
	}
	
	public void add(Period timePeriod) {
		if (this.type == StorageType.LocalDate) {
			this.localDateValue = this.localDateValue.plus(timePeriod);
		} else if (this.type == StorageType.LocalDateTime) {
			this.localDateTimeValue = this.localDateTimeValue.plus(timePeriod);
		} else if (this.type == StorageType.LocalTime) {
			this.localTimeValue = this.localTimeValue.plus(timePeriod);
		}
	}
	
	public void add(Duration timePeriod) {
		if (this.type == StorageType.LocalDate) {
			this.localDateValue = this.localDateValue.plus(timePeriod);
		} else if (this.type == StorageType.LocalDate) {
			this.localDateTimeValue = this.localDateTimeValue.plus(timePeriod);
		} else if (this.type == StorageType.LocalTime) {
			this.localTimeValue = this.localTimeValue.plus(timePeriod);
		}
	}
	
	public void add(double value) {
		if (this.type == StorageType.Integer) {
			this.doubleValue = (double) (this.intValue + value);
			this.intValue = 0;
			this.type = StorageType.Double;
		} else if (this.type == StorageType.Double) {
			this.doubleValue += value;
		}
	}

	public void add(float value) {
		add((double) value);
	}
	
	public void add(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			add(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			add(value.getDoubleValue());
		} else if (value.getType() == StorageType.Period) {
			add(value.getPeriodValue());
		} else if (value.getType() == StorageType.Duration) {
			add(value.getDurationValue());
		}
	}

	public void subtract(int value) {
		if (this.type == StorageType.Integer) {
			this.intValue -= value;
		} else if (this.type == StorageType.Double) {
			this.doubleValue -= value;
		}
	}
	
	public void subtract(double value) {
		if (this.type == StorageType.Integer) {
			this.doubleValue = (double) (this.intValue - value);
			this.intValue = 0;
			this.type = StorageType.Double;
		} else if (this.type == StorageType.Double) {
			this.doubleValue -= value;
		}
	}

	public void subtract(float value) {
		subtract((double) value);
	}
	
	public void subtract(Period timePeriod) {
		if (this.type == StorageType.LocalDate) {
			this.localDateValue = this.localDateValue.minus(timePeriod);
		} else if (this.type == StorageType.LocalDateTime) {
			this.localDateTimeValue = this.localDateTimeValue.minus(timePeriod);
		} else if (this.type == StorageType.LocalTime) {
			this.localTimeValue = this.localTimeValue.minus(timePeriod);
		}
	}
	
	public void subtract(Duration timePeriod) {
		if (this.type == StorageType.LocalDate) {
			this.localDateValue = this.localDateValue.minus(timePeriod);
		} else if (this.type == StorageType.LocalDateTime) {
			this.localDateTimeValue = this.localDateTimeValue.minus(timePeriod);
		} else if (this.type == StorageType.LocalTime) {
			this.localTimeValue = this.localTimeValue.minus(timePeriod);
		}
	}
	
	public void subtract(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			subtract(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			subtract(value.getDoubleValue());
		} else if (value.getType() == StorageType.Period) {
			subtract(value.getPeriodValue());
		} else if (value.getType() == StorageType.Duration) {
			subtract(value.getDurationValue());
		}
	}
	
	public void multiply(int value) {
		if (this.type == StorageType.Integer) {
			this.intValue *= value;
		} else if (this.type == StorageType.Double) {
			this.doubleValue *= value;
		}
	}
	
	public void multiply(double value) {
		if (this.type == StorageType.Integer) {
			this.doubleValue = (double) (this.intValue * value);
			this.intValue = 0;
			this.type = StorageType.Double;
		} else if (this.type == StorageType.Double) {
			this.doubleValue *= value;
		}
	}

	public void multiply(float value) {
		multiply((double) value);
	}
	
	public void multiply(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			multiply(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			multiply(value.getDoubleValue());
		}
	}
	
	
	public void divide(int value) {
		if (this.type == StorageType.Integer) {
			this.intValue /= value;
		} else if (this.type == StorageType.Double) {
			this.doubleValue /= value;
		}
	}
	
	public void divide(double value) {
		if (this.type == StorageType.Integer) {
			this.doubleValue = (double) (this.intValue / value);
			this.intValue = 0;
			this.type = StorageType.Double;
		} else if (this.type == StorageType.Double) {
			this.doubleValue /= value;
		}
	}

	public void divide(float value) {
		divide((double) value);
	}
	
	public void divide(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			divide(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			divide(value.getDoubleValue());
		}
	}
	
	public int mod(int value, int modulo) {
		return value % modulo;
	}
	public void mod(int modulo) {
		if (this.type == StorageType.Integer) {
			this.intValue = mod(this.intValue, modulo);
		} else if (this.type == StorageType.Double) {
			this.intValue = this.doubleValue.intValue();
		}
	}
	
	public void mod(DataItem modulo) {
		mod(modulo.getValueConvertedToInt());
	}
	
	public void power(int value) {
		if (this.type == StorageType.Integer) {
			this.intValue = (int)Math.pow(this.intValue, value);
		} else if (this.type == StorageType.Double) {
			this.doubleValue = Math.pow(this.getDoubleValue(), value);
		}
	}
	
	public void power(double value) {
		if (this.type == StorageType.Integer) {
			this.doubleValue = Math.pow((double)this.intValue, value);
			this.intValue = 0;
			this.type = StorageType.Double;
		} else if (this.type == StorageType.Double) {
			this.doubleValue = Math.pow(this.doubleValue, value);
		}
	}

	public void power(float value) {
		power((double) value);
	}
	
	public void power(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			power(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			power(value.getDoubleValue());
		}
	}
	
	public void intFloor() {
		if (this.type == StorageType.Double) {
			this.intValue = this.doubleValue.intValue();
			this.doubleValue = 0.0;
			this.type = StorageType.Integer;
		}
	}
	
	public void doubleFloor() {
		if (this.type == StorageType.Integer) {
			this.doubleValue = this.intValue.doubleValue();
			this.intValue = 0;
			this.type = StorageType.Double;
		} else if (this.type == StorageType.Double) {
			this.doubleValue = Math.floor(this.doubleValue);
		}
	}
	
	public void intCeiling() {
		if (this.type == StorageType.Double) {
			this.intValue = (int) Math.ceil(this.doubleValue);
			this.doubleValue = 0.0;
			this.type = StorageType.Integer;
		}
	}
	
	public void doubleCeiling() {
		if (this.type == StorageType.Integer) {
			this.doubleValue = this.intValue.doubleValue();
			this.intValue = 0;
			this.type = StorageType.Double;
		} else if (this.type == StorageType.Double) {
			this.doubleValue = Math.ceil(this.doubleValue);
		}
	}
	
	public boolean lessThan(int value) {
		return lessThan((double) value);
	}
	
	public boolean lessThan(double value) {
		if (this.type == StorageType.Integer) {
			return this.intValue < value;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue < value;
		}
		return false;
	}

	public boolean lessThan(float value) {
		return lessThan((double) value);
	}
	
	public boolean lessThan(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			return lessThan(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			return lessThan(value.getDoubleValue());
		}
		return false;
	}
	
	public boolean before(LocalDate date) {
		if (this.type == StorageType.LocalDate) {
			return this.localDateValue.isBefore(date);
		} else if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.isBefore(date.atStartOfDay());
		}
		return false;
	}
	
	public boolean before(LocalDateTime date) {
		if (this.type == StorageType.LocalDate) {
			return this.localDateValue.isBefore(date.toLocalDate());
		} else if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.isBefore(date);
		}
		return false;
	}
	
	public boolean before(LocalTime time) {
		if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.toLocalTime().isBefore(time);
		} else if (this.type == StorageType.LocalTime) {
			return this.localTimeValue.isBefore(time);
		}
		return false;
	}
	
	public boolean equal(int value) {
		return equal((double) value);
	}
	
	public boolean equal(double value) {
		if (this.type == StorageType.Integer) {
			return this.intValue == value;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue == value;
		}
		return false;
	}

	public boolean equal(float value) {
		return equal((double) value);
	}
	
	public boolean equal(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			return equal(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			return equal(value.getDoubleValue());
		}
		return false;
	}
	
	public boolean sameDate(LocalDate date) {
		if (this.type == StorageType.LocalDate) {
			return this.localDateValue.equals(date);
		} else if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.toLocalDate().equals(date);
		}
		return false;
	}
	
	public boolean sameDate(LocalDateTime date) {
		if (this.type == StorageType.LocalDate) {
			return this.localDateValue.equals(date.toLocalDate());
		} else if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.equals(date);
		}
		return false;
	}
	
	public boolean sameTime(LocalTime time) {
		if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.toLocalTime().equals(time);
		} else if (this.type == StorageType.LocalTime) {
			return this.localTimeValue.equals(time);
		}
		return false;
	}
	
	public boolean greaterThan(int value) {
		return greaterThan((double) value);
	}
	
	public boolean greaterThan(double value) {
		if (this.type == StorageType.Integer) {
			return this.intValue > value;
		} else if (this.type == StorageType.Double) {
			return this.doubleValue > value;
		}
		return false;
	}

	public boolean greaterThan(float value) {
		return greaterThan((double) value);
	}
	
	public boolean greaterThan(DataItem value) {
		if (value.getType() == StorageType.Integer) {
			return greaterThan(value.getIntegerValue());
		} else if (value.getType() == StorageType.Double) {
			return greaterThan(value.getDoubleValue());
		}
		return false;
	}
	
	public boolean after(LocalDate date) {
		if (this.type == StorageType.LocalDate) {
			return this.localDateValue.isAfter(date);
		} else if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.isAfter(date.atStartOfDay());
		}
		return false;
	}

	public boolean after(LocalTime time) {
		if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.toLocalTime().isAfter(time);
		} else if (this.type == StorageType.LocalTime) {
			return this.localTimeValue.isAfter(time);
		}
		return false;
	}
	
	public boolean after(LocalDateTime date) {
		if (this.type == StorageType.LocalDate) {
			return this.localDateValue.isAfter(date.toLocalDate());
		} else if (this.type == StorageType.LocalDateTime) {
			return this.localDateTimeValue.isAfter(date);
		}
		return false;
	}
	
	public void flip() {
		if (this.type == StorageType.Boolean) {
			this.booleanValue = !this.booleanValue;
		}
	}
	
	public void clamp(int lowerBound, int upperBound) {
		if (this.type == StorageType.Integer) {
			this.intValue = CommonMath.clamp(this.intValue, lowerBound, upperBound);
		} else if (this.type == StorageType.Double) {
			this.doubleValue = CommonMath.clamp(this.doubleValue, (double) lowerBound, (double) upperBound);
		}
	}
	
	public void clamp(double lowerBound, double upperBound) {
		if (this.type == StorageType.Integer) {
			this.intValue = CommonMath.clamp(this.intValue, (int)lowerBound, (int)upperBound);
		} else if (this.type == StorageType.Double) {
			this.doubleValue = CommonMath.clamp(this.doubleValue,lowerBound, upperBound);
		}
	}
	
	public void clamp(LocalDate lowerBound, LocalDate upperBound) {
		if (this.type == StorageType.LocalDate) {
			this.localDateValue = CommonMath.clamp(this.localDateValue, lowerBound, upperBound);
		} else if (this.type == StorageType.LocalDateTime) {
			this.localDateTimeValue = CommonMath.clamp(this.localDateTimeValue, lowerBound.atStartOfDay(), upperBound.atStartOfDay());
		}
	}
	
	public void clamp(LocalDateTime lowerBound, LocalDateTime upperBound) {
		if (this.type == StorageType.LocalDate) {
			this.localDateValue = CommonMath.clamp(this.localDateValue, lowerBound.toLocalDate(), upperBound.toLocalDate());
		} else if (this.type == StorageType.LocalDateTime) {
			this.localDateTimeValue = CommonMath.clamp(this.localDateTimeValue, lowerBound, upperBound);
		}
	}
	
	public void clamp(LocalTime lowerBound, LocalTime upperBound) {
		if (this.type == StorageType.LocalTime) {
			this.localTimeValue = CommonMath.clamp(this.localTimeValue, lowerBound, upperBound);
		}
	}
	
	public void round(int decimalPlaces) {
		if (this.type == StorageType.Double) {
			BigDecimal bd = BigDecimal.valueOf(this.doubleValue);
		    bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
		    this.doubleValue = bd.doubleValue();
		}
	}
	

	public void squareRoot() {
		if (this.type == StorageType.Double) {
			this.doubleValue = Math.sqrt(this.doubleValue);
		} else if (this.type == StorageType.Integer) {
			this.doubleValue = Math.sqrt(this.intValue);
			this.type = StorageType.Double;
			this.intValue = 0;
		}
	}
	
	@Override
	public String toString() {
		return getValueConvertedToString();
	}
	
	@Override
	public DataItem clone() {
		DataItem newDataItem;
		
		if (this.type == StorageType.String) {
			newDataItem = new DataItem(this.stringValue.toString());
		} else if (this.type == StorageType.Integer) {
			newDataItem = new DataItem(this.intValue.intValue());
		} else if (this.type == StorageType.Double) {
			newDataItem = new DataItem(this.doubleValue.doubleValue());
		} else if (this.type == StorageType.Boolean) {
			newDataItem = new DataItem(this.booleanValue.booleanValue());
		} else if (this.type == StorageType.LocalDate) {
			newDataItem = new DataItem(this.localDateValue);
		} else if (this.type == StorageType.LocalDateTime) {
			newDataItem = new DataItem(this.localDateTimeValue);
		} else if (this.type == StorageType.Period) {
			newDataItem = new DataItem(this.periodValue);
		} else if (this.type == StorageType.Duration) {
			newDataItem = new DataItem(this.durationValue);
		} else {
			newDataItem = new DataItem();
		}
		
		return newDataItem;
	}
	@Override
	public boolean equals(Object otherItem) {
		if (otherItem instanceof DataItem) {
			DataItem formatted = (DataItem)otherItem;
			if (this.getType() == formatted.getType()) {
				if (this.getType() == StorageType.Integer) {
					return this.intValue.intValue() == formatted.intValue.intValue();
				} else if (this.getType() == StorageType.Double) {
					return this.doubleValue.doubleValue() == formatted.doubleValue.doubleValue();
				} else if (this.getType() == StorageType.Boolean) {
					return this.booleanValue.booleanValue() == formatted.booleanValue.booleanValue();
				} else if (this.getType() == StorageType.String) {
					return this.stringValue.equals(formatted.stringValue);
				} else if (this.getType() == StorageType.LocalDate) {
					return this.sameDate(formatted.localDateValue);
				} else if (this.getType() == StorageType.LocalDateTime) {
					return this.sameDate(formatted.localDateTimeValue);
				} else if (this.getType() == StorageType.LocalTime) {
					return this.sameTime(formatted.localTimeValue);
				} else if (this.getType() == StorageType.Period) {
					return this.periodValue.equals(formatted.periodValue);
				} else if (this.getType() == StorageType.Duration) {
					return this.durationValue.equals(formatted.durationValue);
				} else if (this.getType() == StorageType.Null) {
					return true;
				}
			}
		}
		return false;
	}

}
