# Eclipse StartExplorer或EasyShell插件相关设置

## Start Explorer

使用dopus打开资源文件目录的命令:

    dopusrt.exe /acmd Go ${resource_path} NEWTAB=tofront,deflister

使用ConEmu64打开资源目录命令行:

    ConEmu64.exe /Dir ${resource_path} /Single

## EasyShell

使用dopus打开资源文件目录的命令:

    dopusrt.exe /acmd Go ${easyshell:container_loc} NEWTAB=tofront,deflister

使用ConEmu64打开资源目录命令行:

    ConEmu64.exe /Title "${easyshell:project_name}" /Dir "${easyshell:container_loc}" /Single /cmd cmd

使用ConEmu64 GitBash打开资源目录命令行：

    ConEmu64.exe -Single -Dir ${easyshell:container_loc}  -run {Git bash}
