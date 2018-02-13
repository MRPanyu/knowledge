# Windows开启关闭睡眠功能

以管理员身份运行cmd命令行：

关闭睡眠：

    powercfg -h off

开启睡眠：

    powercfg -h on

关闭睡眠后，C盘上会空出几个G大小的空间（内存越大这个临时文件越大）
