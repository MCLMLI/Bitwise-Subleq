import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BS (Bitwise Subleq) 解释器
 * BS (Bitwise Subleq) Interpreter
 *
 * 新格式支持功能位：
 * New format supports function bits:
 * - a段功能位=1: 阻塞输入 / a function bit=1: blocking input
 * - b段功能位=1: 输出 / b function bit=1: output
 * - c段功能位=1: 停机 / c function bit=1: halt
 */
public class BSInterpreter {
    private static class Instruction {
        int a, b, c;
        boolean aFunc, bFunc, cFunc;

        Instruction(int a, int b, int c, boolean aFunc, boolean bFunc, boolean cFunc) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.aFunc = aFunc;
            this.bFunc = bFunc;
            this.cFunc = cFunc;
        }

        @Override
        public String toString() {
            return "a=" + a + (aFunc ? "[IN]" : "") +
                   ", b=" + b + (bFunc ? "[OUT]" : "") +
                   ", c=" + c + (cFunc ? "[HALT]" : "");
        }
    }

    private final List<Instruction> program;
    private final Map<Integer, Integer> memory;
    private int pc;
    private boolean halted;
    private int instructionCount;
    private boolean debug;
    private final UnbufferedInput unbufferedInput;

    public BSInterpreter(String bitstream) throws IOException {
        this(bitstream, false);
    }

    public BSInterpreter(String bitstream, boolean debug) throws IOException {
        this.program = new ArrayList<>();
        this.memory = new HashMap<>();
        this.pc = 0;
        this.halted = false;
        this.instructionCount = 0;
        this.debug = debug;
        this.unbufferedInput = UnbufferedInput.getInstance();

        loadProgram(bitstream);
    }

    private void loadProgram(String bitstream) throws IOException {
        BitReader reader = new BitReader(bitstream);

        while (reader.hasMore()) {
            try {
                BitReader.AddressResult aResult = reader.readAddress();
                if (!reader.hasMore()) {
                    if (debug) System.err.println(Lang.get(
                        "警告：不完整的指令（只有 a=" + aResult.address + "）",
                        "Warning: Incomplete instruction (only a=" + aResult.address + ")"
                    ));
                    break;
                }

                BitReader.AddressResult bResult = reader.readAddress();
                if (!reader.hasMore()) {
                    if (debug) System.err.println(Lang.get(
                        "警告：不完整的指令（a=" + aResult.address + ", b=" + bResult.address + "）",
                        "Warning: Incomplete instruction (a=" + aResult.address + ", b=" + bResult.address + ")"
                    ));
                    break;
                }

                BitReader.AddressResult cResult = reader.readAddress();

                Instruction instr = new Instruction(
                    aResult.address, bResult.address, cResult.address,
                    aResult.hasFunction, bResult.hasFunction, cResult.hasFunction
                );

                if (debug) System.err.println(Lang.get(
                    "已加载指令 " + program.size() + ": " + instr,
                    "Loaded instruction " + program.size() + ": " + instr
                ));
                program.add(instr);
            } catch (IOException e) {
                if (debug) System.err.println(Lang.get(
                    "加载程序时出错：" + e.getMessage(),
                    "Error loading program: " + e.getMessage()
                ));
                break;
            }
        }

        if (debug) System.err.println(Lang.get(
            "已加载 " + program.size() + " 条指令",
            "Loaded " + program.size() + " instructions"
        ));
    }

    private int readMem(int address) {
        return memory.getOrDefault(address, 0);
    }

    private void writeMem(int address, int value) {
        memory.put(address, value);
    }

    public void execute() throws IOException {
        while (!halted && pc >= 0 && pc < program.size()) {
            Instruction instr = program.get(pc);
            instructionCount++;

            if (debug) {
                System.err.println("\nPC=" + pc + ", " + Lang.get("指令", "Instr") + ": " + instr);
                System.err.println("  " + Lang.get("执行前", "Before") + ": mem[" + instr.a + "]=" + readMem(instr.a) +
                                 ", mem[" + instr.b + "]=" + readMem(instr.b));
            }

            executeInstruction(instr);

            if (instructionCount > 1000000) {
                System.err.println("\n" + Lang.get(
                    "警告：已执行 1,000,000 条指令。停止。",
                    "Warning: Executed 1,000,000 instructions. Stopping."
                ));
                break;
            }
        }

        if (debug) System.err.println("\n" + Lang.get(
            "执行完成。总指令数：" + instructionCount,
            "Execution finished. Total instructions: " + instructionCount
        ));
    }

    private void executeInstruction(Instruction instr) throws IOException {
        boolean hasAnyFunction = instr.aFunc || instr.bFunc || instr.cFunc;

        // 按顺序执行功能位：a段（输入）→ b段（输出）→ c段（停机）
        // Execute function bits in order: a (input) → b (output) → c (halt)

        // 1. 检查a段输入功能位 / Check a segment input function bit
        if (instr.aFunc) {
            int input = unbufferedInput.readChar();
            if (input == -1) {
                // EOF encountered - treat as 0 and set halt flag
                input = 0;
                if (debug) System.err.println("  " + Lang.get(
                    "警告：遇到输入流结束（EOF），将作为0处理",
                    "Warning: EOF encountered, treating as 0"
                ));
            }
            writeMem(instr.a, input);
            if (debug) System.err.println("  " + Lang.get(
                "输入：读取字节 " + input + " 到地址 " + instr.a,
                "INPUT: read byte " + input + " to address " + instr.a
            ));
        }

        // 2. 检查b段输出功能位 / Check b segment output function bit
        if (instr.bFunc) {
            int value = readMem(instr.b);
            System.out.write(value & 0xFF);
            System.out.flush();
            if (debug) System.err.println("  " + Lang.get(
                "输出：写入字节 " + (value & 0xFF) + " ('" + (char)(value & 0xFF) + "') 从地址 " + instr.b,
                "OUTPUT: wrote byte " + (value & 0xFF) + " ('" + (char)(value & 0xFF) + "') from address " + instr.b
            ));
        }

        // 3. 检查c段停机功能位 / Check c segment halt function bit
        if (instr.cFunc) {
            if (debug) System.err.println("  " + Lang.get("停机（c功能位）", "HALT (c function bit)"));
            halted = true;
            return;
        }

        // 如果有任何功能位被执行，则只递增PC，不执行正常的Subleq指令
        // If any function bit was executed, just increment PC, don't execute normal Subleq
        if (hasAnyFunction) {
            pc++;
            return;
        }

        // 正常Subleq指令 / Normal Subleq instruction
        int valA = readMem(instr.a);
        int valB = readMem(instr.b);
        int result = valB - valA;
        writeMem(instr.b, result);

        if (debug) System.err.println("  mem[" + instr.b + "] = " + valB + " - " + valA + " = " + result);

        // 跳转或继续 / Jump or continue
        if (result <= 0) {
            if (debug) System.err.println("  " + Lang.get("跳转到 ", "JUMP to ") + instr.c);
            pc = instr.c;
        } else {
            if (debug) System.err.println("  " + Lang.get("继续到 ", "CONTINUE to ") + (pc + 1));
            pc++;
        }
    }

    public boolean isHalted() {
        return halted;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public int getProgramSize() {
        return program.size();
    }
}
