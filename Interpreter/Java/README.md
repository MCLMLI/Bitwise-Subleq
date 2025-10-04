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

## 新编码格式说明

每个地址段由6位组成：`[dddd][s][l]`
- **dddd**: 4位数据（总是累积到地址值）
- **s**: 功能位（不作为数据，仅标记功能）
- **l**: 链接位（不作为数据，控制是否继续）

### 功能位作用

| 段 | 功能位=1 | 说明 |
|----|---------|------|
| a段 | 输入 | 从标准输入读取1字节到mem[a] |
| b段 | 输出 | 输出mem[b]的低8位到标准输出 |
| c段 | 停机 | 立即终止程序 |

**重要**: 当一条指令的多个段都有功能位时，按 **a → b → c** 的顺序依次执行。

### 编码示例

```
000010 = 0000(数据) 1(功能) 0(链接)
         地址=0, 有功能位

000110 = 0001(数据) 1(功能) 0(链接)
         地址=1, 有功能位

000111 = 0001(数据) 1(功能) 1(链接)
         错误！s=1且l=1，视为无功能位，继续读取下一段
```

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
