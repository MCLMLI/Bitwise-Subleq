import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 跨平台无缓冲字符输入类（使用 JNA 底层 API）
 * Cross-platform unbuffered character input class (using JNA native APIs)
 *
 * 实现真正的逐字符读取，按一个键立即响应，无需回车
 * Implements true character-by-character input, responds immediately on key press, no Enter needed
 */
public class UnbufferedInput {

    private static UnbufferedInput instance;
    private boolean terminalModeSet = false;
    private Termios originalTermios = null;
    private int originalConsoleMode = -1;
    private int consoleHandle = -1;

    // Windows API
    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        int GetStdHandle(int nStdHandle);
        boolean GetConsoleMode(int hConsoleHandle, int[] lpMode);
        boolean SetConsoleMode(int hConsoleHandle, int dwMode);
        boolean ReadConsoleInputA(int hConsoleInput, INPUT_RECORD[] lpBuffer, int nLength, int[] lpNumberOfEventsRead);
    }

    // Windows INPUT_RECORD structure
    @Structure.FieldOrder({"EventType", "Event"})
    public static class INPUT_RECORD extends Structure {
        public short EventType;
        public Event Event;

        public static class Event extends Structure {
            public byte[] data = new byte[16];

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("data");
            }
        }
    }

    // Linux/Unix API (libc)
    public interface CLibrary extends Library {
        CLibrary INSTANCE = Native.load("c", CLibrary.class);

        int tcgetattr(int fd, Termios termios);
        int tcsetattr(int fd, int optional_actions, Termios termios);
        int read(int fd, byte[] buffer, int count);
    }

    // Linux termios structure
    @Structure.FieldOrder({"c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "c_cc", "c_ispeed", "c_ospeed"})
    public static class Termios extends Structure {
        public int c_iflag;
        public int c_oflag;
        public int c_cflag;
        public int c_lflag;
        public byte c_line;
        public byte[] c_cc = new byte[32];
        public int c_ispeed;
        public int c_ospeed;
    }

    // Windows constants
    private static final int STD_INPUT_HANDLE = -10;
    private static final int ENABLE_LINE_INPUT = 0x0002;
    private static final int ENABLE_ECHO_INPUT = 0x0004;
    private static final int ENABLE_PROCESSED_INPUT = 0x0001;
    private static final int ENABLE_WINDOW_INPUT = 0x0008;
    private static final int ENABLE_MOUSE_INPUT = 0x0010;
    private static final int KEY_EVENT = 0x0001;

    // Linux constants
    private static final int STDIN_FILENO = 0;
    private static final int TCSANOW = 0;
    private static final int ICANON = 0x0002;
    private static final int ECHO = 0x0008;
    private static final int VMIN = 6;
    private static final int VTIME = 5;

    private UnbufferedInput() {
    }

    public static synchronized UnbufferedInput getInstance() {
        if (instance == null) {
            instance = new UnbufferedInput();
        }
        return instance;
    }

    /**
     * 读取单个字符（无缓冲，无回车）
     * Read a single character (unbuffered, no Enter needed)
     *
     * @return 读取的字节值，如果到达EOF返回-1
     * @throws IOException 如果读取失败
     */
    public int readChar() throws IOException {
        if (Platform.isWindows()) {
            return readCharWindows();
        } else if (Platform.isLinux() || Platform.isMac()) {
            return readCharUnix();
        } else {
            throw new IOException(Lang.get(
                "不支持的操作系统",
                "Unsupported operating system"
            ));
        }
    }

    private int readCharWindows() throws IOException {
        try {
            if (consoleHandle == -1) {
                consoleHandle = Kernel32.INSTANCE.GetStdHandle(STD_INPUT_HANDLE);
                if (consoleHandle == -1) {
                    throw new IOException(Lang.get(
                        "无法获取标准输入句柄",
                        "Cannot get standard input handle"
                    ));
                }
            }

            // 设置控制台模式（禁用行输入、回显等）
            if (!terminalModeSet) {
                int[] mode = new int[1];
                if (Kernel32.INSTANCE.GetConsoleMode(consoleHandle, mode)) {
                    originalConsoleMode = mode[0];
                    int newMode = mode[0] & ~(ENABLE_LINE_INPUT | ENABLE_ECHO_INPUT | ENABLE_PROCESSED_INPUT | ENABLE_WINDOW_INPUT | ENABLE_MOUSE_INPUT);
                    Kernel32.INSTANCE.SetConsoleMode(consoleHandle, newMode);
                    terminalModeSet = true;
                }
            }

            // 使用 System.in.read() 在原始模式下读取单个字节
            int ch = System.in.read();
            return ch;

        } catch (Exception e) {
            throw new IOException(Lang.get(
                "读取字符失败: " + e.getMessage(),
                "Failed to read character: " + e.getMessage()
            ), e);
        }
    }

    private int readCharUnix() throws IOException {
        try {
            // 设置终端为原始模式（禁用行缓冲和回显）
            if (!terminalModeSet) {
                originalTermios = new Termios();
                if (CLibrary.INSTANCE.tcgetattr(STDIN_FILENO, originalTermios) != 0) {
                    throw new IOException(Lang.get(
                        "无法获取终端属性",
                        "Cannot get terminal attributes"
                    ));
                }

                Termios newTermios = new Termios();
                if (CLibrary.INSTANCE.tcgetattr(STDIN_FILENO, newTermios) != 0) {
                    throw new IOException(Lang.get(
                        "无法获取终端属性",
                        "Cannot get terminal attributes"
                    ));
                }

                // 禁用 ICANON (行缓冲) 和 ECHO (回显)
                newTermios.c_lflag &= ~(ICANON | ECHO);
                newTermios.c_cc[VMIN] = 1;  // VMIN = 1 (至少读取1个字符)
                newTermios.c_cc[VTIME] = 0;  // VTIME = 0 (无超时)

                if (CLibrary.INSTANCE.tcsetattr(STDIN_FILENO, TCSANOW, newTermios) != 0) {
                    throw new IOException(Lang.get(
                        "无法设置终端属性",
                        "Cannot set terminal attributes"
                    ));
                }
                terminalModeSet = true;
            }

            byte[] buffer = new byte[1];
            int result = CLibrary.INSTANCE.read(STDIN_FILENO, buffer, 1);

            if (result > 0) {
                return buffer[0] & 0xFF;
            }

            return -1; // EOF
        } catch (Exception e) {
            throw new IOException(Lang.get(
                "读取字符失败: " + e.getMessage(),
                "Failed to read character: " + e.getMessage()
            ), e);
        }
    }

    /**
     * 恢复终端设置（在程序结束时调用）
     * Restore terminal settings (call at program end)
     */
    public void restore() {
        if (Platform.isWindows()) {
            if (terminalModeSet && consoleHandle != -1 && originalConsoleMode != -1) {
                try {
                    Kernel32.INSTANCE.SetConsoleMode(consoleHandle, originalConsoleMode);
                } catch (Exception ignored) {
                }
            }
        } else if (Platform.isLinux() || Platform.isMac()) {
            if (terminalModeSet && originalTermios != null) {
                try {
                    CLibrary.INSTANCE.tcsetattr(STDIN_FILENO, TCSANOW, originalTermios);
                } catch (Exception ignored) {
                }
            }
        }
        terminalModeSet = false;
    }

    /**
     * 检查是否支持无缓冲输入
     * Check if unbuffered input is supported
     */
    public static boolean isSupported() {
        return Platform.isWindows() || Platform.isLinux() || Platform.isMac();
    }
}
