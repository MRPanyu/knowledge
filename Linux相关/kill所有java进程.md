# kill所有java进程

有时候需要用一个命令 kill 掉所有 java 进程或其他某种进程，命令如下：

    ps -ef | grep java | grep -v grep | awk '{print $2}' | xarg kill -9

或者

    kill -9 `ps -ef | grep java | grep -v grep | awk '{print $2}'`

将 grep java 替换成其他的 grep ，或者串接多个 grep 的方式，可以灵活地实现如杀掉所有 tomcat 进程，或者杀掉命令行中带有某特定标志的进程等功能。

另外还有 pkill 命令，可以直接根据正则表达式杀进程

    pkill -9 -f /tomcat
