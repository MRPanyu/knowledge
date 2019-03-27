# Oracle12c2问题

安装Oracle12c2(12201)版本后，遇到如下几个问题：

## ORA-28040

JDBC连接方式还是比较旧的，需要修改sqlnet.ora文件  
（E:\app\oracle\product\12.2.0\dbhome_1\network\admin）

新增一行：SQLNET.ALLOWED_LOGON_VERSION=8

## ORA-01017

老版本的Oracle用户密码是大小写不敏感的，新版本默认变成大小写敏感了，也会和老的JDBC不兼容。

用sysdba登录：

alter system set sec_case_sensitive_logon=false scope=spfile;

然后需要重新修改用户的密码才有效：

alter user system identified by oracle;
