# BS (Bitwise Subleq)

English | [**中文**](README.md)

**A Minimalist Programming Language That Can Write Any Program Using Only 0 and 1**

---

## 📚 Table of Contents

- [What is BS?](#what-is-bs)
- [Quick Start (5 Minutes)](#quick-start-5-minutes)
- [Understanding BS Language](#understanding-bs-language)
- [Complete Language Specification](#complete-language-specification)
- [Frequently Asked Questions](#frequently-asked-questions)
- [License](#license)

---

## What is BS?

### For Complete Beginners

Imagine Morse code — it uses only two symbols ("dot" and "dash") to express all letters. BS language takes this idea even further: **it uses only 0 and 1 to write any computer program**!

For example:
- Calculator programs
- Word processors
- Even operating systems

All can be written using just a string of 0s and 1s (though they'd be extremely long).

### For Programmers

BS (Bitwise Subleq) is a Turing-complete programming language based on **One-Instruction Set Computer (OISC)** architecture:

- **Character Set**: Only `{0, 1}` two symbols
- **Instruction**: Only Subleq (subtract and conditional jump)
- **Encoding**: Self-terminating variable-length address encoding
- **Memory**: Sparse memory model (HashMap implementation)

This is an experimental language for studying the **essence of computation**, proving that even with the simplest elements, you can build a complete computational system.

---

## Quick Start (5 Minutes)

### Step 1: Download the Interpreter

1. Visit the [Releases Page](https://github.com/MCLMLI/Bitwise-Subleq/releases)
2. Download the `Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar` file
3. Save it to your computer (any folder)

### Step 2: Install Java

If you don't have Java installed:
- Visit [java.com](https://www.java.com/)
- Download and install Java
- After installation, open command line and type `java -version` to verify

### Step 3: Run Your First Program

Open command line (Windows: press `Win+R` and type `cmd`, Mac/Linux: open Terminal), then type:

```bash
# Program 1: Immediate halt (simplest program)
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"
```

**Result**: Program ends immediately, doing nothing.

```bash
# Program 2: Echo (repeat your input)
echo ABC | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000000000010000010000000000010000010000010"
```

**Result**: Screen displays `ABC`

🎉 **Congratulations! You've successfully run a BS program!**

> 💡 See more commands: `java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -h`

---

## Understanding BS Language

### 1. Basic Structure of BS Programs

A BS program is just a string of 0s and 1s, like:
```
000010 000010 000010
```

This string is divided into **three segments** (spaces are just for readability, they can be connected):
- First segment `000010` - we call it **segment a**
- Second segment `000010` - we call it **segment b**
- Third segment `000010` - we call it **segment c**

These three segments together form one **instruction**.

### 2. What Does Each Segment Mean?

Each segment is 6 bits (6 zeros or ones), divided into three parts:

```
0 0 0 0 | 1 | 0
└─data─┘ └func┘ └link┘
  4 bits  1 bit  1 bit
```

#### 2.1 Data Bits (First 4 Bits)

This is an **address**, telling the program which "cell" to read/write data.

Imagine memory as a row of cells:
```
Cell Number:  0    1    2    3    4  ...
Content:    [42] [0]  [65] [0]  [0] ...
```

The first 4 bits `0000` means address 0, `0001` means address 1, and so on.

**Examples**:
- `0000` = address 0
- `0001` = address 1
- `0010` = address 2
- `1111` = address 15

#### 2.2 Function Bit (5th Bit)

This bit is special, determining if the segment has a **special function**:
- `0` = no special function
- `1` = has special function (what function depends on whether it's a, b, or c)

#### 2.3 Link Bit (6th Bit)

This bit determines if the address **continues**:
- `0` = segment ends
- `1` = read next 6-bit block (for representing large addresses)

### 3. Special Functions of Three Segments

When function bit = 1, the three segments have different superpowers:

| Segment | Superpower when function bit=1 | Plain English |
|---------|--------------------------------|---------------|
| **Segment a** | 🎤 **Input** | Wait for you to input a character, save to specified address |
| **Segment b** | 📢 **Output** | Display contents of specified address on screen |
| **Segment c** | 🛑 **Halt** | Program stops running |

### 4. Complete Example Analysis

#### Example 1: Immediate Halt Program

```
000010 000010 000010
```

**Segment-by-segment analysis**:

1. **Segment a** `000010`:
   - Data `0000` = address 0
   - Function `1` = has input function
   - Link `0` = segment ends

2. **Segment b** `000010`:
   - Data `0000` = address 0
   - Function `1` = has output function
   - Link `0` = segment ends

3. **Segment c** `000010`:
   - Data `0000` = address 0
   - Function `1` = has halt function ⚠️
   - Link `0` = segment ends

**Execution process**:
1. Segment a: Wait for input character, save to address 0
2. Segment b: Output contents of address 0 (the just-input character)
3. Segment c: **Stop program** ✋

**Running effect**:
```bash
$ echo A | java -jar interpreter.jar -e "000010000010000010"
A
```
Input letter A, program immediately outputs A and stops.

#### Example 2: Echo 3 Characters Continuously

```
000010000010000000 000010000010000000 000010000010000010
└───Instruction 1──┘ └───Instruction 2──┘ └───Instruction 3──┘
```

- **Instruction 1**: Input → Output → Continue
- **Instruction 2**: Input → Output → Continue
- **Instruction 3**: Input → Output → Halt

**Running effect**:
```bash
$ echo ABC | java -jar interpreter.jar -e "program code"
ABC
```

### 5. Representing Large Addresses

If you need to access larger addresses (beyond 15), you can use the **link bit** to connect multiple 6-bit blocks:

```
000111 001110
└─block1┘ └─block2┘
```

**Block 1** `000111`:
- Data `0001` = 1
- Function `1`
- Link `1` = ⚠️ Continue to next block

**Block 2** `001110`:
- Data `0011` = 3
- Function `1`
- Link `0` = End

**Final address** = (1 × 16) + 3 = **19**

> 📖 **Rule**: When function bit=1 AND link bit=1, it's an error condition, treated as function bit=0, but data still accumulates.

### 6. What Happens Without Function Bits?

If all three segments (a, b, c) of an instruction have **no function bits** (all 0s), then the standard **Subleq instruction** executes:

```python
# Subleq instruction pseudocode
mem[b] = mem[b] - mem[a]  # Subtract value at address a from value at address b
if mem[b] <= 0:
    Jump to address c and continue execution
else:
    Continue to next instruction
```

**Plain English**:
1. Subtract the number at address a from the number at address b
2. If result ≤ 0, jump to address c
3. Otherwise continue to next instruction

This is BS language's **only computational instruction**! All complex calculations must be implemented by combining this instruction.

---

## Complete Language Specification

<details>
<summary><b>📖 Click to expand complete technical specification (for advanced users)</b></summary>

### Core Features Summary

| Feature | Description |
|---------|-------------|
| Character Set | `{0, 1}` - Pure binary |
| Minimum Unit | bit |
| Lexical Unit | 6-bit block `[dddd][s][l]` |
| Instruction Set | Single instruction (Subleq) |
| Memory Model | Sparse memory (HashMap) |
| Turing Complete | ✅ Yes |

### 1. Complete Address Encoding Rules

#### 1.1 Single-Block Address

```
Format: [dddd][s][l]
         │    │  └─ Link bit (0=end, 1=continue)
         │    └──── Function bit (0=no function, 1=has function)
         └───────── 4-bit data (address value)
```

**Encoding Table**:

| Binary | Decimal Address | Has Function? |
|--------|----------------|---------------|
| `000000` | 0 | ❌ |
| `000010` | 0 | ✅ |
| `000100` | 1 | ❌ |
| `000110` | 1 | ✅ |
| `111100` | 15 | ❌ |
| `111110` | 15 | ✅ |

#### 1.2 Multi-Block Addresses (Large Addresses)

When representing addresses ≥ 16, use multiple blocks:

```
address = 0
for each 6-bit block:
    address = (address << 4) | data_bits
```

**Example: Address 255**

```
Step 1: 11110 (15, link=1) → address = 15
Step 2: 11110 (15, link=0) → address = 15×16 + 15 = 255
```

#### 1.3 Function Bit and Link Bit Combination Rules

| Function Bit | Link Bit | Result |
|-------------|----------|--------|
| `0` | `0` | ✅ No function, segment ends |
| `0` | `1` | ✅ No function, continue to next block |
| `1` | `0` | ✅ Has function, segment ends |
| `1` | `1` | ⚠️ **Error**, treated as no function, continue |

**Important**: `s=1 AND l=1` is an illegal combination, treated as `s=0, l=1`.

### 2. Instruction Execution Flow

#### 2.1 Complete Execution Order

```
1. Read segment a address and function bit
2. Read segment b address and function bit
3. Read segment c address and function bit

4. Check function bits in order:
   if (a_func):
       mem[a] = read 1 byte from standard input
   
   if (b_func):
       output low 8 bits of mem[b] to standard output
   
   if (c_func):
       halt
       return

5. If any function bit triggered:
   PC = PC + 1
   return

6. Otherwise execute Subleq instruction:
   mem[b] = mem[b] - mem[a]
   if (mem[b] <= 0):
       PC = c
   else:
       PC = PC + 1
```

#### 2.2 Key Characteristics

- **Atomicity**: All function bits of one instruction execute at the same moment
- **Sequential**: a → b → c checked in order
- **Mutual Exclusion**: When function bits exist, Subleq operation doesn't execute

### 3. Memory Model

```java
// Java implementation
Map<Integer, Integer> memory = new HashMap<>();

int read(int address) {
    return memory.getOrDefault(address, 0);
}

void write(int address, int value) {
    memory.put(address, value);
}
```

**Properties**:
- All addresses default to 0
- Supports arbitrary positive integer addresses
- Values are 32-bit signed integers

### 4. Program Counter (PC)

- **Initial Value**: 0
- **Normal Increment**: `PC = PC + 1`
- **Conditional Jump**: When `mem[b] ≤ 0`, `PC = c`
- **Halt Conditions**:
  - c segment function bit = 1
  - PC exceeds program bounds
  - Execution count exceeds limit (default 1,000,000)

### 5. Input/Output Details

#### 5.1 Input (segment a function bit = 1)

```
Behavior: mem[a] = getchar()
```

- Reads **1 byte** from standard input
- Blocking read (waits for input)
- Returns 0 on EOF

**Terminal Line Buffering Characteristic**:

Most terminals are **line-buffered**:
```
User action: Type "ABC" + Enter
Buffer: ['A', 'B', 'C', '\n']
Program behavior: 4 consecutive read() calls return these 4 characters sequentially
```

**Solutions**:
```bash
# Method 1: Use pipe
echo ABC | java -jar interpreter.jar program.bs

# Method 2: Use redirection
java -jar interpreter.jar program.bs < input.txt
```

#### 5.2 Output (segment b function bit = 1)

```
Behavior: putchar(mem[b] & 0xFF)
```

- Outputs **low 8 bits** of `mem[b]`
- Immediately flushes buffer
- Corresponds to ASCII character

**Examples**:
```
mem[5] = 65  → outputs 'A'
mem[5] = 72  → outputs 'H'
mem[5] = 256 → outputs '\0' (256 & 0xFF = 0)
```

#### 5.3 Unbuffered Input (Advanced Feature)

Java interpreter supports true character-by-character input based on JNA:

**Windows**:
```c
SetConsoleMode(hStdin, mode & ~(ENABLE_LINE_INPUT | ENABLE_ECHO_INPUT));
```

**Linux/Mac**:
```c
struct termios new_termios = old_termios;
new_termios.c_lflag &= ~(ICANON | ECHO);
tcsetattr(STDIN_FILENO, TCSANOW, &new_termios);
```

**Effects**:
- ✅ Immediate response on key press, no Enter needed
- ✅ Automatically restores terminal on program exit
- ⚠️ Requires fat JAR with JNA

### 6. Complete Example Programs

#### Example 1: Infinite Echo

```
# Pseudo-assembly representation
loop:
    000010 000010 000000  # Input to address 0, output address 0, jump to self
```

Actual binary (loop pointing to itself requires precise address calculation) is more complex.

#### Example 2: Output Preset Character

Assuming address 0 already contains ASCII value 72 (letter 'H'):

```
000000 000010 000010
│      │      └─ c segment: halt
│      └──────── b segment: output address 0
└───────────── a segment: address 0, no function
```

**Execution**:
1. No a function bit, skip
2. b function bit=1, output contents of address 0 ('H')
3. c function bit=1, halt

**Output**: `H`

</details>

---

## Frequently Asked Questions

### Q1: Why does input appear "merged"?

**Problem Description**:
```bash
$ java -jar interpreter.jar program.bs
123[Enter]
Output: 123
```
Expected to pause 3 times, but read everything at once.

**Reason**: This is the terminal's **line buffering** characteristic, not a bug.

When you type `123` and press Enter, the entire line `"123\n"` enters the buffer. The program's 3 read() calls sequentially retrieve `'1'`, `'2'`, `'3'` from the buffer without pausing mid-way.

**Solution**:
```bash
# Use pipe to provide input all at once
echo 123 | java -jar interpreter.jar program.bs
```

### Q2: Can BS really write any program?

**Answer**: Theoretically yes!

BS is based on the **Subleq** instruction, a mathematically proven **Turing-complete** architecture. This means it can compute any "computable" function, including:
- ✅ Calculators
- ✅ Text editors
- ✅ Compilers
- ✅ Operating systems
- ✅ Games

But actually writing them would be **extremely complex**, because you only have subtraction and jump operations.

### Q3: What's the practical use of BS?

**Main Uses**:

1. **Education**: Understanding fundamental computer principles
   - Learn what "Turing complete" means
   - Understand instruction set architecture
   - Study compiler principles

2. **Research**: Exploring computational limits
   - Minimal instruction set research
   - Coding theory
   - Complexity theory

3. **Challenge**: Mental training for programmers
   - Esoteric programming community
   - Code Golf competitions

4. **Art**: Code as art
   - Creating complex systems from simplest elements

### Q4: How to write complex BS programs?

**Direct hand-writing**: Nearly impossible ❌

**Recommended Methods**:

1. **Write an Assembler**
   ```
   Design a higher-level BS assembly language
   ↓
   Write a program to translate assembly to BS binary
   ```

2. **Write a Compiler**
   ```
   Using C/Python/Java etc.
   ↓
   Write a compiler to generate BS code
   ```

3. **Use Code Generator**
   ```python
   # Python example
   def gen_output(char_code, addr=0):
       return f"{'0'*4}0{addr:04b}0 {char_code:04b}10 000010"
   ```

**Contributions of tools to this project are welcome!**

### Q5: Why is it called "Bitwise Subleq"?

- **Bitwise**: Because programs are pure binary bitstreams
- **Subleq**: Subtract and branch if Less than or Equal

The name directly describes the two major characteristics of the language.

### Q6: How does it compare to other Esoteric languages?

| Language | Character Set | Instructions | Features |
|----------|--------------|-------------|----------|
| **BS** | 2 | 1 | Minimalist design, binary |
| Brainfuck | 8 | 8 | Classic, relatively understandable |
| Malbolge | 94 | 3 | Intentionally designed to be difficult |
| Whitespace | 3 | 24 | Uses only space, tab, newline |

BS's feature is **true minimalism**, using only two symbols and one instruction.

---

## License

This project is licensed under **[GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE)**.

### Simple Explanation

✅ **You Can**:
- Use, learn, modify for free
- Use in personal or commercial projects
- Distribute your modified version

⚠️ **You Must**:
- Open source your modifications (if distributed)
- Keep original author's copyright notice
- Use the same AGPL-3.0 license
- **If made into a network service** (like Web API), must also open source server code

### Why AGPL?

AGPL is an enhanced version of GPL, closing the "SaaS loophole":

**Scenario Comparison**:
```
GPL: I modify your code → make it a website service → profit → don't open source ❌
AGPL: I modify your code → make it a website service → must provide source to users ✅
```

This ensures the BS ecosystem remains **completely open**.

### Detailed Terms

Please read the [LICENSE](LICENSE) file for complete legal terms.

---

## Related Resources

### 📚 Documentation

- **Java Interpreter Guide**: [中文](Interpreter/Java/README.md) | [English](Interpreter/Java/README_EN.md)
- **Project Home**: https://github.com/MCLMLI/Bitwise-Subleq
- **Issue Tracker**: https://github.com/MCLMLI/Bitwise-Subleq/issues
- **Releases**: https://github.com/MCLMLI/Bitwise-Subleq/releases

### 🔗 Related Projects

- [Subleq - Esolang Wiki](https://esolangs.org/wiki/Subleq) - Detailed Subleq language explanation
- [OISC - Wikipedia](https://en.wikipedia.org/wiki/One-instruction_set_computer) - One-instruction set computer
- [Esoteric Programming Languages](https://esolangs.org/) - Esoteric programming community

### 🤝 Contributions

Contributions welcome! Especially needed:

- 📝 Example programs (especially interesting ones)
- 🛠️ Interpreters in other languages (Python, C, Rust, etc.)
- 🔧 Development tools (assemblers, debuggers, visualization tools)
- 📖 Tutorial and documentation improvements
- 🐛 Bug reports and fixes
- 🌍 Translations (other language versions)

How to contribute:
1. Fork this repository
2. Create your feature branch
3. Submit a Pull Request

Or directly [create an Issue](https://github.com/MCLMLI/Bitwise-Subleq/issues) to discuss ideas.

---

## Acknowledgments

Thanks to everyone who has contributed to this project!

**Author**: MCLMLI  
**Project Started**: 2025  
**Current Version**: 1.0

---

<div align="center">

### ⭐ If you find this project interesting, please give it a Star!
