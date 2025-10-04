# BS (Bitwise Subleq)

[中文](README.md) | English

**A minimalist Turing-complete programming language using only binary encoding**

BS (Bitwise Subleq) is an experimental programming language that achieves Turing completeness with the most minimalist design: a two-character alphabet {0, 1}, self-terminating address encoding, and a single-instruction architecture (Subleq). It is a powerful tool for studying computational theory and the essence of programming.

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

- **dddd**: 4 data bits (most significant bit first)
- **s**: Function bit
- **l**: Link bit

#### Parsing Rules:

1. **s=0** (function bit is 0): Discard this segment, don't parse data
   - If l=0: Address parsing ends
   - If l=1: Continue reading next segment

2. **s=1, l=0** (function bit is 1, link bit is 0): Valid segment
   - Parse data bits and accumulate to address
   - Address parsing ends
   - This segment has function bit marker

3. **s=1, l=1** (function bit is 1, link bit is 1): Error condition
   - Treat as s=0 (discard this segment)
   - Address parsing ends

**Examples:**
```
Bitstream: 000110 010010
           ↓       ↓
Segment 1: 0001 s=1 l=0 (valid segment, address ends)
Segment 2: Not read

Result: address=0001₂ = 1₁₀, hasFunction=true
```

```
Bitstream: 010011 100110
           ↓       ↓
Segment 1: 0100 s=1 l=1 (error! treat as s=0)
Address parsing ends

Result: address=0₁₀, hasFunction=false
```

### 2. Instruction Format

Each instruction consists of three consecutive addresses:

```
Instruction = [address_a][address_b][address_c]
```

**Execution Semantics:**
```
// First check function bits
if (c segment function bit == 1)
    halt and exit

if (a segment function bit == 1)
    read 1 byte from input → mem[a]
    PC = PC + 1
    return

if (b segment function bit == 1)
    output low 8 bits of mem[b]
    PC = PC + 1
    return

// Normal Subleq instruction
mem[b] = mem[b] - mem[a]
if (mem[b] <= 0)
    PC = c
else
    PC = next instruction
```

### 3. Function Bit Special Operations

Function bits provide I/O and control operations without relying on all-ones encoding:

| Segment | Behavior when function bit=1 | Description |
|---------|------------------------------|-------------|
| **a segment** | Blocking input | Read 1 byte from stdin to `mem[a]` address, then PC+1 |
| **b segment** | Output | Output contents (low 8 bits) of `mem[b]` address to stdout, then PC+1 |
| **c segment** | Halt | Immediately terminate program execution |

**Advantage:** No longer uses all-ones encoding (prone to misjudgment), but uses explicit function bit markers.

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
# c segment function bit=1, immediate halt
```

#### Output Character
```
# Assume mem[5] already contains character 'A'(65)
010110 000010 000010
#  a=5   b=0   c=0
#       out   halt
# b segment function bit=1: output mem[0]
# c segment function bit=1: halt
```

#### Input and Output
```
000110 000110 000010
# a function bit=1: input to mem[0]
# b function bit=1: output mem[0]  
# c function bit=1: halt
```

## Implementations

### Java Interpreter

Full-featured interpreter with debug mode and new function bit encoding support.

**Location**: `Interpreter/Java/`

**Quick Start**:
```bash
cd Interpreter/Java/src/main/java
javac -encoding UTF-8 *.java
java BSMain -e "000010000010000010"  # Test halt
java BSMain -d -e "your_bitstream"   # Debug mode
```

For detailed usage, see [Java README](Interpreter/Java/README_EN.md).

## Use Cases

### Educational Uses

- **Computational Theory**: Demonstrate Turing completeness with minimal components
- **Compiler Design**: Study instruction encoding and decoding
- **Algorithm Design**: Foster creative problem-solving under constraints

### Research Uses

- **Minimalist Computing**: Explore the boundaries of computational minimalism
- **Coding Theory**: Study self-terminating variable-length encoding
- **Theoretical Computer Science**: Research Subleq architecture variants

### Artistic Uses

- **Esoteric Programming**: Join the minimalist language family (Brainfuck, Malbolge, etc.)
- **Binary Art**: Create visual patterns with executable code
- **Generative Art**: Creative output using computational constraints

## Language Comparison

| Feature | BS | Brainfuck | OISC | Machine Code |
|---------|----|-----------|----- |--------------|
| Alphabet Size | 2 | 8 | Variable | 256 |
| Instruction Count | 1 | 8 | 1 | 100+ |
| Address Encoding | Variable (6-bit blocks) | Fixed | Fixed | Fixed |
| I/O Method | Function bits | Dedicated instructions | Special addresses | Dedicated instructions |
| Turing Complete | ✓ | ✓ | ✓ | ✓ |

## Technical Advantages

1. **Minimal Parsing**: Only need to recognize 6-bit blocks
2. **Self-Describing**: Address length determined by content
3. **Function Bit Mechanism**: Avoids all-ones misjudgment problem
4. **Compact Small Values**: Common addresses (0-15) only require 6 bits
5. **Unbounded Addresses**: No artificial memory limits
6. **Hardware Friendly**: Easy to implement in digital circuits

## Contribution Guidelines

We welcome all forms of contribution:

- **New Implementations**: Write interpreters in other languages
- **Example Programs**: Create interesting BS programs
- **Optimizations**: Improve performance or reduce code size
- **Documentation**: Improve explanations and tutorials
- **Tools**: Assemblers, debuggers, visualization tools

## Project Structure

```
Bitwise-Subleq/
├── README.md           # Chinese documentation
├── README_EN.md        # This file (English)
├── LICENSE             # License information
└── Interpreter/        # Language implementations
    └── Java/           # Java interpreter
        ├── README.md   # Implementation docs (Chinese)
        ├── README_EN.md # Implementation docs (English)
        └── src/        # Source code
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Contact

- **Issues**: Submit issues on GitHub
- **Pull Requests**: Code contributions welcome

---

**BS (Bitwise Subleq)** - Exploring the essence of computation, starting from 0 and 1.
