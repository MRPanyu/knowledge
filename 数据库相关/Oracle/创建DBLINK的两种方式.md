# 有效创建oracle dblink 的两种方式

两台不同的数据库服务器，从一台数据库服务器的一个用户读取另一台数据库服务器下的某个用户的数据，这个时候可以使用dblink。

其实dblink和数据库中的view差不多，建dblink的时候需要知道待读取数据库的ip地址，ssid以及数据库用户名和密码。

创建可以采用两种方式：

## 1、已经配置本地服务

以下是引用片段：

```sql
create public database 
link fwq12 connect to fzept 
identified by neu using 'fjept';

CREATE DATABASE LINK数据库链接名CONNECT TO 用户名 IDENTIFIED BY 密码 USING ‘本地配置的数据的实例名’;
```

## 2、未配置本地服务

以下是引用片段：

```sql
create database link linkfwq 
connect to fzept identified by neu 
using '(DESCRIPTION = 
(ADDRESS_LIST = 
(ADDRESS = (PROTOCOL = TCP)(HOST = 10.142.202.12)(PORT = 1521)) 
) 
(CONNECT_DATA = 
(SERVICE_NAME = fjept) 
) 
)';
```

host=数据库的ip地址，service_name=数据库的ssid。

其实两种方法配置dblink是差不多的，我个人感觉还是第二种方法比较好，这样不受本地服务的影响。

数据库连接字符串可以用NET8 EASY CONFIG或者直接修改TNSNAMES.ORA里定义.

数据库参数global_name=true时要求数据库链接名称跟远端数据库名称一样

数据库全局名称可以用以下命令查出

```sql
SELECT * FROM GLOBAL_NAME;
```

查询远端数据库里的表

``` sql
SELECT …… FROM 表名@数据库链接名;
```

查询、删除和插入数据和操作本地的数据库是一样的，只不过表名需要写成“表名@dblink服务器”而已。

附带说下同义词创建:

```sql
CREATE SYNONYM 同义词名 FOR 表名;
CREATE SYNONYM 同义词名 FOR 表名@数据库链接名;
```

删除dblink：

```sql
DROP PUBLIC DATABASE LINK linkfwq。
```

如果创建全局dblink，必须使用systm或sys用户，在database前加public。
