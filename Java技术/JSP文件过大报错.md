# JSP文件过大报错

JDK 1.6版本运行Java WEB服务器（这里以weblogic为例，应该其他服务器也会有类似情况），如果有JSP文件过大，会导致在运行时无法编译通过：

注：这里的JSP文件过大，主要是编译成class中单个方法代码量过大导致的问题，因此不一定仅仅是单个JSP文件过大，也包含了 `<%@include file="" %>` 方式导入的其他JSP内容的总量。

当开启了 -Xdebug 参数时，JSP文件达到一定量时会出现如下异常：

    java.lang.InternalError: name is too long to represent

这时如果去掉 -Xdebug 参数，因为非调试模式下编译结果会小一点，可能程序还能继续正常运行。

当然再达到一定量时，非调试模式也不能运行了，会出现如下异常：

    weblogic.servlet.jsp.CompilationException: Failed to compile JSP /testbig.jsp 
    testbig.jsp:2:1: The code of method _jspService(HttpServletRequest, HttpServletResponse) is exceeding the 65535 bytes limit

这时就只能削减JSP代码量来修正问题了。

以下是关于JSP中各种类型的代码对整体代码量的影响：

1. `@include`方式导入的子文件是当作直接源码插入的，会增大方法体内容大致等于子文件本身内容。
2. `jsp:include`方式导入的子文件是类似方法调用的，整体代码量增加很少。
3. `<% %>`里面的源码是作为Java代码编译的，削减的话能减少代码量（如提取到其他方法等）。
4. `<% %>`里面的 `//` 或 `/* */` 注释因为不编译，基本上对代码量没什么影响。
5. `<% %>`外面的内容，包括普通的html，`<!-- -->`这种xml注释，或者`<%-- --%>`这种JSP注释，对代码量都有一定影响。
