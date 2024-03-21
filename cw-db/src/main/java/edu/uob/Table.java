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
     */
    private String name;

    /**
     * 表的列名，即表头名
     */
    private List<String> columnNameList;

    /**
     * 表的行
     */
    private List<Row> rowList;

    /**
     * 表行的个数（包括空行）
     */
    private int columnCount;

    public Table(String name) {
        this.name = name;
        this.columnNameList = new ArrayList<>();
        this.rowList = new ArrayList<>();
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    public List<Row> getRowList() {
        return rowList;
    }

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
