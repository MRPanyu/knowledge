# regexp_like函数判断中文

Oracle的 regexp_like 函数，可以用来判断是否文本是全中文。

Oracle本身并不支持直接判断中文，但将中文字符转换为 asciistr 后，就可以用正则表达式进行判断了。

以下示例判断 insuredname 列最少两位，中文或英文字符开头结尾，中间可以包含点号（"."）和空格的：

```sql
select insuredname from prpcinsured where regexp_like(asciistr(insuredname), '^(([\][5-8][0-9A-F][0-9A-F][0-9A-F])|([\]4[E-F][0-9A-F][0-9A-F])|([\]9[0-9A-E][0-9A-F][0-9A-F])|([\]9F[0-9][0-9A-F])|([\]9FA[0-5])|[A-Za-z])(([\][5-8][0-9A-F][0-9A-F][0-9A-F])|([\]4[E-F][0-9A-F][0-9A-F])|([\]9[0-9A-E][0-9A-F][0-9A-F])|([\]9F[0-9][0-9A-F])|([\]9FA[0-5])|[A-Za-z.]|\s)*(([\][5-8][0-9A-F][0-9A-F][0-9A-F])|([\]4[E-F][0-9A-F][0-9A-F])|([\]9[0-9A-E][0-9A-F][0-9A-F])|([\]9F[0-9][0-9A-F])|([\]9FA[0-5])|[A-Za-z])$');
```

以下示例判断单个中文或英文字符（不含数字）：

```sql
select 1 from dual where regexp_like(asciistr('汉'), '(([\][5-8][0-9A-F][0-9A-F][0-9A-F])|([\]4[E-F][0-9A-F][0-9A-F])|([\]9[0-9A-E][0-9A-F][0-9A-F])|([\]9F[0-9][0-9A-F])|([\]9FA[0-5])|[A-Za-z])');
```
