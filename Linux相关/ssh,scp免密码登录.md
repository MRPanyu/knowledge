# ssh,scp免密码登录

适用于在Linux系统（或Windows中如GitBash一类的类GNU环境）中，免密码ssh或scp连接另一台机器。对于自动化脚本等非常有用。

首先通过`ssh-keygen`命令，生成一个本地证书：

```sh
ssh-keygen
```

命令行会提示生成的文件位置，保持默认即可，之后会提示输入密码（可以为空），密码/确认密码都留空。

之后通过`ssh-copy-id`命令，将本地证书上传到远程服务器：

```sh
ssh-copy-id username@remotehost
```

示例命令中 *username* 是远程服务器用户名，*remotehost* 是远程服务器域名或IP。

之后会像普通的ssh/scp命令一样，提示输入密码（首次可能还会要求确认远程机器的证书），正常输入密码即可。

操作完成后，本地证书就被远程服务器认可了，之后执行ssh/scp命令连接该远程机器的该用户，都不再需要输入密码了。