#!/bin/bash
APP_NAME="validation"
BIN_PATH=$(cd `dirname $0`; pwd)
# 进入bin目录
cd `dirname $0`
# 返回到上一级项目根目录路径
cd ..
# 打印项目根目录绝对路径
# `pwd` 执行系统命令并获得结果
BASE_PATH=`pwd`
JAVA_OPT="-server -Xms1024m -Xmx1024m"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
APPLICATION_JAR=$(ls $BASE_PATH/boot/$APP_NAME*.jar)

# 使用说明，用来提示输入参数
usage() {
    echo "Usage: sh server.sh [start|stop|restart|status]"
    exit 1
}

# 检查程序是否在运行
is_exist() {
    # 根据关键字过滤进程PID，关键字由业务方自定义
    pid=$(ps -ef | grep $BASE_PATH/boot/$APP_NAME | grep -v runuser | grep -v grep | awk '{print $2}')
    echo $pid
    if [ -z "${pid}" ]; then
        return 1
    else
        return 0
    fi
}

# 启动程序
start() {
    # 启动程序时，可酌情根据各自服务启动条件做出相应的调整
    is_exist
    if [ $? -eq "0" ]; then
        echo "${APP_NAME} is already running. pid=${pid} ."
    else
        # nohup /bin/java -jar ${APP_NAME}  >$LOG_PATH 2>&1 &
        if [ -e "/opt/soft/jdk1.8.0_211/bin/java" ]; then
          echo "指定jdk路径"
          nohup /opt/soft/jdk1.8.0_211/bin/java ${JAVA_OPT} -jar ${APPLICATION_JAR}  -t auto  > /dev/null 2>&1 &
        else
          echo "默认jdk路径"
          nohup java ${JAVA_OPT} -jar ${APPLICATION_JAR}  -t auto  > /dev/null 2>&1 &
		    fi
        echo "${APP_NAME} start success"
    fi
}

# 停止程序
stop() {
    is_exist
    if [ $? -eq "0" ]; then
        kill -9 $pid
    else
        echo "${APP_NAME} is not running"
    fi
}

# 程序状态
status() {
    is_exist
    if [ $? -eq "0" ]; then
        echo "${APP_NAME} is running. Pid is ${pid}"
    else
        echo "${APP_NAME} is NOT running."
    fi
}

# 重启程序
restart() {
    stop
    start
}

# 主方法入口，接收参数可支持start\stop\status\restart\
if [ `whoami` != "lbs" ];then
  echo "user name is not lbs"
  exit -1;
fi
case "$1" in
"start")
    start
    ;;
"stop")
    stop
    ;;
"status")
    status
    ;;
"restart")
    restart
    ;;
*)
    usage
    ;;
esac
