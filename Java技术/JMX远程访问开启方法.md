# JMX远程访问开启方法

在Java VM参数中增加如下这些参数：
 
    -Dcom.sun.management.jmxremote=true
    -Dcom.sun.management.jmxremote.port=17001
    -Dcom.sun.management.jmxremote.ssl=false
    -Dcom.sun.management.jmxremote.authenticate=false
 
即可开启非ssl连接，无需用户认证的JMX远程服务。

另外在 Linux 环境中，JMX远程访问时，即使设置的访问地址是 IP 形式，最终还是会根据 hostname 去DNS重新获取一下 IP，当DNS中服务器名映射有问题的时候可能会出现访问不通的情况。

检测及解决方案：

首先在作为JMX**服务端**程序所在机器上，检查hostname对应的IP是否正确：

    hostname -i

如果该命令返回了错误的IP，或者 127.0.0.1 这样的IP，都会造成问题。

这时，需要修改这台机器上的 /etc/hosts 文件，增加本机IP对应本机hostname的映射关系，之后远程就可以正常访问了。
