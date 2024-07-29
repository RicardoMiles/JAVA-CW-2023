package edu.uob;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表类
 *
 */
public class Table {
    /**
     * 表名称
     * Table name
     */
    private String name;

    /**
     * 表的列名，即表头名
     * Column names of the table, i.e., the header names
     */
    private List<String> columnNameList;

    /**
     * 表的行
     * Rows of the table
     */
    private List<Row> rowList;

    /**
     * 表行的个数（包括空行）
     * Number of columns in the table (including empty columns)
     */
    private int columnCount;

    public Table(String name) {
        this.name = name;
        this.columnNameList = new ArrayList<>();
        this.rowList = new ArrayList<>();
    }

    /**
     * Gets the list of column names of the table.
     *
     * @return A list of column names.
     */
    public List<String> getColumnNameList() {
        return columnNameList;
    }

    /**
     * Sets the list of column names of the table.
     *
     * @param columnNameList A list of column names to set.
     */
    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    public List<Row> getRowList() {
        return rowList;
    }

    /**
     * Sets the list of rows of the table.
     *
     * @param rowList A list of rows to set.
     */
    public void setRowList(List<Row> rowList) {
        this.rowList = rowList;
    }

    public void addColumn(String name) {
        this.columnNameList.add(name);
    }

    public void insertRow(List<String> values) {
        Row row = new Row(values);
        this.rowList.add(row);
    }

    public void deleteRow(int index) {
        this.rowList.remove(index);
    }

    public void deleteRow(Row row) {
        this.rowList.remove(row);
    }

    public String getName() {
        return name;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
