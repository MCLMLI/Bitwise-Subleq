# BS (Bitwise Subleq)

[中文](README.md) | English

**A minimalist Turing-complete programming language using only binary encoding**

BS (Bitwise Subleq) is an experimental programming language that achieves Turing completeness with the most minimal design possible: a two-character alphabet {0, 1}, self-terminating address encoding, and a single instruction (Subleq). It serves as a powerful tool for studying computational theory and the essence of programming.

## Language Overview

### Core Features

- **Character Set**: `{0, 1}` - Pure binary
- **Minimum Unit**: bit
- **Lexical Unit**: 5-bit block (4 data bits + 1 link bit)
- **Instruction Set**: Single instruction (Subleq - Subtract and Branch if Less than or Equal to zero)
- **Memory Model**: Sparse memory with arbitrary addressing
- **Turing Complete**: Can compute anything computable

### Philosophy

BS explores the question: "What is the absolute minimum needed for computation?" By stripping away all unnecessary complexity, BS demonstrates that:
- Complex computation requires only subtraction and conditional branching
- Self-terminating variable-length encoding enables efficient data representation
- Two symbols are sufficient for any algorithm

## Language Specification

### 1. Self-Terminating Address Encoding

Each address consists of one or more 5-bit blocks: `[dddd][l]`

- **dddd**: 4 data bits (MSB first)
- **l**: Link bit
  - `0` = Address complete
  - `1` = Continue reading next block (shift left by 4 bits)

**Example:**
```
Bitstream: 10011 01000
           ↓      ↓
Segment 1: 1001 (link=1, continue)
Segment 2: 0100 (link=0, end)

Result: (1001 << 4) | 0100 = 10010100₂ = 148₁₀
```

This encoding allows addresses of any magnitude while keeping small addresses compact.

### 2. Instruction Format

Each instruction consists of three consecutive addresses:

```
Instruction = [address_a][address_b][address_c]
```

**Execution Semantics:**
```
mem[b] = mem[b] - mem[a]
if (mem[b] <= 0)
    PC = c
else
    PC = next_instruction
```

That's it - the entire instruction set!

### 3. Special Address -1

When all bits are 1s (`11111 11111 ...`), the address equals -1, triggering special behaviors:

| Operation | Condition | Behavior |
|-----------|-----------|----------|
| **Output** | `b == -1` and `a ≠ -1` | Output `mem[a] & 0xFF` as character |
| **Input** | `a == -1` and `b == -1` | Read one byte from input into `mem[b]` |
| **Halt** | `c == -1` and `mem[b] ≤ 0` | Terminate program execution |

### 4. Memory Model

- Sparse memory implemented with hash table
- All uninitialized addresses default to 0
- Negative addresses supported (used for I/O operations)
- Theoretically unlimited address space

### 5. Program Counter

- Starts at address 0
- Instructions execute sequentially unless branched
- Program terminates when jumping to -1

## Quick Start

### Example Programs

#### Hello World (Conceptual)
```
# Store 'H' (72) in memory and output
[addr_H][addr_output][-1]
# Continue for 'e', 'l', 'l', 'o'...
```

#### Infinite Loop
```
000000000000000
# Decode: a=0, b=0, c=0
# Effect: mem[0] -= mem[0] → always 0 → jump to 0
```

#### Simple Output
```
100010000111111
# Outputs a character from memory
```

## Implementations

### Java Interpreter

A full-featured interpreter with debugging support.

**Location**: `Interpreter/Java/`

See [Java README](Interpreter/Java/README.md) for detailed usage.

### Future Implementations

Planned implementations in other languages:
- Python (educational focus)
- C (performance focus)
- WebAssembly (browser support)

## Use Cases

### Educational

- **Computational Theory**: Demonstrates Turing completeness with minimal components
- **Compiler Design**: Study instruction encoding and decoding
- **Algorithm Design**: Forces creative problem-solving with constraints

### Research

- **Minimal Computing**: Explore the boundaries of computational minimalism
- **Code Golf**: Ultimate challenge for size-optimized programming
- **Theoretical CS**: Study Subleq architecture variations

### Artistic

- **Esoteric Programming**: Join the family of minimal languages (Brainfuck, Malbolge, etc.)
- **Binary Art**: Create visual patterns with executable code
- **Generative Art**: Use computation constraints for creative output

## Language Comparison

| Feature | BS | Brainfuck | OISC | Machine Code |
|---------|----|-----------|----- |--------------|
| Alphabet Size | 2 | 8 | varies | 256 |
| Instructions | 1 | 8 | 1 | 100+ |
| Address Encoding | Variable | Fixed | Fixed | Fixed |
| Turing Complete | ✓ | ✓ | ✓ | ✓ |

## Technical Advantages

1. **Minimal Parsing**: Only 5-bit block recognition needed
2. **Self-Describing**: Address length determined by content
3. **Compact Small Values**: Common addresses (0-15) use only 5 bits
4. **Unbounded Addresses**: No artificial memory limits
5. **Hardware-Friendly**: Easy to implement in digital circuits

## Contributing

We welcome contributions in all forms:

- **New Implementations**: Write interpreters in other languages
- **Example Programs**: Create interesting BS programs
- **Optimizations**: Improve performance or code size
- **Documentation**: Improve explanations and tutorials
- **Tools**: Assemblers, debuggers, visualizers

## Project Structure

```
Bitwise-Subleq/
├── README.md           # Chinese documentation
├── README_EN.md        # This file
├── LICENSE             # License information
└── Interpreter/        # Language implementations
    └── Java/           # Java interpreter
        ├── README.md   # Implementation docs (Chinese)
        ├── README_EN.md # Implementation docs (English)
        └── src/        # Source code
```

## Resources

### Learning Materials

- **Subleq Architecture**: Research papers on one-instruction computing
- **Variable-Length Encoding**: Study Huffman coding and self-delimiting codes
- **Turing Completeness**: Understanding universal computation

### Related Projects

- **Subleq**: The original one-instruction architecture
- **Brainfuck**: Another minimalist language
- **OISC (One Instruction Set Computer)**: Family of minimal architectures

## License

This project is open source. See [LICENSE](LICENSE) file for details.

## Contact & Community

- **Issues**: Report bugs and request features on GitHub
- **Discussions**: Share ideas and programs
- **Pull Requests**: Contribute code and documentation

---

**BS Language** - *Exploring the limits of computational minimalism*

*"Simplicity is the ultimate sophistication." - Leonardo da Vinci*
