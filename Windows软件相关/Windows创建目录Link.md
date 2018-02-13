# Windows创建目录Link

注：Link与一般右键创建快捷方式是不一样的，有点类似于Linux系统中的软链接，虽然实际指向另一个目录，但程序能把其识别为一个正常的文件路径，快捷方式是达不到该效果的。

主要是使用 `mklink /J <新建链接位置> <原路径>` 命令（两个路径都需要是绝对路径），如下方是在Maven工程中很有用的一个脚本，可以将编译的class和依赖jar包链接到webapp目录的正确位置下，方便直接启动。

    SET BASE=%~dp0
    ECHO USING BASE=%BASE%
    mklink /J %BASE%\src\main\webapp\WEB-INF\classes %BASE%\target\classes
    mklink /J %BASE%\src\main\webapp\WEB-INF\lib %BASE%\target\dependency
