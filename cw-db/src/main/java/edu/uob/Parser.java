package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Parser {

    // Query Example - "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ; ";
    final static List<String> SPECIAL_CHARACTERS = Arrays.asList("(", ")", ",", ";");

    public static List<String> setup(String query) {
        List<String> tokens = new ArrayList<>();
        // Remove any whitespace at the beginning and end of the query
        // 删除查询开头和结尾的任何空白
        query = query.trim();
        // Split the query on single quotes (to separate out query characters from string literals)
        // 用单引号分隔查询(将查询字符从字符串字面量中分隔出来)
        String[] fragments = query.split("'");
        for (int i = 0; i < fragments.length; i++) {
            // Every odd fragment is a string literal, so just append it without any alterations
            // 每个奇数片段都是字符串字面值，因此只需追加它而不做任何更改
            if (i % 2 != 0) {
                tokens.add("'" + fragments[i] + "'");
            }
            // If it's not a string literal, it must be query characters (which need further processing)
            // 如果不是字符串字面值，则必须是查询字符(需要进一步处理)。
            else {
                // Tokenize the fragments into an array of strings
                // 将这些片段标记为字符串数组
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                // Then add these to the "result" array list (needs a bit of conversion)
                // 然后将这些添加到“结果”数组列表中(需要一些转换)
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
        // Finally, loop through the result array list, printing out each token a line at a time
        // 最后，循环遍历结果数组列表，每次输出一行标记
        return tokens;
    }

    private static String[] tokenise(String input) {
        // Add in some extra padding spaces around the "special characters"
        // so we can be sure that they are separated by AT LEAST one space (possibly more)
        // 在“特殊字符”周围添加一些额外的填充空间
        // 所以我们可以确定它们之间至少有一个空格(可能更多)
        for (String specialCharacter : SPECIAL_CHARACTERS) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        // Remove all double spaces (the previous replacements may had added some)
        // This is "blind" replacement - replacing if they exist, doing nothing if they don't
        // 删除所有双空格(之前的替换可能增加了一些)
        // 这是“盲”替换-如果它们存在就替换，如果它们不存在就不做任何事情
        while (input.contains("  ")) {
            input = input.replaceAll("  ", " ");
        }
        // Again, remove any whitespace from the beginning and end that might have been introduced
        input = input.trim();
        // Finally split on the space char (since there will now ALWAYS be a space between tokens)
        return input.split(" ");
    }

}
