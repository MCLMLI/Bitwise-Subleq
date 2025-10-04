# BS Java 解释器

中文 | [**English**](README_EN.md)

**运行 BS 程序的官方解释器**

> 💡 **什么是 BS？** 请查阅 [项目主 README](../../README.md)

---

## 📌 约定
文中使用 `<jar>` 代表已下载的可执行文件：
```
<jar> = Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```
如果你自行重命名，请替换为你的文件名。

---
## 📚 目录
- [快速开始](#快速开始)
- [详细使用指南](#详细使用指南)
- [命令行选项完整说明](#命令行选项完整说明)
- [环境变量与优先级](#环境变量与优先级)
- [常见问题](#常见问题)
- [从源码编译](#从源码编译)
- [技术特性（开发者）](#技术特性给开发者)

---
## 快速开始
### 第一步：下载
前往 [Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases) 下载 `<jar>`。

### 第二步：确认 Java
```bash
java -version
```
未安装请到 https://www.java.com 安装（JDK 或 JRE 均可）。

### 第三步：运行示例
```bash
# 查看帮助
java -jar <jar> -h

# 示例 1：纯停机程序（不输入 / 不输出 / 直接结束）
# 指令：a=0(无功能) b=0(无功能) c=0(功能=HALT)
# 位流：000000 000000 000010  → 连续：000000000000000010
java -jar <jar> -e 000000000000000010

# 示例 2：输入一个字节→输出该字节→停机（不是“空”停机）
# 位流：000010 000010 000010  → 连续：000010000010000010
# 若没有通过管道提供输入，会等待键盘输入一个字符
echo A | java -jar <jar> -e 000010000010000010

# 示例 3：连续回显 3 个字符然后停机
echo ABC | java -jar <jar> -e 000010000010000000000010000010000000000010000010000010
```

---
## 详细使用指南
### 1. 从文件运行
创建 `echo1.bs`：
```
000010000010000010
```
运行：
```bash
echo Z | java -jar <jar> echo1.bs
```

### 2. 直接传入比特流
```bash
echo X | java -jar <jar> -e 000010000010000010
```

### 3. 提供输入的方式
| 场景 | 推荐方式 | 说明 |
|------|----------|------|
| 一次性固定输入 | `echo ABC | java -jar <jar> ...` | 最简方式 (Linux/Mac OK, Windows 会附带换行) |
| Windows 去除换行 | `cmd /c set /p=ABC<nul | java -jar <jar> ...` | 不会追加 `\r\n` |
| 使用文件 | `java -jar <jar> prog.bs < input.txt` | 适合长输入 |
| 交互式 | `java -jar <jar> prog.bs` | 受行缓冲影响，回车后整行被消耗 |

> Windows 上 `echo ABC` 实际发送的是 `A B C \r \n`，回显程序会先输出 `A B C`，剩下的 `\r\n` 可能被后续指令读到；如需精确控制请使用 *set /p* 技巧。

### 4. 调试模式
```bash
java -jar <jar> -d example.bs
java -jar <jar> -d -e 000010000010000010
```
或设置环境变量：`BS_DEBUG=1`（等价于添加 -d）。

调试输出包含：
- 每条指令解析（含功能位标记）
- 执行前后关键内存值
- 输入 / 输出动作
- PC 变化

### 5. 真·纯停机 vs “输入+输出+停机”
| 位流 | 描述 | 行为 |
|------|------|------|
| `000000000000000010` | a,b 段无功能；c 段 HALT | 立即结束，无阻塞 |
| `000010000010000010` | a=IN b=OUT c=HALT | 阻塞读 1 字节 → 输出 → 停机 |

---
## 命令行选项完整说明
```bash
java -jar <jar> [--lang zh|en] [-d] [-e <bitstream>] <filename>
```
| 形式 | 说明 | 备注 |
|------|------|------|
| `<filename>` | 从文件读取比特流 | 与 -e 互斥（除 -d 组合）|
| `-e <bitstream>` | 直接执行位串 | 仅包含 0/1/空白 |
| `-d` | 调试模式 | 可与 -e 或文件组合 |
| `--lang zh|en` | 覆盖界面语言 | 优先级高于 BS_LANG |
| `-h/--help` | 打印用法 | 不执行程序 |

> 若同时设置 `BS_VERBOSE=1`，将自动启用调试并在结束打印汇总。

---
## 环境变量与优先级
| 变量 | 作用 | 优先级 | 说明 |
|------|------|--------|------|
| `BS_LANG` | 默认界面语言 | 低 | 被 `--lang` 覆盖 |
| `BS_DEBUG` | 开启调试输出 | 中 | 等价命令行添加 `-d` |
| `BS_VERBOSE` | 调试 + 结束统计 | 高 | 蕴含 BS_DEBUG 行为 |

优先级：命令行参数 > 环境变量（同一功能时）。

---
## 常见问题
### Q1 运行后卡住不动？
程序可能在等待 **输入功能位 (a=IN)**，请通过管道或键盘输入一个字符。

### Q2 想只停机，哪个位流最短？
使用：`000000000000000010` （或分段写：`000000 000000 000010`）。

### Q3 为什么我输入了多字符程序没有“逐次询问”？
终端使用**行缓冲**：整行进入缓冲区，解释器每次 `read()` 直接消耗一个字节直到缓冲空。若需要逐键即时响应：
- 使用本解释器 + JNA 无缓冲模式（已内置）并直接键入，不输入回车（在某些终端仍可能被行模式限制）
- 或自行实现 GUI / 专用控制台。

### Q4 Windows 下换行问题？
`echo ABC` 产生 `A B C \r \n`；若不希望多出的 `\r`：
```cmd
cmd /c set /p=ABC<nul | java -jar <jar> -e 000010000010000010
```

### Q5 如何确认是 fat JAR？
```bash
jar tf <jar> | grep jna
```
能看到 `com/sun/jna/` 目录即已内嵌依赖。

### Q6 如何定位错误位流？
- 添加 `-d` 观察指令解析是否“过少”或“过早 HALT”
- 检查是否出现非法组合：功能位=1 且链接位=1 → 功能位被忽略，地址继续扩展
- 在长位流中按每 6 位插入空格便于人工核对。

### Q7 输出乱码？
当前输出按单字节直写（`value & 0xFF`），请确保输入数据是 ASCII 或自己管理编码。

---
## 从源码编译
```bash
git clone https://github.com/MCLMLI/Bitwise-Subleq.git
cd Bitwise-Subleq/Interpreter/Java
# Windows
gradlew.bat clean build
# Linux / macOS
./gradlew clean build
ls build/libs
```
生成：`build/libs/Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar`

### 项目结构（节选）
```
src/main/java/
  BSMain.java          # 入口 / 参数处理 / usage
  BSInterpreter.java   # 指令循环与功能位执行
  BitReader.java       # 自终止地址解码
  UnbufferedInput.java # JNA 无缓冲输入（Windows / POSIX）
  Lang.java            # 简易多语言
```

---
## 技术特性（给开发者）
<details><summary>展开 / 收起</summary>

### 指令功能位执行顺序
若任意功能位存在：严格按 a(IN) → b(OUT) → c(HALT)。执行后 **不进入 Subleq 算术**，PC++。

### 性能 / 限制
| 项目 | 当前行为 |
|------|----------|
| 最大指令执行数 | 1,000,000（超出即警告并停止） |
| 内存策略 | 稀疏 HashMap，未写入默认为 0 |
| 值范围 | Java `int`（可为负） |
| 输入 | 单字节，EOF→0 |
| 输出 | 低 8 位直接写出 |
| 线程安全 | 否（单线程设计） |
| 依赖 | JNA / JNA-Platform |

### 可能的改进方向
- 增加内存转储与加载（快照）
- 增加步进 / 断点调试接口（例如 WebSocket 或 REPL）
- 增加统计（功能位触发次数）

</details>

---
## 许可证
本项目遵循 **AGPL-3.0**：分发修改版或通过网络提供服务需开放源代码。详情见 [LICENSE](../../LICENSE)。

---
**维护者**: MCLMLI  
**最后更新**: 2025-01-04  
**版本**: 1.0  
欢迎反馈：<https://github.com/MCLMLI/Bitwise-Subleq/issues>
