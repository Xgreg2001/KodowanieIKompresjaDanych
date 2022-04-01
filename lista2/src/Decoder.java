import java.io.*;

public class Decoder {

    private static long totalCount;
    private static long l = 0;
    private static long u = 0xffffffffL;
    private static final long[] cumCount = new long[258];
    private static final long[] dict = new long[257];
    private static int readBuffer = 0;
    private static int bitsInReadBuffer = 0;
    private static long t;
    private static BufferedOutputStream outStream;
    private static BufferedInputStream stream;
    private static boolean finished = false;


    public static void main(String[] args) {
        totalCount = Commons.initializeDictionaries(dict, cumCount);

        File initialFile = new File(args[0]);
        File destinationFile = new File(args[1]);
        try {
            DataInputStream tempStream = new DataInputStream(new FileInputStream(initialFile));
            t = Integer.toUnsignedLong(tempStream.readInt());
            stream = new BufferedInputStream(tempStream);
            outStream = new BufferedOutputStream(new PrintStream(destinationFile));
            while (!finished) {
                decode();
            }
            outStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decode() throws IOException {
        int k = 0;
        long temp = ((t - l + 1) * totalCount - 1) / (u - l + 1);
        while (temp >= cumCount[k + 1]) {
            k++;
        }
        if (k == 256) {
            finished = true;
            return;
        }
        send(k);
        updateBounds(k);
        totalCount = Commons.updateDict(k, totalCount, dict, cumCount);
    }

    private static void updateBounds(int x) throws IOException {
        x = x & 0xff;
        long prevL = l;
        l = l + ((u - l + 1) * cumCount[x] / totalCount);
        u = prevL + ((u - prevL + 1) * cumCount[x + 1] / totalCount) - 1;


        while ((l & 0x80000000L) == (u & 0x80000000L) || ((l & 0x40000000L) == 0x40000000L) && ((u & 0x40000000L) == 0)) {
            if ((l & 0x80000000L) == (u & 0x80000000L)) {
                l = l << 1 & 0xffffffffL;
                u = u << 1 & 0xffffffffL | 0x1L;
                updateTag();
            }
            if ((l & 0x40000000L) == 0x40000000L && (u & 0x40000000L) == 0) {
                l = l << 1 & 0xffffffffL ^ 0x80000000L;
                u = u << 1 & 0xffffffffL | 0x1L ^ 0x80000000L;
                updateTag();

                t = t ^ 0x80000000L;
            }
        }
    }

    private static void updateTag() throws IOException {
        if (bitsInReadBuffer == 0) {
            readBuffer = stream.read();
            if (readBuffer == -1) {
                finished = true;
            }
            bitsInReadBuffer = 8;
        }
        t = ((t << 1) & 0xffffffffL) | ((readBuffer >>> 7) & 0x1);
        readBuffer = readBuffer << 1;
        bitsInReadBuffer--;
    }

    private static void send(int k) throws IOException {
        outStream.write(k);
    }
}
