package thesis.DataFrame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import thesis.Common.CommonArray;
import thesis.DataFrame.DataItem.StorageType;

public class DataFrame implements Iterable<ArrayList<DataItem>> {

	private ArrayList<ArrayList<DataItem>> data;
	private ArrayList<String> colNames;
	private ArrayList<String> rowNames;

	private String[][] printGrid;
	private int[] colToWriteNext;

	private int dataPadding = 1;
	private int indexPadding = 1;

	/*
	 * Create an empty DF object
	 */
	public DataFrame() {
		this.data = new ArrayList<ArrayList<DataItem>>();
		this.colNames = new ArrayList<String>();
		this.rowNames = new ArrayList<String>();
	}

	// Create an empty DF with rows and columns and null values
	public DataFrame(ArrayList<String> colNames, ArrayList<String> rowNames) {
		this();

		for (int rowCount = 0; rowCount < rowNames.size(); rowCount++) {
			ArrayList<Object> row = new ArrayList<Object>(colNames.size());
			for (int colCount = 0; colCount < colNames.size(); colCount++) {
				row.add(null);
			}
			appendRow(row);
		}
		String[] colNamesToAdd = CommonArray.mangle(colNames);
		String[] rowNamesToAdd = CommonArray.mangle(rowNames);
		this.colNames = CommonArray.convertStringArrayToArrayList(colNamesToAdd);
		this.rowNames = CommonArray.convertStringArrayToArrayList(rowNamesToAdd);
	}

