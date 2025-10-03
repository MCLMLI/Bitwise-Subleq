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
# BS Java Interpreter
**Solution**: 
- Check if there's a jump to -1 instruction
- Use debug mode to observe execution flow
- Program auto-terminates after 1 million instructions
Java implementation of the BS (Bitwise Subleq) interpreter with full debugging capabilities and bilingual support.
### 2. Bitstream Parse Error
> **Language Specification**: For complete BS language specification, see [Project Root README](../../README_EN.md)
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

# Rebuild
.\gradlew.bat clean build

# Ensure correct classpath
java -cp build\classes\java\main BSMain

- ✅ Full BS language support
## Extension Development
- ✅ Bilingual interface (Chinese/English)
### Adding New Languages
- ✅ Detailed error messages
Edit `Lang.java`, add new language code:
### Build Project
```java
private void loadMessages() {
    if ("fr".equals(lang)) {  // French
        messages.put("error.bitstream", "Erreur: ...");
        // ...
    }
}
```
```bash
### Custom Memory Size
```
Modify `MAX_INSTRUCTIONS` constant in `BSInterpreter.java`.
### Run Programs
### Adding New I/O Operations
```bash
Extend special address handling logic in `executeInstruction()` method of `BSInterpreter.java`.
java -cp build\classes\java\main BSMain -e <bitstream>
## Test Files
# Execute from file
### test_infinite_loop.bs
Simplest infinite loop: `000000000000000`

### test_output_a.bs
Output test program, demonstrates basic I/O operations.
```
## Build Artifacts
java -cp build\classes\java\main BSMain -e 000000000000000
After compilation, generates:
# Output test
- `build/classes/java/main/` - Compiled .class files
- `build/libs/` - JAR package
- `build/reports/` - Build reports

## Dependencies
```
No external dependencies, uses only Java standard library.
| Option | Description |
**Minimum Requirement**: Java 8+
| `--lang <zh\|en>` | Set interface language (default: zh) |
| `-h, --help` | Show help message |
| `<filename>` | Read and execute program from file |
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
## Environment Variables

| Variable | Value | Description |
See [LICENSE](../../LICENSE) file in project root directory.
| `BS_LANG` | `zh` / `en` | Set interface language |
| `BS_DEBUG` | `1` | Enable verbose debug output |
| `BS_VERBOSE` | `1` | Show execution statistics |
**Tip**: First time user? Try this command:
```bash
java -cp build\classes\java\main BSMain -d -e 000000000000000
```
Experience debug mode and observe how the program executes!
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

This will show bit-by-bit analysis and decoding results of bitstreams.

### Switch Test Tool Language

```bash
java -cp build\classes\java\main TestDecoding --lang zh
```

## Technical Notes

### Address Decoding Algorithm

1. Read 4 data bits
2. Read 1 link bit
3. If link=1, shift accumulated value left and continue
4. If link=0, address complete
5. If all bits are 1s, return -1

### Memory Model

- Uses sparse memory (HashMap)
- Uninitialized addresses default to 0
- Supports negative address indexing (for special operations)

### Program Counter

- PC starts at 0
- Jump instructions set PC directly
- PC increments for sequential execution

## Language Selection

This interpreter defaults to **Chinese** output, which is more friendly for Chinese developers. To switch to English:

**Method 1: Command line argument**
```bash
java -cp build\classes\java\main BSMain --lang en -e 000000000000000
```

**Method 2: Environment variable**
```bash
set BS_LANG=en
java -cp build\classes\java\main BSMain -e 000000000000000
```

## Troubleshooting

### Common Errors

1. **Bitstream length not multiple of 5**
   - Check if input file is complete
   - Use debug mode to see parsing process

2. **Program doesn't stop**
   - Check if there's a halt instruction (c=-1)
   - Default limit: 1 million instructions

3. **Output not as expected**
   - Use `-d` flag to see detailed execution
   - Check memory initial values and instruction order

## Contributing

Issues and improvement suggestions are welcome!

## License

See LICENSE file in project root directory.

---

**Note:** This is an experimental language project for exploring minimalist language design.

