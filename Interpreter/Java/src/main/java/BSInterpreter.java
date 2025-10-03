import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BS (Bitwise Subleq) 解释器
 * BS (Bitwise Subleq) Interpreter
 */
public class BSInterpreter {
    private static class Instruction {
        int a, b, c;
        Instruction(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public String toString() {
            return "a=" + a + ", b=" + b + ", c=" + c;
        }
    }

    private final List<Instruction> program;
    private final Map<Integer, Integer> memory;
    private int pc;
    private boolean halted;
    private int instructionCount;
    private boolean debug;

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

        loadProgram(bitstream);
    }

    private void loadProgram(String bitstream) throws IOException {
        BitReader reader = new BitReader(bitstream);

        while (reader.hasMore()) {
            try {
                int a = reader.readAddress();
                if (!reader.hasMore()) {
                    if (debug) System.err.println(Lang.get(
                        "警告：不完整的指令（只有 a=" + a + "）",
                        "Warning: Incomplete instruction (only a=" + a + ")"
                    ));
                    break;
                }
                int b = reader.readAddress();
                if (!reader.hasMore()) {
                    if (debug) System.err.println(Lang.get(
                        "警告：不完整的指令（a=" + a + ", b=" + b + "）",
                        "Warning: Incomplete instruction (a=" + a + ", b=" + b + ")"
                    ));
                    break;
                }
                int c = reader.readAddress();

                Instruction instr = new Instruction(a, b, c);
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
        if (address == -1) return -1;
        return memory.getOrDefault(address, 0);
    }

    private void writeMem(int address, int value) {
        if (address != -1) {
            memory.put(address, value);
        }
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

            executeInstruction(instr.a, instr.b, instr.c);

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

    private void executeInstruction(int a, int b, int c) throws IOException {
        // I/O: 输入
        if (a == -1 && b == -1) {
            int input = System.in.read();
            if (input == -1) input = 0;
            memory.put(-2, input);
            if (debug) System.err.println("  " + Lang.get(
                "输入：读取字节 " + input,
                "INPUT: read byte " + input
            ));
            pc++;
            return;
        }

        // I/O: 输出
        if (b == -1 && a != -1) {
            int value = readMem(a);
            System.out.write(value & 0xFF);
            System.out.flush();
            if (debug) System.err.println("  " + Lang.get(
                "输出：写入字节 " + (value & 0xFF) + " ('" + (char)(value & 0xFF) + "')",
                "OUTPUT: wrote byte " + (value & 0xFF) + " ('" + (char)(value & 0xFF) + "')"
            ));
            pc++;
            return;
        }

        // 正常指令
        int valA = readMem(a);
        int valB = readMem(b);
        int result = valB - valA;
        writeMem(b, result);

        if (debug) System.err.println("  mem[" + b + "] = " + valB + " - " + valA + " = " + result);

        // 停机
        if (c == -1 && result <= 0) {
            if (debug) System.err.println("  " + Lang.get("停机", "HALT"));
            halted = true;
            return;
        }

        // 跳转或继续
        if (result <= 0) {
            if (debug) System.err.println("  " + Lang.get("跳转到 ", "JUMP to ") + c);
            pc = c;
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
