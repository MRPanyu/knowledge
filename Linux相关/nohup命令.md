# nohup命令

Linux系统中，命令有以下几种运行方式：

下面例子中`command`表示任何命令，可包含参数。

前台运行：

    command

后台运行：

    command &

后台运行模式下，你可以继续使用控制台输入其他命令，命令会继续执行直到完成。

一般情况下会将后台运行的命令输出导入到其他文件中，避免影响后续命令输入，如：

    command > output.log 2>&1 &

上述命令 `> output.log` 表示将 sysout 输出到 output.log 文件，`2>&1` 表示将 syserr 输出到 sysout （即也最终输出到output.log文件），然后最后一个 `&` 表示后台运行。

后台运行的程序，在用户登出的时候会自动关闭。要在用户登出后还让命令能继续执行，需要使用 `nohup` 命令：

    nohup command > output.log 2>&1 &

在前面加了 nohup ，其效果在用户登出前和之前的后台运行命令挺相似，在用户登出后命令也会继续执行。

如果没有指定输出文件，则 nohup 命令会自动生成一个 nohup.out 文件。
