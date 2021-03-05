# luit命令解决远程ssh登录中文编码问题

注：luit是linux中的命令，因此本文档适用的场景是从一个linux系统中使用ssh命令登录到另一个编码不同的linux系统的情况。

## 场景

客户端机器：linux系统，使用UTF-8编码

服务端机器：linux系统，使用GBK编码

直接在客户端机器通过ssh命令远程登录服务端机器时，会出现乱码。

```sh
ssh appuser@remotehost
```

## 安装luit命令

查看客户端机器上是否安装过luit命令：

```sh
luit -V
```

如果显示为2.0版本，则可以使用。注意可能很多linux发行版里面默认还是1.1.1版本，处理GBK编码时会报错*Segmentation fault*。

如果没用安装或者需要升级版本，则在客户端机器上安装新版本的luit：

```sh
# 安装编译工具，如gcc等，如果已经安装过了可以跳过这步
sudo apt-get install -y wget build-essential
# 下载最新版本
wget http://invisible-island.net/datafiles/release/luit.tar.gz
tar -xvf luit.tar.gz
# 注意目录根据版本不同可能会变
cd luit-20210218; ./configure; make; sudo make install
```

正常安装后，重新执行 `luit -V` 命令，应该能显示2.0版本。

## 使用luit命令

重新使用luit命令串接ssh远程登录：

```sh
luit -encoding gbk ssh appuser@remotehost
```

登录后再查看，可以看到中文已经能正常显示了。
