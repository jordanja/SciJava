package thesis.DataFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import thesis.Common.CommonArray;
import thesis.DataFrame.DataFrame.DataFrameIterator;

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
	
	public String[] getKeysAsStrings() {
		return groups.keySet().toArray(new String[0]);
	}
	
	public double[] getKeysAsDoubles() {
		return groups.keySet().stream().mapToDouble(i -> Double.parseDouble(i)).toArray();
	}
	
	public DataFrame average() {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			avgs.put(key, newDF.averageInColumns());
		}
		
		return combine(avgs);
	}

	public DataFrame max() {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			System.out.println(newDF);
			avgs.put(key, newDF.maxInColumns());
		}
		
		return combine(avgs);
	}
	
	public DataFrame min() {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			avgs.put(key, newDF.minInColumns());
		}
		
		return combine(avgs);
	}
	
	public DataFrame mediun() {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			avgs.put(key, newDF.mediunInColumns());
		}
		
		return combine(avgs);
	}
	
	public DataFrame sum() {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			avgs.put(key, newDF.sumInColumns());
		}
		
		return combine(avgs);
	}

	public DataFrame product() {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			avgs.put(key, newDF.productInColumns());
		}
		
		return combine(avgs);
	}
	
	public DataFrame numUnique() {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropColumn(this.columnNameToSplitOn);
			avgs.put(key, newDF.numUniqueInColumns());
		}
		
		return combine(avgs);
	}
	
	public DataFrame variance(int dof) {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			avgs.put(key, newDF.varianceInColumns(dof));
		}
		
		return combine(avgs);
	}
	
	
	public DataFrame standardDeviation(int dof) {
		HashMap<String, DataFrame> avgs = new HashMap<String, DataFrame>();
		for (String key: groups.keySet()) {
			DataFrame newDF = groups.get(key).clone();
			newDF.dropNonNumberColumns();
			avgs.put(key, newDF.standardDeviationInColumns(dof));
		}
		
		return combine(avgs);
	}

	
	private DataFrame combine(HashMap<String, DataFrame> avgs) {
		String[] keys = groups.keySet().toArray(new String[0]);
		ArrayList<String> columnNames = new ArrayList<String>();
		columnNames.add(columnNameToSplitOn);
		String[] rowNames = CommonArray.generateIncreasingSequence(keys.length).toArray(new String[0]);
		
		DataFrame dfToReturn = new DataFrame(columnNames.toArray(new String[0]), rowNames);
		dfToReturn.setColumnValues(0, keys);
		DataFrame combinedAvgs = null;
		String[] groupByOrder = dfToReturn.getColumnAsStringArray(0);
		for (String element: groupByOrder) {
			if (combinedAvgs == null) {
				combinedAvgs = avgs.get(element);
			} else {
				combinedAvgs.joinBelow(avgs.get(element), true, true);
			}
		}
		combinedAvgs.resetRowNames();
		
		dfToReturn.joinToTheRight(combinedAvgs, true, true);
		return dfToReturn;
	}
	
	public HashMap<String, DataFrame> getGroups() {
		return groups;
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
