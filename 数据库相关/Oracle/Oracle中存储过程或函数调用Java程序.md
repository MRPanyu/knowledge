# Oracle中存储过程或函数调用Java程序

在Oracle中可以以存储过程或函数方式调用Java程序：
 
以下方法在Oracle 11g（查阅文档是10g的，因此应该10g也可以通用，可能有一定区别）本机实验通过：
 
1. 创建Java程序：
	1. 可以在Eclipse中创建Java程序，编译级别需要选择1.5或以下。
	2. 将需要让Oracle调用的部分包装成某个类中的一个静态方法（如附件中com.sinosoft.ocj.DemoInsert.doInsertData方法）。
	3. 将java源码或class文件打成一个jar包，源码可以直接让oracle编译，如果用class文件则注意用1.5级别编译。
2. 导入Jar：
	1. 在cmd中执行命令：`loadjava -user panyu/panyu@orcl -v -r demo.jar`
	2. 以上命令中loadjava实际在oracle的bin目录下（安装oracle后一般bin目录会加入PATH环境变量，因此可以在cmd中直接调用）。-user后面的参数用于指定加载到的用户/密码和连接。demo.jar则是已打好的jar包（这里是相对路径）
3. 制作存储过程或函数：
	1. 用PLSQL等登陆加载了该Jar的用户，执行如下命令制作存储过程：`create or replace procedure demo_insert as language java name 'com.sinosoft.ocj.DemoInsert.doInsertData()';`
	2. 函数的制作方法类似：`CREATE OR REPLACE FUNCTION des_encrypt(key varchar2, data varchar2) RETURN VARCHAR2 as LANGUAGE JAVA NAME 'com.kingdee.eas.custom.cpic.bill.DES.des_encrypt(java.lang.String, java.lang.String) return java.lang.String';`
4. 设置执行权限：
	1. 执行过程中可能遇到Java执行权限不足java.security.AccessControlException ，会提示使用dbms_java.grant_permission来指定执行权限。该操作需要SYS用户执行。

参考：
<http://docs.oracle.com/cd/B19306_01/java.102/b14187/chthree.htm#CACFBAEG>
<http://docs.oracle.com/cd/B19306_01/java.102/b14187/cheleven.htm#CACFHDJE>
<http://docs.oracle.com/cd/B19306_01/java.102/b14187/chnine.htm#BABHDBCJ>
