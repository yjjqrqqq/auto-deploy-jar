#思路就是command结果是否包含content内容
#command="netstat -lntp"
#content=":7777"
#等待时长，单位秒
#waitSeconds=60
success=false
i=1
while(( $i<=$waitSeconds ))
do
	echo "第${i}次尝试"
	cr=$(${command})
	match=$(expr match "$cr" ".*$content.*")
	if test $match -gt 0
		then
			echo "第${i}次检测成功"
			success=true
			break
	fi
	i=$[i+1]
	sleep 1s
done
if [ success==true ]
	then
		echo Start success
	else
		echo "##teamcity[buildProblem description='经过 $i 秒尝试后启动服务失败 ' identity='Start service failed']"
fi