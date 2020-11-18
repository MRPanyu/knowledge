# WSL2 安装及操作

## 1. 安装

首先需要更新windows10到2004版本以上（2020年5月版）。

BIOS中需要开启虚拟化选项（Virtualization Technology, VTx）。

C盘保证一定的空间（5GB以上）。

在PowerShell中运行如下命令：

```cmd
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
```

之后重启系统，然后在PowerShell中运行如下命令，设置默认使用wsl2：

```cmd
wsl --set-default-version 2
```

然后开始菜单中打开“Microsoft Store”程序，搜索Ubuntu。不带后缀的那个默认是Ubuntu最新版本，或者也可以找其他LTS版本的。

点进去选择下载安装，完成后执行，会提示提供一个初始的用户名密码，设置后完成。

之后可以用命令行 `wsl` 直接进入Ubuntu子系统。

## 2. 迁移安装位置

刚安装后的Ubuntu系统在C盘，如果想要将其移动到其他盘中，可以进行如下操作：

查看安装的Linux子系统名称：

```cmd
wsl --list -v
```

如果是按之前的安装方式，名称应该是Ubuntu。

然后导出当前镜像成为一个备份tar包：

```cmd
mkdir E:\wsl
wsl --export Ubuntu E:\wsl\ubuntu.tar
```

然后卸载Ubuntu镜像

```cmd
wsl --unregister Ubuntu
wsl --list -v
```

然后再从镜像恢复到其他盘上

```cmd
mkdir E:\wsl\ubuntu
wsl --import Ubuntu E:\wsl\ubuntu\ E:\wsl\ubuntu.tar
wsl --list -v
```

恢复后需要重新设置一下用户（否则会变成root用户）

```cmd
ubuntu config --default-user <yourname>
```

如果成功，之前备份的tar包（E:\wsl\ubuntu.tar）可以删除。

## 3. 其他命令

查看安装的Linux子系统列表：

```cmd
wsl --list -v
```

设置默认Linux子系统（为Ubuntu，可以改成其他已安装的）：

```cmd
wsl --setdefault Ubuntu
```

设置系统使用的wsl版本：

```cmd
wsl --set-version Ubuntu 1
wsl --set-version Ubuntu 2
```

>注：前面一句设置执行版本为wsl1，后面一句设置为wsl2。两者的区别主要是wsl1不支持完整的Linux功能，但跨系统文件访问读写性能（如Linux中访问Windows文件）略好一些。<https://docs.microsoft.com/en-us/windows/wsl/compare-versions>

直接从Windows命令行中执行Linux命令：

```cmd
wsl <command>
```

例如：

```cmd
wsl ls -al
```

关闭、重启WSL子系统（有时/mnt/c会丢失，重启一下会好）

```cmd
wsl --shutdown
wsl
```


## 4. 文件系统相互访问

WSL中的子系统目录在Windows中虚拟成了一个网络位置。可以通过“运行”窗口中访问 `\\wsl$` 来找到对应文件。如 `\\wsl$\Ubuntu` 对应Ubuntu子系统的根目录。

在Linux子系统中，可以从 `/mnt/c` ， `/mnt/d` 等目录访问Windows系统的C盘、D盘等等。

## 5. Docker Desktop安装

安装过WSL2的 Windows 10 Home 版也可以直接安装 Docker Desktop 了。安装后设置项中会显示使用WSL2后端（Home版中默认选中也不可改）。实际Docker Desktop会新建两个Linux子系统叫`docker-desktop`和`docker-desktop-data`，这两个子系统也可以用上述描述的方法来迁移安装目录，保证足够磁盘空间来运行容器。
