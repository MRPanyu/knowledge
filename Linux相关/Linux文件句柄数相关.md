# Linux文件句柄数相关

## 系统级文件句柄数

查看系统级别文件句柄数：

    cat /proc/sys/fs/file-max

查看当前使用的文件句柄数：

    cat /proc/sys/fs/file-nr

修改系统级文件句柄数：

    vi /etc/sysctl.conf

修改 `fs.file-max = 1000000`

然后执行如下命令，使修改生效

    sysctl -p

## 用户级文件句柄数

查看用户级别文件句柄数：

    ulimit -n

修改用户级别文件句柄数：

    vi /etc/security/limits.conf

增加如下内容（\*部分可以是具体用户名，\*表示所有用户）：

    * soft nofile 65535
    * hard nofile 65535

重新登录后生效

## 查看进程相关信息

查看进程限制（PID为进程号）：

    cat /proc/<PID>/limits

查看进程当前打开的文件句柄（PID为进程号）：

    ls -l /proc/<PID>/fd
