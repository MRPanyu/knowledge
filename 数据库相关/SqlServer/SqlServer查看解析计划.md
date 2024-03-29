# SqlServer查看解析计划

使用SqlServer Management Studio直接可以查看解析计划，本文主要介绍当使用堡垒机等环境，环境上的数据库客户端不是SqlServer Management Studio时，如何查看解析计划。

## 1. 导出SHOWPLAN_XML

在堡垒机等环境，执行这样的SQL，执行的用户需要有 SHOWPLAN 权限：

```sql
-- 启动SHOWPLAN_XML开关，对当前Session有效
SET SHOWPLAN_XML ON;
GO

-- 执行你想要解析计划的SQL语句
SELECT * FROM MYTABLE WHERE MYCOL='1';
GO
```

执行到第二句，原本应该返回结果集的，现在应该是返回了单个XML内容，把这个XML内容拷贝出来，保存成 `plan.sqlplan` 文件。然后想办法把这个文件给发送到安装有SqlServer Management Studio的开发机器上。

## 2. 在SqlServer Management Studio中查看SHOWPLAN_XML

将导出的SHOWPLAN_XML保存成文件，扩展名要用 `.sqlplan`，然后用 SqlServer Management Studio 打开这个文件，就可以看到图形化的解析计划了，与直接从 SqlServer Management Studio 中连着数据库查看Sql解析计划的结果一样。

## 3. 查看预编译SQL的解析计划

操作用户需要有 VIEW SERVER STATE 权限。可以根据SQL的特征来查询现有预编译的解析计划XML

```sql
select top 20 total_worker_time/execution_count as avgCpu,
  plan_handle, creation_time, last_execution_time, execution_count,
  qt.text, qp.query_plan
from 
  sys.dm_exec_query_stats qs
  cross APPLY sys.dm_exec_sql_text (qs.[sql_handle]) as qt
  cross APPLY sys.dm_exec_query_plan (qs.[plan_handle]) as qp
where qt.text like '%select * from my_table%'
order by total_worker_time/execution_count desc;
```
