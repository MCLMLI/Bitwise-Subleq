# BS Java 解释器

中文 | [English](README_EN.md) 

Java 实现的 BS (Bitwise Subleq) 解释器，支持新的6位编码格式（4位数据 + 1位功能位 + 1位链接位）。

> 📖 **完整语言规范**: 请参阅 [项目根目录 README](../../README.md)

## 快速开始

### 📦 推荐方式：使用预编译的JAR文件

从 [Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases) 页面下载最新版本的 JAR 文件。

```bash
# 下载后直接运行
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# 查看帮助信息
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar --help

# 从文件运行程序
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar program.bs

# 启用调试模式
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -d -e "000010000010000010"
```

### 使用示例

```bash
# 立即停机（所有段功能位=1，按a→b→c顺序执行）
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# 输入输出测试（需要交互）
# a段输入 → b段输出更新后的内存 → c段停机
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"
# 输入一个字符，程序会立即输出该字符后停机
```

## 命令行选项

```
用法：
  java -jar Bitwise-Subleq-Interpreter-Java-x.x.x.jar [选项] <文件名>
  java -jar Bitwise-Subleq-Interpreter-Java-x.x.x.jar [选项] -e <比特流>

选项：
  -h, --help              显示帮助信息
  --lang zh|en            设置界面语言（默认：系统语言）
  -e <bitstream>          直接执行比特流字符串
  -d                      启用调试模式（显示详细执行过程）
  <filename>              从文件读取并执行程序
```

**⚠️ 重要说明：输入流的行缓冲特性**

从标准输入读取字节流。**标准输入是行缓冲的**，这意味着：

**为什么输入看起来"合并"了？**
```bash
# 交互式运行示例
> java -jar interpreter.jar -f test.bs
> 123[回车]
# 输出: 123

# 你可能期望：程序暂停3次，每次等待1个字符
# 实际发生：当你按回车，整行 "123\n" 进入缓冲区，
#          程序的3次 read() 调用依次读取 '1', '2', '3'，不会中途暂停
```

**原因：**
- 当你输入 `123` 并按回车时，整行（包括换行符）都被放入输入缓冲区
- 程序的每次 `System.in.read()` 调用都是独立的**阻塞调用**
- 但只有在缓冲区**为空**时才会阻塞等待
- 如果缓冲区已有数据，`read()` 会**立即返回**，不会重新询问用户

**示例对比：**

```bash
# 情况1：交互式输入（看起来"合并"）
> bs test.bs
> 2323[回车]
输出: 232
# 解释：'2', '3', '2' 依次从缓冲区读取，第3个字符后停机

# 情况2：管道输入（预期行为）
> echo ABC | bs test.bs
输出: ABC
# 解释：整个字符串在缓冲区，程序依次读取

# 情况3：逐字符输入（需要特殊处理）
> # 在大多数终端中无法实现"每次暂停询问"
> # 因为标准输入是行缓冲的
```

**EOF处理：**
```bash
# 当输入流结束（EOF）时
> bs test.bs
> 1[回车]
输出: 1
# 读取：'1', '\n', EOF（转为0）
# 如果程序期望3个字符，第3次读取会遇到EOF

# 正确方式：使用管道或重定向
> echo ABC | bs test.bs
# 读取：'A', 'B', 'C', '\n', EOF

# Windows下不带换行符
> echo|set /p="ABC" | bs test.bs
# 读取：'A', 'B', 'C', EOF
```

**总结：**
- 输入操作是**阻塞的字节流读取**，这是标准行为
- 输入看起来"合并"是因为**行缓冲**，不是bug

## 调试模式

启用调试模式会显示：
- 指令加载过程和功能位状态
- 每条指令执行前后的内存状态
- 程序计数器变化
- 输入输出操作详情

```bash
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -d -e "000010000010000010"
```

输出示例：
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

## 从源码编译（高级用户）

如果您需要修改源码或从源码构建：

```bash
cd Interpreter/Java/src/main/java
javac -encoding UTF-8 *.java
java BSMain -e "000010000010000010"
```

## 许可证

本项目采用 [GNU Affero General Public License v3.0](../../LICENSE) 开源。

使用本软件即表示您同意遵守 AGPLv3 许可证的所有条款。
