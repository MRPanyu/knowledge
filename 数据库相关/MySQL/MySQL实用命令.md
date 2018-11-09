# MySQL实用命令

```sql
-- 显示innodb引擎相关报告，包括最后一次死锁情况
show engine innodb status;
```

```sql
-- 查询结果中增加一个行号
select @i:=@i+1, t.* from (SELECT @i:=0) r, sometable t where t.somecolum=1;
```