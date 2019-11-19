package com.yjjqrqqq.autodeployjar;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * @author liuyixin
 * @create 2019/11/19
 */
public class CommandUtils {
    public static String executeReturnString(String command) throws Exception {
        System.out.println(command);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        InputStream is = process.getInputStream();
        try {
            String result = IOUtils.toString(is, "utf-8");
            System.out.println(result);
            return result;
        } finally {
            is.close();
        }
    }
}
