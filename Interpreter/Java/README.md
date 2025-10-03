# BS Java 解释器

[English](README_EN.md) | 中文

Java 实现的 BS (Bitwise Subleq) 解释器。

> 📖 **完整语言规范**: 请参阅 [项目根目录 README](../../README.md) 

## 快速开始

### 方式一：使用预编译的 JAR 文件（推荐）

从 [Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases) 下载 JAR 文件后直接运行：

```bash
# 显示帮助
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar --help

# 执行程序
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e 000000000000000

# 从文件运行
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar program.bs

# 调试模式
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -d -e 000000000000000
```

### 方式二：从源码构建

```bash
cd Interpreter/Java
.\gradlew.bat build     # Windows
./gradlew build         # Linux/Mac

# 运行
java -jar build\libs\Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e <bitstream>
```

## 命令行选项

```
用法：
  java -jar <jar文件> [选项] <文件名>
  java -jar <jar文件> [选项] -e <比特流>

选项：
  -h, --help              显示帮助信息
  --lang zh|en            设置界面语言（默认：zh）
  -e <bitstream>          直接执行比特流字符串
  -d                      启用调试模式（单步执行）
  <filename>              从文件读取并执行程序
```

## 使用示例

### 基本用法

```bash
# 无限循环示例
java -jar bs-interpreter.jar -e 000000000000000

# 输出测试
java -jar bs-interpreter.jar -e 100010000111111

# 从文件运行
java -jar bs-interpreter.jar test_program.bs
```

### 调试模式

```bash
# 单步执行，查看每条指令的详细信息
java -jar bs-interpreter.jar -d -e 000000000000000
```

调试模式会显示：
- 每条指令的解码过程
- 执行前后的内存状态
- 程序计数器变化
- 每步等待用户按 Enter 继续

### 多语言支持

```bash
# 使用英文界面
java -jar bs-interpreter.jar --lang en -e 000000000000000

# 使用环境变量设置语言
set BS_LANG=en                           # Windows
export BS_LANG=en                        # Linux/Mac
java -jar bs-interpreter.jar -e 000000000000000
```

## 环境变量

| 变量 | 值 | 说明 |
|------|-----|------|
| `BS_LANG` | `zh` / `en` | 设置界面语言（默认：zh） |
| `BS_DEBUG` | `1` | 启用详细调试输出 |
| `BS_VERBOSE` | `1` | 显示执行统计信息 |

## 功能特性

- ✅ 完整的 BS 语言支持
- ✅ 单步调试模式
- ✅ 中英双语界面
- ✅ 文件和命令行输入
- ✅ 详细的错误提示
- ✅ 执行统计信息
- ✅ 可执行 JAR 文件

## 开发工具

### 地址解码测试工具

用于理解 BS 的自终止地址编码机制：

```bash
java -cp build\classes\java\main TestDecoding
java -cp build\classes\java\main TestDecoding --lang en
```

## 系统要求

- **Java**: 8 或更高版本
- **依赖**: 无外部依赖
- **平台**: Windows / Linux / macOS

## 项目结构

```
Interpreter/Java/
├── src/main/java/
│   ├── BitReader.java       # 比特流解析器接口
│   ├── SimpleBitReader.java # 5位块解析实现
│   ├── BSInterpreter.java   # BS 解释器核心
│   ├── BSMain.java          # 命令行入口
│   ├── Lang.java            # 多语言支持
│   └── TestDecoding.java    # 地址解码测试
├── build.gradle             # Gradle 构建配置
├── README.md               # 本文件
└── README_EN.md            # 英文文档
```

## 常见问题

**Q: 程序不停止怎么办？**  
A: 程序会在执行 100 万条指令后自动停止。使用调试模式 `-d` 可以观察执行流程。

**Q: 如何输出字符？**  
A: 使用特殊地址 -1 作为输出目标，详见[语言规范](../../README.md)。

**Q: 比特流格式错误？**  
A: 确保输入只包含 0 和 1，且长度是 5 的倍数。

## 许可证

本项目采用与主项目相同的许可证。详见 [LICENSE](../../LICENSE)。
