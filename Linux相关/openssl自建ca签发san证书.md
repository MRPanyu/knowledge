# openssl自建ca签发san证书

本文档主要介绍在linux系统（示例使用的是ubuntu，但其他linux发行版应该大致相同）中如何通过openssl工具进行自建ca，并签发san证书。

签发出的证书可以用于https或者其他需要使用ssl的软件，且可以在windows等系统导入信任该ca来实现符合认证的https通讯。

注：以下示例为自建一个名称为 ca.localnetwork.net 的本地ca，并签发一张 *.localnetwork.net 的服务器证书。结合hosts文件的配置，可用于本地关于ssl的各种验证

> SAN(Subject Alternative Name)是 SSL 标准 x509 中定义的一个扩展。使用了 SAN 字段的 SSL 证书，可以扩展此证书支持的域名，使得一个证书可以支持多个不同域名的解析，如 `*.baidu.com`

参考文档：

- <https://networklessons.com/uncategorized/openssl-certification-authority-ca-ubuntu-server/>
- <https://liaoph.com/openssl-san/>
- <https://tomcat.apache.org/tomcat-9.0-doc/ssl-howto.html>
- <https://tomcat.apache.org/tomcat-9.0-doc/config/http.html#SSL_Support_-_SSLHostConfig>

## 1. openssl安装

各发行版安装命令不同，有些可能已经预装过了，可以通过 `openssl version -a` 命令来查看

- RHEL/CentOS: `yum install openssl openssl-devel`
- Ubuntu/Debian: `apt install openssl libssl-dev -y`

## 2. 创建ca

> ca即Certification Authority，用于签发SSL数字证书。互联网上有权威性的ca，如Verisign等，各浏览器等软件原生认可。这里我们要创建一个自己的免费ca，用于本机或者局域网环境验证用。

### 2.1 创建ca目录结构

首先找一个目录作为ca的数据存储目录，并创建其基本文件结构。这里以 `/root/ca` 目录作为ca数据目录。

```sh
mkdir /root/ca
cd /root/ca
touch index.txt
echo '1000' > serial
mkdir newcerts crl private requests
```

> 说明：ca目录下需要有index.txt文件（数据存储文件，初始为空）和serial文件（序列号文件，初始设置为1000），另外各个子目录为后续命令要用到。

### 2.2 修改openssl.cnf配置文件

用`vim`命令打开并修改`openssl.cnf`配置文件：

```sh
vim /usr/lib/ssl/openssl.cnf
```

> 注：这个文件的位置可能会根据linux发行版有所不同，如CentOS6好像在 `/etc/pki/tls/openssl.cnf`

找到文件中这部分：

```ini
[ CA_default ]

dir      = ./demoCA
```

修改为刚才创建的 `/root/ca` 目录

```ini
[ CA_default ]

dir      = /root/ca
```

另外为了避免后续操作可能遇到的一些冲突，可以放宽签发控制策略，找到如下这段：

```ini
[ policy_match ]
countryName             = match
stateOrProvinceName     = match
organizationName        = match
organizationalUnitName  = optional
commonName              = supplied
emailAddress            = optional
```

调整为：

```ini
[ policy_match ]
countryName             = match
stateOrProvinceName     = optional
organizationName        = optional
organizationalUnitName  = optional
commonName              = supplied
emailAddress            = optional
```

### 2.3 创建ca的私钥与证书

回到 `/root/ca` 目录下，执行如下命令生成私钥：

```sh
cd /root/ca
openssl genrsa -aes256 -out private/cakey.pem 4096
```

执行过程中会要求输入一个密码，保护这个cakey.pem文件，然后会要求再输入一遍这个密码确认。密码可以随意设置一个，不过需要记录好以供后续步骤使用。

```txt
Generating RSA private key, 4096 bit long modulus (2 primes)
..........................................
e is 65537 (0x010001)
Enter pass phrase for private/cakey.pem:
Verifying - Enter pass phrase for private/cakey.pem:
```

完成后，需要再用命令生成ca的证书：

```sh
cd /root/ca
openssl req -new -x509 -key ./private/cakey.pem -out cacert.pem -days 3650 -set_serial 0
```

执行命令过程中，会要求输入cakey.pem文件的密码，然后回要求输入ca证书的相关信息，如国家，地区，组织机构等。其中Common Name一般是指服务器的具体域名地址，本地用的话可以给ca随便设置一个容易识别的Common Name。

```txt
Enter pass phrase for ./private/cakey.pem:
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [CN]:CN
State or Province Name (full name) [Some-State]:Shanghai
Locality Name (eg, city) []:
Organization Name (eg, company) [Internet Widgits Pty Ltd]:My Org
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:ca.localnetwork.net
Email Address []:
```

