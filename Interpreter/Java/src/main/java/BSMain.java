import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * BS (Bitwise Subleq) 解释器的主入口点
 * Main entry point for the BS (Bitwise Subleq) interpreter
 */
public class BSMain {

    public static void main(String[] args) {
        // 注册 shutdown hook 以恢复终端设置
        // Register shutdown hook to restore terminal settings
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            UnbufferedInput.getInstance().restore();
        }));

        // 检查语言设置（默认中文）
        String langEnv = System.getenv("BS_LANG");
        if (langEnv != null) {
            Lang.setLanguage(langEnv);
        }

        if (args.length == 0) {
            printUsage();
            return;
        }

        // 检查帮助参数
        if (args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
            return;
        }

        try {
            String bitstream;
            boolean debug = System.getenv("BS_DEBUG") != null || System.getenv("BS_VERBOSE") != null;

            // 处理语言参数
            int argOffset = 0;
            if (args[0].equals("--lang") && args.length > 1) {
                Lang.setLanguage(args[1]);
                argOffset = 2;
            }

            if (argOffset >= args.length) {
                printUsage();
                return;
            }

            // 再次检查帮助参数（在 --lang 之后）
            if (args[argOffset].equals("-h") || args[argOffset].equals("--help")) {
                printUsage();
                return;
            }

            if (args[argOffset].equals("-e")) {
                if (args.length < argOffset + 2) {
                    System.err.println(Lang.get(
                        "错误：-e 需要一个比特流参数",
                        "Error: -e requires a bitstream argument"
                    ));
                    printUsage();
                    return;
                }
                bitstream = args[argOffset + 1];
            } else if (args[argOffset].equals("-d")) {
                debug = true;
                if (args.length < argOffset + 2) {
                    System.err.println(Lang.get(
                        "错误：-d 需要一个比特流或文件名",
                        "Error: -d requires a bitstream or filename"
                    ));
                    printUsage();
                    return;
                }
                if (args[argOffset + 1].equals("-e") && args.length >= argOffset + 3) {
                    bitstream = args[argOffset + 2];
                } else {
                    bitstream = new String(Files.readAllBytes(Paths.get(args[argOffset + 1])));
                }
            } else {
                bitstream = new String(Files.readAllBytes(Paths.get(args[argOffset])));
            }

            BSInterpreter interpreter = new BSInterpreter(bitstream, debug);

            if (debug) {
                System.err.println(Lang.get(
                    "开始执行 " + interpreter.getProgramSize() + " 条指令...\n",
                    "Starting execution of " + interpreter.getProgramSize() + " instructions...\n"
                ));
            }

            interpreter.execute();

            if (System.getenv("BS_VERBOSE") != null) {
                System.err.println("\n" + Lang.get(
                    "已执行 " + interpreter.getInstructionCount() + " 条指令",
                    "Executed " + interpreter.getInstructionCount() + " instructions"
                ));
                System.err.println(Lang.get(
                    "已停机：" + interpreter.isHalted(),
                    "Halted: " + interpreter.isHalted()
                ));
            }

        } catch (IOException e) {
            System.err.println(Lang.get("错误：", "Error: ") + e.getMessage());
            if (System.getenv("BS_DEBUG") != null) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private static void printUsage() {
        if (Lang.isChinese()) {
            System.out.println("BS (Bitwise Subleq) 解释器");
            System.out.println();
            System.out.println("用法：");
            System.out.println("  java BSMain [--lang zh|en] <filename>          从文件运行 BS 程序");
            System.out.println("  java BSMain [--lang zh|en] -e <bitstream>      从命令行运行 BS 程序");
            System.out.println("  java BSMain [--lang zh|en] -d <filename>       以调试模式运行");
            System.out.println("  java BSMain [--lang zh|en] -d -e <bitstream>   以调试模式运行比特流");
            System.out.println();
            System.out.println("示例：");
            System.out.println("  无限循环：    java BSMain -e 000000000000000");
            System.out.println("  输出测试：    java BSMain -e 100010000111111");
            System.out.println("  英文模式：    java BSMain --lang en -e 000000000000000");
            System.out.println();
            System.out.println("环境变量：");
            System.out.println("  BS_LANG=zh|en     设置语言（默认：zh）");
            System.out.println("  BS_DEBUG=1        启用调试输出");
            System.out.println("  BS_VERBOSE=1      打印执行统计信息");
        } else {
            System.out.println("BS (Bitwise Subleq) Interpreter");
            System.out.println();
            System.out.println("Usage:");
            System.out.println("  java BSMain [--lang zh|en] <filename>          Run BS program from file");
            System.out.println("  java BSMain [--lang zh|en] -e <bitstream>      Run BS program from command line");
            System.out.println("  java BSMain [--lang zh|en] -d <filename>       Run in debug mode");
            System.out.println("  java BSMain [--lang zh|en] -d -e <bitstream>   Run bitstream in debug mode");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("  Infinite loop:    java BSMain -e 000000000000000");
            System.out.println("  Output test:      java BSMain -e 100010000111111");
            System.out.println("  Chinese mode:     java BSMain --lang zh -e 000000000000000");
            System.out.println();
            System.out.println("Environment:");
            System.out.println("  BS_LANG=zh|en     Set language (default: zh)");
            System.out.println("  BS_DEBUG=1        Enable debug output");
            System.out.println("  BS_VERBOSE=1      Print execution statistics");
        }
    }
}
