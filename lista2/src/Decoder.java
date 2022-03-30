import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Decoder {

    private static long totalCount;
    private static long l = 0;
    private static long u = 0xffffffffL;
    private static long[] cumCount = new long[257];
    private static long[] dict;
    private static int writeBuffer = 0;
    private static int bytesInWriteBuffer = 0;
    private static int readBuffer = 0;
    private static int bitsInReadBuffer = 0;
    private static long t;
    private static long prevL;
    private static long numberOfBytes;

    private static void initializeDictionaries() {
        dict = new long[265];
        for (int b = 0; b < 256; b++) {
            dict[b] = 1;
        }
        totalCount = 256;
        for (int i = 0; i < 257; i++) {
            cumCount[i] = i;
        }
    }

    private static void updateDict(int b) {
        dict[b & 0xff]++;
        totalCount++;
        // reskalujemy jeśli suma w słowniku równa niż 2^30 - 1
        if (totalCount == 1073741823) {
            for (int i = 0; i < 256; i++) {
                long temp = dict[i];
                dict[i] = (long) (Math.ceil((double) dict[i] / 2.0));
                totalCount -= temp - dict[i];
            }
            //recalculate cumCount
            cumCount[0] = 0;
            for (int i = 1; i < 257; i++) {
                cumCount[i] = cumCount[i - 1] + dict[i - 1];
            }
        } else {
            //hope it is unsigned
            for (int i = b & 0xff; i < 256; i++) {
                cumCount[i + 1]++;
            }
        }
    }

    public static void main(String[] args) {
        initializeDictionaries();

        String filename = args[0];
        File initialFile = new File(filename);
        try {
            DataInputStream stream = new DataInputStream(new FileInputStream(initialFile));
            numberOfBytes = stream.readLong();
            t = Integer.toUnsignedLong(stream.readInt());
            while (numberOfBytes > 0) {
                decode(stream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decode(DataInputStream stream) throws IOException {
        int k = 0;
        long temp = ((t - l + 1) * totalCount - 1) / (u - l + 1);
        while (temp >= cumCount[k + 1]) {
            k++;
        }
        send(k);
        updateBounds(k, stream);
        updateDict(k);
    }

    private static void updateBounds(int x, DataInputStream stream) throws IOException {
        x = x & 0xff;
        prevL = l;
        l = l + ((u - l + 1) * cumCount[x] / totalCount);
        u = prevL + ((u - prevL + 1) * cumCount[x + 1] / totalCount) - 1;

        //can be optimised
        long lMsb = (l & 0xffffffffL) >>> 31;
        long uMsb = (u & 0xffffffffL) >>> 31;
        long lSmsb = (l & 0x7fffffffL) >>> 30;
        long uSmsb = (u & 0x7fffffffL) >>> 30;

        while (lMsb == uMsb || (lSmsb == 1 && uSmsb == 0)) {
            if (lMsb == uMsb) {
                l = l << 1 & 0xffffffffL;
                u = u << 1 & 0xffffffffL | 0x1L;
                updateTag(stream);

                lMsb = (l & 0xffffffffL) >>> 31;
                uMsb = (u & 0xffffffffL) >>> 31;
                lSmsb = (l & 0x7fffffffL) >>> 30;
                uSmsb = (u & 0x7fffffffL) >>> 30;
            }
            if (lSmsb == 1 && uSmsb == 0) {
                l = l << 1 & 0xffffffffL ^ 0x80000000L;
                u = u << 1 & 0xffffffffL | 0x1L ^ 0x80000000L;
                updateTag(stream);

                t = t ^ 0x80000000L;

                lMsb = (l & 0xffffffffL) >>> 31;
                uMsb = (u & 0xffffffffL) >>> 31;
                lSmsb = (l & 0x7fffffffL) >>> 30;
                uSmsb = (u & 0x7fffffffL) >>> 30;
            }
        }
    }

    private static void updateTag(DataInputStream stream) throws IOException {
        if (bitsInReadBuffer == 0) {
            readBuffer = stream.readByte() & 0xff;
            bitsInReadBuffer = 8;
        }
        t = ((t << 1) & 0xffffffffL) | ((readBuffer >>> 7) & 0x1);
        readBuffer = readBuffer << 1;
        bitsInReadBuffer--;
    }

    private static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    private static void send(int k) throws IOException {
        numberOfBytes--;
        System.out.write(k);
        System.out.flush();
    }
}