> pem是一种Base64格式用于存储私钥或者证书的文件，这里生成的私钥cakey.pem和证书cacert.pem文件都是pem格式，但存储的内容一个是私钥（需保密），一个是公钥证书（可以发放给其他人）。在windows环境下，可以把这个cacert.pem文件改名成cacert.crt，这样就变成一个可导入的证书文件了（crt文件的内容实际也是pem格式，只是仅适用于证书，不适用于私钥存储）

目前的文件结构：

- **/root/ca/private/cakey.pem**: ca的私钥文件，这个文件需要保密，且有密码保护
- **/root/ca/cacert.pem**: ca的证书文件，这个是可以提供给客户端等用于信任该ca的

## 3. 创建并签发san证书

创建好ca后，下一步要创建一个san证书，并使用ca进行签发。

### 3.1 修改openssl.cnf配置文件

首先再次编辑 `/usr/lib/ssl/openssl.cnf` 文件（调整会较多，建议先备份），这个文件的格式应该挺容易看懂，中括号是分组，里面内容则是key=value的格式。

首先找到 `[ v3_req ]` 部分，原始的内容可能是这样：

```ini
[ v3_req ]

# Extensions to add to a certificate request

basicConstraints = CA:FALSE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment
```

调整为如下（注意默认这个文件中不存在 `[ alt_names ]` 块，是整个是加出来的。）

```ini
[ v3_req ]

# Extensions to add to a certificate request

basicConstraints = CA:FALSE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment
subjectAltName = @alt_names

[ alt_names ]

DNS.1 = *.localnetwork.net
DNS.2 = localnetwork.net
```

注意 `[ alt_names ]` 部分的就是要创建的证书支持的子域名，可以按绝对域名设置，也可以设置通配符。
通配符的情况应该是有一定限制，如我试过设置 "*.localnetwork" ，签发的证书Chrome浏览器就不认可，可能是后缀至少需要两段。另外设置通配符的情况最好也新加一个不含通配符的域名。

之后继续在文件中找到 `[ req ]` 部分，找到这行内容（默认应该是注释掉的），放开注释：

```ini
[ req ]
# ......
# 注：从[ req ]到这行中间还有其他内容的，不要修改，只把下面这行原先注释掉的放开
req_extensions = v3_req # The extensions to add to a certificate request
```

### 3.2 创建san证书并使用ca签发

回到 `/root/ca/requests` 目录下，创建服务器的私钥：

```sh
cd /root/ca/requests
openssl genrsa -out some_server_key.pem 2048
```

为了后续发布的时候方便，与创建ca私钥文件时不同，这里没有添加`-aes256`参数，因此不会要求输入文件保护的密码。

> 如果服务器私钥有密码保护，则配置到nginx或java程序的时候，一般需要在启动时命令行中输入一遍密码才能正常使用。

然后需要生成一个证书申请文件：

```sh
cd /root/ca/requests
openssl req -new -key some_server_key.pem -out some_server.csr
```

中间也会要求输入证书信息等内容。Common Name这里用了通配的"*.localnetwork.net"，另外最后两项"A challenge password"和"An optional company name"这里是留空的：

```txt
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:CN
State or Province Name (full name) [Some-State]:Shanghai
Locality Name (eg, city) []:
Organization Name (eg, company) [Internet Widgits Pty Ltd]:MyOrg
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []:*.localnetwork.net
Email Address []:

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
```

最后以ca的身份对这个证书请求文件进行签发：

```sh
cd /root/ca/requests
openssl ca -in some_server.csr -out some_server.pem -extensions v3_req
```

这个命令需要输入ca私钥证书的密码，之后显示待签发的证书请求信息，填写"y"确认签发：

```txt
Using configuration from /usr/lib/ssl/openssl.cnf
Enter pass phrase for /root/ca/private/cakey.pem:
Check that the request matches the signature
Signature ok
Certificate Details:
        Serial Number: 4096 (0x1000)
        Validity
            Not Before: Feb 28 07:53:45 2022 GMT
            Not After : Feb 28 07:53:45 2023 GMT
        Subject:
            countryName               = CN
            stateOrProvinceName       = Shanghai
            organizationName          = MyOrg
            commonName                = *.localnetwork.net
        X509v3 extensions:
            X509v3 Basic Constraints:
                CA:FALSE
            X509v3 Key Usage:
                Digital Signature, Non Repudiation, Key Encipherment
            X509v3 Subject Alternative Name:
                DNS:*.localnetwork.net, DNS:localnetwork.net
Certificate is to be certified until Feb 28 07:53:45 2023 GMT (365 days)
Sign the certificate? [y/n]:y


1 out of 1 certificate requests certified, commit? [y/n]y
Write out database with 1 new entries
Data Base Updated
```

现在 `/root/ca/requests` 目录下有如下几个文件：

