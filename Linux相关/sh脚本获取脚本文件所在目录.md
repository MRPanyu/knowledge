# sh脚本获取脚本文件所在目录

可以使用以下语句，设置baseDirForScriptSelf变量为当前这个sh脚本所在目录。

    baseDirForScriptSelf=$(cd "$(dirname "$0")"; pwd)
