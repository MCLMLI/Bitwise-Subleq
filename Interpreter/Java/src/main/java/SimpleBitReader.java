import java.io.IOException;

/**
 * 简单的 BitReader，将每 5 位视为一个完整地址
 * Simple BitReader that treats every 5 bits as a complete address
 */
public class SimpleBitReader {
    private final String bitstream;
    private int position;

    public SimpleBitReader(String bitstream) {
        this.bitstream = bitstream.replaceAll("\\s+", "");
        this.position = 0;
    }

    public boolean hasMore() {
        return position + 5 <= bitstream.length();
    }

    public int readAddress() throws IOException {
        if (position + 5 > bitstream.length()) {
            throw new IOException(Lang.get(
                "位置 " + position + " 处没有足够的位来读取地址",
                "Not enough bits for address at position " + position
            ));
        }

        String segment = bitstream.substring(position, position + 5);
        position += 5;

        if (segment.equals("11111")) {
            return -1;
        }

        return Integer.parseInt(segment, 2);
    }

    public int getPosition() {
        return position;
    }
}
