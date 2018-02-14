# npm和yarn设置使用国内镜像站点


## npm

可以下载cnpm命令：

    npm install cnpm -g --registry=https://registry.npm.taobao.org

之后可以改用cnpm进行install等操作，cnpm命令与npm用法基本相同，例如：

    cnpm install

## yarn

yarn可以直接设置为使用国内的镜像站点：

    yarn config set registry https://registry.npm.taobao.org
