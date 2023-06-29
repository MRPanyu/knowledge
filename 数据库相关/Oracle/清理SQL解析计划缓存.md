# 清理SQL解析计划缓存

注：需要DBA账号执行

根据部分SQL内容，如查询的表名等，查找语句信息：

```sql
select sql_id, sql_text, sql_fulltext address, hash_value from gv$sqlarea a where sql_text like '%T_EDF_TASK%';
```

根据查找出的 sql_id 字段，可以查到相关的解析计划产生时间等信息：

```sql
select * from gv$sql_plan where sql_id='3wagb6tnfk3xd';
```

11g版本以后，可以通过 DBMS_SHARED_POOL.PURGE 来清除相关SQL的解析计划缓存：

```sql
BEGIN
  FOR i IN (SELECT address, hash_value FROM gv$sqlarea WHERE sql_text like '%T_EDF_TASK%')
  LOOP
    SYS.DBMS_SHARED_POOL.PURGE(i.address||','||i.hash_value, 'C');
  END LOOP;
END;
```

这里是根据 `sql_text like` 清理，如果精确定位到某句sql，修改下语句，通过 `sql_id =` 来清理也可以。

清理后再查询解析计划表，相关的内容应该已清空，如果之后重新执行过相关SQL，也可以看到解析计划表的 timestamp 值变成新的了，表示重新生成过解析计划了。

> 注：PURGE动作可能影响在途SQL的执行，因此做这个操作的时候，相关系统尽量停机，操作完后重启。

参考：<https://expertoracle.com/2015/07/08/flush-bad-sql-plan-from-shared-pool/>
