# BS Java Interpreter

[中文](README.md) | English

Java implementation of the BS (Bitwise Subleq) interpreter with full debugging capabilities and bilingual support.

> **Language Specification**: For complete BS language specification, see [Project Root README](../../README_EN.md)

## Features

- ✅ Full BS language support
- ✅ Debug mode (step-by-step execution, memory inspection)
- ✅ Bilingual interface (Chinese/English)
- ✅ Read programs from file or command line
- ✅ Detailed error messages
- ✅ Execution statistics
- ✅ Configurable execution limits

## Quick Start

### Build Project

```bash
cd Interpreter/Java
.\gradlew.bat build     # Windows
./gradlew build         # Linux/Mac
```

### Run Programs

#### Basic Usage

```bash
# Execute bitstream from command line
java -cp build\classes\java\main BSMain -e <bitstream>

# Execute from file
java -cp build\classes\java\main BSMain <filename>

# Debug mode
java -cp build\classes\java\main BSMain -d -e <bitstream>
```

#### Examples

```bash
# Infinite loop example
java -cp build\classes\java\main BSMain -e 000000000000000

# Output test
java -cp build\classes\java\main BSMain -e 100010000111111

# Using test files
java -cp build\classes\java\main BSMain test_infinite_loop.bs
java -cp build\classes\java\main BSMain test_output_a.bs
```

## Command Line Options

| Option | Description |
|--------|-------------|
| `-e <bitstream>` | Execute bitstream string directly |
| `-d, --debug` | Enable debug mode (step-by-step) |
| `--lang <zh\|en>` | Set interface language (default: zh) |
| `-h, --help` | Show help message |
| `<filename>` | Read and execute program from file |

## Environment Variables

| Variable | Value | Description |
|----------|-------|-------------|
| `BS_LANG` | `zh` / `en` | Set interface language |
| `BS_DEBUG` | `1` | Enable verbose debug output |
| `BS_VERBOSE` | `1` | Show execution statistics |

**Example:**
```bash
set BS_LANG=en
set BS_DEBUG=1
java -cp build\classes\java\main BSMain -e 000000000000000
```

## Project Structure

```
Interpreter/Java/
├── src/main/java/
│   ├── BitReader.java       # Bitstream parser interface
│   ├── SimpleBitReader.java # 5-bit block parser implementation
│   ├── BSInterpreter.java   # BS interpreter core
│   ├── BSMain.java          # Command line entry point
│   ├── Lang.java            # Multilingual support
│   └── TestDecoding.java    # Address decoding test utility
├── test_*.bs                # Test programs
├── build.gradle             # Gradle build configuration
└── README_EN.md            # This file
```

## Core Components

### BitReader
Bitstream reading interface, supports bit-by-bit reading and address decoding.

### SimpleBitReader
Implements 5-bit block parsing logic, handles self-terminating address encoding.

### BSInterpreter
Interpreter core, includes:
- Instruction decoding and execution
- Sparse memory management (HashMap)
- I/O operation handling
- Special address -1 handling

### BSMain
Command line interface, provides:
- Argument parsing
- File reading
- Debug mode control
- Multilingual output

### Lang
Internationalization support, easy to add new languages.

## Development Tools

### Address Decoding Test Tool

For testing and understanding the address encoding mechanism:

```bash
java -cp build\classes\java\main TestDecoding
java -cp build\classes\java\main TestDecoding --lang en
```

This tool shows the decoding process of various bitstreams to help understand self-terminating encoding.

## Debug Mode

When debug mode is enabled, the interpreter will:

1. Display the decoding process of each instruction
2. Show memory state before and after execution
3. Wait for user to press Enter (step-by-step execution)
4. Show program counter changes

**Example Output:**
```
=== Instruction #1 ===
PC: 0
Read address a: 0
Read address b: 0
Read address c: 0
Execute: mem[0] -= mem[0]
Result: mem[0] = 0
Condition: 0 <= 0 (true)
Jump to: 0
[Press Enter to continue...]
```

## Performance & Limits

### Default Limits

- **Max Instructions**: 1,000,000 (prevent infinite loop hangs)
- **Memory**: Uses sparse storage, theoretically unlimited
- **Address Range**: Java int range (-2³¹ ~ 2³¹-1)

### Performance Considerations

- Sparse memory uses HashMap, O(1) lookup time
- Instruction decoding uses streaming, space efficient
- Suitable for teaching and experiments, not production

## Troubleshooting

### 1. Program Doesn't Stop

**Cause**: Might be in infinite loop or missing halt instruction.

**Solution**: 
- Check if there's a jump to -1 instruction
- Use debug mode to observe execution flow
- Program auto-terminates after 1 million instructions

### 2. Bitstream Parse Error

**Error Message**: "Bitstream length must be multiple of 5"

**Solution**:
- Check if input file is complete (only 0s and 1s)
- Ensure bitstream length is correct (minimum 15 bits per instruction)

### 3. Garbled Output

**Cause**: Output byte value not in printable ASCII range.

**Solution**:
- Check the value in memory to be output
- Use `mem[a] & 0xFF` to ensure 0-255 range

### 4. Main Class Not Found

**Error Message**: "Could not find or load main class BSMain"

**Solution**:
```bash
# Rebuild
.\gradlew.bat clean build

# Ensure correct classpath
java -cp build\classes\java\main BSMain
```

## Extension Development

### Adding New Languages

Edit `Lang.java`, add new language code:

```java
private void loadMessages() {
    if ("fr".equals(lang)) {  // French
        messages.put("error.bitstream", "Erreur: ...");
        // ...
    }
}
```

### Custom Memory Size

Modify `MAX_INSTRUCTIONS` constant in `BSInterpreter.java`.

### Adding New I/O Operations

Extend special address handling logic in `executeInstruction()` method of `BSInterpreter.java`.

## Test Files

### test_infinite_loop.bs
Simplest infinite loop: `000000000000000`

### test_output_a.bs
Output test program, demonstrates basic I/O operations.

## Build Artifacts

After compilation, generates:

- `build/classes/java/main/` - Compiled .class files
- `build/libs/` - JAR package
- `build/reports/` - Build reports

## Dependencies

No external dependencies, uses only Java standard library.

**Minimum Requirement**: Java 8+

## Contributing

Contributions welcome:

- 🐛 Report bugs
- ✨ Suggest new features
- 📝 Improve documentation
- 🚀 Performance optimization
- 🌍 Add new language support

## Related Resources

- **Project Home**: [BS Language](../../README_EN.md)
- **Language Specification**: [README_EN.md](../../README_EN.md#language-specification)
- **Chinese Documentation**: [README.md](README.md)

## License

See [LICENSE](../../LICENSE) file in project root directory.

---

**Tip**: First time user? Try this command:
```bash
java -cp build\classes\java\main BSMain -d -e 000000000000000
```
Experience debug mode and observe how the program executes!
