# Oracle分页SQL

```sql
-- 使用row_number()函数和rowid
select * from myusers where rowid in (
  select rid from (
    select rowid rid, row_number() over (order by username asc) linenum from prpduser
  ) where linenum between 180 and 200
) order by username asc;
 
-- 使用rownum和rowid
select * from myusers where rowid in (
  select rid from (
    select TT.rowid as rid, rownum As linenum from (
      select rowid from myusers where 1 = 1 order by username asc
    ) TT where rownum < 200
  ) where linenum >= 180
) order by username asc;
 
-- 只使用rownum，不用rowid（适用于查询部分无rowid的视图）
select * from (
  select TT.*, rownum as linenum from (
    select * from logview where 1 = 1 order by starttime desc
  ) TT where rownum <= 200)
where linenum >= 180;

```
