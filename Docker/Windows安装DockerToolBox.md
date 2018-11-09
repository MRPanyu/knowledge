# Windows安装DockerToolBox说明

Docker ToolBox适用于在Windows 7以上版本安装docker环境，相比Docker for Windows，不依赖于Hyper-V功能（Windows 10需要专业版以上才提供此功能），而是使用Oracle VirtualBox来虚拟。

性能上应该不适合生产用，可以做开发用途。

## 1.安装

执行DockerToolBox.exe安装包进行安装，开发环境的话KiteMatic可以不装。可以勾选自动安装Oracle VirtualBox。

默认docker的虚拟机会放在用户目录下的.docker目录，安装镜像多了以后虚拟机会很大，如果不想放在C盘，可以事先建目录链接，例如：

```
mklink /J C:\Users\用户名\.docker D:\docker\.docker
```

## 2.启动Docker Machine

安装后桌面上会有一个Docker Quickstart Terminal的快捷方式，实际是用GitBash执行安装目录下的startup.sh。

双击这个快捷方式，会先在VirtualBox中创建一个用于运行docker的虚拟机。默认会去github上下载最新版本的虚拟机镜像，如果网络不好的情况，可以按如下方式操作：

1. 随便找个本地http服务器，nginx，tomcat或者node的http-server等等，把docker安装目录下的boot2docker.iso发布倒服务器上（下面假设发布在了http://127.0.0.1:8080/boot2docker.iso）
2. 修改docker安装目录下的startup.sh，找到`"${DOCKER_MACHINE}" create -d virtualbox $PROXY_ENV "${VM}"`这么一行，增加一个参数，修改成`"${DOCKER_MACHINE}" create -d virtualbox --virtualbox-boot2docker-url=http://127.0.0.1:8080/boot2docker.iso $PROXY_ENV "${VM}"`。
3. 再启动桌面快捷方式或startup.sh命令行，就不会去github上取镜像了

稍等一段时间，打开的命令行窗口中出现了docker鲸鱼图样的时候，就表示default虚拟机创建/启动完成了。以后再次开这个命令行，如果default虚拟机已经启动着，则基本就直接进入命令行模式，如果未启动，则需要花一些实际先启动default虚拟机。

## 3.管理Docker Machine

在Docker命令行中（其实就是GitBash命令行），可以用docker-machine命令管理虚拟机

1. `docker-machine ls` - 打印虚拟机列表，默认应该只有一个default机器
2. `docker-machine stop default` - 关闭default机器，即停止VirtualBox中运行的那个虚拟机
3. `docker-machine start default` - 再次启动default虚拟机

**注意**：VirtualBox虚拟机默认会采用Host-Only模式的一种虚拟网络，主机要访问虚拟机，可以用虚拟机IP去访问，默认应该是192.168.99.100（在Docker命令行开始鲸鱼图下面会显示）。如果有其他虚拟机也在同一个虚拟网络中，应该也能够访问到。在Docker虚拟机中启动的Container暴露端口，实际还是暴露在VBox虚拟机里面，要用虚拟机的IP访问，如192.168.99.100:8080，而不是127.0.0.1:8080

## 4.使用docker

Docker虚拟机启动后，就可以在Docker命令行里面用docker命令例如，例如：

```
docker pull tomcat:8.5-jre8
docker run --rm -p 8080:8080 tomcat
docker ps
```

之后就可以通过http://192.168.99.100:8080看到tomcat发布首页了

类似的如Redis：

```
docker pull redis
docker run --rm -p 6379:6379 redis
```
