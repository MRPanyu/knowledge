# Eclipse编译报错相关

## The package javax.xml.datatype is accessible from more than one module: <unnamed>, java.xml

由于java9以后模块化，传统的java程序可能在某些引入的jar包中包含一些`java.xml`等下面的类，属于`<unnamed>`模块，而JDK中这些类属于`java.xml`模块，冲突导致报错。

比较正统的解决方案是调整相关的jar包依赖，例如在maven pom.xml中把`xml-apis:xml-apis`这个依赖给exclusion排除掉。有些时候会存在比如axis-jaxrpc这种很老的组件里部分是`java.xml`的代码部分是自己代码的情况，则比较麻烦，需要自己重新打一个自定义版本，把组件jar包里`java.xml`目录给手工删掉避免冲突。

但由于OpenJDK对这块有一套不太标准化的说明是在这种情况下有模块的优先，因此实际这个错误只会由Eclipse报错，其他情况（如Maven编译等）并不会报错。

因此Eclipse实际还是留了一个开关可以忽略这类错误的。方法是导入工程后，自己手工编辑工程的`.settings/org.eclipse.jdt.core.prefs`文件，增加一行

```
org.eclipse.jdt.core.compiler.ignoreUnnamedModuleForSplitPackage=enabled
```

重新进入Eclipse中，针对这个工程的此类报错就不再报了。

参考: <https://help.eclipse.org/latest/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/JavaCore.html#COMPILER_IGNORE_UNNAMED_MODULE_FOR_SPLIT_PACKAGE>