- **some_server_key.pem**: 服务器的私钥文件，搭建服务器时是需要的
- **some_server.pem**: 服务器的证书文件，搭建服务器时时需要的
- **some_server.csr**: 证书请求文件，这个文件已经使用完了，可以删除

### 3.3 导出为PKCS12格式证书库

对部分中间件（如JDK），需要使用PKCS12格式的证书库来存储私钥和证书，也可以通过openssl命令来完成：

```sh
cd /root/ca/requests
openssl pkcs12 -export -in some_server.pem -inkey some_server_key.pem -out some_server.p12 -name app -CAfile ../cacert.pem -caname root -chain
```

会要求输入密码，这个密码是这个some_server.p12密钥库文件的密码，可以随意设定。这个命令将刚才生成的some_server_key.pem和some_server.pem中的私钥与证书，导入了some_server.p12证书库文件，别名为app。

> 注意这个命令设置了服务器密钥/证书的别名为app，并关联了相关的CA证书

### 3.4 导出为JKS格式证书库

> 注：JKS是Java使用的证书库格式。Java8以后已经支持直接使用PKCS12格式证书库了（需要指定证书库类型），所以这步可选。以下命令keytool是JDK提供的，在 $JAVA_HOME/bin 目录下，这里默认当作已经配置好 PATH 环境变量了。另外这个命令只需要从之前生成的p12证书库文件导入，因此只要有p12文件就可以了，不一定需要在ca那台机器上操作，可以放在任意安装了JDK的机器上进行。

```sh
keytool -importkeystore -srckeystore some_server.p12 -srcstoretype PKCS12 -destkeystore some_server.jks -deststoretype JKS -srcalias app -destalias app
```

## 4. 在客户端配置信任ca签发的证书

### 4.1 在windows环境配置信任ca证书

首先将 `cacert.pem` 文件复制到 windows 机器上，将其改名为 `cacert.crt`，然后双击，应该就会弹出“证书导入向导”的界面。

注意第二页中，需要选择“将所有的证书都放入下列存储”，然后选择“受信任的根证书颁发机构”。

之后全部确认，以后windows下的浏览器就会把这个ca签发的证书也认为是合法证书了。

### 4.2 Java相关设置

在Java中调用https服务的时候，也需要导入对ca的信任，才能正确进行调用。

> 注：以下用到的keytool命令在 $JAVA_HOME/bin 目录下，这里默认当作已经配置好 PATH 环境变量了。

首先使用keytool命令创建一个信任证书库，将cacert.pem这个ca证书给导入进去。下面假设已经将cacert.pem文件复制到当前目录下。

```sh
keytool -importcert -trustcacerts -file cacert.pem -keystore trustca.jks
```

命令执行时会要求输入密钥库口令，这个是指`trustca.jks`这个密钥库文件的口令，因为这次是新建文件所以可以随意设置一个。之后会要求确认是否导入，输入`y`确认。

这样当前目录下就产生了`trustca.jks`这个密钥库文件，保管好这个密钥库文件及口令。

之后可以通过java命令中的-D启动参数，配置信任证书库：

```sh
java -Djavax.net.ssl.trustStore=/path/to/trustca.jks -Djavax.net.ssl.trustStorePassword=trustca_password ...
```

> 两个配置项分别是信任证书库的文件与口令。

之后再作为客户端进行https调用的时候，如果服务端的证书是由这个自建ca签发出来的，且访问url域名与证书一致（包含在san里面），则应该可以正常访问不报错。

## 5. 服务器证书的使用示例

### 5.1 spring-boot配置

需要之前生成的some_server.p12密钥库文件。

在application.yml中做如下配置：

```yml
server:
  port: 8443
  ssl:
    key-store: /path/to/some_server.p12 # 密钥库文件路径
    key-store-type: PKCS12 # 密钥库格式为PKCS12
    key-store-password: 'server_password' # 密钥库的密码
    key-password: 'server_password' # 同密钥库密码
    key-alias: app # 密钥库中私钥/证书的别名
```

### 5.2 tomcat配置

放开`server.xml`文件中ssl 8443端口的配置，配置上`some_server.p12`证书库。示例如下：

```xml
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
        maxThreads="150" SSLEnabled="true">
    <SSLHostConfig>
        <Certificate certificateKeystoreFile="/path/to/some_server.p12" 
            certificateKeystorePassword="server_password"
            certificateKeystoreType="PKCS12" type="RSA" />
    </SSLHostConfig>
</Connector>
```

### 5.3 nginx的ssl配置

这里仅说明一下证书相关的配置，需要生成的服务器私钥`some_server_key.pem`和服务器证书`some_server.pem`文件，nginx对ssl有其他很多配置项，请查找相关的文档。

```nginx.conf
server {
    listen              443 ssl;
    
    ssl_certificate     /path/to/some_server.pem;
    ssl_certificate_key /path/to/some_server_key.pem;

    ...
}
```
