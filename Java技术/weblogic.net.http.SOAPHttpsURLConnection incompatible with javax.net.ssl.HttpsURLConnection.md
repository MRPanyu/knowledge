# weblogic.net.http.SOAPHttpsURLConnection incompatible with javax.net.ssl.HttpsURLConnection

发生weblogic.net.http.SOAPHttpsURLConnection incompatible with javax.net.ssl.HttpsURLConnection问题时，有两种方法可以处理：

1. 启动脚本增加-DUseSunHttpHandler=true参数
2. 创建URL时指定handler：
        URL url = new URL(null, strUrl, new sun.net.www.protocol.https.Handler());
        httpURLConnection = (HttpURLConnection) url.openConnection();


注：方法2中，如果修改不当可能导致出现这个异常：

    com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection    

这是由于上述代码片段中 Handler 类 import 错误导致的：

* 正确：`sun.net.www.protocol.https.Handler`
* 错误：`com.sun.net.ssl.internal.www.protocol.https.Handler`
