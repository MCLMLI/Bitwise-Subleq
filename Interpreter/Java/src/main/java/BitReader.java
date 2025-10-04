import java.io.IOException;

/**
 * BitReader 解析比特流并解码自终止地址
 * BitReader parses a bitstream and decodes self-terminating addresses.
 *
 * 新格式：每个地址段由6位组成：[dddd][s][l]
 * New format: Each address segment consists of 6 bits: [dddd][s][l]
 * - dddd: 4位数据位 / 4 data bits
 * - s: 功能位 / function bit
 * - l: 链接位 / link bit
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
     * 地址解析结果
     * Address parsing result
     */
    public static class AddressResult {
        public final int address;
        public final boolean hasFunction;

        public AddressResult(int address, boolean hasFunction) {
            this.address = address;
            this.hasFunction = hasFunction;
        }
    }

    /**
     * 读取一个自终止地址段（新格式）
     * Read a self-terminating address segment (new format).
     *
     * 格式：[dddd][s][l]
     * - s=0: 丢弃该段，不解析
     * - s=1且l=0: 有效段，hasFunction=true
     * - s=1且l=1: 错误，视为s=0处理
     *
     * 返回解码后的地址值和功能位状态
     * Returns the decoded address value and function bit status.
     */
    public AddressResult readAddress() throws IOException {
        int address = 0;
        int segmentCount = 0;
        boolean hasFunction = false;

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
                    bitsRead = i;
                    return new AddressResult(address, hasFunction);
                }
                data = (data << 1) | bit;
                bitsRead++;
            }

            // 读取功能位 / Read function bit
            int functionBit = readBit();
            if (functionBit == -1) {
                // EOF - 视为地址结束 / EOF - treat as end of address
                return new AddressResult(address, hasFunction);
            }

            // 读取链接位 / Read link bit
            int linkBit = readBit();
            if (linkBit == -1) {
                // EOF - 视为地址结束 / EOF - treat as end of address
                return new AddressResult(address, hasFunction);
            }

            // 处理功能位逻辑
            // Process function bit logic
            if (functionBit == 0) {
                // s=0: 丢弃该段，不解析 / s=0: discard this segment, don't parse
                // 不累积数据，继续读取（如果有链接）
                if (linkBit == 0) {
                    // 链接位也是0，地址结束 / link bit is also 0, address ends
                    break;
                }
                // 链接位为1，继续读取下一段 / link bit is 1, continue to next segment
                continue;
            } else {
                // s=1: 检查链接位 / s=1: check link bit
                if (linkBit == 1) {
                    // 错误：s=1且l=1，视为s=0处理
                    // Error: s=1 and l=1, treat as s=0
                    if (segmentCount == 0) {
                        // 如果这是第一段且出错，返回地址0
                        return new AddressResult(0, false);
                    }
                    // 否则忽略该段，地址结束
                    break;
                }

                // s=1且l=0：有效段 / s=1 and l=0: valid segment
                address = (address << 4) | data;
                hasFunction = true;
                segmentCount++;
                break; // l=0，地址结束 / l=0, address ends
            }
        }

        return new AddressResult(address, hasFunction);
    }

    /**
     * 获取当前在比特流中的位置
     * Get current position in bitstream
     */
    public int getPosition() {
        return position;
    }
}
