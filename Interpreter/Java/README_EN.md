# BS Java Interpreter

English | [**中文**](README.md)

**The Official Interpreter for Running BS Programs**

> 💡 **What is BS?** Check the [Project Main README](../../README_EN.md) for details about the BS language

---

## 📚 Table of Contents

- [Quick Start](#quick-start)
- [Detailed Usage Guide](#detailed-usage-guide)
- [Complete Command Line Options](#complete-command-line-options)
- [Frequently Asked Questions](#frequently-asked-questions)
- [Building from Source](#building-from-source)

---

## Quick Start

### Step 1: Download the Interpreter

Visit the [GitHub Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases) page and download the latest JAR file:

```
Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```

### Step 2: Check if Java is Installed

Open a command line and type:

```bash
java -version
```

**If it shows a version number** (like `java version "1.8.0"`), you're good to go. Skip to Step 3.

**If it says "command not found"**, you need to install Java first:
1. Visit [java.com](https://www.java.com/)
2. Download and install
3. Restart your command line window

### Step 3: Run Your First Program

In the directory where the JAR file is located, open a command line and type:

```bash
# View help information
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -h

# Run a simple program (immediate halt)
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# Run an echo program
echo ABC | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000000000010000010000000000010000010000010"
```

🎉 **Success! You've run your first BS program!**

---

## Detailed Usage Guide

### Basic Usage

#### Method 1: Run from File

Create a `.bs` file (e.g., `hello.bs`) with content:
```
000010000010000010
```

Then run:
```bash
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar hello.bs
```

#### Method 2: Run Code Directly

```bash
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "your code"
```

**Examples**:
```bash
# Immediate halt
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# Echo one character
echo A | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"
```

### Providing Input Data

BS programs may require input data (when segment a function bit = 1). There are several ways to provide input:

#### Method 1: Using Pipes (Recommended)

```bash
echo "your input" | java -jar interpreter.jar program.bs
```

**Example**:
```bash
# Input ABC, program processes character by character
echo ABC | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000000000010000010000000000010000010000010"
```

**Output**: `ABC`

#### Method 2: Using File Redirection

```bash
# First create an input file
echo ABC > input.txt

# Use redirection
java -jar interpreter.jar program.bs < input.txt
```

#### Method 3: Interactive Input (Note Line Buffering)

```bash
# Run the program directly
java -jar interpreter.jar program.bs
# Then type your input and press Enter
```

⚠️ **Note**: Due to the terminal's **line buffering**, you need to type the complete content and press Enter before the program starts processing.

**Example**:
```bash
$ java -jar interpreter.jar echo3.bs
ABC[press Enter]
ABC
```

The program will read `A`, `B`, `C` all at once.

### Debug Mode

When you want to see **how the program executes step by step**, use debug mode:

```bash
java -jar interpreter.jar -d program.bs
```

**Debug mode shows**:
- ✅ Detailed information for each instruction (addresses and function bits of a, b, c segments)
- ✅ Memory state before and after execution
- ✅ Program counter (PC) changes
- ✅ Detailed input/output process

**Example output**:
```
Loaded instruction 0: a=0[IN], b=0[OUT], c=0[HALT]
Loaded 1 instructions

Starting execution of 1 instructions...

PC=0, Instr: a=0[IN], b=0[OUT], c=0[HALT]
  Before: mem[0]=0, mem[0]=0
  INPUT: read byte 65 to address 0
  OUTPUT: wrote byte 65 ('A') from address 0
  HALT (c function bit)

Execution finished. Total instructions: 1
```

### Language Switching

The interpreter uses **Chinese interface** by default, but you can switch to English:

```bash
# Method 1: Using command line parameter
java -jar interpreter.jar --lang en program.bs

# Method 2: Set environment variable
set BS_LANG=en         # Windows
export BS_LANG=en      # Linux/Mac
```

---

## Complete Command Line Options

### Basic Syntax

```bash
java -jar interpreter.jar [options] <filename or code>
```

### All Options

| Option | Description | Example |
|--------|-------------|---------|
| **No option** | Run specified file | `java -jar interpreter.jar program.bs` |
| `-e <code>` | Execute code string directly | `java -jar interpreter.jar -e "000010000010000010"` |
| `-d <file>` | Run file in debug mode | `java -jar interpreter.jar -d program.bs` |
| `-d -e <code>` | Run code in debug mode | `java -jar interpreter.jar -d -e "000010000010000010"` |
| `-h` or `--help` | Show help message | `java -jar interpreter.jar -h` |
| `--lang <language>` | Set interface language | `java -jar interpreter.jar --lang en program.bs` |

### Environment Variables

You can control interpreter behavior by setting environment variables:

| Variable | Purpose | Values |
|----------|---------|--------|
| `BS_LANG` | Interface language | `zh` (Chinese) or `en` (English) |
| `BS_DEBUG` | Enable debug output | Set to `1` to enable |
| `BS_VERBOSE` | Show execution statistics | Set to `1` to enable |

**Windows example**:
```cmd
set BS_LANG=en
set BS_DEBUG=1
java -jar interpreter.jar program.bs
```

**Linux/Mac example**:
```bash
export BS_LANG=en
export BS_DEBUG=1
java -jar interpreter.jar program.bs
```

---

## Frequently Asked Questions

### Q1: Error "java command not found"

**Reason**: Java is not installed on your computer, or environment variables are not configured correctly.

**Solution**:

1. **Install Java**:
   - Visit [java.com](https://www.java.com/)
   - Download and install
   
2. **Verify installation**:
   ```bash
   java -version
   ```
   
3. **If still not working** (Windows):
   - Right-click "This PC" → Properties → Advanced system settings → Environment Variables
   - Check if `Path` includes Java path (like `C:\Program Files\Java\jdk-xx\bin`)

### Q2: Program Produces No Output

**Possible reasons**:

1. **Program needs input data**
   
   Solution:
   ```bash
   echo ABC | java -jar interpreter.jar program.bs
   ```

2. **Program only performed calculations, no output operations**
   
   Check method: Use debug mode to see program behavior
   ```bash
   java -jar interpreter.jar -d program.bs
   ```

3. **Program halted immediately** (c segment function bit = 1)
   
   This is normal, for example `000010000010000010` is a program that halts immediately.

### Q3: Why Does Input Appear "Merged"?

**Problem description**:
```bash
$ java -jar interpreter.jar program.bs
123[Enter]
Output: 123
```

Expected program to pause 3 times for separate inputs, but actually processed all at once.

**Reason**: This is the **terminal's line buffering mechanism**, not a bug.

When you type `123` and press Enter:
1. The entire line `"123\n"` is placed in the buffer
2. Multiple `read()` calls **sequentially** retrieve characters from the buffer
3. Won't pause mid-way to ask again

**Solution**:

Use pipe to provide all input at once:
```bash
echo 123 | java -jar interpreter.jar program.bs
```

### Q4: How to Write a BS Program File?

**Steps**:

1. **Create a text file** with `.bs` extension
2. **Enter code** (pure 0s and 1s, can use spaces for readability)
3. **Save file**
4. **Run**

**Example** (create `echo3.bs`):

```
000010000010000000
000010000010000000
000010000010000010
```

After saving, run:
```bash
echo ABC | java -jar interpreter.jar echo3.bs
```

**Output**: `ABC`

### Q5: Too Much Debug Output, Can't See Clearly

**Solution**:

Redirect debug output to a file:

```bash
# Windows
java -jar interpreter.jar -d program.bs 2> debug.log

# Linux/Mac
java -jar interpreter.jar -d program.bs 2> debug.log
```

Then open `debug.log` with a text editor to review slowly.

### Q6: What Encoding Does the Interpreter Support?

The interpreter **only recognizes 0 and 1**, other characters are ignored (including spaces, newlines, etc.).

**Valid input**:
```
✅ 000010000010000010
✅ 000010 000010 000010
✅ 0000
    1000
    0100
    0010
```

**Invalid input**:
```
❌ 2310   (contains 2 and 3)
❌ O00010 (contains letter O, should be digit 0)
```

---

## Building from Source

### Prerequisites

- **JDK 8 or higher** (Note: JRE is not enough, need JDK)
- **Gradle** (or use the project's Gradle Wrapper)

### Build Steps

#### Method 1: Using Gradle Wrapper (Recommended)

```bash
# 1. Clone repository
git clone https://github.com/MCLMLI/Bitwise-Subleq.git
cd Bitwise-Subleq/Interpreter/Java

# 2. Build using Gradle Wrapper
# Windows:
.\gradlew.bat clean build

# Linux/Mac:
./gradlew clean build

# 3. After building, JAR file is at:
# build/libs/Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```

#### Method 2: Using Installed Gradle

```bash
gradle clean build
```

### Project Structure

```
Interpreter/Java/
├── src/main/java/          # Source code
│   ├── BSMain.java         # Main entry
│   ├── BSInterpreter.java  # Core interpreter
│   ├── BitReader.java      # Bitstream parser
│   ├── UnbufferedInput.java # Unbuffered input (JNA)
│   └── Lang.java           # Multi-language support
├── build.gradle            # Build configuration
└── README.md               # This document
```

### Dependencies

This project uses the following dependencies (configured in `build.gradle`):

| Dependency | Version | Purpose |
|------------|---------|---------|
| JNA | 5.13.0 | Call native system APIs (unbuffered input) |
| JNA Platform | 5.13.0 | JNA platform extensions |

Dependencies are automatically downloaded during build.

### About the Generated JAR

The compiled JAR is a **fat JAR** (includes all dependencies), can be run directly without additional library installation.

---

## Technical Features (For Developers)

<details>
<summary><b>Click to expand technical details</b></summary>

### 1. Unbuffered Input Implementation

The interpreter supports true character-by-character input based on JNA (no Enter needed):

**Windows implementation**:
```java
Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);
int handle = kernel32.GetStdHandle(STD_INPUT_HANDLE);

// Disable line input and echo
int[] mode = new int[1];
kernel32.GetConsoleMode(handle, mode);
int newMode = mode[0] & ~(ENABLE_LINE_INPUT | ENABLE_ECHO_INPUT);
kernel32.SetConsoleMode(handle, newMode);

// Read single byte
int ch = System.in.read();
```

**Linux/Mac implementation**:
```java
CLibrary clib = Native.load("c", CLibrary.class);

// Get original terminal settings
Termios original = new Termios();
clib.tcgetattr(STDIN_FILENO, original);

// Set to raw mode
Termios raw = new Termios();
clib.tcgetattr(STDIN_FILENO, raw);
raw.c_lflag &= ~(ICANON | ECHO);  // Disable line buffering and echo
raw.c_cc[VMIN] = 1;   // Minimum 1 character
raw.c_cc[VTIME] = 0;  // No timeout
clib.tcsetattr(STDIN_FILENO, TCSANOW, raw);

// Read single byte
byte[] buffer = new byte[1];
clib.read(STDIN_FILENO, buffer, 1);
```

**Effect**:
- Key presses are read immediately, no need to wait for Enter
- Terminal settings automatically restored on program exit

### 2. Memory Model

Uses `HashMap<Integer, Integer>` for sparse memory implementation:

```java
private Map<Integer, Integer> memory = new HashMap<>();

// Read (uninitialized addresses return 0)
int readMem(int address) {
    return memory.getOrDefault(address, 0);
}

// Write
void writeMem(int address, int value) {
    memory.put(address, value);
}
```

**Advantages**:
- Only stores non-zero values, saves memory
- Supports arbitrarily large address space
- O(1) access efficiency

### 3. Address Parsing Algorithm

Uses state machine to parse self-terminating addresses:

```java
int address = 0;
while (true) {
    // Read 4 data bits
    int data = read4Bits();
    
    // Read function bit and link bit
    int funcBit = readBit();
    int linkBit = readBit();
    
    // Accumulate address
    address = (address << 4) | data;
    
    // Check function bit and link bit
    if (funcBit == 1 && linkBit == 0) {
        hasFunction = true;
    }
    
    if (linkBit == 0) {
        break;  // Address ends
    }
}
```

### 4. Multi-language Support

Uses `Lang.java` for dynamic language switching:

```java
public class Lang {
    private static String language = "zh";  // Default Chinese
    
    public static String get(String zh, String en) {
        return language.equals("en") ? en : zh;
    }
    
    public static void setLanguage(String lang) {
        language = lang;
    }
}
```

Usage example:
```java
System.out.println(Lang.get("错误", "Error"));
```

</details>

---

## Related Links

- **Project Home**: https://github.com/MCLMLI/Bitwise-Subleq
- **Complete BS Language Specification**: [中文版](../../README.md) | [English](../../README_EN.md)
- **Issue Tracker**: https://github.com/MCLMLI/Bitwise-Subleq/issues
- **Releases**: https://github.com/MCLMLI/Bitwise-Subleq/releases

---

## License

This project is licensed under [GNU Affero General Public License v3.0](../../LICENSE).

In simple terms:
- ✅ Free to use, study, modify
- ✅ Can be used in commercial projects
- ⚠️ Modified versions must be open sourced if distributed
- ⚠️ Network services must also be open sourced

---

**Project Maintainer**: MCLMLI  
**Last Updated**: 2025-01-04  
**Version**: 1.0

Questions or suggestions? Feel free to [submit an Issue](https://github.com/MCLMLI/Bitwise-Subleq/issues)!
