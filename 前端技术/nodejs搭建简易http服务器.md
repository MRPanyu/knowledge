# nodejs搭建简易http服务器

有时会遇到这样的情况，从网上下载了某个前端程序包（比如某些JS程序的示例等），但需要发布到 http 服务器上才能正常查看。如果使用 apache 或者 nginx 等来发布，需要将程序复制到指定目录下，或者可能需要修改配置文件，不够快捷方便。nodejs有相关的三方工具，可以非常快速地启动一个小型 http 资源服务器，一个命令发布任意目录为 http 服务。

首先通过 npm 安装全局命令 http-server

    npm install http-server -g

之后，只要在任意目录下，执行 http-server 命令，即可将该目录作为网站根目录，发布成本地 http 服务。

    cd E:\path_to_html_root
    http-server -p 8080

http-server 命令的参数：

* -help：显示帮助信息
* -p：设置启动服务的端口，默认是8080
* -o：如果有该参数，会自动打开系统默认的浏览器访问启动的站点
