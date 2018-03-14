# Java程序宕机或缓慢分析指南

Java程序在生产环境中，有时会出现宕机或响应非常缓慢的情况。本篇主要针对这种情况下的基本问题分析方法进行一下介绍。

## 1. 常见的造成系统缓慢的可能原因

系统宕机或响应缓慢，一般都是由于某种系统资源不足导致的。

Java WEB程序中主要的系统资源是：

1. 内存
2. CPU
3. 连接池中的数据库连接
4. 中间件WEB请求线程池中的线程

而可能导致资源不足的原因常见有以下几种：

1. 数据库连接未关闭（泄露），导致连接池中连接耗尽。
2. 数据库SQL执行时间过长，用户反复操作，导致连接池连接耗尽，或WEB线程池中线程耗尽。
3. SQL提取数据过多，导致内存溢出，同时因为提取数据多就一定会慢，所以也会导致用户反复操作，和第二点类似。
4. 系统某个非数据库的常用操作耗时较长，用户反复操作，导致WEB线程耗尽。
5. 系统某个操作有同步锁，且耗时稍长，引起后面等待锁的线程堆积起来导致WEB线程耗尽。
6. 解析大文件等过程中导致内存溢出的。
7. 系统中逻辑不当造成死循环之类的，导致CPU居高不下，系统缓慢直至宕机。
8. JDK8以前的版本PermSize配置不够大，而加载的类过多（有时热部署机制容易导致此类问题）时导致Perm内存不够，系统宕机。

## 2. 系统缓慢或宕机原因分析概述

发生系统缓慢或宕机情况，要分析其原因，做重要的是首先收集系统信息。

有以下几点信息是需要收集的：

1. 明确一下当时的具体现象，是完全无法访问，还是系统很慢但还偶尔能用，是所有功能都慢，还是某几个功能尚可某些功能特别慢等。
2. 系统参数，如Java版本，启动参数中配置的内存配置（-Xms, -Xmx, -PermSize等参数）。
3. 系统当时的整体情况，如CPU消耗，内存消耗等。这些一般可以通过Linux命令，如top（查CPU及进程），free（查内存）命令等获取。
4. JVM的运行情况，一般如果生产环境有任何监控类的工具的话，就能提取到JVM本身的CPU消耗和内存消耗图表。
5. WEB中间件的一些监控信息，如WEB线程池中线程数，排队数，还有数据源连接池连接数，可用数，排队数等等。
6. 如果有DBA支持，提取一下当时数据库的连接信息，如各个客户端的连接数，总连接数，允许的总连接数限制，是否有突出的SQL执行问题（比如全表扫描）等。
7. 取几个threaddump文件，后面专题讲。
8. 如果感觉有可能是内存溢出问题，取heapdump文件，后面专题讲。
9. 系统日志，特别是报错信息等。

实际情况下不一定能全部收集到上述信息，当然尽可能使提前准备好发生问题时获取这些信息的脚本或方案之类的，有备无患。其中大部分信息是有时效性的，只能在缓慢或宕机当时尽快获取才能准确定位问题。

收集到信息后，按以下步骤逐步缩小问题分类：

1. 首先先看是否内存问题，这一般是根据JVM监控的内存曲线以及看日志中是否存在OutOfMemoryError。JVM在报出OutOfMemory前会努力GC，因此实际报错可能会比较晚才出现或不出现。JVM的内存曲线一般应该是上下波动的，即接近100%会进行GC，之后会下来，因此短时间内存接近满是正常现象。但如果JVM较长时间在97%以上没有波动不下来，或者后台日志确实出现了OutOfMemory错误，就基本可以认为是内存不足的问题了。
2. 如果不是内存问题，第二步可以继续通过监控和日志，判断是否数据库连接不足。主要关注的是数据源正在使用的连接数是否达到设置上限，等待连接的数量是否较多，日志中是否有获取连接超时之类的问题。另外从threaddump文件中如果大量线程等待在获取连接的方法上，也可以判断是数据库连接方面的问题。
3. 一般排除上面两类后多数就是WEB线程耗尽的问题了，根据WEB线程数的监控或者threaddump文件中线程数量分析比较容易确定。
4. 实际环境中出现单纯因为CPU过高导致系统响应慢的情况非常少见，但如果发现系统CPU长时间过高而且确实是相关java命令使用的，且其他几项资源都基本没问题的情况下，可以分类为CPU问题来分析。

分完类型后，再按各类型进行问题定位：

1. 内存问题，分析threaddump文件和heapdump文件。找到占内存最大块的业务对象来定位问题。
2. 数据库问题，分析threaddump文件，根据线程数量和当前占用连接的数量来判断连接是否泄漏，还是SQL执行缓慢一类的问题。如果是执行缓慢问题，还是通过threaddump文件分析多数线程在执行那块程序。连接泄露一般需要其他方式来分析了。
3. WEB线程耗尽的问题，一般通过threaddump文件定位多数线程在执行哪块程序。
4. CPU问题的话比较少见，一般也是通过threaddump文件再手工找可能有问题的线程。

下面先介绍一下比较重要的两个点：threaddump文件和heapdump文件的获取和分析。

## 3. threaddump文件的获取及分析

### 3.1 threaddump文件的获取

有两种常用的方法可以获取到threaddump文件：

