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

## oracle XE 或 12c 以后没有wm_concat了怎么办

```sql
CREATE OR REPLACE TYPE wm_concat_impl
   AUTHID CURRENT_USER
AS OBJECT (
   curr_str   VARCHAR2 (32767),
   STATIC FUNCTION odciaggregateinitialize (sctx IN OUT wm_concat_impl)
      RETURN NUMBER,
   MEMBER FUNCTION odciaggregateiterate (
      SELF   IN OUT   wm_concat_impl,
      p1     IN       VARCHAR2
   )
      RETURN NUMBER,
   MEMBER FUNCTION odciaggregateterminate (
      SELF          IN       wm_concat_impl,
      returnvalue   OUT      VARCHAR2,
      flags         IN       NUMBER
   )
      RETURN NUMBER,
   MEMBER FUNCTION odciaggregatemerge (
      SELF    IN OUT   wm_concat_impl,
      sctx2   IN       wm_concat_impl
   )
      RETURN NUMBER
);
/

CREATE OR REPLACE TYPE BODY wm_concat_impl
IS
   STATIC FUNCTION odciaggregateinitialize (sctx IN OUT wm_concat_impl)
      RETURN NUMBER
   IS
   BEGIN
      sctx := wm_concat_impl (NULL);
      RETURN odciconst.success;
   END;
   MEMBER FUNCTION odciaggregateiterate (
      SELF   IN OUT   wm_concat_impl,
      p1     IN       VARCHAR2
   )
      RETURN NUMBER
   IS
   BEGIN
      IF (curr_str IS NOT NULL)
      THEN
         curr_str := curr_str || ',' || p1;
      ELSE
         curr_str := p1;
      END IF;

      RETURN odciconst.success;
   END;
   MEMBER FUNCTION odciaggregateterminate (
      SELF          IN       wm_concat_impl,
      returnvalue   OUT      VARCHAR2,
      flags         IN       NUMBER
   )
      RETURN NUMBER
   IS
   BEGIN
      returnvalue := curr_str;
      RETURN odciconst.success;
   END;
   MEMBER FUNCTION odciaggregatemerge (
      SELF    IN OUT   wm_concat_impl,
      sctx2   IN       wm_concat_impl
   )
      RETURN NUMBER
   IS
   BEGIN
      IF (sctx2.curr_str IS NOT NULL)
      THEN
         SELF.curr_str := SELF.curr_str || ',' || sctx2.curr_str;
      END IF;

      RETURN odciconst.success;
   END;
END;
/

CREATE OR REPLACE FUNCTION wm_concat (p1 VARCHAR2)
   RETURN VARCHAR2
   AGGREGATE USING wm_concat_impl;
/
```