	// Create an empty DF with rows and columns and null values
	public DataFrame(String[] colNames, String[] rowNames) {
		this(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames));
	}

	/*
	 * Create a DF object from a HashMap (cols) For example:
	 * map = {
	 * "one": [1, 2,3],
	 * "two": [3, 4, 5]
	 * }
	 * 
	 * Becomes: | one| two --+----+---- 0| 1| 3 1| 2| 4 2| 3| 5
	 */
	public DataFrame(HashMap<String, ArrayList<Object>> map) {
		this();

		appendColumns(map);

	}

	/*
	 * Create a DF from list of hashmaps (rows) For example: maps = [ map1: { "one":
	 * 1, "two": 2, "three": 3 }, map1: { "one": 10, "two": 20, "three": 30 }, ]
	 * 
	 * Becomes: | one| two| three --+----+----+------ 0| 1| 2| 3 1| 10| 20| 30
	 */
	public DataFrame(ArrayList<HashMap<String, Object>> maps) {
		this();
		for (HashMap<String, Object> map : maps) {
			appendRow(map);
		}

	}

	public DataFrame(DataFrame df) {
		this();
		this.colNames = df.getColumnNames();
		this.rowNames = df.getRowNames();
		this.data = df.getData();
	}

	/*
	 * Create a DF object with one list For example if: name = "hello", list =
	 * Arrays.asList(1, 2, 3); isRow = false
	 * 
	 * Result is: | hello --+------ 0| 1 1| 2 2| 3
	 * 
	 * For example if: name = "hello", list = Arrays.asList(1, 2, 3); isRow = true
	 * 
	 * Result is: | 0| 1| 2 ------+--+--+-- hello| 1| 2| 3
	 */
	public DataFrame(String name, ArrayList<Object> list, boolean isRow) {
		this();

		if (isRow == true) {
			appendRow(name, list);
		} else {
			appendColumn(name, list);
		}

	}

	/*
	 * Create a DF object from an array For example if: name = "hello" array = [1,
	 * 2, 3, 4, 5] isRow = false
	 * 
	 * Result is: | hello --+------ 0| 1 1| 2 2| 3 3| 4 4| 5
	 * 
	 * If isRow = true. Result is: | 0| 1| 2| 3| 4 ------+--+--+--+--+-- hello| 1|
	 * 2| 3| 4| 5
	 */
	public DataFrame(String name, Object[] array, boolean isRow) {
		this(name, new ArrayList<Object>(Arrays.asList(array)), isRow);
	}

	/*
	 * Create a DF object form multiple columns For example if: names = ["one",
	 * "two", "three"] lists = Arrays.asList( Arrays.asList(1, 2, 3),
	 * Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9), ), isRow = false
	 * 
	 * Result is: | one| two| three --+----+----+------ 0| 1| 4| 7 1| 2| 5| 8 2| 3|
	 * 6| 9
	 * 
	 * If isRow = true. Result is:
	 * | 0| 1| 2
	 * ------+--+--+--
	 * one| 1| 2| 3
	 * two| 4| 5| 6
	 * three| 7| 8| 9
	 */
	public DataFrame(ArrayList<String> names, ArrayList<ArrayList<Object>> lists, boolean isRow) {
		this();
		if (names.size() != lists.size()) {
			System.out.println("Number of names = " + names.size() + ", number of lists = " + lists.size());
			return;
		}

		for (int i = 0; i < names.size(); i++) {
			if (isRow == true) {
				appendRow(names.get(i), lists.get(i));
			} else {
				appendColumn(names.get(i), lists.get(i));
			}
		}

	}

	/*
	 * Create a DF object from a csv file
	 */
	public DataFrame(String filePath, boolean hasHeaderRow, boolean hasIndexRow) {
		this();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			int rowNum = 0;
			ArrayList<String> rowNames = new ArrayList<String>();

			while ((line = br.readLine()) != null) {
				String cleanLine = removeSpecialChars(line);
				String[] values = cleanLine.split(",");

				// If there are row names, extract the first value and add to list of rows
				if (hasIndexRow) {
					if (!((hasHeaderRow) && (rowNum == 0))) {
						rowNames.add(values[0]);
					}
					values = Arrays.copyOfRange(values, 1, values.length);
				}

				// Extract the header row
				if (rowNum == 0) {
					String[] mangledColumns = CommonArray.mangle(values);
					this.colNames = CommonArray.convertStringArrayToArrayList(mangledColumns);

				} else {
					if (values.length > 0) {
						appendRow(CommonArray.convertStringArrayToObjectArrayList(values));
					}

				}

				rowNum++;

			}

			// If there were row names specified, mangle and then set them
			if (hasIndexRow) {
				this.setRowNames(rowNames);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String removeSpecialChars(String line) {
		return line.replace("\"", "").replaceAll("[^a-zA-Z0-9, ./<>?;:\"'`!@#$%^&*()\\[\\]{}_+=|\\\\-]", "").trim();
	}

	// Rename rows
	public void setRowNames(ArrayList<String> rowNamesToAdd) {

		if (CommonArray.anyNullValues(rowNamesToAdd)) {
			System.out.println("Cannot have any null values in row names");
			return;
		}

		if (rowNamesToAdd.size() != this.rowNames.size()) {
			System.out.println("Number of row names (" + rowNamesToAdd.size() + ") must equal number of rows (" + this.rowNames.size() + ")");
			return;
		}

		String[] mangledRowNames = CommonArray.mangle(rowNamesToAdd);

		this.rowNames = CommonArray.convertStringArrayToArrayList(mangledRowNames);

	}

	public void setRowNames(String[] rowNamesToAdd) {
		setRowNames(new ArrayList<String>(Arrays.asList(rowNamesToAdd)));
	}
	
	public void setData(ArrayList<ArrayList<DataItem>> data) {
		this.data = data;
	}

	public void resetRowNames() {
		this.rowNames = CommonArray.generateIncreasingSequence(this.rowNames.size());
	}

	public void setColumnNames(ArrayList<String> colNamesToAdd) {
		if (CommonArray.anyNullValues(colNamesToAdd)) {
			System.out.println("Cannot have any null values in row names");
			return;
		}

		if (colNamesToAdd.size() != this.colNames.size()) {
			System.out.println("Number of column names must equal number of columns");
			return;
		}

		String[] mangledColNames = CommonArray.mangle(colNamesToAdd);
		this.colNames = CommonArray.convertStringArrayToArrayList(mangledColNames);
	}

	public void setColumnNames(String[] colNamesToUse) {
		setColumnNames(new ArrayList<String>(Arrays.asList(colNamesToUse)));

	}

	public void resetColumnNames() {
		this.colNames = CommonArray.generateIncreasingSequence(this.colNames.size());
	}

	private String generateUnusedColumnName() {
		String columnName = "";
		int possibleName = 1;
		while (columnName == "") {
			if (!this.colNames.contains(String.valueOf(possibleName))) {
				columnName = String.valueOf(possibleName);
			}
			possibleName++;
		}
		return columnName;
	}
	
	private String generateUnusedRowName() {
		String rowName = "";
		int possibleName = 1;
		while (rowName == "") {
			if (!this.rowNames.contains(String.valueOf(possibleName))) {
				rowName = String.valueOf(possibleName);
			}
			possibleName++;
		}
		return rowName;
	}
	
	public void insertColumn(int index, String columnName, ArrayList<Object> column) {
		if (index > this.colNames.size()) {
			System.out.println("Column index too high");
			return;
		}

		if (this.data.size() > 0) {
			if (data.get(0).size() != column.size()) {
				System.out.println("New column must have same number of rows as other columns");
				return;
			}
		} else {
			this.rowNames = CommonArray.generateIncreasingSequence(column.size());
		}
		this.data.add(index, convertObjectListToItemList(column));
		String newColumnName = CommonArray.getNewMangleName(this.colNames, columnName);
		this.colNames.add(index, newColumnName);

	}

	public void insertColumn(int index, String columnName, Object[] column) {
		
		insertColumn(index, columnName, new ArrayList<Object>(Arrays.asList(column)));
		
	}
	
	public void insertColumn(int index, ArrayList<Object> column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, Object[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumns(int index, HashMap<String, ArrayList<Object>> map) {
		int insertionOffset = 0;
		for (String columnName : map.keySet()) {
			this.insertColumn(index + insertionOffset, columnName, map.get(columnName));
			insertionOffset++;
		}
	}
	
	public void insertColumn(int index, String columnName, HashMap<String, Object> map) {
		ArrayList<Object> col = new ArrayList<Object>();
		for (String rowName : map.keySet()) {
			int rowIndex = this.rowNames.indexOf(rowName);
			if (rowIndex == -1) {
				this.colNames.add(rowName);

			}
			col.add(map.get(rowName));

		}
		insertColumn(index, columnName, col);
	}
	
	public void insertColumn(int index, HashMap<String, Object> map) {
		insertColumn(index, generateUnusedColumnName(), map);
	}
	
	public void insertColumns(int index, ArrayList<String> columnNames, ArrayList<ArrayList<Object>> columns) {
		int columnOffset = 0;
		for (ArrayList<Object> column : columns) {
			insertColumn(index + columnOffset, columnNames.get(columnOffset), column);
			columnOffset++;
		}
	}
	
	public void insertColumns(int index, String[] columnNames, Object[][] columns) {
		int columnOffset = 0;
		for (Object[] column : columns) {
			insertColumn(index + columnOffset, columnNames[columnOffset], column);
			columnOffset++;
		}
	}
	
	public void insertColumns(int index, ArrayList<ArrayList<Object>> columns) {
		int columnOffset = 0;
		for (ArrayList<Object> column: columns) {
			insertColumn(index + columnOffset, column);
		}
	}
	
	public void insertColumns(int index, Object[][] columns) {
		int columnOffset = 0;
		for (Object[] column: columns) {
			insertColumn(index + columnOffset, column);
		}
	}

	public void appendColumn(String columnName, ArrayList<Object> column) {
		insertColumn(this.colNames.size(), columnName, column);
	}

	public void appendColumn(String columnName, Object[] column) {
		insertColumn(this.colNames.size(), columnName, column);
	}
	
	public void appendColumn(ArrayList<Object> column) {
		insertColumn(this.colNames.size(), column);
	}
	
	public void appendColumn(Object[] column) {
		insertColumn(this.colNames.size(), column);
	}

	public void appendColumns(HashMap<String, ArrayList<Object>> map) {
		insertColumns(this.colNames.size(), map);
	}
	
	public void appendColumn(String columnName, HashMap<String, Object> map) {
		insertColumn(this.colNames.size(), columnName, map);
	}
	
	public void appendColumn(HashMap<String, Object> map) {
		insertColumn(this.colNames.size(), map);
	}

	public void appendColumns(ArrayList<String> columnNames, ArrayList<ArrayList<Object>> columns) {
		insertColumns(this.colNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, Object[][] columns) {
		insertColumns(this.colNames.size(), columnNames, columns);
	}
	
	public void appendColumns(ArrayList<ArrayList<Object>> columns) {
		insertColumns(this.colNames.size(), columns);
	}

	public void appendColumns(Object[][] columns) {
		insertColumn(this.getColumnNames().size(), columns);
	}
	
	public void insertRow(int index, String rowName, ArrayList<Object> row) {
		if (index > this.rowNames.size()) {
			System.out.println("Row index too high");
			return;
		}
		
		if ((this.rowNames.size() > 0) && (row.size() != this.colNames.size())) {
			System.out.print(row.size() + " != " + this.colNames.size() + ": ");
			System.out.println("Your row does not have the correct amount of columns");
			return;
		}
		
		for (int colCount = 0; colCount < row.size(); colCount++) {
			if (this.rowNames.size() == 0) {
				this.data.add(new ArrayList<DataItem>());
				if (this.colNames.size() <= colCount) {
					this.colNames.add(generateUnusedColumnName());
				}
			}
			this.data.get(colCount).add(index, new DataItem(row.get(colCount)));
		}
		String newRowName = CommonArray.getNewMangleName(this.rowNames, rowName);
		this.rowNames.add(index, newRowName);
	}
	
	public void insertRow(int index, String rowName, Object[] row) {
		insertRow(index, rowName, new ArrayList<Object>(Arrays.asList(row)));
	}
	
	public void insertRow(int index, ArrayList<Object> row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, Object[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRows(int index, HashMap<String, ArrayList<Object>> map) {
		int insertionOffset = 0;
		for (String rowName: map.keySet()) {
			this.insertRow(index + insertionOffset, rowName, map.get(rowName));
			insertionOffset++;
		}
	}
	
	public void insertRow(int index, String rowName, HashMap<String, Object> map) {
		ArrayList<Object> row = new ArrayList<Object>(this.colNames.size());
		for (int i = 0; i < this.colNames.size(); i++) {
			row.add("");
		}
		for (String colName : map.keySet()) {
			int colIndex = this.colNames.indexOf(colName);
			if (colIndex == -1) {
				this.colNames.add(colName);
			}
			row.set(colIndex, map.get(colName));
		}
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, HashMap<String, Object> map) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, map);
	}
	
	public void insertRows(int index, ArrayList<String> rowNames, ArrayList<ArrayList<Object>> rows) {
		int rowOffset = 0;
		for (ArrayList<Object> row : rows) {
			insertColumn(index + rowOffset, rowNames.get(rowOffset), row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, String[] rowNames, Object[][] rows) {
		int rowOffset = 0;
		for (Object[] row : rows) {
			insertColumn(index + rowOffset, rowNames[rowOffset], row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, ArrayList<ArrayList<Object>> rows) {
		int rowOffset = 0;
		for (ArrayList<Object> row : rows) {
			String rowName = generateUnusedRowName();
			insertColumn(index + rowOffset, rowName, row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, Object[][] rows) {
		int rowOffset = 0;
		for (Object[] row : rows) {
			String rowName = generateUnusedRowName();
			insertColumn(index + rowOffset, rowName, row);
			rowOffset++;
		}
	}

	public void appendRow(String rowName, ArrayList<Object> row) {
		insertRow(this.rowNames.size(), rowName, row);
	}

	public void appendRow(String rowName, Object[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(ArrayList<Object> row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(Object[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRows(HashMap<String, ArrayList<Object>> map) {
		insertRows(this.rowNames.size(), map);
	}
	
	public void appendRow(String name, HashMap<String, Object> map) {
		insertRow(this.rowNames.size(), name, map);
	}

	public void appendRow(HashMap<String, Object> map) {
		insertRow(this.rowNames.size(), map);
	}

	public void appendRows(ArrayList<ArrayList<Object>> rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(ArrayList<String> names, ArrayList<ArrayList<Object>> rows) {
		insertRows(this.rowNames.size(), names, rows);
	}

	public void appendRows(String[] names, Object[][] rows) {
		insertRows(this.rowNames.size(), names, rows);
	}

	public void appendRows(Object[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}

	public void dropColumn(int columnIndexToDrop) {
		this.data.remove(columnIndexToDrop);
		this.colNames.remove(columnIndexToDrop);
	}
	
	public void dropColumn(String name) {
		int colIndex = this.colNames.indexOf(name);
		if (colIndex != -1) {
			dropColumn(colIndex);
		}
	}

	public void dropColumns(ArrayList<String> names) {
		for (String columnName: names) {
			dropColumn(columnName);
		}
	}
	
	public void dropColumns(String[] names) {
		for (String columnName: names) {
			dropColumn(columnName);
		}
	}
	
	public void dropColumns(int[] columnIndicesToDrop) {
		Arrays.sort(columnIndicesToDrop);
		columnIndicesToDrop = CommonArray.reverse(columnIndicesToDrop);
		for (int columnIndex: columnIndicesToDrop) {
			dropColumn(columnIndex);
		}
	}
	
	public void dropRow(int rowIndexToDrop) {
		this.rowNames.remove(rowIndexToDrop);
		for (int columnCount = 0; columnCount < this.getNumCols(); columnCount++) {
			this.data.get(columnCount).remove(rowIndexToDrop);
		}
	}
	
	public void dropRow(String name) {
		int rowIndex = this.rowNames.indexOf(name);
		if (rowIndex >= 0) {
			dropRow(rowIndex);
		}
	}

	public void dropRows(ArrayList<String> names) {
		for (String rowName: names) {
			dropRow(rowName);
		}
	}
	
	public void dropRows(String[] names) {
		for (String rowName: names) {
			dropRow(rowName);
		}
	}
	
	public void dropRows(int[] rowIndicesToDrop) {
		Arrays.sort(rowIndicesToDrop);
		rowIndicesToDrop = CommonArray.reverse(rowIndicesToDrop);
		for (int rowindex: rowIndicesToDrop) {
			dropRow(rowindex);
		}
	}
	
	
	
	
	
	public DataItem[] getColumnAsDataItemArray(int index) {
		DataItem[] column = new DataItem[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i);
		}
		return column;
	}
	
	public DataItem[] getColumnAsDataItemArray(String name) {
		int index = this.colNames.indexOf(name);
		return getColumnAsDataItemArray(index);
	}
	
	public String[] getColumnAsStringArray(int index) {
		String[] column = new String[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).toString();
		}
		return column;
	}
	
	public String[] getColumnAsStringArray(String name) {
		int index = this.colNames.indexOf(name);
		return getColumnAsStringArray(index);
	}
	
	public int[] getColumnAsIntArray(int index) {
		int[] column = new int[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getValueConvertedToInt();
		}
		return column;
	}
	public int[] getColumnAsIntArray(String name) {
		int index = this.colNames.indexOf(name);
		return getColumnAsIntArray(index);
	}
	
	public double[] getColumnAsDoubleArray(int index) {
		double[] column = new double[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getValueConvertedToDouble();
		}
		return column;
	}
	
	public double[] getColumnAsDoubleArray(String name) {
		int index = this.colNames.indexOf(name);
		return getColumnAsDoubleArray(index);
	}

	public DataItem[][] getColumnsAs2DDataItemArray(int[] indices){
		DataItem[][] columns = new DataItem[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsDataItemArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(String[] names){
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.colNames, names);
		return getColumnsAs2DDataItemArray(indices);
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(ArrayList<String> names){
		return getColumnsAs2DDataItemArray(names.toArray(new String[0]));
	}
	
	public String[][] getColumnsAs2DStringArray(int[] indices) {
		String[][] columns = new String[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsStringArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public String[][] getColumnsAs2DStringArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.colNames, names);
		return getColumnsAs2DStringArray(indices);
	}
	
	public String[][] getColumnsAs2DStringArray(ArrayList<String> names) {
		return getColumnsAs2DStringArray(names.toArray(new String[0]));
	}
	
	public int[][] getColumnsAs2DIntArray(int[] indices) {
		int[][] columns = new int[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsIntArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public int[][] getColumnsAs2DIntArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.colNames, names);
		return getColumnsAs2DIntArray(indices);
	}
	
	public int[][] getColumnsAs2DIntArray(ArrayList<String> names) {
		return getColumnsAs2DIntArray(names.toArray(new String[0]));
	}
	
	public double[][] getColumnsAs2DDoubleArray(int[] indices) {
		double[][] columns = new double[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsDoubleArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public double[][] getColumnsAs2DDoubleArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.colNames, names);
		return getColumnsAs2DDoubleArray(indices);
	}
	
	public double[][] getColumnsAs2DDoubleArray(ArrayList<String> names) {
		return getColumnsAs2DDoubleArray(names.toArray(new String[0]));
	}
	
	
	public DataFrame getColumnAsDataFrame(String name) {
		ArrayList<String> names = new ArrayList<String>();
		names.add(name);
		return getColumnsAsDataFrame(names);
	}
	
	public DataFrame getColumnAsDataFrame(int col) {
		return getColumnAsDataFrame(this.colNames.get(col));
	}

	
	public DataFrame getColumnsAsDataFrame(int[] colIndices) {
		DataFrame newDF = this.clone();
		
		int numCols = newDF.getColumnNames().size();
		int[] indicesToDrop = new int[numCols - colIndices.length];
		int numAdded = 0;
		for (int colCount = 0; colCount < numCols; colCount++) {
			if (!CommonArray.contains(colIndices, colCount)) {
				indicesToDrop[numAdded] = colCount;
				numAdded++;
			}
		}
		newDF.dropColumns(indicesToDrop);
		
		return newDF;
	}
	
	
	

	public DataFrame getColumnsAsDataFrame(ArrayList<String> names) {
		DataFrame newDF = new DataFrame();
		boolean addedCol = false;

		for (int i = 0; i < this.colNames.size(); i++) {
			if (names.contains((this.colNames.get(i)))) {
				newDF.appendColumn(this.colNames.get(i), convertItemToObject(this.data.get(i)));
				addedCol = true;
			}
		}
		if (addedCol) {
			newDF.rowNames = this.rowNames;

		}
		return newDF;
	}

	
	public ArrayList<DataItem> getColumnAsDataItemArrayList(String name) {
		int index = this.colNames.indexOf(name);
		return this.data.get(index);
	}


	public DataFrame getRows(ArrayList<String> names) {
		DataFrame newDF = new DataFrame();

		boolean addedCol = false;

		ArrayList<ArrayList<DataItem>> rows = new ArrayList<ArrayList<DataItem>>();

		for (int i = 0; i < this.rowNames.size(); i++) {
			if (names.contains((this.rowNames.get(i)))) {
				ArrayList<DataItem> row = new ArrayList<DataItem>();
				for (int colNum = 0; colNum < this.colNames.size(); colNum++) {
					row.add(this.data.get(colNum).get(i));
				}
				rows.add(row);
				addedCol = true;
			}
		}

		if (addedCol) {
			newDF.rowNames = names;
			newDF.colNames = this.colNames;

			ArrayList<ArrayList<DataItem>> newColumns = new ArrayList<ArrayList<DataItem>>();

			for (int colCount = 0; colCount < rows.get(0).size(); colCount++) {
				ArrayList<DataItem> newColumn = new ArrayList<DataItem>();
				for (int rowCount = 0; rowCount < rows.size(); rowCount++) {
					newColumn.add(rows.get(rowCount).get(colCount));
				}
				newColumns.add(newColumn);
			}
			newDF.data = newColumns;
		}

		return newDF;
	}

	public DataFrame getRows(int lowerBound, int upperBound) {

		if (lowerBound < 0) {
			System.out.println("Lower bound must be greater than 0");
			return null;
		}

		if (lowerBound > upperBound) {
			System.out.println("Lower bound must be less than or equal to upper bound");
			return null;
		}

		if (upperBound > rowNames.size() - 1) {
			System.out.println("Upper bound must be less than the number of rows");
			return null;
		}

		ArrayList<String> names = new ArrayList<String>();
		for (int i = lowerBound; i <= upperBound; i++) {
			names.add(this.rowNames.get(i));
		}
		return getRows(names);
	}

	public DataFrame getRow(int row) {
		ArrayList<String> names = new ArrayList<String>();
		names.add(this.rowNames.get(row));

		return getRows(names);

	}

	public DataFrame getRow(String row) {
		ArrayList<String> names = new ArrayList<String>();
		names.add(row);

		return getRows(names);
	}

	public DataFrame head() {
		return head(5);
	}

	public DataFrame head(int number) {
		return getRows(0, number - 1);
	}

	public DataFrame tail() {
		return tail(5);
	}

	public DataFrame tail(int number) {
		return getRows(this.rowNames.size() - number, this.rowNames.size() - 1);
	}

	// Fix duplication of error checks
	public void setValue(int colNum, int rowNum, Object value, StorageType type) {
		if (colNum >= this.colNames.size()) {
			System.out.println("Column number must be lower than the amount of columns");
			return;
		}

		if (rowNum >= this.rowNames.size()) {
			System.out.println("Row number must be lower than the amount of rows");
			return;
		}

		if (colNum < 0) {
			System.out.println("Column number must be greater than 0");
			return;
		}

		if (rowNum < 0) {
			System.out.println("Row number must be greater than 0");
			return;
		}

//		this.data.get(colNum).set(rowNum, new DataItem(value, type));
		this.data.get(colNum).get(rowNum).setType(type);

	}

	public void setValue(int colNum, int rowNum, Object value) {
		if (colNum >= this.colNames.size()) {
			System.out.println("Column number must be lower than the amount of columns");
			return;
		}

		if (rowNum >= this.rowNames.size()) {
			System.out.println("Row number must be lower than the amount of rows");
			return;
		}

		if (colNum < 0) {
			System.out.println("Column number must be greater than 0");
			return;
		}

		if (rowNum < 0) {
			System.out.println("Row number must be greater than 0");
			return;
		}

		this.data.get(colNum).set(rowNum, new DataItem(value));
	}

	public DataItem getValue(int colNum, int rowNum) {
		if (colNum >= this.colNames.size()) {
			System.out.println("Column number must be lower than the amount of columns");
			return null;
		}

		if (rowNum >= this.rowNames.size()) {
			System.out.println("Row number must be lower than the amount of rows");
			return null;
		}

		if (colNum < 0) {
			System.out.println("Column number must be greater than 0");
			return null;
		}

		if (rowNum < 0) {
			System.out.println("Row number must be greater than 0");
			return null;
		}

		return this.data.get(colNum).get(rowNum);
	}

	public ArrayList<String> getColumnNames() {
		return this.colNames;
	}

	public ArrayList<String> getRowNames() {
		return this.rowNames;
	}

	public ArrayList<ArrayList<DataItem>> getData() {
		return this.data;
	}

	public int getNumRows() {
		return this.rowNames.size();
	}

	public int getNumCols() {
		return this.colNames.size();
	}

	public DataFrame isNull() {
		DataFrame nullDF = new DataFrame(this.colNames, this.rowNames);

		for (int colCount = 0; colCount < nullDF.colNames.size(); colCount++) {
			for (int rowCount = 0; rowCount < nullDF.rowNames.size(); rowCount++) {
				if (this.data.get(colCount).get(rowCount).getType() == DataItem.StorageType.Null) {
					nullDF.setValue(colCount, rowCount, true);
				} else {
					nullDF.setValue(colCount, rowCount, false);
				}
			}
		}

		return nullDF;
	}

	public void transpose() {
		ArrayList<ArrayList<DataItem>> transpose = new ArrayList<ArrayList<DataItem>>();

		for (int rowCount = 0; rowCount < this.rowNames.size(); rowCount++) {
			ArrayList<DataItem> newCol = new ArrayList<DataItem>();
			for (int colCount = 0; colCount < this.colNames.size(); colCount++) {
				newCol.add(this.data.get(colCount).get(rowCount));
			}
			transpose.add(newCol);
		}

		this.data = transpose;

		ArrayList<String> tempList;
		tempList = this.colNames;
		this.colNames = this.rowNames;
		this.rowNames = tempList;

	}
	
	public void setColumnType(int colNum, StorageType type) {
		for (int rowNum = 0; rowNum < this.getNumRows(); rowNum++) {
			this.setValue(colNum, rowNum, getValue(colNum, rowNum).getObjectValue(), type);
		}
	}

	private ArrayList<DataItem> convertObjectListToItemList(ArrayList<Object> column) {
		ArrayList<DataItem> list = new ArrayList<DataItem>();
		for (Object item : column) {
//			System.out.println("item = " + item);
			list.add(new DataItem(item));
		}
		return list;
	}

	private ArrayList<Object> convertItemToObject(ArrayList<DataItem> arrayList) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (DataItem item : arrayList) {
			list.add(item);
		}
		return list;
	}

	/*
	 * 
	 * Have a look into java.util.Optional or using Maps
	 * https://stackoverflow.com/questions/965690/java-optional-parameters
	 *
	 */
	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean rightAlign) {

		int largestIndexWidth = -1;
		for (int i = 0; i < this.rowNames.size(); i++) {
			int indexWidth = String.valueOf(rowNames.get(i)).length();
			if (indexWidth > largestIndexWidth) {
				largestIndexWidth = indexWidth;
			}
		}

		int[] largestColWidths = new int[this.colNames.size()];
		for (int i = 0; i < largestColWidths.length; i++) {
			largestColWidths[i] = String.valueOf(this.colNames.get(i)).length();
		}

		for (int colCount = 0; colCount < this.colNames.size(); colCount++) {
			for (int rowCount = 0; rowCount < this.data.get(colCount).size(); rowCount++) {
				int lengthOfValue = String.valueOf(data.get(colCount).get(rowCount)).length();
				if (lengthOfValue > largestColWidths[colCount]) {
					largestColWidths[colCount] = lengthOfValue;
				}
			}
		}

		int totalColWidth = 0;
		for (int i = 0; i < colNames.size(); i++) {
			totalColWidth += largestColWidths[i] + dataPadding;
		}

		int totalGridWidth = (largestIndexWidth + indexPadding) + (totalColWidth) + this.colNames.size();
		int totalGridHeight = 1 + 1 + rowNames.size();
		printGrid = new String[totalGridHeight][totalGridWidth];
		colToWriteNext = new int[totalGridHeight];

		for (int i = 0; i < colToWriteNext.length; i++) {
			colToWriteNext[i] = 0;
		}

		// Top left corner
		addWhiteSpace(0, largestIndexWidth + indexPadding);

		// Top column names
		for (int colCount = 0; colCount < colNames.size(); colCount++) {
			writeChar(0, "|");
			int numberOfWhiteSpace = ((largestColWidths[colCount] + dataPadding) - colNames.get(colCount).length());

			writeWholeBlock(colNames.get(colCount), 0, numberOfWhiteSpace, rightAlign);
		}

		// Row of dashes
		for (int i = 0; i < largestIndexWidth + indexPadding; i++) {
			writeChar(1, "-");
		}
		for (int colCount = 0; colCount < colNames.size(); colCount++) {
			writeChar(1, "+");
			for (int dashCount = 0; dashCount < largestColWidths[colCount] + dataPadding; dashCount++) {
				writeChar(1, "-");
			}

		}

		// Fill in each row
		for (int rowCount = 0; rowCount < this.rowNames.size(); rowCount++) {
			int gridRowNum = rowCount + 2;
			String currentIndex = String.valueOf(rowNames.get(rowCount));

			int numberOfWhiteSpace = ((largestIndexWidth + indexPadding) - currentIndex.length());

			writeWholeBlock(currentIndex, gridRowNum, numberOfWhiteSpace, rightAlign);

			for (int colCount = 0; colCount < this.colNames.size(); colCount++) {
				writeChar(gridRowNum, "|");
				String currentDataItem = String.valueOf(data.get(colCount).get(rowCount));

				numberOfWhiteSpace = ((largestColWidths[colCount] + dataPadding) - currentDataItem.length());

				writeWholeBlock(currentDataItem, gridRowNum, numberOfWhiteSpace, rightAlign);

			}

		}

		String stringToReturn = "";
		for (int rowCount = 0; rowCount < totalGridHeight; rowCount++) {
			for (int colCount = 0; colCount < totalGridWidth; colCount++) {
				stringToReturn += printGrid[rowCount][colCount];
			}
			stringToReturn += "\n";
		}

		return stringToReturn;

	}

	private void writeChar(int row, String ch) {
		printGrid[row][colToWriteNext[row]] = ch;
		colToWriteNext[row]++;
	}

	private void writeChar(int row, char ch) {
		writeChar(row, String.valueOf(ch));
	}

	private void addWhiteSpace(int row, int amountOfWhiteSpace) {
		for (int i = 0; i < amountOfWhiteSpace; i++) {
			writeChar(row, " ");
		}
	}

	private void writeWholeBlock(String currentItem, int gridRowNum, int numberOfWhiteSpace, boolean rightAlign) {

		if (rightAlign) {

			addWhiteSpace(gridRowNum, numberOfWhiteSpace);
			writeTextItemInBlock(currentItem, gridRowNum);
		} else {
			writeTextItemInBlock(currentItem, gridRowNum);
			addWhiteSpace(gridRowNum, numberOfWhiteSpace);
		}

	}

	private void writeTextItemInBlock(String currentItem, int gridRowNum) {
		for (int charCount = 0; charCount < currentItem.length(); charCount++) {
			writeChar(gridRowNum, currentItem.charAt(charCount));
		}
	}
	
	@Override
	public DataFrame clone() {
		DataFrame newDF = new DataFrame((ArrayList<String>)this.getColumnNames().clone(), (ArrayList<String>)this.getRowNames().clone());
		newDF.setData((ArrayList<ArrayList<DataItem>>)this.getData().clone());
				
		return newDF;
	}

	@Override
	public Iterator<ArrayList<DataItem>> iterator() {

		return new DataFrameIterator();
	}

	class DataFrameIterator implements Iterator<ArrayList<DataItem>> {

		int current;

		public DataFrameIterator() {
			current = 0;
		}

		@Override
		public boolean hasNext() {
			if (current < rowNames.size()) {
				return true;
			}
			return false;
		}

		@Override
		public ArrayList<DataItem> next() {

			ArrayList<DataItem> row = new ArrayList<DataItem>();
			for (int colNum = 0; colNum < colNames.size(); colNum++) {
				row.add(data.get(colNum).get(current));
			}
			current++;
			return row;
		}

	}

}
