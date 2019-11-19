package com.yjjqrqqq.autodeployjar;

/**
 * @author liuyixin
 * @create 2019/11/19
 */
public class StringUtils {
    public static boolean isBlank(String value) {
        return value == null || value.trim().equalsIgnoreCase("");
    }
}
