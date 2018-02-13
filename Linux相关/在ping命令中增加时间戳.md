# 在ping命令中增加时间戳

可以用awk命令，在ping命令每行前面输出一个时间戳。这也适用于其他类似形式持续输出的命令（如vmstat）

    ping 10 192.168.2.1 | awk '{ print strftime("[%H:%M:%S]",systime()) "\t" $0 } '
