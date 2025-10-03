import java.io.IOException;

/**
 * 测试程序，用于调试地址解码
 * Test program to debug address decoding
 */
public class TestDecoding {
    public static void main(String[] args) throws IOException {
        // 检查语言设置
        if (args.length > 0 && args[0].equals("--lang")) {
            if (args.length > 1) {
                Lang.setLanguage(args[1]);
            }
        }

        System.out.println("=== " + Lang.get("逐位手动分析", "Manual bit-by-bit analysis") + " ===");

        System.out.println("\n" + Lang.get("测试", "Test") + ": 000000000000000 (15 bits)");
        manualDecode("000000000000000");

        System.out.println("\n" + Lang.get("测试", "Test") + ": 100010000111111 (15 bits)");
        manualDecode("100010000111111");

        System.out.println("\n\n=== " + Lang.get("简单 5 位解析（无自终止）", "Simple 5-bit parsing (no self-terminating)") + " ===");

        System.out.println("\n=== " + Lang.get("测试 1：无限循环", "Test 1: Infinite loop") + " ===");
        testSimple("000000000000000");

        System.out.println("\n=== " + Lang.get("测试 2：输出 'A'", "Test 2: Output 'A'") + " ===");
        testSimple("100010000111111");
    }

    private static void manualDecode(String bits) {
        System.out.println(Lang.get("比特", "Bits") + ": " + bits + " (" + Lang.get("长度", "length") + "=" + bits.length() + ")");
        for (int i = 0; i < bits.length(); i += 5) {
            if (i + 5 <= bits.length()) {
                String segment = bits.substring(i, i + 5);
                String data = segment.substring(0, 4);
                String link = segment.substring(4, 5);
                int dataVal = Integer.parseInt(data, 2);
                int segmentVal = Integer.parseInt(segment, 2);
                System.out.println("  " + Lang.get("位置", "Pos") + " " + i + "-" + (i+4) + ": " + segment +
                                 " = " + Lang.get("数据", "data") + ":" + data + "(" + dataVal + "), " +
                                 Lang.get("链接", "link") + ":" + link +
                                 " | " + Lang.get("作为 5 位值", "as 5-bit value") + ": " + segmentVal);
            }
        }
    }

    private static void testSimple(String bitstream) throws IOException {
        SimpleBitReader reader = new SimpleBitReader(bitstream);
        int instrNum = 0;

        while (reader.hasMore()) {
            int a = reader.readAddress();
            if (!reader.hasMore()) {
                System.out.println(Lang.get("指令", "Instruction") + " " + instrNum + ": a=" + a + " (" + Lang.get("不完整", "incomplete") + ")");
                break;
            }
            int b = reader.readAddress();
            if (!reader.hasMore()) {
                System.out.println(Lang.get("指令", "Instruction") + " " + instrNum + ": a=" + a + ", b=" + b + " (" + Lang.get("不完整", "incomplete") + ")");
                break;
            }
            int c = reader.readAddress();
            System.out.println(Lang.get("指令", "Instruction") + " " + instrNum + ": a=" + a + ", b=" + b + ", c=" + c);
            instrNum++;
        }
    }
}
