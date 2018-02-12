# 获取Windows目录的短名称

首先创建一个bat脚本，起名为shortname.bat，内容如下：

    @ECHO OFF
    echo %~s1

然后在命令行中运行如下命令：

    shortname.bat "C:\Program Files\Java\jdk1.6.0_45"

会显示出该目录的短名称：`C:\PROGRA~1\Java\JDK16~1.0_4`
