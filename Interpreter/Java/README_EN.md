# BS Java Interpreter

[**中文**](README.md) | English

**Official interpreter for running BS (Bitwise Subleq) programs**

> 💡 New to BS? See the [project root README](../../README_EN.md)

---
## 📌 Conventions
We use `<jar>` below to refer to the downloaded executable:
```
<jar> = Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar
```
Rename as you wish; substitute accordingly in commands.

---
## 📚 Table of Contents
- [Quick Start](#quick-start)
- [Usage Guide](#usage-guide)
- [Command Line Options](#command-line-options)
- [Environment Variables & Priority](#environment-variables--priority)
- [FAQ](#faq)
- [Build From Source](#build-from-source)
- [Technical Notes (Developers)](#technical-notes-developers)

---
## Quick Start
### 1. Download
Grab `<jar>` from the latest [Releases](https://github.com/MCLMLI/Bitwise-Subleq/releases).

### 2. Check Java
```bash
java -version
```
If not installed: https://www.java.com

### 3. Run examples
```bash
# Help
java -jar <jar> -h

# Example 1: Pure HALT (no input, no output)
# Instruction: a=0(no fn) b=0(no fn) c=0(HALT)
# Bitstream (grouped): 000000 000000 000010  → Continuous: 000000000000000010
java -jar <jar> -e 000000000000000010

# Example 2: Input 1 byte → output it → halt (NOT an empty halt)
# Bitstream: 000010 000010 000010 → 000010000010000010
echo A | java -jar <jar> -e 000010000010000010

# Example 3: Echo 3 characters then halt
echo ABC | java -jar <jar> -e 000010000010000000000010000010000000000010000010000010
```
> If it “hangs”, it’s probably waiting for input (a=IN).

---
## Usage Guide
### 1. From file
`echo1.bs`:
```
000010000010000010
```
Run:
```bash
echo Z | java -jar <jar> echo1.bs
```

### 2. Direct bitstream
```bash
echo X | java -jar <jar> -e 000010000010000010
```

### 3. Supplying input
| Scenario | Recommended | Notes |
|----------|-------------|-------|
| One‑shot literal | `echo ABC | java -jar <jar> ...` | Windows adds CRLF |
| Windows no newline | `cmd /c set /p=ABC<nul | java -jar <jar> ...` | Exact bytes only |
| Large / scripted | `java -jar <jar> prog.bs < input.txt` | Good for long feeds |
| Interactive | `java -jar <jar> prog.bs` | Line buffered; press Enter |

> Windows `echo ABC` sends `A B C \r \n`; the `\r` may appear in subsequent logic unless controlled.

### 4. Debug mode
```bash
java -jar <jar> -d prog.bs
java -jar <jar> -d -e 000010000010000010
```
Or: `BS_DEBUG=1` (same as adding `-d`).

Shows: instruction decode, memory pre/post, I/O events, PC changes.

### 5. Pure halt vs IO+halt
| Bitstream | Meaning | Behaviour |
|-----------|---------|-----------|
| `000000000000000010` | a,b no fn; c halt | Immediate return |
| `000010000010000010` | a=IN b=OUT c=HALT | Block for 1 byte → echo → halt |

---
## Command Line Options
```bash
java -jar <jar> [--lang zh|en] [-d] [-e <bitstream>] <filename>
```
| Form | Description | Notes |
|------|-------------|-------|
| `<filename>` | Load bitstream from file | Exclusive with raw -e (except with -d) |
| `-e <bitstream>` | Raw bitstream (0/1 only, blanks ignored) | No file read |
| `-d` | Debug mode | Can pair with file or -e |
| `--lang zh|en` | Override interface language | Higher than env `BS_LANG` |
| `-h/--help` | Print usage | No execution |

> `BS_VERBOSE=1` implies debug + final summary.

---
## Environment Variables & Priority
| Variable | Effect | Priority | Notes |
|----------|--------|----------|-------|
| `BS_LANG` | Default language | Low | Overridden by `--lang` |
| `BS_DEBUG` | Enable debug output | Medium | Equivalent to `-d` |
| `BS_VERBOSE` | Debug + end stats | High | Implies BS_DEBUG |

Priority: Command line > Environment variable.

---
## FAQ
### Q1: Program “hangs”?
Likely waiting for input (a segment function bit = IN). Provide a byte via pipe or type and press Enter.

### Q2: Minimal halt bitstream?
`000000000000000010` (or spaced: `000000 000000 000010`).

### Q3: Why not prompted for each character?
Terminal line buffering: entire line enters buffer; successive reads drain it. For closer per‑key behavior rely on the built‑in unbuffered mode + platform support (still may need a “raw” terminal).

### Q4: Windows newline issues?
Use:
```cmd
cmd /c set /p=ABC<nul | java -jar <jar> -e 000010000010000010
```

### Q5: Confirm fat JAR?
```bash
jar tf <jar> | grep jna
```
Presence of `com/sun/jna/` ⇒ deps included.

### Q6: Diagnose malformed bitstream?
- Run with `-d` to see early HALT / truncated decode
- Look for illegal `function=1 & link=1` segments (function ignored, address extended)
- Re-group every 6 bits while proofreading.

### Q7: Output looks garbled?
Output is raw low byte (`value & 0xFF`). Ensure ASCII or manage encoding externally.

### Q8: Need multi‑byte / wide char IO?
Not currently supported—design is byte oriented. You can layer a protocol on top.

---
## Build From Source
```bash
git clone https://github.com/MCLMLI/Bitwise-Subleq.git
cd Bitwise-Subleq/Interpreter/Java
# Windows
gradlew.bat clean build
# Linux / macOS
./gradlew clean build
ls build/libs
```
Produces: `build/libs/Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar`

### Project Layout (excerpt)
```
src/main/java/
  BSMain.java          # entry / args / usage
  BSInterpreter.java   # instruction loop & function bits
  BitReader.java       # self-terminating address decode
  UnbufferedInput.java # JNA raw input (Windows / POSIX)
  Lang.java            # simple i18n
```

---
## Technical Notes (Developers)
<details><summary>Expand / Collapse</summary>

### Function bit execution order
If any function bit is present: a(IN) → b(OUT) → c(HALT). Subleq arithmetic **skipped**; PC++.

### Runtime characteristics
| Aspect | Current Behaviour |
|--------|-------------------|
| Max instructions | 1,000,000 safeguard |
| Memory model | Sparse HashMap (default 0) |
| Value type | Java `int` (signed) |
| Input | Single byte, EOF→0 |
| Output | Low 8 bits directly |
| Thread safety | Not thread‑safe (single-thread design) |
| Dependencies | JNA / JNA-Platform |

### Potential enhancements
- Memory snapshot dump/load
- Interactive step / breakpoint REPL
- Execution metrics (function bit counts)

</details>

---
## License
AGPL-3.0 — modifications & network use must publish source. See [LICENSE](../../LICENSE).

---
**Maintainer**: MCLMLI  
**Last Updated**: 2025-01-04  
**Version**: 1.0  
Feedback: <https://github.com/MCLMLI/Bitwise-Subleq/issues>
