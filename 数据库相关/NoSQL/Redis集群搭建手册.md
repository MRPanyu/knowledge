# Redis集群搭建手册

## 1. 概述

本文档介绍如何用多台服务器搭建Redis集群（Sentinel方式），并简单介绍Java Spring Boot程序中连接配置方法。

## 2. 环境准备

若干Linux服务器，需要安装过基本开发环境（gcc/make之类的包，用于编译Redis），或者已经通过发行版安装过Redis程序包。

>**注意**：按照Redis官方推荐的集群方式，高可用Redis集群比较合理的方式至少需要一主一丛两个RedisServer，以及至少三个RedisSentinel，其中RedisServer服务器承担主要负载，需要有足够内存/CPU等，而RedisSentinel是一个监控用的轻量程序，消耗内存和CPU不高，可以搭建在RedisServer的服务器上，也可以搭建在其他服务器上（比如借用搭APP的应用服务器）。搭RedisServer和RedisSentinel的服务器上都需要安装好Redis（Redis整体是一个软件包，包含了客户端，Server和Sentinel程序）。

>除了下面的源码安装以外，可能也可以通过Linux发行版的包管理（如yum或apt-get等）来安装，这里不详细介绍了。

### 2.1 源码方式安装

首先安装Redis，这里简单介绍默认的源码安装方式，以5.0.12版本为例（下面操作可能需要root或者足够授权的用户）：

>建议逐句执行以下命令

```sh
cd /usr/local
wget https://download.redis.io/releases/redis-5.0.12.tar.gz
tar -zxf redis-5.0.12.tar.gz
cd redis-5.0.12
make
make install
```

之后可以用 `redis-cli` 命令测试一下是否安装成功。如果能显示相关命令行则表示正常安装了。

## 3. 配置Redis主从服务

为方便说明，先假定有这么几台服务器，已经安装好Redis，安装目录均为 `/usr/local/redis-5.0.12`。

- IP(10.0.2.15): 用于搭建RedisServer主节点
- IP(10.0.2.16): 用于搭建RedisServer从节点
- IP(10.0.2.17-10.0.2.19): 用于搭建RedisSentinel

### 3.1 编写配置文件

#### 3.1.1 主节点配置文件

登录主节点服务器(10.0.2.15)。

首先备份 `/usr/local/redis-5.0.12/redis.conf` 文件后，修改为如下内容，如果是上传的话，注意UTF-8中文编码（或者把中文注释都去掉）和用Unix换行符。

```conf
# 绑定到所有网卡
bind 0.0.0.0
# 默认端口6379
port 6379
# 持久化设置，间隔时间与最少修改的条目数，就按这个默认配置
save 900 1
save 300 10
save 60 10000
# 持久化文件名
dbfilename dump.rdb
# 持久化目录，要先mkdir创建好
dir /usr/local/redis-5.0.12/data/
# 登录密码
requirepass mypassword
```

>**注意**: 持久化的目录要事先mkdir好，登录密码可以修改成复杂一点的，但注意调整密码的话从节点的配置文件也要对应调整。

#### 3.1.2 从节点配置文件

与主节点类似，登录后备份 `/usr/local/redis-5.0.12/redis.conf` ，之后修改内容如下：

```conf
# 绑定到所有网卡
bind 0.0.0.0
# 默认端口6379
port 6379
# 持久化设置，间隔时间与最少修改的条目数，就按这个默认配置
save 900 1
save 300 10
save 60 10000
# 持久化文件名
dbfilename dump.rdb
# 持久化目录，要先mkdir创建好
dir /usr/local/redis-5.0.12/data/
# 登录密码
requirepass mypassword

# 以下是从节点相关配置
# 主节点的IP及端口
replicaof 10.0.2.15 6379
# 主节点的登录密码
masterauth mypassword
```

>从节点的配置基本保持和主节点相同，只是额外增加从节点部分，从主节点复制数据。主节点的IP和密码根据需要调整。从节点的登录密码建议保持与主节点一致。

