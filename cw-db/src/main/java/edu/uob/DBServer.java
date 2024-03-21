package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;/*后加*/
import java.util.Map;/*后加*/
import java.util.HashMap;/*后加*/

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    /** DIY attributes*/
    private static final List<Database> DATABASE_LIST = new ArrayList<>();
    private static Database currentDatabase = null;

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
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
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
            if (!hasText(command)) {
                return "ERROR: No command entered";
            }
            List<String> parserList = Parser.setup(command);

            String action = parserList.get(0).toUpperCase();

            String databaseName;
            String tableName = null;
            Map<String, List<String>> conditionsMap;
            String type;
            switch (action) {
                case "DROP":
                    if (parserList.size() < 3) {
                        return "ERROR: sql语句错误";
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
                            return "ERROR: sql语句错误";
                    }
                    break;
                case "CREATE":
                    if (parserList.size() < 3) {
                        return "ERROR: sql语句错误";
                    }
                    type = parserList.get(1).toUpperCase();
                    switch (type) {
                        case "DATABASE":
                            databaseName = parserList.get(2).toLowerCase();
                            createDatabase(databaseName);
                            break;
                        case "TABLE":
                            //创建数据库表
                            tableName = parserList.get(2).toLowerCase();
                            List<String> columnNameList = new ArrayList<>();
                            columnNameList.add("id");
                            for (int i = 3; i < parserList.size(); i++) {
                                String parser = parserList.get(i);
                                if (!Parser.SPECIAL_CHARACTERS.contains(parser)) {
                                    columnNameList.add(parser);
                                }
                            }
                            createTable(tableName, columnNameList);
                            break;
                        default:
                            throw new RuntimeException("sql语句错误");
                    }
                    break;
                case "USE":
                    if (parserList.size() < 2) {
                        throw new RuntimeException("GET command requires key");
                    }
                    databaseName = parserList.get(1).toLowerCase();
                    use(databaseName);
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
                    insert(tableName, valueList);
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

                    if (!Util.hasText(tableName)) {
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
                    String select = select(tableName, attributeList, conditionsMap);
                    if (!hasText(select)) {
                        break;
                    } else {
                        sb.append("[OK]");
                        sb.append("\n");
                        sb.append(select);
                    }
                    return sb.toString();
                case "DELETE":
                    if (parserList.size() < 3 || !"FROM".equalsIgnoreCase(parserList.get(1))) {
                        throw new RuntimeException("DELETE command requires key");
                    }

                    tableName = parserList.get(2);
                    conditionsMap = getConditionsMap(3, parserList);
                    delete(tableName, conditionsMap);
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
                        throw new RuntimeException("sql 语句错误");
                    }

                    conditionsMap = getConditionsMap(4, parserList);
                    update(tableName, attributesList, conditionsMap);
                    break;
                case "ALTER":
                    if (parserList.size() < 5 || !"table".equalsIgnoreCase(parserList.get(1))) {
                        throw new RuntimeException("ALTER command requires key");
                    }
                    tableName = parserList.get(2);
                    String actionType = parserList.get(3);
                    String attribute = parserList.get(4);
                    alter(tableName, actionType, attribute);
                    break;
                default:
                    throw new RuntimeException("Unknown command");
            }
            return "[OK]";
        } catch (Exception e) {
            return "[ERROR]: " + e.getMessage();
        }
    }

    private void use(String name) {
        if (DATABASE_LIST.isEmpty()) {
            throw new RuntimeException("未创建数据库");
        } else {
            for (Database database : DATABASE_LIST) {
                String databaseName = database.getName();
                if (databaseName.equals(name)) {
                    currentDatabase = database;
                    break;
                }
            }
            if (currentDatabase == null) {
                throw new RuntimeException("未找到数据库");
            }
        }
    }
    public static boolean hasText(String str) {
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
                    throw new RuntimeException("数据库已存在");
                }
            }
        }
        //创建数据库
        File file = Paths.get("databases" + File.separator + name).toAbsolutePath().toFile();
        if (!file.mkdirs()) {
            throw new RuntimeException("数据库创建失败");
        }
        Database database = new Database(name);
        DATABASE_LIST.add(database);
    }

    private static void dropDatabase(String name) {
        Database currentDatabase = null;
        if (!DATABASE_LIST.isEmpty()) {
            for (Database database : DATABASE_LIST) {
                String databaseName = database.getName();
                if (name.equals(databaseName)) {
                    currentDatabase = database;
                    break;
                }
            }
        }
        if (currentDatabase == null) {
            throw new RuntimeException("未找到数据库");
        }
        //创建数据库
        File databaseFile = Paths.get("databases" + File.separator + name).toAbsolutePath().toFile();
        if (!Util.deleteFile(databaseFile)) {
            throw new RuntimeException("删除数据库失败");
        }

        DATABASE_LIST.remove(currentDatabase);
    }

    private static void createTable(String name, List<String> columnNameList) {
        if (currentDatabase == null) {
            throw new RuntimeException("先选择数据库");
        }
        List<Table> tableList = currentDatabase.getTableList();
        if (!tableList.isEmpty()) {
            for (Table table : tableList) {
                String tableName = table.getName();
                if (tableName.equals(name)) {
                    throw new RuntimeException("表已存在");
                }
            }
        }


        File file = Paths.get("databases" + File.separator + currentDatabase.getName() +
                File.separator + name + ".tab").toAbsolutePath().toFile();

        if (file.exists()) {
            if (file.delete()) {
                throw new RuntimeException("表创建失败");
            }
        }
        try {
            if (!file.createNewFile()) {
                throw new RuntimeException("表创建失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("表创建失败");
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
            throw new RuntimeException("表创建失败");
        }
        Table table = new Table(name);
        table.setColumnNameList(columnNameList);
        table.setColumnCount(1);

        tableList.add(table);
        currentDatabase.setTableList(tableList);
        updateDatabaseList(currentDatabase);
    }

    /**
     * 释放表，删除数据库表的信息
     *
     * @param name 表名称
     */
    private static void dropTable(String name) {
        if (currentDatabase == null) {
            throw new RuntimeException("先选择数据库");
        }
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
            throw new RuntimeException("未找到数据库表");
        }

        File table = Paths.get("databases" + File.separator + currentDatabase.getName() +
                File.separator + name + ".tab").toAbsolutePath().toFile();

        //删除表文件
        if (!Util.deleteFile(table)) {
            throw new RuntimeException("删除表失败");
        }
        //更新内存信息
        tableList.remove(currentTable);
        currentDatabase.setTableList(tableList);
        updateDatabaseList(currentDatabase);
    }

    /**
     * 插入命令
     *
     * @param tableName 表名
     * @param valueList 插入的值
     */
    private static void insert(String tableName, List<String> valueList) {
        if (currentDatabase == null) {
            throw new RuntimeException("未选中数据库");
        }
        List<Table> tableList = currentDatabase.getTableList();
        if (tableList == null || tableList.isEmpty()) {
            throw new RuntimeException("未找到数据表");
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
            throw new RuntimeException("未找到数据表");
        }

        //插入行的id索引
        int columnCount = currentTable.getColumnCount();
        List<Row> rowList = currentTable.getRowList();

        List<String> rowValueList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(++columnCount).append("\t");
        rowValueList.add(String.valueOf(columnCount));
        for (String value : valueList) {
            sb.append(value).append("\t");
            rowValueList.add(value);
        }
        List<String> insertRowList = new ArrayList<>();
        String value = sb.toString();
        insertRowList.add(value);
        rowList.add(new Row(rowValueList));
        File tableFile = Paths.get("databases" + File.separator + currentDatabase.getName() +
                File.separator + currentTable.getName() + ".tab").toAbsolutePath().toFile();
        writeFile(tableFile, true, insertRowList);
        currentTable.setColumnCount(columnCount);
        currentTable.setRowList(rowList);
        tableList.set(currentTableIndex, currentTable);
        currentDatabase.setTableList(tableList);
        updateDatabaseList(currentDatabase);
    }

    /**
     * 查找命令
     *
     * @param tableName     表名
     * @param attributeList 属性名
     * @param conditionsMap 条件
     * @return 筛选后的数据
     */
    private static String select(String tableName, List<String> attributeList, Map<String, List<String>> conditionsMap) {
        if (currentDatabase == null) {
            throw new RuntimeException("未选择数据库");
        }
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("未找到数据表");
        }

        List<String> columnNameList = table.getColumnNameList();
        List<Row> rowList = table.getRowList();
        if (columnNameList.isEmpty() || rowList.isEmpty()) {
            return null;
        }
        List<Row> conditionsRowList = new ArrayList<>();
        for (Row row : rowList) {
            //逐行遍历

            List<String> valueList = row.getValueList();
            if (!valueList.isEmpty()) {
                if (conditionsMap.isEmpty()) {
                    conditionsRowList.add(row);
                } else {
                    for (int i = 0; i < columnNameList.size(); i++) {
                        String columnName = columnNameList.get(i);
                        List<String> conditions = conditionsMap.get(columnName);
                        if (conditions != null && !conditions.isEmpty()) {
                            //筛选条件含有此字段
                            String value = row.getValue(i);
                            String compare = conditions.get(1);
                            String valueStr = conditions.get(2);
                            try {
                                if (!hasText(valueStr) || !hasText(value)) {
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
                            //满足条件则添加到打印的行中
                            conditionsRowList.add(row);
                        }
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        if (!conditionsRowList.isEmpty()) {
            //筛选结束打印剩余行
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
                    List<String> valueList = row.getValueList();
                    for (String value : valueList) {
                        if ("null".equals(value)) {
                            sb.append("\t").append("\t");
                        } else {
                            sb.append(value).append("\t");
                        }
                    }
                } else {
                    for (Integer index : printColumn) {
                        String value = row.getValue(index);
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
     * 删除命令
     *
     * @param tableName     表名
     * @param conditionsMap 筛选条件
     */
    private void delete(String tableName, Map<String, List<String>> conditionsMap) {
        if (currentDatabase == null) {
            throw new RuntimeException("未选择数据库");
        }
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("未找到数据表");
        }

        List<String> columnNameList = table.getColumnNameList();
        List<Row> rowList = table.getRowList();
        if (columnNameList.isEmpty() || rowList.isEmpty()) {
            return;
        }
        boolean updateFile = false;
        for (int i = 0; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            List<String> valueList = row.getValueList();
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
                            String value = row.getValue(j);
                            String compare = conditions.get(1);
                            String valueStr = conditions.get(2);
                            try {
                                if (!hasText(valueStr) || !hasText(value)) {
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
            writeFile(tableFile, false, table);
        }
    }

    /**
     * 更新命令
     *
     * @param tableName      表名
     * @param attributesList 属性名和属性值
     * @param conditionsMap  筛选条件
     */
    private void update(String tableName, List<List<String>> attributesList, Map<String, List<String>> conditionsMap) {
        if (currentDatabase == null) {
            throw new RuntimeException("未选择数据库");
        }
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("未找到数据表");
        }

        List<String> columnNameList = table.getColumnNameList();
        List<Row> rowList = table.getRowList();
        if (columnNameList.isEmpty() || rowList.isEmpty()) {
            return;
        }
        //是否需要更新文件
        boolean updateFile = false;
        for (Row row : rowList) {
            List<String> valueList = row.getValueList();
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
                            String value = row.getValue(j);
                            String compare = conditions.get(1);
                            String valueStr = conditions.get(2);
                            try {
                                if (!hasText(valueStr) || !hasText(value)) {
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
                                row.setValue(j, attributes.get(2));
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
            writeFile(tableFile, false, table);
        }
    }

    /**
     * 修改表结构命令
     *
     * @param tableName  表名称
     * @param actionType 命令烈性
     * @param attribute  属性
     */
    private void alter(String tableName, String actionType, String attribute) {
        if (currentDatabase == null) {
            throw new RuntimeException("未选择数据库");
        }
        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new RuntimeException("未找到数据表");
        }
        if ("ADD".equalsIgnoreCase(actionType)) {
            List<String> columnNameList = table.getColumnNameList();
            if (columnNameList.contains(attribute)) {
                throw new RuntimeException("属性已存在");
            }
            columnNameList.add(attribute);
            table.setColumnNameList(columnNameList);
            List<Row> rowList = table.getRowList();
            if (!rowList.isEmpty()) {
                for (Row row : rowList) {
                    List<String> valueList = row.getValueList();
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
                List<String> valueList = row.getValueList();
                valueList.remove(attributeIndex);
            }

            table.setColumnNameList(columnNameList);
            table.setRowList(rowList);
        }
        currentDatabase.setTableList(tableName, table);
        File tableFile = Paths.get("databases" + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab")
                .toAbsolutePath().toFile();
        writeFile(tableFile, false, table);
    }

    /**
     * 写入文件
     *
     * @param file    文件
     * @param append  是否拼接写
     * @param rowList 行列表
     */
    private static void writeFile(File file, Boolean append, List<String> rowList) {
        try (FileWriter fw = new FileWriter(file, append)) {
            for (String row : rowList) {
                row += "\n";
                fw.write(row);
                fw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("插入数据失败");
        }
    }

    /**
     * 写入文件
     *
     * @param file   文件
     * @param append 是否拼接写
     * @param table  表
     */
    private static void writeFile(File file, Boolean append, Table table) {
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
                    List<String> valueList = row.getValueList();
                    if (!valueList.isEmpty()) {
//                        fw.write("\n");
//                    } else {
                        sb = new StringBuilder();
                        for (String value : valueList) {
                            sb.append(value).append("\t");
                        }
                        fw.write(sb.toString());
                    }
                    fw.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("插入数据失败");
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
                if (!Parser.SPECIAL_CHARACTERS.contains(parser)) {
                    if (!hasText(key)) {
                        key = parser;
                    }
                    conditions.add(parser);
                }
                if (conditions.size() == 3) {
                    if (conditionsMap.containsKey(key)) {
                        throw new RuntimeException("sql 语句错误");
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
    private static void updateDatabaseList(Database database) {
        for (int i = 0; i < DATABASE_LIST.size(); i++) {
            Database db = DATABASE_LIST.get(i);
            String name = db.getName();
            if (name.equals(database.getName())) {
                DATABASE_LIST.set(i, database);
                break;
            }
        }
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
