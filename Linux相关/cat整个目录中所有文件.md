# cat整个目录中所有文件

```sh
find src/main/java -name '*.java' -exec cat {} \; > output.txt
```

>find <where> \
  -name <file_name_pattern> \
  -exec <run_cmd_on_every_hit> {} \; \
    > <where_to_store>
