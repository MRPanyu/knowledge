# Linux离线安装ollama及模型

我使用的环境是Ubuntu 24（Ubuntu MATE 24发行版），其他比较新的Linux发行版应该也是类似命令。

Ollama用于本机启动开源LLM。

## 参考

<https://zhuanlan.zhihu.com/p/1887585692919522766>

## Linux离线安装ollama

### 下载

<https://github.com/ollama/ollama/releases>

展开所有内容，找对应系统版本的包，例如我用的是x86的linux，因此是`ollama-linux-amd64.tgz`

### 安装

把压缩包解压到/usr目录

```sh
sudo tar -C /usr -xzf ollama-linux-amd64.tgz
```

增加文件执行权限

```sh
sudo chmod +x /usr/bin/ollama
```

创建ollama用户和组

```sh
sudo useradd -r -s /bin/false -m -d /usr/share/ollama ollama
```

参数解释：

- `-r`：创建系统用户。
- `-s /bin/false`：禁止该用户登录。
- `-d /usr/share/ollama`：指定用户主目录（存放模型文件）

创建一个`/etc/systemd/system/ollama.service`文件，如果vi工具不太好用的话，用记事本或者其他工具也行

```sh
sudo vi /etc/systemd/system/ollama.service
```

这个文件的内容如下

```ini
[Unit]
Description=Ollama Service
After=network-online.target

[Service]
ExecStart=/usr/bin/ollama serve
User=ollama
Group=ollama
Restart=always
RestartSec=3
#自定义端口
Environment="OLLAMA_HOST=0.0.0.0:11434"
#代表让ollama能识别到第几张显卡
#Environment="CUDA_VISIBLE_DEVICES=0,1"
#这几张卡均衡使用
#Environment="OLLAMA_SCHED_SPREAD=1" 
#模型一直加载, 不自动卸载
#Environment="OLLAMA_KEEP_ALIVE=-1" 
#配置跨域请求
Environment="OLLAMA_ORIGINS=*"
#配置OLLAMA的模型存放路径，默认路径是/usr/share/ollama/.ollama/models/
Environment="OLLAMA_MODELS=/home/data/.ollama/models"

[Install]
WantedBy=default.target
```

- OLLAMA_HOST和OLLAMA_ORIGINS开放远程访问，比如依赖docker的平台，如dify/coze等要这样开放了才能访问到
- OLLAMA_MODELS用于指定模型目录

模型目录要创建出来并给ollama用户访问权限

```sh
sudo mkdir -p /home/data/.ollama/models
sudo chown -R ollama:ollama /home/data/.ollama
```

然后刷新下系统服务缓存，这里直接enable把其设为开机自启，否则每次使用前需要`systemctl start ollama`才能用

```sh
sudo systemctl daemon-reload
sudo systemctl enable ollama
sudo systemctl start ollama
```

之后可以简单命令验证一下

```sh
ollama --version
```

## 安装模型

在线的情况，直接`ollama run 模型名称`，ollama就会自己去官网拉取模型了，这里主要介绍离线方案。

首先要下载模型文件，我是在modelscope上搜的:

<https://modelscope.cn/models?name=GGUF%20Deepseek&page=1&tabKey=task>

要搜GGUF格式的模型文件，我找的是DeepSeek R1的一个通义千问蒸馏版7B模型（蒸馏模型一般体积比较小但功能还不错）

<https://modelscope.cn/models/unsloth/DeepSeek-R1-Distill-Qwen-7B-GGUF>

把“模型文件”里面某个模型GGUF文件下载下来，比如我用来验证的是Q4_K_M那个。下载后本地有一个4.3GB的`DeepSeek-R1-Distill-Qwen-7B-Q4_K_M.gguf`文件，把它放到Linux里面，找一个专用的空目录（我用的/home/appuser/model）放在下面。

然后在同一个目录下新建一个 `Modelfile` 文件（没有扩展名），文件内容如下

```txt
FROM ./DeepSeek-R1-Distill-Qwen-7B-Q4_K_M.gguf
```

然后让ollama加载模型

```sh
ollama create deepseek-r1 -f ./Modelfile
ollama list
```

里面deepseek-r1是我们自己给模型起的名字。可以看到ollama已经加载了模型，实际ollama把模型文件转换变成了自己识别的格式，并放在了ollama的模型文件目录下（如果按前文的步骤是`/home/data/.ollama/models`）。之后gguf文件和Modelfile应该可以删除解约空间。

可以用这个命令启动模型

```sh
ollama run deepseek-r1
```

启动后会进入一个命令行，可以输入对话信息，输入 `/bye` 退出命令行，但并没有结束模型实例。用 `ollma ps` 命令可以看哪些模型在运行，用 `ollama stop deepseek-r1` 可以把这个模型停掉。
