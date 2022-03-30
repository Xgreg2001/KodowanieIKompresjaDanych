import java.io.*;
import java.nio.ByteBuffer;

public class Encoder {

    private static long totalCount;
    private static long l = 0;
    private static long u = 0xffffffffL;
    static long[] dict;
    private static final long[] cumCount = new long[257];
    private static int scale3;
    private static long buffer = 0;
    private static int bitsInBuffer = 0;
    private static BufferedOutputStream outStream;
    private static long bytesRead = 0;

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

    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static void main(String[] args) {
        initializeDictionaries();
        File initialFile = new File(args[0]);
        File destination = new File(args[1]);
        long numberOfBytes = initialFile.length();
        try {

            BufferedInputStream stream = new BufferedInputStream(new DataInputStream(new FileInputStream(initialFile)));
            outStream = new BufferedOutputStream(new PrintStream(destination));
            outStream.write(longToBytes(numberOfBytes));
            while (bytesRead < numberOfBytes) {
                int b = stream.read();
                bytesRead++;
                encode(b);
                updateDict(b);
            }
            writeReminder();
            stream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProcessBuilder builder = new ProcessBuilder("/usr/local/julia-1.7.2/bin/julia", "lista1/entropia.jl", args[1]);
        builder.inheritIO();
        try {
            Process p = builder.start();
            p.waitFor();
            System.out.println((double) destination.length() / (double) bytesRead * 8.0);
            System.out.println((double) destination.length() / (double) initialFile.length());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void writeReminder() throws IOException {
        for (int i = 0; i < 32; i++) {
            send(l >>> 31 & 0x1);
            while (scale3 > 0) {
                send(l >>> 31 & 0x1 ^ 0x1);
                scale3--;
            }
            l = l << 1 & 0xffffffffL;
        }
        buffer = buffer << (32 - bitsInBuffer);
        outStream.write(intToBytes((int) buffer));
        outStream.flush();
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

    private static void encode(int b) throws IOException {
        int x = b & 0xff;
        long prevL = l;
        l = l + ((u - l + 1) * cumCount[x] / totalCount);
        u = prevL + ((u - prevL + 1) * cumCount[x + 1] / totalCount) - 1;

        while ((l & 0x80000000L) == (u & 0x80000000L) || (l & 0x40000000L) == 0x40000000L && (u & 0x40000000L) == 0) {
            if ((l & 0x80000000L) == (u & 0x80000000L)) {
                long bit = (l & 0xffffffffL) >>> 31;
                send(bit);
                l = l << 1 & 0xffffffffL;
                u = u << 1 & 0xffffffffL | 0x1L;
                while (scale3 > 0) {
                    send(bit ^ 0xfL);
                    scale3--;
                }
            }
            if ((l & 0x40000000L) == 0x40000000L && (u & 0x40000000L) == 0) {
                l = l << 1 & 0xffffffffL ^ 0x80000000L;
                u = u << 1 & 0xffffffffL | 0x1L ^ 0x80000000L;
                scale3++;
            }
        }
    }

    private static void send(long lMsb) throws IOException {
        buffer = buffer << 1 | (lMsb & 0x1);
        bitsInBuffer++;
        if (bitsInBuffer == 32) {
            outStream.write(intToBytes((int) buffer));
            buffer = 0L;
            bitsInBuffer = 0;
        }
    }
}
