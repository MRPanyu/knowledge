# Win11 Virtualbox无法虚拟化问题

## 现象

Windows 11中，使用Virtualbox运行虚拟机感觉非常慢，另外表现为虚拟机配置中 “系统 -> 处理器 -> 启用嵌套VT-x/AMD-V” 灰色无法勾选，以及启动虚拟机后，状态条右下角的“V”图表是乌龟图标，而不是芯片图标。

## 处理方案

注：以下两步均需要执行

1. Windows设置中搜索“内核隔离”，将内存完整性设为关闭，需重启

2. 管理员权限执行命令 `bcdedit /set hypervisorlaunchtype off` 关闭 Hyper-V，需重启

注意第2步执行后由于关闭了 Hyper-V ，WSL或Docker for windows等会无法使用，需要重新打开后才能使用WSL。

重新开启 Hyper-V 的命令：`bcdedit /set hypervisorlaunchtype auto`
