# BS Java 解释器

[**English**](README_EN.md) | 中文

**运行 BS 程序的官方解释器**

> 💡 **什么是 BS？** 请查看 [项目主 README](../../README.md) 了解 BS 语言详情

---

## 📚 目录

- [快速开始](#快速开始)
- [详细使用指南](#详细使用指南)
- [命令行选项完整说明](#命令行选项完整说明)
- [常见问题](#常见问题)
- [从源码编译](#从源码编译)

---

## 快速开始

### 第一步：下载解释器

访问 [GitHub Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases) 页面，下载最新版本的 JAR 文件：

```
Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```

### 第二步：检查 Java 是否已安装

打开命令行，输入：

```bash
java -version
```

**如果显示版本号**（如 `java version "1.8.0"`），说明已安装，跳到第三步。

**如果提示"找不到命令"**，需要先安装 Java：
1. 访问 [java.com](https://www.java.com/)
2. 下载并安装
3. 重启命令行窗口

### 第三步：运行你的第一个程序

在 JAR 文件所在目录，打开命令行，输入：

```bash
# 查看帮助信息
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -h

# 运行一个简单程序（立即停止）
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# 运行回显程序
echo ABC | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000000000010000010000000000010000010000010"
```

🎉 **成功！你已经运行了第一个 BS 程序！**

---

## 详细使用指南

### 基本用法

#### 方式 1：从文件运行

创建一个 `.bs` 文件（比如 `hello.bs`），内容为：
```
000010000010000010
```

然后运行：
```bash
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar hello.bs
```

#### 方式 2：直接输入代码运行

```bash
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "你的代码"
```

**示例**：
```bash
# 立即停机
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# 回显一个字符
echo A | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"
```

### 提供输入数据

BS 程序可能需要输入数据（当 a 段功能位 = 1 时）。有几种方式提供输入：

#### 方式 1：使用管道（推荐）

```bash
echo "你的输入" | java -jar interpreter.jar program.bs
```

**示例**：
```bash
# 输入 ABC，程序会逐字符处理
echo ABC | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000000000010000010000000000010000010000010"
```

**输出**：`ABC`

#### 方式 2：使用文件重定向

```bash
# 先创建输入文件
echo ABC > input.txt

# 使用重定向
java -jar interpreter.jar program.bs < input.txt
```

#### 方式 3：交互式输入（注意行缓冲）

```bash
# 直接运行程序
java -jar interpreter.jar program.bs
# 然后输入内容并按回车
```

⚠️ **注意**：由于终端的**行缓冲特性**，你需要输入完整内容后按回车，程序才会开始处理。

**示例**：
```bash
$ java -jar interpreter.jar echo3.bs
ABC[按回车]
ABC
```

程序会一次性读取 `A`、`B`、`C` 三个字符。

### 调试模式

当你想看程序**如何一步步执行**时，使用调试模式：

```bash
java -jar interpreter.jar -d program.bs
```

**调试模式会显示**：
- ✅ 每条指令的详细信息（a、b、c 段的地址和功能位）
- ✅ 执行前后的内存状态
- ✅ 程序计数器（PC）的变化
- ✅ 输入输出的详细过程

**示例输出**：
```
已加载指令 0: a=0[IN], b=0[OUT], c=0[HALT]
已加载 1 条指令

开始执行 1 条指令...

PC=0, 指令: a=0[IN], b=0[OUT], c=0[HALT]
  执行前: mem[0]=0, mem[0]=0
  输入：读取字节 65 到地址 0
  输出：写入字节 65 ('A') 从地址 0
  停机（c功能位）

执行完成。总指令数：1
```

### 切换语言

解释器默认使用**中文界面**，可以切换为英文：

```bash
# 方式1：使用命令行参数
java -jar interpreter.jar --lang en program.bs

# 方式2：设置环境变量
set BS_LANG=en         # Windows
export BS_LANG=en      # Linux/Mac
```

---

## 命令行选项完整说明

### 基本语法

```bash
java -jar interpreter.jar [选项] <文件名或代码>
```

### 所有选项

| 选项 | 说明 | 示例 |
|------|------|------|
| **无选项** | 运行指定文件 | `java -jar interpreter.jar program.bs` |
| `-e <代码>` | 直接执行代码字符串 | `java -jar interpreter.jar -e "000010000010000010"` |
| `-d <文件>` | 调试模式运行文件 | `java -jar interpreter.jar -d program.bs` |
| `-d -e <代码>` | 调试模式运行代码 | `java -jar interpreter.jar -d -e "000010000010000010"` |
| `-h` 或 `--help` | 显示帮助信息 | `java -jar interpreter.jar -h` |
| `--lang <语言>` | 设置界面语言 | `java -jar interpreter.jar --lang en program.bs` |

### 环境变量

可以通过设置环境变量来控制解释器行为：

| 环境变量 | 作用 | 取值 |
|---------|------|------|
| `BS_LANG` | 界面语言 | `zh`（中文）或 `en`（英文） |
| `BS_DEBUG` | 启用调试输出 | 设置为 `1` 启用 |
| `BS_VERBOSE` | 显示执行统计 | 设置为 `1` 启用 |

**Windows 设置示例**：
```cmd
set BS_LANG=en
set BS_DEBUG=1
java -jar interpreter.jar program.bs
```

**Linux/Mac 设置示例**：
```bash
export BS_LANG=en
export BS_DEBUG=1
java -jar interpreter.jar program.bs
```

---

## 常见问题

### Q1: 提示"找不到 java 命令"

**原因**：你的电脑没有安装 Java，或者环境变量没配置好。

**解决方法**：

1. **安装 Java**：
   - 访问 [java.com](https://www.java.com/)
   - 下载并安装
   
2. **验证安装**：
   ```bash
   java -version
   ```
   
3. **如果还是不行**（Windows）：
   - 右键"此电脑" → 属性 → 高级系统设置 → 环境变量
   - 检查 `Path` 中是否有 Java 的路径（如 `C:\Program Files\Java\jdk-xx\bin`）

### Q2: 程序没有任何输出

**可能原因**：

1. **程序需要输入数据**
   
   解决方法：
   ```bash
   echo ABC | java -jar interpreter.jar program.bs
   ```

2. **程序只执行了计算，没有输出操作**
   
   检查方法：使用调试模式查看程序行为
   ```bash
   java -jar interpreter.jar -d program.bs
   ```

3. **程序立即停止了**（c 段功能位 = 1）
   
   这是正常的，比如 `000010000010000010` 就是立即停止的程序。

### Q3: 为什么输入好像被"合并"了？

**问题描述**：
```bash
$ java -jar interpreter.jar program.bs
123[回车]
输出：123
```

期望程序暂停 3 次分别输入，但实际一次性处理了。

**原因**：这是**终端的行缓冲机制**，不是 bug。

当你输入 `123` 并按回车：
1. 整行内容 `"123\n"` 被放入缓冲区
2. 程序的多次 `read()` 调用会**依次**从缓冲区取字符
3. 不会中途暂停重新询问

**解决方法**：

使用管道一次性提供所有输入：
```bash
echo 123 | java -jar interpreter.jar program.bs
```

### Q4: 如何写一个 BS 程序文件？

**步骤**：

1. **创建文本文件**，扩展名为 `.bs`
2. **输入代码**（纯 0 和 1，可以用空格分隔便于阅读）
3. **保存文件**
4. **运行**

**示例**（创建 `echo3.bs`）：

```
000010000010000000
000010000010000000
000010000010000010
```

保存后运行：
```bash
echo ABC | java -jar interpreter.jar echo3.bs
```

**输出**：`ABC`

### Q5: 调试模式输出太多信息，怎么看不清

**解决方法**：

将调试输出重定向到文件：

```bash
# Windows
java -jar interpreter.jar -d program.bs 2> debug.log

# Linux/Mac
java -jar interpreter.jar -d program.bs 2> debug.log
```

然后用文本编辑器打开 `debug.log` 慢慢查看。

### Q6: 解释器支持什么编码？

解释器**只识别 0 和 1**，其他字符会被忽略（包括空格、换行符等）。

**有效输入**：
```
✅ 000010000010000010
✅ 000010 000010 000010
✅ 0000
    1000
    0100
    0010
```

**无效输入**：
```
❌ 2310  （含有 2 和 3）
❌ O00010（含有字母 O，应该是数字 0）
```

---

## 从源码编译

### 前提条件

- **JDK 8 或更高版本**（注意：JRE 不够，需要 JDK）
- **Gradle**（或使用项目自带的 Gradle Wrapper）

### 编译步骤

#### 方式 1：使用 Gradle Wrapper（推荐）

```bash
# 1. 克隆仓库
git clone https://github.com/MCLMLI/Bitwise-Subleq.git
cd Bitwise-Subleq/Interpreter/Java

# 2. 使用 Gradle Wrapper 编译
# Windows:
.\gradlew.bat clean build

# Linux/Mac:
./gradlew clean build

# 3. 编译完成后，JAR 文件在：
# build/libs/Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```

#### 方式 2：使用已安装的 Gradle

```bash
gradle clean build
```

### 项目结构

```
Interpreter/Java/
├── src/main/java/          # 源代码
│   ├── BSMain.java         # 主入口
│   ├── BSInterpreter.java  # 核心解释器
│   ├── BitReader.java      # 比特流解析器
│   ├── UnbufferedInput.java # 无缓冲输入（JNA）
│   └── Lang.java           # 多语言支持
├── build.gradle            # 构建配置
└── README.md               # 本文档
```

### 依赖说明

本项目使用以下依赖（已在 `build.gradle` 中配置）：

| 依赖 | 版本 | 用途 |
|------|------|------|
| JNA | 5.13.0 | 调用底层系统 API（无缓冲输入） |
| JNA Platform | 5.13.0 | JNA 的平台扩展 |

编译时会自动下载这些依赖。

### 生成的 JAR 说明

编译后生成的 JAR 是 **fat JAR**（包含所有依赖），可以直接运行，无需额外安装库。

---

## 技术特性（给开发者）

<details>
<summary><b>点击展开技术细节</b></summary>

### 1. 无缓冲输入实现

解释器支持基于 JNA 的真正逐字符输入（无需回车）：

**Windows 实现**：
```java
Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);
int handle = kernel32.GetStdHandle(STD_INPUT_HANDLE);

// 禁用行输入和回显
int[] mode = new int[1];
kernel32.GetConsoleMode(handle, mode);
int newMode = mode[0] & ~(ENABLE_LINE_INPUT | ENABLE_ECHO_INPUT);
kernel32.SetConsoleMode(handle, newMode);

// 读取单个字节
int ch = System.in.read();
```

**Linux/Mac 实现**：
```java
CLibrary clib = Native.load("c", CLibrary.class);

// 获取原始终端设置
Termios original = new Termios();
clib.tcgetattr(STDIN_FILENO, original);

// 设置为原始模式
Termios raw = new Termios();
clib.tcgetattr(STDIN_FILENO, raw);
raw.c_lflag &= ~(ICANON | ECHO);  // 禁用行缓冲和回显
raw.c_cc[VMIN] = 1;   // 最少读取 1 字符
raw.c_cc[VTIME] = 0;  // 无超时
clib.tcsetattr(STDIN_FILENO, TCSANOW, raw);

// 读取单个字节
byte[] buffer = new byte[1];
clib.read(STDIN_FILENO, buffer, 1);
```

**效果**：
- 按键立即被读取，无需等待回车
- 程序退出时自动恢复终端设置

### 2. 内存模型

使用 `HashMap<Integer, Integer>` 实现稀疏内存：

```java
private Map<Integer, Integer> memory = new HashMap<>();

// 读取（未初始化地址返回 0）
int readMem(int address) {
    return memory.getOrDefault(address, 0);
}

// 写入
void writeMem(int address, int value) {
    memory.put(address, value);
}
```

**优点**：
- 只存储非零值，节省内存
- 支持任意大的地址空间
- 访问效率 O(1)

### 3. 地址解析算法

使用状态机解析自终止地址：

```java
int address = 0;
while (true) {
    // 读取 4 位数据
    int data = read4Bits();
    
    // 读取功能位和链接位
    int funcBit = readBit();
    int linkBit = readBit();
    
    // 累积地址
    address = (address << 4) | data;
    
    // 检查功能位和链接位
    if (funcBit == 1 && linkBit == 0) {
        hasFunction = true;
    }
    
    if (linkBit == 0) {
        break;  // 地址结束
    }
}
```

### 4. 多语言支持

使用 `Lang.java` 实现动态语言切换：

```java
public class Lang {
    private static String language = "zh";  // 默认中文
    
    public static String get(String zh, String en) {
        return language.equals("en") ? en : zh;
    }
    
    public static void setLanguage(String lang) {
        language = lang;
    }
}
```

使用示例：
```java
System.out.println(Lang.get("错误", "Error"));
```

</details>

---

## 相关链接

- **项目主页**：https://github.com/MCLMLI/Bitwise-Subleq
- **BS 语言完整规范**：[���文版](../../README.md) | [English](../../README_EN.md)
- **问题反馈**：https://github.com/MCLMLI/Bitwise-Subleq/issues
- **版本发布**：https://github.com/MCLMLI/Bitwise-Subleq/releases

---

## 开源协议

本项目使用 [GNU Affero General Public License v3.0](../../LICENSE) 开源。

简单来说：
- ✅ 可以免费使用、学习、修改
- ✅ 可以用于商业项目
- ⚠️ 修改后分发必须开源
- ⚠️ 做成网络服务也必须开源

---

**项目维护者**: MCLMLI  
**最后更新**: 2025-01-04  
**版本**: 1.0

如有问题或建议，欢迎 [提交 Issue](https://github.com/MCLMLI/Bitwise-Subleq/issues)！
