import java.io.IOException;

/**
 * BitReader 解析比特流并解码自终止地址
 * BitReader parses a bitstream and decodes self-terminating addresses.
 *
 * 新格式：每个地址段由6位组成：[dddd][s][l]
 * New format: Each address segment consists of 6 bits: [dddd][s][l]
 * - dddd: 4位数据位（总是累积到地址） / 4 data bits (always accumulate to address)
 * - s: 功能位（不作为数据，仅标记） / function bit (not data, only marker)
 * - l: 链接位（不作为数据，控制是否继续） / link bit (not data, controls continuation)
 *
 * 解析规则：
 * - s=0: 该段没有功能位标记
 * - s=1且l=0: 该段有功能位标记，地址结束
 * - s=1且l=1: 错误情况，视为s=0处理
 * - l=0: 地址结束
 * - l=1: 继续读取下一段
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
     * - 数据位（dddd）总是累积到地址
     * - s=0: 无功能位标记
     * - s=1且l=0: 有功能位标记，地址结束
     * - s=1且l=1: 错误，视为s=0处理
     * - l=0: 地址结束
     * - l=1: 继续读取下一段（数据左移4位）
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
                    address = (address << 4) | data;
                    return new AddressResult(address, hasFunction);
                }
                data = (data << 1) | bit;
            }

            // 读取功能位 / Read function bit (不作为数据)
            int functionBit = readBit();
            if (functionBit == -1) {
                // EOF - 累积数据并结束 / EOF - accumulate data and end
                address = (address << 4) | data;
                return new AddressResult(address, hasFunction);
            }

            // 读取链接位 / Read link bit (不作为数据)
            int linkBit = readBit();
            if (linkBit == -1) {
                // EOF - 累积数据并结束 / EOF - accumulate data and end
                address = (address << 4) | data;
                return new AddressResult(address, hasFunction);
            }

            // 累积数据位到地址 / Accumulate data bits to address
            address = (address << 4) | data;
            segmentCount++;

            // 处理功能位逻辑 / Process function bit logic
            if (functionBit == 1) {
                if (linkBit == 0) {
                    // s=1且l=0：有效的功能位标记 / s=1 and l=0: valid function bit marker
                    hasFunction = true;
                } else {
                    // s=1且l=1：错误，视为s=0 / s=1 and l=1: error, treat as s=0
                    // 不设置hasFunction，但仍然累积了数据
                }
            }

            // 检查链接位 / Check link bit
            if (linkBit == 0) {
                // l=0: 地址结束 / l=0: address ends
                break;
            }
            // l=1: 继续读取下一段 / l=1: continue to next segment
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
