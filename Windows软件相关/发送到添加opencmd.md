# 发送到添加opencmd

适用于权限受限不能随便装软件或者改注册表的Windows环境，用“发送到”菜单来添加一个opencmd的功能。

随便哪里新建一个opencmd.bat文件，内容如下：

```bat
set P=%~1
START /D "%P%\..\" cmd.exe
```

给opencmd.bat文件创建一个快捷方式，然后将快捷方式放到 "C:\Users\用户名\AppData\Roaming\Microsoft\Windows\SendTo" 目录下。

之后资源管理器里面目录中随便选择哪个文件或子目录，右键发送到 -> opencmd，就可以用命令行打开当前目录了。
