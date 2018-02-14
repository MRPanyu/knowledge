# Javascript中使用GBK编码进行encodeURI

Javascript中 `encodeURI` 和 `encodeURIComponent` 方法，均是以 UTF-8 字符集进行中文编码的。对部分老程序，HTTP层使用了GBK编码的情况下，可以用如下的方法进行 GBK 字符集的 encodeURI。

```javascript
function encodeURI_GBK(s) {
    var img = document.createElement("img");
    // escapeDBC 对多字节字符编码的函数
    function escapeDBC(s) {
        if (!s) return ""
        if (window.ActiveXObject) {
            // 如果是 ie, 使用 vbscript
            execScript('SetLocale "zh-cn"', 'vbscript');
            return s.replace(/[\d\D]/g, function($0) {
                window.vbsval = "";
                execScript('window.vbsval=Hex(Asc("' + $0 + '"))', "vbscript");
                return "%" + window.vbsval.slice(0,2) + "%" + window.vbsval.slice(-2);
            });
        }
        // 其它浏览器利用浏览器对请求地址自动编码的特性
        img.src = "nothing.action?separator=" + s;
        return img.src.split("?separator=").pop();
    }
    // 把 多字节字符 与 单字节字符 分开，分别使用 escapeDBC 和 encodeURIComponent 进行编码
    return s.replace(/([^\x00-\xff]+)|([\x00-\xff]+)/g, function($0, $1, $2) {
        return escapeDBC($1) + encodeURIComponent($2||'');
    });
}
```
