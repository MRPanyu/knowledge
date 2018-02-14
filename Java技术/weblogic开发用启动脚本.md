# weblogic开发用启动脚本

我常用的 weblogic 开发用启动脚本示例，脚本放置在 domains 目录下，每个脚本启动一个 domain：

```bat
title test_domain

REM 内存参数设置
SET USER_MEM_ARGS=-Xms256m -Xmx1024m -XX:MaxPermSize=1024m

REM CLASSPATH前额外增加的
SET EXT_PRE_CLASSPATH=E:\work\test\webapp\WEB-INF\lib\hibernate-jpa-2.0-api-1.0.1.Final.jar

REM 其他JVM参数，要先清空防止重复设置
SET JAVA_OPTIONS=
REM 启用调试
SET JAVA_OPTIONS=%JAVA_OPTIONS% -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4001,suspend=n
REM 启用远程JMX
SET JAVA_OPTIONS=%JAVA_OPTIONS% -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=17001 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false

test_domain\bin\startWeblogic.CMD
```
