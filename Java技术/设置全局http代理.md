# 设置全局http代理

Java程序中，可以通过启动参数设置全局http代理

    -Dhttp.proxyHost=123.45.67.89
    -Dhttp.proxyPort=8080
    -Dhttp.nonProxyHosts=localhost|127.0.0.1

另外，如果只是需要在某个特定http连接上使用代理，代码为：

```java
java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress("123.45.67.89", 8080));
java.net.URL url = new java.net.URL("http://www.baidu.com");
url.openConnection(proxy);
```
