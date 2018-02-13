# 命令行获取SQL解析计划

    SQL> explain plan for select * from user_tables where tablename=:1 ;
    Explained
    SQL> SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
    PLAN_TABLE_OUTPUT
    --------------------------------------------------------------------------------
    Plan hash value: 825974890
    --------------------------------------------------------------------------------
    | Id  | Operation         | Name        | Rows  | Bytes | Cost (%CPU)| Time
    --------------------------------------------------------------------------------
    |   0 | SELECT STATEMENT  |             |     1 |    25 |     3   (0)| 00:00:01
    |*  1 |  TABLE ACCESS FULL| USER_TABLES |     1 |    25 |     3   (0)| 00:00:01
    --------------------------------------------------------------------------------
    Predicate Information (identified by operation id):
    ---------------------------------------------------
       1 - filter("TABLENAME"=:1)
    13 rows selected  
