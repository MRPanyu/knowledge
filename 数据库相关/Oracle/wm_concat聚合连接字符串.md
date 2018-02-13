# wm_concat聚合连接字符串

Oracle数据库中，可以使用 wm_concat 函数聚合连接字符串。

示例：

```sql
select wm_concat(usercode) from users where usercode in ('0000000000','3101000000','3201000000');
```

因为是聚合函数（类似 sum 或 count 一类的）显示的结果集为一行一列，把查询出来的列用逗号分隔符拼接起来：

|WM_CONCAT(USERCODE)             |
|--------------------------------|
|0000000000,3101000000,3301000000|
