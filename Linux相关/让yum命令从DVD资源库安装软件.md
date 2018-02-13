# 让yum命令从DVD资源库安装软件

这主要适用于没有网络环境或网络环境差，但有DVD安装介质的情况。

下面的操作是针对 CentOS 6 版本的。

1. 把DVD介质 mount 到 /media/cdrom 目录

        cd /media
        mkdir cdrom
        mount /dev/cdrom /media/cdrom

2. 在 /etc/yum.repos.d/CentOS-Media.repo 文件中，有关于如何使用DVD介质的说明
3. 以指定 repo 的方式运行 yum，例如：

        yum --disablerepo=\* --enablerepo=c6-media install kernel-devel.x86_64
        yum --disablerepo=\* --enablerepo=c6-media install gcc.x86_64

4. 最后可以 unmount DVD介质：

        umount /media/cdrom
