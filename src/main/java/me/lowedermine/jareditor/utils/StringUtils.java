package me.lowedermine.jareditor.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static int indexOf(String str, char bs, char be, char ch) {
        int bracketCount = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == be) {
                bracketCount--;
            }
            if (c == ch && bracketCount == 0) {
                return i;
            }
            if (c == bs) {
                bracketCount++;
            }
        }
        return -1;
    }

    public static int lastIndexOf(String str, char bs, char be, char ch) {
        int bracketCount = 0;
        for (int i = str.length() - 1; i > 0; i--) {
            char c = str.charAt(i);
            if (c == bs) {
                bracketCount--;
            }
            if (c == ch && bracketCount == 0) {
                return i;
            }
            if (c == be) {
                bracketCount++;
            }
        }
        return -1;
    }

    public static String[] split(String str, char bs, char be, char de) {
        List<String> result = new ArrayList<>();
        int start = 0;
        int end;
        while ((end = indexOf(str.substring(start), bs, be, de)) != -1) {
            end += start;
            result.add(str.substring(start, end));
            start = end + 1;
        }
        result.add(str.substring(start));
        return result.toArray(new String[0]);
    }
}
