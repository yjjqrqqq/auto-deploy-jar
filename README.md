# auto-deploy-jar
自动化部署jar包,与TeamCity或jekins集成使用

## 示例
> java -jar auto-deploy.jar packageJar=/package/test-2010-11-11.jar port=7777 fileDays=30 ruanArg=-Denv=online jarName=run.jar waitSeconds=60

运行上面命令后，会自动拷贝文件，清理历史文件，生成相关部署脚本

## 参数及含义
- packageJar jar包路径，强制要求这个路径是仓库地址。如/package/hello/test-2010-01-01.jar,认为/package/hello/是仓库地址，会根据fileDays来清楚仓库文件 ，防止文件过多
- port 端口，用于根据port检测服务是否成功 [可选]
- fileDays 仓库文件最大保存天数，与packageJar联合使用
- runArg 运行jar包时的额外参数,最终 run.sh 中的内容是： java -jar [jarName] [runArg]
- jarName 运行时jar包的名字，启动时会把packageJar拷贝到当前目录 ，并重命为jarName
- waitSeconds check.sh中等待服务启动成功的超时时间，单位为秒

## 最终生成相关部署文件目录及含义

```bash
├── /run.jar 与jarName一致，实际运行的jar
├── /run.sh 启动命令文件
├── /shutdown.sh 停止命令文件
├── /check.sh 检查服务是否启动， 等等成功的超时时间 waitSeconds秒。这里若启用失败会生成TeamCity 的失败Message. 
 
```
