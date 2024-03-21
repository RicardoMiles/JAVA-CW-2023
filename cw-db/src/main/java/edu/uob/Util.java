package edu.uob;

import java.io.File;


public class Util {

    /**
     * 是否含有文本
     *
     * @param str 字符串
     * @return 是否含有文本
     */
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

    /**
     * 递归文件删除
     *
     * @param file 文件
     * @return 是否删除成功
     */
    public static boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File child : files) {
                        if (child.isFile()) {
                            return child.delete();
                        } else if (file.isDirectory()) {
                            return deleteFile(file);
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
