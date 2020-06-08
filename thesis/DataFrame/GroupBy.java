package thesis.DataFrame;

import java.util.HashMap;

public class GroupBy {
	HashMap<String, DataFrame> groups;

	
	public GroupBy(DataFrame df, String columnName) {
		this.groups = new HashMap<String, DataFrame>();
		
		int columnIndex = df.getColumnNames().indexOf(columnName);
		String[] uniqueValuesInColumn = df.getUniqueValuesInColumnAsStringArray(columnIndex);
		for (String columnValue: uniqueValuesInColumn) {
			DataFrame newDF = df.getDataFrameWhereColumnValueEquals(columnIndex, columnValue);
			groups.put(columnValue, newDF);
		}

	}
	
	
	
	
	@Override
	public String toString() {
		String strToReturn = "";
		for (String key: groups.keySet()) {
			strToReturn += key + ":\n";
			strToReturn += groups.get(key).toString();
			strToReturn += "\n";
		}
		
		
		return strToReturn;
	}

}
