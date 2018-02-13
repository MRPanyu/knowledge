# 语言字符集相关设置（Locale）

## 获取当前Locale

可以通过执行以下命令获取当前Locale：

    locale

显示的格式为：`<lang>_<territory>.<codeset>[@<modifiers>]`

例如，澳大利亚英语UTF-8字符集，显示：`en_AU.UTF-8`

## 所有可用Locale

可以通过以下命令获取所有可用的Locale：

    locale -a

## 增加Locale

可以从以下文件找到所有系统中支持的Locale:

    /usr/share/i18n/SUPPORTED

可以通过 `local-gen` 命令增加可用Locale（root 权限），如:

    locale-gen en_US.UTF-8

之后用 `locale -a` 命令可以看到新增的可用Locale。

## 设置当前Session中的Locale

只要设置`LANG`变量即可，如：

     export LANG=en_US.utf8

可以通过将该命令增加到 `~/.bashrc` 或 `~/.profile` 文件中，使每次登录后自动生效。

## 设置操作系统默认Locale

修改 `/etc/default/locale` 文件，设置 `LANG="en_US.utf8"` ，系统重启后生效。
