# iptables防火墙的简单操作

* 启动或停止iptables服务：

        /etc/init.d/iptables start
        /etc/init.d/iptables stop

* 查看入站规则：

        iptables -L INPUT -n --line-numbers

* 删除第5条进入规则：

        iptables -D INPUT 5

* 插入进入规则（插入在第1条，开放tcp 8080端口）：

        iptables -I INPUT 1 -s 0/0 -d 0/0  -p TCP --dport 8080 -j ACCEPT

* 保存iptables设置，使下次系统启动后还有效：

        /sbin/service iptables save 
