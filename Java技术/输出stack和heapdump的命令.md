# 输出stack和heapdump的命令

输出stack：

```
jstack <pid> > stacktrace.txt
```

输出heapdump：

```
jmap -dump:format=b,file=heapdump.hprof <pid>
```
