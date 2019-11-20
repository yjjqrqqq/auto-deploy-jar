package com.yjjqrqqq.autodeployjar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
        args = new String[]{"packageJar=/home/liuyixin/tmp/cashme.worker.jar.2010", "port=7777", "fileDays=30", "runArg=-Dsonar=123", "jarName=cashme-worker.jar", "waitSeconds=60"};
        File packageJar = new File(getArg(args, "packageJar"));
        String jarName = getArg(args, "jarName");
        Integer days = Integer.parseInt(getArg(args, "fileDays"));
        clear(packageJar.getParentFile(), days);//1 ：清理历史文件

        generateShutdownFile(jarName, getArg(args, "port"));
        File targetJar = new File(jarName);
        FileUtils.deleteQuietly(targetJar);
        System.out.println(String.format("拷贝文件%s到当前目录%s", packageJar.getCanonicalPath(), jarName));
        FileUtils.copyFile(packageJar, targetJar);
        generateStartFile(jarName, getArg(args, "runArg"));
        generateCheckFile(jarName, getArg(args, "port"), getArg(args, "waitSeconds"));

    }

    private static void generateCheckFile(String jarName, String port, String waitSeconds) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isBlank(port)) {
            sb.append("command=\"netstat -lntp\"\n");
            sb.append(String.format("content=\":%s\"\n", port));
        } else {
            sb.append("command=\"ps -aux |grep -v 'color'\"\n");
            sb.append(String.format("content=\"%s\"\n", jarName));
        }
        sb.append(String.format("waitSeconds=%s\n", waitSeconds));
        String txt = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("check.sh"), "utf-8");
        sb.append(txt);
        FileUtils.write(new File("check.sh"), sb.toString(), "utf-8");
        CommandUtils.executeReturnString("chmod +x check.sh");
    }

    private static void generateStartFile(String jarName, String runArg) throws Exception {
        String command = String.format("./shutdown.sh\n" +
                "nohup java -jar %s %s > /dev/null & exit", jarName, StringUtils.isBlank(runArg) ? "" : runArg);
        FileUtils.write(new File("start.sh"), command, "utf-8");
        CommandUtils.executeReturnString("chmod +x start.sh");
    }

    private static void generateShutdownFile(String jarName, String port) throws Exception {
        String command = String.format("pid=$(ps -aux | grep '%s' |grep -v 'color' | awk '{print $2}');\n" +
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
                String value = arg.substring(index + 1).trim();
                value = value.replace("^['\"]", "");
                value = value.replace("['\"]$", "");
                return value;
            }
        }
        return null;
    }
}
