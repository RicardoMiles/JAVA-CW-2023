package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class Database {

    /**
     * 数据库名称
     */
    private String name;

    /**
     * 数据库表列表
     */
    private List<Table> tableList = new ArrayList<>();

    public Database(String name) {
        this.name = name;
    }

    public Database(String name, List<Table> tables) {
        this.name = name;
        this.tableList = tables;
    }

    public String getName() {
        return name;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    public void setTableList(String tableName, Table table) {
        for (int i = 0; i < this.tableList.size(); i++) {
            Table thisTable = this.tableList.get(i);
            if (thisTable.getName().equals(tableName)) {
                this.tableList.set(i, table);
            }
        }
    }

    /**
     * 新增表及文件
     *
     * @param tableFile 表的文件
     */
    public void addTable(File tableFile) {
        String name = tableFile.getName();
        if (!name.endsWith(".tab")) {
            return;
        }
        String tableName = tableFile.getName().substring(0, name.length() - 4);
        Table table = new Table(tableName);
        try (FileReader fr = new FileReader(tableFile); BufferedReader br = new BufferedReader(fr)) {
            String line;
            int index = -1;
            List<String> columnNameList = new ArrayList<>();
            List<Row> rowList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (index == -1) {
                    //插入表的首行即列名
                    String[] split = line.split("\t");
                    for (String s : split) {
                        if (Util.checkStringHasText(s)) {
                            columnNameList.add(s.trim());
                        }
                    }
                } else {
                    //插入表的每行的值
                    List<String> valueList = new ArrayList<>();
                    if (Util.checkStringHasText(line)) {
                        String[] split = line.split("\t");
                        for (String s : split) {
                            if (Util.checkStringHasText(s)) {
                                if ("null".equals(s)) {
                                    valueList.add("\t");
                                } else {
                                    valueList.add(s.trim());
                                }
                            }
                        }
                    }
                    rowList.add(new Row(valueList));
                }
                index++;
            }
            table.setColumnCount(index);
            table.setColumnNameList(columnNameList);
            table.setRowList(rowList);
        } catch (Exception e) {
            throw new RuntimeException("Error reading table file");
        }
        this.tableList.add(table);
    }

    public void addTable(Table table) {
        String tableName = table.getName();
        if (tableName == null || tableName.isEmpty()) {
            throw new RuntimeException("Table name cannot be null or empty");
        }
        Table queryTable = getTable(tableName);
        if (queryTable != null) {
            throw new RuntimeException("Table already exists");
        }
        this.tableList.add(table);
    }

    public void dropTable(String name) {
        this.tableList.removeIf(table -> table.getName().equals(name));
    }

    public Table getTable(String name) {
        for (Table table : tableList) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }
}
