package edu.uob;

import java.io.File;


public class Util {

    /**
     * 是否含有文本
     * Checks if the given string has non-whitespace text.
     *
     * @param str The string to check. 字符串
     * @return True if the string contains non-whitespace text, false otherwise. 是否含有文本
     */
    public static boolean checkStringHasText(String str) {
        return str != null && !str.isEmpty() && checkStringContainsNonWhitespace(str);
    }

    /**
     * Checks if the given CharSequence contains any non-whitespace characters.
     *
     * @param str The CharSequence to check.
     * @return True if the CharSequence contains non-whitespace characters, false otherwise.
     */
    private static boolean checkStringContainsNonWhitespace(CharSequence str) {
        int strLen = str.length();

        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 递归文件删除
     * Recursively deletes a file or directory.
     * If the file is a directory, all its contents are also deleted.
     *
     * @param file The file or directory to delete. 文件
     * @return True if the file or directory was successfully deleted, false otherwise. 是否删除成功
     */
    public static boolean recursivelyDeleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File child : files) {
                        if (child.isFile()) {
                            return child.delete();
                        } else if (file.isDirectory()) {
                            return recursivelyDeleteFile(file);
                        }
                    }
                } else {
                    return file.delete();
                }
            }
        }
        return true;
    }
}
