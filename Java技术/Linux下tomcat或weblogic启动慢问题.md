# Linux下tomcat或weblogic启动慢问题

Linux环境下，有时tomcat会启动非常慢，卡在
```
Deploying web application directory ......
```
这样一句输出语句下。

处理方案是：找到 jdk1.x.x_xx/jre/lib/security/java.security 文件，在文件中找到`securerandom.source`这个设置项，将其改为：
```
securerandom.source=file:/dev/./urandom
```

Linux或者部分Unix系统提供随机数设备是/dev/random 和/dev/urandom ，两个有区别，urandom安全性没有random高，但random需要时间间隔生成随机数。jdk默认调用random。
