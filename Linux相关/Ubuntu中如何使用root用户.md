# Ubuntu中如何使用root用户

Ubuntu系统安装时会要求创建一个用户，但没有类似RedHat那样要求输入root用户密码的地方。

刚安装好系统后，Ubuntu中的root用户是没有密码的，因此无法执行如`su root`之类的命令。

在命令行中的替代方案是：`sudo -i`

图形界面上，如果要用root用户权限来执行某个程序，可以创建如下命令的快捷方式：`pkexec someprogram`，其中someprogram表示要运行的程序