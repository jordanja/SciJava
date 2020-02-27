package thesis.DataFrame;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import thesis.DataFrame.DataItem.StorageType;

public class DataFrame implements Iterable<ArrayList<DataItem>>{

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
	
	/*
	 * Create an empty DF with rows and columns and null values
	 */
	
	public DataFrame(ArrayList<String> colNames, ArrayList<String> rowNames) {
		this();
		
		for (int rowCount = 0; rowCount < rowNames.size(); rowCount++) {
			ArrayList<Object> row = new ArrayList<Object>(colNames.size());
			for (int colCount = 0; colCount < colNames.size(); colCount++) {
				row.add(null);
			}
			AppendRow(row);
		}
		this.colNames = colNames;
		this.rowNames = rowNames;
	}
	
	/*
	 * Create a DF object from a Hashmap (cols)
	 */
	public DataFrame(HashMap<String,ArrayList<Object>> map) {
		this();
		
		AppendColumn(map);
		
	}
	
	/*
	 * Create a DF from list of hashmaps (rows)
	 */
	public DataFrame(ArrayList<HashMap<String, Object>> maps) {
		this();
		for (HashMap<String, Object> map: maps) {
			AppendRow(map);
		}
		
	}

	
	public DataFrame(DataFrame df) {
		this();
		this.colNames = df.GetColumnNames();
		this.rowNames = df.GetRowNames();
		this.data = df.GetData();
	}
	
	
	/*
	 * Create a DF object with one list
	 */
	public DataFrame(String name, ArrayList<Object> list, boolean isRow) {
		this();
		
		if (isRow == true) {
			AppendRow(name, list);
		} else {
			AppendColumn(name, list);
		}
		

	}
	
