# BS Java Interpreter

English | [中文](README.md)

Java implementation of the BS (Bitwise Subleq) interpreter.

> 📖 **Full Language Specification**: See [Project Root README](../../README_EN.md)

## Quick Start

### Option 1: Use Pre-built JAR (Recommended)

Download the JAR file from [Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases) and run directly:

```bash
# Show help
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar --help

# Execute program
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e 000000000000000

# Run from file
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar program.bs

# Debug mode
java -jar Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -d -e 000000000000000
```

### Option 2: Build from Source

```bash
cd Interpreter/Java
.\gradlew.bat build     # Windows
./gradlew build         # Linux/Mac

# Run
java -jar build\libs\Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar -e <bitstream>
```

## Command Line Options

```
Usage:
  java -jar <jarfile> [options] <filename>
  java -jar <jarfile> [options] -e <bitstream>

Options:
  -h, --help              Show help message
  --lang zh|en            Set interface language (default: zh)
  -e <bitstream>          Execute bitstream string directly
  -d                      Enable debug mode (step-by-step)
  <filename>              Read and execute program from file
```

## Usage Examples

### Basic Usage

```bash
# Infinite loop example
java -jar bs-interpreter.jar -e 000000000000000

# Output test
java -jar bs-interpreter.jar -e 100010000111111

# Run from file
java -jar bs-interpreter.jar test_program.bs
```

### Debug Mode

```bash
# Step-by-step execution with detailed information
java -jar bs-interpreter.jar -d -e 000000000000000
```

Debug mode displays:
- Instruction decoding process
- Memory state before and after execution
- Program counter changes
- Waits for Enter key after each step

### Language Support

```bash
# Use English interface
java -jar bs-interpreter.jar --lang en -e 000000000000000

# Set language via environment variable
set BS_LANG=en                           # Windows
export BS_LANG=en                        # Linux/Mac
java -jar bs-interpreter.jar -e 000000000000000
```

## Environment Variables

| Variable | Value | Description |
|----------|-------|-------------|
| `BS_LANG` | `zh` / `en` | Set interface language (default: zh) |
| `BS_DEBUG` | `1` | Enable verbose debug output |
| `BS_VERBOSE` | `1` | Show execution statistics |

## Features

- ✅ Full BS language support
- ✅ Step-by-step debug mode
- ✅ Bilingual interface (Chinese/English)
- ✅ File and command line input
- ✅ Detailed error messages
- ✅ Execution statistics
- ✅ Executable JAR file

## Development Tools

### Address Decoding Test Tool

For understanding BS's self-terminating address encoding:

```bash
java -cp build\classes\java\main TestDecoding
java -cp build\classes\java\main TestDecoding --lang en
```

## System Requirements

- **Java**: 8 or higher
- **Dependencies**: None
- **Platform**: Windows / Linux / macOS

## Project Structure

```
Interpreter/Java/
├── src/main/java/
│   ├── BitReader.java       # Bitstream parser interface
│   ├── SimpleBitReader.java # 5-bit block parser implementation
│   ├── BSInterpreter.java   # BS interpreter core
│   ├── BSMain.java          # Command line entry point
│   ├── Lang.java            # Multilingual support
│   └── TestDecoding.java    # Address decoding test
├── build.gradle             # Gradle build configuration
├── README.md               # Chinese documentation
└── README_EN.md            # This file
```

## FAQ

**Q: Program doesn't stop?**  
A: The program will automatically stop after 1,000,000 instructions. Use debug mode `-d` to observe execution flow.

**Q: How to output characters?**  
A: Use special address -1 as output target. See [Language Specification](../../README_EN.md) for details.

**Q: Bitstream format error?**  
A: Ensure input contains only 0s and 1s, and length is a multiple of 5.

## License

This project uses the same license as the main project. See [LICENSE](../../LICENSE) for details.
