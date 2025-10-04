# BS Java Interpreter

English | [中文](README.md)

Java implementation of the BS (Bitwise Subleq) interpreter, supporting the new 6-bit encoding format (4 data bits + 1 function bit + 1 link bit).

> 📖 **Full Language Specification**: See [Project Root README](../../README_EN.md)

## Quick Start

### Compile and Run from Source

```bash
cd Interpreter/Java/src/main/java
javac -encoding UTF-8 *.java
java BSMain -e "000010000010000010"    # Test halt instruction
```

### Usage Examples

```bash
# Immediate halt (c segment function bit=1)
java BSMain -e "000010000010000010"

# Debug mode with detailed information
java BSMain -d -e "000010000010000010"

# Run from file
java BSMain program.bs

# Input/output test (interactive)
java BSMain -e "000110000110000010"
```

## Command Line Options

```
Usage:
  java BSMain [options] <filename>
  java BSMain [options] -e <bitstream>

Options:
  -h, --help              Show help message
  --lang zh|en            Set interface language (default: system language)
  -e <bitstream>          Execute bitstream string directly
  -d                      Enable debug mode (show detailed execution)
  <filename>              Read and execute program from file
```

## New Encoding Format

Each address segment consists of 6 bits: `[dddd][s][l]`
- **dddd**: 4 data bits
- **s**: Function bit (0=discard, 1=valid)
- **l**: Link bit (0=end, 1=continue)

### Function Bit Actions

| Segment | Function bit=1 | Description |
|---------|---------------|-------------|
| a segment | Input | Read 1 byte from stdin to mem[a] |
| b segment | Output | Output low 8 bits of mem[b] to stdout |
| c segment | Halt | Immediately terminate program |

### Encoding Examples

```
000010 = 0000(data) 1(function) 0(link)
         address=0, has function bit

000110 = 0001(data) 1(function) 0(link)
         address=1, has function bit

010011 = 0100(data) 1(function) 1(link)
         Error! s=1 and l=1, treated as invalid
```

## Debug Mode

Debug mode displays:
- Instruction loading process and function bit status
- Memory state before and after each instruction
- Program counter changes
- Input/output operation details

```bash
java BSMain -d -e "000010000010000010"
```

Example output:
```
Loaded instruction 0: a=0[], b=0[], c=0[HALT]
Loaded 1 instructions
Starting execution of 1 instructions...

PC=0, Instr: a=0, b=0, c=0[HALT]
  Before: mem[0]=0, mem[0]=0
  HALT (c function bit)

Execution finished. Total instructions: 1
```

## Building JAR

To generate a standalone JAR file:

```bash
cd Interpreter/Java
.\gradlew.bat build     # Windows
./gradlew build         # Linux/Mac

# JAR file location
build/libs/Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```

## Technical Features

- ✅ New 6-bit encoding format support
- ✅ Function bit mechanism (input/output/halt)
- ✅ Sparse memory implementation (HashMap)
- ✅ Detailed debug output
- ✅ Bilingual support (Chinese/English)
- ✅ Self-terminating address parsing
- ✅ Error handling and validation

## License

MIT License - See [LICENSE](../../LICENSE) file in project root
