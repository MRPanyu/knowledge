# Oracle查询被锁的表并释放Session

查询sql:

```sql
SELECT A.OWNER,
A.OBJECT_NAME,
B.XIDUSN,
B.XIDSLOT,
B.XIDSQN,
B.SESSION_ID,
B.ORACLE_USERNAME, 
B.OS_USER_NAME,
B.PROCESS, 
B.LOCKED_MODE, 
C.MACHINE,
C.STATUS,
C.SERVER,
C.SID,
C.SERIAL#,
C.PROGRAM
FROM ALL_OBJECTS A,
V$LOCKED_OBJECT B,
SYS.GV_$SESSION C 
WHERE ( A.OBJECT_ID = B.OBJECT_ID )
AND (B.PROCESS = C.PROCESS )
ORDER BY 1,2
```

释放session Sql:

```sql
alter system kill session 'sid, serial#'
alter system kill session '379, 21132'
alter system kill session '374, 6938'
```
----------
 
查询锁的状况的对象有

V$LOCK, V$LOCKED_OBJECT, V$SESSION, V$SQLAREA, V$PROCESS

查询锁的表的方法:

```sql
SELECT S.SID SESSION_ID, S.USERNAME, DECODE(LMODE, 0, 'None', 1, 'Null', 2, 'Row-S (SS)', 3, 'Row-X (SX)', 4, 'Share', 5, 'S/Row-X (SSX)', 6, 'Exclusive', TO_CHAR(LMODE)) MODE_HELD, DECODE(REQUEST, 0, 'None', 1, 'Null', 2, 'Row-S (SS)', 3, 'Row-X (SX)', 4, 'Share', 5, 'S/Row-X (SSX)', 6, 'Exclusive', TO_CHAR(REQUEST)) MODE_REQUESTED, O.OWNER||'.'||O.OBJECT_NAME||' ('||O.OBJECT_TYPE||')', S.TYPE LOCK_TYPE, L.ID1 LOCK_ID1, L.ID2 LOCK_ID2 FROM V$LOCK L, SYS.DBA_OBJECTS O, V$SESSION S WHERE L.SID = S.SID AND L.ID1 = O.OBJECT_ID ;
```
 
oracle 查看死锁的脚本

```sql
SELECT substr(v$lock.sid,1,4) "SID",
       substr(username,1,12) "UserName",
       substr(object_name,1,25) "ObjectName",
       v$lock.type "LockType",
       decode(rtrim(substr(lmode,1,4)),
       '2','Row-S (SS)','3','Row-X (SX)',
       '4','Share',     '5','S/Row-X (SSX)',
       '6','Exclusive', 'Other' ) "LockMode",
       substr(v$session.program,1,25) "ProgramName"
FROM V$LOCK,SYS.DBA_OBJECTS,V$SESSION
WHERE (OBJECT_ID = v$lock.id1
      AND v$lock.sid = v$session.sid
      AND username IS NOT NULL
      AND username NOT IN ('SYS','SYSTEM')
      AND SERIAL# != 1);
```
 
查看锁表进程SQL语句1：

```sql
select sess.sid,
    sess.serial#,
    lo.oracle_username,
    lo.os_user_name,
    ao.object_name,
    lo.locked_mode
    from v$locked_object lo,
    dba_objects ao,
    v$session sess
where ao.object_id = lo.object_id and lo.session_id = sess.sid;
```

查看锁表进程SQL语句2：

```sql
select * from v$session t1, v$locked_object t2 where t1.sid = t2.SESSION_ID;
```

杀掉锁表进程：
如有記錄則表示有lock，記錄下SID和serial# ，將記錄的ID替換下面的738,1429，即可解除LOCK

```sql
alter system kill session '738,1429';
```
