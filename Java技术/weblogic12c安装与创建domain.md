# weblogic12c安装与创建domain

## 下载

目前在 <https://www.oracle.com/cn/middleware/technologies/weblogic-server-downloads.html> 还有weblogic 12.2.1.4版本的下载，开发用可以下载 *Quick Installer intended for Oracle WebLogic Server and Oracle Coherence development only.* 的 *Quick Installer for Mac OSX, Windows and Linux (225 MB)* 版本。

## 安装

1. 需要安装一个Oracle JDK8，不支持OpenJDK8
2. 下载下来的压缩文件中包含一个jar包，环境变量切换成Oracle JDK8后（另外Windows下需要用管理员模式cmd），执行 `java -jar fmw_12.2.1.4.0_wls_quick.jar` 命令进行安装，安装后会在当前目录下创建出一个 *wls12214* 目录，即weblogic12c的主目录。

## 创建domain

### 有图形化环境的情况，如Windows

命令行执行 *wls12214/oracle_common/common/bin* 目录下的 config.cmd 或 config.sh 文件，根据图形界面引导操作。

### 无图形化环境的情况

在没有图形化界面的环境，如Linux命令行，可以通过wlst工具来创建domain。

命令行执行 *wls12214/oracle_common/common/bin* 目录下的 wlst.sh 文件，启动 wlst 交互界面。

然后可以按以下脚本逐行执行，#开头的为注释，需要根据实际情况修改。

```
# Select the template to use for creating the domain
selectTemplate('Basic WebLogic Server Domain', '12.2.1.3.0')
loadTemplates()

# Set the listen address and listen port for the Administration Server
cd('Servers/AdminServer')
set('ListenAddress','')
set('ListenPort', 7001)
 
# Enable SSL on the Administration Server and set the SSL listen address and port
create('AdminServer','SSL')
cd('SSL/AdminServer')
set('Enabled', 'True')
set('ListenPort', 7002)

# Set the domain password for the WebLogic Server administration user
# In this example the username is weblogic and the password is weblogic12, can be changed
cd('/')
cd('Security/base_domain/User/weblogic')
cmo.setPassword('weblogic12')

# If the domain already exists, overwrite the domain
setOption('OverwriteDomain', 'true')

# write the domain and close the template
writeDomain('/home/app/domains/mydomain')
closeTemplate()

exit()
```

> selectTemplate中的 12.2.1.3.0 版本是根据 showAvailableTemplates() 查询出来的，确实与weblogic的12.2.1.4有差异，后续小版本更新可能会变化。
