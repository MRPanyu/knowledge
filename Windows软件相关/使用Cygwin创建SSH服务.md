# 使用Cygwin创建SSH服务

Linux的机器可以直接使用内置的sshd来启动远程ssh连接服务，在Windows机器上也可以通过Cygwin工具实现。

#### 1. 安装Cygwin，在安装Cygwin时，会要求选择要安装哪些软件包，在Net组下面有openssh的一项，选择上，Cygwin会自动获取依赖等。直至完成安装。
 
#### 2. 启动Cygwin-Terminal
 
#### 3. 按以下方式完成SSH服务端配置（其中部分内容是手工输入的）：

    appadmin@WIN-kaifa ~
    $ ssh-host-config
     
    *** Info: Generating /etc/ssh_host_key
    *** Info: Generating /etc/ssh_host_rsa_key
    *** Info: Generating /etc/ssh_host_dsa_key
    *** Info: Generating /etc/ssh_host_ecdsa_key
    *** Info: Creating default /etc/ssh_config file
    *** Info: Creating default /etc/sshd_config file
    *** Info: Privilege separation is set to yes by default since OpenSSH 3.3.
    *** Info: However, this requires a non-privileged account called 'sshd'.
    *** Info: For more info on privilege separation read /usr/share/doc/openssh/README.privsep.
    *** Query: Should privilege separation be used? (yes/no) no
    *** Info: Updating /etc/sshd_config file
     
    *** Query: Do you want to install sshd as a service?
    *** Query: (Say "no" if it is already installed as a service) (yes/no) yes
    *** Query: Enter the value of CYGWIN for the daemon: [] sshd
    *** Info: On Windows Server 2003, Windows Vista, and above, the
    *** Info: SYSTEM account cannot setuid to other users -- a capability
    *** Info: sshd requires. You need to have or to create a privileged
    *** Info: account. This script will help you do so.
     
    *** Info: You appear to be running Windows XP 64bit, Windows 2003 Server,
    *** Info: or later. On these systems, it's not possible to use the LocalSystem
    *** Info: account for services that can change the user id without an
    *** Info: explicit password (such as passwordless logins [e.g. public key
    *** Info: authentication] via sshd).
     
    *** Info: If you want to enable that functionality, it's required to create
    *** Info: a new account with special privileges (unless a similar account
    *** Info: already exists). This account is then used to run these special
    *** Info: servers.
     
    *** Info: Note that creating a new user requires that the current account
    *** Info: have Administrator privileges itself.
     
    *** Info: No privileged account could be found.
     
    *** Info: This script plans to use 'cyg_server'.
    *** Info: 'cyg_server' will only be used by registered services.
    *** Query: Do you want to use a different name? (yes/no) no
    *** Query: Create new privileged user account 'cyg_server'? (yes/no) no
    *** ERROR: There was a serious problem creating a privileged user.
    *** Query: Do you want to proceed anyway? (yes/no) yes
    *** Warning: Expected privileged user 'cyg_server' does not exist.
    *** Warning: Defaulting to 'SYSTEM'
     
    *** Info: The sshd service has been installed under the LocalSystem
    *** Info: account (also known as SYSTEM). To start the service now, call
    *** Info: `net start sshd' or `cygrunsrv -S sshd'. Otherwise, it
    *** Info: will start automatically after the next reboot.
     
    *** Warning: Host configuration exited with 1 errors or warnings!
    *** Warning: Make sure that all problems reported are fixed,
    *** Warning: then re-run ssh-host-config.
     
    appadmin@WIN-kaifa ~
    $ cygrunsrv -S sshd

#### 4. 最后一句cygrunsrv -S sshd会将SSH服务启动，之后通过Putty等SSH工具连接22端口，用户名/密码是当前Windows系统用户的用户名/密码
