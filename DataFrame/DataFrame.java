package thesis.DataFrame;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import thesis.Common.CommonArray;
import thesis.Common.CommonFiles;
import thesis.Common.CommonMath;
import thesis.DataFrame.DataItem.StorageType;
import thesis.Exceptions.ColumnNameException;
import thesis.Exceptions.DataFrameOutOfBoundsException;
import thesis.Exceptions.DataFrameShapeException;
import thesis.Exceptions.RowNameException;

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

	// Create a DF with a single specified value
	public DataFrame(int numColumns, int numRows, DataItem fill) {
		this(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), fill);
	}
	
	public <T> DataFrame(int numColumns, int numRows, T fill) {
		this(numColumns, numRows, fill, DataItem.getStorageTypeOfObject(fill));
	}
	
	public <T> DataFrame(String[] columnNames, String[] rowNames, T fill) {
		this(columnNames,rowNames, fill, DataItem.getStorageTypeOfObject(fill));
	}
	
	public DataFrame(String[] columnNames, String[] rowNames, DataItem fill) {
		this(columnNames,rowNames, fill, fill.getType());
	}
	
	public <T> DataFrame(List<String> columnNames, List<String> rowNames, T fill) {
		this(columnNames, rowNames, fill, DataItem.getStorageTypeOfObject(fill));
	}

	public DataFrame(int numColumns, int numRows, Object fill, StorageType type) {
		this(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), fill, type);	
	}
	
	
	public <T> DataFrame(String[] columnNames, String[] rowNames, T fill, StorageType type) {
		this(Arrays.asList(columnNames), Arrays.asList(rowNames), fill, type);
	}
	
	public DataFrame(List<String> columnNames, List<String> rowNames, DataItem fill) {
		this.columnNames = CommonArray.convertStringArrayToArrayList(CommonArray.mangle(columnNames));
		this.rowNames = CommonArray.convertStringArrayToArrayList(CommonArray.mangle(rowNames));
		this.data = new ArrayList<ArrayList<DataItem>>();
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			ArrayList<DataItem> column = new ArrayList<DataItem>();
			for (int rowCount = 0; rowCount < rowNames.size(); rowCount++) {
				column.add(fill);
			}
			this.data.add(column);
		}
	}
	
	public <T> DataFrame(List<String> columnNames, List<String> rowNames, T fill, StorageType type) {
		this(columnNames, rowNames, new DataItem(fill, type));
	}
	
	// Create an empty DF with rows and columns and null values
	public DataFrame(List<String> colNames, List<String> rowNames) {
		this();
		String[] colNamesToAdd = CommonArray.mangle(colNames);
		String[] rowNamesToAdd = CommonArray.mangle(rowNames);
		this.setColumnNames(colNamesToAdd);
		this.setRowNames(rowNamesToAdd);
		for (int colNum = 0; colNum < colNames.size(); colNum++) {
			this.data.add(new ArrayList<DataItem>());
			for (int rowNum = 0; rowNum < rowNames.size(); rowNum++) {
				this.data.get(colNum).add(new DataItem());
			}
		}
	}

	// Create an empty DF with rows and columns and null values
	public DataFrame(String[] colNames, String[] rowNames) {
		this(Arrays.asList(colNames), Arrays.asList(rowNames));
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
	public <T> DataFrame(Map<String, List<T>> map, boolean isRow) {
		this();
		if (isRow) {
			appendRows(map);
		} else {			
			appendColumns(map);
		}

	}

	/**
	 * <pre>
	 * Create a DF from list of hashmaps 
	 * For example: 
	 *     maps = [ 
	 *         map1: { 
	 *             "one": 1, 
	 *             "two": 2, 
	 *             "three": 3 
	 *         }, 
	 *         map2: { 
	 *             "one": 10, 
	 *             "two": 20, 
	 *             "three": 30 
	 *         }
	 *     ]
	 * 
	 * Becomes: 
	 *       | one| two| three 
	 *     --+----+----+------ 
	 *      0|   1|   2|     3 
	 *      1|  10|  20|    30
	 * </pre>
	 */
	public DataFrame(List<Map<String, Object>> maps, boolean isRow) {
		this();
		ArrayList<String> cumulativeNames = new ArrayList<String>();
		for (Map<String, Object> map: maps) {
			for (String name: map.keySet()) {
				if (!cumulativeNames.contains(name)) {
					cumulativeNames.add(name);
				}
			}
		}
		if (isRow) {
			this.setColumnNames(cumulativeNames);
		} else {
			this.setRowNames(cumulativeNames);
		}
		for (Map<String, Object> map : maps) {
			if (isRow) {				
				appendRow(map);
			} else {
				appendColumn(map);
			}
		}
	
		

	}

	public DataFrame(DataFrame df) {
		this();
		this.columnNames = df.getColumnNames();
		this.rowNames = df.getRowNames();
		this.data = df.getDataAs2DDataItemArrayList();
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
	public <T> DataFrame(String name, List<T> list, boolean isRow) {
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
	public <T> DataFrame(String name, T[] array, boolean isRow) {
		this(name, new ArrayList<T>(Arrays.asList(array)), isRow);
	}
	
	public DataFrame(String name, int[] array, boolean isRow) {
		this();

		if (isRow == true) {
			appendRow(name, array);
		} else {
			appendColumn(name, array);
		}
	}
	
	public DataFrame(String name, float[] array, boolean isRow) {
		this();

		if (isRow == true) {
			appendRow(name, array);
		} else {
			appendColumn(name, array);
		}
	}
	
	public DataFrame(String name, double[] array, boolean isRow) {
		this();

		if (isRow == true) {
			appendRow(name, array);
		} else {
			appendColumn(name, array);
		}
	}
	
	public DataFrame(String name, boolean[] array, boolean isRow) {
		this();

		if (isRow == true) {
			appendRow(name, array);
		} else {
			appendColumn(name, array);
		}
	}

	/*
	 * Create a DF object form multiple columns 
	 * 
	 * For example if: 
	 * 
	 * 		names = ["one", "two", "three"] 
	 * 		lists = Arrays.asList( 
	 * 			Arrays.asList(1, 2, 3),
	 * 			Arrays.asList(4, 5, 6), 
	 * 			Arrays.asList(7, 8, 9), 
	 * 		), 
	 * 		isRow = false
	 * 
	 * Result is: 
	 * 
	 * 			| one| two| three 
	 * 		  --+----+----+------ 
	 * 		   0|   1|   4|     7 
	 *  	   1|   2|   5|     8 
	 *  	   2|   3|   6|     9
	 * 
	 * If isRow = true. 
	 * Result is:
	 * 
	 * 			| 0| 1| 2
	 * 	  ------+--+--+--
	 *       one| 1| 2| 3
	 *       two| 4| 5| 6
	 *     three| 7| 8| 9
	 */
	public <T> DataFrame(List<String> names, List<List<T>> lists, boolean isRow) {
		this();
		try {	
			if (names.size() != lists.size()) {
				throw new DataFrameShapeException("Number of names = " + names.size() + ", number of lists = " + lists.size());
			}
			for (int i = 0; i < names.size(); i++) {
				if (isRow == true) {
					appendRow(names.get(i), lists.get(i));
				} else {
					appendColumn(names.get(i), lists.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public <T> DataFrame(String[] names, T[][] lists, boolean isRow) {
		this();
		try {	
			if (names.length != lists.length) {
				throw new DataFrameShapeException("Number of names = " + names.length + ", number of lists = " + lists.length);
			}
			for (int i = 0; i < names.length; i++) {
				if (isRow == true) {
					appendRow(names[i], lists[i]);
				} else {
					appendColumn(names[i], lists[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataFrame(String[] names, int[][] lists, boolean isRow) {
		this();
		try {	
			if (names.length != lists.length) {
				throw new DataFrameShapeException("Number of names = " + names.length + ", number of lists = " + lists.length);
			}
			for (int i = 0; i < names.length; i++) {
				if (isRow == true) {
					appendRow(names[i], lists[i]);
				} else {
					appendColumn(names[i], lists[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataFrame(String[] names, float[][] lists, boolean isRow) {
		this();
		try {	
			if (names.length != lists.length) {
				throw new DataFrameShapeException("Number of names = " + names.length + ", number of lists = " + lists.length);
			}
			for (int i = 0; i < names.length; i++) {
				if (isRow == true) {
					appendRow(names[i], lists[i]);
				} else {
					appendColumn(names[i], lists[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataFrame(String[] names, double[][] lists, boolean isRow) {
		this();
		try {	
			if (names.length != lists.length) {
				throw new DataFrameShapeException("Number of names = " + names.length + ", number of lists = " + lists.length);
			}
			for (int i = 0; i < names.length; i++) {
				if (isRow == true) {
					appendRow(names[i], lists[i]);
				} else {
					appendColumn(names[i], lists[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataFrame(String[] names, boolean[][] lists, boolean isRow) {
		this();
		try {	
			if (names.length != lists.length) {
				throw new DataFrameShapeException("Number of names = " + names.length + ", number of lists = " + lists.length);
			}
			for (int i = 0; i < names.length; i++) {
				if (isRow == true) {
					appendRow(names[i], lists[i]);
				} else {
					appendColumn(names[i], lists[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
					// If there are row names, remove the first value
					values = Arrays.copyOfRange(values, 1, values.length);
				}

				// Extract the header row
				if (rowNum == 0) {
					String[] mangledColumns = CommonArray.mangle(values);
					this.columnNames = CommonArray.convertStringArrayToArrayList(mangledColumns);

				} else {
					if (values.length > 0) {
						appendRow(values);
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
	
	public static DataFrame emptyDataFrame() {
		return new DataFrame();
	}
	
	public static DataFrame zeros(int numColumns, int numRows) {
		return new DataFrame(numColumns, numRows, 0);
	}
	
	public static DataFrame zeros(String[] columnNames, String[] rowNames) {
		return new DataFrame(columnNames, rowNames, 0);
	}
	
	public static DataFrame zeros(List<String> columnNames, List<String> rowNames) {
		return new DataFrame(columnNames, rowNames, 0);
	}
	
	public static DataFrame zerosLike(DataFrame otherDF) {
		return zeros(otherDF.getColumnNames(), otherDF.getRowNames());
	}
	
	public static <T> DataFrame zerosLike(T[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame zerosLike(byte[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame zerosLike(short[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame zerosLike(int[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame zerosLike(long[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame zerosLike(float[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame zerosLike(double[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame zerosLike(boolean[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}

	public static DataFrame zerosLike(char[][] otherDF) {
		return zeros(otherDF.length, otherDF[0].length);
	}
	
	
	public static <T> DataFrame zerosLike(List<List<T>> otherDF) {
		return zeros(otherDF.size(), otherDF.get(0).size());
	}
	
	public static DataFrame ones(int numColumns, int numRows) {
		return new DataFrame(numColumns, numRows, 1);
	}
	
	public static DataFrame ones(String[] columnNames, String[] rowNames) {
		return new DataFrame(columnNames, rowNames, 1);
	}
	
	public static DataFrame ones(List<String> columnNames, List<String> rowNames) {
		return new DataFrame(columnNames, rowNames, 1);
	}

	public static DataFrame onesLike(DataFrame otherDF) {
		return ones(otherDF.getColumnNames(), otherDF.getRowNames());
	}

	public static <T> DataFrame onesLike(T[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame onesLike(byte[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static DataFrame onesLike(short[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static DataFrame onesLike(int[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static DataFrame onesLike(long[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static DataFrame onesLike(float[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static DataFrame onesLike(double[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static DataFrame onesLike(boolean[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static DataFrame onesLike(char[][] otherDF) {
		return ones(otherDF.length, otherDF[0].length);
	}

	public static <T> DataFrame onesLike(List<List<T>> otherDF) {
		return ones(otherDF.size(), otherDF.get(0).size());
	}

	public static DataFrame nullValues(int numColumns, int numRows) {
		return new DataFrame(numColumns, numRows, new DataItem());
	}

	public static DataFrame nullValues(String[] columnNames, String[] rowNames) {
		return new DataFrame(columnNames, rowNames, new DataItem());
	}
	
	public static DataFrame nullValues(List<String> columnNames, List<String> rowNames) {
		return new DataFrame(columnNames, rowNames, new DataItem());
	}
	
	public static DataFrame nullValuesLike(DataFrame otherDF) {
		return nullValues(otherDF.getColumnNames(), otherDF.getRowNames());
	}

	public static <T> DataFrame nullValuesLike(T[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}
	
	public static DataFrame nullValuesLike(byte[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}

	public static DataFrame nullValuesLike(short[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}

	public static DataFrame nullValuesLike(int[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}

	public static DataFrame nullValuesLike(long[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}

	public static DataFrame nullValuesLike(float[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}

	public static DataFrame nullValuesLike(double[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}

	public static DataFrame nullValuesLike(boolean[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}

	public static DataFrame nullValuesLike(char[][] otherDF) {
		return nullValues(otherDF.length, otherDF[0].length);
	}
	
	public static <T> DataFrame nullValuesLike(List<List<T>> otherDF) {
		return nullValues(otherDF.size(), otherDF.get(0).size());
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, DataItem value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static <T> DataFrame dataFrameWithValue(int numColumns, int numRows, T value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, Object value, StorageType type) {
		return new DataFrame(numColumns, numRows, value, type);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, int value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, float value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, double value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, boolean value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, String value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, LocalDate value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, LocalDateTime value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, Period value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(int numColumns, int numRows, Duration value) {
		return new DataFrame(numColumns, numRows, value);
	}
	
	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, DataItem value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, Object value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, Object value, StorageType type) {
		return new DataFrame(columnNames, rowNames, value, type);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, int value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, float value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, double value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, boolean value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, String value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, LocalDate value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, LocalDateTime value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, Period value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(String[] columnNames, String[] rowNames, Duration value) {
		return new DataFrame(columnNames, rowNames, value);
	}
	
	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, DataItem value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, Object value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, Object value, StorageType type) {
		return new DataFrame(columnNames, rowNames, value, type);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, int value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, float value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, double value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, boolean value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, String value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, LocalDate value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, LocalDateTime value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, Period value) {
		return new DataFrame(columnNames, rowNames, value);
	}

	public static DataFrame dataFrameWithValue(List<String> columnNames, List<String> rowNames, Duration value) {
		return new DataFrame(columnNames, rowNames, value);
	}
	
	
	public static DataFrame readCSV(String filePath, boolean hasHeaderRow, boolean hasIndexRow) {
		return new DataFrame(filePath, hasHeaderRow, hasIndexRow);
	}
	
	public static DataFrame readCSV(String filePath, boolean hasHeaderRow, boolean hasIndexRow, StorageType[] columnTypes) {
		DataFrame newDF = new DataFrame(filePath, hasHeaderRow, hasIndexRow);
		newDF.setColumnsType(columnTypes);
		return newDF;
	}
	
	public static DataFrame readCSV(String filePath, boolean hasHeaderRow, boolean hasIndexRow, Map<String,StorageType> columnTypes) {
		DataFrame newDF = new DataFrame(filePath, hasHeaderRow, hasIndexRow);
		newDF.setColumnsType(columnTypes);
		return newDF;
	}
	
	
	public static DataFrame uniqueInts(int numColumns, int numRows, int minValue, int maxValue) {
		return DataFrame.uniqueInts(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minValue, maxValue);
	}
	
	public static DataFrame uniqueInts(String[] columnNames, String[] rowNames, int minValue, int maxValue) {
		return DataFrame.uniqueInts(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minValue, maxValue);
	}
	
	public static DataFrame uniqueInts(ArrayList<String> columnNames, ArrayList<String> rowNames, int minValue, int maxValue) {
		int numValues = columnNames.size() * rowNames.size();
		if (maxValue - minValue < numValues) {			
			throw new IllegalArgumentException("Not possible to get unique values from range provided (" + minValue + ", " + maxValue + ")");
		}
		int[] intValues = CommonArray.getUniqueInts(minValue, maxValue);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(intValues);
		
        return df;
	}
	
	public static DataFrame uniqueInts(int numColumns, int numRows) {
		return DataFrame.uniqueInts(numColumns, numRows, 0, numColumns * numRows);
	}

	public static DataFrame uniqueInts(String[] columnNames, String[] rowNames) {
		return DataFrame.uniqueInts(columnNames, rowNames, 0, columnNames.length * rowNames.length);
	}
	
	public static DataFrame uniqueInts(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniqueInts(columnNames, rowNames, 0, columnNames.size() * rowNames.size());
	}

	
	public static DataFrame uniqueDoubles(int numColumns, int numRows, double minValue, double maxValue) {
		return DataFrame.uniqueDoubles(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minValue, maxValue);
	}
	
	public static DataFrame uniqueDoubles(String[] columnNames, String[] rowNames, double minValue, double maxValue) {
		return DataFrame.uniqueDoubles(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minValue, maxValue);
	}
	
	public static DataFrame uniqueDoubles(ArrayList<String> columnNames, ArrayList<String> rowNames, double minValue, double maxValue) {
		int numValues = columnNames.size() * rowNames.size();
		double[] doubleValues = CommonArray.getUniqueDoubles(numValues, minValue, maxValue);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(doubleValues);
		return df;
	}
	
	public static DataFrame uniqueDoubles(int numColumns, int numRows) {
		return DataFrame.uniqueDoubles(numColumns, numRows, 0, 1);
	}
	
	public static DataFrame uniqueDoubles(String[] columnNames, String[] rowNames) {
		return DataFrame.uniqueDoubles(columnNames, rowNames, 0, 1);
	}
	
	public static DataFrame uniqueDoubles(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniqueDoubles(columnNames, rowNames, 0, 1);
	}
	
	public static DataFrame uniqueStrings(int numColumns, int numRows, int stringLength) {
		return DataFrame.uniqueStrings(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), stringLength);
	}
	
	public static DataFrame uniqueStrings(String[] columnNames, String[] rowNames, int stringLength) {
		return DataFrame.uniqueStrings(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), stringLength);
	}
	
	public static DataFrame uniqueStrings(ArrayList<String> columnNames, ArrayList<String> rowNames, int stringLength) {
		int numValues = columnNames.size() * rowNames.size();
		String[] StringValues = CommonArray.getUniqueStrings(numValues, stringLength);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(StringValues);
		return df;
	}
	
	public static DataFrame uniqueStrings(int numColumns, int numRows) {
		return DataFrame.uniqueStrings(numColumns, numRows, 10);
	}
	
	public static DataFrame uniqueStrings(String[] columnNames, String[] rowNames) {
		return DataFrame.uniqueStrings(columnNames, rowNames, 10);
	}
	
	public static DataFrame uniqueStrings(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniqueStrings(columnNames, rowNames, 10);
	}
	
	public static DataFrame uniqueLocalDate(int numColumns, int numRows, LocalDate minLocalDate, LocalDate maxLocalDate) {
		return DataFrame.uniqueLocalDate(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minLocalDate, maxLocalDate);
	}
	
	public static DataFrame uniqueLocalDate(String[] columnNames, String[] rowNames, LocalDate minLocalDate, LocalDate maxLocalDate) {
		return DataFrame.uniqueLocalDate(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minLocalDate, maxLocalDate);
	}
	
	public static DataFrame uniqueLocalDate(ArrayList<String> columnNames, ArrayList<String> rowNames, LocalDate minLocalDate, LocalDate maxLocalDate) {
		int numValues = columnNames.size() * rowNames.size();
		LocalDate[] localDateValues = CommonArray.getUniqueLocalDates(numValues, minLocalDate, maxLocalDate);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(localDateValues);
		return df;
	}
	
	public static DataFrame uniqueLocalDate(int numColumns, int numRows) {
		return DataFrame.uniqueLocalDate(numColumns, numRows, LocalDate.now().minusYears(40), LocalDate.now().plusYears(40));
	}
	
	public static DataFrame uniqueLocalDate(String[] columnNames, String[] rowNames) {
		return DataFrame.uniqueLocalDate(columnNames, rowNames, LocalDate.now().minusYears(40), LocalDate.now().plusYears(40));
	}
	
	public static DataFrame uniqueLocalDate(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniqueLocalDate(columnNames, rowNames, LocalDate.now().minusYears(40), LocalDate.now().plusYears(40));
	}
	
	public static DataFrame uniqueLocalDateTime(int numColumns, int numRows, LocalDateTime minLocalDateTime, LocalDateTime maxLocalDateTime) {
		return DataFrame.uniqueLocalDateTime(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minLocalDateTime, maxLocalDateTime);
	}
	
	public static DataFrame uniqueLocalDateTime(String[] columnNames, String[] rowNames, LocalDateTime minLocalDateTime, LocalDateTime maxLocalDateTime) {
		return DataFrame.uniqueLocalDateTime(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minLocalDateTime, maxLocalDateTime);
	}
	
	public static DataFrame uniqueLocalDateTime(ArrayList<String> columnNames, ArrayList<String> rowNames, LocalDateTime minLocalDateTime, LocalDateTime maxLocalDateTime) {
		int numValues = columnNames.size() * rowNames.size();
		LocalDateTime[] localDateTimeValues = CommonArray.getUniqueLocalDateTimes(numValues, minLocalDateTime, maxLocalDateTime);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(localDateTimeValues);
		return df;
	}
	
	public static DataFrame uniqueLocalDateTime(int numColumns, int numRows) {
		return DataFrame.uniqueLocalDateTime(numColumns, numRows, LocalDateTime.now().minusYears(40), LocalDateTime.now().plusYears(40));
	}
	
	public static DataFrame uniqueLocalDateTime(String[] columnNames, String[] rowNames) {
		return DataFrame.uniqueLocalDateTime(columnNames, rowNames, LocalDateTime.now().minusYears(40), LocalDateTime.now().plusYears(40));
	}
	
	public static DataFrame uniqueLocalDateTime(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniqueLocalDateTime(columnNames, rowNames, LocalDateTime.now().minusYears(40), LocalDateTime.now().plusYears(40));
	}
	
	public static DataFrame uniqueLocalTime(int numColumns, int numRows, LocalTime minLocalTime, LocalTime maxLocalTime) {
		return DataFrame.uniqueLocalTime(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minLocalTime, maxLocalTime);
	}
	
	public static DataFrame uniqueLocalTime(String[] columnNames, String[] rowNames, LocalTime minLocalTime, LocalTime maxLocalTime) {
		return DataFrame.uniqueLocalTime(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minLocalTime, maxLocalTime);
	}
	
	public static DataFrame uniqueLocalTime(ArrayList<String> columnNames, ArrayList<String> rowNames, LocalTime minLocalTime, LocalTime maxLocalTime) {
		int numValues = columnNames.size() * rowNames.size();
		LocalTime[] localTimeValues = CommonArray.getUniqueLocalTimes(numValues, minLocalTime, maxLocalTime);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(localTimeValues);
		return df;
	}
	
	public static DataFrame uniqueLocalTime(int numColumns, int numRows) {
		return DataFrame.uniqueLocalTime(numColumns, numRows, LocalTime.MIN, LocalTime.MAX);
	}
	
	public static DataFrame uniqueLocalTime(String[] columnNames, String[] rowNames) {
		return DataFrame.uniqueLocalTime(columnNames, rowNames, LocalTime.MIN, LocalTime.MAX);
	}
	
	public static DataFrame uniqueLocalTime(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniqueLocalTime(columnNames, rowNames, LocalTime.MIN, LocalTime.MAX);
	}
	
	public static DataFrame uniquePeriod(int numColumns, int numRows, Period minPeriod, Period maxPeriod) {
		return DataFrame.uniquePeriod(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minPeriod, maxPeriod);
	}
	
	public static DataFrame uniquePeriod(String[] columnNames, String[] rowNames, Period minPeriod, Period maxPeriod) {
		return DataFrame.uniquePeriod(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minPeriod, maxPeriod);
	}
	
	public static DataFrame uniquePeriod(ArrayList<String> columnNames, ArrayList<String> rowNames, Period minPeriod, Period maxPeriod) {
		int numValues = columnNames.size() * rowNames.size();
		Period[] periodValues = CommonArray.getUniquePeriods(numValues, minPeriod, maxPeriod);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(periodValues);
		return df;
	}
	
	public static DataFrame uniquePeriod(int numColumns, int numRows) {
		return DataFrame.uniquePeriod(numColumns, numRows, Period.of(0, 0, 1), Period.ofYears(15));
	}
	
	public static DataFrame uniquePeriod(String[] columnNames, String[] rowNames) {
		return DataFrame.uniquePeriod(columnNames, rowNames, Period.of(0, 0, 1), Period.ofYears(15));
	}
	
	public static DataFrame uniquePeriod(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniquePeriod(columnNames, rowNames, Period.of(0, 0, 1), Period.ofYears(15));
	}
	
	public static DataFrame uniqueDuration(int numColumns, int numRows, Duration minDuration, Duration maxDuration) {
		return DataFrame.uniqueDuration(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minDuration, maxDuration);
	}
	
	public static DataFrame uniqueDuration(String[] columnNames, String[] rowNames, Duration minDuration, Duration maxDuration) {
		return DataFrame.uniqueDuration(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minDuration, maxDuration);
	}
	
	public static DataFrame uniqueDuration(ArrayList<String> columnNames, ArrayList<String> rowNames, Duration minDuration, Duration maxDuration) {
		int numValues = columnNames.size() * rowNames.size();
		Duration[] durationValues = CommonArray.getUniqueDurations(numValues, minDuration, maxDuration);
		DataFrame df = DataFrame.nullValues(columnNames, rowNames);
		df.copySerializedColumnsIntoDataFrame(durationValues);
		return df;
	}
	
	public static DataFrame uniqueDuration(int numColumns, int numRows) {
		return DataFrame.uniqueDuration(numColumns, numRows, Duration.ofSeconds(1), Duration.ofHours(12));
	}
	
	public static DataFrame uniqueDuration(String[] columnNames, String[] rowNames) {
		return DataFrame.uniqueDuration(columnNames, rowNames, Duration.ofSeconds(1), Duration.ofHours(12));
	}
	
	public static DataFrame uniqueDuration(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return DataFrame.uniqueDuration(columnNames, rowNames, Duration.ofSeconds(1), Duration.ofHours(12));
	}
	
	public static DataFrame identity(int dimensions) {
		DataFrame identity = DataFrame.zeros(dimensions, dimensions);
		for (int i = 0; i < dimensions; i++) {
			identity.setValue(i, i, 1);
		}
		return identity;
	}

	
	public static DataFrame randomInts(int numColumns, int numRows) {
		return randomInts(numColumns, numRows, 0, 10);
	}
	
	public static DataFrame randomInts(String[] columnNames, String[] rowNames) {
		return randomInts(columnNames, rowNames, 0, 10);
	}
	
	public static DataFrame randomInts(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomInts(columnNames, rowNames, 0, 10);
	}
	
	public static DataFrame randomInts(int numColumns, int numRows, int minimum, int maximum) {
		return DataFrame.randomInts(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minimum, maximum);
	}
	
	public static DataFrame randomInts(String[] columnNames, String[] rowNames, int minimum, int maximum) {
		return DataFrame.randomInts(CommonArray.convertStringArrayToArrayList(columnNames), CommonArray.convertStringArrayToArrayList(rowNames), minimum, maximum);
	}
	
	public static DataFrame randomInts(ArrayList<String> columnNames, ArrayList<String> rowNames, int minimum, int maximum) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemIntSeries(newDF.getNumRows(), minimum, maximum));
		}
		return newDF;
	}
	
	public static DataFrame randomDoubles(int numColumns, int numRows) {
		return randomDoubles(numColumns, numRows, 0, 10);
	}
	
	public static DataFrame randomDoubles(String[] columnNames, String[] rowNames) {
		return randomDoubles(columnNames, rowNames, 0, 10);
	}
	
	public static DataFrame randomDoubles(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomDoubles(columnNames, rowNames, 0, 10);
	}
	
	public static DataFrame randomDoubles(int numColumns, int numRows, double minimum, double maximum) {
		return DataFrame.randomDoubles(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minimum, maximum);
	}
	
	public static DataFrame randomDoubles(String[] colNames, String[] rowNames, double minimum, double maximum) {
		return DataFrame.randomDoubles(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames), minimum, maximum);
	}
	
	public static DataFrame randomDoubles(ArrayList<String> columnNames, ArrayList<String> rowNames, double minimum, double maximum) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemDoubleSeries(newDF.getNumRows(), minimum, maximum));
		}
		return newDF;
	}
	
	
	public static DataFrame randomStrings(int numColumns, int numRows) {
		return randomStrings(numColumns, numRows, 5);
	}
	
	public static DataFrame randomStrings(String[] columnNames, String[] rowNames) {
		return randomStrings(columnNames, rowNames, 5);
	}
	
	public static DataFrame randomStrings(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomStrings(columnNames, rowNames, 5);
	}
	
	
	public static DataFrame randomStrings(int numColumns, int numRows, int stringLength) {
		return DataFrame.randomStrings(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), stringLength);
	}
	
	public static DataFrame randomStrings(String[] colNames, String[] rowNames, int stringLength) {
		return DataFrame.randomStrings(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames), stringLength);
	}
	
	public static DataFrame randomStrings(ArrayList<String> columnNames, ArrayList<String> rowNames, int stringLength) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemStringSeries(newDF.getNumRows(), stringLength));
		}
		return newDF;
	}
	
	
	public static DataFrame randomBooleans(int numColumns, int numRows) {
		return DataFrame.randomBooleans(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows));
	}
	
	public static DataFrame randomBooleans(String[] colNames, String[] rowNames) {
		return DataFrame.randomBooleans(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames));
	}
	
	public static DataFrame randomBooleans(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemBooleanSeries(newDF.getNumRows()));
		}
		return newDF;
	}
	
	
	public static DataFrame randomLocalDates(int numColumns, int numRows) {
		return randomLocalDates(numColumns, numRows, LocalDate.now(), LocalDate.now().plusYears(1));
	}
	
	public static DataFrame randomLocalDates(String[] columnNames, String[] rowNames) {
		return randomLocalDates(columnNames, rowNames, LocalDate.now(), LocalDate.now().plusYears(1));
	}
	
	public static DataFrame randomLocalDates(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomLocalDates(columnNames, rowNames, LocalDate.now(), LocalDate.now().plusYears(1));
	}
	
	public static DataFrame randomLocalDates(int numColumns, int numRows, LocalDate minimum, LocalDate maximum) {
		return DataFrame.randomLocalDates(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minimum, maximum);
	}
	
	public static DataFrame randomLocalDates(String[] colNames, String[] rowNames, LocalDate minimum, LocalDate maximum) {
		return DataFrame.randomLocalDates(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames), minimum, maximum);
	}
	
	public static DataFrame randomLocalDates(ArrayList<String> columnNames, ArrayList<String> rowNames, LocalDate minimum, LocalDate maximum) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemLocalDateSeries(newDF.getNumRows(), minimum, maximum));
		}
		return newDF;
	}
	
	
	public static DataFrame randomLocalDateTimes(int numColumns, int numRows) {
		return randomLocalDateTimes(numColumns, numRows, LocalDateTime.now(), LocalDateTime.now().plusYears(1));
	}
	
	public static DataFrame randomLocalDateTimes(String[] columnNames, String[] rowNames) {
		return randomLocalDateTimes(columnNames, rowNames, LocalDateTime.now(), LocalDateTime.now().plusYears(1));
	}
	
	public static DataFrame randomLocalDateTimes(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomLocalDateTimes(columnNames, rowNames, LocalDateTime.now(), LocalDateTime.now().plusYears(1));
	}
	
	public static DataFrame randomLocalDateTimes(int numColumns, int numRows, LocalDateTime minimum, LocalDateTime maximum) {
		return DataFrame.randomLocalDateTimes(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minimum, maximum);
	}

	public static DataFrame randomLocalDateTimes(String[] colNames, String[] rowNames, LocalDateTime minimum, LocalDateTime maximum) {
		return DataFrame.randomLocalDateTimes(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames), minimum, maximum);
	}

	public static DataFrame randomLocalDateTimes(ArrayList<String> columnNames, ArrayList<String> rowNames, LocalDateTime minimum, LocalDateTime maximum) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemLocalDateTimeSeries(newDF.getNumRows(), minimum, maximum));
		}
		return newDF;
	}
	
	
	public static DataFrame randomLocalTimes(int numColumns, int numRows) {
		return randomLocalTimes(numColumns, numRows, LocalTime.MIDNIGHT, LocalTime.NOON);
	}
	
	public static DataFrame randomLocalTimes(String[] columnNames, String[] rowNames) {
		return randomLocalTimes(columnNames, rowNames, LocalTime.MIDNIGHT, LocalTime.NOON);
	}
	
	public static DataFrame randomLocalTimes(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomLocalTimes(columnNames, rowNames, LocalTime.MIDNIGHT, LocalTime.NOON);
	}
	
	public static DataFrame randomLocalTimes(int numColumns, int numRows, LocalTime minimum, LocalTime maximum) {
		return DataFrame.randomLocalTimes(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minimum, maximum);
	}

	public static DataFrame randomLocalTimes(String[] colNames, String[] rowNames, LocalTime minimum, LocalTime maximum) {
		return DataFrame.randomLocalTimes(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames), minimum, maximum);
	}

	public static DataFrame randomLocalTimes(ArrayList<String> columnNames, ArrayList<String> rowNames, LocalTime minimum, LocalTime maximum) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemLocalTimeSeries(newDF.getNumRows(), minimum, maximum));
		}
		return newDF;
	}
	
	
	public static DataFrame randomPeriods(int numColumns, int numRows) {
		return randomPeriods(numColumns, numRows, Period.of(0, 0, 1), Period.of(2, 0, 0));
	}
	
	public static DataFrame randomPeriods(String[] columnNames, String[] rowNames) {
		return randomPeriods(columnNames, rowNames, Period.of(0, 0, 1), Period.of(2, 0, 0));
	}
	
	public static DataFrame randomPeriods(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomPeriods(columnNames, rowNames, Period.of(0, 0, 1), Period.of(2, 0, 0));
	}
	
	public static DataFrame randomPeriods(int numColumns, int numRows, Period minimum, Period maximum) {
		return DataFrame.randomPeriods(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minimum, maximum);
	}

	public static DataFrame randomPeriods(String[] colNames, String[] rowNames, Period minimum, Period maximum) {
		return DataFrame.randomPeriods(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames), minimum, maximum);
	}

	public static DataFrame randomPeriods(ArrayList<String> columnNames, ArrayList<String> rowNames, Period minimum, Period maximum) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemPeriodSeries(newDF.getNumRows(), minimum, maximum));
		}
		return newDF;
	}
	
	public static DataFrame randomDurations(int numColumns, int numRows) {
		return randomDurations(numColumns, numRows, Duration.ofSeconds(1), Duration.ofHours(12));
	}
	
	public static DataFrame randomDurations(String[] columnNames, String[] rowNames) {
		return randomDurations(columnNames, rowNames, Duration.ofSeconds(1), Duration.ofHours(12));
	}
	
	public static DataFrame randomDurations(ArrayList<String> columnNames, ArrayList<String> rowNames) {
		return randomDurations(columnNames, rowNames, Duration.ofSeconds(1), Duration.ofHours(12));
	}
	
	public static DataFrame randomDurations(int numColumns, int numRows, Duration minimum, Duration maximum) {
		return DataFrame.randomDurations(CommonArray.generateIncreasingSequence(numColumns), CommonArray.generateIncreasingSequence(numRows), minimum, maximum);
	}

	public static DataFrame randomDurations(String[] colNames, String[] rowNames, Duration minimum, Duration maximum) {
		return DataFrame.randomDurations(CommonArray.convertStringArrayToArrayList(colNames), CommonArray.convertStringArrayToArrayList(rowNames), minimum, maximum);
	}

	public static DataFrame randomDurations(ArrayList<String> columnNames, ArrayList<String> rowNames, Duration minimum, Duration maximum) {
		DataFrame newDF = new DataFrame(columnNames, rowNames);
		for (int columnCount = 0; columnCount < columnNames.size(); columnCount++) {
			newDF.setColumnValues(columnCount, DataItem.randomDataItemDurationSeries(newDF.getNumRows(), minimum, maximum));
		}
		return newDF;
	}
	
	public static DataFrame randomDataFrameWithColumnTypes(int numColumns, int numRows, StorageType[] columnTypes) {
		DataFrame df = DataFrame.nullValues(numColumns, numRows);
		for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
			df.setColumnValues(columnIndex, DataItem.randomDataItemSeries(numRows, columnTypes[columnIndex]));
		}
		return df;
	}
	
	public static DataFrame randomDataFrameWithColumnTypes(String[] colNames, String[] rowNames, StorageType[] columnTypes) {
		DataFrame df = DataFrame.nullValues(colNames, rowNames);
		for (int columnIndex = 0; columnIndex < colNames.length; columnIndex++) {
			df.setColumnValues(columnIndex, DataItem.randomDataItemSeries(rowNames.length, columnTypes[columnIndex]));
		}
		return df;
	}
	
	public static DataFrame randomDataFrameWithColumnTypes(int numColumns, int numRows, Map<String, StorageType> columnTypes) {
		DataFrame df = DataFrame.nullValues(numColumns, numRows);
		for (String columnName: columnTypes.keySet()) {
			df.setColumnValues(columnName, DataItem.randomDataItemSeries(numRows, columnTypes.get(columnName)));
		}
		return df;
	}
	
	public static DataFrame randomDataFrameWithColumnTypes(String[] colNames, String[] rowNames, Map<String, StorageType> columnTypes) {
		DataFrame df = DataFrame.nullValues(colNames, rowNames);
		for (String columnName: columnTypes.keySet()) {
			df.setColumnValues(columnName, DataItem.randomDataItemSeries(rowNames.length, columnTypes.get(columnName)));
		}
		return df;
	}
	
	public static <T> DataFrame dataFrameFromMapOfLists(Map<String, List<T>> map, boolean isRow) {
		return new DataFrame(map, isRow);
	}
	
	public static <T> DataFrame dataFrameFromMapOfColumns(Map<String, List<T>> map) {
		return new DataFrame(map, false);
	}
	
	public static <T> DataFrame dataFrameFromMapOfRows(Map<String, List<T>> map) {
		return new DataFrame(map, true);
	}
	
	public static DataFrame dataFrameFromListOfMaps(List<Map<String, Object>> maps, boolean isRow) {
		return new DataFrame(maps, isRow);
	}
	
	public static DataFrame dataFrameFromListOfColumnMaps(List<Map<String, Object>> maps) {
		return new DataFrame(maps, false);
	}
	
	public static DataFrame dataFrameFromListOfRowMaps(List<Map<String, Object>> maps) {
		return new DataFrame(maps, true);
	}
	
	public static DataFrame dataFrameFromOtherDataFrame(DataFrame otherDF) {
		return new DataFrame(otherDF);
	}
	
	public static <T> DataFrame dataFrameFromList(String name, List<T> list, boolean isRow) {
		return new DataFrame(name, list, isRow);
	}
	
	public static <T> DataFrame dataFrameFromList(List<T> list, boolean isRow) {
		return new DataFrame("0", list, isRow);
	}
	
	public static <T> DataFrame dataFrameFromColumnList(String name, List<T> list) {
		return new DataFrame(name, list, false);
	}
	
	public static <T> DataFrame dataFrameFromColumnList(List<T> list) {
		return new DataFrame("0", list, false);
	}
	
	public static <T> DataFrame dataFrameFromRowList(String name, List<T> list) {
		return new DataFrame(name, list, true);
	}
	
	public static <T> DataFrame dataFrameFromRowList(List<T> list) {
		return new DataFrame("0", list, true);
	}
	
	
	
	public static <T> DataFrame dataFrameFromArray(String name, T[] list, boolean isRow) {
		return new DataFrame(name, list, isRow);
	}
	
	public static <T> DataFrame dataFrameFromArray(T[] list, boolean isRow) {
		return new DataFrame("0", list, isRow);
	}
	
	public static <T> DataFrame dataFrameFromColumnArray(String name, T[] list) {
		return new DataFrame(name, list, false);
	}
	
	public static <T> DataFrame dataFrameFromColumnArray(T[] list) {
		return new DataFrame("0", list, false);
	}
	
	public static <T> DataFrame dataFrameFromRowArray(String name, T[] list) {
		return new DataFrame(name, list, true);
	}
	
	public static <T> DataFrame dataFrameFromRowArray(T[] list) {
		return new DataFrame("0", list, true);
	}
	
	
	public static DataFrame dataFrameFromArray(String name, int[] list, boolean isRow) {
		return new DataFrame(name, list, isRow);
	}
	
	public static DataFrame dataFrameFromArray(int[] list, boolean isRow) {
		return new DataFrame("0", list, isRow);
	}
	
	public static DataFrame dataFrameFromColumnArray(String name, int[] list) {
		return new DataFrame(name, list, false);
	}
	
	public static DataFrame dataFrameFromColumnArray(int[] list) {
		return new DataFrame("0", list, false);
	}
	
	public static DataFrame dataFrameFromRowArray(String name, int[] list) {
		return new DataFrame(name, list, true);
	}
	
	public static DataFrame dataFrameFromRowArray(int[] list) {
		return new DataFrame("0", list, true);
	}
	
	public static DataFrame dataFrameFromArray(String name, float[] list, boolean isRow) {
		return new DataFrame(name, list, isRow);
	}
	
	public static DataFrame dataFrameFromArray(float[] list, boolean isRow) {
		return new DataFrame("0", list, isRow);
	}
	
	public static DataFrame dataFrameFromColumnArray(String name, float[] list) {
		return new DataFrame(name, list, false);
	}
	
	public static DataFrame dataFrameFromColumnArray(float[] list) {
		return new DataFrame("0", list, false);
	}
	
	public static DataFrame dataFrameFromRowArray(String name, float[] list) {
		return new DataFrame(name, list, true);
	}
	
	public static DataFrame dataFrameFromRowArray(float[] list) {
		return new DataFrame("0", list, true);
	}
	
	public static DataFrame dataFrameFromArray(String name, double[] list, boolean isRow) {
		return new DataFrame(name, list, isRow);
	}
	
	public static DataFrame dataFrameFromArray(double[] list, boolean isRow) {
		return new DataFrame("0", list, isRow);
	}
	
	public static DataFrame dataFrameFromColumnArray(String name, double[] list) {
		return new DataFrame(name, list, false);
	}
	
	public static DataFrame dataFrameFromColumnArray(double[] list) {
		return new DataFrame("0", list, false);
	}
	
	public static DataFrame dataFrameFromRowArray(String name, double[] list) {
		return new DataFrame(name, list, true);
	}
	
	public static DataFrame dataFrameFromRowArray(double[] list) {
		return new DataFrame("0", list, true);
	}
	
	public static DataFrame dataFrameFromArray(String name, boolean[] list, boolean isRow) {
		return new DataFrame(name, list, isRow);
	}
	
	public static DataFrame dataFrameFromArray(boolean[] list, boolean isRow) {
		return new DataFrame("0", list, isRow);
	}
	
	public static DataFrame dataFrameFromColumnArray(String name, boolean[] list) {
		return new DataFrame(name, list, false);
	}
	
	public static DataFrame dataFrameFromColumnArray(boolean[] list) {
		return new DataFrame("0", list, false);
	}
	
	public static DataFrame dataFrameFromRowArray(String name, boolean[] list) {
		return new DataFrame(name, list, true); 
	}
	
	public static DataFrame dataFrameFromRowArray(boolean[] list) {
		return new DataFrame("0", list, true);
	}
	
	public static <T> DataFrame dataFrameFrom2DList(List<String> names, List<List<T>> lists, boolean isRow) {
		return new DataFrame(names, lists, isRow);
	}
	
	public static <T> DataFrame dataFrameFrom2DList(List<List<T>> lists, boolean isRow) {
		return new DataFrame(CommonArray.generateIncreasingSequence(lists.size()), lists, isRow);
	}
	
	public static <T> DataFrame dataFrameFrom2DColumnList(List<String> names, List<List<T>> lists) {
		return new DataFrame(names, lists, false);
	}
	
	public static <T> DataFrame dataFrameFrom2DColumnList(List<List<T>> lists) {
		return new DataFrame(CommonArray.generateIncreasingSequence(lists.size()), lists, false);
	}
	
	public static <T> DataFrame dataFrameFrom2DRowList(List<String> names, List<List<T>> lists) {
		return new DataFrame(names, lists, true);
	}
	
	public static <T> DataFrame dataFrameFrom2DRowList(List<List<T>> lists) {
		return new DataFrame(CommonArray.generateIncreasingSequence(lists.size()), lists, true);
	}
	
	public static <T> DataFrame dataFrameFrom2DArray(String[] names, T[][] arrays, boolean isRow) {
		return new DataFrame(names, arrays, isRow);
	}

	public static <T> DataFrame dataFrameFrom2DArray(T[][] arrays, boolean isRow) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, isRow);
	}

	public static <T> DataFrame dataFrameFrom2DColumnArray(String[] names, T[][] arrays) {
		return new DataFrame(names, arrays, false);
	}

	public static <T> DataFrame dataFrameFrom2DColumnArray(T[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, false);
	}

	public static <T> DataFrame dataFrameFrom2DRowArray(String[] names, T[][] arrays) {
		return new DataFrame(names, arrays, true);
	}

	public static <T> DataFrame dataFrameFrom2DRowArray(T[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, true);
	}


	
	public static DataFrame dataFrameFrom2DArray(String[] names, int[][] arrays, boolean isRow) {
		return new DataFrame(names, arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DArray(int[][] arrays, boolean isRow) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DColumnArray(String[] names, int[][] arrays) {
		return new DataFrame(names, arrays, false);
	}

	public static DataFrame dataFrameFrom2DColumnArray(int[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, false);
	}

	public static DataFrame dataFrameFrom2DRowArray(String[] names, int[][] arrays) {
		return new DataFrame(names, arrays, true);
	}

	public static DataFrame dataFrameFrom2DRowArray(int[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, true);
	}
	
	public static DataFrame dataFrameFrom2DArray(String[] names, double[][] arrays, boolean isRow) {
		return new DataFrame(names, arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DArray(double[][] arrays, boolean isRow) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DColumnArray(String[] names, double[][] arrays) {
		return new DataFrame(names, arrays, false);
	}

	public static DataFrame dataFrameFrom2DColumnArray(double[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, false);
	}

	public static DataFrame dataFrameFrom2DRowArray(String[] names, double[][] arrays) {
		return new DataFrame(names, arrays, true);
	}

	public static DataFrame dataFrameFrom2DRowArray(double[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, true);
	}

	public static DataFrame dataFrameFrom2DArray(String[] names, float[][] arrays, boolean isRow) {
		return new DataFrame(names, arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DArray(float[][] arrays, boolean isRow) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DColumnArray(String[] names, float[][] arrays) {
		return new DataFrame(names, arrays, false);
	}

	public static DataFrame dataFrameFrom2DColumnArray(float[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, false);
	}

	public static DataFrame dataFrameFrom2DRowArray(String[] names, float[][] arrays) {
		return new DataFrame(names, arrays, true);
	}

	public static DataFrame dataFrameFrom2DRowArray(float[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, true);
	}


	public static DataFrame dataFrameFrom2DArray(String[] names, boolean[][] arrays, boolean isRow) {
		return new DataFrame(names, arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DArray(boolean[][] arrays, boolean isRow) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, isRow);
	}

	public static DataFrame dataFrameFrom2DColumnArray(String[] names, boolean[][] arrays) {
		return new DataFrame(names, arrays, false);
	}

	public static DataFrame dataFrameFrom2DColumnArray(boolean[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, false);
	}

	public static DataFrame dataFrameFrom2DRowArray(String[] names, boolean[][] arrays) {
		return new DataFrame(names, arrays, true);
	}

	public static DataFrame dataFrameFrom2DRowArray(boolean[][] arrays) {
		return new DataFrame(CommonArray.generateIncreasingSequence(arrays.length).toArray(new String[0]), arrays, true);
	}

	
	
	public <T> void insertColumn(int index, String columnName, List<T> column) {
		try {
			if (index > this.columnNames.size()) {
				throw new DataFrameOutOfBoundsException("Column index too high: " + index + " > " + this.columnNames.size());
			}
			
			if (this.data.size() > 0) {
				if (data.get(0).size() != column.size()) {
					throw new DataFrameShapeException("New column must have same number of rows as other columns");
				}
			}
			
			// If there currently are no rows
			if (this.rowNames.size() == 0) {				
				this.rowNames = CommonArray.generateIncreasingSequence(column.size());
			}
			
			this.data.add(index, createDataItemList(column));
			String newColumnName = CommonArray.getNewMangleName(this.columnNames, columnName);
			this.columnNames.add(index, newColumnName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public <T> void insertColumn(int index, String columnName, T[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, DataItem[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, int[] column) {
		insertColumn(index, columnName, CommonArray.convertArrayToObjectList(column));
	}
	
	public void insertColumn(int index, String columnName, float[] column) {
		insertColumn(index, columnName, CommonArray.convertFloatArrayToDoubleArray(column));
	}
	
	public void insertColumn(int index, String columnName, double[] column) {
		insertColumn(index, columnName, CommonArray.convertArrayToObjectList(column));
	}
	
	public void insertColumn(int index, String columnName, boolean[] column) {
		insertColumn(index, columnName, CommonArray.convertArrayToObjectList(column));
	}
	
	public void insertColumn(int index, String columnName, String[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, LocalDate[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, LocalDateTime[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, LocalTime[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, Period[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, Duration[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}
	
	public void insertColumn(int index, String columnName, BigDecimal[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}

	public void insertColumn(int index, String columnName, Color[] column) {
		insertColumn(index, columnName, Arrays.asList(column));
	}

	public <T> void insertColumn(int index, String columnName, T value) {
		T[] column = CommonArray.initializeGenericArrayWithValues(this.getNumRows(), value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, DataItem value) {
		DataItem[] column = CommonArray.initializeDataItemArrayWithValues(this.getNumRows(), value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, int value) {
		int[] column = CommonArray.initializeIntArrayWithValues(this.getNumRows(), value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, float value) {
		float[] column = CommonArray.initializeFloatArrayWithValues(this.getNumRows(), value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, double value) {
		double[] column = CommonArray.initializeDoubleArrayWithValues(this.getNumRows(), value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, Boolean value) {
		boolean[] column = CommonArray.initializeBooleanArrayWithValues(this.getNumRows(), value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, String value) {
		String[] column = new String[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, LocalDate value) {
		LocalDate[] column = new LocalDate[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, LocalDateTime value) {
		LocalDateTime[] column = new LocalDateTime[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, LocalTime value) {
		LocalTime[] column = new LocalTime[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, Period value) {
		Period[] column = new Period[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, Duration value) {
		Duration[] column = new Duration[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName, BigDecimal value) {
		BigDecimal[] column = new BigDecimal[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}

	public void insertColumn(int index, String columnName, Color value) {
		Color[] column = new Color[this.getNumRows()];
		Arrays.setAll(column, i -> value);
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, String columnName) {
		insertColumn(index, columnName, new DataItem());
	}
	
	public <T> void insertColumn(int index, List<T> column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public <T> void insertColumn(int index, T[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, DataItem[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, int[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, float[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, double[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, boolean[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	} 
	
	public void insertColumn(int index, String[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, LocalDate[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, LocalDateTime[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, LocalTime[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, Period[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, Duration[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, BigDecimal[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}

	public void insertColumn(int index, Color[] column) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, column);
	}
	
	public void insertColumn(int index, DataItem value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public <T> void insertColumn(int index, T value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, int value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, float value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, double value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, boolean value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, LocalDate value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, LocalDateTime value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, LocalTime value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, Period value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, Duration value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index, BigDecimal value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}

	public void insertColumn(int index, Color value) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName, value);
	}
	
	public void insertColumn(int index) {
		String columnName = generateUnusedColumnName();
		insertColumn(index, columnName);
	}
	
	
	
	public <T> void insertColumns(int index, Map<String, List<T>> map) {
		int insertionOffset = 0;
		for (String columnName : map.keySet()) {
			this.insertColumn(index + insertionOffset, columnName, map.get(columnName));
			insertionOffset++;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> void insertColumn(int index, String columnName, Map<String, T> map) {
		List<T> col = new ArrayList<T>(this.rowNames.size());
		for (int i = 0; i < this.getNumRows(); i++) {
			col.add((T) new Object());
		}
		 
		for (String rowName : map.keySet()) {

			int rowIndex = this.rowNames.indexOf(rowName);
			if (rowIndex == -1) {
				this.rowNames.add(rowName);

			}
			col.set(rowIndex, map.get(rowName));

		}
		insertColumn(index, columnName, col);
	}
	
	public <T> void insertColumn(int index, Map<String, T> map) {
		insertColumn(index, generateUnusedColumnName(), map);
	}
	
	public <T> void insertColumns(int index, List<String> columnNames, List<List<T>> columns) {
		IntStream.range(0, columnNames.size()).forEachOrdered(i -> insertColumn(index + i, columnNames.get(i), columns.get(i)));
	}
	
	public void insertColumns(int index, String[] columnNames, DataItem[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public <T> void insertColumns(int index, String[] columnNames, T[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, int[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, float[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, double[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, boolean[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, String[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, LocalDate[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, LocalDateTime[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, LocalTime[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, Period[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, Duration[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, BigDecimal[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}
	
	public void insertColumns(int index, String[] columnNames, Color[][] columns) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], columns[i]));
	}

	public void insertColumns(int index, String[] columnNames, DataItem value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public <T> void insertColumns(int index, String[] columnNames, T value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, int value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, float value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, double value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, boolean value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, String value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, LocalDate value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, LocalDateTime value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, LocalTime value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, Period value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, Duration value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames, BigDecimal value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}

	public void insertColumns(int index, String[] columnNames, Color value) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], value));
	}
	
	public void insertColumns(int index, String[] columnNames) {
		IntStream.range(0, columnNames.length).forEachOrdered(i -> insertColumn(index + i, columnNames[i], new DataItem()));
	}
	
	public <T> void insertColumns(int index, List<List<T>> columns) {
		IntStream.range(0, columns.size()).forEachOrdered(i -> insertColumn(index + i, columns.get(i)));
	}
	
	public void insertColumns(int index, DataItem[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public <T> void insertColumns(int index, T[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, int[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, float[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, double[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, boolean[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, String[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, LocalDate[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, LocalDateTime[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, LocalTime[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, Period[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, Duration[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, BigDecimal[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}

	public void insertColumns(int index, Color[][] columns) {
		IntStream.range(0, columns.length).forEachOrdered(i -> insertColumn(index + i, columns[i]));
	}
	
	public void insertColumns(int index, int numColumns, DataItem value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public <T> void insertColumns(int index, int numColumns, T value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, int value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, float value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, double value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, boolean value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, LocalDate value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, LocalDateTime value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, LocalTime value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, Period value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, Duration value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns, BigDecimal value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}

	public void insertColumns(int index, int numColumns, Color value) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, value));
	}
	
	public void insertColumns(int index, int numColumns) {
		IntStream.range(0, numColumns).forEachOrdered(i -> insertColumn(index + i, new DataItem()));
	}
	

	public <T> void appendColumn(String columnName, List<T> column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, DataItem[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public <T> void appendColumn(String columnName, T[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, int[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, float[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, double[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, boolean[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, String[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, LocalDate[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, LocalDateTime[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, LocalTime[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, Period[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, Duration[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, BigDecimal[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}
	
	public void appendColumn(String columnName, Color[] column) {
		insertColumn(this.columnNames.size(), columnName, column);
	}

	public void appendColumn(String columnName, DataItem value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public <T> void appendColumn(String columnName, T value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, int value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, float value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, double value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, boolean value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, String value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, LocalDate value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, LocalDateTime value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, LocalTime value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, Period value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, Duration value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName, BigDecimal value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}

	public void appendColumn(String columnName, Color value) {
		insertColumn(this.columnNames.size(), columnName, value);
	}
	
	public void appendColumn(String columnName) {
		insertColumn(this.columnNames.size(), columnName);
	}
	
	public <T> void  appendColumn(List<T> column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(DataItem[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public <T> void appendColumn(T[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(int[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(float[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(double[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(boolean[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(String[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(LocalDate[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(LocalDateTime[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(LocalTime[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(Period[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(Duration[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(BigDecimal[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(Color[] column) {
		insertColumn(this.columnNames.size(), column);
	}
	
	public void appendColumn(DataItem value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public <T> void appendColumn(T value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(int value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(float value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(double value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(boolean value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(LocalDate value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(LocalDateTime value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(LocalTime value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(Period value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(Duration value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn(BigDecimal value) {
		insertColumn(this.columnNames.size(), value);
	}

	public void appendColumn(Color value) {
		insertColumn(this.columnNames.size(), value);
	}
	
	public void appendColumn() {
		insertColumn(this.columnNames.size());
	}

	public <T> void appendColumns(Map<String, List<T>> map) {
		insertColumns(this.columnNames.size(), map);
	}
	
	public <T> void appendColumn(String columnName, Map<String, T> map) {
		insertColumn(this.columnNames.size(), columnName, map);
	}
	
	public <T> void appendColumn(Map<String, T> map) {
		insertColumn(this.columnNames.size(), map);
	}

	public <T> void appendColumns(List<String> columnNames, List<List<T>> columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, DataItem[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public <T> void appendColumns(String[] columnNames, T[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, int[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, float[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, double[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, boolean[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, String[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, LocalDate[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, LocalDateTime[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, LocalTime[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, Period[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, Duration[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}
	
	public void appendColumns(String[] columnNames, BigDecimal[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}

	public void appendColumns(String[] columnNames, Color[][] columns) {
		insertColumns(this.columnNames.size(), columnNames, columns);
	}

	public void appendColumns(String[] columnNames, DataItem value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public <T> void appendColumns(String[] columnNames, T value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, int value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, float value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, double value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, boolean value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, String value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, LocalDate value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, LocalDateTime value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, LocalTime value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, Period value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, Duration value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, BigDecimal value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}
	
	public void appendColumns(String[] columnNames, Color value) {
		insertColumns(this.columnNames.size(), columnNames, value);
	}

	public void appendColumns(String[] columnNames) {
		insertColumns(this.columnNames.size(), columnNames);
	}
	
	public <T> void appendColumns(List<List<T>> columns) {
		insertColumns(this.columnNames.size(), columns);
	}

	public void appendColumns(DataItem[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public <T> void appendColumns(T[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(int[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(float[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(double[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(boolean[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(String[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(LocalDate[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(LocalDateTime[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(LocalTime[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(Period[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(Duration[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(BigDecimal[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}

	public void appendColumns(Color[][] columns) {
		insertColumns(this.getColumnNames().size(), columns);
	}
	
	public void appendColumns(int numColumns, DataItem value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public <T> void appendColumns(int numColumns, T value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, int value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, float value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, double value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, boolean value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, LocalDate value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, LocalDateTime value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, LocalTime value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, Period value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, Duration value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns, BigDecimal value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}

	public void appendColumns(int numColumns, Color value) {
		insertColumns(this.getColumnNames().size(), numColumns, value);
	}
	
	public void appendColumns(int numColumns) {
		insertColumns(this.getColumnNames().size(), numColumns);
	}

	
	public <T> void insertRow(int index, String rowName, List<T> row) {
		try {
			if (index > this.rowNames.size()) {
				throw new DataFrameOutOfBoundsException("Column index too high: " + index + " > " + this.rowNames.size());
			}
			
			if ((this.rowNames.size() > 0) && (row.size() != this.columnNames.size())) {
				throw new DataFrameShapeException("New row must have same number of rows as other rows");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertRow(int index, String rowName, DataItem[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public <T> void insertRow(int index, String rowName, T[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, int[] row) {
		insertRow(index, rowName, CommonArray.convertArrayToObjectList(row));
	}
	
	public void insertRow(int index, String rowName, float[] row) {
		insertRow(index, rowName, CommonArray.convertFloatArrayToDoubleArray(row));
	}
	
	public void insertRow(int index, String rowName, double[] row) {
		insertRow(index, rowName, CommonArray.convertArrayToObjectList(row));
	}
	
	public void insertRow(int index, String rowName, boolean[] row) {
		insertRow(index, rowName, CommonArray.convertArrayToObjectList(row));
	}
	
	public void insertRow(int index, String rowName, String[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, LocalDate[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, LocalDateTime[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, LocalTime[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, Period[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, Duration[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, BigDecimal[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}

	public void insertRow(int index, String rowName, Color[] row) {
		insertRow(index, rowName, Arrays.asList(row));
	}
	
	public void insertRow(int index, String rowName, DataItem value) {
		DataItem[] row = CommonArray.initializeDataItemArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public <T> void insertRow(int index, String rowName, T value) {
		T[] row = CommonArray.initializeGenericArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, int value) {
		int[] row = CommonArray.initializeIntArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, float value) {
		float[] row = CommonArray.initializeFloatArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, double value) {
		double[] row = CommonArray.initializeDoubleArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, boolean value) {
		boolean[] row = CommonArray.initializeBooleanArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, String value) {
		String[] row = CommonArray.initializeStringArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, LocalDate value) {
		LocalDate[] row = CommonArray.initializeLocalDateArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, LocalDateTime value) {
		LocalDateTime[] row = CommonArray.initializeLocalDateTimeArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, LocalTime value) {
		LocalTime[] row = CommonArray.initializeLocalTimeArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, Period value) {
		Period[] row = CommonArray.initializePeriodArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, Duration value) {
		Duration[] row = CommonArray.initializeDurationArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName, BigDecimal value) {
		BigDecimal[] row = CommonArray.initializeBigDecimalArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}

	public void insertRow(int index, String rowName, Color value) {
		Color[] row = CommonArray.initializeColorArrayWithValues(this.getNumColumns(), value);
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String rowName) {
		insertRow(index, rowName, new DataItem());
	}
	
	public <T> void insertRow(int index, List<T> row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, DataItem[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public <T> void insertRow(int index, T[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, int[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, float[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, double[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, boolean[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, String[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, LocalDate[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, LocalDateTime[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, LocalTime[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, Period[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, Duration[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, BigDecimal[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}

	public void insertRow(int index, Color[] row) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, row);
	}
	
	public void insertRow(int index, DataItem value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public <T> void insertRow(int index, T value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, int value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, float value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, double value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, boolean value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, LocalDate value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, LocalDateTime value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, LocalTime value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, Period value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, Duration value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index, BigDecimal value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}

	public void insertRow(int index, Color value) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, value);
	}
	
	public void insertRow(int index) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, new DataItem());
	}
	
	public <T> void insertRows(int index, Map<String, List<T>> map) {
		int insertionOffset = 0;
		for (String rowName: map.keySet()) {
			this.insertRow(index + insertionOffset, rowName, map.get(rowName));
			insertionOffset++;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> void insertRow(int index, String rowName, Map<String, T> map) {
		List<T> row = new ArrayList<T>(this.columnNames.size());
		for (int i = 0; i < this.columnNames.size(); i++) {
			row.add((T) new Object());
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
	
	public <T> void insertRow(int index, Map<String, T> map) {
		String rowName = generateUnusedRowName();
		insertRow(index, rowName, map);
	}
	
	public <T> void insertRows(int index, List<String> rowNames, List<List<T>> rows) {
		int rowOffset = 0;
		for (List<T> row : rows) {
			insertRow(index + rowOffset, rowNames.get(rowOffset), row);
			rowOffset++;
		}
	}
	
	public void insertRows(int index, String[] rowNames, DataItem[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public <T> void insertRows(int index, String[] rowNames, T[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, int[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, float[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, double[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, boolean[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, String[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, LocalDate[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, LocalDateTime[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, LocalTime[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, Period[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, Duration[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, BigDecimal[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}

	public void insertRows(int index, String[] rowNames, Color[][] rows) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], rows[i]));
	}
	
	public void insertRows(int index, String[] rowNames, DataItem value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public <T> void insertRows(int index, String[] rowNames, T value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, int value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, float value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, double value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, boolean value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, String value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, LocalDate value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, LocalDateTime value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, LocalTime value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, Period value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, Duration value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames, BigDecimal value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}

	public void insertRows(int index, String[] rowNames, Color value) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], value));
	}
	
	public void insertRows(int index, String[] rowNames) {
		IntStream.range(0, rowNames.length).forEachOrdered(i -> insertRow(index + i, rowNames[i], new DataItem()));
	}
	
	public <T> void insertRows(int index, List<List<T>> rows) {
		IntStream.range(0, rows.size()).forEachOrdered(i -> insertRow(index + i, rows.get(i)));
	}
	
	public void insertRows(int index, DataItem[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public <T> void insertRows(int index, T[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, int[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, float[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, double[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, boolean[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, String[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, LocalDate[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, LocalDateTime[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, LocalTime[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, Period[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, Duration[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, BigDecimal[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}

	public void insertRows(int index, Color[][] rows) {
		IntStream.range(0, rows.length).forEachOrdered(i -> insertRow(index + i, rows[i]));
	}
	
	public void insertRows(int index, int numRows, DataItem value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public <T> void insertRows(int index, int numRows, T value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, int value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, float value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, double value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, boolean value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, LocalDate value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}

	public void insertRows(int index, int numRows, LocalDateTime value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, LocalTime value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, Period value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, Duration value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows, BigDecimal value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}

	public void insertRows(int index, int numRows, Color value) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i, value));
	}
	
	public void insertRows(int index, int numRows) {
		IntStream.range(0, numRows).forEachOrdered(i -> insertRow(index + i));
	}


	public <T> void appendRow(String rowName, List<T> row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, DataItem[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public <T> void appendRow(String rowName, T[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, int[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, float[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, double[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, boolean[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, String[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, LocalDate[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, LocalDateTime[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, LocalTime[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, Period[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, Duration[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, BigDecimal[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}

	public void appendRow(String rowName, Color[] row) {
		insertRow(this.rowNames.size(), rowName, row);
	}
	
	public void appendRow(String rowName, DataItem value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public <T> void appendRow(String rowName, T value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, int value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, float value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, double value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, boolean value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, String value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, LocalDate value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, LocalDateTime value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, LocalTime value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, Period value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, Duration value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName, BigDecimal value) {
		insertRow(this.rowNames.size(), rowName, value);
	}

	public void appendRow(String rowName, Color value) {
		insertRow(this.rowNames.size(), rowName, value);
	}
	
	public void appendRow(String rowName) {
		insertRow(this.rowNames.size(), rowName);
	}
	
	public <T> void appendRow(List<T> row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(DataItem[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public <T> void appendRow(T[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(int[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(float[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(double[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(boolean[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(String[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(LocalDate[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(LocalDateTime[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(LocalTime[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(Period[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(Duration[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(BigDecimal[] row) {
		insertRow(this.rowNames.size(), row);
	}

	public void appendRow(Color[] row) {
		insertRow(this.rowNames.size(), row);
	}
	
	public void appendRow(DataItem value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public <T> void appendRow(T value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(int value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(float value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(double value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(boolean value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(LocalDate value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(LocalDateTime value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(LocalTime value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(Period value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(Duration value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow(BigDecimal value) {
		insertRow(this.rowNames.size(), value);
	}

	public void appendRow(Color value) {
		insertRow(this.rowNames.size(), value);
	}
	
	public void appendRow() {
		insertRow(this.rowNames.size());
	}
	
	public <T> void appendRows(Map<String, List<T>> map) {
		insertRows(this.rowNames.size(), map);
	}
	
	public <T> void appendRow(String name, Map<String, T> map) {
		insertRow(this.rowNames.size(), name, map);
	}

	public <T> void appendRow(Map<String, T> map) {
		insertRow(this.rowNames.size(), map);
	}

	public <T> void appendRows(List<List<T>> rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public <T> void appendRows(List<String> names, List<List<T>> rows) {
		insertRows(this.rowNames.size(), names, rows);
	}

	public void appendRows(String[] names, DataItem[][] rows) {
		insertRows(this.rowNames.size(), names, rows);
	}
	
	public <T> void appendRows(String[] names, T[][] rows) {
		insertRows(this.rowNames.size(), names, rows);
	}
	
	public void appendRows(String[] rowNames, int[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, float[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, double[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, boolean[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, String[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, LocalDate[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, LocalDateTime[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, LocalTime[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, Period[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, Duration[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, BigDecimal[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}

	public void appendRows(String[] rowNames, Color[][] rows) {
		insertRows(this.rowNames.size(), rowNames, rows);
	}
	
	public void appendRows(String[] rowNames, DataItem value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public <T> void appendRows(String[] rowNames, T value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, int value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, float value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, double value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, boolean value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, String value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, LocalDate value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, LocalDateTime value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, LocalTime value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, Period value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, Duration value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames, BigDecimal value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}

	public void appendRows(String[] rowNames, Color value) {
		insertRows(this.rowNames.size(), rowNames, value);
	}
	
	public void appendRows(String[] rowNames) {
		insertRows(this.rowNames.size(), rowNames);
	}

	
	public void appendRows(DataItem[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public <T> void appendRows(T[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(int[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(float[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(double[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(boolean[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(String[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(LocalDate[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(LocalDateTime[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(LocalTime[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(Period[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(Duration[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(BigDecimal[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}

	public void appendRows(Color[][] rows) {
		insertRows(this.rowNames.size(), rows);
	}
	
	public void appendRows(int numRows, DataItem value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public <T> void appendRows(int numRows, T value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, int value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, float value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, double value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, boolean value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, LocalDate value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, LocalDateTime value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, LocalTime value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, Period value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, Duration value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows, BigDecimal value) {
		insertRows(this.rowNames.size(), numRows, value);
	}

	public void appendRows(int numRows, Color value) {
		insertRows(this.rowNames.size(), numRows, value);
	}
	
	public void appendRows(int numRows) {
		insertRows(this.rowNames.size(), numRows);
	}
	
	public void dropNonNumberColumns() {
		List<Integer> indices = new ArrayList<Integer>();
		for (int columnIndex = 0; columnIndex < this.getNumColumns(); columnIndex++) {
			if (!(this.getTypeOfColumn(columnIndex) == StorageType.Integer || 
				this.getTypeOfColumn(columnIndex) == StorageType.Double ||
				this.getTypeOfColumn(columnIndex) == StorageType.BigDecimal)) {
				indices.add(columnIndex);
			}
		}
		this.dropColumns(indices.stream().mapToInt(i -> i).toArray());
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
		for (int columnCount = 0; columnCount < this.getNumColumns(); columnCount++) {
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
	
	public ArrayList<ArrayList<DataItem>> getDataAs2DDataItemArrayList() {
		return this.data;
	}
	
	public List<List<Object>> getDataAsObjectList() {
		return getColumnsAs2DObjectList(0, this.getNumColumns() - 1);
	}

	public  List<List<String>> getDataAs2DStringList() {
		return getColumnsAs2DStringList(0, this.getNumColumns() - 1);
	}

	public  List<List<Integer>> getDataAs2DIntList() {
		return getColumnsAs2DIntList(0, this.getNumColumns() - 1);
	}

	public  List<List<Double>> getDataAs2DDoubleList() {
		return getColumnsAs2DDoubleList(0, this.getNumColumns() - 1);
	}

	public  List<List<Float>> getDataAs2DFloatList() {
		return getColumnsAs2DFloatList(0, this.getNumColumns() - 1);
	}

	public  List<List<Boolean>> getDataAs2DBooleanList() {
		return getColumnsAs2DBooleanList(0, this.getNumColumns() - 1);
	}

	public  List<List<LocalDate>> getDataAs2DLocalDateList() {
		return getColumnsAs2DLocalDateList(0, this.getNumColumns() - 1);
	}

	public  List<List<LocalDateTime>> getDataAs2DLocalDateTimeList() {
		return getColumnsAs2DLocalDateTimeList(0, this.getNumColumns() - 1);
	}

	public  List<List<LocalTime>> getDataAs2DLocalTimeList() {
		return getColumnsAs2DLocalTimeList(0, this.getNumColumns() - 1);
	}

	public  List<List<Period>> getDataAs2DPeriodList() {
		return getColumnsAs2DPeriodList(0, this.getNumColumns() - 1);
	}

	public  List<List<Duration>> getDataAs2DDurationList() {
		return getColumnsAs2DDurationList(0, this.getNumColumns() - 1);
	}
	
	public  List<List<BigDecimal>> getDataAs2DBigDecimalList() {
		return getColumnsAs2DBigDecimalList(0, this.getNumColumns() - 1);
	}

	public DataItem[][] getDataAs2DDataItemArray() {
		return getColumnsAs2DDataItemArray(0, this.getNumColumns() - 1);
	}
	
	public Object[][] getDataAs2DObjectArray() {
		return getColumnsAs2DObjectArray(0, this.getNumColumns() - 1);
	}
	
	public String[][] getDataAs2DStringArray() {
		return getColumnsAs2DStringArray(0, this.getNumColumns() - 1);
	}
	
	public int[][] getDataAs2DIntArray() {
		return getColumnsAs2DIntArray(0, this.getNumColumns() - 1);
	}
	
	public double[][] getDataAs2DDoubleArray() {
		return getColumnsAs2DDoubleArray(0, this.getNumColumns() - 1);
	}
	
	public float[][] getDataAs2DFloatArray() {
		return getColumnsAs2DFloatArray(0, this.getNumColumns() - 1);
	}
	
	public boolean[][] getDataAs2DBooleanArray() {
		return getColumnsAs2DBooleanArray(0, this.getNumColumns() - 1);
	}
	
	public LocalDate[][] getDataAs2DLocalDateArray() {
		return getColumnsAs2DLocalDateArray(0, this.getNumColumns() - 1);
	}
	
	public LocalDateTime[][] getDataAs2DLocalDateTimeArray() {
		return getColumnsAs2DLocalDateTimeArray(0, this.getNumColumns() - 1);
	}
	
	public LocalTime[][] getDataAs2DLocalTimeArray() {
		return getColumnsAs2DLocalTimeArray(0, this.getNumColumns() - 1);
	}
	
	public Period[][] getDataAs2DPeriodArray() {
		return getColumnsAs2DPeriodArray(0, this.getNumColumns() - 1);
	}
	
	public Duration[][] getDataAs2DDurationArray() {
		return getColumnsAs2DDurationArray(0, this.getNumColumns() - 1);
	}

	public BigDecimal[][] getDataAs2DBigDecimalArray() {
		return getColumnsAs2DBigDecimalArray(0, this.getNumColumns() - 1);
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
	
	public Object[] getColumnAsObjectArray(int index) {
		Object[] column = new Object[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getObjectValue();
		}
		return column;
	}
	
	public Object[] getColumnAsObjectArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsObjectArray(index);
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
	
	public float[] getColumnAsFloatArray(int index) {
		float[] column = new float[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getValueConvertedToFloat();
		}
		return column;
	}
	
	public float[] getColumnAsFloatArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsFloatArray(index);
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
	
	public LocalDate[] getColumnAsLocalDateArray(int index) {
		LocalDate[] column = new LocalDate[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getDateValue();
		}
		return column;
	}
	
	public LocalDate[] getColumnAsLocalDateArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsLocalDateArray(index);
	}
	
	public LocalDateTime[] getColumnAsLocalDateTimeArray(int index) {
		LocalDateTime[] column = new LocalDateTime[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getDateTimeValue();
		}
		return column;
	}
	
	public LocalDateTime[] getColumnAsLocalDateTimeArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsLocalDateTimeArray(index);
	}
	
	public LocalTime[] getColumnAsLocalTimeArray(int index) {
		LocalTime[] column = new LocalTime[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getTimeValue();
		}
		return column;
	}
	
	public LocalTime[] getColumnAsLocalTimeArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsLocalTimeArray(index);
	}
	
	public Period[] getColumnAsPeriodArray(int index) {
		Period[] column = new Period[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getPeriodValue();
		}
		return column;
	}
	
	public Period[] getColumnAsPeriodArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsPeriodArray(index);
	}
	
	public Duration[] getColumnAsDurationArray(int index) {
		Duration[] column = new Duration[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getDurationValue();
		}
		return column;
	}
	
	public Duration[] getColumnAsDurationArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsDurationArray(index);
	}

	public BigDecimal[] getColumnAsBigDecimalArray(int index) {
		BigDecimal[] column = new BigDecimal[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getBigDecimalValue();
		}
		return column;
	}
	
	
	public BigDecimal[] getColumnAsBigDecimalArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsBigDecimalArray(index);
	}
	
	public Color[] getColumnAsColorArray(int index) {
		Color[] column = new Color[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).getColorValue();
		}
		return column;
	}
	
	public Color[] getColumnAsColorArray(String name) {
		int index = this.columnNames.indexOf(name);
		return getColumnAsColorArray(index);
	}

	public List<DataItem> getColumnAsDataItemList(int index) {
		return Arrays.asList(this.getColumnAsDataItemArray(index));
	}

	public List<DataItem> getColumnAsDataItemList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsDataItemList(index);
	}

	public List<Object> getColumnAsObjectList(int index) {
		return Arrays.asList(this.getColumnAsObjectArray(index));
	}

	public List<Object> getColumnAsObjectList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsObjectList(index);
	}

	public List<String> getColumnAsStringList(int index) {
		return Arrays.asList(this.getColumnAsStringArray(index));
	}

	public List<String> getColumnAsStringList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsStringList(index);
	}

	public List<Integer> getColumnAsIntList(int index) {
		return Arrays.stream(this.getColumnAsIntArray(index)).boxed().collect(Collectors.toList());
	}

	public List<Integer> getColumnAsIntList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsIntList(index);
	}

	public List<Double> getColumnAsDoubleList(int index) {
		return Arrays.stream(this.getColumnAsDoubleArray(index)).boxed().collect(Collectors.toList());
	}

	public List<Double> getColumnAsDoubleList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsDoubleList(index);
	}

	public List<Float> getColumnAsFloatList(int index) {
		return CommonArray.convertFloatArrayToFloatList(this.getColumnAsFloatArray(index));
	}

	public List<Float> getColumnAsFloatList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsFloatList(index);
	}

	public List<Boolean> getColumnAsBooleanList(int index) {
		return CommonArray.convertBooleanArrayToBooleanList(this.getColumnAsBooleanArray(index));
	}

	public List<Boolean> getColumnAsBooleanList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsBooleanList(index);
	}

	public List<LocalDate> getColumnAsLocalDateList(int index) {
		return Arrays.asList(this.getColumnAsLocalDateArray(index));
	}

	public List<LocalDate> getColumnAsLocalDateList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsLocalDateList(index);
	}

	public List<LocalDateTime> getColumnAsLocalDateTimeList(int index) {
		return Arrays.asList(this.getColumnAsLocalDateTimeArray(index));
	}

	public List<LocalDateTime> getColumnAsLocalDateTimeList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsLocalDateTimeList(index);
	}

	public List<LocalTime> getColumnAsLocalTimeList(int index) {
		return Arrays.asList(this.getColumnAsLocalTimeArray(index));
	}

	public List<LocalTime> getColumnAsLocalTimeList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsLocalTimeList(index);
	}

	public List<Period> getColumnAsPeriodList(int index) {
		return Arrays.asList(this.getColumnAsPeriodArray(index));
	}

	public List<Period> getColumnAsPeriodList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsPeriodList(index);
	}

	public List<Duration> getColumnAsDurationList(int index) {
		return Arrays.asList(this.getColumnAsDurationArray(index));
	}

	public List<Duration> getColumnAsDurationList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsDurationList(index);
	}

	public List<BigDecimal> getColumnAsBigDecimalList(int index) {
		return Arrays.asList(this.getColumnAsBigDecimalArray(index));
	}

	public List<BigDecimal> getColumnAsBigDecimalList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsBigDecimalList(index);
	}

	public List<Color> getColumnAsColorList(int index) {
		return Arrays.asList(this.getColumnAsColorArray(index));
	}

	public List<Color> getColumnAsColorList(String name) {
		int index = this.columnNames.indexOf(name);
		return this.getColumnAsColorList(index);
	}

	public DataItem[][] getColumnsAs2DDataItemArray(int[] indices) {
		DataItem[][] columns = new DataItem[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsDataItemArray(indices[columnCount]);
		}
		return columns;
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DDataItemArray(indices);
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(ArrayList<String> names) {
		return getColumnsAs2DDataItemArray(names.toArray(new String[0]));
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DDataItemArray(indicesToGet);
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DDataItemArray(columnIndices);
	}
	
	public DataItem[][] getColumnsAs2DDataItemArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DDataItemArray(indices);
	}
	
	public Object[][] getColumnsAs2DObjectArray(int[] indices) {
		Object[][] columns = new Object[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsObjectArray(indices[columnCount]);
		}
		return columns;
	}
	
	public Object[][] getColumnsAs2DObjectArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DObjectArray(indices);
	}
	
	public Object[][] getColumnsAs2DObjectArray(ArrayList<String> names) {
		return getColumnsAs2DObjectArray(names.toArray(new String[0]));
	}
	
	public Object[][] getColumnsAs2DObjectArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DObjectArray(indicesToGet);
	}
	
	public Object[][] getColumnsAs2DObjectArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DDataItemArray(columnIndices);
	}
	
	public Object[][] getColumnsAs2DObjectArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DObjectArray(indices);
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
	
	public String[][] getColumnsAs2DStringArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DStringArray(columnIndices);
	}
	
	public String[][] getColumnsAs2DStringArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DStringArray(indices);
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
	
	public int[][] getColumnsAs2DIntArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DIntArray(columnIndices);
	}
	
	public int[][] getColumnsAs2DIntArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DIntArray(indices);
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
	
	public double[][] getColumnsAs2DDoubleArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DDoubleArray(columnIndices);
	}
	
	public double[][] getColumnsAs2DDoubleArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DDoubleArray(indices);
	}
	
	public float[][] getColumnsAs2DFloatArray(int[] indices) {
		float[][] columns = new float[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsFloatArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public float[][] getColumnsAs2DFloatArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DFloatArray(indices);
	}
	
	public float[][] getColumnsAs2DFloatArray(ArrayList<String> names) {
		return getColumnsAs2DFloatArray(names.toArray(new String[0]));
	}
	
	public float[][] getColumnsAs2DFloatArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DFloatArray(indicesToGet);
	}
	
	public float[][] getColumnsAs2DFloatArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DFloatArray(columnIndices);
	}
	
	public float[][] getColumnsAs2DFloatArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DFloatArray(indices);
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
	
	public boolean[][] getColumnsAs2DBooleanArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DBooleanArray(columnIndices);
	}
	
	public boolean[][] getColumnsAs2DBooleanArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DBooleanArray(indices);
	}
	
	public LocalDate[][] getColumnsAs2DLocalDateArray(int[] indices) {
		LocalDate[][] columns = new LocalDate[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsLocalDateArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public LocalDate[][] getColumnsAs2DLocalDateArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DLocalDateArray(indices);
	}
	
	public LocalDate[][] getColumnsAs2DLocalDateArray(ArrayList<String> names) {
		return getColumnsAs2DLocalDateArray(names.toArray(new String[0]));
	}
	
	public LocalDate[][] getColumnsAs2DLocalDateArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DLocalDateArray(indicesToGet);
	}
	
	public LocalDate[][] getColumnsAs2DLocalDateArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DLocalDateArray(columnIndices);
	}
	
	public LocalDate[][] getColumnsAs2DLocalDateArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DLocalDateArray(indices);
	}
	
	public LocalDateTime[][] getColumnsAs2DLocalDateTimeArray(int[] indices) {
		LocalDateTime[][] columns = new LocalDateTime[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsLocalDateTimeArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public LocalDateTime[][] getColumnsAs2DLocalDateTimeArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DLocalDateTimeArray(indices);
	}
	
	public LocalDateTime[][] getColumnsAs2DLocalDateTimeArray(ArrayList<String> names) {
		return getColumnsAs2DLocalDateTimeArray(names.toArray(new String[0]));
	}
	
	public LocalDateTime[][] getColumnsAs2DLocalDateTimeArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DLocalDateTimeArray(indicesToGet);
	}
	
	public LocalDateTime[][] getColumnsAs2DLocalDateTimeArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DLocalDateTimeArray(columnIndices);
	}
	
	public LocalDateTime[][] getColumnsAs2DLocalDateTimeArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DLocalDateTimeArray(indices);
	}
	
	public LocalTime[][] getColumnsAs2DLocalTimeArray(int[] indices) {
		LocalTime[][] columns = new LocalTime[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsLocalTimeArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public LocalTime[][] getColumnsAs2DLocalTimeArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DLocalTimeArray(indices);
	}
	
	public LocalTime[][] getColumnsAs2DLocalTimeArray(ArrayList<String> names) {
		return getColumnsAs2DLocalTimeArray(names.toArray(new String[0]));
	}
	
	public LocalTime[][] getColumnsAs2DLocalTimeArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DLocalTimeArray(indicesToGet);
	}
	
	public LocalTime[][] getColumnsAs2DLocalTimeArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DLocalTimeArray(columnIndices);
	}
	
	public LocalTime[][] getColumnsAs2DLocalTimeArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DLocalTimeArray(indices);
	}
	
	public Period[][] getColumnsAs2DPeriodArray(int[] indices) {
		Period[][] columns = new Period[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsPeriodArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public Period[][] getColumnsAs2DPeriodArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DPeriodArray(indices);
	}
	
	public Period[][] getColumnsAs2DPeriodArray(ArrayList<String> names) {
		return getColumnsAs2DPeriodArray(names.toArray(new String[0]));
	}
	
	public Period[][] getColumnsAs2DPeriodArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DPeriodArray(indicesToGet);
	}
	
	public Period[][] getColumnsAs2DPeriodArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DPeriodArray(columnIndices);
	}
	
	public Period[][] getColumnsAs2DPeriodArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DPeriodArray(indices);
	}
	
	public Duration[][] getColumnsAs2DDurationArray(int[] indices) {
		Duration[][] columns = new Duration[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsDurationArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public Duration[][] getColumnsAs2DDurationArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DDurationArray(indices);
	}
	
	public Duration[][] getColumnsAs2DDurationArray(ArrayList<String> names) {
		return getColumnsAs2DDurationArray(names.toArray(new String[0]));
	}
	
	public Duration[][] getColumnsAs2DDurationArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DDurationArray(indicesToGet);
	}
	
	public Duration[][] getColumnsAs2DDurationArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DDurationArray(columnIndices);
	}
	
	public Duration[][] getColumnsAs2DDurationArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DDurationArray(indices);
	}



	public BigDecimal[][] getColumnsAs2DBigDecimalArray(int[] indices) {
		BigDecimal[][] columns = new BigDecimal[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsBigDecimalArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public BigDecimal[][] getColumnsAs2DBigDecimalArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DBigDecimalArray(indices);
	}
	
	public BigDecimal[][] getColumnsAs2DBigDecimalArray(ArrayList<String> names) {
		return getColumnsAs2DBigDecimalArray(names.toArray(new String[0]));
	}
	
	public BigDecimal[][] getColumnsAs2DBigDecimalArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DBigDecimalArray(indicesToGet);
	}
	
	public BigDecimal[][] getColumnsAs2DBigDecimalArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DBigDecimalArray(columnIndices);
	}
	
	public BigDecimal[][] getColumnsAs2DBigDecimalArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DBigDecimalArray(indices);
	}

	public Color[][] getColumnsAs2DColorArray(int[] indices) {
		Color[][] columns = new Color[indices.length][this.rowNames.size()];
		for (int columnCount = 0; columnCount < indices.length; columnCount++) {
			columns[columnCount] = getColumnAsColorArray(indices[columnCount]);
		}
		
		return columns;
	}
	
	public Color[][] getColumnsAs2DColorArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, names);
		return getColumnsAs2DColorArray(indices);
	}
	
	public Color[][] getColumnsAs2DColorArray(ArrayList<String> names) {
		return getColumnsAs2DColorArray(names.toArray(new String[0]));
	}
	
	public Color[][] getColumnsAs2DColorArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getColumnsAs2DColorArray(indicesToGet);
	}
	
	public Color[][] getColumnsAs2DColorArray(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAs2DColorArray(columnIndices);
	}
	
	public Color[][] getColumnsAs2DColorArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAs2DColorArray(indices);
	}	
	
	public List<List<DataItem>> getColumnsAs2DDataItemList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDataItemArray(indices));
	}

	public List<List<DataItem>> getColumnsAs2DDataItemList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDataItemArray(names));
	}

	public List<List<DataItem>> getColumnsAs2DDataItemList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDataItemArray(names));
	}

	public List<List<DataItem>> getColumnsAs2DDataItemList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDataItemArray(lowerBound, upperBound));
	}

	public List<List<DataItem>> getColumnsAs2DDataItemList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDataItemArray(getColumn));
	}
	
	public List<List<DataItem>> getColumnsAs2DDataItemList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDataItemArray(regex));
	}

	public List<List<Object>> getColumnsAs2DObjectList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DObjectArray(indices));
	}

	public List<List<Object>> getColumnsAs2DObjectList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DObjectArray(names));
	}

	public List<List<Object>> getColumnsAs2DObjectList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DObjectArray(names));
	}

	public List<List<Object>> getColumnsAs2DObjectList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DObjectArray(lowerBound, upperBound));
	}

	public List<List<Object>> getColumnsAs2DObjectList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DObjectArray(getColumn));
	}
	
	public List<List<Object>> getColumnsAs2DObjectList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DObjectArray(regex));
	}

	public List<List<String>> getColumnsAs2DStringList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DStringArray(indices));
	}

	public List<List<String>> getColumnsAs2DStringList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DStringArray(names));
	}

	public List<List<String>> getColumnsAs2DStringList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DStringArray(names));
	}

	public List<List<String>> getColumnsAs2DStringList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DStringArray(lowerBound, upperBound));
	}

	public List<List<String>> getColumnsAs2DStringList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DStringArray(getColumn));
	}
	
	public List<List<String>> getColumnsAs2DStringList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DStringArray(regex));
	}

	public List<List<Integer>> getColumnsAs2DIntList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DIntArray(indices));
	}

	public List<List<Integer>> getColumnsAs2DIntList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DIntArray(names));
	}

	public List<List<Integer>> getColumnsAs2DIntList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DIntArray(names));
	}

	public List<List<Integer>> getColumnsAs2DIntList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DIntArray(lowerBound, upperBound));
	}

	public List<List<Integer>> getColumnsAs2DIntList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DIntArray(getColumn));
	}
	
	public List<List<Integer>> getColumnsAs2DIntList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DIntArray(regex));
	}

	public List<List<Double>> getColumnsAs2DDoubleList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDoubleArray(indices));
	}

	public List<List<Double>> getColumnsAs2DDoubleList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDoubleArray(names));
	}

	public List<List<Double>> getColumnsAs2DDoubleList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDoubleArray(names));
	}

	public List<List<Double>> getColumnsAs2DDoubleList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDoubleArray(lowerBound, upperBound));
	}

	public List<List<Double>> getColumnsAs2DDoubleList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDoubleArray(getColumn));
	}
	
	public List<List<Double>> getColumnsAs2DDoubleList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDoubleArray(regex));
	}

	public List<List<Float>> getColumnsAs2DFloatList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DFloatArray(indices));
	}

	public List<List<Float>> getColumnsAs2DFloatList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DFloatArray(names));
	}

	public List<List<Float>> getColumnsAs2DFloatList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DFloatArray(names));
	}

	public List<List<Float>> getColumnsAs2DFloatList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DFloatArray(lowerBound, upperBound));
	}

	public List<List<Float>> getColumnsAs2DFloatList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DFloatArray(getColumn));
	}
	
	public List<List<Float>> getColumnsAs2DFloatList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DFloatArray(regex));
	}

	public List<List<Boolean>> getColumnsAs2DBooleanList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBooleanArray(indices));
	}

	public List<List<Boolean>> getColumnsAs2DBooleanList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBooleanArray(names));
	}

	public List<List<Boolean>> getColumnsAs2DBooleanList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBooleanArray(names));
	}

	public List<List<Boolean>> getColumnsAs2DBooleanList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBooleanArray(lowerBound, upperBound));
	}

	public List<List<Boolean>> getColumnsAs2DBooleanList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBooleanArray(getColumn));
	}
	
	public List<List<Boolean>> getColumnsAs2DBooleanList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBooleanArray(regex));
	}

	public List<List<LocalDate>> getColumnsAs2DLocalDateList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateArray(indices));
	}

	public List<List<LocalDate>> getColumnsAs2DLocalDateList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateArray(names));
	}

	public List<List<LocalDate>> getColumnsAs2DLocalDateList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateArray(names));
	}

	public List<List<LocalDate>> getColumnsAs2DLocalDateList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateArray(lowerBound, upperBound));
	}

	public List<List<LocalDate>> getColumnsAs2DLocalDateList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateArray(getColumn));
	}
	
	public List<List<LocalDate>> getColumnsAs2DLocalDateList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateArray(regex));
	}

	public List<List<LocalDateTime>> getColumnsAs2DLocalDateTimeList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateTimeArray(indices));
	}

	public List<List<LocalDateTime>> getColumnsAs2DLocalDateTimeList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateTimeArray(names));
	}

	public List<List<LocalDateTime>> getColumnsAs2DLocalDateTimeList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateTimeArray(names));
	}

	public List<List<LocalDateTime>> getColumnsAs2DLocalDateTimeList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateTimeArray(lowerBound, upperBound));
	}

	public List<List<LocalDateTime>> getColumnsAs2DLocalDateTimeList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateTimeArray(getColumn));
	}
	
	public List<List<LocalDateTime>> getColumnsAs2DLocalDateTimeList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalDateTimeArray(regex));
	}

	public List<List<LocalTime>> getColumnsAs2DLocalTimeList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalTimeArray(indices));
	}

	public List<List<LocalTime>> getColumnsAs2DLocalTimeList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalTimeArray(names));
	}

	public List<List<LocalTime>> getColumnsAs2DLocalTimeList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalTimeArray(names));
	}

	public List<List<LocalTime>> getColumnsAs2DLocalTimeList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalTimeArray(lowerBound, upperBound));
	}

	public List<List<LocalTime>> getColumnsAs2DLocalTimeList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalTimeArray(getColumn));
	}

	public List<List<LocalTime>> getColumnsAs2DLocalTimeList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DLocalTimeArray(regex));
	}
	
	public List<List<Period>> getColumnsAs2DPeriodList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DPeriodArray(indices));
	}

	public List<List<Period>> getColumnsAs2DPeriodList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DPeriodArray(names));
	}

	public List<List<Period>> getColumnsAs2DPeriodList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DPeriodArray(names));
	}

	public List<List<Period>> getColumnsAs2DPeriodList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DPeriodArray(lowerBound, upperBound));
	}

	public List<List<Period>> getColumnsAs2DPeriodList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DPeriodArray(getColumn));
	}
	
	public List<List<Period>> getColumnsAs2DPeriodList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DPeriodArray(regex));
	}

	public List<List<Duration>> getColumnsAs2DDurationList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDurationArray(indices));
	}

	public List<List<Duration>> getColumnsAs2DDurationList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDurationArray(names));
	}

	public List<List<Duration>> getColumnsAs2DDurationList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDurationArray(names));
	}

	public List<List<Duration>> getColumnsAs2DDurationList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDurationArray(lowerBound, upperBound));
	}

	public List<List<Duration>> getColumnsAs2DDurationList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDurationArray(getColumn));
	}
	
	public List<List<Duration>> getColumnsAs2DDurationList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DDurationArray(regex));
	}

	public List<List<BigDecimal>> getColumnsAs2DBigDecimalList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBigDecimalArray(indices));
	}

	public List<List<BigDecimal>> getColumnsAs2DBigDecimalList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBigDecimalArray(names));
	}

	public List<List<BigDecimal>> getColumnsAs2DBigDecimalList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBigDecimalArray(names));
	}

	public List<List<BigDecimal>> getColumnsAs2DBigDecimalList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBigDecimalArray(lowerBound, upperBound));
	}

	public List<List<BigDecimal>> getColumnsAs2DBigDecimalList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBigDecimalArray(getColumn));
	}
	
	public List<List<BigDecimal>> getColumnsAs2DBigDecimalList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DBigDecimalArray(regex));
	}

	public List<List<Color>> getColumnsAs2DColorList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DColorArray(indices));
	}

	public List<List<Color>> getColumnsAs2DColorList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DColorArray(names));
	}

	public List<List<Color>> getColumnsAs2DColorList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DColorArray(names));
	}

	public List<List<Color>> getColumnsAs2DColorList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DColorArray(lowerBound, upperBound));
	}

	public List<List<Color>> getColumnsAs2DColorList(boolean[] getColumn) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DColorArray(getColumn));
	}
	
	public List<List<Color>> getColumnsAs2DColorList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getColumnsAs2DColorArray(regex));
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
	
	public DataFrame getColumnsAsDataFrame(boolean[] getColumn) {
		int[] columnIndices = CommonArray.elementsOfTrues(getColumn);
		return this.getColumnsAsDataFrame(columnIndices);
	}
	
	public DataFrame getColumnsAsDataFrame(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.columnNames, regex);
		return this.getColumnsAsDataFrame(indices);
	}
	
	public StorageType getTypeOfColumn(int columnIndex) {
		StorageType[] types = new StorageType[this.getNumRows()];
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			types[rowCount] = this.getValue(columnIndex, rowCount).getType();
		}
		if ((CommonArray.numUnique(types) == 1) && (types.length > 0)) {
			return types[0];
		}
		return null;
	}
	
	public StorageType getTypeOfColumn(String columnName) {
		return getTypeOfColumn(this.columnNames.indexOf(columnName));
	}
	
	public DataFrame getColumnsOfTypeAsDataFrame(StorageType type) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int columnCount = 0; columnCount < this.getNumColumns(); columnCount++) {
			if (getTypeOfColumn(columnCount) == type) {
				indices.add(columnCount);
			}
		}
		return this.getColumnsAsDataFrame(indices.stream().mapToInt(i -> i).toArray());
	}
	
	public DataFrame getStringColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.String);
	}

	public DataFrame getIntegerColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.Integer);
	}

	public DataFrame getDoubleColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.Double);
	}

	public DataFrame getBooleanColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.Boolean);
	}

	public DataFrame getLocalDateColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.LocalDate);
	}

	public DataFrame getLocalDateTimeColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.LocalDateTime);
	}

	public DataFrame getLocalTimeColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.LocalTime);
	}

	public DataFrame getPeriodColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.Period);
	}

	public DataFrame getDurationColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.Duration);
	}

	public DataFrame getBigDecimalColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.BigDecimal);
	}

	public DataFrame getColorColumnsAsDataFrame() {
		return this.getColumnsOfTypeAsDataFrame(StorageType.Color);
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
	
	public Object[] getRowAsObjectArray(int index) {
		Object[] row = new Object[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getObjectValue();
		}
		return row;
	}

	public Object[] getRowAsObjectArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsObjectArray(index);
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
			row[i] = this.data.get(i).get(index).getValueConvertedToDouble();
		}
		return row;
	}

	public double[] getRowAsDoubleArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsDoubleArray(index);
	}
	
	public float[] getRowAsFloatArray(int index) {
		float[] row = new float[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getValueConvertedToFloat();
		}
		return row;
	}
	
	public float[] getRowAsFloatArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsFloatArray(index);
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

	public LocalDate[] getRowAsLocalDateArray(int index) {
		LocalDate[] row = new LocalDate[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getDateValue();
		}
		return row;
	}
	
	public LocalDate[] getRowAsLocalDateArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsLocalDateArray(index);
	}

	
	public LocalDateTime[] getRowAsLocalDateTimeArray(int index) {
		LocalDateTime[] row = new LocalDateTime[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getDateTimeValue();
		}
		return row;
	}

	public LocalDateTime[] getRowAsLocalDateTimeArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsLocalDateTimeArray(index);
	}

	public LocalTime[] getRowAsLocalTimeArray(int index) {
		LocalTime[] row = new LocalTime[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getTimeValue();
		}
		return row;
	}

	public LocalTime[] getRowAsLocalTimeArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsLocalTimeArray(index);
	}

	public Period[] getRowAsPeriodArray(int index) {
		Period[] row = new Period[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getPeriodValue();
		}
		return row;
	}

	public Period[] getRowAsPeriodArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsPeriodArray(index);
	}

	public Duration[] getRowAsDurationArray(int index) {
		Duration[] row = new Duration[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getDurationValue();
		}
		return row;
	}

	public Duration[] getRowAsDurationArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsDurationArray(index);
	}

	public BigDecimal[] getRowAsBigDecimalArray(int index) {
		BigDecimal[] row = new BigDecimal[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getBigDecimalValue();
		}
		return row;
	}

	public BigDecimal[] getRowAsBigDecimalArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsBigDecimalArray(index);
	}

	public Color[] getRowAsColorArray(int index) {
		Color[] row = new Color[this.columnNames.size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = this.data.get(i).get(index).getColorValue();
		}
		return row;
	}

	public Color[] getRowAsColorArray(String name) {
		int index = this.rowNames.indexOf(name);
		return getRowAsColorArray(index);
	}

	public List<DataItem> getRowAsDataItemList(int index) {
		return Arrays.asList(this.getRowAsDataItemArray(index));
	}

	public List<DataItem> getRowAsDataItemList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsDataItemList(index);
	}

	public List<Object> getRowAsObjectList(int index) {
		return Arrays.asList(this.getRowAsObjectArray(index));
	}

	public List<Object> getRowAsObjectList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsObjectList(index);
	}

	public List<String> getRowAsStringList(int index) {
		return Arrays.asList(this.getRowAsStringArray(index));
	}

	public List<String> getRowAsStringList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsStringList(index);
	}

	public List<Integer> getRowAsIntList(int index) {
		return Arrays.stream(this.getRowAsIntArray(index)).boxed().collect(Collectors.toList());
	}

	public List<Integer> getRowAsIntList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsIntList(index);
	}

	public List<Double> getRowAsDoubleList(int index) {
		return Arrays.stream(this.getRowAsDoubleArray(index)).boxed().collect(Collectors.toList());
	}

	public List<Double> getRowAsDoubleList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsDoubleList(index);
	}

	public List<Float> getRowAsFloatList(int index) {
		return CommonArray.convertFloatArrayToFloatList(this.getRowAsFloatArray(index));
	}

	public List<Float> getRowAsFloatList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsFloatList(index);
	}

	public List<Boolean> getRowAsBooleanList(int index) {
		return CommonArray.convertBooleanArrayToBooleanList(this.getRowAsBooleanArray(index));
	}

	public List<Boolean> getRowAsBooleanList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsBooleanList(index);
	}

	public List<LocalDate> getRowAsLocalDateList(int index) {
		return Arrays.asList(this.getRowAsLocalDateArray(index));
	}

	public List<LocalDate> getRowAsLocalDateList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsLocalDateList(index);
	}

	public List<LocalDateTime> getRowAsLocalDateTimeList(int index) {
		return Arrays.asList(this.getRowAsLocalDateTimeArray(index));
	}

	public List<LocalDateTime> getRowAsLocalDateTimeList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsLocalDateTimeList(index);
	}

	public List<LocalTime> getRowAsLocalTimeList(int index) {
		return Arrays.asList(this.getRowAsLocalTimeArray(index));
	}

	public List<LocalTime> getRowAsLocalTimeList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsLocalTimeList(index);
	}

	public List<Period> getRowAsPeriodList(int index) {
		return Arrays.asList(this.getRowAsPeriodArray(index));
	}

	public List<Period> getRowAsPeriodList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsPeriodList(index);
	}

	public List<Duration> getRowAsDurationList(int index) {
		return Arrays.asList(this.getRowAsDurationArray(index));
	}

	public List<Duration> getRowAsDurationList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsDurationList(index);
	}

	public List<BigDecimal> getRowAsBigDecimalList(int index) {
		return Arrays.asList(this.getRowAsBigDecimalArray(index));
	}

	public List<BigDecimal> getRowAsBigDecimalList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsBigDecimalList(index);
	}
	
	public List<Color> getRowAsColorList(int index) {
		return Arrays.asList(this.getRowAsColorArray(index));
	}

	public List<Color> getRowAsColorList(String name) {
		int index = this.rowNames.indexOf(name);
		return this.getRowAsColorList(index);
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
	
	public DataItem[][] getRowsAs2DDataItemArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DDataItemArray(columnIndices);
	}
	
	public DataItem[][] getRowsAs2DDataItemArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DDataItemArray(indices);
	}
	
	public Object[][] getRowsAs2DObjectArray(int[] indices) {
		Object[][] rows = new Object[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsObjectArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public Object[][] getRowsAs2DObjectArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DObjectArray(indices);
	}
	
	public Object[][] getRowsAs2DObjectArray(ArrayList<String> names) {
		return getRowsAs2DObjectArray(names.toArray(new String[0]));
	}

	public Object[][] getRowsAs2DObjectArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DObjectArray(indicesToGet);
	}
	
	public Object[][] getRowsAs2DObjectArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DObjectArray(columnIndices);
	}
	
	public Object[][] getRowsAs2DObjectArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DObjectArray(indices);
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
	
	public String[][] getRowsAs2DStringArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DStringArray(columnIndices);
	}
	
	public String[][] getRowsAs2DStringArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DStringArray(indices);
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
	
	public int[][] getRowsAs2DIntArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DIntArray(columnIndices);
	}
	
	public int[][] getRowsAs2DIntArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DIntArray(indices);
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
	
	public double[][] getRowsAs2DDoubleArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DDoubleArray(columnIndices);
	}
	
	public double[][] getRowsAs2DDoubleArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DDoubleArray(indices);
	}
	
	public float[][] getRowsAs2DFloatArray(int[] indices) {
		float[][] rows = new float[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsFloatArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public float[][] getRowsAs2DFloatArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DFloatArray(indices);
	}
	
	public float[][] getRowsAs2DFloatArray(ArrayList<String> names) {
		return getRowsAs2DFloatArray(names.toArray(new String[0]));
	}
	
	public float[][] getRowsAs2DFloatArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DFloatArray(indicesToGet);
	}
	
	public float[][] getRowsAs2DFloatArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DFloatArray(columnIndices);
	}
	
	public float[][] getRowsAs2DFloatArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DFloatArray(indices);
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
	
	public boolean[][] getRowsAs2DBooleanArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DBooleanArray(columnIndices);
	}
	
	public boolean[][] getRowsAs2DBooleanArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DBooleanArray(indices);
	}

	public LocalDate[][] getRowsAs2DLocalDateArray(int[] indices) {
		LocalDate[][] rows = new LocalDate[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsLocalDateArray(indices[rowCount]);
		}
		
		return rows;
	}
	
	public LocalDate[][] getRowsAs2DLocalDateArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DLocalDateArray(indices);
	}
	
	public LocalDate[][] getRowsAs2DLocalDateArray(ArrayList<String> names) {
		return getRowsAs2DLocalDateArray(names.toArray(new String[0]));
	}
	
	public LocalDate[][] getRowsAs2DLocalDateArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DLocalDateArray(indicesToGet);
	}
	
	public LocalDate[][] getRowsAs2DLocalDateArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DLocalDateArray(columnIndices);
	}
	
	public LocalDate[][] getRowsAs2DLocalDateArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DLocalDateArray(indices);
	}
	
	public LocalDateTime[][] getRowsAs2DLocalDateTimeArray(int[] indices) {
		LocalDateTime[][] rows = new LocalDateTime[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsLocalDateTimeArray(indices[rowCount]);
		}
		
		return rows;
	}

	public LocalDateTime[][] getRowsAs2DLocalDateTimeArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DLocalDateTimeArray(indices);
	}

	public LocalDateTime[][] getRowsAs2DLocalDateTimeArray(ArrayList<String> names) {
		return getRowsAs2DLocalDateTimeArray(names.toArray(new String[0]));
	}

	public LocalDateTime[][] getRowsAs2DLocalDateTimeArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DLocalDateTimeArray(indicesToGet);
	}

	public LocalDateTime[][] getRowsAs2DLocalDateTimeArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DLocalDateTimeArray(columnIndices);
	}
	
	public LocalDateTime[][] getRowsAs2DLocalDateTimeArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DLocalDateTimeArray(indices);
	}

	public LocalTime[][] getRowsAs2DLocalTimeArray(int[] indices) {
		LocalTime[][] rows = new LocalTime[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsLocalTimeArray(indices[rowCount]);
		}
		
		return rows;
	}

	public LocalTime[][] getRowsAs2DLocalTimeArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DLocalTimeArray(indices);
	}

	public LocalTime[][] getRowsAs2DLocalTimeArray(ArrayList<String> names) {
		return getRowsAs2DLocalTimeArray(names.toArray(new String[0]));
	}

	public LocalTime[][] getRowsAs2DLocalTimeArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DLocalTimeArray(indicesToGet);
	}

	public LocalTime[][] getRowsAs2DLocalTimeArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DLocalTimeArray(columnIndices);
	}
	
	public LocalTime[][] getRowsAs2DLocalTimeArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DLocalTimeArray(indices);
	}

	public Period[][] getRowsAs2DPeriodArray(int[] indices) {
		Period[][] rows = new Period[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsPeriodArray(indices[rowCount]);
		}
		
		return rows;
	}

	public Period[][] getRowsAs2DPeriodArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DPeriodArray(indices);
	}

	public Period[][] getRowsAs2DPeriodArray(ArrayList<String> names) {
		return getRowsAs2DPeriodArray(names.toArray(new String[0]));
	}

	public Period[][] getRowsAs2DPeriodArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DPeriodArray(indicesToGet);
	}

	public Period[][] getRowsAs2DPeriodArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DPeriodArray(columnIndices);
	}
	
	public Period[][] getRowsAs2DPeriodArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DPeriodArray(indices);
	}

	public Duration[][] getRowsAs2DDurationArray(int[] indices) {
		Duration[][] rows = new Duration[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsDurationArray(indices[rowCount]);
		}
		
		return rows;
	}

	public Duration[][] getRowsAs2DDurationArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DDurationArray(indices);
	}

	public Duration[][] getRowsAs2DDurationArray(ArrayList<String> names) {
		return getRowsAs2DDurationArray(names.toArray(new String[0]));
	}

	public Duration[][] getRowsAs2DDurationArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DDurationArray(indicesToGet);
	}

	public Duration[][] getRowsAs2DDurationArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DDurationArray(columnIndices);
	}

	public Duration[][] getRowsAs2DDurationArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DDurationArray(indices);
	}

	public BigDecimal[][] getRowsAs2DBigDecimalArray(int[] indices) {
		BigDecimal[][] rows = new BigDecimal[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsBigDecimalArray(indices[rowCount]);
		}
		
		return rows;
	}

	public BigDecimal[][] getRowsAs2DBigDecimalArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DBigDecimalArray(indices);
	}

	public BigDecimal[][] getRowsAs2DBigDecimalArray(ArrayList<String> names) {
		return getRowsAs2DBigDecimalArray(names.toArray(new String[0]));
	}

	public BigDecimal[][] getRowsAs2DBigDecimalArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DBigDecimalArray(indicesToGet);
	}

	public BigDecimal[][] getRowsAs2DBigDecimalArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DBigDecimalArray(columnIndices);
	}

	public BigDecimal[][] getRowsAs2DBigDecimalArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DBigDecimalArray(indices);
	}

	public Color[][] getRowsAs2DColorArray(int[] indices) {
		Color[][] rows = new Color[indices.length][this.columnNames.size()];
		for (int rowCount = 0; rowCount < indices.length; rowCount++) {
			rows[rowCount] = getRowAsColorArray(indices[rowCount]);
		}
		
		return rows;
	}

	public Color[][] getRowsAs2DColorArray(String[] names) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.rowNames, names);
		return getRowsAs2DColorArray(indices);
	}

	public Color[][] getRowsAs2DColorArray(ArrayList<String> names) {
		return getRowsAs2DColorArray(names.toArray(new String[0]));
	}

	public Color[][] getRowsAs2DColorArray(int lowerBound, int upperBound) {
		int[] indicesToGet = IntStream.rangeClosed(lowerBound, upperBound).toArray();
		return getRowsAs2DColorArray(indicesToGet);
	}

	public Color[][] getRowsAs2DColorArray(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAs2DColorArray(columnIndices);
	}

	public Color[][] getRowsAs2DColorArray(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAs2DColorArray(indices);
	}

	
	public List<List<DataItem>> getRowsAs2DDataItemList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDataItemArray(indices));
	}

	public List<List<DataItem>> getRowsAs2DDataItemList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDataItemArray(names));
	}

	public List<List<DataItem>> getRowsAs2DDataItemList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDataItemArray(names));
	}

	public List<List<DataItem>> getRowsAs2DDataItemList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDataItemArray(lowerBound, upperBound));
	}

	public List<List<DataItem>> getRowsAs2DDataItemList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDataItemArray(getRow));
	}
	
	public List<List<DataItem>> getRowsAs2DDataItemList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDataItemArray(regex));
	}

	public List<List<Object>> getRowsAs2DObjectList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DObjectArray(indices));
	}

	public List<List<Object>> getRowsAs2DObjectList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DObjectArray(names));
	}

	public List<List<Object>> getRowsAs2DObjectList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DObjectArray(names));
	}

	public List<List<Object>> getRowsAs2DObjectList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DObjectArray(lowerBound, upperBound));
	}

	public List<List<Object>> getRowsAs2DObjectList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DObjectArray(getRow));
	}
	
	public List<List<Object>> getRowsAs2DObjectList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DObjectArray(regex));
	}

	public List<List<String>> getRowsAs2DStringList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DStringArray(indices));
	}

	public List<List<String>> getRowsAs2DStringList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DStringArray(names));
	}

	public List<List<String>> getRowsAs2DStringList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DStringArray(names));
	}

	public List<List<String>> getRowsAs2DStringList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DStringArray(lowerBound, upperBound));
	}

	public List<List<String>> getRowsAs2DStringList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DStringArray(getRow));
	}

	public List<List<String>> getRowsAs2DStringList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DStringArray(regex));
	}
	
	public List<List<Integer>> getRowsAs2DIntList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DIntArray(indices));
	}

	public List<List<Integer>> getRowsAs2DIntList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DIntArray(names));
	}

	public List<List<Integer>> getRowsAs2DIntList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DIntArray(names));
	}

	public List<List<Integer>> getRowsAs2DIntList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DIntArray(lowerBound, upperBound));
	}

	public List<List<Integer>> getRowsAs2DIntList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DIntArray(getRow));
	}
	
	public List<List<Integer>> getRowsAs2DIntList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DIntArray(regex));
	}

	public List<List<Double>> getRowsAs2DDoubleList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDoubleArray(indices));
	}

	public List<List<Double>> getRowsAs2DDoubleList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDoubleArray(names));
	}

	public List<List<Double>> getRowsAs2DDoubleList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDoubleArray(names));
	}

	public List<List<Double>> getRowsAs2DDoubleList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDoubleArray(lowerBound, upperBound));
	}

	public List<List<Double>> getRowsAs2DDoubleList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDoubleArray(getRow));
	}

	public List<List<Double>> getRowsAs2DDoubleList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDoubleArray(regex));
	}
	
	public List<List<Float>> getRowsAs2DFloatList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DFloatArray(indices));
	}

	public List<List<Float>> getRowsAs2DFloatList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DFloatArray(names));
	}

	public List<List<Float>> getRowsAs2DFloatList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DFloatArray(names));
	}

	public List<List<Float>> getRowsAs2DFloatList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DFloatArray(lowerBound, upperBound));
	}

	public List<List<Float>> getRowsAs2DFloatList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DFloatArray(getRow));
	}
	
	public List<List<Float>> getRowsAs2DFloatList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DFloatArray(regex));
	}

	public List<List<Boolean>> getRowsAs2DBooleanList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBooleanArray(indices));
	}

	public List<List<Boolean>> getRowsAs2DBooleanList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBooleanArray(names));
	}

	public List<List<Boolean>> getRowsAs2DBooleanList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBooleanArray(names));
	}

	public List<List<Boolean>> getRowsAs2DBooleanList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBooleanArray(lowerBound, upperBound));
	}

	public List<List<Boolean>> getRowsAs2DBooleanList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBooleanArray(getRow));
	}

	public List<List<Boolean>> getRowsAs2DBooleanList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBooleanArray(regex));
	}
	
	public List<List<LocalDate>> getRowsAs2DLocalDateList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateArray(indices));
	}

	public List<List<LocalDate>> getRowsAs2DLocalDateList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateArray(names));
	}

	public List<List<LocalDate>> getRowsAs2DLocalDateList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateArray(names));
	}

	public List<List<LocalDate>> getRowsAs2DLocalDateList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateArray(lowerBound, upperBound));
	}

	public List<List<LocalDate>> getRowsAs2DLocalDateList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateArray(getRow));
	}
	
	public List<List<LocalDate>> getRowsAs2DLocalDateList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateArray(regex));
	}

	public List<List<LocalDateTime>> getRowsAs2DLocalDateTimeList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateTimeArray(indices));
	}

	public List<List<LocalDateTime>> getRowsAs2DLocalDateTimeList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateTimeArray(names));
	}

	public List<List<LocalDateTime>> getRowsAs2DLocalDateTimeList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateTimeArray(names));
	}

	public List<List<LocalDateTime>> getRowsAs2DLocalDateTimeList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateTimeArray(lowerBound, upperBound));
	}

	public List<List<LocalDateTime>> getRowsAs2DLocalDateTimeList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateTimeArray(getRow));
	}
	
	public List<List<LocalDateTime>> getRowsAs2DLocalDateTimeList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalDateTimeArray(regex));
	}

	public List<List<LocalTime>> getRowsAs2DLocalTimeList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalTimeArray(indices));
	}

	public List<List<LocalTime>> getRowsAs2DLocalTimeList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalTimeArray(names));
	}

	public List<List<LocalTime>> getRowsAs2DLocalTimeList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalTimeArray(names));
	}

	public List<List<LocalTime>> getRowsAs2DLocalTimeList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalTimeArray(lowerBound, upperBound));
	}

	public List<List<LocalTime>> getRowsAs2DLocalTimeList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalTimeArray(getRow));
	}
	
	public List<List<LocalTime>> getRowsAs2DLocalTimeList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DLocalTimeArray(regex));
	}

	public List<List<Period>> getRowsAs2DPeriodList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DPeriodArray(indices));
	}

	public List<List<Period>> getRowsAs2DPeriodList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DPeriodArray(names));
	}

	public List<List<Period>> getRowsAs2DPeriodList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DPeriodArray(names));
	}

	public List<List<Period>> getRowsAs2DPeriodList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DPeriodArray(lowerBound, upperBound));
	}

	public List<List<Period>> getRowsAs2DPeriodList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DPeriodArray(getRow));
	}

	public List<List<Period>> getRowsAs2DPeriodList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DPeriodArray(regex));
	}

	public List<List<Duration>> getRowsAs2DDurationList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDurationArray(indices));
	}

	public List<List<Duration>> getRowsAs2DDurationList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDurationArray(names));
	}

	public List<List<Duration>> getRowsAs2DDurationList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDurationArray(names));
	}

	public List<List<Duration>> getRowsAs2DDurationList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDurationArray(lowerBound, upperBound));
	}

	public List<List<Duration>> getRowsAs2DDurationList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDurationArray(getRow));
	}
	
	public List<List<Duration>> getRowsAs2DDurationList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DDurationArray(regex));
	}

	public List<List<BigDecimal>> getRowsAs2DBigDecimalList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBigDecimalArray(indices));
	}

	public List<List<BigDecimal>> getRowsAs2DBigDecimalList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBigDecimalArray(names));
	}

	public List<List<BigDecimal>> getRowsAs2DBigDecimalList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBigDecimalArray(names));
	}

	public List<List<BigDecimal>> getRowsAs2DBigDecimalList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBigDecimalArray(lowerBound, upperBound));
	}

	public List<List<BigDecimal>> getRowsAs2DBigDecimalList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBigDecimalArray(getRow));
	}
	
	public List<List<BigDecimal>> getRowsAs2DBigDecimalList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DBigDecimalArray(regex));
	}

	public List<List<Color>> getRowsAs2DColorList(int[] indices) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DColorArray(indices));
	}

	public List<List<Color>> getRowsAs2DColorList(String[] names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DColorArray(names));
	}

	public List<List<Color>> getRowsAs2DColorList(ArrayList<String> names) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DColorArray(names));
	}

	public List<List<Color>> getRowsAs2DColorList(int lowerBound, int upperBound) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DColorArray(lowerBound, upperBound));
	}

	public List<List<Color>> getRowsAs2DColorList(boolean[] getRow) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DColorArray(getRow));
	}
	
	public List<List<Color>> getRowsAs2DColorList(Pattern regex) {
		return CommonArray.convert2DArrayTo2DArrayList(this.getRowsAs2DColorArray(regex));
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
	
	public DataFrame getRowsAsDataFrame(boolean[] getRow) {
		int[] columnIndices = CommonArray.elementsOfTrues(getRow);
		return this.getRowsAsDataFrame(columnIndices);
	}
	
	public DataFrame getRowsAsDataFrame(Pattern regex) {
		int[] indices = CommonArray.getIndicesOfListThatMatchRegex(this.rowNames, regex);
		return this.getRowsAsDataFrame(indices);
	}
	public StorageType getTypeOfRow(int rowIndex) {
		StorageType[] types = new StorageType[this.getNumColumns()];
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			types[colCount] = this.getValue(colCount, rowIndex).getType();
		}
		if ((CommonArray.numUnique(types) == 1) && (types.length > 0)) {
			return types[0];
		}
		return null;
	}

	public StorageType getTypeOfRow(String rowName) {
		return getTypeOfRow(this.rowNames.indexOf(rowName));
	}

	public DataFrame getRowsOfTypeAsDataFrame(StorageType type) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			if (getTypeOfRow(rowCount) == type) {
				indices.add(rowCount);
			}
		}
		return this.getRowsAsDataFrame(indices.stream().mapToInt(i -> i).toArray());
	}

	public DataFrame getStringRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.String);
	}

	public DataFrame getIntegerRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.Integer);
	}

	public DataFrame getDoubleRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.Double);
	}

	public DataFrame getBooleanRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.Boolean);
	}

	public DataFrame getLocalDateRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.LocalDate);
	}

	public DataFrame getLocalDateTimeRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.LocalDateTime);
	}

	public DataFrame getLocalTimeRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.LocalTime);
	}

	public DataFrame getPeriodRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.Period);
	}

	public DataFrame getDurationRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.Duration);
	}

	public DataFrame getBigDecimalRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.BigDecimal);
	}

	public DataFrame getColorRowsAsDataFrame() {
		return this.getRowsOfTypeAsDataFrame(StorageType.Color);
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
		DataFrame newDF = this.clone();
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumColumns(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					newDF.getValue(colCount, rowCount).add(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return newDF;
	}
	
	public DataFrame add(DataItem value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).add(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame add(int value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).add(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame add(double value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).add(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame add(float value) {
		DataFrame newDF = this.clone();
		return newDF.add((double) value);
	}
	
	public DataFrame add(Period timePeriod) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).add(timePeriod);
			}	
		}
		return newDF;
	}
	
	
	public DataFrame subtract(DataFrame df) {
		DataFrame newDF = this.clone();
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumColumns(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					newDF.getValue(colCount, rowCount).subtract(df.getValue(colCount, rowCount));
				}	
			}
		}
		return newDF;
	}
	
	public DataFrame subtract(DataItem value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).subtract(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame subtract(int value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).subtract(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame subtract(double value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).subtract(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame subtract(float value) {
		DataFrame newDF = this.clone();
		return newDF.subtract((double) value);
	}
	
	public DataFrame subtract(Period timePeriod) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).subtract(timePeriod);
			}	
		}
		return newDF;
	}
	

	public DataFrame and(DataFrame df) {
		DataFrame newDF = this.clone().getBooleanColumnsAsDataFrame();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).and(df.getValue(colCount, rowCount));
			}	
		}
		return newDF;
	}

	public DataFrame and(boolean value) {
		DataFrame newDF = this.clone().getBooleanColumnsAsDataFrame();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).and(value);
			}	
		}
		return newDF;
	}

	public DataFrame notAnd(DataFrame df) {
		return this.and(df).negate();
	}

	public DataFrame notAnd(boolean bool) {
		return this.and(bool).negate();
	}

	public DataFrame or(DataFrame df) {
		DataFrame newDF = this.clone().getBooleanColumnsAsDataFrame();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).or(df.getValue(colCount, rowCount));
			}	
		}
		return newDF;
	}

	public DataFrame or(boolean value) {
		DataFrame newDF = this.clone().getBooleanColumnsAsDataFrame();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).or(value);
			}	
		}
		return newDF;
	}

	public DataFrame notOr(DataFrame df) {
		return this.or(df).negate();
	}

	public DataFrame notOr(boolean value) {
		return this.or(value).negate();
	}

	public DataFrame exclusiveOr(DataFrame df) {
		DataFrame newDF = this.clone().getBooleanColumnsAsDataFrame();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).exclusiveOr(df.getValue(colCount, rowCount));
			}	
		}
		return newDF;
	}

	public DataFrame exclusiveOr(boolean value) {
		DataFrame newDF = this.clone().getBooleanColumnsAsDataFrame();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).exclusiveOr(value);
			}	
		}
		return newDF;
	}

	public DataFrame exclusiveNotOr(DataFrame df) {
		return this.exclusiveOr(df).negate();
	}

	public DataFrame exclusiveNotOr(boolean value) {
		return this.exclusiveOr(value).negate();
	}
	
	
	public DataFrame multiply(DataFrame df) {
		DataFrame newDF = this.clone();
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumColumns(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					newDF.getValue(colCount, rowCount).multiply(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return newDF;
	}
	
	public DataFrame multiply(DataItem value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).multiply(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame multiply(int value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).multiply(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame multiply(double value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).multiply(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame multiply(float value) {
		DataFrame newDF = this.clone();
		return newDF.multiply((double) value);
	}
	
	public DataFrame divide(DataFrame df) {
		DataFrame newDF = this.clone();
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumColumns(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					newDF.getValue(colCount, rowCount).divide(df.getValue(colCount, rowCount));
				}	
			}
		}
		
		return newDF;
	}
	
	public DataFrame divide(DataItem value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).divide(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame divide(int value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).divide(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame divide(double value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).divide(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame divide(float value) {
		DataFrame newDF = this.clone();
		return newDF.divide((double) value);
	}
	
	public DataFrame mod(DataFrame df) {
		DataFrame newDF = this.clone();
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumColumns(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					newDF.getValue(colCount, rowCount).mod(df.getValue(colCount, rowCount));
				}	
			}
		}
		return newDF;
	}
	
	public DataFrame mod(DataItem value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).mod(value);
			}	
		}
		return newDF;
	}
	
	public DataFrame mod(int value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).mod(value);
			}
		}
		return newDF;
	}
	
	public DataFrame power(DataFrame df) {
		DataFrame newDF = this.clone();
		if (this.sameShape(df)) {
			for (int colCount = 0; colCount < df.getNumColumns(); colCount++) {
				for (int rowCount = 0; rowCount < df.getNumRows(); rowCount++) {
					newDF.getValue(colCount, rowCount).power(df.getValue(colCount, rowCount));
				}	
			}
		}
		return newDF;
	}

	public DataFrame power(DataItem value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).power(value);
			}	
		}
		return newDF;
	}

	public DataFrame power(int value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).power(value);
			}	
		}
		return newDF;
	}

	public DataFrame power(double value) {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).power(value);
			}	
		}
		return newDF;
	}

	public DataFrame power(float value) {
		DataFrame newDF = this.clone();
		return newDF.power((double) value);
	}

	public DataFrame intFloor() {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).intFloor();
			}	
		}
		return newDF;
	}
	
	public DataFrame doubleFloor() {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).doubleFloor();
			}	
		}
		return newDF;
	}
	
	public DataFrame intCeiling() {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).intCeiling();
			}	
		}
		return newDF;
	}
	
	public DataFrame doubleCeiling() {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).doubleCeiling();
			}	
		}
		return newDF;
	}
	
	public DataFrame negate() {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				DataItem value = newDF.getValue(colCount, rowCount);
				if (value.getType() == StorageType.Boolean) {
					value.flip();
				} else {
					value.multiply(-1);
				}
				
			}	
		}
		return newDF;
	}
	
	public DataFrame squareRoot() {
		DataFrame newDF = this.clone();
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				newDF.getValue(colCount, rowCount).squareRoot();
			}	
		}
		return newDF;
	}

	
	// ------ Absolute Value ------ 
	public DataFrame absoluteValue() {
		return absoluteValueColumns(0, this.getNumColumns() - 1);
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
		return clampColumns(0, this.getNumColumns() - 1, lowerBound, upperBound);
	}

	public DataFrame clamp(LocalDate lowerBound, LocalDate upperBound) { 
		return clampColumns(0, this.getNumColumns() - 1, lowerBound, upperBound);
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
			for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
	

	
	public DataFrame columnLessThan(int columnIndex, DataItem value) {
		return columnLessThan(columnIndex, value.getValueConvertedToDouble());
	}

	public DataFrame columnLessThan(int columnIndex, int value) {
		return columnLessThan(columnIndex, (double) value);
	}

	public DataFrame columnLessThan(int columnIndex, double value) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			newDF.setValue(0, rowCount, this.getValue(columnIndex, rowCount).lessThan(value));
		}
		return newDF;
	}

	public DataFrame columnLessThan(int columnIndex, float value) {
		return columnLessThan(columnIndex, (double) value);
	}


	public DataFrame columnLessThan(String columnName, DataItem value) {
		return columnLessThan(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnLessThan(String columnName, int value) {
		return columnLessThan(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnLessThan(String columnName, double value) {
		return columnLessThan(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnLessThan(String columnName, float value) {
		return columnLessThan(this.columnNames.indexOf(columnName), value);
	}


	public DataFrame columnsLessThan(int[] columnIndices, DataItem value) {
		return columnsLessThan(columnIndices, value.getValueConvertedToDouble());
		
	}

	public DataFrame columnsLessThan(int[] columnIndices, int value) {
		return columnsLessThan(columnIndices,(double) value);
	}

	public DataFrame columnsLessThan(int[] columnIndices, double value) {
		DataFrame newDF = getColumnsAsDataFrame(columnIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				newDF.setValue(columnCount, rowCount, newDF.getValue(columnCount, rowCount).lessThan(value));
			}	
		}
		return newDF;
	}

	public DataFrame columnsLessThan(int[] columnIndices, float value) {
		return columnsLessThan(columnIndices,(double) value);
	}


	public DataFrame columnsLessThan(String[] columnNames, DataItem value) {
		return columnsLessThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsLessThan(String[] columnNames, int value) {
		return columnsLessThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsLessThan(String[] columnNames, double value) {
		return columnsLessThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsLessThan(String[] columnNames, float value) {
		return columnsLessThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}


	public DataFrame columnsLessThan(ArrayList<String> columnNames, DataItem value) {
		return columnsLessThan(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsLessThan(ArrayList<String> columnNames, int value) {
		return columnsLessThan(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsLessThan(ArrayList<String> columnNames, double value) {
		return columnsLessThan(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsLessThan(ArrayList<String> columnNames, float value) {
		return columnsLessThan(columnNames.toArray(new String[0]), value);
	}


	public DataFrame columnLessThan(int columnIndex, DataItem[] values) {
		return columnLessThan(columnIndex, DataItem.convertToPrimitiveDoubleList(values));
	}

	public DataFrame columnLessThan(int columnIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return columnLessThan(columnIndex, doubleArr);
	}

	public DataFrame columnLessThan(int columnIndex, double[] values) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < values.length; rowCount++) {
			newDF.setValue(0, rowCount, this.getValue(columnIndex, rowCount).lessThan(values[rowCount]));
		}
		return newDF;
	}

	public DataFrame columnLessThan(int columnIndex, float[] values) {
		return columnLessThan(columnIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame columnLessThan(int columnIndexInSelf, int columnIndexInOtherDF, DataFrame otherDF) {
		double[] otherColumn = otherDF.getColumnAsDoubleArray(columnIndexInOtherDF);
		return columnLessThan(columnIndexInSelf, otherColumn);
	}

	public DataFrame rowLessThan(int rowIndex, DataItem value) {
		return rowLessThan(rowIndex, value.getValueConvertedToDouble());
	}

	public DataFrame rowLessThan(int rowIndex, int value) {
		return rowLessThan(rowIndex, (double) value);
	}

	public DataFrame rowLessThan(int rowIndex, double value) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.rowNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < this.getNumColumns(); columnCount++) {
			
			newDF.setValue(columnCount, 0, this.getValue(columnCount, rowIndex).lessThan(value));
		}
		return newDF;
	}

	public DataFrame rowLessThan(int rowIndex, float value) {
		return rowLessThan(rowIndex, (double) value);
	}


	public DataFrame rowLessThan(String rowName, DataItem value) {
		return rowLessThan(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowLessThan(String rowName, int value) {
		return rowLessThan(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowLessThan(String rowName, double value) {
		return rowLessThan(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowLessThan(String rowName, float value) {
		return rowLessThan(this.rowNames.indexOf(rowName), value);
	}


	public DataFrame rowsLessThan(int[] rowIndices, DataItem value) {
		return rowsLessThan(rowIndices, value.getValueConvertedToDouble());
	}

	public DataFrame rowsLessThan(int[] rowIndices, int value) {
		return rowsLessThan(rowIndices, (double)value);
	}

	public DataFrame rowsLessThan(int[] rowIndices, double value) {
		DataFrame newDF = getRowsAsDataFrame(rowIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				newDF.setValue(columnCount, rowCount, newDF.getValue(columnCount, rowCount).lessThan(value));
			}	
		}
		return newDF;
	}

	public DataFrame rowsLessThan(int[] rowIndices, float value) {
		return rowsLessThan(rowIndices, (double)value);
	}


	public DataFrame rowsLessThan(String[] rowNames, DataItem value) {
		return rowsLessThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsLessThan(String[] rowNames, int value) {
		return rowsLessThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsLessThan(String[] rowNames, double value) {
		return rowsLessThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsLessThan(String[] rowNames, float value) {
		return rowsLessThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}


	public DataFrame rowsLessThan(ArrayList<String> rowNames, DataItem value) {
		return rowsLessThan(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsLessThan(ArrayList<String> rowNames, int value) {
		return rowsLessThan(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsLessThan(ArrayList<String> rowNames, double value) {
		return rowsLessThan(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsLessThan(ArrayList<String> rowNames, float value) {
		return rowsLessThan(rowNames.toArray(new String[0]), value);
	}


	public DataFrame rowLessThan(int rowIndex, DataItem[] values) {
		return rowLessThan(rowIndex, DataItem.convertToPrimitiveDoubleList(values));
	}

	public DataFrame rowLessThan(int rowIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return rowLessThan(rowIndex, doubleArr);
	}

	public DataFrame rowLessThan(int rowIndex, double[] values) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.columnNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < values.length; columnCount++) {
			newDF.setValue(columnCount, 0, this.getValue(columnCount, rowIndex).lessThan(values[columnCount]));
		}
		return newDF;
	}

	public DataFrame rowLessThan(int rowIndex, float[] values) {
		return rowLessThan(rowIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame rowLessThan(int rowIndexInSelf, int rowIndexInOtherDF, DataFrame otherDF) {
		double[] otherRow = otherDF.getRowAsDoubleArray(rowIndexInOtherDF);
		return rowLessThan(rowIndexInSelf, otherRow);
	}


	public DataFrame lessThanOrEqual(DataFrame df) {
		if (this.sameShape(df)) { 
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
	

	
	public DataFrame columnLessThanOrEqual(int columnIndex, DataItem value) {
		return columnLessThanOrEqual(columnIndex, value.getValueConvertedToDouble());
	}

	public DataFrame columnLessThanOrEqual(int columnIndex, int value) {
		return columnLessThanOrEqual(columnIndex, (double)value);
	}

	public DataFrame columnLessThanOrEqual(int columnIndex, double value) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			boolean lessThan = this.getValue(columnIndex, rowCount).lessThan(value);
			boolean equals = this.getValue(columnIndex, rowCount).equal(value);
			newDF.setValue(0, rowCount, lessThan || equals);
		}
		return newDF;
	}

	public DataFrame columnLessThanOrEqual(int columnIndex, float value) {
		return columnLessThanOrEqual(columnIndex, (double)value);
	}


	public DataFrame columnLessThanOrEqual(String columnName, DataItem value) {
		return columnLessThanOrEqual(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnLessThanOrEqual(String columnName, int value) {
		return columnLessThanOrEqual(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnLessThanOrEqual(String columnName, double value) {
		return columnLessThanOrEqual(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnLessThanOrEqual(String columnName, float value) {
		return columnLessThanOrEqual(this.columnNames.indexOf(columnName), value);
	}


	public DataFrame columnsLessThanOrEqual(int[] columnIndices, DataItem value) {
		return columnsLessThanOrEqual(columnIndices, value.getValueConvertedToDouble());
	}

	public DataFrame columnsLessThanOrEqual(int[] columnIndices, int value) {
		return columnsLessThanOrEqual(columnIndices, (double) value);
	}

	public DataFrame columnsLessThanOrEqual(int[] columnIndices, double value) {
		DataFrame newDF = getColumnsAsDataFrame(columnIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				boolean lessThan = newDF.getValue(columnCount, rowCount).lessThan(value);
				boolean equals = newDF.getValue(columnCount, rowCount).equal(value);
				newDF.setValue(columnCount, rowCount, lessThan || equals);
			}	
		}
		return newDF;
	}

	public DataFrame columnsLessThanOrEqual(int[] columnIndices, float value) {
		return columnsLessThanOrEqual(columnIndices, (double) value);
	}


	public DataFrame columnsLessThanOrEqual(String[] columnNames, DataItem value) {
		return columnsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsLessThanOrEqual(String[] columnNames, int value) {
		return columnsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsLessThanOrEqual(String[] columnNames, double value) {
		return columnsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsLessThanOrEqual(String[] columnNames, float value) {
		return columnsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}


	public DataFrame columnsLessThanOrEqual(ArrayList<String> columnNames, DataItem value) {
		return columnsLessThanOrEqual(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsLessThanOrEqual(ArrayList<String> columnNames, int value) {
		return columnsLessThanOrEqual(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsLessThanOrEqual(ArrayList<String> columnNames, double value) {
		return columnsLessThanOrEqual(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsLessThanOrEqual(ArrayList<String> columnNames, float value) {
		return columnsLessThanOrEqual(columnNames.toArray(new String[0]), value);
	}


	public DataFrame columnLessThanOrEqual(int columnIndex, DataItem[] value) {
		return columnLessThanOrEqual(columnIndex, DataItem.convertToPrimitiveDoubleList(value));
	}

	public DataFrame columnLessThanOrEqual(int columnIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return columnLessThanOrEqual(columnIndex, doubleArr);
	}

	public DataFrame columnLessThanOrEqual(int columnIndex, double[] values) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < values.length; rowCount++) {
			boolean lessThan = this.getValue(columnIndex, rowCount).lessThan(values[rowCount]);
			boolean equal = this.getValue(columnIndex, rowCount).equal(values[rowCount]);
			newDF.setValue(0, rowCount, lessThan || equal);
		}
		return newDF;
	}

	public DataFrame columnLessThanOrEqual(int columnIndex, float[] values) {
		return columnLessThanOrEqual(columnIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame columnLessThanOrEqual(int columnIndexInSelf, int columnIndexInOtherDF, DataFrame otherDF) {
		double[] otherColumn = otherDF.getColumnAsDoubleArray(columnIndexInOtherDF);
		return columnLessThanOrEqual(columnIndexInSelf, otherColumn);
	}

	public DataFrame rowLessThanOrEqual(int rowIndex, DataItem value) {
		return rowLessThanOrEqual(rowIndex, value.getValueConvertedToDouble());
	}

	public DataFrame rowLessThanOrEqual(int rowIndex, int value) {
		return rowLessThanOrEqual(rowIndex, (double) value);
	}

	public DataFrame rowLessThanOrEqual(int rowIndex, double value) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.rowNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < this.getNumColumns(); columnCount++) {
			boolean lessThan = this.getValue(columnCount, rowIndex).lessThan(value);
			boolean equal = this.getValue(columnCount, rowIndex).equal(value);
			newDF.setValue(columnCount, 0, lessThan || equal);
		}
		return newDF;
	}

	public DataFrame rowLessThanOrEqual(int rowIndex, float value) {
		return rowLessThanOrEqual(rowIndex, (double) value);
	}


	public DataFrame rowLessThanOrEqual(String rowName, DataItem value) {
		return rowLessThanOrEqual(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowLessThanOrEqual(String rowName, int value) {
		return rowLessThanOrEqual(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowLessThanOrEqual(String rowName, double value) {
		return rowLessThanOrEqual(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowLessThanOrEqual(String rowName, float value) {
		return rowLessThanOrEqual(this.rowNames.indexOf(rowName), value);
	}


	public DataFrame rowsLessThanOrEqual(int[] rowIndices, DataItem value) {
		return rowsLessThanOrEqual(rowIndices, value.getValueConvertedToDouble());
	}

	public DataFrame rowsLessThanOrEqual(int[] rowIndices, int value) {
		return rowsLessThanOrEqual(rowIndices, (double)value);
	}

	public DataFrame rowsLessThanOrEqual(int[] rowIndices, double value) {
		DataFrame newDF = getRowsAsDataFrame(rowIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				boolean lessThan = newDF.getValue(columnCount, rowCount).lessThan(value);
				boolean equal = newDF.getValue(columnCount, rowCount).equal(value);
				newDF.setValue(columnCount, rowCount, lessThan || equal);
			}	
		}
		return newDF;
	}

	public DataFrame rowsLessThanOrEqual(int[] rowIndices, float value) {
		return rowsLessThanOrEqual(rowIndices, (double)value);
	}


	public DataFrame rowsLessThanOrEqual(String[] rowNames, DataItem value) {
		return rowsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsLessThanOrEqual(String[] rowNames, int value) {
		return rowsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsLessThanOrEqual(String[] rowNames, double value) {
		return rowsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsLessThanOrEqual(String[] rowNames, float value) {
		return rowsLessThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}


	public DataFrame rowsLessThanOrEqual(ArrayList<String> rowNames, DataItem value) {
		return rowsLessThanOrEqual(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsLessThanOrEqual(ArrayList<String> rowNames, int value) {
		return rowsLessThanOrEqual(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsLessThanOrEqual(ArrayList<String> rowNames, double value) {
		return rowsLessThanOrEqual(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsLessThanOrEqual(ArrayList<String> rowNames, float value) {
		return rowsLessThanOrEqual(rowNames.toArray(new String[0]), value);
	}


	public DataFrame rowLessThanOrEqual(int rowIndex, DataItem[] values) {
		return rowLessThanOrEqual(rowIndex, DataItem.convertToPrimitiveDoubleList(values));
	}

	public DataFrame rowLessThanOrEqual(int rowIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return rowLessThanOrEqual(rowIndex, doubleArr);
	}

	public DataFrame rowLessThanOrEqual(int rowIndex, double[] values) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.columnNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < values.length; columnCount++) {
			boolean lessThan = this.getValue(columnCount, rowIndex).lessThan(values[columnCount]);
			boolean equal = this.getValue(columnCount, rowIndex).equal(values[columnCount]);
			newDF.setValue(columnCount, 0, lessThan || equal);
		}
		return newDF;
	}

	public DataFrame rowLessThanOrEqual(int rowIndex, float[] values) {
		return rowLessThanOrEqual(rowIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame rowLessThanOrEqual(int rowIndexInSelf, int rowIndexInOtherDF, DataFrame otherDF) {
		double[] otherRow = otherDF.getRowAsDoubleArray(rowIndexInOtherDF);
		return rowLessThanOrEqual(rowIndexInSelf, otherRow);
	}

	
	public DataFrame greaterThan(DataFrame df) {
		if (this.sameShape(df)) { 	
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
	

	
	
	public DataFrame columnGreaterThan(int columnIndex, DataItem value) {
		return columnGreaterThan(columnIndex, value.getValueConvertedToDouble());
	}

	public DataFrame columnGreaterThan(int columnIndex, int value) {
		return columnGreaterThan(columnIndex, (double) value);
	}

	public DataFrame columnGreaterThan(int columnIndex, double value) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			newDF.setValue(0, rowCount, this.getValue(columnIndex, rowCount).greaterThan(value));
		}
		return newDF;
	}

	public DataFrame columnGreaterThan(int columnIndex, float value) {
		return columnGreaterThan(columnIndex, (double) value);
	}


	public DataFrame columnGreaterThan(String columnName, DataItem value) {
		return columnGreaterThan(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnGreaterThan(String columnName, int value) {
		return columnGreaterThan(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnGreaterThan(String columnName, double value) {
		return columnGreaterThan(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnGreaterThan(String columnName, float value) {
		return columnGreaterThan(this.columnNames.indexOf(columnName), value);
	}


	public DataFrame columnsGreaterThan(int[] columnIndices, DataItem value) {
		return columnsGreaterThan(columnIndices, value.getValueConvertedToDouble());
		
	}

	public DataFrame columnsGreaterThan(int[] columnIndices, int value) {
		return columnsGreaterThan(columnIndices,(double) value);
	}

	public DataFrame columnsGreaterThan(int[] columnIndices, double value) {
		DataFrame newDF = getColumnsAsDataFrame(columnIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				newDF.setValue(columnCount, rowCount, newDF.getValue(columnCount, rowCount).greaterThan(value));
			}	
		}
		return newDF;
	}

	public DataFrame columnsGreaterThan(int[] columnIndices, float value) {
		return columnsGreaterThan(columnIndices,(double) value);
	}


	public DataFrame columnsGreaterThan(String[] columnNames, DataItem value) {
		return columnsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsGreaterThan(String[] columnNames, int value) {
		return columnsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsGreaterThan(String[] columnNames, double value) {
		return columnsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsGreaterThan(String[] columnNames, float value) {
		return columnsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}


	public DataFrame columnsGreaterThan(ArrayList<String> columnNames, DataItem value) {
		return columnsGreaterThan(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsGreaterThan(ArrayList<String> columnNames, int value) {
		return columnsGreaterThan(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsGreaterThan(ArrayList<String> columnNames, double value) {
		return columnsGreaterThan(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsGreaterThan(ArrayList<String> columnNames, float value) {
		return columnsGreaterThan(columnNames.toArray(new String[0]), value);
	}


	public DataFrame columnGreaterThan(int columnIndex, DataItem[] values) {
		return columnGreaterThan(columnIndex, DataItem.convertToPrimitiveDoubleList(values));
	}

	public DataFrame columnGreaterThan(int columnIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return columnGreaterThan(columnIndex, doubleArr);
	}

	public DataFrame columnGreaterThan(int columnIndex, double[] values) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < values.length; rowCount++) {
			newDF.setValue(0, rowCount, this.getValue(columnIndex, rowCount).greaterThan(values[rowCount]));
		}
		return newDF;
	}

	public DataFrame columnGreaterThan(int columnIndex, float[] values) {
		return columnGreaterThan(columnIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame columnGreaterThan(int columnIndexInSelf, int columnIndexInOtherDF, DataFrame otherDF) {
		double[] otherColumn = otherDF.getColumnAsDoubleArray(columnIndexInOtherDF);
		return columnGreaterThan(columnIndexInSelf, otherColumn);
	}

	public DataFrame rowGreaterThan(int rowIndex, DataItem value) {
		return rowGreaterThan(rowIndex, value.getValueConvertedToDouble());
	}

	public DataFrame rowGreaterThan(int rowIndex, int value) {
		return rowGreaterThan(rowIndex, (double) value);
	}

	public DataFrame rowGreaterThan(int rowIndex, double value) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.rowNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < this.getNumColumns(); columnCount++) {
			
			newDF.setValue(columnCount, 0, this.getValue(columnCount, rowIndex).greaterThan(value));
		}
		return newDF;
	}

	public DataFrame rowGreaterThan(int rowIndex, float value) {
		return rowGreaterThan(rowIndex, (double) value);
	}


	public DataFrame rowGreaterThan(String rowName, DataItem value) {
		return rowGreaterThan(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowGreaterThan(String rowName, int value) {
		return rowGreaterThan(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowGreaterThan(String rowName, double value) {
		return rowGreaterThan(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowGreaterThan(String rowName, float value) {
		return rowGreaterThan(this.rowNames.indexOf(rowName), value);
	}


	public DataFrame rowsGreaterThan(int[] rowIndices, DataItem value) {
		return rowsGreaterThan(rowIndices, value.getValueConvertedToDouble());
	}

	public DataFrame rowsGreaterThan(int[] rowIndices, int value) {
		return rowsGreaterThan(rowIndices, (double)value);
	}

	public DataFrame rowsGreaterThan(int[] rowIndices, double value) {
		DataFrame newDF = getRowsAsDataFrame(rowIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				newDF.setValue(columnCount, rowCount, newDF.getValue(columnCount, rowCount).greaterThan(value));
			}	
		}
		return newDF;
	}

	public DataFrame rowsGreaterThan(int[] rowIndices, float value) {
		return rowsGreaterThan(rowIndices, (double)value);
	}


	public DataFrame rowsGreaterThan(String[] rowNames, DataItem value) {
		return rowsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsGreaterThan(String[] rowNames, int value) {
		return rowsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsGreaterThan(String[] rowNames, double value) {
		return rowsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsGreaterThan(String[] rowNames, float value) {
		return rowsGreaterThan(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}


	public DataFrame rowsGreaterThan(ArrayList<String> rowNames, DataItem value) {
		return rowsGreaterThan(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsGreaterThan(ArrayList<String> rowNames, int value) {
		return rowsGreaterThan(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsGreaterThan(ArrayList<String> rowNames, double value) {
		return rowsGreaterThan(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsGreaterThan(ArrayList<String> rowNames, float value) {
		return rowsGreaterThan(rowNames.toArray(new String[0]), value);
	}


	public DataFrame rowGreaterThan(int rowIndex, DataItem[] values) {
		return rowGreaterThan(rowIndex, DataItem.convertToPrimitiveDoubleList(values));
	}

	public DataFrame rowGreaterThan(int rowIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return rowGreaterThan(rowIndex, doubleArr);
	}

	public DataFrame rowGreaterThan(int rowIndex, double[] values) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.columnNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < values.length; columnCount++) {
			newDF.setValue(columnCount, 0, this.getValue(columnCount, rowIndex).greaterThan(values[columnCount]));
		}
		return newDF;
	}

	public DataFrame rowGreaterThan(int rowIndex, float[] values) {
		return rowGreaterThan(rowIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame rowGreaterThan(int rowIndexInSelf, int rowIndexInOtherDF, DataFrame otherDF) {
		double[] otherRow = otherDF.getRowAsDoubleArray(rowIndexInOtherDF);
		return rowGreaterThan(rowIndexInSelf, otherRow);
	}

	
	
	public DataFrame greaterThanOrEqual(DataFrame df) {
		if (this.sameShape(df)) { 		
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
	

	
	public DataFrame columnGreaterThanOrEqual(int columnIndex, DataItem value) {
		return columnGreaterThanOrEqual(columnIndex, value.getValueConvertedToDouble());
	}

	public DataFrame columnGreaterThanOrEqual(int columnIndex, int value) {
		return columnGreaterThanOrEqual(columnIndex, (double)value);
	}

	public DataFrame columnGreaterThanOrEqual(int columnIndex, double value) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			boolean greaterThan = this.getValue(columnIndex, rowCount).greaterThan(value);
			boolean equals = this.getValue(columnIndex, rowCount).equal(value);
			newDF.setValue(0, rowCount, greaterThan || equals);
		}
		return newDF;
	}

	public DataFrame columnGreaterThanOrEqual(int columnIndex, float value) {
		return columnGreaterThanOrEqual(columnIndex, (double)value);
	}


	public DataFrame columnGreaterThanOrEqual(String columnName, DataItem value) {
		return columnGreaterThanOrEqual(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnGreaterThanOrEqual(String columnName, int value) {
		return columnGreaterThanOrEqual(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnGreaterThanOrEqual(String columnName, double value) {
		return columnGreaterThanOrEqual(this.columnNames.indexOf(columnName), value);
	}

	public DataFrame columnGreaterThanOrEqual(String columnName, float value) {
		return columnGreaterThanOrEqual(this.columnNames.indexOf(columnName), value);
	}


	public DataFrame columnsGreaterThanOrEqual(int[] columnIndices, DataItem value) {
		return columnsGreaterThanOrEqual(columnIndices, value.getValueConvertedToDouble());
	}

	public DataFrame columnsGreaterThanOrEqual(int[] columnIndices, int value) {
		return columnsGreaterThanOrEqual(columnIndices, (double) value);
	}

	public DataFrame columnsGreaterThanOrEqual(int[] columnIndices, double value) {
		DataFrame newDF = getColumnsAsDataFrame(columnIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				boolean greaterThan = newDF.getValue(columnCount, rowCount).greaterThan(value);
				boolean equals = newDF.getValue(columnCount, rowCount).equal(value);
				newDF.setValue(columnCount, rowCount, greaterThan || equals);
			}	
		}
		return newDF;
	}

	public DataFrame columnsGreaterThanOrEqual(int[] columnIndices, float value) {
		return columnsGreaterThanOrEqual(columnIndices, (double) value);
	}


	public DataFrame columnsGreaterThanOrEqual(String[] columnNames, DataItem value) {
		return columnsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsGreaterThanOrEqual(String[] columnNames, int value) {
		return columnsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsGreaterThanOrEqual(String[] columnNames, double value) {
		return columnsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}

	public DataFrame columnsGreaterThanOrEqual(String[] columnNames, float value) {
		return columnsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames), value);
	}


	public DataFrame columnsGreaterThanOrEqual(ArrayList<String> columnNames, DataItem value) {
		return columnsGreaterThanOrEqual(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsGreaterThanOrEqual(ArrayList<String> columnNames, int value) {
		return columnsGreaterThanOrEqual(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsGreaterThanOrEqual(ArrayList<String> columnNames, double value) {
		return columnsGreaterThanOrEqual(columnNames.toArray(new String[0]), value);
	}

	public DataFrame columnsGreaterThanOrEqual(ArrayList<String> columnNames, float value) {
		return columnsGreaterThanOrEqual(columnNames.toArray(new String[0]), value);
	}


	public DataFrame columnGreaterThanOrEqual(int columnIndex, DataItem[] value) {
		return columnGreaterThanOrEqual(columnIndex, DataItem.convertToPrimitiveDoubleList(value));
	}

	public DataFrame columnGreaterThanOrEqual(int columnIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return columnGreaterThanOrEqual(columnIndex, doubleArr);
	}

	public DataFrame columnGreaterThanOrEqual(int columnIndex, double[] values) {
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame(newColumnNames, (ArrayList<String>)this.rowNames.clone());
		for (int rowCount = 0; rowCount < values.length; rowCount++) {
			boolean greaterThan = this.getValue(columnIndex, rowCount).greaterThan(values[rowCount]);
			boolean equal = this.getValue(columnIndex, rowCount).equal(values[rowCount]);
			newDF.setValue(0, rowCount, greaterThan || equal);
		}
		return newDF;
	}

	public DataFrame columnGreaterThanOrEqual(int columnIndex, float[] values) {
		return columnGreaterThanOrEqual(columnIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame columnGreaterThanOrEqual(int columnIndexInSelf, int columnIndexInOtherDF, DataFrame otherDF) {
		double[] otherColumn = otherDF.getColumnAsDoubleArray(columnIndexInOtherDF);
		return columnGreaterThanOrEqual(columnIndexInSelf, otherColumn);
	}

	public DataFrame rowGreaterThanOrEqual(int rowIndex, DataItem value) {
		return rowGreaterThanOrEqual(rowIndex, value.getValueConvertedToDouble());
	}

	public DataFrame rowGreaterThanOrEqual(int rowIndex, int value) {
		return rowGreaterThanOrEqual(rowIndex, (double) value);
	}

	public DataFrame rowGreaterThanOrEqual(int rowIndex, double value) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.rowNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < this.getNumColumns(); columnCount++) {
			boolean greaterThan = this.getValue(columnCount, rowIndex).greaterThan(value);
			boolean equal = this.getValue(columnCount, rowIndex).equal(value);
			newDF.setValue(columnCount, 0, greaterThan || equal);
		}
		return newDF;
	}

	public DataFrame rowGreaterThanOrEqual(int rowIndex, float value) {
		return rowGreaterThanOrEqual(rowIndex, (double) value);
	}


	public DataFrame rowGreaterThanOrEqual(String rowName, DataItem value) {
		return rowGreaterThanOrEqual(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowGreaterThanOrEqual(String rowName, int value) {
		return rowGreaterThanOrEqual(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowGreaterThanOrEqual(String rowName, double value) {
		return rowGreaterThanOrEqual(this.rowNames.indexOf(rowName), value);
	}

	public DataFrame rowGreaterThanOrEqual(String rowName, float value) {
		return rowGreaterThanOrEqual(this.rowNames.indexOf(rowName), value);
	}


	public DataFrame rowsGreaterThanOrEqual(int[] rowIndices, DataItem value) {
		return rowsGreaterThanOrEqual(rowIndices, value.getValueConvertedToDouble());
	}

	public DataFrame rowsGreaterThanOrEqual(int[] rowIndices, int value) {
		return rowsGreaterThanOrEqual(rowIndices, (double)value);
	}

	public DataFrame rowsGreaterThanOrEqual(int[] rowIndices, double value) {
		DataFrame newDF = getRowsAsDataFrame(rowIndices);
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				boolean greaterThan = newDF.getValue(columnCount, rowCount).greaterThan(value);
				boolean equal = newDF.getValue(columnCount, rowCount).equal(value);
				newDF.setValue(columnCount, rowCount, greaterThan || equal);
			}	
		}
		return newDF;
	}

	public DataFrame rowsGreaterThanOrEqual(int[] rowIndices, float value) {
		return rowsGreaterThanOrEqual(rowIndices, (double)value);
	}


	public DataFrame rowsGreaterThanOrEqual(String[] rowNames, DataItem value) {
		return rowsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsGreaterThanOrEqual(String[] rowNames, int value) {
		return rowsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsGreaterThanOrEqual(String[] rowNames, double value) {
		return rowsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}

	public DataFrame rowsGreaterThanOrEqual(String[] rowNames, float value) {
		return rowsGreaterThanOrEqual(CommonArray.getIndicesOfStringsInArray(this.rowNames, rowNames), value);
	}


	public DataFrame rowsGreaterThanOrEqual(ArrayList<String> rowNames, DataItem value) {
		return rowsGreaterThanOrEqual(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsGreaterThanOrEqual(ArrayList<String> rowNames, int value) {
		return rowsGreaterThanOrEqual(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsGreaterThanOrEqual(ArrayList<String> rowNames, double value) {
		return rowsGreaterThanOrEqual(rowNames.toArray(new String[0]), value);
	}

	public DataFrame rowsGreaterThanOrEqual(ArrayList<String> rowNames, float value) {
		return rowsGreaterThanOrEqual(rowNames.toArray(new String[0]), value);
	}


	public DataFrame rowGreaterThanOrEqual(int rowIndex, DataItem[] values) {
		return rowGreaterThanOrEqual(rowIndex, DataItem.convertToPrimitiveDoubleList(values));
	}

	public DataFrame rowGreaterThanOrEqual(int rowIndex, int[] values) {
		double[] doubleArr = IntStream.of(values).asDoubleStream().toArray();
		return rowGreaterThanOrEqual(rowIndex, doubleArr);
	}

	public DataFrame rowGreaterThanOrEqual(int rowIndex, double[] values) {
		ArrayList<String> newRowNames = new ArrayList<String>();
		newRowNames.add(this.columnNames.get(rowIndex));
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), newRowNames);
		for (int columnCount = 0; columnCount < values.length; columnCount++) {
			boolean greaterThan = this.getValue(columnCount, rowIndex).greaterThan(values[columnCount]);
			boolean equal = this.getValue(columnCount, rowIndex).equal(values[columnCount]);
			newDF.setValue(columnCount, 0, greaterThan || equal);
		}
		return newDF;
	}

	public DataFrame rowGreaterThanOrEqual(int rowIndex, float[] values) {
		return rowGreaterThanOrEqual(rowIndex, CommonArray.convertFloatArrayToDoubleArray(values));
	}


	public DataFrame rowGreaterThanOrEqual(int rowIndexInSelf, int rowIndexInOtherDF, DataFrame otherDF) {
		double[] otherRow = otherDF.getRowAsDoubleArray(rowIndexInOtherDF);
		return rowGreaterThanOrEqual(rowIndexInSelf, otherRow);
	}
	
	public DataFrame elementwiseEqual(DataFrame df) {
		if (this.sameShape(df)) { 		
			@SuppressWarnings("unchecked")
			DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
			for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
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
	

	
	// -----------------------------------------------
	// ------ Elementwise Date/Time Comparisons ------
	// -----------------------------------------------
	public DataFrame before(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(date);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	
	public DataFrame before(LocalDateTime dateTime) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(dateTime);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	
	public DataFrame before(LocalTime time) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(time);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	
	public DataFrame beforeOrSame(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(date);
				boolean sameDate = this.getValue(colCount, rowCount).sameDate(date);
				newDF.setValue(colCount, rowCount, before || sameDate);
			}	
		}
		return newDF;
	}
	
	public DataFrame beforeOrSame(LocalDateTime dateTime) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(dateTime);
				boolean sameDateTime = this.getValue(colCount, rowCount).sameDate(dateTime);
				newDF.setValue(colCount, rowCount, before || sameDateTime);
			}	
		}
		return newDF;
	}
	
	public DataFrame beforeOrSame(LocalTime time) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).before(time);
				boolean sameTime = this.getValue(colCount, rowCount).sameTime(time);
				newDF.setValue(colCount, rowCount, before || sameTime);
			}	
		}
		return newDF;
	}
	
	public DataFrame after(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).after(date);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	public DataFrame after(LocalDateTime dateTime) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).after(dateTime);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	
	public DataFrame after(LocalTime time) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean before = this.getValue(colCount, rowCount).after(time);
				newDF.setValue(colCount, rowCount, before);
			}	
		}
		return newDF;
	}
	

	
	public DataFrame afterOrSame(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean after = this.getValue(colCount, rowCount).after(date);
				boolean sameDate = this.getValue(colCount, rowCount).sameDate(date);
				newDF.setValue(colCount, rowCount, after || sameDate);
			}	
		}
		return newDF;
	}
	
	public DataFrame afterOrSame(LocalDateTime dateTime) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean after = this.getValue(colCount, rowCount).after(dateTime);
				boolean sameDateTime = this.getValue(colCount, rowCount).sameDate(dateTime);
				newDF.setValue(colCount, rowCount, after || sameDateTime);
			}	
		}
		return newDF;
	}
	
	public DataFrame afterOrSame(LocalTime time) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean after = this.getValue(colCount, rowCount).after(time);
				boolean sameTime = this.getValue(colCount, rowCount).sameTime(time);
				newDF.setValue(colCount, rowCount, after || sameTime);
			}	
		}
		return newDF;
	}
	

	
	public DataFrame sameDate(LocalDate date) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean equal = this.getValue(colCount, rowCount).sameDate(date);
				newDF.setValue(colCount, rowCount, equal);
			}	
		}
		return newDF;
	}
	
	public DataFrame sameDate(LocalDateTime dateTime) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean equal = this.getValue(colCount, rowCount).sameDate(dateTime);
				newDF.setValue(colCount, rowCount, equal);
			}	
		}
		return newDF;
	}
	
	public DataFrame sameTime(LocalTime time) {
		@SuppressWarnings("unchecked")
		DataFrame newDF = new DataFrame((ArrayList<String>)this.columnNames.clone(), (ArrayList<String>)this.rowNames.clone());
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				boolean equal = this.getValue(colCount, rowCount).sameTime(time);
				newDF.setValue(colCount, rowCount, equal);
			}	
		}
		return newDF;
	}
	
	public DataFrame differentDate(LocalDate date) {
		return sameDate(date).negate();
	}
	
	public DataFrame differentDate(LocalDateTime dateTime) {
		return sameDate(dateTime).negate();
	}
	
	public DataFrame differentTime(LocalTime time) {
		return sameTime(time).negate();
	}
	
	
	// ------------------------
	// ------ True/False ------
	// ------------------------
	
	public Boolean allTrue() {
		return allTrueInColumns(0, this.getNumColumns() - 1);
	}
	public Boolean allFalse() {
		return allFalseInColumns(0, this.getNumColumns() - 1);
	}
	public Boolean anyTrue() {
		return anyTrueInColumns(0, this.getNumColumns() - 1);
	}
	public Boolean anyFalse() {
		return anyFalseInColumns(0, this.getNumColumns() - 1);
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
		return ((this.getNumColumns() == df.getNumColumns()) && (this.getNumRows() == df.getNumRows())); 
	}
	
	// ----------------------------------------------
	// ------ Computations / Descriptive Stats ------
	// ----------------------------------------------
	public Double max() {
		DataFrame newDF = this.maxInColumns();
		return newDF.maxInRow(0);
	}

	public DataFrame maxInColumns() {
		return maxInColumns(0, this.getNumColumns() - 1);	
	}

	public Double maxInColumn(int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonArray.maxValue(column);	
	}

	public Double maxInColumn(String columnName) {
		return maxInColumn(this.columnNames.indexOf(columnName));	
	}

	public DataFrame maxInColumns(int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("max");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, maxInColumn(columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame maxInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return maxInColumns(indices);
	}

	public DataFrame maxInColumns(ArrayList<String> columnNames) {
		return maxInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame maxInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return maxInColumns(indicesToGet);
	}

	public DataFrame maxInRows() {
		this.transpose();
		DataFrame value = this.maxInColumns();
		this.transpose();
		return value;		
	}

	public Double maxInRow(int rowIndex) {
		this.transpose();
		double value = this.maxInColumn(rowIndex);
		this.transpose();
		return value;	
	}

	public Double maxInRow(String columnName) {
		this.transpose();
		double value = this.maxInColumn(columnName);
		this.transpose();
		return value;	
	}

	public DataFrame maxInRows(int[] columnIndices) {
		this.transpose();
		DataFrame newDF = this.maxInColumns(columnIndices);
		this.transpose();
		return newDF;	
	}

	public DataFrame maxInRows(String[] columnNames) {
		this.transpose();
		DataFrame newDF = this.maxInColumns(columnNames);
		this.transpose();
		return newDF;	
	}

	public DataFrame maxInRows(ArrayList<String> columnNames) {
		return maxInRows(columnNames.toArray(new String[0]));	
	}

	public DataFrame maxInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame newDF = this.maxInColumns(minIndex, maxIndex);
		this.transpose();
		return newDF;	
	}

	
	public Double min() {
		DataFrame newDF = this.minInColumns();
		return newDF.minInRow(0);
	}
	
	public DataFrame minInColumns() {
		return minInColumns(0, this.getNumColumns() - 1);	
	}
	
	public Double minInColumn(int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonArray.minValue(column);
	}
	
	public Double minInColumn(String columnName) {
		return minInColumn(this.columnNames.indexOf(columnName));	
	}
	
	public DataFrame minInColumns(int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("max");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, minInColumn(columnIndices[columnIndex]));
		}
		
		return maxDF;
	}
	
	public DataFrame minInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return minInColumns(indices);
	}
	
	public DataFrame minInColumns(ArrayList<String> columnNames) {
		return minInColumns(columnNames.toArray(new String[0]));
	}
	
	public DataFrame minInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return minInColumns(indicesToGet);
	}
	
	public DataFrame minInRows() {
		this.transpose();
		DataFrame value = this.minInColumns();
		this.transpose();
		return value;	
	}
	
	public Double minInRow(int rowIndex) {
		this.transpose();
		double value = this.minInColumn(rowIndex);
		this.transpose();
		return value;
	}
	
	public Double minInRow(String rowName) {
		this.transpose();
		double value = this.minInColumn(rowName);
		this.transpose();
		return value;
	}
	
	public DataFrame minInRows(int[] rowIndices) {
		this.transpose();
		DataFrame newDF = this.minInColumns(rowIndices);
		this.transpose();
		return newDF;
	}
	
	public DataFrame minInRows(String[] rowNames) {
		this.transpose();
		DataFrame newDF = this.minInColumns(rowNames);
		this.transpose();
		return newDF;	
	}
	
	public DataFrame minInRows(ArrayList<String> rowNames) {
		return minInRows(rowNames.toArray(new String[0]));
	}
	
	public DataFrame minInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame newDF = this.minInColumns(minIndex, maxIndex);
		this.transpose();
		return newDF;	
	}
	
	public Double average() {
		DataFrame newDF = this.averageInColumns();
		return newDF.averageInRow(0);
	}

	public DataFrame averageInColumns() {
		return averageInColumns(0, this.getNumColumns() - 1);	
	}

	public Double averageInColumn(int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonArray.average(column);
	}

	public Double averageInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return averageInColumn(columnIndex);
	}

	public DataFrame averageInColumns(int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("average");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, averageInColumn(columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame averageInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return averageInColumns(indices);
	}

	public DataFrame averageInColumns(ArrayList<String> columnNames) {
		return averageInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame averageInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return averageInColumns(indicesToGet);
	}

	public DataFrame averageInRows() {
		this.transpose();
		DataFrame value = this.averageInColumns();
		this.transpose();
		return value;
	}

	public Double averageInRow(int rowIndex) {
		this.transpose();
		Double value = this.averageInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public Double averageInRow(String rowName) {
		this.transpose();
		Double value = this.averageInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame averageInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.averageInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame averageInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.averageInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame averageInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.averageInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame averageInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.averageInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}

	
	public Double mediun() {
		DataFrame newDF = this.mediunInColumns();
		return newDF.mediunInRow(0);
	}

	public DataFrame mediunInColumns() {
		return mediunInColumns(0, this.getNumColumns() - 1);
	}

	public Double mediunInColumn(int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonArray.median(column);
	}

	public Double mediunInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return mediunInColumn(columnIndex);
	}

	public DataFrame mediunInColumns(int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("mediun");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, mediunInColumn(columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame mediunInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return mediunInColumns(indices);
	}

	public DataFrame mediunInColumns(ArrayList<String> columnNames) {
		return mediunInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame mediunInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return mediunInColumns(indicesToGet);
	}

	public DataFrame mediunInRows() {
		this.transpose();
		DataFrame value = this.mediunInColumns();
		this.transpose();
		return value;
	}

	public Double mediunInRow(int rowIndex) {
		this.transpose();
		Double value = this.mediunInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public Double mediunInRow(String rowName) {
		this.transpose();
		Double value = this.mediunInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame mediunInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.mediunInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame mediunInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.mediunInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame mediunInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.mediunInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame mediunInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.mediunInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}

	public Double sum() {
		DataFrame newDF = this.sumInColumns();
		return newDF.sumInRow(0);
	}

	public DataFrame sumInColumns() {
		return sumInColumns(0, this.getNumColumns() - 1);
	}

	public Double sumInColumn(int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonArray.sum(column);
	}

	public Double sumInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return sumInColumn(columnIndex);
	}

	public DataFrame sumInColumns(int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("sum");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, sumInColumn(columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame sumInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return sumInColumns(indices);
	}

	public DataFrame sumInColumns(ArrayList<String> columnNames) {
		return sumInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame sumInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return sumInColumns(indicesToGet);
	}

	public DataFrame sumInRows() {
		this.transpose();
		DataFrame value = this.sumInColumns();
		this.transpose();
		return value;
	}

	public Double sumInRow(int rowIndex) {
		this.transpose();
		Double value = this.sumInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public Double sumInRow(String rowName) {
		this.transpose();
		Double value = this.sumInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame sumInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.sumInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame sumInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.sumInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame sumInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.sumInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame sumInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.sumInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}

	public Double product() {
		DataFrame newDF = this.productInColumns();
		return newDF.productInRow(0);
	}

	public DataFrame productInColumns() {
		return productInColumns(0, this.getNumColumns() - 1);
	}

	public Double productInColumn(int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonArray.product(column);
	}

	public Double productInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return productInColumn(columnIndex);
	}

	public DataFrame productInColumns(int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("product");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, productInColumn(columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame productInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return productInColumns(indices);
	}

	public DataFrame productInColumns(ArrayList<String> columnNames) {
		return productInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame productInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return productInColumns(indicesToGet);
	}

	public DataFrame productInRows() {
		this.transpose();
		DataFrame value = this.productInColumns();
		this.transpose();
		return value;
	}

	public Double productInRow(int rowIndex) {
		this.transpose();
		Double value = this.productInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public Double productInRow(String rowName) {
		this.transpose();
		Double value = this.productInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame productInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.productInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame productInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.productInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame productInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.productInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame productInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.productInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}
	
	public DataFrame cumulativeMaxInColumns() {
		return cumulativeMaxInColumns(0, this.getNumColumns() - 1);
	}

	public DataFrame cumulativeMaxInColumn(int columnIndex) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		
		double[] cumulativeMax = CommonArray.cumulativeMax(this.getColumnAsDoubleArray(columnIndex));
		newDF.setColumnValues(0, cumulativeMax);
		
		return newDF;
	}

	public DataFrame cumulativeMaxInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return cumulativeMaxInColumn(columnIndex);
	}

	public DataFrame cumulativeMaxInColumns(int[] columnIndices) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			newColumnNames.add(this.columnNames.get(columnIndex));
		}
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			double[] newColumn = CommonArray.cumulativeMax(this.getColumnAsDoubleArray(columnIndices[columnIndex]));
			newDF.setColumnValues(columnIndex, newColumn);
		}
		return newDF;
	}

	public DataFrame cumulativeMaxInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return cumulativeMaxInColumns(indices);
	}

	public DataFrame cumulativeMaxInColumns(ArrayList<String> columnNames) {
		return cumulativeMaxInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame cumulativeMaxInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return cumulativeMaxInColumns(indicesToGet);
	}

	public DataFrame cumulativeMaxInRows() {
		this.transpose();
		DataFrame value = this.cumulativeMaxInColumns();
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMaxInRow(int rowIndex) {
		this.transpose();
		DataFrame value = this.cumulativeMaxInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMaxInRow(String rowName) {
		this.transpose();
		DataFrame value = this.cumulativeMaxInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMaxInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.cumulativeMaxInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMaxInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.cumulativeMaxInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMaxInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.cumulativeMaxInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMaxInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.cumulativeMaxInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}


	public DataFrame cumulativeMinInColumns() {
		return cumulativeMinInColumns(0, this.getNumColumns() - 1);
	}

	public DataFrame cumulativeMinInColumn(int columnIndex) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		
		double[] cumulativeMin = CommonArray.cumulativeMin(this.getColumnAsDoubleArray(columnIndex));
		newDF.setColumnValues(0, cumulativeMin);
		
		return newDF;
	}

	public DataFrame cumulativeMinInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return cumulativeMinInColumn(columnIndex);
	}

	public DataFrame cumulativeMinInColumns(int[] columnIndices) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			newColumnNames.add(this.columnNames.get(columnIndex));
		}
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			double[] newColumn = CommonArray.cumulativeMin(this.getColumnAsDoubleArray(columnIndices[columnIndex]));
			newDF.setColumnValues(columnIndex, newColumn);
		}
		return newDF;
	}

	public DataFrame cumulativeMinInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return cumulativeMinInColumns(indices);
	}

	public DataFrame cumulativeMinInColumns(ArrayList<String> columnNames) {
		return cumulativeMinInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame cumulativeMinInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return cumulativeMinInColumns(indicesToGet);
	}

	public DataFrame cumulativeMinInRows() {
		this.transpose();
		DataFrame value = this.cumulativeMinInColumns();
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMinInRow(int rowIndex) {
		this.transpose();
		DataFrame value = this.cumulativeMinInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMinInRow(String rowName) {
		this.transpose();
		DataFrame value = this.cumulativeMinInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMinInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.cumulativeMinInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMinInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.cumulativeMinInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMinInRows(ArrayList<String> names) {
		this.transpose();
		DataFrame value = this.cumulativeMinInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeMinInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.cumulativeMinInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}

	
	public DataFrame cumulativeSumInColumns() {
		return cumulativeSumInColumns(0, this.getNumColumns() - 1);
	}

	public DataFrame cumulativeSumInColumn(int columnIndex) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		
		double[] cumulativeSum = CommonArray.cumulativeSum(this.getColumnAsDoubleArray(columnIndex));
		newDF.setColumnValues(0, cumulativeSum);
		
		return newDF;
	}

	public DataFrame cumulativeSumInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return cumulativeSumInColumn(columnIndex);
	}

	public DataFrame cumulativeSumInColumns(int[] columnIndices) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			newColumnNames.add(this.columnNames.get(columnIndex));
		}
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			double[] newColumn = CommonArray.cumulativeSum(this.getColumnAsDoubleArray(columnIndices[columnIndex]));
			newDF.setColumnValues(columnIndex, newColumn);
		}
		return newDF;
	}

	public DataFrame cumulativeSumInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return cumulativeSumInColumns(indices);
	}

	public DataFrame cumulativeSumInColumns(ArrayList<String> columnNames) {
		return cumulativeSumInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame cumulativeSumInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return cumulativeSumInColumns(indicesToGet);
	}

	public DataFrame cumulativeSumInRows() {
		this.transpose();
		DataFrame value = this.cumulativeSumInColumns();
		this.transpose();
		return value;
	}

	public DataFrame cumulativeSumInRow(int rowIndex) {
		this.transpose();
		DataFrame value = this.cumulativeSumInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeSumInRow(String rowName) {
		this.transpose();
		DataFrame value = this.cumulativeSumInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeSumInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.cumulativeSumInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeSumInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.cumulativeSumInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeSumInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.cumulativeSumInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeSumInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.cumulativeSumInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}

	
	public DataFrame cumulativeProductInColumns() {
		return cumulativeProductInColumns(0, this.getNumColumns() - 1);
	}

	public DataFrame cumulativeProductInColumn(int columnIndex) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		
		double[] cumulativeProduct = CommonArray.cumulativeProduct(this.getColumnAsDoubleArray(columnIndex));
		newDF.setColumnValues(0, cumulativeProduct);
		
		return newDF;
	}

	public DataFrame cumulativeProductInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return cumulativeProductInColumn(columnIndex);
	}

	public DataFrame cumulativeProductInColumns(int[] columnIndices) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			newColumnNames.add(this.columnNames.get(columnIndex));
		}
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			double[] newColumn = CommonArray.cumulativeProduct(this.getColumnAsDoubleArray(columnIndices[columnIndex]));
			newDF.setColumnValues(columnIndex, newColumn);
		}
		return newDF;
	}

	public DataFrame cumulativeProductInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return cumulativeProductInColumns(indices);
	}

	public DataFrame cumulativeProductInColumns(ArrayList<String> columnNames) {
		return cumulativeProductInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame cumulativeProductInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return cumulativeProductInColumns(indicesToGet);
	}

	public DataFrame cumulativeProductInRows() {
		this.transpose();
		DataFrame value = this.cumulativeProductInColumns();
		this.transpose();
		return value;
	}

	public DataFrame cumulativeProductInRow(int rowIndex) {
		this.transpose();
		DataFrame value = this.cumulativeProductInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeProductInRow(String rowName) {
		this.transpose();
		DataFrame value = this.cumulativeProductInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeProductInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.cumulativeProductInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeProductInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.cumulativeProductInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeProductInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.cumulativeProductInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame cumulativeProductInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.cumulativeProductInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}

	public DataFrame percentageChangeInColumns() {
		return percentageChangeInColumns(0, this.getNumColumns() - 1);
	}

	public DataFrame percentageChangeInColumn(int columnIndex) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		newColumnNames.add(this.columnNames.get(columnIndex));
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		
		double[] percentageChange = CommonArray.percentageChange(this.getColumnAsDoubleArray(columnIndex));
		newDF.setColumnValues(0, percentageChange);
		
		return newDF;
	}

	public DataFrame percentageChangeInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return percentageChangeInColumn(columnIndex);
	}

	public DataFrame percentageChangeInColumns(int[] columnIndices) {
		@SuppressWarnings("unchecked")
		ArrayList<String> newRowNames = (ArrayList<String>) this.rowNames.clone();
		ArrayList<String> newColumnNames = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			newColumnNames.add(this.columnNames.get(columnIndex));
		}
		DataFrame newDF = new DataFrame(newColumnNames, newRowNames);
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			double[] newColumn = CommonArray.percentageChange(this.getColumnAsDoubleArray(columnIndices[columnIndex]));
			newDF.setColumnValues(columnIndex, newColumn);
		}
		return newDF;
	}

	public DataFrame percentageChangeInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return percentageChangeInColumns(indices);
	}

	public DataFrame percentageChangeInColumns(ArrayList<String> columnNames) {
		return percentageChangeInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame percentageChangeInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return percentageChangeInColumns(indicesToGet);
	}

	public DataFrame percentageChangeInRows() {
		this.transpose();
		DataFrame value = this.percentageChangeInColumns();
		this.transpose();
		return value;
	}

	public DataFrame percentageChangeInRow(int rowIndex) {
		this.transpose();
		DataFrame value = this.percentageChangeInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public DataFrame percentageChangeInRow(String rowName) {
		this.transpose();
		DataFrame value = this.percentageChangeInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame percentageChangeInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.percentageChangeInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame percentageChangeInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.percentageChangeInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame percentageChangeInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.percentageChangeInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame percentageChangeInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.percentageChangeInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}
	
	
	public DataFrame roundColumns(int decimalPlaces) {
		return roundColumns(0, this.getNumColumns() - 1, decimalPlaces);
	}

	public DataFrame roundColumn(int columnIndex, int decimalPlaces) {
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			this.getValue(columnIndex, rowCount).round(decimalPlaces);
		}
		
		return this;
	}

	public DataFrame roundColumn(String columnName, int decimalPlaces) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return roundColumn(columnIndex, decimalPlaces);
	}

	public DataFrame roundColumns(int[] columnIndices, int decimalPlaces) {
		for (int columnIndex: columnIndices) {
			roundColumn(columnIndex, decimalPlaces);
		}
		
		return this;
	}

	public DataFrame roundColumns(String[] columnNames, int decimalPlaces) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return roundColumns(indices, decimalPlaces);
	}

	public DataFrame roundColumns(ArrayList<String> columnNames, int decimalPlaces) {
		return roundColumns(columnNames.toArray(new String[0]), decimalPlaces);
	}

	public DataFrame roundColumns(int minIndex, int maxIndex, int decimalPlaces) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return roundColumns(indicesToGet, decimalPlaces);
	}

	public DataFrame roundColumns(HashMap<String, Integer> map) {
		for (String columnName: map.keySet()) {
			if (this.columnNames.contains(columnName)) {
				roundColumn(columnName, map.get(columnName));
			}
		}
		return this;
	}

	public DataFrame roundRows(int decimalPlaces) {
		this.transpose();
		DataFrame value = this.roundColumns(decimalPlaces);
		this.transpose();
		return value;
	}

	public DataFrame roundRow(int rowIndex, int decimalPlaces) {
		this.transpose();
		DataFrame value = this.roundColumn(rowIndex, decimalPlaces);
		this.transpose();
		return value;
	}

	public DataFrame roundRow(String rowName, int decimalPlaces) {
		this.transpose();
		DataFrame value = this.roundColumn(rowName, decimalPlaces);
		this.transpose();
		return value;
	}

	public DataFrame roundRows(int[] rowIndices, int decimalPlaces) {
		this.transpose();
		DataFrame value = this.roundColumns(rowIndices, decimalPlaces);
		this.transpose();
		return value;
	}

	public DataFrame roundRows(String[] rowNames, int decimalPlaces) {
		this.transpose();
		DataFrame value = this.roundColumns(rowNames, decimalPlaces);
		this.transpose();
		return value;
	}

	public DataFrame roundRows(ArrayList<String> rowNames, int decimalPlaces) {
		this.transpose();
		DataFrame value = this.roundColumns(rowNames, decimalPlaces);
		this.transpose();
		return value;
	}

	public DataFrame roundRows(int minIndex, int maxIndex, int decimalPlaces) {
		this.transpose();
		DataFrame value = this.roundColumns(minIndex, maxIndex, decimalPlaces);
		this.transpose();
		return value;
	}

	public DataFrame roundRows(HashMap<String, Integer> map) {
		this.transpose();
		DataFrame value = this.roundColumns(map);
		this.transpose();
		return value;
	}

	public int numUnique() {
		double[] serializedValues = new double[this.getNumRows() * this.getNumColumns()];
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				serializedValues[colCount * this.getNumRows() + rowCount] = this.getValue(colCount, rowCount).getValueConvertedToDouble();
			}	
		}
		return CommonArray.numUnique(serializedValues);
	}
	
	public DataFrame numUniqueInColumns() {
		return numUniqueInColumns(0, this.getNumColumns() - 1);
	}

	public int numUniqueInColumn(int columnIndex) {
		String[] column = this.getColumnAsStringArray(columnIndex);
		return CommonArray.numUnique(column);
	}

	public int numUniqueInColumn(String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return numUniqueInColumn(columnIndex);
	}

	public DataFrame numUniqueInColumns(int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("number_of_unique");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, numUniqueInColumn(columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame numUniqueInColumns(String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return numUniqueInColumns(indices);
	}

	public DataFrame numUniqueInColumns(ArrayList<String> columnNames) {
		return numUniqueInColumns(columnNames.toArray(new String[0]));
	}

	public DataFrame numUniqueInColumns(int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return numUniqueInColumns(indicesToGet);
	}

	public DataFrame numUniqueInRows() {
		this.transpose();
		DataFrame value = this.numUniqueInColumns();
		this.transpose();
		return value;
	}

	public int numUniqueInRow(int rowIndex) {
		this.transpose();
		int value = this.numUniqueInColumn(rowIndex);
		this.transpose();
		return value;
	}

	public int numUniqueInRow(String rowName) {
		this.transpose();
		int value = this.numUniqueInColumn(rowName);
		this.transpose();
		return value;
	}

	public DataFrame numUniqueInRows(int[] rowIndices) {
		this.transpose();
		DataFrame value = this.numUniqueInColumns(rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame numUniqueInRows(String[] rowNames) {
		this.transpose();
		DataFrame value = this.numUniqueInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame numUniqueInRows(ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.numUniqueInColumns(rowNames);
		this.transpose();
		return value;
	}

	public DataFrame numUniqueInRows(int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.numUniqueInColumns(minIndex, maxIndex);
		this.transpose();
		return value;
	}

	public Double variance(int dof) {
		double[] serializedValues = new double[this.getNumRows() * this.getNumColumns()];
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				serializedValues[colCount * this.getNumRows() + rowCount] = this.getValue(colCount, rowCount).getValueConvertedToDouble();
			}	
		}
		return CommonMath.variance(serializedValues, dof);
	}

	public DataFrame varianceInColumns(int dof) {
		return varianceInColumns(dof, 0, this.getNumColumns() - 1);
	}

	public Double varianceInColumn(int dof, int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonMath.variance(column, dof);
	}

	public Double varianceInColumn(int dof, String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return varianceInColumn(dof, columnIndex);
	}

	public DataFrame varianceInColumns(int dof, int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("variance");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, varianceInColumn(dof, columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame varianceInColumns(int dof, String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return varianceInColumns(dof, indices);
	}

	public DataFrame varianceInColumns(int dof, ArrayList<String> columnNames) {
		return varianceInColumns(dof, columnNames.toArray(new String[0]));
	}

	public DataFrame varianceInColumns(int dof, int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return varianceInColumns(dof, indicesToGet);
	}

	public DataFrame varianceInRows(int dof) {
		this.transpose();
		DataFrame value = this.varianceInColumns(dof);
		this.transpose();
		return value;
	}

	public Double varianceInRow(int dof, int rowIndex) {
		this.transpose();
		Double value = this.varianceInColumn(dof, rowIndex);
		this.transpose();
		return value;
	}

	public Double varianceInRow(int dof, String rowName) {
		this.transpose();
		Double value = this.varianceInColumn(dof, rowName);
		this.transpose();
		return value;
	}

	public DataFrame varianceInRows(int dof, int[] rowIndices) {
		this.transpose();
		DataFrame value = this.varianceInColumns(dof, rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame varianceInRows(int dof, String[] rowNames) {
		this.transpose();
		DataFrame value = this.varianceInColumns(dof, rowNames);
		this.transpose();
		return value;
	}

	public DataFrame varianceInRows(int dof, ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.varianceInColumns(dof, rowNames);
		this.transpose();
		return value;
	}

	public DataFrame varianceInRows(int dof, int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.varianceInColumns(dof, minIndex, maxIndex);
		this.transpose();
		return value;
	}
	
	public Double standardDeviation(int dof) {
		double[] serializedValues = new double[this.getNumRows() * this.getNumColumns()];
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
				serializedValues[colCount * this.getNumRows() + rowCount] = this.getValue(colCount, rowCount).getValueConvertedToDouble();
			}	
		}
		return CommonMath.standardDeviation(serializedValues, dof);
	}

	public DataFrame standardDeviationInColumns(int dof) {
		return standardDeviationInColumns(dof, 0, this.getNumColumns() - 1);
	}

	public Double standardDeviationInColumn(int dof, int columnIndex) {
		double[] column = this.getColumnAsDoubleArray(columnIndex);
		return CommonMath.standardDeviation(column, dof);
	}

	public Double standardDeviationInColumn(int dof, String columnName) {
		int columnIndex = this.columnNames.indexOf(columnName);
		return standardDeviationInColumn(dof, columnIndex);
	}

	public DataFrame standardDeviationInColumns(int dof, int[] columnIndices) {
		ArrayList<String> columns = new ArrayList<String>();
		for (int columnIndex: columnIndices) {
			columns.add(this.columnNames.get(columnIndex));
		}
		ArrayList<String> row = new ArrayList<String>();
		row.add("standard_deviation");
		DataFrame maxDF = new DataFrame(columns, row);
		
		for (int columnIndex = 0; columnIndex < columnIndices.length; columnIndex++) {
			maxDF.setValue(columnIndex, 0, standardDeviationInColumn(dof, columnIndices[columnIndex]));
		}
		
		return maxDF;
	}

	public DataFrame standardDeviationInColumns(int dof, String[] columnNames) {
		int[] indices = CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames);
		return standardDeviationInColumns(dof, indices);
	}

	public DataFrame standardDeviationInColumns(int dof, ArrayList<String> columnNames) {
		return standardDeviationInColumns(dof, columnNames.toArray(new String[0]));
	}

	public DataFrame standardDeviationInColumns(int dof, int minIndex, int maxIndex) {
		int[] indicesToGet = IntStream.rangeClosed(minIndex, maxIndex).toArray();
		return standardDeviationInColumns(dof, indicesToGet);
	}

	public DataFrame standardDeviationInRows(int dof) {
		this.transpose();
		DataFrame value = this.standardDeviationInColumns(dof);
		this.transpose();
		return value;
	}

	public Double standardDeviationInRow(int dof, int rowIndex) {
		this.transpose();
		Double value = this.standardDeviationInColumn(dof, rowIndex);
		this.transpose();
		return value;
	}

	public Double standardDeviationInRow(int dof, String rowName) {
		this.transpose();
		Double value = this.standardDeviationInColumn(dof, rowName);
		this.transpose();
		return value;
	}

	public DataFrame standardDeviationInRows(int dof, int[] rowIndices) {
		this.transpose();
		DataFrame value = this.standardDeviationInColumns(dof, rowIndices);
		this.transpose();
		return value;
	}

	public DataFrame standardDeviationInRows(int dof, String[] rowNames) {
		this.transpose();
		DataFrame value = this.standardDeviationInColumns(dof, rowNames);
		this.transpose();
		return value;
	}

	public DataFrame standardDeviationInRows(int dof, ArrayList<String> rowNames) {
		this.transpose();
		DataFrame value = this.standardDeviationInColumns(dof, rowNames);
		this.transpose();
		return value;
	}

	public DataFrame standardDeviationInRows(int dof, int minIndex, int maxIndex) {
		this.transpose();
		DataFrame value = this.standardDeviationInColumns(dof, minIndex, maxIndex);
		this.transpose();
		return value;
	}


	
	// ---------------------
	// ------ Setters ------
	// ---------------------
	public void setRowNames(ArrayList<String> rowNamesToAdd) {
		try {
			if (CommonArray.anyNullValues(rowNamesToAdd)) {
				throw new RowNameException("Cannot have any null values in row names");
			}
			
			if (this.rowNames.size() != 0) {
				if (rowNamesToAdd.size() != this.rowNames.size()) {
					throw new DataFrameShapeException("Number of row names (" + rowNamesToAdd.size() + ") must equal number of rows (" + this.rowNames.size() + ")");
				}
			}	
			
			String[] mangledRowNames = CommonArray.mangle(rowNamesToAdd);
			this.rowNames = CommonArray.convertStringArrayToArrayList(mangledRowNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		try {
			if (CommonArray.anyNullValues(colNamesToAdd)) {
				throw new ColumnNameException("Cannot have any null values in row names");
			}
	
			if (this.columnNames.size() != 0) {			
				if (colNamesToAdd.size() != this.columnNames.size()) {
					throw new DataFrameShapeException("Number of column names (" + colNamesToAdd.size() + ") must equal number of columns (" + this.columnNames.size() + ")");
				}
			}
	
			String[] mangledColNames = CommonArray.mangle(colNamesToAdd);
			this.columnNames = CommonArray.convertStringArrayToArrayList(mangledColNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		try {
			if (colNum >= this.columnNames.size()) {
				throw new DataFrameOutOfBoundsException("Column index must be lower than the amount of columns");
			}
			
			if (rowNum >= this.rowNames.size()) {
				throw new DataFrameOutOfBoundsException("Row index must be lower than the amount of rows");
			}
			
			if (colNum < 0) {
				throw new DataFrameOutOfBoundsException("Column index must be greater than 0");
			}
			
			if (rowNum < 0) {
				throw new DataFrameOutOfBoundsException("Row index must be greater than 0");
			}
			
			this.data.get(colNum).set(rowNum, new DataItem(value, type));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setValue(int colNum, int rowNum, Object value) {
		try {
			if (colNum >= this.columnNames.size()) {
				throw new DataFrameOutOfBoundsException("Column index must be lower than the amount of columns");
			}
			
			if (rowNum >= this.rowNames.size()) {
				throw new DataFrameOutOfBoundsException("Row index must be lower than the amount of rows");
			}
			
			if (colNum < 0) {
				throw new DataFrameOutOfBoundsException("Column index must be greater than 0");
			}
			
			if (rowNum < 0) {
				throw new DataFrameOutOfBoundsException("Row index must be greater than 0");
			}
			this.data.get(colNum).set(rowNum, new DataItem(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setValue(String columnName, int rowNum, Object value) {
		setValue(this.columnNames.indexOf(columnName), rowNum, value);
	}
	
	public void setValue(int columnNum, String rowName, Object value) {
		setValue(columnNum, this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, DataItem value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, int value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, float value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, double value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, boolean value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, LocalDate value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, LocalDateTime value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, LocalTime value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, Period value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(String columnName, String rowName, Duration value) {
		setValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), value);
	}
	
	public void setValue(int columnIndex, int rowIndex, DataItem value) {
		this.data.get(columnIndex).set(rowIndex, value);
	}
	
	public void setValue(int columnIndex, int rowIndex, int value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}

	public void setValue(int columnIndex, int rowIndex, float value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}

	public void setValue(int columnIndex, int rowIndex, double value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}

	public void setValue(int columnIndex, int rowIndex, boolean value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}

	public void setValue(int columnIndex, int rowIndex, LocalDate value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}

	public void setValue(int columnIndex, int rowIndex, LocalDateTime value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}
	
	public void setValue(int columnIndex, int rowIndex, LocalTime value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}
	
	public void setValue(int columnIndex, int rowIndex, Period value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}
	
	public void setValue(int columnIndex, int rowIndex, Duration value) {
		this.data.get(columnIndex).set(rowIndex, new DataItem(value));
	}

	public void copySerializedColumnsIntoDataFrame(List<Object> values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			Object[] column = Arrays.copyOfRange(values.toArray(new Object[0]), colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(Object[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			Object[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(DataItem[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			DataItem[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(int[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			int[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(float[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			float[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(double[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			double[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(int numRows, double[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			double[] column = Arrays.copyOfRange(values, colCount * numRows, (colCount + 1) * numRows);
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(boolean[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			boolean[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(String[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			String[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(LocalDate[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			LocalDate[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(LocalDateTime[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			LocalDateTime[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(LocalTime[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			LocalTime[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(Period[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			Period[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	
	public void copySerializedColumnsIntoDataFrame(Duration[] values) {
		for (int colCount = 0; colCount < this.getNumColumns(); colCount++) {
			Duration[] column = Arrays.copyOfRange(values, colCount * this.getNumRows(), (colCount + 1) * this.getNumRows());
			this.setColumnValues(colCount, column);
		}
	}
	

	
	public void setColumnValues(int columnIndex, DataItem[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}

	public void setColumnValues(int columnIndex, Object[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}

	public void setColumnValues(int columnIndex, Object[] column, StorageType type) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i], type));
	}

	public void setColumnValues(int columnIndex, int[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}

	public void setColumnValues(int columnIndex, float[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}

	public void setColumnValues(int columnIndex, double[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}

	public void setColumnValues(int columnIndex, boolean[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}

	public void setColumnValues(int columnIndex, String[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}
	
	public void setColumnValues(int columnIndex, LocalDate[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}
	
	public void setColumnValues(int columnIndex, LocalDateTime[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}

	public void setColumnValues(int columnIndex, LocalTime[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}
	
	public void setColumnValues(int columnIndex, Duration[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}
	
	public void setColumnValues(int columnIndex, Period[] column) {
		IntStream.range(0, column.length).forEach(i -> setValue(columnIndex, i, column[i]));
	}
	
	
	public void setColumnValues(String columnName, DataItem[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, Object[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, Object[] column, StorageType type) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column, type);
	}
	
	public void setColumnValues(String columnName, int[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, float[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, double[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, boolean[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, String[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, LocalDate[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, LocalDateTime[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, LocalTime[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, Duration[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	public void setColumnValues(String columnName, Period[] column) {
		this.setColumnValues(this.columnNames.indexOf(columnName), column);
	}
	
	

	public void setColumnValues(int columnIndex, DataItem value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}

	public void setColumnValues(int columnIndex, Object value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}

	public void setColumnValues(int columnIndex, Object value, StorageType type) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value, type));
	}

	public void setColumnValues(int columnIndex, int value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}

	public void setColumnValues(int columnIndex, float value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}

	public void setColumnValues(int columnIndex, double value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}

	public void setColumnValues(int columnIndex, boolean value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}

	public void setColumnValues(int columnIndex, String value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}
	
	public void setColumnValues(int columnIndex, LocalDate value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}
	
	public void setColumnValues(int columnIndex, LocalDateTime value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}
	
	public void setColumnValues(int columnIndex, LocalTime value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}
	
	public void setColumnValues(int columnIndex, Period value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}
	
	public void setColumnValues(int columnIndex, Duration value) {
		IntStream.range(0, this.getNumRows()).forEach(i -> setValue(columnIndex, i, value));
	}
	
	public void setColumnsValues(int[] columnIndices, DataItem value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnsValues(int[] columnIndices, Object value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnsValues(int[] columnIndices, Object value, StorageType type) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value, type));
	}

	public void setColumnsValues(int[] columnIndices, int value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnsValues(int[] columnIndices, float value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnsValues(int[] columnIndices, double value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnsValues(int[] columnIndices, boolean value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnsValues(int[] columnIndices, String value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnsValues(int[] columnIndices, LocalDate value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}
	
	public void setColumnsValues(int[] columnIndices, LocalDateTime value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}
	
	public void setColumnsValues(int[] columnIndices, LocalTime value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}
	
	public void setColumnsValues(int[] columnIndices, Period value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}
	
	public void setColumnsValues(int[] columnIndices, Duration value) {
		IntStream.range(0, columnIndices.length).forEach(i -> setColumnValues(i, value));
	}

	public void setColumnValues(String columnName, DataItem value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, Object value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, Object value, StorageType type) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value, type);
	}

	public void setColumnValues(String columnName, int value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, float value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, double value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, boolean value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, String value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}
	
	public void setColumnValues(String columnName, LocalDate value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}
	
	public void setColumnValues(String columnName, LocalDateTime value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, LocalTime value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}
	
	public void setColumnValues(String columnName, Period value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}

	public void setColumnValues(String columnName, Duration value) {
		int columnIndex = this.columnNames.indexOf(columnName);
		setColumnValues(columnIndex, value);
	}
	
	public void setColumnsValues(String[] columnNames, DataItem value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}

	public void setColumnsValues(String[] columnNames, Object value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}

	public void setColumnsValues(String[] columnNames, Object value, StorageType type) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value, type));
	}

	public void setColumnsValues(String[] columnNames, int value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}

	public void setColumnsValues(String[] columnNames, float value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}

	public void setColumnsValues(String[] columnNames, double value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}

	public void setColumnsValues(String[] columnNames, boolean value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}

	public void setColumnsValues(String[] columnNames, String value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}
	
	public void setColumnsValues(String[] columnNames, LocalDate value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}

	public void setColumnsValues(String[] columnNames, LocalDateTime value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}
	
	public void setColumnsValues(String[] columnNames, LocalTime value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}
	
	public void setColumnsValues(String[] columnNames, Period value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}
	
	public void setColumnsValues(String[] columnNames, Duration value) {
		IntStream.range(0, columnNames.length).forEach(i -> setColumnValues(columnNames[i], value));
	}
	
	public void setColumnsValues(ArrayList<String> columnNames, DataItem value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(ArrayList<String> columnNames, Object value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(ArrayList<String> columnNames, Object value, StorageType type) {
		setColumnsValues(columnNames.toArray(new String[0]), value, type);
	}

	public void setColumnsValues(ArrayList<String> columnNames, int value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(ArrayList<String> columnNames, float value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(ArrayList<String> columnNames, double value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(ArrayList<String> columnNames, boolean value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(ArrayList<String> columnNames, String value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(ArrayList<String> columnNames, LocalDate value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}
	
	public void setColumnsValues(ArrayList<String> columnNames, LocalDateTime value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}
	
	public void setColumnsValues(ArrayList<String> columnNames, LocalTime value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}
	
	public void setColumnsValues(ArrayList<String> columnNames, Period value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}
	
	public void setColumnsValues(ArrayList<String> columnNames, Duration value) {
		setColumnsValues(columnNames.toArray(new String[0]), value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, DataItem value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, Object value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, Object value, StorageType type) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value, type);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, int value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, float value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, double value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, boolean value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, String value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}
	
	public void setColumnsValues(int lowestIndex, int highestIndex, LocalDate value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}
	
	public void setColumnsValues(int lowestIndex, int highestIndex, LocalDateTime value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}

	public void setColumnsValues(int lowestIndex, int highestIndex, LocalTime value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}
	
	public void setColumnsValues(int lowestIndex, int highestIndex, Period value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}
	
	public void setColumnsValues(int lowestIndex, int highestIndex, Duration value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setColumnsValues(indicesToGet, value);
	}
	
	public void setRowValues(int rowIndex, DataItem[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}

	public void setRowValues(int rowIndex, Object[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}

	public void setRowValues(int rowIndex, Object[] row, StorageType type) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i], type));
	}

	public void setRowValues(int rowIndex, int[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}

	public void setRowValues(int rowIndex, float[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}

	public void setRowValues(int rowIndex, double[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}

	public void setRowValues(int rowIndex, boolean[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}

	public void setRowValues(int rowIndex, String[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}

	public void setRowValues(int rowIndex, LocalDate[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}
	
	public void setRowValues(int rowIndex, LocalDateTime[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}
	
	public void setRowValues(int rowIndex, LocalTime[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}
	
	public void setRowValues(int rowIndex, Period[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}
	
	public void setRowValues(int rowIndex, Duration[] row) {
		IntStream.range(0, row.length).forEach(i -> setValue(i, rowIndex, row[i]));
	}
	
	public void setRowValues(String rowName, DataItem[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, Object[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, Object[] column, StorageType type) {
		setRowValues(this.rowNames.indexOf(rowName), column, type);
	}
	
	public void setRowValues(String rowName, int[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, float[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, double[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, boolean[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, String[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, LocalDate[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, LocalDateTime[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, LocalTime[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, Duration[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	
	public void setRowValues(String rowName, Period[] column) {
		setRowValues(this.rowNames.indexOf(rowName), column);
	}
	


	public void setRowValues(int rowIndex, DataItem value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowValues(int rowIndex, Object value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowValues(int rowIndex, Object value, StorageType type) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value, type));
	}

	public void setRowValues(int rowIndex, int value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowValues(int rowIndex, float value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowValues(int rowIndex, double value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowValues(int rowIndex, boolean value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowValues(int rowIndex, String value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowValues(int rowIndex, LocalDate value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}
	
	public void setRowValues(int rowIndex, LocalDateTime value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}
	
	public void setRowValues(int rowIndex, LocalTime value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}
	
	public void setRowValues(int rowIndex, Period value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}
	
	public void setRowValues(int rowIndex, Duration value) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> setValue(i, rowIndex, value));
	}

	public void setRowsValues(int[] rowIndices, DataItem value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, Object value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, Object value, StorageType type) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value, type));
	}

	public void setRowsValues(int[] rowIndices, int value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, float value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, double value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, boolean value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, String value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}
	
	public void setRowsValues(int[] rowIndices, LocalDate value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}
	
	public void setRowsValues(int[] rowIndices, LocalDateTime value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, LocalTime value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}
	
	public void setRowsValues(int[] rowIndices, Period value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}

	public void setRowsValues(int[] rowIndices, Duration value) {
		IntStream.range(0, rowIndices.length).forEach(i -> setRowValues(i, value));
	}
	
	public void setRowValues(String rowName, DataItem value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}

	public void setRowValues(String rowName, Object value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}

	public void setRowValues(String rowName, Object value, StorageType type) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value, type);
	}

	public void setRowValues(String rowName, int value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}

	public void setRowValues(String rowName, float value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}

	public void setRowValues(String rowName, double value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}

	public void setRowValues(String rowName, boolean value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}

	public void setRowValues(String rowName, String value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}

	public void setRowValues(String rowName, LocalDate value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}
	
	public void setRowValues(String rowName, LocalDateTime value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}
	
	public void setRowValues(String rowName, LocalTime value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}
	
	public void setRowValues(String rowName, Period value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}
	
	public void setRowValues(String rowName, Duration value) {
		int rowIndex = this.rowNames.indexOf(rowName);
		setRowValues(rowIndex, value);
	}
	
	public void setRowsValues(String[] rowNames, DataItem value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}

	public void setRowsValues(String[] rowNames, Object value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}

	public void setRowsValues(String[] rowNames, Object value, StorageType type) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value, type));
	}

	public void setRowsValues(String[] rowNames, int value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
		
	}

	public void setRowsValues(String[] rowNames, float value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}

	public void setRowsValues(String[] rowNames, double value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}

	public void setRowsValues(String[] rowNames, boolean value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}

	public void setRowsValues(String[] rowNames, String value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}

	public void setRowsValues(String[] rowNames, LocalDate value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}
	
	public void setRowsValues(String[] rowNames, LocalDateTime value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}
	
	public void setRowsValues(String[] rowNames, LocalTime value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}
	
	public void setRowsValues(String[] rowNames, Period value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}
	
	public void setRowsValues(String[] rowNames, Duration value) {
		IntStream.range(0, rowNames.length).forEach(i -> setRowValues(rowNames[i], value));
	}

	public void setRowsValues(ArrayList<String> rowNames, DataItem value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}

	public void setRowsValues(ArrayList<String> rowNames, Object value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}

	public void setRowsValues(ArrayList<String> rowNames, Object value, StorageType type) {
		setRowsValues(rowNames.toArray(new String[0]), value, type);
	}

	public void setRowsValues(ArrayList<String> rowNames, int value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}

	public void setRowsValues(ArrayList<String> rowNames, float value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}

	public void setRowsValues(ArrayList<String> rowNames, double value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}

	public void setRowsValues(ArrayList<String> rowNames, boolean value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}

	public void setRowsValues(ArrayList<String> rowNames, String value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}
	
	public void setRowsValues(ArrayList<String> rowNames, LocalDate value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}
	
	public void setRowsValues(ArrayList<String> rowNames, LocalDateTime value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}
	
	public void setRowsValues(ArrayList<String> rowNames, LocalTime value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}
	
	public void setRowsValues(ArrayList<String> rowNames, Period value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}
	
	public void setRowsValues(ArrayList<String> rowNames, Duration value) {
		setRowsValues(rowNames.toArray(new String[0]), value);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, DataItem value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, Object value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, Object value, StorageType type) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value, type);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, int value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, float value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, double value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, boolean value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}

	public void setRowsValues(int lowestIndex, int highestIndex, String value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}
	
	public void setRowsValues(int lowestIndex, int highestIndex, LocalDate value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}
	
	public void setRowsValues(int lowestIndex, int highestIndex, LocalDateTime value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}
	
	public void setRowsValues(int lowestIndex, int highestIndex, LocalTime value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}
	
	public void setRowsValues(int lowestIndex, int highestIndex, Period value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}
	
	public void setRowsValues(int lowestIndex, int highestIndex, Duration value) {
		int[] indicesToGet = IntStream.rangeClosed(lowestIndex, highestIndex).toArray();
		setRowsValues(indicesToGet, value);
	}


	public DataItem getValue(int colNum, int rowNum) {
		try {			
			if (colNum >= this.columnNames.size()) {
				throw new DataFrameOutOfBoundsException("Column index must be lower than the amount of columns");
			}
			
			if (rowNum >= this.rowNames.size()) {
				throw new DataFrameOutOfBoundsException("Row index must be lower than the amount of rows");
			}
			
			if (colNum < 0) {
				throw new DataFrameOutOfBoundsException("Column index must be greater than 0");
			}
			
			if (rowNum < 0) {
				throw new DataFrameOutOfBoundsException("Row index must be greater than 0");
			}
			
			return this.data.get(colNum).get(rowNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public DataItem getValue(String columnName, String rowName) {
		return getValue(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName));
	}
	
	public ArrayList<String> getColumnNames() {
		return this.columnNames;
	}

	public ArrayList<String> getRowNames() {
		return this.rowNames;
	}

	public void addPrefixToColumnNames(String prefix) {
		addPrefixToColumnNames(0, this.getNumColumns() - 1, prefix);
	}
	
	public void addSuffixToColumnNames(String suffix) {
		addSuffixToColumnNames(0, this.getNumColumns() - 1, suffix);
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
	

	public int getNumRows() {
		return this.rowNames.size();
	}

	public int getNumColumns() {
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
	
	public DataFrame[] splitDataFrameByColumn(int index) {
		return this.splitDataFrameByColumns(new int[] {index});
	}
	
	public DataFrame[] splitDataFrameByColumn(String columnName) {
		return this.splitDataFrameByColumns(new String[] {columnName});
	}
	
	public DataFrame[] splitDataFrameByColumns(int[] indices) {
		DataFrame[] df = new DataFrame[indices.length + 1];
		int currentIndex = 0;
		for (int dfCount = 0; dfCount < indices.length; dfCount++) {
			df[dfCount] = this.getColumnsAsDataFrame(currentIndex, indices[dfCount]);
			currentIndex = indices[dfCount] + 1;
		}
		df[df.length - 1] = this.getColumnsAsDataFrame(currentIndex, this.getNumColumns() - 1);
		return df;
	}
	
	public DataFrame[] splitDataFrameByColumns(String[] columnNames) {
		return this.splitDataFrameByColumns(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames));
	}
	
	
	public DataFrame[] splitDataFrameByRow(int index) {
		return this.splitDataFrameByRows(new int[] {index});
	}
	
	public DataFrame[] splitDataFrameByRow(String columnName) {
		return this.splitDataFrameByRows(new String[] {columnName});
	}
	
	public DataFrame[] splitDataFrameByRows(int[] indices) {
		DataFrame[] df = new DataFrame[indices.length + 1];
		int currentIndex = 0;
		for (int dfCount = 0; dfCount < indices.length; dfCount++) {
			df[dfCount] = this.getRowsAsDataFrame(currentIndex, indices[dfCount]);
			currentIndex = indices[dfCount] + 1;
		}
		df[df.length - 1] = this.getRowsAsDataFrame(currentIndex, this.getNumColumns() - 1);
		return df;
	}
	
	public DataFrame[] splitDataFrameByRows(String[] columnNames) {
		return this.splitDataFrameByRows(CommonArray.getIndicesOfStringsInArray(this.columnNames, columnNames));
	}
	
	
	
	
	
	
	
	public DataFrame joinToTheRight(DataFrame newDF, boolean outerJoin, boolean keepDuplicateColumns) {
		setNewColumnNames(newDF, keepDuplicateColumns);
		correctRowsForJoin(newDF, outerJoin);
		
		this.appendColumns(newDF.getColumnNames().toArray(new String[0]));
		
		setValuesInNewDF(newDF);
			
		return this;
	}


	
	public DataFrame joinToTheLeft(DataFrame newDF, boolean outerJoin, boolean keepDuplicateColumns) {
		setNewColumnNames(newDF, keepDuplicateColumns);
		correctRowsForJoin(newDF, outerJoin);
		
		this.insertColumns(0, newDF.getColumnNames().toArray(new String[0]));
		
		setValuesInNewDF(newDF);
			
		return this;
	}


	private void setNewColumnNames(DataFrame newDF, boolean keepDuplicateColumns) {
		ArrayList<String> newColumnNames = newDF.getColumnNames();
		
		if (keepDuplicateColumns) {			
			// mangle newColumnNames
			for (int columnCount = 0; columnCount < newColumnNames.size(); columnCount++) {
				String mangledColumnName = CommonArray.getNewMangleName(this.getColumnNames(), newColumnNames.get(columnCount));
				newColumnNames.set(columnCount, mangledColumnName);
			}
		} else {
			// drop columns in newDF which already exist in existingDF
			ArrayList<Integer> indicesToDrop = new ArrayList<Integer>();
			for (int columnCount = 0; columnCount < newColumnNames.size(); columnCount++) {
				if (this.columnNames.contains(newColumnNames.get(columnCount))) {
					indicesToDrop.add(columnCount);
				}
			}
			for (int indexCount = indicesToDrop.size() - 1; indexCount >= 0; indexCount--) {
				newDF.dropColumn(indicesToDrop.get(indexCount));
			}
		
		}
	}

	@SuppressWarnings("unchecked")
	private void correctRowsForJoin(DataFrame newDF, boolean outerJoin) {
		// Rows in current DF
		ArrayList<String> currentRows = (ArrayList<String>)this.rowNames.clone();
		// Rows in new DF
		ArrayList<String> newRows = (ArrayList<String>)newDF.getRowNames().clone();
		
		// order new rows to be same as other rows (same as charting library)
		if (outerJoin) {
			// Rows in new DF but not in current DF
			ArrayList<String> rowsToAddToCurrentDF = CommonArray.doesntContain(newRows, currentRows);
			this.appendRows(rowsToAddToCurrentDF.toArray(new String[0]));
				
		} else {
			// Rows in the first array but not in the second
			ArrayList<String> rowsToDropInFirst = CommonArray.uncommonStrings(currentRows, newRows);
			this.dropRows(rowsToDropInFirst);
			// Rows in the second array but not in the first
			ArrayList<String> rowsToDropInSecond = CommonArray.uncommonStrings(newRows, currentRows);
			newDF.dropRows(rowsToDropInSecond);
		}
	}
	
	private void setValuesInNewDF(DataFrame newDF) {
		for (int columnCount = 0; columnCount < newDF.getNumColumns(); columnCount++) {
			for (int rowCount = 0; rowCount < newDF.getNumRows(); rowCount++) {
				DataItem value = newDF.getValue(columnCount, rowCount).clone();
				this.setValue(newDF.getColumnNames().get(columnCount), newDF.getRowNames().get(rowCount), value);
			}	
		}
	}
	
	public DataFrame joinBelow(DataFrame newDF, boolean outerJoin, boolean keepDuplicateColumns) {
		this.transpose();
		newDF.transpose();
		this.joinToTheRight(newDF, outerJoin, keepDuplicateColumns);
		this.transpose();
		return this;
	}
	
	public DataFrame joinAbove(DataFrame newDF, boolean outerJoin, boolean keepDuplicateColumns) {
		this.transpose();
		newDF.transpose();
		this.joinToTheLeft(newDF, outerJoin, keepDuplicateColumns);
		this.transpose();
		return this;
	}
	

	public void swapTwoColumns(int columnIndex1, int columnIndex2) {
		DataItem[] tempColumn = this.getColumnAsDataItemArray(columnIndex1);
		String tempColumnName = this.columnNames.get(columnIndex1);
		
		this.setColumnValues(columnIndex1, this.getColumnAsDataItemArray(columnIndex2));
		this.columnNames.set(columnIndex1, this.columnNames.get(columnIndex2));
		
		this.setColumnValues(columnIndex2, tempColumn);
		this.columnNames.set(columnIndex2, tempColumnName);
		
	}
	
	public void swapTwoColumns(String columnName1, String columnName2) {
		swapTwoColumns(this.columnNames.indexOf(columnName1), this.columnNames.indexOf(columnName2));
	}
	
	public void swapTwoRows(int rowIndex1, int rowIndex2) {
		DataItem[] tempRow = this.getRowAsDataItemArray(rowIndex1);
		String tempRowName = this.rowNames.get(rowIndex1);
		
		this.setRowValues(rowIndex1, this.getRowAsDataItemArray(rowIndex2));
		this.rowNames.set(rowIndex1, this.rowNames.get(rowIndex2));
		
		this.setRowValues(rowIndex2, tempRow);
		this.rowNames.set(rowIndex2, tempRowName);
	}
	
	public void swapTwoRows(String rowName1, String rowName2) {
		swapTwoColumns(this.rowNames.indexOf(rowName1), this.rowNames.indexOf(rowName2));
	}
	
	
	private boolean lessThan(String str1, String str2) {
		return (str1.compareToIgnoreCase(str2) < 0);
	}

	public void sortColumnsAlphabetically(boolean ascending) {

		int n = this.getNumColumns(); 
        for (int i = 1; i < n; ++i) { 
            String columnName = this.columnNames.get(i); 
            DataItem[] column = this.getColumnAsDataItemArray(i);
            int j = i - 1;
           
            if (ascending) {            	
            	while (j >= 0 && lessThan(columnName, this.columnNames.get(j))) { 
            		this.columnNames.set(j + 1, this.columnNames.get(j));
            		this.setColumnValues(j + 1, this.getColumnAsDataItemArray(j));
            		j = j - 1; 
            	} 
            } else {
            	while (j >= 0 && !lessThan(columnName, this.columnNames.get(j))) { 
            		this.columnNames.set(j + 1, this.columnNames.get(j));
            		this.setColumnValues(j + 1, this.getColumnAsDataItemArray(j));
            		j = j - 1; 
            	}
            }
            this.columnNames.set(j + 1, columnName);
            this.setColumnValues(j + 1, column);
        } 
	}
	
	public void sortRowsAlphabetically(boolean ascending) {
		int n = this.getNumRows(); 
        for (int i = 1; i < n; ++i) { 
            String rowName = this.rowNames.get(i); 
            DataItem[] row = this.getRowAsDataItemArray(i);
            int j = i - 1;
           
            if (ascending) {            	
            	while (j >= 0 && lessThan(rowName, this.rowNames.get(j))) { 
            		this.rowNames.set(j + 1, this.rowNames.get(j));
            		this.setRowValues(j + 1, this.getRowAsDataItemArray(j));
            		j = j - 1; 
            	} 
            } else {
            	while (j >= 0 && !lessThan(rowName, this.rowNames.get(j))) { 
            		this.rowNames.set(j + 1, this.rowNames.get(j));
            		this.setRowValues(j + 1, this.getRowAsDataItemArray(j));
            		j = j - 1; 
            	}
            }
            this.rowNames.set(j + 1, rowName);
            this.setRowValues(j + 1, row);
        } 
	}
	
	
	public void reverseColumnOrder() {
		for (int columnIndex = 0; columnIndex < this.getNumColumns()/2; columnIndex++) {
			this.swapTwoColumns(columnIndex, this.getNumColumns() - columnIndex - 1);
		}
	}
	
	public void reverseRowOrder() {
		for (int rowIndex = 0; rowIndex < this.getNumRows()/2; rowIndex++) {
			this.swapTwoRows(rowIndex, this.getNumRows() - rowIndex - 1);
		}
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
	
	public void setValueType(int colNum, int rowNum, StorageType type) {
		this.getValue(colNum, rowNum).setType(type);
	}
	
	public void setValueType(String columnName, String rowName, StorageType type) {
		this.setValueType(this.columnNames.indexOf(columnName), this.rowNames.indexOf(rowName), type);
	}
	
	public void setColumnType(int colNum, StorageType type) {
		IntStream.range(0, this.getNumRows()).forEach(i -> this.getValue(colNum, i).setType(type));
	}
	
	public void setColumnType(String columnName, StorageType type) {
		this.setColumnType(this.columnNames.indexOf(columnName), type);
	}
	
	public void setColumnsType(int[] columnNums, StorageType type) {
		IntStream.of(columnNums).forEach(i -> this.setColumnType(i, type));
	}
	
	public void setColumnsType(String[] columnNames, StorageType type) {
		this.setColumnsType(Arrays.asList(columnNames), type);
	}
	
	public void setColumnsType(List<String> columnNames, StorageType type) {
		columnNames.stream().forEach(name -> this.setColumnType(name, type));
	}
	
	public void setColumnsType(int minColumnIndex, int maxColumnIndex, StorageType type) {
		IntStream.rangeClosed(minColumnIndex, maxColumnIndex).forEach(i -> setColumnType(i, type));
	}
	
	public void setColumnsType(StorageType[] types) {
		for (int columnIndex = 0; columnIndex < types.length; columnIndex++) {
			this.setColumnType(columnIndex, types[columnIndex]);
		}
	}
	
	public void setColumnsType(Map<String, StorageType> typesMap) {
		for (String columnName: typesMap.keySet()) {
			setColumnType(columnName, typesMap.get(columnName));
		}
	}
	
	public void setRowType(int rowNum, StorageType type) {
		IntStream.range(0, this.getNumColumns()).forEach(i -> this.getValue(i, rowNum).setType(type));
	}
	
	public void setRowType(String rowName, StorageType type) {
		this.setRowType(this.rowNames.indexOf(rowName), type);
	}
	
	public void setRowsType(int[] rowNums, StorageType type) {
		IntStream.of(rowNums).forEach(i -> this.setRowType(i, type));
	}
	
	public void setRowsType(String[] rowNames, StorageType type) {
		this.setRowsType(Arrays.asList(rowNames), type);
	}
	
	public void setRowsType(List<String> rowNames, StorageType type) {
		rowNames.stream().forEach(name -> this.setRowType(name, type));
	}
	
	public void setRowsType(int minRowIndex, int maxRowIndex, StorageType type) {
		IntStream.rangeClosed(minRowIndex, maxRowIndex).forEach(i -> setRowType(i, type));
	}
	
	public void setRowsType(StorageType[] types) {
		for (int rowIndex = 0; rowIndex < types.length; rowIndex++) {
			this.setRowType(rowIndex, types[rowIndex]);
		}
	}
	
	public void setRowsType(Map<String, StorageType> typesMap) {
		for (String rowName: typesMap.keySet()) {
			setRowType(rowName, typesMap.get(rowName));
		}
	}
	
	
	public DataItem[] getUniqueValuesInColumnAsDataItemArray(int columnIndex) {
		DataItem[] column = this.getColumnAsDataItemArray(columnIndex);
		return CommonArray.getUniqueValues(column);
	}
	
	public String[] getUniqueValuesInColumnAsStringArray(int columnIndex) {
		String[] column = this.getColumnAsStringArray(columnIndex);
		return CommonArray.getUniqueValues(column);
	}
	
	public DataItem[] getUniqueValuesInColumnAsDataItemArray(String columnName) {
		return getUniqueValuesInColumnAsDataItemArray(this.columnNames.indexOf(columnName));
	}
	
	public String[] getUniqueValuesInColumnAsStringArray(String columnName) {
		return getUniqueValuesInColumnAsStringArray(this.columnNames.indexOf(columnName));
	}
	
	public DataFrame getDataFrameWhereColumnValueEquals(int columnIndex, String value) {
		DataFrame newDF = this.clone();
		String[] column = this.getColumnAsStringArray(columnIndex);
		int[] indicesToKeep = CommonArray.indicesOf(column, value);
		int[] indicesToDrop = new int[this.getNumRows() - indicesToKeep.length];
		int index = 0;
		for (int i = 0; i < this.getNumRows(); i++) {
			if (!CommonArray.contains(indicesToKeep, i)) {
				indicesToDrop[index] = i;
				index++;
			}
		}
		newDF.dropRows(indicesToDrop);
		return newDF;
	}
	
	public GroupBy groupBy(String columnName) {
		GroupBy groupBy = new GroupBy(this, columnName);
		
		return groupBy;
	}

	private ArrayList<DataItem> convertObjectListToItemList(List<Object> column) {
		ArrayList<DataItem> list = new ArrayList<DataItem>();
		for (Object item : column) {
			list.add(new DataItem(item));
		}
		return list;
	}
	
	private <T>ArrayList<DataItem> createDataItemList(List<T> column) {
		ArrayList<DataItem> list = new ArrayList<DataItem>();
		for (Object item : column) {
			list.add(new DataItem(item));
		}
		return list;
	}
	
	public boolean columnAllNumbers(int columnIndex) {
		DataItem[] column = this.getColumnAsDataItemArray(columnIndex);
		for (DataItem value: column) {
			if (!value.isNumber()) {
				return false;
			}
		}
		return true;
	}
	
	public int indexOfInColumn(int columnIndex, String value) {
		String[] column = this.getColumnAsStringArray(columnIndex);
		return CommonArray.indexOf(column, value);
	}
	
	public int indexOfInColumn(String columnName, String value) {
		return indexOfInColumn(this.columnNames.indexOf(columnName), value);
	}


	public DataFrame lengthOfStrings() {
		DataFrame lengthDF = DataFrame.zeros(this.columnNames.toArray(new String[0]), this.rowNames.toArray(new String[0]));
		for (int rowCount = 0; rowCount < this.getNumRows(); rowCount++) {
			for (int columnCount = 0; columnCount < this.getNumColumns(); columnCount++) {
				int length = this.getValue(columnCount, rowCount).getValueConvertedToString().length();
				lengthDF.setValue(columnCount, rowCount, length);
			}	
		}
		return lengthDF;
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
		if ((this.getNumColumns() == 0) || (this.getNumRows() == 0) || (this.data.size() == 0)) {
			return "--- Empty DataFrame ---";
		}

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
	
	// ---------------------------
	// ------ Serialization ------
	// ---------------------------
	public void toCSV(String path, boolean preserveRowNames) {
		String strToWrite = "";
		for (int columnIndex = 0; columnIndex < this.getNumColumns(); columnIndex++) {
			strToWrite += this.columnNames.get(columnIndex);
			if (columnIndex < this.getNumColumns() - 1) {
				strToWrite += ",";
			} else {
				strToWrite += "\n";
			}
		}
		int rowCount = 0;
		for (ArrayList<DataItem> row: this) {
			if (preserveRowNames) {
				strToWrite += this.rowNames.get(rowCount) + ",";
				rowCount++;
			}
			for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
				strToWrite += row.get(columnIndex).toString();
				if (columnIndex < row.size() - 1) {
					strToWrite += ",";
				} else {
					strToWrite += "\n";
				}
			}
		}
		CommonFiles.writeFile(path, strToWrite);
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