	public DataFrame(String name, Object[] array, boolean isRow) {
		this(name, new ArrayList<Object>(Arrays.asList(array)),isRow);
	}
	
	
	/*
	 * Create a DF object form multiple columns
	 */
	public DataFrame(ArrayList<String> names, ArrayList<ArrayList<Object>> lists, boolean isRow) {
		this();
		if (names.size() != lists.size()) {
			System.out.println("Number of names = " + names.size() + ", number of lists = " + lists.size());
			return;
		}
		
		for (int i = 0; i < names.size(); i++) {
			if (isRow == true) {
				AppendRow(names.get(i), lists.get(i));
			} else {
				AppendColumn(names.get(i), lists.get(i));
			}
		}
		
	}
	
	
	/*
	 * Create a DF object form a csv file
	 */
	public DataFrame(String filePath, boolean hasHeaderRow) {
		this();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    int rowNum = 0;
		    
		    while ((line = br.readLine()) != null) {
		    	
		        List<String> values = Arrays.asList(line.replace("\"", "").replaceAll("[^a-zA-Z0-9, ./<>?;:\"'`!@#$%^&*()\\[\\]{}_+=|\\\\-]", "").trim().split(","));
		        
		        if (rowNum == 0) { // Header row

		        	this.colNames = new ArrayList<String>(values);

		        } else {
		        	if (values.size() > 0) {
		        		AppendRow(new ArrayList<Object>(values));
		        	}
		        	
		        }

		        rowNum++;
		        
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Rename rows 
	 * 
	 */
	public void setRowNames(ArrayList<String> rowNamesToAdd) {
		if (!valuesUnique(rowNamesToAdd)) {
			System.out.println("All row names must be unique");
			return;
		}
		
		if (anyNullValues(rowNamesToAdd)) {
			System.out.println("Cannot have any null values in row names");
			return;
		}
		
		if (rowNamesToAdd.size() != this.rowNames.size()) {
			System.out.println("Number of row names must equal number of rows");
			return;
		}
		
		this.rowNames = rowNamesToAdd;
		
	}
	
	public void resetRowNames() {
		this.rowNames = generateRowNumbers(this.rowNames.size());
	}
	

	public void setColumnNames(String[] colNamesToUse) {
		if (colNamesToUse.length != this.colNames.size()) {
			System.out.println("Number of columns does not equal number of columns provided");
			return;
		}
		
		this.colNames = new ArrayList<String>(Arrays.asList(colNamesToUse));
		

	}
	public void resetColumnNames() {
		this.colNames = generateRowNumbers(this.colNames.size());
	}

	
	public void InsertColumn(int index, String name, ArrayList<Object> column) {
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
			this.rowNames = generateRowNumbers(column.size());
		}
		this.data.add(index,convertObjectListToItemList(column));
		this.colNames.add(index,name);

		
	}

	public void AppendColumn(String name, ArrayList<Object> column) {
		
		InsertColumn(this.colNames.size(), name, column);

	}

	public void AppendColumn(HashMap<String,ArrayList<Object>> map) {
		for (String key: map.keySet()) {
			this.AppendColumn(key, map.get(key));
		}
	}

	public void AppendColumn(ArrayList<Object> column) {
		AppendColumn(String.valueOf(this.colNames.size()), column);
	}

	public void AppendColumns(ArrayList<ArrayList<Object>> columns) {
		for (ArrayList<Object> column: columns) {
			AppendColumn(column);
		}
	}
	
	public void AppendColumns(ArrayList<String> names, ArrayList<ArrayList<Object>> columns) {
		int i = 0;
		for (ArrayList<Object> column: columns) {
			AppendColumn(names.get(i), column);
		}
	}
	
	
	public void AppendRow(String rowName, ArrayList<Object> row) {
		if (this.rowNames.size() != 0) {
			if (row.size() != this.colNames.size()) {
				System.out.println(row);
				System.out.println(row.size() + " != " + this.colNames.size());
				System.out.println("Your row does not have the correct amount of columns");
				return;
			}
		}
		for (int colCount = 0; colCount < row.size(); colCount++) {
			if (this.rowNames.size() == 0) {
				this.data.add(new ArrayList<DataItem>());
				if (this.colNames.size() <= colCount) {
					this.colNames.add(String.valueOf(colCount)); // changed this out for csv. may need to rethink
				}
			}
			this.data.get(colCount).add(new DataItem(row.get(colCount)));
		}
		
		
		this.rowNames.add(rowName);
		
	}
	
	public void AppendDataItemRow(ArrayList<DataItem> row) {
		
	}
	
	public void AppendRow(ArrayList<Object> row) {
		String rowName = String.valueOf(this.rowNames.size());
		AppendRow(rowName, row);
	}
	
	public void AppendRow(HashMap<String, Object> map) {
		ArrayList<Object> row = new ArrayList<Object>();
		for (String colName: map.keySet()) {
			int colIndex = this.colNames.indexOf(colName);
			if (colIndex == -1) {
				this.colNames.add(colName);

			}
			row.add(map.get(colName));

			
		}
		AppendRow(row);
		
	}
	
	public void AppendRows(ArrayList<ArrayList<Object>> rows) {
		for (int newRowCount = 0; newRowCount < rows.size(); newRowCount++) {
			AppendRow(rows.get(newRowCount));
		}
		
	}
	
	public void Transpose() {
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
	public void DropColumn(String name) {
		int colIndex = -1;
		for (int i = 0; i < this.colNames.size(); i++) {
			if (this.colNames.get(i) == name) {
				colIndex = i;
				
			}
		}
		if (colIndex != -1) {
			this.data.remove(colIndex);
			this.colNames.remove(colIndex);
		}
	}
	
	
	public void DropColumn(ArrayList<String> names) {

		for (String name: names) {
			DropColumn(name);
			
		}

	}
	
	public DataItem[] GetColumnAsArray(String name) {
		
		
		int index = this.colNames.indexOf(name);
		DataItem[] column = new DataItem[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i);
		}
		return column;
	}
	
	public String[] GetColumnAsStringArray(String name) {
		int index = this.colNames.indexOf(name);
		String[] column = new String[this.rowNames.size()];
		for (int i = 0; i < column.length; i++) {
			column[i] = this.data.get(index).get(i).toString();
		}
		return column;
	}
	

	public DataFrame GetColumns(String name) {
		ArrayList<String> names = new ArrayList<String>();
		names.add(name);
		return GetColumns(names);
	}
	
	public DataFrame GetColumns(ArrayList<String> names) {
		DataFrame newDF = new DataFrame();
		boolean addedCol = false;
			
		for (int i = 0; i < this.colNames.size(); i++) {
			if (names.contains((this.colNames.get(i)))) {
				newDF.AppendColumn(this.colNames.get(i), convertItemToObject(this.data.get(i)));
				addedCol = true;
			}
		}
		if (addedCol) {
			newDF.rowNames = this.rowNames;
			
		}
		return newDF;
	}
	

	public DataFrame GetColumn(int col) {		
		return GetColumns(this.colNames.get(col));

	}

	public DataFrame GetRows(ArrayList<String> names) {
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
	
	public DataFrame GetRows(int lowerBound, int upperBound) {
		
		if (lowerBound < 0) {
			System.out.println("Lower bound must be greater than 0");
			return null;
		}
		
		if (lowerBound > upperBound) {
			System.out.println("Lower bound must be less than or equal to upper bound");
			return null;
		}
		
		if (upperBound > rowNames.size()-1) {
			System.out.println("Upper bound must be less than the number of rows");
			return null;
		}
		
		
		ArrayList<String> names = new ArrayList<String>();
		for (int i = lowerBound; i <= upperBound; i++) {
			names.add(this.rowNames.get(i));
		}
		return GetRows(names);
	}
	
	public DataFrame GetRows(int row) {
		ArrayList<String> names = new ArrayList<String>();
		names.add(this.rowNames.get(row));
		
		return GetRows(names);

	}
	
	public DataFrame GetRows(String row) {
		ArrayList<String> names = new ArrayList<String>();
		names.add(row);
		
		return GetRows(names);
	}
	
	public DataFrame Head() {
		return Head(5);
	}
	
	public DataFrame Head(int number) {			
		return GetRows(0,number-1);
	}
	
	public DataFrame Tail() {
		return Tail(5);
	}
	
	public DataFrame Tail(int number) {
		return GetRows(this.rowNames.size() - number,this.rowNames.size() - 1);
	}

	// Fix duplication of error checks
	public void SetValue(int colNum, int rowNum, Object value, StorageType type) {
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
		
		this.data.get(colNum).set(rowNum, new DataItem(value, type));

		
	}
	
	public void SetValue(int colNum, int rowNum, Object value) {
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
	
	public ArrayList<String> GetColumnNames() {
		return this.colNames;
	}
	
	public ArrayList<String> GetRowNames() {
		return this.rowNames;
	}
	
	public ArrayList<ArrayList<DataItem>> GetData() {
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
					nullDF.SetValue(colCount, rowCount, true);
				} else {
					nullDF.SetValue(colCount, rowCount, false);
				}
			}
		}

		return nullDF;
	}
	
	
	
	
	
