# VirtualBox共享目录设置

在VirtualBox中设置好共享目录后，用一般的登录用户访问，会告知说权限不足。

解决方法是把一般登录用户加入到vboxsf组中。

如要把myuser用户加入vboxsf组中，命令是：

```bash
usermod -a -G vboxsf myuser
```

---

另外还有一种方案是直接su root用户来操作。在图形界面中，可以通过这样的命令，弹出获取管理员权限的框，用管理员身份打开文件管理器：

Ubuntu Mate: `pkexec caja` （pkexec命令用于提升为管理员权限，caja是Mate桌面的文件管理器）

CentOS KDE: `kdesu dolphin` （kdesu命令用于提升为管理员权限，dolphin是KDE桌面的文件管理器）
