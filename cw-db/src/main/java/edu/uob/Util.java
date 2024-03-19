package edu.uob;

import java.io.File;

/**
 * @author force
 */
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
}
