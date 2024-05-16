# WSL2虚拟盘转移

## 转移 Ubuntu

```bat
wsl --shutdown
wsl --export Ubuntu D:\wsl\Ubuntu.tar
wsl --unregister Ubuntu
wsl --import Ubuntu D:\wsl\Ubuntu D:\wsl\Ubuntu.tar --version 2
ubuntu.exe config --default-user <username>
```

## 转移 docker-desktop 的命令

```bat
wsl --shutdown
wsl --export docker-desktop-data D:\wsl\docker-desktop-data.tar
wsl --unregister docker-desktop-data
wsl --import docker-desktop-data D:\wsl\docker-desktop-data D:\wsl\docker-desktop-data.tar --version 2
wsl --export docker-desktop D:\wsl\docker-desktop.tar
wsl --unregister docker-desktop
wsl --import docker-desktop D:\wsl\docker-desktop D:\wsl\docker-desktop.tar --version 2
```
