# RocketMQ集群搭建手册

本文档介绍如何在Linux服务器上搭建RocketMQ集群环境。

目前本文介绍的配置方案为二主二从同步方式集群（2m-2s-sync），能尽可能保证消息不丢失。

## 1. 环境准备

四台服务器，上面均安装好JDK8，并配置好JAVA_HOME和PATH环境变量。

>注：以下假设这四台服务器的IP分别是10.0.2.15-10.0.2.18，下面提到的服务器1为10.0.2.15，服务器2为10.0.2.16，以此类推。

将rocketmq程序包上传并解压到任意目录。下文中假设为 `/home/myuser/dev/rocketmq-all-4.7.1-bin-release` 目录中。

使用chmod命令配置bin目录下所有脚本执行权限：

```sh
cd /home/myuser/dev/rocketmq-all-4.7.1-bin-release/bin
chmod 744 *
```

## 2. 启动NameServer

在服务器1、服务器2上各启动两个NameServer，按默认配置，

```sh
cd /home/myuser/dev/rocketmq-all-4.7.1-bin-release
nohup sh bin/mqnamesrv &
tail -f ~/logs/rocketmqlogs/namesrv.log
```

如果正常启动，那两个NameServer的地址应该是

- 10.0.2.15:9876
- 10.0.2.16:9876

## 3. 启动Broker

分别在四个服务器上启动Broker

>**注意**: 因为中间一句nohup命令太长显示上会换行，实际是一句命令。-n后面的参数是两个NameServer地址，用分号隔开。另外四台服务器命令的区别主要是指定了不同的配置文件。

服务器1：

```sh
cd /home/myuser/dev/rocketmq-all-4.7.1-bin-release

nohup sh bin/mqbroker -c conf/2m-2s-sync/broker-a.properties -n "10.0.2.15:9876;10.0.2.16:9876" autoCreateTopicEnable=true &

tail -f ~/logs/rocketmqlogs/broker.log
```

服务器2：

```sh
cd /home/myuser/dev/rocketmq-all-4.7.1-bin-release

nohup sh bin/mqbroker -c conf/2m-2s-sync/broker-b.properties -n "10.0.2.15:9876;10.0.2.16:9876" autoCreateTopicEnable=true &

tail -f ~/logs/rocketmqlogs/broker.log
```

服务器3：

```sh
cd /home/myuser/dev/rocketmq-all-4.7.1-bin-release

nohup sh bin/mqbroker -c conf/2m-2s-sync/broker-a-s.properties -n "10.0.2.15:9876;10.0.2.16:9876" autoCreateTopicEnable=true &

tail -f ~/logs/rocketmqlogs/broker.log
```

服务器4：

```sh
cd /home/myuser/dev/rocketmq-all-4.7.1-bin-release

nohup sh bin/mqbroker -c conf/2m-2s-sync/broker-b-s.properties -n "10.0.2.15:9876;10.0.2.16:9876" autoCreateTopicEnable=true &

tail -f ~/logs/rocketmqlogs/broker.log
```

## 4. 验证整体环境

在随意哪台服务器上，通过mqadmin来验证集群情况：

```sh
cd /home/myuser/dev/rocketmq-all-4.7.1-bin-release
bin/mqadmin clusterlist -n "10.0.2.15:9876;10.0.2.16:9876"
```

如果集群正常启动成功，这个命令应该会正常显示四个节点的IP和端口信息。

## 5. Java程序中的使用

RocketMQ客户端的namesrvAddr参数本来就支持多个IP端口用分号分隔的格式，因此可以调整为连接多个NameServer。

```yaml
# 单节点环境
# rocketmq.namesrvAddr: 127.0.0.1:9876
# 集群环境
rocketmq.namesrvAddr: 10.0.2.15:9876;10.0.2.16:9876
```

## 6. 搭建RocketMQ-Console监控平台

另外可以搭建一个rocketmq-console程序，从Web页面上查看RocketMQ状态。

原始的下载地址是：<https://github.com/apache/rocketmq-externals.git>

可以通过git clone下来，但实际只需要其中的rocketmq-console目录。

目前碎管整体提供的程序包下的rocketmq-console.zip文件是这个工程的压缩包。

解压这个目录，工程本身是一个spring-boot工程，调整 `src/main/resources/application.properties` 文件（下面只包含了调整的项目，其他内容注意保持不变）：

```properties
# 程序端口设置为9999
server.port=9999
# RocketMQ NameServer地址、端口
rocketmq.config.namesrvAddr=10.0.2.15:9876;10.0.2.16:9876
```

之后可以正常通过 `mvn -DskipTests clean package` 打包并发布到任意服务器上。

发布完成后，在服务上通过 `java -jar rocketmq-console-ng-2.0.0.jar` 执行，之后就可以通过 <http://服务器IP:9999> 访问。
