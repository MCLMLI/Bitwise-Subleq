import java.io.IOException;

/**
 * BitReader 解析比特流并解码自终止地址
 * BitReader parses a bitstream and decodes self-terminating addresses.
 */
public class BitReader {
    private final String bitstream;
    private int position;

    public BitReader(String bitstream) {
        this.bitstream = bitstream.replaceAll("\\s+", "");
        this.position = 0;
    }

    /**
     * 检查是否还有更多位可读
     * Check if there are more bits to read
     */
    public boolean hasMore() {
        return position < bitstream.length();
    }

    /**
     * 读取单个比特，EOF 时返回 -1
     * Read a single bit, returns -1 on EOF
     */
    private int readBit() {
        if (position >= bitstream.length()) {
            return -1;
        }
        char c = bitstream.charAt(position++);
        if (c == '0') return 0;
        if (c == '1') return 1;
        return -1; // 无效字符视为 EOF / Invalid character treated as EOF
    }

    /**
     * 读取一个自终止地址段
     * Read a self-terminating address segment.
     *
     * 返回解码后的地址值
     * Returns the decoded address value.
     *
     * 特殊情况：如果所有段中的位都是 1，则返回 -1
     * Special case: if all bits in segments are 1s, returns -1
     */
    public int readAddress() throws IOException {
        int address = 0;
        boolean allOnes = true;
        int segmentCount = 0;
        boolean hasData = false;

        while (true) {
            // 读取 4 位数据 / Read 4 data bits
            int data = 0;
            int bitsRead = 0;
            for (int i = 0; i < 4; i++) {
                int bit = readBit();
                if (bit == -1) {
                    // 数据位期间遇到 EOF / EOF during data bits
                    if (segmentCount == 0 && i == 0) {
                        throw new IOException(Lang.get(
                            "位流在位置 " + position + " 处意外结束",
                            "Unexpected end of bitstream at position " + position
                        ));
                    }
                    // 部分段 - 剩余部分视为 0 并结束 / Partial segment - treat remaining as 0s and end
                    data = data << (4 - i);
                    allOnes = false;
                    hasData = true;
                    bitsRead = i;
                    break;
                }
                data = (data << 1) | bit;
                if (bit == 0) allOnes = false;
                bitsRead++;
                hasData = true;
            }

            // 读取链接位 / Read link bit
            int link = readBit();
            if (link == -1) {
                // EOF - 视为地址结束 (链接位=0) / EOF - treat as end of address (link=0)
                link = 0;
            } else if (link == 0) {
                allOnes = false;
            }

            // 累积地址 / Accumulate address
            address = (address << 4) | data;
            segmentCount++;

            // 检查链接位是否为 0（地址结束）或 EOF
            // Check if link bit is 0 (end of address) or EOF
            if (link == 0 || !hasMore()) {
                break;
            }
        }

        // 检测 -1：为简单起见，检查累积值是否等于所有可能的 1
        // Detect -1: For simplicity, check if accumulated value equals all possible 1s
        // 对于单个 5 位段 "11111"：数据=1111(15)，链接=1
        // For single 5-bit segment "11111": data=1111(15), link=1
        // 但如果所有段中都是 1，则为 -1
        // But if we have all 1s in all segments, it's -1
        if (allOnes && segmentCount == 1 && address == 15) {
            // 单段，数据部分全为 1 且链接=1 / Single segment with all 1s in data part and link=1
            return -1;
        }

        return address;
    }

    /**
     * 获取当前在比特流中的位置
     * Get current position in bitstream
     */
    public int getPosition() {
        return position;
    }
}
