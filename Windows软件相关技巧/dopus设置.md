# dopus设置

## 1.工具按钮命令脚本

**ConEmu64:**

    "C:\Program Files\ConEmu\ConEmu64.exe" -Dir {sourcepath$} -Single

**ConEmu64 GitBash:**

    "C:\Program Files\ConEmu\ConEmu64.exe" -Dir {sourcepath$} -Single -run {Git bash}

**GitBash:**

    C:\Program Files\Git\git-bash.exe --cd={sourcepathshort}

## 2.外部工具在dopus中打开目录

    dopusrt.exe /acmd Go ${resource_path} NEWTAB=tofront
