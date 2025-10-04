# BS Java 解释器

[English](README_EN.md) | 中文

Java 实现的 BS (Bitwise Subleq) 解释器，支持新的6位编码格式（4位数据 + 1位功能位 + 1位链接位）。

> 📖 **完整语言规范**: 请参阅 [项目根目录 README](../../README.md)

## 快速开始

### 从源码编译并运行

```bash
cd Interpreter/Java/src/main/java
javac -encoding UTF-8 *.java
java BSMain -e "000010000010000010"    # 测试停机指令
```

### 使用示例

```bash
# 立即停机（c段功能位=1）
java BSMain -e "000010000010000010"

# 调试模式查看详细信息
java BSMain -d -e "000010000010000010"

# 从文件运行
java BSMain program.bs

# 输入输出测试（需要交互）
java BSMain -e "000110000110000010"
```

## 命令行选项

```
用法：
  java BSMain [选项] <文件名>
  java BSMain [选项] -e <比特流>

选项：
  -h, --help              显示帮助信息
  --lang zh|en            设置界面语言（默认：系统语言）
  -e <bitstream>          直接执行比特流字符串
  -d                      启用调试模式（显示详细执行过程）
  <filename>              从文件读取并执行程序
```

## 新编码格式说明

每个地址段由6位组成：`[dddd][s][l]`
- **dddd**: 4位数据
- **s**: 功能位（0=丢弃，1=有效）
- **l**: 链接位（0=结束，1=继续）

### 功能位作用

| 段 | 功能位=1 | 说明 |
|----|---------|------|
| a段 | 输入 | 从标准输入读取1字节到mem[a] |
| b段 | 输出 | 输出mem[b]的低8位到标准输出 |
| c段 | 停机 | 立即终止程序 |

### 编码示例

```
000010 = 0000(数据) 1(功能) 0(链接)
         地址=0, 有功能位

000110 = 0001(数据) 1(功能) 0(链接)
         地址=1, 有功能位

010011 = 0100(数据) 1(功能) 1(链接)
         错误！s=1且l=1，视为无效
```

## 调试模式

启用调试模式会显示：
- 指令加载过程和功能位状态
- 每条指令执行前后的内存状态
- 程序计数器变化
- 输入输出操作详情

```bash
java BSMain -d -e "000010000010000010"
```

输出示例：
```
已加载指令 0: a=0[], b=0[], c=0[HALT]
已加载 1 条指令
开始执行 1 条指令...

PC=0, 指令: a=0, b=0, c=0[HALT]
  执行前: mem[0]=0, mem[0]=0
  停机（c功能位）

执行完成。总指令数：1
```

## 编译JAR包

如果需要生成独立的JAR文件：

```bash
cd Interpreter/Java
.\gradlew.bat build     # Windows
./gradlew build         # Linux/Mac

# JAR文件位置
build/libs/Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```

## 技术特性

- ✅ 支持新的6位编码格式
- ✅ 功能位机制（输入/输出/停机）
- ✅ 稀疏内存实现（HashMap）
- ✅ 详细的调试输出
- ✅ 中英文双语支持
- ✅ 自终止地址解析
- ✅ 错误处理和验证

## 许可证

MIT License - 详见项目根目录的 [LICENSE](../../LICENSE) 文件
