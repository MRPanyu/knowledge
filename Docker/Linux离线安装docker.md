# Linux离线安装docker

参考: <https://www.cnblogs.com/sowler/p/18228732>

## 验证环境

VMWare虚拟机里装的Ubuntu 24（Ubuntu MATE发行版）

操作用户名是appuser，有sudo权限。

## 下载docker包

地址: <https://download.docker.com/linux/static/stable/x86_64/>

找到最新版本的包，如我用的是 `docker-28.4.0.tgz`

## 安装

下载下来的文件放到随意哪个目录下解压:

```sh
cd ~
tar -xf docker-28.4.0.tgz
```

解压出来会在当前目录下生成一个 `docker` 目录，要把里面的所有内容复制到 `/usr/bin` 下面

```sh
sudo cp docker/* /usr/bin
```

然后创建一个 `/etc/systemd/system/docker.service` 文件

```sh
sudo vi /etc/systemd/system/docker.service
```

内容如下

```ini
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target
  
[Service]
Type=notify
# the default is not to use systemd for cgroups because the delegate issues still
# exists and systemd currently does not support the cgroup feature set required
# for containers run by docker
ExecStart=/usr/bin/dockerd -H unix:///var/run/docker.sock --selinux-enabled=false --default-ulimit nofile=65536:65536
ExecReload=/bin/kill -s HUP $MAINPID
# Having non-zero Limit*s causes performance problems due to accounting overhead
# in the kernel. We recommend using cgroups to do container-local accounting.
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
# Uncomment TasksMax if your systemd version supports it.
# Only systemd 226 and above support this version.
#TasksMax=infinity
TimeoutStartSec=0
# set delegate yes so that systemd does not reset the cgroups of docker containers
Delegate=yes
# kill only the docker process, not all processes in the cgroup
KillMode=process
# restart the docker process if it exits prematurely
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s
  
[Install]
WantedBy=multi-user.target
```

添加文件可执行权限，重新加载 daemon 服务，然后启动 docker 服务

```sh
chmod +x /etc/systemd/system/docker.service
systemctl daemon-reload
systemctl start docker
```

## 配置镜像源

国内目前免费有效的镜像源很少，找到后类似这样配置，可以配置多个镜像源，docker会尝试从镜像源获取镜像，获取不到再从主站取。从主站取一般会失败。

```sh
sudo mkdir /etc/docker
sudo tee /etc/docker/daemon.json <<EOF
{
    "registry-mirrors": [
        "https://docker.1ms.run"
    ]
}
EOF
```

## 设置用户权限，避免每次操作docker都要加sudo

刚安装好后docker是需要sudo才能正常运行的（部分命令，如`docker --version`这种可以不用）

执行以下命令（注：第二句里的appuser是要进行操作的用户名）

```sh
sudo groupadd docker
sudo usermod -aG docker appuser
sudo service docker restart
```

然后用户需要Logout一下再重新进系统。

## 相关系统设置

重新加载配置文件

```sh
sudo systemctl daemon-reload
```

启动 docker 服务

```sh
sudo systemctl start docker
```

查看 docker 服务的运行状态

```sh
sudo systemctl status docker
```

停止运行

```sh
sudo systemctl stop docker
```

重新启动

```sh
sudo systemctl restart docker
```

将 docker 服务设置为开机自动启动

```sh
sudo systemctl enable docker
```

禁用开机自动启动

```sh
sudo systemctl disabled docker
```

查看docker开机自动启动状态 enabled:开启, disabled:关闭

```sh
sudo systemctl is-enabled docker.service
```

## 离线安装docker-compose

到下载发布页: <https://github.com/docker/compose/releases>

下载 `docker-compose-linux-x86_64` 这个文件（本身就是二进制执行文件）

复制成Linux系统中的 `/usr/local/bin/docker-compose` 文件

```sh
chmod +x /usr/local/bin/docker-compose
docker-compose --version
```

> 注: 这种独立运行的docker-compose命令一般称为v1，而在线安装docker的时候安装的compose插件一般称为v2，两者的命令使用上有点小区别，v1的命令是 `docker-compose up` ，对应v2是 `docker compose up`，中间少一个减号。

## 导入导出镜像（用于发布到内网服务器上）

首先在可以连接网络的服务器上面获取相关软件镜像，然后通过 save 和 load 命令导出和导入镜像。由于导入的镜像没有镜像名称和 tag 版本号，需要使用 docker tag 命令 修改导入的镜像命令。

docker导出镜像：

```sh
docker save 99ee9af2b6b1 > redis.tar # 99ee9af2b6b1 镜像ID
```

docker导入镜像：

```sh
docker load < redis.tar
```

docker修改镜像标签名称：

```sh
docker tag 99ee9af2b6b1 redis:3.2.0  #99ee9af2b6b1 镜像ID  镜像名称:版本号
```