注：下文中 `<pid>` 部分都是指java进程的pid，可以通过类似下面的命令获取：
```
ps -ef | grep java | grep <java命令中其他标志性短语，如serverName等>
```
Windows系统可以在任务管理器里面查看进程的PID列，具体方法不详细介绍了。

第一种是执行：
```
kill -3 <pid>
```
然后stack信息会输出到System.out里（有些版本的JVM可能会输出到System.err里）。有时不是很方便获取。
（Windows系统下因为没有kill命令，有一个方法可以模拟这种效果，就是在程序的那个命令行窗口里面按Ctrl-Break，就会看到类似输出了）

另一种是执行：
```
jstack -l <pid> > threaddump.log
```
`jstack` 命令在 `$JAVA_HOME/bin` 目录下面，和 `java` 、 `javac` 等命令一起。上面这句命令会把threaddump输出到当前目录下的threaddump.log文件中。命令中`-l`参数表示额外输出同步锁情况。

如果系统处于宕机状态，`jstack`命令可能无法输出内容，这时可以额外加一个`-F`参数强制输出

一般来说`jstack`命令更方便一些，建议优先选择这种方式。`kill -3`命令生成的不容易截取，但除了threaddump以外，还包含当时的JVM基本信息，如下面这段，有时会有用：
```
Heap
 PSYoungGen      total 325632K, used 250760K [0x000000076b400000, 0x0000000794d00000, 0x00000007c0000000)
  eden space 263680K, 95% used [0x000000076b400000,0x000000077a8e22e8,0x000000077b580000)
  from space 61952K, 0% used [0x000000077b580000,0x000000077b580000,0x000000077f200000)
  to   space 144896K, 0% used [0x000000078bf80000,0x000000078bf80000,0x0000000794d00000)
 ParOldGen       total 823296K, used 403365K [0x00000006c1c00000, 0x00000006f4000000, 0x000000076b400000)
  object space 823296K, 48% used [0x00000006c1c00000,0x00000006da5e96e0,0x00000006f4000000)
 Metaspace       used 40470K, capacity 40994K, committed 41472K, reserved 1085440K
  class space    used 4890K, capacity 5016K, committed 5120K, reserved 1048576K
```

另外IBM的JDK产生的javacore文件，实际也是一种threaddump文件，其用途和分析方法基本类似。

### 3.2 threaddump文件分析

#### 3.2.1 threaddump文件的结构

Threaddump文件是一个文本文件，直接用记事本软件就可以看。如果考虑用专门的工具的话，可以网上搜索这个软件：IBM Thread and Monitor Dump Analyze for Java。但软件本身只是把信息更结构化一下而已，有时候用记事本搜索之类的会更方便一下。下面只介绍下直接用记事本看的方式。

Threaddump文件结构其实很简单，头两行是一个时间戳和一个VM版本信息，之后一段一段的就是各个线程的执行堆栈，也就是系统中各个线程，当前这瞬间正在做什么，执行那段代码。

举个例子，下面这段堆栈信息（只截取前面一部分）：

```java
"http-nio-8080-exec-2" #29 daemon prio=5 os_prio=0 tid=0x000000002859f000 nid=0x9568 waiting on condition [0x000000002b29c000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at com.sinosoft.ins.productfactory.service.TestService.test(TestService.java:24)
	- locked <0x00000006c2c9f9f8> (a com.sinosoft.ins.productfactory.service.TestService)
	at com.sinosoft.ins.productfactory.controller.TestController.test(TestController.java:17)
	......
```

第一行是线程名称 "http-nio-8080-exec-2"，线程的数字编号 #29，是否精灵线程daemon，线程优先级prio，线程识别号tid等等，其他如nid和waiting on condition后面那个代码等都是操作系统级的标识符，对常规分析意义不大。

第二行是当前线程的状态，有以下几种，详细的可以看 java.lang.Thread.State 类的api或注释说明：

| 状态          | 说明
|---------------|--------------------
| RUNNABLE      | 正在运行中的
| WAITING       | 等待中，如执行到Object.wait()方法
| TIMED_WAITING | 有时限的等待中，如执行Thread.sleep(毫秒数)或Object.wait(毫秒数)方法
| BLOCKED       | 等待synchronized同步锁

从第三行开始就是具体堆栈，每行是一层方法调用的类名.方法名及行号信息等。其中有时还会有一些关于同步锁的信息，如上面例子的第五行，有一个
`- locked <0x00000006c2c9f9f8>` ，这表示这个线程已经获取并锁定了编号为 0x00000006c2c9f9f8 的同步锁。

如果是在等待同步锁的线程，大致是这样的堆栈信息：

```java
"http-nio-8080-exec-6" #33 daemon prio=5 os_prio=0 tid=0x00000000285a1800 nid=0x5668 waiting for monitor entry [0x000000002b69c000]
   java.lang.Thread.State: BLOCKED (on object monitor)
	at com.sinosoft.ins.productfactory.service.TestService.test(TestService.java:14)
	- waiting to lock <0x00000006c2c9f9f8> (a com.sinosoft.ins.productfactory.service.TestService)
	at com.sinosoft.ins.productfactory.controller.TestController.test(TestController.java:17)
    .......
```

这段的第四行有个 `- waiting to lock <0x00000006c2c9f9f8>` ，就表示这个线程在等待编号 0x00000006c2c9f9f8 的同步锁。

#### 3.2.2 分析技巧
