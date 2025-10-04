# BS Java Interpreter

[中文](README.md) | English

Java implementation of the BS (Bitwise Subleq) interpreter, supporting the new 6-bit encoding format (4 data bits + 1 function bit + 1 link bit).

> 📖 **Full Language Specification**: See [Project Root README](../../README_EN.md)

## Quick Start

### 📦 Recommended: Use Pre-compiled JAR File

Download the latest JAR file from the [Releases](https://github.com/your-username/Bitwise-Subleq/releases) page.

```bash
# Run directly after download
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# View help message
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar --help

# Run program from file
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar program.bs

# Enable debug mode
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -d -e "000010000010000010"
```

### Usage Examples

```bash
# Immediate halt (all segment function bits=1, executed in a→b→c order)
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"

# Input/output test (interactive)
# a segment input → b segment output updated memory → c segment halt
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e "000010000010000010"
# Input a character, program will immediately output that character then halt
```

## Command Line Options

```
Usage:
  java -jar Bitwise-Subleq-Interpreter-Java-x.x.x.jar [options] <filename>
  java -jar Bitwise-Subleq-Interpreter-Java-x.x.x.jar [options] -e <bitstream>

Options:
  -h, --help              Show help message
  --lang zh|en            Set interface language (default: system language)
  -e <bitstream>          Execute bitstream string directly
  -d                      Enable debug mode (show detailed execution)
  <filename>              Read and execute program from file
```

## New Encoding Format

Each address segment consists of 6 bits: `[dddd][s][l]`
- **dddd**: 4 data bits (always accumulated to address value)
- **s**: Function bit (not data, only marks function)
- **l**: Link bit (not data, controls continuation)

### Function Bit Actions

| Segment | Function bit=1 | Description |
|---------|---------------|-------------|
| a segment | Input | Read 1 byte from stdin to mem[a] |
| b segment | Output | Output low 8 bits of mem[b] to stdout |
| c segment | Halt | Immediately terminate program |

**Important**: When multiple segments in an instruction have function bits, they execute in **a → b → c** order.

### Encoding Examples

```
000010 = 0000(data) 1(function) 0(link)
         address=0, has function bit

000110 = 0001(data) 1(function) 0(link)
         address=1, has function bit

000111 = 0001(data) 1(function) 1(link)
         Error! s=1 and l=1, treated as no function bit, continue reading next segment
```

## Debug Mode

Debug mode displays:
- Instruction loading process and function bit status
- Memory state before and after each instruction
- Program counter changes
- Input/output operation details

```bash
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -d -e "000010000010000010"
```

Example output:
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

## Build from Source (Advanced Users)

If you need to modify the source code or build from source:

```bash
cd Interpreter/Java/src/main/java
javac -encoding UTF-8 *.java
java BSMain -e "000010000010000010"
```

## License

This project is licensed under the [GNU Affero General Public License v3.0](../../LICENSE).

By using this software, you agree to comply with all terms of the AGPLv3 license.
