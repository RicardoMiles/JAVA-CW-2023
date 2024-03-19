package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;/*随时删除*/

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
            String tableName;
            String type;
            switch (action) {
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
                            return "ERROR: sql语句错误";
                    }
                    break;
                case "USE":
                    if (parserList.size() != 2) {
                        throw new RuntimeException("GET command requires key");
                    }
                    databaseName = parserList.get(1).toLowerCase();
                    use(databaseName);
                    break;
                case "INSERT":
                    if (parserList.size() < 4 || !"into".equals(parserList.get(1)) || !"values".equals(parserList.get(3))) {
                        return "ERROR: SET command requires key and value";
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
                default:
                    throw new RuntimeException("Unknown command");
            }
            return "[OK]";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
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
