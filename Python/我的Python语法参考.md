# 我的Python语法参考

> 注：这个是我基于已有java编程经验整理的python学习笔记，供将来参考用的。

## 基本语法

### 注释

```python
# 这是注释
print('1') # 行后注释
```

### 变量

变量直接用，所有变量都是通用类型，不需要声明类型，也可以赋值成任何类型。

变量必须字母或下划线开头，后面可以含数字，不能用$符号。

变量大小写敏感。

### 换行缩进

分号`;`分隔可以将两句语句放一行，句尾的分号可省略

```python
print("a"); print("b");
```

缩进表示代码块层级（如if/循环的代码块），一般用4个空格。

### 流程控制

#### if/elif/else

```python
a = int(input('输入整数: '))
if a == 0:
    print("a = 0")
elif a > 0:
    print("a > 0")
else:
    print("a < 0")
```

#### while

```python
b = True
while b:
    a = int(input('输入整数: '))
    if a == 0:
        print("退出循环")
        b = False
    else:
        print("继续循环")
```

#### for

for用来循环 turple / list / dictionary(的key)

```python
for i in range(1, 5):
    print(i)
else:
    print('结束')

tp = (0,2,4,6)
for i in tp:
    print(i)
else:
    print('结束')

lst = [1,3,5,7]
for i in lst:
    print(i)
else:
    print('结束')

dict = {"a": 1, "b": 2}
for i in dict:
    print(i)
else:
    print('结束')
```

#### break/continue

同 java 用法

### 函数

```python
z = 0 # 全局变量z

# 一个包含了参数默认值，不定参，命名不定参的函数
def func1(x, y = 0, *params, **namedParams):
    '''函数一开始的字符串是DocString，
    可以通过func1.__doc__获取'''
    global z # 表示函数里用到的z是全局变量
    z += 1 # 可以修改全局变量
    s = f'x={x}, y={y}, z={z}, {params}, {namedParams}'
    print(s)
    return s

func1(1) # y使用默认值
func1(1, 2) # x, y都指定
func1(1, 2, 3, 4) # 包含两个不定参
func1(1, 2, 3, 4, a = 7, b = 8) # 包含不定参和命名不定参
func1(y = 5, x = 1, a = 6) # x, y指定值，再加上命名不定参
print(func1.__doc__) # 获取DocString
```

### 模块/包

```python
import sys
import math
print(sys.path)
print(math.sqrt(16))
print(dir()) # 打印本模块有哪些内容，如变量等
print(dir(sys)) # 打印sys模块有哪些内容
```

```python
from math import sqrt, pow
print(sqrt(16))
print(pow(2, 5))
```

相当于别的编程语言的main方法的:

```python
if __name__ == '__main__':
    print('相当于main方法执行的程序')
else:
    print('当别的模块import本模块的时候会执行')
```

包结构，可以在当前目录下组织目录结构:

- `package1`
    - `subpackage1`
        - `__init__.py`
        - `module1.py`
    - `subpackage2`
        - `__init__.py`
        - `module2.py`
- `main.py`

其中 `__init__.py` 相当于这个包的定义文件

```python
import package1.subpackage1 # 引入的实际是 package1/subpackage1/__init__.py 定义的内容
import package1.subpackage1.module1 # 引入的是 package1/subpackage1/module1.py 定义的内容
print(package1.subpackage1.var1)
print(package1.subpackage1.module1.var1)
```

> 注: `sys.path` 相当于java classpath，包含会加载python文件的路径，默认是当前目录+python系统库+pip类库之类的。python默认是pip全局依赖只能一个版本，一般需要用虚拟环境之类的来实现项目依赖管理，这个是个专门话题了暂不深入。

## 数据类型

### 字符串

单引号/双引号括起来都是相同的字符串

三连引号（单双引号都可以）括起来的字符串可以换行

```python
print("""Line1
Line2""")
```

几种格式化方式

```python
print("{0} is {1}".format("Orange", "sweet"))
print("{} is {}".format("Orange", "sweet"))
print("{a} is {b}".format(a="Orange", b="sweet"))
x = "Orange"
y = "sweet"
print(f"{x} is {y}")
print("{0:.3f}".format(1/3))
print("{:.5f}".format(1/3))
```

字符串与数字不能直接相加，要用`str()`函数转成字符串

```python
print("1" + str(1))
print(int(1) + 1)
print(float(1.1) + 1)
```

`\n \t \\`这种也通用，另外r开头表示Raw String，不做转义，适用于正则表达式这种需要写许多`\`的地方

```python
print(r"\n \t \\")
```

其他一些方法

```python
s = 'Good morning'
print(s.startswith('Good'))
print('Good' in s)
print(s.find('mo')) # 获取下标，找不到返回-1
print(s.index('mo')) # 同find，但找不到会报错
print(','.join(['a', 'b', 'c'])) # 数组连接，用分隔符的join方法
```

### 数字

只有int/float两种，都是大数类型，没有长度和精度限制

计算符：

- `+ - * /`: 基本的加减乘除
- `**`: 乘方
- `//`: 整除（floor）
- `%`: 取模
- `+= -= *= /=`: 自增等，注意没有 `a++` 这种语法，用 `a+=1`

### List

可变的数组/List结构

```python
ls = ["aaa", "bbb", "ccc"]
ls[0] = "aaaa" # 下标赋值
print(ls)
ls.append('ddd') # 追加
ls.insert(0, "000") # 插入
print(ls)
ls.remove("bbb") # 删除中间某个
print(ls)
del ls[0] # 下标删除
print(ls)
ls.sort() # 排序
print(len(ls)) # 长度
print('ccc' in ls) # 是否包含
print('999' not in ls)
print(ls.count('ddd')) # 查找
print(ls.index('ddd')) # 定位（注：找不到会报错）
ls.extend(['eee', 'fff']) # 追加全部
print(ls)
ls.clear() # 清空
```

### Tuple

不可变的数组

```python
tp = ('aaa', 'bbb', 'ccc')
tp = 'aaa', 'bbb', 'ccc' # 括号省略
print(len(tp), 'aaa' in tp, tp[0])

# 以下这些List和字符串也支持
print(tp[-1]) # 倒数
print(tp[0:2]) # 子集
print(tp[0:-1]) # 子集加倒数
print(tp[:]) # 复制
print(tp[::2]) # step=2，即隔一个的子集
print(tp[::-1]) # step=-1，即倒排的子集（倒序）
```

### Dictionary

key/value映射

```python
d = {'key1': 'value1', 'key2': 'value2'}
print(len(d))
print(d['key1'])
d['key3'] = 'value3'
del d['key2']
print(d)
for n in d:
    print(f'{n}={d[n]}')
for v in d.values():
    print(v)
print('key1' in d) # 是否包含key
x = dict() # 空dict
x['key1'] = 'value1'
x = {} # 也是空dict
x['key1'] = 'value1'
```

### Set

无需，去重集合，方法比较符合数学集合的运算

```python
s = set(['aaa', 'bbb', 'ccc'])
s.add('aaa')
print(s)
s.add('ddd')
print(s)
print('aaa' in s)
print(s.intersection(['aaa', 'bbb', 'eee'])) # 交集
print(s.union(['aaa', 'fff'])) # 并集
print(s.difference(['aaa'])) # 差集
print(s.issuperset(['aaa'])) # 是否超集
print(s.issubset(['aaa'])) # 是否子集
```
