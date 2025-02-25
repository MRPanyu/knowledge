# vmware ubuntu open-vm-tools

## install

```sh
sudo apt-get install open-vm-tools
```

## folder share

创建 /mnt/hgfs

```sh
sudo mkdir /mnt/hgfs
```

编辑 `/etc/fstab`, 增加内容:

```txt
.host:/ /mnt/hgfs fuse.vmhgfs-fuse allow_other 0 0
```
