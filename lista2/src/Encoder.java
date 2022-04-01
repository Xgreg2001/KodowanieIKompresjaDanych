import java.io.*;

public class Encoder {

    private static long totalCount;
    private static long l = 0;
    private static long u = 0xffffffffL;
    private static final long[] dict = new long[257];
    private static final long[] cumCount = new long[258];
    private static int scale3;
    private static int buffer = 0;
    private static int bitsInBuffer = 0;
    private static BufferedOutputStream outStream;
    private static long bytesRead = 0;


    public static void main(String[] args) {
        totalCount = Commons.initializeDictionaries(dict, cumCount);
        File initialFile = new File(args[0]);
        File destination = new File(args[1]);
        try {

            BufferedInputStream stream = new BufferedInputStream(new DataInputStream(new FileInputStream(initialFile)));
            outStream = new BufferedOutputStream(new PrintStream(destination));
            int b = stream.read();
            while (b != -1) {
                bytesRead++;
                encode(b);
                totalCount = Commons.updateDict(b, totalCount, dict, cumCount);
                b = stream.read();
            }
            encode(256);
            writeReminder();
            stream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProcessBuilder builder = new ProcessBuilder("/usr/local/julia-1.7.2/bin/julia", "lista1/entropia.jl", args[0]);
        builder.inheritIO();
        try {
            Process p = builder.start();
            p.waitFor();
            System.out.println((double) destination.length() / (double) bytesRead * 8.0);
            System.out.println((double) bytesRead / (double) destination.length());
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
        buffer = buffer << (8 - bitsInBuffer);
        outStream.write(buffer);
        outStream.flush();
    }

    private static void encode(int b) throws IOException {
        long prevL = l;
        l = l + ((u - l + 1) * cumCount[b] / totalCount);
        u = prevL + ((u - prevL + 1) * cumCount[b + 1] / totalCount) - 1;

        while ((l & 0x80000000L) == (u & 0x80000000L) || (l & 0x40000000L) == 0x40000000L && (u & 0x40000000L) == 0) {
            if ((l & 0x80000000L) == (u & 0x80000000L)) {
                long bit = (l & 0xffffffffL) >>> 31;
                send(bit);
                l = l << 1 & 0xffffffffL;
                u = u << 1 & 0xffffffffL | 0x1L;
                while (scale3 > 0) {
                    send(bit ^ 0x1L);
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

    private static void send(long bit) throws IOException {
        int b = (int) bit & 0x1;
        buffer = buffer << 1 | b;
        bitsInBuffer++;
        if (bitsInBuffer == 8) {
            outStream.write(buffer);
            buffer = 0;
            bitsInBuffer = 0;
        }
    }
}
