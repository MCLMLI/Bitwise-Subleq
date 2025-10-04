# BS (Bitwise Subleq)

[中文](README.md) | English

**A minimalist Turing-complete programming language using only binary encoding**

BS (Bitwise Subleq) is an experimental programming language that achieves Turing completeness with the most minimalist design: a two-character alphabet {0, 1}, self-terminating address encoding, and a single-instruction architecture (Subleq). It is a powerful tool for studying computational theory and the essence of programming.

## Table of Contents

- [Language Overview](#language-overview)
- [Language Specification](#language-specification)
- [Implementations](#implementations)
- [License](#license)

## Language Overview

### Core Features

- **Character Set**: `{0, 1}` - Pure binary
- **Minimum Unit**: bit
- **Lexical Unit**: 6-bit blocks (4 data bits + 1 function bit + 1 link flag)
- **Instruction Set**: Single instruction (Subleq - subtract and jump if less than or equal to zero)
- **Memory Model**: Sparse memory supporting arbitrary addressing
- **Turing Complete**: Can compute any computable function

### Design Philosophy

BS explores the question: "What is the absolute minimum required to implement computation?" By stripping away all unnecessary complexity, BS demonstrates that:
- Complex computation requires only subtraction and conditional branching
- Self-terminating variable-length encoding can efficiently represent data
- Two symbols are sufficient to implement any algorithm

## Language Specification

### 1. Self-Terminating Address Encoding (New Format)

Each address consists of one or more 6-bit blocks: `[dddd][s][l]`

- **dddd**: 4 data bits (most significant bit first, **always accumulated to address**)
- **s**: Function bit (**not data**, only a marker)
- **l**: Link bit (**not data**, controls continuation)

#### Parsing Rules:

**Data Accumulation**:
- The 4 data bits (dddd) of each segment are **always** parsed and accumulated to the address value
- address = (address << 4) | data, continuously left-shifted and accumulated

**Function Bit (s)**:
- **s=0**: This segment has no function bit marker
- **s=1 and l=0**: This segment has function bit marker (valid)
- **s=1 and l=1**: Error condition, treated as s=0

**Link Bit (l)**:
- **l=0**: Address parsing ends
- **l=1**: Continue reading next 6-bit block

**Examples:**
```
Bitstream: 000010
           ↓
Segment:   0000(data) 1(function) 0(link)

Parsing:
- Data bits 0000 = 0
- Function bit s=1, link bit l=0 → has function, address ends
- Result: address=0, hasFunction=true
```

```
Bitstream: 000110 010010
           ↓       ↓
Seg1:      0001 s=1 l=0 (has function, address ends)
Seg2:      Not read

Parsing:
- Seg1: data=0001=1, s=1, l=0 → has function, ends
- Result: address=1, hasFunction=true
```

```
Bitstream: 000111 001110
           ↓       ↓
Seg1:      0001 s=1 l=1 (error! s=1 and l=1)
Seg2:      0011 s=1 l=0 (has function)

Parsing:
- Seg1: data=0001=1 accumulated, s=1 but l=1→error, no function marker, continue
- Seg2: data=0011=3 accumulated, s=1 and l=0→has function, ends
- Result: address=(1<<4)|3=19, hasFunction=true
```

### 2. Instruction Format

Each instruction consists of three consecutive addresses:

```
Instruction = [address_a][address_b][address_c]
```

**Execution Semantics:**
```
// Within the same instruction, check and execute all function bits in sequence
// Note: All function bit operations are completed within the same instruction, no return in between

// 1. Check a segment function bit (input)
if (a segment function bit == 1)
    read 1 byte from input → mem[a]
    // Continue to check subsequent function bits

// 2. Check b segment function bit (output)
if (b segment function bit == 1)
    output low 8 bits of mem[b]
    // Continue to check subsequent function bits

// 3. Check c segment function bit (halt)
if (c segment function bit == 1)
    halt and exit
    return

// 4. If any function bit was triggered
if (a segment function bit == 1 || b segment function bit == 1 || c segment function bit == 1)
    PC = PC + 1
    return  // Do not execute Subleq instruction

// 5. Otherwise execute normal Subleq instruction
mem[b] = mem[b] - mem[a]
if (mem[b] <= 0)
    PC = c
else
    PC = PC + 1
```

**Important Notes:**
- Within the same instruction, function bits of a, b, c segments will **all be executed in sequence** (if set)
- For example: `000010 000010 000000` will first input to mem[0], then immediately output mem[0], finally PC+1
- As long as any function bit is triggered, the normal Subleq subtraction operation will not be executed

### 3. Function Bit Special Operations

Function bits provide I/O and control operations without relying on special address values:

| Segment | Behavior when function bit=1 | Description |
|---------|------------------------------|-------------|
| **a segment** | Blocking input | Read 1 byte from stdin to `mem[a]` address, then PC+1 |
| **b segment** | Output | Output contents (low 8 bits) of `mem[b]` address to stdout, then PC+1 |
| **c segment** | Halt | Immediately terminate program execution |

**Advantage:** No longer relies on special address values (like all-ones), but uses explicit function bit markers, avoiding special reservations in the address space.

### 4. Memory Model

- Sparse memory implemented using hash table
- All uninitialized addresses default to 0
- Supports arbitrary positive integer addresses
- Theoretically infinite address space

### 5. Program Counter

- Starts at address 0
- Instructions execute sequentially unless a jump occurs
- Program terminates when c segment function bit is 1

## Quick Start

### Example Programs

#### Immediate Halt
```
000010 000010 000010
#  a     b     c
# a=0, b=0, c=0
# c segment function bit=1, immediate halt
```

#### Simple Output
```
# Assume mem[0] already contains character 'A'(65)
000000 000010 000010
# a=0(no function), b=0(output), c=0(halt)
# b segment function bit=1: output mem[0]
# c segment function bit=1: halt
```

#### Input and Echo
```
000010 000010 000010
# a=0(input), b=0(output), c=0(halt)
# a segment function bit=1: input to mem[0]
# b segment function bit=1: output mem[0]
# c segment function bit=1: halt
```

**Running:**
```bash
# Use pipe input (recommended)
echo A | java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# Or use file input
echo ABC > input.txt
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -f program.bs < input.txt
```

**⚠️ Important: Line Buffering of Input Stream**

BS programs read byte streams from standard input. **Standard input is line-buffered**, which means:

**Why does input appear "merged"?**
```bash
# Interactive execution example
> java -jar interpreter.jar -f test.bs
> 123[Enter]
# Output: 123

# You might expect: program pauses 3 times, waiting for 1 character each time
# What actually happens: when you press Enter, the entire line "123\n" enters the buffer,
#                        the program's 3 read() calls sequentially read '1', '2', '3', without pausing
```

**Reason:**
- When you type `123` and press Enter, the entire line (including newline) is placed in the input buffer
- Each `System.in.read()` call by the program is an independent **blocking call**
- But it only blocks when the buffer is **empty**
- If the buffer already has data, `read()` returns **immediately**, without re-prompting the user

**Example Comparison:**

```bash
# Case 1: Interactive input (appears "merged")
> bs test.bs
> 2323[Enter]
Output: 232
# Explanation: '2', '3', '2' read sequentially from buffer, halts after 3rd character

# Case 2: Piped input (expected behavior)
> echo ABC | bs test.bs
Output: ABC
# Explanation: entire string is in buffer, program reads sequentially

# Case 3: Character-by-character input (requires special handling)
> # Cannot achieve "pause and ask each time" in most terminals
> # Because standard input is line-buffered
```

**EOF Handling:**
```bash
# When input stream ends (EOF)
> bs test.bs
> 1[Enter]
Output: 1
# Reads: '1', '\n', EOF (converted to 0)
# If program expects 3 characters, 3rd read encounters EOF

# Correct way: use pipe or redirection
> echo ABC | bs test.bs
# Reads: 'A', 'B', 'C', '\n', EOF

# Windows without newline
> echo|set /p="ABC" | bs test.bs
# Reads: 'A', 'B', 'C', EOF
```

**Summary:**
- BS language input operations are **blocking byte stream reads**, this is standard behavior
- Input appears "merged" due to **line buffering**, not a bug
- If you need true "character-by-character interaction", you need special terminal mode (raw mode), which is beyond standard BS language specification

#### Multi-segment Address Example
```
000111 001110 000010 000010
# a segment: 0001 s=1 l=1 → continue
#            0011 s=1 l=0 → address=19, has function (input)
# b segment: 0000 s=1 l=0 → address=0, has function (output)
# c segment: 0000 s=1 l=0 → address=0, has function (halt)
# Function: input to mem[19], output mem[0], halt
```

## Implementations

### Java Interpreter

We provide a fully functional Java interpreter implementation.

📦 **Recommended: Use Pre-compiled Version**

Download the latest `Bitwise-Subleq-Interpreter-Java-x.x.x.jar` from the [Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases) page.

```bash
# Run directly after download
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# View help
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar --help
```

📖 Detailed Documentation: [Java Interpreter README](Interpreter/Java/README_EN.md)

## License

This project is licensed under the [GNU Affero General Public License v3.0](LICENSE).

---

**© 2025 BS (Bitwise Subleq) Project**  
By using this software, you agree to comply with all terms of the AGPLv3 license.
