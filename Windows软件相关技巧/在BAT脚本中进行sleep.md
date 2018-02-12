# 在BAT脚本中进行sleep

## 方法1：使用ping命令

示例中30000为30秒

    ping 1.2.3.4 -n 1 -w 30000 > %TMP%\_sleep.out

## 方法2：使用timeout命令

停顿30秒，用户可以按键中断：

    timeout /t 30

如果不想让用户按键中断（还是可以Ctrl-C中断）：

    timeout /t 30 /nobreak

如果不想显示倒计时：

    timeout /t 30 /nobreak > NUL
