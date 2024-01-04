# Nginx去除Server响应头方案

Nginx服务器，默认会在HTTP响应中增加 `Server: nginx/1.24` 这样的响应头来标识服务器版本，部分安全检测会认为暴露了服务器中间件类型与版本信息，有安全风险。

## 1. 去除版本信息

修改 *nginx.conf* 配置文件，在 `http` 节点增加一行 `server_tokens off;` ，即可屏蔽掉版本号输出。

```conf
http {
   ...
   server_tokens off;
   ...
}
```

这时输出的HTTP响应头变为：`Server: nginx`

## 2. 完整去除Server响应头

标准版 Nginx 不支持完整去除这个 Server 响应头，需要通过额外加装插件来实现该功能。

本文档中以源码编译安装的方式介绍，各Linux发行版可能有其他便捷的安装方式。

### 2.1 下载

首先下载 nginx 源码：<https://nginx.org/en/download.html>

这里示例的是下载到了 *nginx-1.24.0.tar.gz* 文件。

然后下载 headers-more-nginx-module 组件的源码：<https://github.com/openresty/headers-more-nginx-module/tags>

这里示例是下载到了 *headers-more-nginx-module-0.37.tar.gz* 文件。

### 2.2 安装

以下假设Linux系统用户为 *appuser* ，用户目录为 `/home/appuser` ，nginx希望安装到 `/home/appuser/nginx` 目录。

创建 `/home/appuser/nginx_install` 目录：

```sh
cd /home/appuser
mkdir nginx_install
```

之后将下载的两个 tar.gz 包上传到这个目录下，之后解压：

```sh
cd /home/appuser/nginx_install
tar -xf nginx-1.24.0.tar.gz
tar -xf headers-more-nginx-module-0.37.tar.gz
ls -al
```

这时，应该在 `/home/appuser/nginx_install` 下有这两个目录：

- *nginx-1.24.0*: nginx安装源码的目录
- *headers-more-nginx-module-0.37*: headers-more插件的目录

之后进入 nginx 安装目录，进行安装：

```sh
cd /home/appuser/nginx_install/nginx-1.24.0
./configure --prefix=/home/appuser/nginx --add-module=/home/appuser/nginx_install/headers-more-nginx-module-0.37
make
make install
```

> 注：运行 `./configure` 时可能会遇到各种缺lib等问题（如pcre, zlib等等），各种Linux Distribution的安装方式不同。这里假设服务器是之前曾经安装过标准版nginx，这些lib都已经安装成功的情况

### 2.3 修改nginx.conf配置文件

修改 *nginx.conf* 配置文件，在 http 节点（server/location节点也支持，但对于去掉 `Server` 响应头的目标来说全局的 http 节点较合适），增加 `more_clear_headers Server;`，如下：

```conf
http {
   ...
   more_clear_headers Server;
   ...
}
```

之后重启 nginx 或 reload 使配置生效。用浏览器访问，可以看到之前会返回的 `Server: nginx` 响应头已经不返回了。
