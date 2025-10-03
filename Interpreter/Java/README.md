# BS Java 解释器

中文 | [English](README_EN.md)

Java 实现的 BS (Bitwise Subleq) 解释器，具有完整的调试功能和双语支持。

> **语言规范**: 完整的 BS 语言规范请参阅 [项目根目录 README](../../README.md)

## 功能特性

- ✅ 完整的 BS 语言支持
- ✅ 调试模式（单步执行、内存查看）
- ✅ 中英双语界面
- ✅ 从文件或命令行读取程序
- ✅ 详细的错误提示
- ✅ 执行统计信息
- ✅ 可配置的执行限制

## 快速开始

### 编译项目

```bash
cd Interpreter/Java
.\gradlew.bat build     # Windows
./gradlew build         # Linux/Mac
```

### 运行程序

#### 基本用法

```bash
# 从命令行执行比特流
java -cp build\classes\java\main BSMain -e <bitstream>

# 从文件执行
java -cp build\classes\java\main BSMain <filename>

# 调试模式
java -cp build\classes\java\main BSMain -d -e <bitstream>
```

#### 示例

```bash
# 无限循环示例
java -cp build\classes\java\main BSMain -e 000000000000000

# 输出测试
java -cp build\classes\java\main BSMain -e 100010000111111

# 使用测试文件
java -cp build\classes\java\main BSMain test_infinite_loop.bs
java -cp build\classes\java\main BSMain test_output_a.bs
```

## 命令行选项

| 选项 | 说明 |
|------|------|
| `-e <bitstream>` | 直接执行比特流字符串 |
| `-d, --debug` | 启用调试模式（单步执行） |
| `--lang <zh\|en>` | 设置界面语言（默认：zh） |
| `-h, --help` | 显示帮助信息 |
| `<filename>` | 从文件读取并执行程序 |

## 环境变量

| 变量 | 值 | 说明 |
|------|-----|------|
| `BS_LANG` | `zh` / `en` | 设置界面语言 |
| `BS_DEBUG` | `1` | 启用详细调试输出 |
| `BS_VERBOSE` | `1` | 显示执行统计信息 |

**示例:**
```bash
set BS_LANG=en
set BS_DEBUG=1
java -cp build\classes\java\main BSMain -e 000000000000000
```

## 项目结构

```
Interpreter/Java/
├── src/main/java/
│   ├── BitReader.java       # 比特流解析器接口
│   ├── SimpleBitReader.java # 5位块解析实现
│   ├── BSInterpreter.java   # BS 解释器核心
│   ├── BSMain.java          # 命令行入口
│   ├── Lang.java            # 多语言支持
│   └── TestDecoding.java    # 地址解码测试工具
├── test_*.bs                # 测试程序
├── build.gradle             # Gradle 构建配置
└── README.md               # 本文件
```

## 核心组件说明

### BitReader
比特流读取接口，支持按位读取和地址解码。

### SimpleBitReader
实现 5位块的解析逻辑，处理自终止地址编码。

### BSInterpreter
解释器核心，包含：
- 指令解码和执行
- 稀疏内存管理（HashMap）
- I/O 操作处理
- 特殊地址 -1 的处理

### BSMain
命令行界面，提供：
- 参数解析
- 文件读取
- 调试模式控制
- 多语言输出

### Lang
国际化支持，可轻松添加新语言。

## 开发工具

### 地址解码测试工具

用于测试和理解地址编码机制：

```bash
java -cp build\classes\java\main TestDecoding
java -cp build\classes\java\main TestDecoding --lang en
```

该工具会显示各种比特流的解码过程，帮助理解自终止编码。

## 调试模式

启用调试模式后，解释器会：

1. 显示每条指令的解码过程
2. 显示执行前后的内存状态
3. 等待用户按 Enter 继续（单步执行）
4. 显示程序计数器变化

**示例输出:**
```
=== 指令 #1 ===
PC: 0
读取地址 a: 0
读取地址 b: 0
读取地址 c: 0
执行: mem[0] -= mem[0]
结果: mem[0] = 0
条件: 0 <= 0 (真)
跳转到: 0
[按 Enter 继续...]
```

## 性能与限制

### 默认限制

- **最大指令数**: 1,000,000（防止无限循环挂起）
- **内存**: 使用稀疏存储，理论上无限
- **地址范围**: Java int 范围（-2³¹ ~ 2³¹-1）

### 性能考虑

- 稀疏内存使用 HashMap，查找时间 O(1)
- 指令解码采用流式读取，空间效率高
- 适合教学和实验，不适合生产环境

## 常见问题

### 1. 程序不停止

**原因**: 可能陷入无限循环或未设置停机指令。

**解决**: 
- 检查是否有跳转到 -1 的指令
- 使用调试模式观察执行流程
- 程序会在 100 万条指令后自动终止

### 2. 比特流解析错误

**错误信息**: "位流长度必须是5的倍数"

**解决**:
- 检查输入文件是否完整（仅包含 0 和 1）
- 确保比特流长度正确（每条指令至少 15 位）

### 3. 输出乱码

**原因**: 输出的字节值不在可打印 ASCII 范围。

**解决**:
- 检查内存中要输出的值
- 使用 `mem[a] & 0xFF` 确保在 0-255 范围

### 4. 找不到主类

**错误信息**: "Could not find or load main class BSMain"

**解决**:
```bash
# 重新编译
.\gradlew.bat clean build

# 确保使用正确的 classpath
java -cp build\classes\java\main BSMain
```

## 扩展开发

### 添加新语言

编辑 `Lang.java`，添加新的语言代码：

```java
private void loadMessages() {
    if ("fr".equals(lang)) {  // 法语
        messages.put("error.bitstream", "Erreur: ...");
        // ...
    }
}
```

### 自定义内存大小

修改 `BSInterpreter.java` 中的 `MAX_INSTRUCTIONS` 常量。

### 添加新的 I/O 操作

在 `BSInterpreter.java` 的 `executeInstruction()` 方法中扩展特殊地址处理逻辑。

## 测试文件说明

### test_infinite_loop.bs
最简单的无限循环：`000000000000000`

### test_output_a.bs
输出测试程序，演示基本 I/O 操作。

## 构建产物

编译后生成：

- `build/classes/java/main/` - 编译的 .class 文件
- `build/libs/` - JAR 包
- `build/reports/` - 构建报告

## 依赖项

无外部依赖，仅使用 Java 标准库。

**最低要求**: Java 8+

## 贡献

欢迎贡献：

- 🐛 报告 Bug
- ✨ 提出新功能
- 📝 改进文档
- 🚀 性能优化
- 🌍 添加新语言支持

## 相关资源

- **项目主页**: [BS Language](../../README.md)
- **语言规范**: [README.md](../../README.md#语言规范)
- **英文文档**: [README_EN.md](README_EN.md)

## 许可证

见项目根目录 [LICENSE](../../LICENSE) 文件。

---

**提示**: 第一次使用？试试这个命令：
```bash
java -cp build\classes\java\main BSMain -d -e 000000000000000
```
体验调试模式，观察程序如何执行！
