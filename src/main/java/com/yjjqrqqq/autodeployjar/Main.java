package com.yjjqrqqq.autodeployjar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author liuyixin
 * @create 2019/11/19
 */
public class Main {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        //packageJar=jar路径 port=端口号 fileDays=30 jarName= runArg=
        File packageJar = new File(getArg(args, "packageJar"));
        String jarName = getArg(args, "jarName");
        Integer days = Integer.parseInt(getArg(args, "fileDays"));
        clear(packageJar.getParentFile(), days);//1 ：清理历史文件

        generateShutdownFile(jarName, getArg(args, "port"));
        File targetJar = new File(jarName);
        FileUtils.deleteQuietly(targetJar);
        System.out.println(String.format("拷贝文件%s到当前目录%s", packageJar.getCanonicalPath(), jarName));
        FileUtils.copyFile(packageJar, targetJar);

    }

    private static void generateStartFile() {

    }

    private static void generateShutdownFile(String jarName, String port) throws Exception {
        String command = String.format("pid=$(ps -aux | grep '%' |grep -v 'color' | awk '{print $2}');\n" +
                        "echo $pid\n" +
                        "kill -9 $pid\n",
                jarName
        );
        if (!StringUtils.isBlank(port)) {
            command = String.format("port=%s\n" +
                    "pid=$(netstat -nlp | grep :$port | awk '{print $7}' | awk -F\"/\" '{ print $1 }');\n" +
                    "echo $pid\n" +
                    "if [ \"$pid\" -gt \"0\" ]; then\n" +
                    "\tkill -9 $pid\n" +
                    "fi", port);
        }
        FileUtils.write(new File("shutdown.sh"), command, "utf-8");
        CommandUtils.executeReturnString("chmod +x shutdown.sh");
        CommandUtils.executeReturnString("sh shutdown.sh");
    }

    private static void clear(File dir, int days) throws IOException {
        for (File file : dir.listFiles()) {
            if (!file.getName().toLowerCase().trim().endsWith(".jar")) {
                continue;
            }
            int interval = (int) ((System.currentTimeMillis() - file.lastModified()) / (1000 * 3600 * 24));
            if (interval > days) {
                System.out.println(String.format("超过%d天，删除文件%s", days, file.getCanonicalPath()));
                FileUtils.deleteQuietly(file);
            }
        }
    }

    private static String getArg(String[] args, String key) {
        for (String arg : args) {
            int index = arg.indexOf('=');
            if (index < 0) {
                continue;
            }
            String first = arg.substring(0, index);
            if (key.equalsIgnoreCase(first.trim())) {
                String value = arg.substring(index + 1);
                value.trim();
            }
        }
        return null;
    }
}
