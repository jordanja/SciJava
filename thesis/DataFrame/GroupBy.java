package thesis.DataFrame;

import java.util.ArrayList;
import java.util.HashMap;

import thesis.Common.CommonArray;

public class GroupBy {
	HashMap<String, DataFrame> groups;
	String columnNameToSplitOn;
	
	public GroupBy(DataFrame df, String columnName) {
		this.columnNameToSplitOn = columnName;
		this.groups = new HashMap<String, DataFrame>();
		
		int columnIndex = df.getColumnNames().indexOf(columnName);
		String[] uniqueValuesInColumn = df.getUniqueValuesInColumnAsStringArray(columnIndex);
		for (String columnValue: uniqueValuesInColumn) {
			DataFrame newDF = df.getDataFrameWhereColumnValueEquals(columnIndex, columnValue);
			groups.put(columnValue, newDF);
		}

	}
	
	public DataFrame average() {
		
		String[] keys = groups.keySet().toArray(new String[0]);
		String[] rowNames = CommonArray.generateIncreasingSequence(keys.length).toArray(new String[0]);
		ArrayList<String> columnNames = new ArrayList<String>();
		columnNames.add(columnNameToSplitOn);

		DataFrame dfToReturn = new DataFrame(columnNames.toArray(new String[0]), rowNames);
		dfToReturn.setColumnValues(0, keys);
		
		
		for (int columnCount = 1; columnCount < groups.get(keys[0]).getNumCols(); columnCount++) {
			HashMap<String, Object> averagesForColumn = new HashMap<String, Object>();
			for (String key: groups.keySet()) {
				if (groups.get(key).columnAllNumbers(columnCount)) {
					String columnName = groups.get(key).getColumnNames().get(columnCount);
					if (!columnNames.contains(columnName)) {
						columnNames.add(columnName);
					}
					double[] column = (groups.get(key).getColumnAsDoubleArray(columnCount));
					double average = CommonArray.average(column);
					averagesForColumn.put(key, average);
					
				}
				
			}
			
			dfToReturn.appendColumn(columnNames.get(columnNames.size() - 1));
			for (String key: averagesForColumn.keySet()) {
				int rowIndex = dfToReturn.indexOfInColumn(0, key);
				dfToReturn.setValue(dfToReturn.getNumCols() - 1, rowIndex, averagesForColumn.get(key));
			}
			
			
		}
		
		
		
		
		
		
		
		
		return dfToReturn;
		
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
