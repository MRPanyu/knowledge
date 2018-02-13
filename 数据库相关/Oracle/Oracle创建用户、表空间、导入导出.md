# Oracle创建用户、表空间、导入导出

注：Oracle12新增了pluggable datasource的概念，开启数据库服务后需要执行

    alter pluggable database pdborcl open;

相关的JDBC URL格式：`jdbc:oracle:thin:@//127.0.0.1:1521/pdborcl`

## Oracle 12
```sql
alter pluggable database pdborcl open;
alter session set container=PDBORCL;
create temporary tablespace gateway_temp 
tempfile 'E:\app\oracle\oradata\orcl\gateway_temp01.dbf' 
size 32m 
autoextend on 
next 32m maxsize 2048m
extent management local;
create tablespace gateway_data
logging
datafile 'E:\app\oracle\oradata\orcl\gateway_data01.dbf' 
size 32m 
autoextend on 
next 32m maxsize 2048m
extent management local;
create user gateway identified by gateway
default tablespace gateway_data
temporary tablespace gateway_temp;
grant connect,resource,create view to gateway; 
```

## Oracle 10/11

```sql
--创建临时表空间
create temporary tablespace test_temp 
tempfile 'E:\oracle\product\10.2.0\oradata\testserver\test_temp01.dbf' 
size 32m 
autoextend on 
next 32m maxsize 2048m
extent management local;
--创建数据表空间
create tablespace test_data
logging
datafile 'E:\oracle\product\10.2.0\oradata\testserver\test_data01.dbf' 
size 32m 
autoextend on 
next 32m maxsize 2048m
extent management local;
--创建用户并指定表空间
create user testserver_user identified by testserver_user
default tablespace test_data
temporary tablespace test_temp;
--给用户授予权限
grant connect,resource to testserver_user;
```

## 导入导出命令：

Oracle数据导入导出imp/exp就相当于oracle数据还原与备份。exp命令可以把数据从远程数据库服务器导出到本地的dmp文件， imp命令可以把dmp文件从本地导入到远处的数据库服务器中。 利用这个功能可以构建两个相同的数据库，一个用来测试，一个用来正式使用。

执行环境：可以在SQLPLUS.EXE或者DOS（命令行）中执行，
DOS中可以执行时由于 在oracle 8i 中 安装目录ora81BIN被设置为全局路径，
该目录下有EXP.EXE与IMP.EXE文件被用来执行导入导出。

oracle用java编写，SQLPLUS.EXE、EXP.EXE、IMP.EXE这两个文件有可能是被包装后的类文件。

SQLPLUS.EXE调用EXP.EXE、IMP.EXE所包裹的类，完成导入导出功能。

下面介绍的是导入导出的实例。

### 数据导出：

1. 将数据库TEST完全导出,用户名system 密码manager 导出到D:\daochu.dmp中

        exp system/manager@TEST file=d:\daochu.dmp full=y

2. 将数据库中system用户与sys用户的表导出

        exp system/manager@TEST file=d:\daochu.dmp owner=(system,sys)

3. 将数据库中的表inner_notify、notify_staff_relat导出

        exp aichannel/aichannel@TESTDB2 file= d:datanewsmgnt.dmp tables=(inner_notify,notify_staff_relat)

4. 将数据库中的表table1中的字段filed1以"00"打头的数据导出

        exp system/manager@TEST file=d:daochu.dmp tables=(table1) query=" where filed1 like '00%'"


上面是常用的导出，对于压缩，既用winzip把dmp文件可以很好的压缩。
也可以在上面命令后面 加上 compress=y 来实现。

### 数据的导入

1. 将D:\daochu.dmp 中的数据导入TEST数据库中。

        imp system/manager@TEST file=d:daochu.dmp
        imp aichannel/aichannel@HUST full=y file=d:datanewsmgnt.dmp ignore=y

    上面可能有点问题，因为有的表已经存在，然后它就报错，对该表就不进行导入。在后面加上 ignore=y 就可以了。

2. 将d:\daochu.dmp中的表table1导入

        imp system/manager@TEST file=d:daochu.dmp tables=(table1)

    基本上上面的导入导出够用了。不少情况要先是将表彻底删除，然后导入。

注意：

操作者要有足够的权限，权限不够它会提示。

数据库时可以连上的。可以用tnsping TEST 来获得数据库TEST能否连上。

附录一：

给用户增加导入数据权限的操作

第一,启动sql*puls

第二，以system/manager登陆

第三，create user 用户名 IDENTIFIED BY 密码 （如果已经创建过用户，这步可以省略）

第四，GRANT CREATE USER,DROP USER,ALTER USER ,CREATE ANY VIEW ,
   DROP ANY VIEW,EXP_FULL_DATABASE,IMP_FULL_DATABASE,
      DBA,CONNECT,RESOURCE,CREATE SESSION TO 用户名字

第五, 运行-cmd-进入dmp文件所在的目录,
      imp userid=system/manager full=y file=*.dmp
      或者 imp userid=system/manager full=y file=filename.dmp


附录二：

Oracle 不允许直接改变表的拥有者, 利用Export/Import可以达到这一目的.

先建立import9.par, 然后，使用时命令如下：imp parfile=/filepath/import9.par

例 import9.par 内容如下：

        FROMUSER=TGPMS       
        TOUSER=TGPMS2     （注：把表的拥有者由FROMUSER改为TOUSER，FROMUSER和TOUSER的用户可以不同）          
        ROWS=Y
        INDEXES=Y
        GRANTS=Y
        CONSTRAINTS=Y
        BUFFER=409600
        file==/backup/ctgpc_20030623.dmp
        log==/backup/import_20030623.log
