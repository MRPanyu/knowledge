# tomcat启停脚本

## 启动脚本

```sh
# JVM Startup Options
CATALINA_OPTS=
CATALINA_OPTS="$CATALINA_OPTS -DsomeValue=someValue"

export CATALINA_OPTS

export CATALINA_HOME=/home/admin/apache-tomcat-8.5.20

LOG_FILE=/home/admin/logs/tomcat.log

nohup sh $CATALINA_HOME/bin/catalina.sh run >> $LOG_FILE 2>&1 &
tail -400f $LOG_FILE

```

## 停止脚本

注：先使用 kill 命令，并附带30秒检查，如果进程还存在则使用 kill -9 命令。

```sh
TOMCAT_NAME=apache-tomcat-8.5.20

echo Killing $TOMCAT_NAME ...
kill `ps -ef | grep java | grep $TOMCAT_NAME | awk '{print $2}'`

for (( c=1; c<=30; c++ ))
do
    sleep 1
    echo Checking tomcat status $c ...
    PID=`ps -ef | grep java | grep $TOMCAT_NAME | awk '{print $2}'`
    if [ "$PID"x == ""x ]
    then
        break
    fi
done

PID=`ps -ef | grep java | grep $TOMCAT_NAME | awk '{print $2}'`
if [ "$PID"x != ""x ]
then
    echo Force killing $TOMCAT_NAME ...
	kill -9 $PID
fi

echo Killed $TOMCAT_NAME
```

## 滚动日志的脚本

```sh
LOGFILE_DIR=/home/admin/logs
LOGFILE_NAME=tomcat.log

# if needed, remove tomcat native log files
# rm -f /home/admin/apache-tomcat-8.5.20/logs/*

ROLL_FILE_NAME=$LOGFILE_NAME.`date +%Y%m%d`

cp $LOGFILE_DIR/$LOGFILE_NAME $LOGFILE_DIR/$ROLL_FILE_NAME
echo '' > $LOGFILE_DIR/$LOGFILE_NAME
tar -zcf $LOGFILE_DIR/$ROLL_FILE_NAME.tar.gz -C $LOGFILE_DIR $ROLL_FILE_NAME
rm -f $LOGFILE_DIR/$ROLL_FILE_NAME
```
