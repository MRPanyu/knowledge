# SSH远程执行图形化程序

SSH 支持一种叫做 X11 Forwarding 的功能，使客户端能远程使用服务器上的图形化程序。

## 使用方法：

首先需要在客户端安装一个 X Window Server （注意 X Window 是一个专用词，和 Windows 没什么关系），如 Xming 或者 Cygwin 套件中的 XWin-server 。

之后要启动这个 X Windows Server 程序，它会在客户端本地打开监听接收图形相关的 SSH 指令。

之后再进行 SSH 连接的时候，要启用 X11 Forwarding 功能。

如果使用的是 ssh 命令，指定 -X 参数，如

    ssh -X [user@]hostname

如果使用的是 putty ，配置中 Connection/SSH/X11 下有一个 Enable X11 forwarding 的选项要勾选上。

之后直接在 ssh 命令行窗口启用任何图形化程序（例如 jconsole），客户端本地的 X Window Server 就会收到指令展示图形界面。