	public void setColumnType(int colNum, StorageType type) {
		for (int rowNum = 0; rowNum < this.getNumRows(); rowNum++) {
			this.SetValue(colNum, rowNum, getValue(colNum, rowNum).getObjectValue(), type);
		}
	}


	private ArrayList<DataItem> convertObjectListToItemList(ArrayList<Object> column) {
		ArrayList<DataItem> list = new ArrayList<DataItem>();
		for (Object item: column) {
//			System.out.println("item = " + item);
			list.add(new DataItem(item));
		}
		return list;
	}
	
	private ArrayList<Object> convertItemToObject(ArrayList<DataItem> arrayList) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (DataItem item: arrayList) {
			list.add(item);
		}
		return list;
	}

	private ArrayList<String> generateRowNumbers(int size) {
		ArrayList<String> rowNames = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			rowNames.add(String.valueOf(i));
		}
		return rowNames;
	}

	
	private boolean anyNullValues(ArrayList<String> rowNamesToAdd) {
		for (int i = 0; i < rowNamesToAdd.size(); i++) {
			if (rowNamesToAdd.get(i) == null) {
				return true;
			}
		}
		return false;
	}
	
	
	
	private boolean valuesUnique(ArrayList<String> rowNamesToAdd) {
		Set<String> set = new HashSet<String>(rowNamesToAdd);
		
		if(set.size() < rowNamesToAdd.size()){
			return false;
		}
		return true;
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
			writeChar(0,"|");
			int numberOfWhiteSpace = ((largestColWidths[colCount] + dataPadding) - colNames.get(colCount).length());
			
			writeWholeBlock(colNames.get(colCount), 0, numberOfWhiteSpace, rightAlign);
		}
		
		// Row of dashes
		for (int i = 0; i < largestIndexWidth + indexPadding; i++) {
			writeChar(1,"-");
		}
		for (int colCount = 0; colCount < colNames.size(); colCount++) {
			writeChar(1,"+");
			for (int dashCount = 0; dashCount < largestColWidths[colCount] + dataPadding; dashCount++) {
				writeChar(1, "-");
			}

		}
		
		//Fill in each row
		for (int rowCount = 0; rowCount < this.rowNames.size(); rowCount++) {
			int gridRowNum = rowCount + 2;
			String currentIndex = String.valueOf(rowNames.get(rowCount));
			
			int numberOfWhiteSpace = ((largestIndexWidth + indexPadding) - currentIndex.length());
			
			writeWholeBlock(currentIndex,gridRowNum, numberOfWhiteSpace, rightAlign);
			
			for (int colCount = 0; colCount < this.colNames.size(); colCount++) {
				writeChar(gridRowNum,"|");
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
			writeChar(gridRowNum,  currentItem.charAt(charCount));
		}
	}

	private ArrayList<DataItem> convertObjectsToDataItems(ArrayList<Object> objectArray) {
		ArrayList<DataItem> dataItemArray = new ArrayList<DataItem>();
		for (Object obj: objectArray) {
			dataItemArray.add(new DataItem(obj));
		}
		return dataItemArray;
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







