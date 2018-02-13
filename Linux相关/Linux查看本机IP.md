# Linux查看本地IP

可以用如下命令查询本机IP。注：如果有多网卡，可能会需要用有eth1,eth2等等：

    cd /etc/sysconfig/networking/devices
    cat ifcfg-eth0
