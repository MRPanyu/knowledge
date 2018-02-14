# 禁止Chrome强制跳转https

Chrome浏览器有称为 HSTS (Http Strict Transport Security) 的功能，对任何网站，进行过一次 https 的访问后，之后即使再次访问 http 的地址，浏览器也会自动跳转到 https。对开发人员来说，有时会造成问题。

在chrome的地址栏输入：

<chrome://net-internals/#hsts>

在打开的页面中， Delete domain 栏的输入框中输入要http访问的域名，然后点击“delete”按钮，即可完成配置。

然后你可以在 Query domain 栏中搜索刚才输入的域名，点击“query”按钮后如果提示“Not found”，那么就可以再次使用 http 协议进行访问了。
