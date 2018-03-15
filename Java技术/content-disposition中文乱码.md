# content-disposition中文乱码问题

Java程序中可以通过指定content-disposition的HTTP头来指定文件下载的名称，有时会出现中文乱码。这时可以通过`filename*`指定编码，一般现代浏览器如Chrome等会优先`filename*`指定的文件名。

```java
response.addHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(fname, "UTF-8") + ";filename*=UTF-8''" + URLEncoder.encode(fname, "UTF-8"));
```