### 3.2 启动服务

先登录主节点服务器，执行命令：

```sh
redis-server /usr/local/redis-5.0.12/redis.conf
```

注意目前还是命令行实时启动的，Ctrl-C后会停止，先看下启动是否正常。正常的情况下应该最后一句会显示 `Ready to accept connections`。

如果启动正常，可以改用nohup方式启动，避免Ctrl-C停止进程：

```sh
nohup redis-server /usr/local/redis-5.0.12/redis.conf > /usr/local/redis-5.0.12/redis-server.log 2>&1 &
```

主节点启动完成后，再登录从节点服务器，执行和主节点相同的命令：

```sh
redis-server /usr/local/redis-5.0.12/redis.conf
```

因为从节点的配置文件中包含了从主节点复制的配置，启动后会有 `Connecting to MASTER 10.0.2.15:6379` 这样的信息。

如果从节点启动正常，也同样可以改用nohup启动（命令也与主节点相同）。

## 4. 配置RedisSentinel

### 4.1 编写配置文件

几个Sentinel的配置文件初始是相同的。备份后修改 `/usr/local/redis-5.0.12/sentinel.conf` 文件如下：

```conf
# sentinel自己的端口是26379
port 26379
# sentinel自己的密码，不过有些客户端不支持，这里不设置
# reqiurepass mypassword
# 指定主节点IP端口，从节点的会自动发现，最后一个2是投票数，保持默认的不要修改
sentinel monitor mymaster 10.0.2.15 6379 2
# 主节点的登录密码
sentinel auth-pass mymaster mypassword
# 节点多少毫秒无法接通认为是宕机
sentinel down-after-milliseconds mymaster 30000

# 切换主节点时允许并发切换的从节点数，保持默认1
sentinel parallel-syncs mymaster 1
# 重试最小间隔时间，保持默认三分钟
sentinel failover-timeout mymaster 180000
```

>配置文件中主节点的IP和密码按实际情况调整。不用配置从节点和其他Sentinel，会依赖主节点自动发现。

### 4.2 启动Sentinel

在Sentinel服务器上使用如下命令启动Sentinel：

```sh
redis-sentinel /usr/local/redis-5.0.12/sentinel.conf
```

启动成功的话，最后会显示出连接到主服务器的IP端口，从服务器的IP端口，以及其他启动的Sentinel服务的IP端口信息。

可以改成nohup命令来启动：

```sh
nohup redis-sentinel /usr/local/redis-5.0.12/sentinel.conf > /usr/local/redis-5.0.12/sentinel.log 2>&1 &
```

## 5. Spring-Boot程序中连接Sentinel方式Redis的配置

>注：对应的spring-boot 2.3版本，走默认依赖Lettuce客户端（Jedis客户端应该也同样配置，但不支持Sentinel认证）

```yaml
spring:
  # 所有redis配置在spring.redis之下，这里省略其他部分
  redis:
    # 注：连单节点的时候会有下面这两行配置，改成sentinel的时候要去掉
#   host: 127.0.0.1
#   port: 6379
    # 密码还是需要的，这个是主从服务器的密码
    password: mypassword
    # Sentinel的配置
    sentinel:
      # 对应sentinel.conf中那个集群名称mymaster
      master: mymaster
      # 多个sentinel的IP和端口，逗号隔开
      nodes: 10.0.2.15:26379,10.0.2.4:26379
      # 如果sentinel.conf里面有requirepass，则这个是sentinel的密码
#     password: mypassword
```

## 6. 其他几项官方建议的系统配置

注：以下这些命令调整Linux系统配置，非必须，但可以优化Redis集群稳定性。需要root执行：

```sh
sysctl -w net.core.somaxconn=1024
sysctl -w vm.overcommit_memory=1
```

另外在 `/etc/sysctl.conf` 文件中增加如下的配置行，避免上述调整在重启后失效：

```conf
net.core.somaxconn=1024
vm.overcommit_memory=1
```
