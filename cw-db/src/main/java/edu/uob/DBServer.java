package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    /** DIY attributes*/
    private static final List<Database> DATABASE_LIST = new ArrayList<>();
    private static Database currentDatabase = null;

    private final static List<String> RESERVED_WORD_LIST = Arrays.asList("TRUE", "FALSE", "AND", "OR", "LIKE");

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));

            //遍历数据文件地址，读取数据到内存中
            //Traverse the data file addresses and read data into memory
            File storageFolder = new File(storageFolderPath);
            File[] files = storageFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String name = file.getName();
                        Database database = new Database(name);
                        File[] tables = file.listFiles();
                        if (tables != null) {
                            for (File tableFile : tables) {
                                database.addTable(tableFile);
                            }
                        }
                        DATABASE_LIST.add(database);
                    }
                }
            }

        } catch(IOException ioe) {
            System.out.println("[ERROR] Failure in IO stream create database storage folder " + storageFolderPath);
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        try {
            if (!checkCommandHasText(command)) {
                return "[ERROR]: No command entered";
            }
            List<String> parserList = Parser.setup(command);

            String action = parserList.get(0).toUpperCase();

            String endStr = parserList.get(parserList.size() - 1);
            if (!";".equalsIgnoreCase(endStr)) {
                throw new RuntimeException("Semi colon missing at end of line");
            }

            String databaseName;
            String tableName = null;
            Map<String, List<String>> conditionsMap;
            String type;
            switch (action) {
                case "DROP":
                    if (parserList.size() < 3) {
                        return "[ERROR]: sql syntax error";
                    }
                    type = parserList.get(1).toUpperCase();
                    switch (type) {
                        case "DATABASE":
                            databaseName = parserList.get(2).toLowerCase();
                            dropDatabase(databaseName);
                            break;
                        case "TABLE":
                            //创建数据库表
                            tableName = parserList.get(2).toLowerCase();
                            dropTable(tableName);
                            break;
                        default:
                            return "[ERROR]: sql syntax error";
                    }
                    break;
                case "CREATE":
                    if (parserList.size() < 3) {
                        return "[ERROR]: sql syntax error";
                    }
                    type = parserList.get(1).toUpperCase();
                    switch (type) {
                        case "DATABASE":
                            databaseName = parserList.get(2).toLowerCase();
                            createDatabase(databaseName);
                            if (checkIsReservedWords(databaseName)) {
                                throw new RuntimeException("Use invalid element names reserved SQL keywords)");
                            }
                            break;
                        case "TABLE":
                            //创建数据库表
                            tableName = parserList.get(2).toLowerCase();
                            if (checkIsReservedWords(tableName)) {
                                throw new RuntimeException("Use invalid element names reserved SQL keywords)");
                            }
                            List<String> columnNameList = new ArrayList<>();
                            columnNameList.add("id");
                            for (int i = 3; i < parserList.size(); i++) {
                                String parser = parserList.get(i);
                                if (!Parser.SPECIAL_CHARACTERS.contains(parser)) {
                                    if (checkIsReservedWords(tableName)) {
                                        throw new RuntimeException("Use invalid element names reserved SQL keywords)");
                                    }
                                    columnNameList.add(parser);
                                }
                            }
                            createNewTable(tableName, columnNameList);
                            break;
                        default:
                            throw new RuntimeException("sql syntax error");
                    }
                    break;
                case "USE":
                    if (parserList.size() < 2) {
                        throw new RuntimeException("GET command requires key");
                    }
                    databaseName = parserList.get(1).toLowerCase();
                    useDatabase(databaseName);
                    break;
                case "INSERT":
                    if (parserList.size() < 4 || !"into".equalsIgnoreCase(parserList.get(1)) || !"values".equalsIgnoreCase(parserList.get(3))) {
                        throw new RuntimeException("SET command requires key and value");
                    }
                    tableName = parserList.get(2).toLowerCase();
                    List<String> valueList = new ArrayList<>();
                    for (int i = 4; i < parserList.size(); i++) {
                        String parser = parserList.get(i);
                        if (!Parser.SPECIAL_CHARACTERS.contains(parser)) {
                            valueList.add(parser);
                        }
                    }
                    insertIntoTable(tableName, valueList);
                    break;
                case "SELECT":

                    if (parserList.size() < 4) {
                        throw new RuntimeException("SELECT command requires key and value");
                    }

                    for (int i = 2; i < parserList.size(); i++) {
                        String parser = parserList.get(i);
                        if ("from".equalsIgnoreCase(parser)) {
                            if (i + 1 < parserList.size() && !Parser.SPECIAL_CHARACTERS.contains(parser)) {
                                tableName = parserList.get(i + 1);
                            }
                            break;
                        }
                    }

                    if (!Util.checkStringHasText(tableName)) {
                        throw new RuntimeException("SELECT command requires key and value");
                    }

                    List<String> attributeList = new ArrayList<>();
                    if (!"*".equalsIgnoreCase(parserList.get(1))) {
                        for (int i = 1; i < parserList.size(); i++) {
                            String parser = parserList.get(i);
                            if ("from".equalsIgnoreCase(parser)) {
                                break;
                            }
                            if (!Parser.SPECIAL_CHARACTERS.contains(parser)) {
                                attributeList.add(parser);
                            }
                        }
                    }

                    conditionsMap = getConditionsMap(4, parserList);

                    StringBuilder sb = new StringBuilder();
                    String select = selectFromTable(tableName, attributeList, conditionsMap);
                    if (!checkCommandHasText(select)) {
                        break;
                    } else {
                        sb.append("[OK]");
                        sb.append("\n");
                        sb.append(select);
                    }
                    return sb.toString();
                case "JOIN":
                    List<String> tableNameList = new ArrayList<>();
                    tableNameList.add(parserList.get(1));
                    tableNameList.add(parserList.get(3));
                    List<String> tableAttributeList = new ArrayList<>();
                    tableAttributeList.add(parserList.get(5));
                    tableAttributeList.add(parserList.get(7));
                    List<Row> rowList = executeJoinQuery(tableNameList, tableAttributeList);
                    if (rowList.isEmpty()) {
                        break;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("[OK]");
                        stringBuilder.append("\n");
                        for (int i = 0; i < rowList.size(); i++) {
                            Row row = rowList.get(i);
                            List<String> rowValueList = row.getValueListFromRow();
                            for (String value : rowValueList) {
                                stringBuilder.append(value).append("\t");
                            }
                            if (i != rowList.size() - 1) {
                                stringBuilder.append("\n");
                            }
                        }
                        return stringBuilder.toString();
                    }
                case "DELETE":
                    if (parserList.size() < 3 || !"FROM".equalsIgnoreCase(parserList.get(1))) {
                        throw new RuntimeException("DELETE command requires key");
                    }

                    tableName = parserList.get(2);
                    conditionsMap = getConditionsMap(3, parserList);
                    deleteFromTable(tableName, conditionsMap);
                    break;
                case "UPDATE":
                    if (parserList.size() < 3 || !"SET".equalsIgnoreCase(parserList.get(2))) {
                        throw new RuntimeException("UPDATE command requires key");
                    }

                    tableName = parserList.get(1);

                    List<List<String>> attributesList = new ArrayList<>();
                    List<String> attributes = new ArrayList<>();
                    for (int i = 3; i < parserList.size(); i++) {
                        String parser = parserList.get(i);
                        if ("where".equalsIgnoreCase(parser)) {
                            break;
                        }
                        if (!Parser.SPECIAL_CHARACTERS.contains(parser)) {
                            attributes.add(parser);
                        }
                        if (attributes.size() == 3) {
                            attributesList.add(attributes);
                            attributes = new ArrayList<>();
                        }
                    }

                    if (attributesList.isEmpty()) {
                        throw new RuntimeException("sql syntax error");
                    }

                    conditionsMap = getConditionsMap(4, parserList);
                    updateTable(tableName, attributesList, conditionsMap);
                    break;
                case "ALTER":
                    if (parserList.size() < 5 || !"table".equalsIgnoreCase(parserList.get(1))) {
                        throw new RuntimeException("ALTER command requires key");
                    }
                    tableName = parserList.get(2);
                    String actionType = parserList.get(3);
                    String attribute = parserList.get(4);
                    alterTableStructure(tableName, actionType, attribute);
                    break;
                default:
                    throw new RuntimeException("Unknown command");
            }
            return "[OK]";
        } catch (Exception e) {
            return "[ERROR]: " + e.getMessage();
        }
    }

    /**
     * Execute Join Query, make two tables based on specified attributes.
     *
     * @param tableNameList List of table names to join.
     * @param tableAttributeList List of attributes from each table to join on.
     * @return List of joined rows.
     * @throws RuntimeException if the database is not selected, a table doesn't exist, or an attribute doesn't exist.
     *
     * The join operation includes the following details:
     * - Discards the original table IDs.
     * - Discards matching columns between the two tables, retaining only one copy in the output.
     * - Creates a new unique ID for each generated row.
     * - Prefixes attribute names with their source table's name.
     */
    private List<Row> executeJoinQuery(List<String> tableNameList, List<String> tableAttributeList) {
        validateDatabase();
        validateTablesAndAttributes(tableNameList, tableAttributeList);
        return joinAttributesToTables(tableNameList, tableAttributeList);
    }

    private static void validateDatabase() {
        if (currentDatabase == null) {
            throw new RuntimeException("Database not selected.");
        }
    }

    private void validateTablesAndAttributes(List<String> tableNameList, List<String> tableAttributeList) {
        List<Table> tableList = currentDatabase.getTableList();
        for (int i = 0; i < tableNameList.size(); i++) {
            String tableName = tableNameList.get(i);
            Table table = findTableByName(tableList, tableName);
            if (table == null) {
                throw new RuntimeException("Table '" + currentDatabase.getName() + "." + tableName + "' doesn't exist");
            } else {
                String tableAttribute = tableAttributeList.get(i);
                validateAttributeExists(table, tableAttribute);
            }
        }
    }

    private Table findTableByName(List<Table> tableList, String tableName) {
        for (Table t : tableList) {
            if (tableName.equalsIgnoreCase(t.getName())) {
                return t;
            }
        }
        return null;
    }

    private void validateAttributeExists(Table table, String attribute) {
        List<String> columnNameList = table.getColumnNameList();
        for (String columnName : columnNameList) {
            if (columnName.equalsIgnoreCase(attribute)) {
                return;
            }
        }
        throw new RuntimeException("Unknown column '" + attribute + "' in '" + currentDatabase.getName() + "." + table.getName() + "'");
    }

    private List<Row> joinAttributesToTables(List<String> tableNameList, List<String> tableAttributeList) {
        String firstTableName = tableNameList.get(0);
        String secondTableName = tableNameList.get(1);
        Table firstTable = currentDatabase.getTable(firstTableName);
        Table secondTable = currentDatabase.getTable(secondTableName);

        String firstTableAttribute = tableAttributeList.get(0);
        String secondTableAttribute = tableAttributeList.get(1);

        List<Row> rowList = new ArrayList<>();
        List<Row> firstTableRowList = selectRowListFromTable(firstTableName, new HashMap<>());

        if (firstTableRowList != null && !firstTableRowList.isEmpty()) {
            int firstTableAttributeIndex = getColumnIndex(firstTable.getColumnNameList(), firstTableAttribute);

            for (Row firstTableRow : firstTableRowList) {
                String firstTableRowValue = firstTableRow.getRowValue(firstTableAttributeIndex);
                Map<String, List<String>> conditionsMap = createConditionsMap(secondTableAttribute, firstTableRowValue);
                List<Row> secondTableRowList = selectRowListFromTable(secondTableName, conditionsMap);

                if (secondTableRowList != null && !secondTableRowList.isEmpty()) {
                    if (rowList.isEmpty()) {
                        rowList.add(createHeaderRow(firstTable, secondTable, firstTableAttribute, secondTableAttribute));
                    }
                    rowList.add(createJoinedRow(firstTableRow, secondTableRowList.get(0), firstTable, secondTable, firstTableAttribute, secondTableAttribute, rowList.size()));
                }
            }
        }

        return rowList;
    }

    private int getColumnIndex(List<String> columnNames, String attributeName) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equalsIgnoreCase(attributeName)) {
                return i;
            }
        }
        return -1;
    }

    private Map<String, List<String>> createConditionsMap(String attributeName, String value) {
        Map<String, List<String>> conditionsMap = new HashMap<>();
        List<String> condition = new ArrayList<>();
        condition.add(attributeName);
        condition.add("==");
        condition.add(value);
        conditionsMap.put(attributeName, condition);
        return conditionsMap;
    }

    private Row createHeaderRow(Table firstTable, Table secondTable, String firstTableAttribute, String secondTableAttribute) {
        List<String> valueList = new ArrayList<>();
        valueList.add("id");

        for (String columnName : firstTable.getColumnNameList()) {
            if (!"id".equalsIgnoreCase(columnName) && !columnName.equalsIgnoreCase(firstTableAttribute)) {
                valueList.add(firstTable.getName() + "." + columnName);
            }
        }

        for (String columnName : secondTable.getColumnNameList()) {
            if (!"id".equalsIgnoreCase(columnName) && !columnName.equalsIgnoreCase(secondTableAttribute)) {
                valueList.add(secondTable.getName() + "." + columnName);
            }
        }

        return new Row(valueList);
    }

    private Row createJoinedRow(Row firstTableRow, Row secondTableRow, Table firstTable, Table secondTable, String firstTableAttribute, String secondTableAttribute, int rowIndex) {
        List<String> valueList = new ArrayList<>();
        valueList.add(String.valueOf(rowIndex));

        for (int i = 0; i < firstTable.getColumnNameList().size(); i++) {
            String columnName = firstTable.getColumnNameList().get(i);
            if (!"id".equalsIgnoreCase(columnName) && !columnName.equalsIgnoreCase(firstTableAttribute)) {
                valueList.add(firstTableRow.getRowValue(i));
            }
        }

        for (int i = 0; i < secondTable.getColumnNameList().size(); i++) {
            String columnName = secondTable.getColumnNameList().get(i);
            if (!"id".equalsIgnoreCase(columnName) && !columnName.equalsIgnoreCase(secondTableAttribute)) {
                valueList.add(secondTableRow.getRowValue(i));
            }
        }

        return new Row(valueList);
    }

    private void useDatabase(String name) {
        if (DATABASE_LIST.isEmpty()) {
            throw new RuntimeException("Database not created");
        } else {
            for (Database database : DATABASE_LIST) {
                String databaseName = database.getName();
                if (databaseName.equals(name)) {
                    currentDatabase = database;
                    break;
                }
            }
            if (currentDatabase == null) {
                throw new RuntimeException("Database not found");
            }
        }
    }

    public static boolean checkCommandHasText(String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();

        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private static void createDatabase(String name) {
        if (!DATABASE_LIST.isEmpty()) {
            for (Database database : DATABASE_LIST) {
                String databaseName = database.getName();
                if (name.equals(databaseName)) {
                    throw new RuntimeException("Database already exists");
                }
            }
        }
        //创建数据库
        File file = Paths.get("databases" + File.separator + name).toAbsolutePath().toFile();
        if (!file.mkdirs()) {
            throw new RuntimeException("Database creation failed");
        }
        Database database = new Database(name);
        DATABASE_LIST.add(database);
    }

    private static void dropDatabase(String name) {
        Database deleteDatabase = null;
        if (!DATABASE_LIST.isEmpty()) {
            for (Database database : DATABASE_LIST) {
                String databaseName = database.getName();
                if (name.equals(databaseName)) {
                    deleteDatabase = database;
                    break;
                }
            }
        }
        if (deleteDatabase == null) {
            throw new RuntimeException("Database not found");
        }
        //创建数据库
        File databaseFile = Paths.get("databases" + File.separator + name).toAbsolutePath().toFile();
        if (!Util.recursivelyDeleteFile(databaseFile)) {
            throw new RuntimeException("Database deletion failed");
        }

        DATABASE_LIST.remove(deleteDatabase);
        if (deleteDatabase == currentDatabase) {
            currentDatabase = null;
        }
    }

    private static void createNewTable(String name, List<String> columnNameList) {
        validateDatabase();
        List<Table> tableList = currentDatabase.getTableList();
        if (!tableList.isEmpty()) {
            for (Table table : tableList) {
                String tableName = table.getName();
                if (tableName.equals(name)) {
                    throw new RuntimeException("Table already exists");
                }
            }
        }


        File file = Paths.get("databases" + File.separator + currentDatabase.getName() +
                File.separator + name + ".tab").toAbsolutePath().toFile();

        if (file.exists()) {
            if (file.delete()) {
                throw new RuntimeException("Table creation failed");
            }
        }
        try {
            if (!file.createNewFile()) {
                throw new RuntimeException("Table creation failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("Table creation failed");
        }

        try (FileWriter fw = new FileWriter(file)) {
            if (!columnNameList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String colName : columnNameList) {
                    sb.append(colName.trim()).append("\t");
                }
                sb.append("\n");
                fw.write(sb.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException("Table creation failed");
        }
        Table table = new Table(name);
        table.setColumnNameList(columnNameList);
        table.setColumnCount(0);

        tableList.add(table);
        currentDatabase.setTableList(tableList);
        updateDatabaseListInMemory(currentDatabase);
    }

    /**
     * 释放表，删除数据库表的信息
     *
     * @param name 表名称
     */
    private static void dropTable(String name) {
        validateDatabase();
        Table currentTable = null;
        List<Table> tableList = currentDatabase.getTableList();
        if (!tableList.isEmpty()) {
            for (Table table : tableList) {
                String tableName = table.getName();
                if (tableName.equals(name)) {
                    currentTable = table;
                    break;
                }
            }
        }
        if (currentTable == null) {
            throw new RuntimeException("Table not found");
        }

        File table = Paths.get("databases" + File.separator + currentDatabase.getName() +
                File.separator + name + ".tab").toAbsolutePath().toFile();

        //删除表文件
        if (!Util.recursivelyDeleteFile(table)) {
            throw new RuntimeException("Table deletion failed");
        }
        //更新内存信息
        tableList.remove(currentTable);
        currentDatabase.setTableList(tableList);
        updateDatabaseListInMemory(currentDatabase);
    }

    /**
     * 插入命令
     *
     * @param tableName 表名
     * @param valueList 插入的值
     */
    private static void insertIntoTable(String tableName, List<String> valueList) {
        validateDatabase();
        List<Table> tableList = currentDatabase.getTableList();
        if (tableList == null || tableList.isEmpty()) {
            throw new RuntimeException("Database not found");
        }

        Table currentTable = null;
        int currentTableIndex = 0;
        for (int i = 0; i < tableList.size(); i++) {
            Table table = tableList.get(i);
            String name = table.getName();
            if (name.equals(tableName)) {
                currentTable = table;
                currentTableIndex = i;
            }
        }

        if (currentTable == null) {
            throw new RuntimeException("Database not found");
        }

        //插入行的id索引
        int columnCount = currentTable.getColumnCount();
        List<Row> rowList = currentTable.getRowList();

        List<String> rowColumnList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(++columnCount).append("\t");
        rowColumnList.add(String.valueOf(columnCount));
        for (String value : valueList) {
            sb.append(value).append("\t");
            rowColumnList.add(value);
        }
        List<String> insertRowList = new ArrayList<>();
        String value = sb.toString();
        insertRowList.add(value);
        rowList.add(new Row(rowColumnList));
        File tableFile = Paths.get("databases" + File.separator + currentDatabase.getName() +
                File.separator + currentTable.getName() + ".tab").toAbsolutePath().toFile();
        writeTableFile(tableFile, true, insertRowList);
        currentTable.setColumnCount(columnCount);
        currentTable.setRowList(rowList);
        tableList.set(currentTableIndex, currentTable);
        currentDatabase.setTableList(tableList);
        updateDatabaseListInMemory(currentDatabase);
    }

    /**
     * 查找命令
     *
     * @param tableName     表名
     * @param attributeList 属性名
     * @param conditionsMap 条件
     * @return 筛选后的数据
     */
    private static String selectFromTable(String tableName, List<String> attributeList, Map<String, List<String>> conditionsMap) {
        validateDatabase();
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            File file = Paths.get("databases" + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab").toAbsolutePath().toFile();
            if (file.exists()) {
                currentDatabase.addTable(file);
                for (int i = 0; i < DATABASE_LIST.size(); i++) {
                    Database database = DATABASE_LIST.get(i);
                    String name = database.getName();
                    if (name.equalsIgnoreCase(currentDatabase.getName())) {
                        DATABASE_LIST.set(i, currentDatabase);
                    }
                }
                table = currentDatabase.getTable(tableName);
            }
        }
        if (table == null) {
            throw new RuntimeException("Database not found");
        }

        List<String> columnNameList = table.getColumnNameList();
        List<Row> conditionsRowList = selectRowListFromTable(tableName, conditionsMap);

        StringBuilder sb = new StringBuilder();
        if (conditionsRowList != null && !conditionsRowList.isEmpty()) {
            // 筛选结束打印剩余行
            List<Integer> printColumn = new ArrayList<>();
            for (int i = 0; i < columnNameList.size(); i++) {
                String columnName = columnNameList.get(i);
                if (!attributeList.isEmpty() && attributeList.contains(columnName)) {
                    printColumn.add(i);
                }
            }
            if (printColumn.isEmpty()) {
                for (String columnName : columnNameList) {
                    sb.append(columnName).append("\t");
                }
            } else {
                for (Integer index : printColumn) {
                    sb.append(columnNameList.get(index)).append("\t");
                }
            }
            sb.append("\n");
            for (int i = 0; i < conditionsRowList.size(); i++) {
                Row row = conditionsRowList.get(i);
                if (printColumn.isEmpty()) {
                    List<String> valueList = row.getValueListFromRow();
                    for (String value : valueList) {
                        if ("null".equals(value)) {
                            sb.append("\t").append("\t");
                        } else {
                            sb.append(value).append("\t");
                        }
                    }
                } else {
                    for (Integer index : printColumn) {
                        String value = row.getRowValue(index);
                        if ("null".equals(value)) {
                            sb.append("\t").append("\t");
                        } else {
                            sb.append(value).append("\t");
                        }
                    }
                }
                if (i != conditionsRowList.size() - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 查找命令
     *
     * @param tableName     表名
     * @param conditionsMap 条件
     * @return 筛选后的数据
     */
    private static List<Row> selectRowListFromTable(String tableName, Map<String, List<String>> conditionsMap) {
        validateDatabase();
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("Database not found");
        }

        List<String> columnNameList = table.getColumnNameList();
        //查询不存在的属性时候，只读到WHERE并没有判断属性本身存在与否
        if (!conditionsMap.isEmpty()) {
            Set<String> keySet = conditionsMap.keySet();
            for (String key : keySet) {
                boolean columnNameExist = false;
                for (String columnName : columnNameList) {
                    if (columnName.equalsIgnoreCase(key)) {
                        columnNameExist = true;
                        break;
                    }
                }
                if (!columnNameExist) {
                    throw new RuntimeException("Unknown column '" + key + "' in 'where clause'");
                }
            }
        }
        List<Row> rowList = table.getRowList();
        if (columnNameList.isEmpty() || rowList.isEmpty()) {
            return null;
        }
        List<Row> conditionsRowList = new ArrayList<>();
        for (Row row : rowList) {
            //逐行遍历
            List<String> valueList = row.getValueListFromRow();
            if (!valueList.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : valueList) {
                    stringBuilder.append(s);
                }
                if (!checkCommandHasText(stringBuilder.toString())) {
                    break;
                }
                boolean isSelect = true;
                if (!conditionsMap.isEmpty()) {
                    for (int i = 0; i < columnNameList.size(); i++) {
                        String columnName = columnNameList.get(i);
                        List<String> conditions = conditionsMap.get(columnName);
                        if (conditions != null && !conditions.isEmpty()) {
                            //筛选条件含有此字段
                            String value = row.getRowValue(i);
                            String compare = conditions.get(1);
                            String valueStr = conditions.get(2);
                            try {
                                if (!checkCommandHasText(valueStr) || !checkCommandHasText(value)) {
                                    isSelect = false;
                                    break;
                                } else if (("==".equals(compare) || "=".equals(compare)) && !value.equals(valueStr)) {
                                    isSelect = false;
                                    break;
                                } else if ("!=".equals(compare) && value.equals(valueStr)) {
                                    isSelect = false;
                                    break;
                                } else if (">".equals(compare) && Double.parseDouble(value) <= Double.parseDouble(valueStr)) {
                                    isSelect = false;
                                    break;
                                } else if ("<".equals(compare) && Double.parseDouble(value) >= Double.parseDouble(valueStr)) {
                                    isSelect = false;
                                    break;
                                } else if (">=".equals(compare) && Double.parseDouble(value) < Double.parseDouble(valueStr)) {
                                    isSelect = false;
                                    break;
                                } else if ("<=".equals(compare) && Double.parseDouble(value) > Double.parseDouble(valueStr)) {
                                    isSelect = false;
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Number format exception");
                            }
                        }

                    }
                }
                if (isSelect) {
                    conditionsRowList.add(row);
                }
            }
        }
        return conditionsRowList;
    }

    /**
     * 删除命令
     *
     * @param tableName     表名
     * @param conditionsMap 筛选条件
     */
    private void deleteFromTable(String tableName, Map<String, List<String>> conditionsMap) {
        validateDatabase();
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("Database not found");
        }

        List<String> columnNameList = table.getColumnNameList();
        //查询不存在的属性时候，只读到WHERE并没有判断属性本身存在与否
        if (!conditionsMap.isEmpty()) {
            Set<String> keySet = conditionsMap.keySet();
            for (String key : keySet) {
                boolean columnNameExist = false;
                for (String columnName : columnNameList) {
                    if (columnName.equalsIgnoreCase(key)) {
                        columnNameExist = true;
                        break;
                    }
                }
                if (!columnNameExist) {
                    throw new RuntimeException("Unknown column '" + key + "' in 'where clause'");
                }
            }
        }
        List<Row> rowList = table.getRowList();
        if (columnNameList.isEmpty() || rowList.isEmpty()) {
            return;
        }
        boolean updateFile = false;
        for (int i = 0; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            List<String> valueList = row.getValueListFromRow();
            if (!valueList.isEmpty()) {
                boolean isDelete = false;
                if (conditionsMap.isEmpty()) {
                    //没有条件直接删除
                    isDelete = true;
                } else {
                    for (int j = 0; j < columnNameList.size(); j++) {
                        String columnName = columnNameList.get(j);
                        List<String> conditions = conditionsMap.get(columnName);
                        if (conditions != null && !conditions.isEmpty()) {
                            //筛选条件含有此字段
                            String value = row.getRowValue(j);
                            String compare = conditions.get(1);
                            String valueStr = conditions.get(2);
                            try {
                                if (!checkCommandHasText(valueStr) || !checkCommandHasText(value)) {
                                    break;
                                } else if (("==".equals(compare) || "=".equals(compare)) && !value.equals(valueStr)) {
                                    break;
                                } else if ("!=".equals(compare) && value.equals(valueStr)) {
                                    break;
                                } else if (">".equals(compare) && Double.parseDouble(value) <= Double.parseDouble(valueStr)) {
                                    break;
                                } else if ("<".equals(compare) && Double.parseDouble(value) >= Double.parseDouble(valueStr)) {
                                    break;
                                } else if (">=".equals(compare) && Double.parseDouble(value) < Double.parseDouble(valueStr)) {
                                    break;
                                } else if ("<=".equals(compare) && Double.parseDouble(value) > Double.parseDouble(valueStr)) {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Number format exception");
                            }
                            //满足条件则修改
                            isDelete = true;
                        }
                    }
                }
                if (isDelete) {
                    rowList.set(i, new Row(new ArrayList<>()));
                    updateFile = true;
                }
            }
        }
        table.setRowList(rowList);
        currentDatabase.setTableList(tableName, table);
        if (updateFile) {
            File tableFile = Paths.get("databases" + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab")
                    .toAbsolutePath().toFile();
            writeToFile(tableFile, false, table);
        }
    }

    /**
     * 更新命令
     *
     * @param tableName      表名
     * @param attributesList 属性名和属性值
     * @param conditionsMap  筛选条件
     */
    private void updateTable(String tableName, List<List<String>> attributesList, Map<String, List<String>> conditionsMap) {
        validateDatabase();
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("Database not found");
        }

        List<String> columnNameList = table.getColumnNameList();
        //查询不存在的属性时候，只读到WHERE并没有判断属性本身存在与否
        if (!conditionsMap.isEmpty()) {
            Set<String> keySet = conditionsMap.keySet();
            for (String key : keySet) {
                boolean columnNameExist = false;
                for (String columnName : columnNameList) {
                    if (columnName.equalsIgnoreCase(key)) {
                        columnNameExist = true;
                        break;
                    }
                }
                if (!columnNameExist) {
                    throw new RuntimeException("Unknown column '" + key + "' in 'where clause'");
                }
            }
        }
        List<Row> rowList = table.getRowList();
        if (columnNameList.isEmpty() || rowList.isEmpty()) {
            return;
        }
        //是否需要更新文件
        boolean updateFile = false;
        for (Row row : rowList) {
            List<String> valueList = row.getValueListFromRow();
            if (!valueList.isEmpty()) {
                boolean isUpdate = false;
                if (conditionsMap.isEmpty()) {
                    //没有条件直接修改
                    isUpdate = true;
                } else {
                    for (int j = 0; j < columnNameList.size(); j++) {
                        String columnName = columnNameList.get(j);
                        List<String> conditions = conditionsMap.get(columnName);
                        if (conditions != null && !conditions.isEmpty()) {
                            //筛选条件含有此字段
                            String value = row.getRowValue(j);
                            String compare = conditions.get(1);
                            String valueStr = conditions.get(2);
                            try {
                                if (!checkCommandHasText(valueStr) || !checkCommandHasText(value)) {
                                    break;
                                } else if (("==".equals(compare) || "=".equals(compare)) && !value.equals(valueStr)) {
                                    break;
                                } else if ("!=".equals(compare) && value.equals(valueStr)) {
                                    break;
                                } else if (">".equals(compare) && Double.parseDouble(value) <= Double.parseDouble(valueStr)) {
                                    break;
                                } else if ("<".equals(compare) && Double.parseDouble(value) >= Double.parseDouble(valueStr)) {
                                    break;
                                } else if (">=".equals(compare) && Double.parseDouble(value) < Double.parseDouble(valueStr)) {
                                    break;
                                } else if ("<=".equals(compare) && Double.parseDouble(value) > Double.parseDouble(valueStr)) {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Number format exception");
                            }
                            //满足条件则修改
                            isUpdate = true;
                        }
                    }
                }
                if (isUpdate) {
                    for (int j = 0; j < columnNameList.size(); j++) {
                        String columnName = columnNameList.get(j);
                        for (List<String> attributes : attributesList) {
                            String attributeName = attributes.get(0);
                            if (columnName.equals(attributeName)) {
                                row.setRowValue(j, attributes.get(2));
                            }
                        }
                    }
                    updateFile = true;
                }
            }
        }
        table.setRowList(rowList);
        currentDatabase.setTableList(tableName, table);
        if (updateFile) {
            File tableFile = Paths.get("databases" + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab")
                    .toAbsolutePath().toFile();
            writeToFile(tableFile, false, table);
        }
    }

    /**
     * 修改表结构命令
     *
     * @param tableName  表名称
     * @param actionType 命令烈性
     * @param attribute  属性
     */
    private void alterTableStructure(String tableName, String actionType, String attribute) {
        validateDatabase();
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("Table not found queries on non-existent tables");
        }
        if ("ADD".equalsIgnoreCase(actionType)) {
            List<String> columnNameList = table.getColumnNameList();
            for (String columnName : columnNameList) {
                if (columnName.equalsIgnoreCase(actionType)) {
                    throw new RuntimeException("Attribute already exists");
                }
            }
            columnNameList.add(attribute);
            table.setColumnNameList(columnNameList);
            List<Row> rowList = table.getRowList();
            if (!rowList.isEmpty()) {
                for (Row row : rowList) {
                    List<String> valueList = row.getValueListFromRow();
                    valueList.add("null");
                }
            }
        } else if ("DROP".equalsIgnoreCase(actionType)) {
            List<String> columnNameList = table.getColumnNameList();
            int attributeIndex = -1;
            for (int i = 0; i < columnNameList.size(); i++) {
                String columnName = columnNameList.get(i);
                if (columnName.equalsIgnoreCase(attribute)) {
                    attributeIndex = i;
                    break;
                }
            }
            if (attributeIndex < 0) {
                return;
            }

            columnNameList.remove(attributeIndex);
            List<Row> rowList = table.getRowList();
            for (Row row : rowList) {
                List<String> valueList = row.getValueListFromRow();
                valueList.remove(attributeIndex);
            }

            table.setColumnNameList(columnNameList);
            table.setRowList(rowList);
        }
        currentDatabase.setTableList(tableName, table);
        File tableFile = Paths.get("databases" + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab")
                .toAbsolutePath().toFile();
        writeToFile(tableFile, false, table);
    }

    /**
     * 写入文件
     *
     * @param file    文件
     * @param append  是否拼接写
     * @param rowList 行列表
     */
    private static void writeTableFile(File file, Boolean append, List<String> rowList) {
        try (FileWriter fw = new FileWriter(file, append)) {
            for (String row : rowList) {
                row += "\n";
                fw.write(row);
                fw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Insert data fail");
        }
    }

    /**
     * 写入文件
     *
     * @param file   文件
     * @param append 是否拼接写
     * @param table  表
     */
    private static void writeToFile(File file, Boolean append, Table table) {
        try (FileWriter fw = new FileWriter(file, append)) {
            List<String> columnNameList = table.getColumnNameList();
            StringBuilder sb = new StringBuilder();
            for (String columnName : columnNameList) {
                sb.append(columnName).append("\t");
            }
            fw.write(sb.toString());
            fw.write("\n");
            List<Row> rowList = table.getRowList();
            if (!rowList.isEmpty()) {
                for (Row row : rowList) {
                    List<String> valueList = row.getValueListFromRow();
                    if (!valueList.isEmpty()) {
//                        fw.write("\n");
//                    } else {
                        sb = new StringBuilder();
                        for (String value : valueList) {
                            if (value == null || "null".equalsIgnoreCase(value)) {
                                value = "\t";
                            }
                            sb.append(value).append("\t");
                        }
                        fw.write(sb.toString());
                    }
                    fw.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Insert data fail");
        }
    }

    /**
     * 获取where条件后的map
     *
     * @param startIndex 开始索引
     * @param parserList 命令列表
     * @return 条件map
     */
    private static Map<String, List<String>> getConditionsMap(int startIndex, List<String> parserList) {
        int whereIndex = -1;
        Map<String, List<String>> conditionsMap = new HashMap<>();
        for (int i = startIndex; i < parserList.size(); i++) {
            String parser = parserList.get(i);
            if ("where".equalsIgnoreCase(parser)) {
                whereIndex = i + 1;
                break;
            }
        }

        if (whereIndex > 0) {
            List<String> conditions = new ArrayList<>();
            String key = null;
            for (int i = whereIndex; i < parserList.size(); i++) {
                String parser = parserList.get(i);
                if (!Parser.SPECIAL_CHARACTERS.contains(parser) && !"and".equalsIgnoreCase(parser)) {
                    if (!checkCommandHasText(key)) {
                        key = parser;
                    }
                    conditions.add(parser);
                }
                if (conditions.size() == 3) {
                    if (conditionsMap.containsKey(key)) {
                        throw new RuntimeException("sql syntax error");
                    }
                    conditionsMap.put(key, conditions);
                    conditions = new ArrayList<>();
                    key = null;
                }
            }
        }
        return conditionsMap;
    }

    /**
     * 更新数据库内存信息
     *
     * @param database 数据库
     */
    private static void updateDatabaseListInMemory(Database database) {
        for (int i = 0; i < DATABASE_LIST.size(); i++) {
            Database db = DATABASE_LIST.get(i);
            String name = db.getName();
            if (name.equals(database.getName())) {
                DATABASE_LIST.set(i, database);
                break;
            }
        }
    }

    private static boolean checkIsReservedWords(String word) {
        if (!checkCommandHasText(word)) {
            return false;
        }
        for (String reservedWord : RESERVED_WORD_LIST) {
            if (word.equalsIgnoreCase(reservedWord)) {
                return true;
            }
        }
        return false;
    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
