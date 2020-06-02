package thesis.DataFrame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import thesis.Common.CommonArray;
import thesis.Common.CommonMath;
import thesis.DataFrame.DataItem.StorageType;

public class DataFrame implements Iterable<ArrayList<DataItem>> {

	private ArrayList<ArrayList<DataItem>> data;
	private ArrayList<String> columnNames;
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
		this.columnNames = new ArrayList<String>();
		this.rowNames = new ArrayList<String>();
	}

	// Create a DF with specified value
	public DataFrame(int numColumns, int numRows, Object fill) {
		this.columnNames = CommonArray.generateIncreasingSequence(numColumns);
		this.rowNames = CommonArray.generateIncreasingSequence(numRows);
		this.data = new ArrayList<ArrayList<DataItem>>();
		for (int columnCount = 0; columnCount < numColumns; columnCount++) {
			ArrayList<DataItem> column = new ArrayList<DataItem>();
			for (int rowCount = 0; rowCount < numRows; rowCount++) {
				DataItem item = new DataItem(fill);
				column.add(item);
			}
			this.data.add(column);
		}
	}
	
	// Create a DF with random values
	public DataFrame(int numColumns, int numRows, Class<?> cls) {
		this(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), cls);
	}
	
	public DataFrame(ArrayList<String> columnNames, ArrayList<String> rowNames, Class<?> cls) {
		this.columnNames = columnNames;
		this.rowNames = rowNames;
		this.data = new ArrayList<ArrayList<DataItem>>();
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			ArrayList<DataItem> column = new ArrayList<DataItem>();
			for (int rowCount = 0; rowCount < rowNames.size(); rowCount++) {
				Object fill;
				if (cls == String.class) {
					fill = CommonArray.randomString(5);
				} else if (cls == Integer.class) {
					fill = ThreadLocalRandom.current().nextInt(-10, 11);
				} else if (cls == Double.class) {
					Double doubleValue = ThreadLocalRandom.current().nextDouble(-10, 10);
					DecimalFormat df = new DecimalFormat("#.####");
					df.setRoundingMode(RoundingMode.CEILING);
					fill = Double.parseDouble(df.format(doubleValue));
				} else if (cls == Boolean.class) {
					fill = ThreadLocalRandom.current().nextBoolean();
				} else if (cls == LocalDate.class) {
					// credit for this logic https://stackoverflow.com/a/34051525/6122201
					long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
				    long maxDay = LocalDate.of(2030, 12, 31).toEpochDay();
				    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
				    fill = LocalDate.ofEpochDay(randomDay);
				} else {
					fill = null;
				}
				DataItem item = new DataItem(fill);
				column.add(item);
			}
			this.data.add(column);
		}
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
		this.columnNames = CommonArray.convertStringArrayToArrayList(colNamesToAdd);
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
		this.columnNames = df.getColumnNames();
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
					this.columnNames = CommonArray.convertStringArrayToArrayList(mangledColumns);

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

	
	
	public void insertColumn(int index, String columnName, ArrayList<Object> column) {
		if (index > this.columnNames.size()) {
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
		String newColumnName = CommonArray.getNewMangleName(this.columnNames, columnName);
		this.columnNames.add(index, newColumnName);

	}

	public void insertColumn(int index, String columnName, Object[] column) {
		insertColumn(index, columnName, new ArrayList<Object>(Arrays.asList(column)));
	}
	
	public void insertColumn(int index, String columnName, DataItem[] column) {
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
	
	public void insertColumn(int index, DataItem[] column) {
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
				this.columnNames.add(rowName);

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
	
	public void insertColumns(int index, String[] columnNames, DataItem[][] columns) {
		int columnOffset = 0;
		for (DataItem[] column : columns) {
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
	
	public void insertColumns(int index, DataItem[][] columns) {
		int columnOffset = 0;
		for (DataItem[] column: columns) {
			insertColumn(index + columnOffset, column);
		}
	}

	public void appendColumn(String columnName, ArrayList<Object> column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}

	public void appendColumn(String columnName, Object[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, DataItem[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(ArrayList<Object> column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(Object[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(DataItem[] column) {
		insertColumn(this.columnNames.size(), column);
	}

	public void appendColumns(HashMap<String, ArrayList<Object>> map) {
		insertColumns(this.columnNames.size(), map);
	}
	
	public void appendColumn(String columnName, HashMap<String, Object> map) {
		insertColumn(this.columnNames.size(), columnName, map);
	}
	
	public void appendColumn(HashMap<String, Object> map) {
		insertColumn(this.columnNames.size(), map);
	}

	public void appendColumns(ArrayList<String> columnNames, ArrayList<ArrayList<Object>> columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, Object[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, DataItem[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(ArrayList<ArrayList<Object>> columns) {
		insertColumns(this.columnNames.size(), columns);
	}

	public void appendColumns(Object[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(DataItem[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void insertRow(int index, String rowName, ArrayList<Object> row) {
		if (index > this.rowNames.size()) {
			System.out.println("Row index too high");
			return;
		}
		
		if ((this.rowNames.size() > 0) && (row.size() != this.columnNames.size())) {
			System.out.print(row.size() + " != " + this.columnNames.size() + ": ");
			System.out.println("Your row does not have the correct amount of columns");
			return;
		}
		
		for (int colCount = 0; colCount < row.size(); colCount++) {
			if (this.rowNames.size() == 0) {
				this.data.add(new ArrayList<DataItem>());
				if (this.columnNames.size() <= colCount) {
					this.columnNames.add(generateUnusedColumnName());
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
	
	public void insertRow(int index, String rowName, DataItem[] row) {
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
	
	public void insertRow(int index, DataItem[] row) {
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
		ArrayList<Object> row = new ArrayList<Object>(this.columnNames.size());
		for (int i = 0; i < this.columnNames.size(); i++) {
			row.add("");
		}
		for (String colName : map.keySet()) {
			int colIndex = this.columnNames.indexOf(colName);
			if (colIndex == -1) {
				this.columnNames.add(colName);
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
			insertRow(index + rowOffset, rowNames.get(rowOffset), row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, String[] rowNames, Object[][] rows) {
		int rowOffset = 0;
		for (Object[] row : rows) {
			insertRow(index + rowOffset, rowNames[rowOffset], row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, String[] rowNames, DataItem[][] rows) {
		int rowOffset = 0;
		for (DataItem[] row : rows) {
			insertRow(index + rowOffset, rowNames[rowOffset], row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, ArrayList<ArrayList<Object>> rows) {
		int rowOffset = 0;
		for (ArrayList<Object> row : rows) {
			String rowName = generateUnusedRowName();
			insertRow(index + rowOffset, rowName, row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, Object[][] rows) {
		int rowOffset = 0;
		for (Object[] row : rows) {
			String rowName = generateUnusedRowName();
			insertRow(index + rowOffset, rowName, row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, DataItem[][] rows) {
		int rowOffset = 0;
		for (DataItem[] row : rows) {
			String rowName = generateUnusedRowName();
			insertRow(index + rowOffset, rowName, row);
			rowOffset++;
		}
	}

	public void appendRow(String rowName, ArrayList<Object> row) {
		insertRow(this.rowNames.size(), rowName, row);
	}

	public void appendRow(String rowName, Object[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, DataItem[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(ArrayList<Object> row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(Object[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(DataItem[] row) {
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

	public void appendRows(String[] rowNames, DataItem[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(Object[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(DataItem[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}

	public void dropColumn(int columnIndexToDrop) {
		this.data.remove(columnIndexToDrop);
		this.columnNames.remove(columnIndexToDrop);
	}
	
	public void dropColumn(String name) {
		int colIndex = this.columnNames.indexOf(name);
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
	
	// ---------------------
	// ------ Getters ------
	// ---------------------
	
	public DataItem[][] getDataAs2DDataItemArray() {
		return getColumnsAs2DDataItemArray(0, this.getNumCols() - 1);
	}
	
	public String[][] getDataAs2DStringArray() {
		return getColumnsAs2DStringArray(0, this.getNumCols() - 1);
	}
	
	public int[][] getDataAs2DIntArray() {
		return getColumnsAs2DIntArray(0, this.getNumCols() - 1);
	}
	
	public double[][] getDataAs2DDoubleArray() {
		return getColumnsAs2DDoubleArray(0, this.getNumCols() - 1);
	}
	
	public LocalDate[][] getDataAs2DDateArray() {
		return getColumnsAs2DDateArray(0, this.getNumCols() - 1);
	}
	
	public boolean[][] getDataAs2DBooleanArray() {
		return getColumnsAs2DBooleanArray(0, this.getNumCols() - 1);
	}
	
	
	// -------------------------
	// ------ Get Columns ------
	// -------------------------
	public DataItem[] getColumnAsDataItemArray(int index) {
		DataItem[] column = new DataItem[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i);
		}
		return column;
	}
	
	public DataItem[] getColumnAsDataItemArray(String name) {
		int index = this.columnNames.indexOf(name);
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
		int index = this.columnNames.indexOf(name);
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
		int index = this.columnNames.indexOf(name);
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
		int index = this.columnNames.indexOf(name);
		return getColumnAsDoubleArray(index);
	}
	
	public LocalDate[] getColumnAsDateArray(int index) {
		LocalDate[] column = new LocalDate[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getDateValue();
		}
		return column;
	}
	
	public LocalDate[] getColumnAsDateArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsDateArray(index);
	}

	public boolean[] getColumnAsBooleanArray(int index) {
		boolean[] column = new boolean[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getBooleanValue();
		}
		return column;
	}
	
	public boolean[] getColumnAsBooleanArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsBooleanArray(index);
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(int[] indices){
		DataItem[][] columns = new DataItem[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsDataItemArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(String[] names){
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DDataItemArray(indices);
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(ArrayList<String> names){
		return getColumnsAs2DDataItemArray(names.toArray(new String[0]));
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DDataItemArray(indicesToGet);
	}
	
	public String[][] getColumnsAs2DStringArray(int[] indices) {
		String[][] columns = new String[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsStringArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public String[][] getColumnsAs2DStringArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DStringArray(indices);
	}
	
	public String[][] getColumnsAs2DStringArray(ArrayList<String> names) {
		return getColumnsAs2DStringArray(names.toArray(new String[0]));
	}
	
	public String[][] getColumnsAs2DStringArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DStringArray(indicesToGet);
	}
	
	public int[][] getColumnsAs2DIntArray(int[] indices) {
		int[][] columns = new int[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsIntArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public int[][] getColumnsAs2DIntArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DIntArray(indices);
	}
	
	public int[][] getColumnsAs2DIntArray(ArrayList<String> names) {
		return getColumnsAs2DIntArray(names.toArray(new String[0]));
	}
	
	public int[][] getColumnsAs2DIntArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DIntArray(indicesToGet);
	}
	
	public double[][] getColumnsAs2DDoubleArray(int[] indices) {
		double[][] columns = new double[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsDoubleArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public double[][] getColumnsAs2DDoubleArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DDoubleArray(indices);
	}
	
	public double[][] getColumnsAs2DDoubleArray(ArrayList<String> names) {
		return getColumnsAs2DDoubleArray(names.toArray(new String[0]));
	}
	
	public double[][] getColumnsAs2DDoubleArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DDoubleArray(indicesToGet);
	}
	
	public boolean[][] getColumnsAs2DBooleanArray(int[] indices) {
		boolean[][] columns = new boolean[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsBooleanArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public boolean[][] getColumnsAs2DBooleanArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DBooleanArray(indices);
	}
	
	public boolean[][] getColumnsAs2DBooleanArray(ArrayList<String> names) {
		return getColumnsAs2DBooleanArray(names.toArray(new String[0]));
	}
	
	
	public boolean[][] getColumnsAs2DBooleanArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DBooleanArray(indicesToGet);
	}
	
	
	public LocalDate[][] getColumnsAs2DDateArray(int[] indices) {
		LocalDate[][] columns = new LocalDate[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsDateArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public LocalDate[][] getColumnsAs2DDateArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DDateArray(indices);
	}
	
	public LocalDate[][] getColumnsAs2DDateArray(ArrayList<String> names) {
		return getColumnsAs2DDateArray(names.toArray(new String[0]));
	}
	
	public LocalDate[][] getColumnsAs2DDateArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DDateArray(indicesToGet);
	}
	
	public DataFrame getColumnAsDataFrame(String name) {
		return getColumnsAsDataFrame(new String[] { name });
	}
	
	public DataFrame getColumnAsDataFrame(int col) {
		return getColumnsAsDataFrame(new int[] { col });
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
	
	public DataFrame getColumnsAsDataFrame(String[] colNames) {
		int[] colIndices = CommonArray.getIndicesOfStringsInArray(this.getColumnNames(), colNames);
		return getColumnsAsDataFrame(colIndices);
	}
	

	public DataFrame getColumnsAsDataFrame(ArrayList<String> names) {
		int[] colIndices = CommonArray.getIndicesOfStringsInArray(this.getColumnNames(), names);
		return getColumnsAsDataFrame(colIndices);
	}
	
	// inclusive lowerBound, inclusive upperBound
	public DataFrame getColumnsAsDataFrame(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAsDataFrame(indicesToGet);
	}

	
	// ----------------------
	// ------ Get Rows ------
	// ----------------------
	
	public DataItem[] getRowAsDataItemArray(int index) {
		DataItem[] row = new DataItem[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index);
		}
		return row;
	}

	public DataItem[] getRowAsDataItemArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsDataItemArray(index);
	}

	public String[] getRowAsStringArray(int index) {
		String[] row = new String[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).toString();
		}
		return row;
	}

	public String[] getRowAsStringArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsStringArray(index);
	}

	public int[] getRowAsIntArray(int index) {
		int[] row = new int[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getValueConvertedToInt();
		}
		return row;
	}

	public int[] getRowAsIntArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsIntArray(index);
	}

	public double[] getRowAsDoubleArray(int index) {
		double[] row = new double[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getValueConvertedToInt();
		}
		return row;
	}

	public double[] getRowAsDoubleArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsDoubleArray(index);
	}

	public LocalDate[] getRowAsDateArray(int index) {
		LocalDate[] row = new LocalDate[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getDateValue();
		}
		return row;
	}
	
	public LocalDate[] getRowAsDateArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsDateArray(index);
	}

	public boolean[] getRowAsBooleanArray(int index) {
		boolean[] row = new boolean[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getBooleanValue();
		}
		return row;
	}

	public boolean[] getRowAsBooleanArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsBooleanArray(index);
	}
	
	public DataItem[][] getRowsAs2DDataItemArray(int[] indices) {
		DataItem[][] rows = new DataItem[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsDataItemArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public DataItem[][] getRowsAs2DDataItemArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DDataItemArray(indices);
	}
	
	public DataItem[][] getRowsAs2DDataItemArray(ArrayList<String> names) {
		return getRowsAs2DDataItemArray(names.toArray(new String[0]));
	}

	public DataItem[][] getRowsAs2DDataItemArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DDataItemArray(indicesToGet);
	}
	
	
	public String[][] getRowsAs2DStringArray(int[] indices)	{
		String[][] rows = new String[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsStringArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public String[][] getRowsAs2DStringArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DStringArray(indices);
	}
	
	public String[][] getRowsAs2DStringArray(ArrayList<String> names) {
		return getRowsAs2DStringArray(names.toArray(new String[0]));
	}
	
	public String[][] getRowsAs2DStringArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DStringArray(indicesToGet);
	}
	
	public int[][] getRowsAs2DIntArray(int[] indices) {
		int[][] rows = new int[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsIntArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public int[][] getRowsAs2DIntArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DIntArray(indices);
	}
	
	public int[][] getRowsAs2DIntArray(ArrayList<String> names) {
		return getRowsAs2DIntArray(names.toArray(new String[0]));
	}
	
	public int[][] getRowsAs2DIntArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DIntArray(indicesToGet);
	}
	
	
	public double[][] getRowsAs2DDoubleArray(int[] indices) {
		double[][] rows = new double[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsDoubleArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public double[][] getRowsAs2DDoubleArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DDoubleArray(indices);
	}
	
	public double[][] getRowsAs2DDoubleArray(ArrayList<String> names) {
		return getRowsAs2DDoubleArray(names.toArray(new String[0]));
	}
	
	public double[][] getRowsAs2DDoubleArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DDoubleArray(indicesToGet);
	}
	
	public boolean[][] getRowsAs2DBooleanArray(int[] indices) {
		boolean[][] rows = new boolean[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsBooleanArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public boolean[][] getRowsAs2DBooleanArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DBooleanArray(indices);
	}
	
	public boolean[][] getRowsAs2DBooleanArray(ArrayList<String> names) {
		return getRowsAs2DBooleanArray(names.toArray(new String[0]));
	}
	
	public boolean[][] getRowsAs2DBooleanArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DBooleanArray(indicesToGet);
	}

	public LocalDate[][] getRowsAs2DDateArray(int[] indices) {
		LocalDate[][] rows = new LocalDate[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsDateArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public LocalDate[][] getRowsAs2DDateArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DDateArray(indices);
	}
	
	public LocalDate[][] getRowsAs2DDateArray(ArrayList<String> names) {
		return getRowsAs2DDateArray(names.toArray(new String[0]));
	}
	
	public LocalDate[][] getRowsAs2DDateArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DDateArray(indicesToGet);
	}
	
	public DataFrame getRowAsDataFrame(String name) {
		return getRowsAsDataFrame(new String[] { name });
	}
	
	public DataFrame getRowAsDataFrame(int row) {
		return getRowsAsDataFrame(new int[] { row });
	}
	
	public DataFrame getRowsAsDataFrame(ArrayList<String> names) {
		int[] rowIndices = CommonArray.getIndicesOfStringsInArray(this.getRowNames(), names);
		return getRowsAsDataFrame(rowIndices);
	}
	
	public DataFrame getRowsAsDataFrame(String[] names) {
		int[] rowIndices = CommonArray.getIndicesOfStringsInArray(this.getRowNames(), names);
		return getRowsAsDataFrame(rowIndices);
	}
	
	public DataFrame getRowsAsDataFrame(int[] rowIndices) {
		DataFrame newDF = this.clone();
		
		int numRows = newDF.getRowNames().size();
		int[] indicesToDrop = new int[numRows - rowIndices.length];
		int numAdded = 0;
		for (int rowCount = 0; rowCount < numRows; rowCount++) {
			if (!CommonArray.contains(rowIndices, rowCount)) {
				indicesToDrop[numAdded] = rowCount;
				numAdded++;
			}
		}
		newDF.dropRows(indicesToDrop);
		
		return newDF;
	}
	
	public DataFrame getRowsAsDataFrame(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAsDataFrame(indicesToGet);
	}
	
	public DataFrame first() {
		return head(1);
	}
	public DataFrame last()	{
		return tail(1);
	}
	
	public DataFrame head() {
		return head(5);
	}

	public DataFrame head(int number) {
		return getRowsAsDataFrame(0, number - 1);
	}

	public DataFrame tail() {
		return tail(5);
	}

	public DataFrame tail(int number) {
		return getRowsAsDataFrame(this.rowNames.size() - number, this.rowNames.size() - 1);
	}

	// -----------------------------
	// ------ Math Operations ------
	// -----------------------------
	public DataFrame add(DataFrame df) {
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					this.getValue(colCount, rowCount).add(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return this;
	}
	
	public DataFrame add(DataItem value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).add(value);
			}	
		}
		return this;
	}
	
	public DataFrame add(int value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).add(value);
			}	
		}
		return this;
	}
	
	public DataFrame add(double value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).add(value);
			}	
		}
		return this;
	}
	
	public DataFrame add(float value) {
		return this.add((double) value);
	}
	
	public DataFrame add(Period timePeriod) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).add(timePeriod);
			}	
		}
		return this;
	}
	
	
	public DataFrame subtract(DataFrame df) {
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					this.getValue(colCount, rowCount).subtract(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return this;
	}
	
	public DataFrame subtract(DataItem value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).subtract(value);
			}	
		}
		return this;
	}
	
	public DataFrame subtract(int value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).subtract(value);
			}	
		}
		return this;
	}
	
	public DataFrame subtract(double value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).subtract(value);
			}	
		}
		return this;
	}
	
	public DataFrame subtract(float value) {
		return this.subtract((double) value);
	}
	
	public DataFrame subtract(Period timePeriod) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).subtract(timePeriod);
			}	
		}
		return this;
	}
	
	public DataFrame multiply(DataFrame df) {
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					this.getValue(colCount, rowCount).multiply(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return this;
	}
	
	public DataFrame multiply(DataItem value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).multiply(value);
			}	
		}
		return this;
	}
	
	public DataFrame multiply(int value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).multiply(value);
			}	
		}
		return this;
	}
	
	public DataFrame multiply(double value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).multiply(value);
			}	
		}
		return this;
	}
	
	public DataFrame multiply(float value) {
		return this.multiply((double) value);
	}
	
	public DataFrame divide(DataFrame df) {
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					this.getValue(colCount, rowCount).divide(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return this;
	}
	
	public DataFrame divide(DataItem value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).divide(value);
			}	
		}
		return this;
	}
	
	public DataFrame divide(int value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).divide(value);
			}	
		}
		return this;
	}
	
	public DataFrame divide(double value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).divide(value);
			}	
		}
		return this;
	}
	
	public DataFrame divide(float value) {
		return this.divide((double) value);
	}
	
	public DataFrame mod(DataFrame df) {
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					this.getValue(colCount, rowCount).mod(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return this;
	}
	
	public DataFrame mod(DataItem value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).mod(value);
			}	
		}
		return this;
	}
	
	public DataFrame mod(int value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).mod(value);
			}	
		}
		return this;
	}
	
	public DataFrame power(DataFrame df) {
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					this.getValue(colCount, rowCount).power(df.getValue(colCount, rowCount));
				}	
			}
		}
		return this;
	}

	public DataFrame power(DataItem value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).power(value);
			}	
		}
		return this;
	}

	public DataFrame power(int value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).power(value);
			}	
		}
		return this;
	}

	public DataFrame power(double value) {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).power(value);
			}	
		}
		return this;
	}

	public DataFrame power(float value) {
		return this.divide((double) value);
	}

	public DataFrame intFloor() {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).intFloor();
			}	
		}
		return this;
	}
	
	public DataFrame doubleFloor() {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).doubleFloor();
			}	
		}
		return this;
	}
	
	public DataFrame intCeiling() {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).intCeiling();
			}	
		}
		return this;
	}
	
	public DataFrame doubleCeiling() {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				this.getValue(colCount, rowCount).doubleCeiling();
			}	
		}
		return this;
	}
	
	public DataFrame negate() {
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				DataItem value = this.getValue(colCount, rowCount);
				if (value.type == StorageType.Boolean) {
					value.flip();
				} else {
					value.multiply(-1);
				}
				
			}	
		}
		return this;
	}
	
	// ------ Absolute Value ------ 
		public DataFrame absoluteValue() {
			return absoluteValueColumns(0, this.getNumCols() - 1);
		}
		
		public DataFrame absoluteValueColumn(int index) {
			DataItem[] column = this.getColumnAsDataItemArray(index);
			for (int rowNum = 0; rowNum < column.length; rowNum++) {
				if (column[rowNum].getValueConvertedToDouble() < 0) {
					column[rowNum].multiply(-1);
				}
			}
			return this;
		}
		
		public DataFrame absoluteValueColumn(String name) {
			int index = this.columnNames.indexOf(name);
			return absoluteValueColumn(index);
		}
		
		public DataFrame absoluteValueColumns(int[] indices) {
			for (int colCount = 0; colCount < indices.length; colCount++) {
				absoluteValueColumn(indices[colCount]);
			}
			return this;
		}
		
		public DataFrame absoluteValueColumns(String[] names) {
			int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
			return absoluteValueColumns(indices);
		}
		
		public DataFrame absoluteValueColumns(ArrayList<String> names) {
			return absoluteValueColumns(names.toArray(new String[0]));
		}
		
		public DataFrame absoluteValueColumns(int lowerBound, int upperBound) {
			int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
			return absoluteValueColumns(indicesToGet);
		}
		
		public DataFrame absoluteValueRow(int index) {
			DataItem[] row = this.getRowAsDataItemArray(index);
			for (int colNum = 0; colNum < row.length; colNum++) {
				if (row[colNum].getValueConvertedToDouble() < 0) {
					row[colNum].multiply(-1);
				}
			}
			return this;
		}
		
		public DataFrame absoluteValueRow(String name) {
			int index = this.rowNames.indexOf(name);
			return absoluteValueRow(index);
		}
		
		public DataFrame absoluteValueRows(int[] indices) {
			for (int rowCount = 0; rowCount < indices.length; rowCount++) {
				absoluteValueRow(indices[rowCount]);
			}
			return this;
		}
		
		public DataFrame absoluteValueRows(String[] names) {
			int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
			return absoluteValueRows(indices);
		}
		
		public DataFrame absoluteValueRows(ArrayList<String> names) {
			return absoluteValueRows(names.toArray(new String[0]));
		}
		
		public DataFrame absoluteValueRows(int lowerBound, int upperBound) {
			int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
			return absoluteValueRows(indicesToGet);
		}
		
		// ------ Clamp ------  
		public DataFrame clamp(int lowerBound, int upperBound) { 
			return clamp((double)lowerBound, (double)upperBound);
		}

		public DataFrame clamp(double lowerBound, double upperBound) { 
			return clampColumns(0, this.getNumCols() - 1, lowerBound, upperBound);
		}

		public DataFrame clamp(LocalDate lowerBound, LocalDate upperBound) { 
			return clampColumns(0, this.getNumCols() - 1, lowerBound, upperBound);
		}

		public DataFrame clampColumn(int columnIndex, int lowerBound, int upperBound) { 
			return clampColumn(columnIndex, (double) lowerBound, (double) upperBound);
		}

		public DataFrame clampColumn(int columnIndex, double lowerBound, double upperBound) { 
			DataItem[] column = this.getColumnAsDataItemArray(columnIndex);
			for (int rowIndex = 0; rowIndex < column.length; rowIndex++) {
				column[rowIndex].clamp(lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampColumn(int columnIndex, LocalDate lowerBound, LocalDate upperBound) { 
			DataItem[] column = this.getColumnAsDataItemArray(columnIndex);
			for (int rowIndex = 0; rowIndex < column.length; rowIndex++) {
				column[rowIndex].clamp(lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampColumn(String columnName, int lowerBound, int upperBound) { 
			return clampColumn(columnName, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampColumn(String columnName, double lowerBound, double upperBound) { 
			int columnIndex = this.columnNames.indexOf(columnName);
			return clampColumn(columnIndex, lowerBound, upperBound);
		}

		public DataFrame clampColumn(String columnName, LocalDate lowerBound, LocalDate upperBound) { 
			int columnIndex = this.columnNames.indexOf(columnName);
			return clampColumn(columnIndex, lowerBound, upperBound);
		}

		public DataFrame clampColumns(int[] columnIndices, int lowerBound, int upperBound) { 
			return clampColumns(columnIndices, (double)lowerBound,(double) upperBound);
		}

		public DataFrame clampColumns(int[] columnIndices, double lowerBound, double upperBound) { 
			for (int colCount = 0; colCount < columnIndices.length; colCount++) {
				clampColumn(columnIndices[colCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampColumns(int[] columnIndices, LocalDate lowerBound, LocalDate upperBound) { 
			for (int colCount = 0; colCount < columnIndices.length; colCount++) {
				clampColumn(columnIndices[colCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampColumns(String[] columnNames, int lowerBound, int upperBound) { 
			return clampColumns(columnNames, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampColumns(String[] columnNames, double lowerBound, double upperBound) { 
			for (int colCount = 0; colCount < columnNames.length; colCount++) {
				clampColumn(columnNames[colCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampColumns(String[] columnNames, LocalDate lowerBound, LocalDate upperBound) { 
			for (int colCount = 0; colCount < columnNames.length; colCount++) {
				clampColumn(columnNames[colCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampColumns(ArrayList<String> columnNames, int lowerBound, int upperBound) { 
			return clampColumns(columnNames.toArray(new String[0]), lowerBound, upperBound);
		}

		public DataFrame clampColumns(ArrayList<String> columnNames, double lowerBound, double upperBound) { 
			return clampColumns(columnNames.toArray(new String[0]), lowerBound, upperBound);
		}

		public DataFrame clampColumns(ArrayList<String> columnNames, LocalDate lowerBound, LocalDate upperBound) { 
			return clampColumns(columnNames.toArray(new String[0]), lowerBound, upperBound);
		}

		public DataFrame clampColumns(int lowestColumnIndex, int highestColumnIndex, int lowerBound, int upperBound) {
			return clampColumns(lowestColumnIndex, highestColumnIndex, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampColumns(int lowestColumnIndex, int highestColumnIndex, double lowerBound, double upperBound) { 
			int[] indicesToGet = IntStream.rangeClosed(lowestColumnIndex, highestColumnIndex).toArray();
			return clampColumns(indicesToGet, lowerBound, upperBound);
		}

		public DataFrame clampColumns(int lowestColumnIndex, int highestColumnIndex, LocalDate lowerBound, LocalDate upperBound) { 
			int[] indicesToGet = IntStream.rangeClosed(lowestColumnIndex, highestColumnIndex).toArray();
			return clampColumns(indicesToGet, lowerBound, upperBound);
		}
		
		public DataFrame clampRow(int rowIndex, int lowerBound, int upperBound) { 
			return clampRow(rowIndex, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampRow(int rowIndex, double lowerBound, double upperBound) { 
			DataItem[] row = this.getRowAsDataItemArray(rowIndex);
			for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
				row[columnIndex].clamp(lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampRow(int rowIndex, LocalDate lowerBound, LocalDate upperBound) { 
			DataItem[] row = this.getRowAsDataItemArray(rowIndex);
			for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
				row[columnIndex].clamp(lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampRow(String rowName, int lowerBound, int upperBound) { 
			return clampRow(rowName, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampRow(String rowName, double lowerBound, double upperBound) { 
			int rowIndex = this.rowNames.indexOf(rowName);
			return clampRow(rowIndex, lowerBound, upperBound);
		}

		public DataFrame clampRow(String rowName, LocalDate lowerBound, LocalDate upperBound) { 
			int rowIndex = this.rowNames.indexOf(rowName);
			return clampRow(rowIndex, lowerBound, upperBound);
		}

		public DataFrame clampRows(int[] rowIndices, int lowerBound, int upperBound) { 
			return clampRows(rowIndices, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampRows(int[] rowIndices, double lowerBound, double upperBound) { 
			for (int rowCount = 0; rowCount < rowIndices.length; rowCount++) {
				clampRow(rowIndices[rowCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampRows(int[] rowIndices, LocalDate lowerBound, LocalDate upperBound) { 
			for (int rowCount = 0; rowCount < rowIndices.length; rowCount++) {
				clampRow(rowIndices[rowCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampRows(String[] rowNames, int lowerBound, int upperBound) { 
			return clampRows(rowNames, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampRows(String[] rowNames, double lowerBound, double upperBound) { 
			for (int rowCount = 0; rowCount < rowNames.length; rowCount++) {
				clampRow(rowNames[rowCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampRows(String[] rowNames, LocalDate lowerBound, LocalDate upperBound) { 
			for (int rowCount = 0; rowCount < rowNames.length; rowCount++) {
				clampRow(rowNames[rowCount], lowerBound, upperBound);
			}
			return this;
		}

		public DataFrame clampRows(ArrayList<String> rowNames, int lowerBound, int upperBound) { 
			return clampRows(rowNames.toArray(new String[0]), lowerBound, upperBound);
		}

		public DataFrame clampRows(ArrayList<String> rowNames, double lowerBound, double upperBound) { 
			return clampRows(rowNames.toArray(new String[0]), lowerBound, upperBound);
		}

		public DataFrame clampRows(ArrayList<String> rowNames, LocalDate lowerBound, LocalDate upperBound) { 
			return clampRows(rowNames.toArray(new String[0]), lowerBound, upperBound);
		}
		
		public DataFrame clampRows(int lowestRowIndex, int highestRowIndex, int lowerBound, int upperBound) { 
			return clampRows(lowestRowIndex, highestRowIndex, (double)lowerBound, (double)upperBound);
		}

		public DataFrame clampRows(int lowestRowIndex, int highestRowIndex, double lowerBound, double upperBound) { 
			int[] indicesToGet = IntStream.rangeClosed(lowestRowIndex, highestRowIndex).toArray();
			return clampRows(indicesToGet, lowerBound, upperBound);
		}

		public DataFrame clampRows(int lowestRowIndex, int highestRowIndex, LocalDate lowerBound, LocalDate upperBound) { 
			int[] indicesToGet = IntStream.rangeClosed(lowestRowIndex, highestRowIndex).toArray();
			return clampRows(indicesToGet, lowerBound, upperBound);
		}
	
	public DataFrame lessThan(DataFrame df) {
		if (this.sameShape(df)) { 			
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
					boolean lessThan = this.getValue(colCount, rowCount).lessThan(df.getValue(colCount, rowCount));
					newDF.setValue(colCount, rowCount, lessThan);
				}	
			}
			return newDF;
		}
		return null;
	}
	
	public DataFrame lessThan(DataItem value) {
		return lessThan(value.getValueConvertedToDouble());
	}
	
	public DataFrame lessThan(int value) {
		return lessThan((double) value);
	}
	
	public DataFrame lessThan(double value) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean lessThan = this.getValue(colCount, rowCount).lessThan(value);
				newDF.setValue(colCount, rowCount, lessThan);
			}	
		}
		return newDF;
	}
	
	public DataFrame lessThan(float value) {
		return lessThan((double) value);
	}
	
	public DataFrame before(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(date);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	
	public DataFrame lessThanOrEqual(DataFrame df) {
		if (this.sameShape(df)) { 
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
					boolean lessThan = this.getValue(colCount, rowCount).lessThan(df.getValue(colCount, rowCount));
					boolean equal = this.getValue(colCount, rowCount).equal(df.getValue(colCount, rowCount));
					newDF.setValue(colCount, rowCount, lessThan || equal);
				}	
			}
			return newDF;
		}
		return null;
	}
	
	public DataFrame lessThanOrEqual(DataItem value) {
		return lessThanOrEqual(value.getValueConvertedToDouble());
	}
	
	public DataFrame lessThanOrEqual(int value) {
		return lessThanOrEqual((double) value);
	}
	
	public DataFrame lessThanOrEqual(double value) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean lessThan = this.getValue(colCount, rowCount).lessThan(value);
				boolean equal = this.getValue(colCount, rowCount).equal(value);
				newDF.setValue(colCount, rowCount, lessThan || equal);
			}	
		}
		return newDF;
	}
	
	public DataFrame lessThanOrEqual(float value) {
		return lessThanOrEqual((double) value);
	}
	
	public DataFrame beforeOrSame(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(date);
				boolean sameDate = this.getValue(colCount, rowCount).sameDate(date);
				newDF.setValue(colCount, rowCount, before || sameDate);
			}	
		}
		return newDF;
	}
	
	public DataFrame greaterThan(DataFrame df) {
		if (this.sameShape(df)) { 	
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
					boolean greaterThan = this.getValue(colCount, rowCount).greaterThan(df.getValue(colCount, rowCount));
					newDF.setValue(colCount, rowCount, greaterThan);
				}	
			}
			return newDF;
		}
		return null;
	}
	
	public DataFrame greaterThan(DataItem value) {
		return greaterThan(value.getValueConvertedToDouble());
	}
	
	public DataFrame greaterThan(int value) {
		return greaterThan((double) value);
	}
	
	public DataFrame greaterThan(double value) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean greaterThan = this.getValue(colCount, rowCount).greaterThan(value);
				newDF.setValue(colCount, rowCount, greaterThan);
			}	
		}
		return newDF;
	}
	
	public DataFrame greaterThan(float value) {
		return greaterThan((double) value);
	}
	
	public DataFrame after(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).after(date);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	
	public DataFrame greaterThanOrEqual(DataFrame df) {
		if (this.sameShape(df)) { 		
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
					boolean greaterThan = this.getValue(colCount, rowCount).greaterThan(df.getValue(colCount, rowCount));
					boolean equal = this.getValue(colCount, rowCount).equal(df.getValue(colCount, rowCount));
					newDF.setValue(colCount, rowCount, greaterThan || equal);
				}	
			}
			return newDF;
		}
		return null;
	}
	
	public DataFrame greaterThanOrEqual(DataItem value) {
		return greaterThanOrEqual(value.getValueConvertedToDouble());
	}
	
	public DataFrame greaterThanOrEqual(int value) {
		return greaterThanOrEqual((double) value);
	}
	
	public DataFrame greaterThanOrEqual(double value) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean greaterThan = this.getValue(colCount, rowCount).greaterThan(value);
				boolean equal = this.getValue(colCount, rowCount).equal(value);
				newDF.setValue(colCount, rowCount, greaterThan || equal);
			}	
		}
		return newDF;
	}
	
	public DataFrame greaterThanOrEqual(float value) {
		return greaterThanOrEqual((double) value);
	}
	
	public DataFrame afterOrSame(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean after = this.getValue(colCount, rowCount).after(date);
				boolean sameDate = this.getValue(colCount, rowCount).sameDate(date);
				newDF.setValue(colCount, rowCount, after || sameDate);
			}	
		}
		return newDF;
	}
	
	public DataFrame elementwiseEqual(DataFrame df) {
		if (this.sameShape(df)) { 		
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
				for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
					boolean equal = this.getValue(colCount, rowCount).equal(df.getValue(colCount, rowCount));
					newDF.setValue(colCount, rowCount, equal);
				}	
			}
			return newDF;
		}
		return null;
	}
	
	public DataFrame elementwiseEqual(DataItem value) {
		return elementwiseEqual(value.getValueConvertedToDouble());
	}
	
	public DataFrame elementwiseEqual(int value) {
		return elementwiseEqual((double) value);
	}
	
	public DataFrame elementwiseEqual(double value) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean equal = this.getValue(colCount, rowCount).equal(value);
				newDF.setValue(colCount, rowCount, equal);
			}	
		}
		return newDF;
	}
	
	public DataFrame elementwiseEqual(float value) {
		return elementwiseEqual((double) value);
	}
	
	public DataFrame sameDate(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumCols(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean equal = this.getValue(colCount, rowCount).sameDate(date);
				newDF.setValue(colCount, rowCount, equal);
			}	
		}
		return newDF;
	}
	
	public DataFrame elementwiseNotEqual(DataFrame df) {
		return elementwiseEqual(df).negate();
	}
	
	public DataFrame elementwiseNotEqual(DataItem value) {
		return elementwiseEqual(value).negate();
	}
	
	public DataFrame elementwiseNotEqual(int value) {
		return elementwiseEqual(value).negate();
	}
	
	public DataFrame elementwiseNotEqual(double value) {
		return elementwiseEqual(value).negate();
	}
	
	public DataFrame elementwiseNotEqual(float value) {
		return elementwiseEqual(value).negate();
	}
	
	public DataFrame differentDate(LocalDate date) {
		return sameDate(date).negate();
	}
	
	

	
	// ------------------------
	// ------ True/False ------
	// ------------------------
	
	public Boolean allTrue() {
		return allTrueInColumns(0, this.getNumCols() - 1);
	}
	public Boolean allFalse() {
		return allFalseInColumns(0, this.getNumCols() - 1);
	}
	public Boolean anyTrue() {
		return anyTrueInColumns(0, this.getNumCols() - 1);
	}
	public Boolean anyFalse() {
		return anyFalseInColumns(0, this.getNumCols() - 1);
	}
	
	// ------ All True in Column ------
	public Boolean allTrueInColumn(int index) {
		boolean[] column = this.getColumnAsBooleanArray(index);
		for (int rowNum = 0; rowNum < column.length; rowNum++) {
			if (!column[rowNum]) {
				return false;
			}
		}
		return true;
	}
	
	public Boolean allTrueInColumn(String name) {
		int index = this.columnNames.indexOf(name);
		return allTrueInColumn(index);
	}
	
	public Boolean allTrueInColumns(int[] indices) {
		for (int indexCount = 0; indexCount < indices.length; indexCount++) {
			if (!allTrueInColumn(indices[indexCount])) {
				return false;
			}
		}
		return true;
	}
	
	public Boolean allTrueInColumns(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return allTrueInColumns(indices);
	}
	
	public Boolean allTrueInColumns(ArrayList<String> names) {
		return allTrueInColumns(names.toArray(new String[0]));
	}
	
	public Boolean allTrueInColumns(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return allTrueInColumns(indicesToGet);
	}
	
	// ------ Any True in Column ------
	public Boolean anyTrueInColumn(int index) {
		boolean[] column = this.getColumnAsBooleanArray(index);
		for (int rowNum = 0; rowNum < column.length; rowNum++) {
			if (column[rowNum]) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean anyTrueInColumn(String name) {
		int index = this.columnNames.indexOf(name);
		return anyTrueInColumn(index);
	}
	
	public Boolean anyTrueInColumns(int[] indices) {
		for (int indexCount = 0; indexCount < indices.length; indexCount++) {
			if (anyTrueInColumn(indices[indexCount])) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean anyTrueInColumns(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return anyTrueInColumns(indices);
	}
	
	public Boolean anyTrueInColumns(ArrayList<String> names) {
		return anyTrueInColumns(names.toArray(new String[0]));
	}
	
	public Boolean anyTrueInColumns(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return anyTrueInColumns(indicesToGet);
	}
	
	// ------ All False in Column ------
	public Boolean allFalseInColumn(int index) {
		return !anyTrueInColumn(index);
	}
	
	public Boolean allFalseInColumn(String name) {
		return !anyTrueInColumn(name);
	}
	
	public Boolean allFalseInColumns(int[] indices) {
		return !anyTrueInColumns(indices);
	}
	
	public Boolean allFalseInColumns(String[] names) {
		return !anyTrueInColumns(names);
	}
	
	public Boolean allFalseInColumns(ArrayList<String> names) {
		return !anyTrueInColumns(names);
	}
	
	public Boolean allFalseInColumns(int lowerBound, int upperBound) {
		return !anyTrueInColumns(lowerBound, upperBound);
	}
	
	// ------ Any False in Column ------
	public Boolean anyFalseInColumn(int index) {
		return !allTrueInColumn(index);
	}
	
	public Boolean anyFalseInColumn(String name) {
		return !allTrueInColumn(name);
	}
	
	public Boolean anyFalseInColumns(int[] indices) {
		return !allTrueInColumns(indices);
	}
	
	public Boolean anyFalseInColumns(String[] names) {
		return !allTrueInColumns(names);
	}
	
	public Boolean anyFalseInColumns(ArrayList<String> names) {
		return !allTrueInColumns(names);
	}
	
	public Boolean anyFalseInColumns(int lowerBound, int upperBound) {
		return !allTrueInColumns(lowerBound, upperBound);
	}
	
	// ------ All True in Row ------
	public Boolean allTrueInRow(int index) {
		boolean[] row = this.getRowAsBooleanArray(index);
		for (int colNum = 0; colNum < row.length; colNum++) {
			if (!row[colNum]) {
				return false;
			}
		}
		return true;
	}
	
	public Boolean allTrueInRow(String name) {
		int index = this.rowNames.indexOf(name);
		return allTrueInRow(index);
	}
	
	public Boolean allTrueInRows(int[] indices) {
		for (int indexCount = 0; indexCount < indices.length; indexCount++) {
			if (!allTrueInRow(indices[indexCount])) {
				return false;
			}
		}
		return true;
	}
	
	public Boolean allTrueInRows(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return allTrueInRows(indices);
	}
	
	public Boolean allTrueInRows(ArrayList<String> names) {
		return allTrueInRows(names.toArray(new String[0]));
	}
	
	public Boolean allTrueInRows(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return allTrueInRows(indicesToGet);
	}
	
	// ------ Any True in Row ------
	public Boolean anyTrueInRow(int index) {
		boolean[] row = this.getRowAsBooleanArray(index);
		for (int colNum = 0; colNum < row.length; colNum++) {
			if (row[colNum]) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean anyTrueInRow(String name) {
		int index = this.rowNames.indexOf(name);
		return anyTrueInRow(index);
	}
	
	public Boolean anyTrueInRows(int[] indices) {
		for (int indexCount = 0; indexCount < indices.length; indexCount++) {
			if (anyTrueInRow(indices[indexCount])) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean anyTrueInRows(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return anyTrueInRows(indices);
	}
	
	public Boolean anyTrueInRows(ArrayList<String> names) {
		return anyTrueInRows(names.toArray(new String[0]));
	}
	
	public Boolean anyTrueInRows(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return anyTrueInRows(indicesToGet);
	}
	
	// ------ All False in Row ------
	public Boolean allFalseInRow(int index) {
		return !anyTrueInRow(index);
	}
	
	public Boolean allFalseInRow(String name) {
		return !anyTrueInRow(name);
	}
	
	public Boolean allFalseInRows(int[] indices) {
		return !anyTrueInRows(indices);
	}
	
	public Boolean allFalseInRows(String[] names) {
		return !anyTrueInRows(names);
	}
	
	public Boolean allFalseInRows(ArrayList<String> names) {
		return !anyTrueInRows(names);
	}
	
	public Boolean allFalseInRows(int lowerBound, int upperBound) {
		return !anyTrueInRows(lowerBound, upperBound);
	}
	
	// ------ Any False in Row ------
	public Boolean anyFalseInRow(int index) {
		return !allTrueInRow(index);
	}
	
	public Boolean anyFalseInRow(String name) {
		return !allTrueInRow(name);
	}
	
	public Boolean anyFalseInRows(int[] indices) {
		return !allTrueInRows(indices);
	}
	
	public Boolean anyFalseInRows(String[] names) {
		return !allTrueInRows(names);
	}
	
	public Boolean anyFalseInRows(ArrayList<String> names) {
		return !allTrueInRows(names);
	}
	
	public Boolean anyFalseInRows(int lowerBound, int upperBound) {
		return allTrueInRows(lowerBound, upperBound);
	}
	
	
	public boolean sameShape(DataFrame df) {
		return ((this.getNumCols() == df.getNumCols()) && (this.getNumRows() == df.getNumRows())); 
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

		if (colNamesToAdd.size() != this.columnNames.size()) {
			System.out.println("Number of column names must equal number of columns");
			return;
		}

		String[] mangledColNames = CommonArray.mangle(colNamesToAdd);
		this.columnNames = CommonArray.convertStringArrayToArrayList(mangledColNames);
	}

	public void setColumnNames(String[] colNamesToUse) {
		setColumnNames(new ArrayList<String>(Arrays.asList(colNamesToUse)));

	}

	public void resetColumnNames() {
		this.columnNames = CommonArray.generateIncreasingSequence(this.columnNames.size());
	}

	private String generateUnusedColumnName() {
		String columnName = "";
		int possibleName = 1;
		while (columnName == "") {
			if (!this.columnNames.contains(String.valueOf(possibleName))) {
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
	
	// Fix duplication of error checks
	public void setValue(int colNum, int rowNum, Object value, StorageType type) {
		if (colNum >= this.columnNames.size()) {
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

		this.data.get(colNum).get(rowNum).setType(type);

	}

	public void setValue(int colNum, int rowNum, Object value) {
		if (colNum >= this.columnNames.size()) {
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
		if (colNum >= this.columnNames.size()) {
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
		return this.columnNames;
	}

	public ArrayList<String> getRowNames() {
		return this.rowNames;
	}

	public void addPrefixToColumnNames(String prefix) {
		addPrefixToColumnNames(0, this.getNumCols() - 1, prefix);
	}
	
	public void addSuffixToColumnNames(String suffix) {
		addSuffixToColumnNames(0, this.getNumCols() - 1, suffix);
	}
	
	public void addPrefixToColumnNames(int lowestIndex, int highestIndex, String prefix) {
		for(int colNum = lowestIndex; colNum <= highestIndex; colNum++) {
			this.columnNames.set(colNum, prefix + this.columnNames.get(colNum));
		}
	}
	
	public void addSuffixToColumnNames(int lowestIndex, int highestIndex, String suffix) {
		for(int colNum = lowestIndex; colNum <= highestIndex; colNum++) {
			this.columnNames.set(colNum, this.columnNames.get(colNum) + suffix);
		}
	}
	
	public void addPrefixToRowNames(String prefix) {
		addPrefixToRowNames(0, this.getNumRows() - 1, prefix);
	}
	
	public void addSuffixToRowNames(String suffix) {
		addSuffixToRowNames(0, this.getNumRows() - 1, suffix);
	}
	
	public void addPrefixToRowNames(int lowestIndex, int highestIndex, String prefix) {
		for(int rowNum = lowestIndex; rowNum <= highestIndex; rowNum++) {
			this.rowNames.set(rowNum, prefix + this.rowNames.get(rowNum));
		}
	}
	
	public void addSuffixToRowNames(int lowestIndex, int highestIndex, String suffix) {
		for(int rowNum = lowestIndex; rowNum <= highestIndex; rowNum++) {
			this.rowNames.set(rowNum, this.rowNames.get(rowNum) + suffix);
		}
	}
	
	public ArrayList<ArrayList<DataItem>> getData() {
		return this.data;
	}
	

	public int getNumRows() {
		return this.rowNames.size();
	}

	public int getNumCols() {
		return this.columnNames.size();
	}

	public DataFrame isNull() {
		DataFrame nullDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());

		for (int colCount = 0; colCount < nullDF.columnNames.size(); colCount++) {
			for (int rowCount = 0; rowCount < nullDF.rowNames.size(); rowCount++) {
				boolean isNull = (this.data.get(colCount).get(rowCount).getType() == StorageType.Null);
				nullDF.setValue(colCount, rowCount, isNull);
			}
		}

		return nullDF;
	}
	
	public DataFrame isNotNull() {
		return this.isNull().negate();
	}
	

	
	public DataFrame joinToTheRight(DataFrame newDF) {
		if (this.getNumRows() != newDF.getNumRows()) {
			return this;
		}
		
		return null;
	}
	
	public DataFrame joinToTheLeft(DataFrame newDF) {
		return null;
	}
	
	public DataFrame joinBelow(DataFrame newDF) {
		return null;
	}
	
	public DataFrame joinAbove(DataFrame newDF) {
		return null;
	}
	
	private boolean lessThan(String str1, String str2) {
		return (str1.compareToIgnoreCase(str2) < 0);
	}
	
	public void sortColumnsAlphabetically(boolean ascending) {
		sortColumnsAlphabetically(ascending, 0, this.getNumCols());
	}
	
	public void sortColumnsAlphabetically(boolean ascending, int lowerBound, int upperBound) {
//		int innerCounter;
//	    for (int outerCounter = lowerBound; outerCounter < upperBound; outerCounter++) {
//	        String savedColumnName = this.columnNames.get(outerCounter);
//	        DataItem[] savedColumn = this.getColumnAsDataItemArray(outerCounter);
//	        for(innerCounter = outerCounter; innerCounter > 0 && (ascending ? lessThan(savedColumnName,this.columnNames.get(innerCounter-1)) : !lessThan(savedColumnName,this.columnNames.get(innerCounter-1))); innerCounter--) {
//	        	this.columnNames.set(innerCounter, this.columnNames.get(innerCounter - 1));
//	        }
//	        this.columnNames.set(innerCounter, savedColumnName);
//	    }
	    
	}
	
	public void sortRowsAlphabetically(boolean ascending) {
		
	}
	
	public void sortRowsAlphabetically(boolean ascending, int lowerBound, int upperBound) {
		
	}

	public void transpose() {
		ArrayList<ArrayList<DataItem>> transpose = new ArrayList<ArrayList<DataItem>>();

		for (int rowCount = 0; rowCount < this.rowNames.size(); rowCount++) {
			ArrayList<DataItem> newCol = new ArrayList<DataItem>();
			for (int colCount = 0; colCount < this.columnNames.size(); colCount++) {
				newCol.add(this.data.get(colCount).get(rowCount));
			}
			transpose.add(newCol);
		}

		this.data = transpose;

		ArrayList<String> tempList;
		tempList = this.columnNames;
		this.columnNames = this.rowNames;
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
//			System.out.println("item = " + item + ", class = " + item.getClass());
			list.add(new DataItem(item));
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

		int[] largestColWidths = new int[this.columnNames.size()];
		for (int i = 0; i < largestColWidths.length; i++) {
			largestColWidths[i] = String.valueOf(this.columnNames.get(i)).length();
		}

		for (int colCount = 0; colCount < this.columnNames.size(); colCount++) {
			for (int rowCount = 0; rowCount < this.data.get(colCount).size(); rowCount++) {
				int lengthOfValue = String.valueOf(data.get(colCount).get(rowCount)).length();
				if (lengthOfValue > largestColWidths[colCount]) {
					largestColWidths[colCount] = lengthOfValue;
				}
			}
		}

		int totalColWidth = 0;
		for (int i = 0; i < columnNames.size(); i++) {
			totalColWidth += largestColWidths[i] + dataPadding;
		}

		int totalGridWidth = (largestIndexWidth + indexPadding) + (totalColWidth) + this.columnNames.size();
		int totalGridHeight = 1 + 1 + rowNames.size();
		printGrid = new String[totalGridHeight][totalGridWidth];
		colToWriteNext = new int[totalGridHeight];

		for (int i = 0; i < colToWriteNext.length; i++) {
			colToWriteNext[i] = 0;
		}

		// Top left corner
		addWhiteSpace(0, largestIndexWidth + indexPadding);

		// Top column names
		for (int colCount = 0; colCount < columnNames.size(); colCount++) {
			writeChar(0, "|");
			int numberOfWhiteSpace = ((largestColWidths[colCount] + dataPadding) - columnNames.get(colCount).length());

			writeWholeBlock(columnNames.get(colCount), 0, numberOfWhiteSpace, rightAlign);
		}

		// Row of dashes
		for (int i = 0; i < largestIndexWidth + indexPadding; i++) {
			writeChar(1, "-");
		}
		for (int colCount = 0; colCount < columnNames.size(); colCount++) {
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

			for (int colCount = 0; colCount < this.columnNames.size(); colCount++) {
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
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.getColumnNames().clone(), (ArrayList<String>)this.getRowNames().clone());
		ArrayList<ArrayList<DataItem>> newData = new ArrayList<ArrayList<DataItem>>();
		for (int colCount = 0; colCount < this.data.size(); colCount++) {
			ArrayList<DataItem> newCol = new ArrayList<DataItem>();
			for (int rowCount = 0; rowCount < this.data.get(colCount).size(); rowCount++) {
				newCol.add(data.get(colCount).get(rowCount).clone());
			}
			newData.add(newCol);
		}
		
		newDF.setData(newData);
				
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
			for (int colNum = 0; colNum < columnNames.size(); colNum++) {
				row.add(data.get(colNum).get(current));
			}
			current++;
			return row;
		}

	}

}
