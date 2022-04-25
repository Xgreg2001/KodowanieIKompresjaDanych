import java.nio.ByteBuffer;

public class Predictors {

    public static int jpegLS1(int n, int w, int nw) {
        return w;
    }

    public static int jpegLS2(int n, int w, int nw) {
        return n;
    }

    public static int jpegLS3(int n, int w, int nw) {
        return nw;
    }

    public static int jpegLS4(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();
        byte[] nwBytes = ByteBuffer.allocate(4).putInt(nw).array();

        byte r = (byte) (nBytes[1] + wBytes[1] - nwBytes[1]);
        byte g = (byte) (nBytes[2] + wBytes[2] - nwBytes[2]);
        byte b = (byte) (nBytes[3] + wBytes[3] - nwBytes[3]);


        return 0xff << 24 | r << 16 | g << 8 | b;
    }

    public static int jpegLS5(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();
        byte[] nwBytes = ByteBuffer.allocate(4).putInt(nw).array();

        byte r = (byte) (nBytes[1] + (wBytes[1] - nwBytes[1]) / 2);
        byte g = (byte) (nBytes[2] + (wBytes[2] - nwBytes[2]) / 2);
        byte b = (byte) (nBytes[3] + (wBytes[3] - nwBytes[3]) / 2);

        return 0xff << 24 | r << 16 | g << 8 | b;
    }

    public static int jpegLS6(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();
        byte[] nwBytes = ByteBuffer.allocate(4).putInt(nw).array();

        byte r = (byte) (wBytes[1] + (nBytes[1] - nwBytes[1]) / 2);
        byte g = (byte) (wBytes[2] + (nBytes[2] - nwBytes[2]) / 2);
        byte b = (byte) (wBytes[3] + (nBytes[3] - nwBytes[3]) / 2);

        return 0xff << 24 | r << 16 | g << 8 | b;
    }

    public static int jpegLS7(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();

        byte r = (byte) ((wBytes[1] + nBytes[1]) / 2);
        byte g = (byte) ((wBytes[2] + nBytes[2]) / 2);
        byte b = (byte) ((wBytes[3] + nBytes[3]) / 2);

        return 0xff << 24 | r << 16 | g << 8 | b;
    }

    public static int jpegLSNew(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();
        byte[] nwBytes = ByteBuffer.allocate(4).putInt(nw).array();

        byte r;
        byte g;
        byte b;

        if (nwBytes[1] >= Math.max(wBytes[1], nBytes[1])) {
            r = (byte) Math.max(wBytes[1], nBytes[1]);
        } else if (nwBytes[1] <= Math.min(wBytes[1], nBytes[1])) {
            r = (byte) Math.min(wBytes[1], nBytes[1]);
        } else {
            r = (byte) (nBytes[1] + wBytes[1] - nwBytes[1]);
        }

        if (nwBytes[2] >= Math.max(wBytes[2], nBytes[2])) {
            g = (byte) Math.max(wBytes[2], nBytes[2]);
        } else if (nwBytes[2] <= Math.min(wBytes[2], nBytes[2])) {
            g = (byte) Math.min(wBytes[2], nBytes[2]);
        } else {
            g = (byte) (nBytes[2] + wBytes[2] - nwBytes[2]);
        }

        if (nwBytes[3] >= Math.max(wBytes[3], nBytes[3])) {
            b = (byte) Math.max(wBytes[3], nBytes[3]);
        } else if (nwBytes[3] <= Math.min(wBytes[3], nBytes[3])) {
            b = (byte) Math.min(wBytes[3], nBytes[3]);
        } else {
            b = (byte) (nBytes[3] + wBytes[3] - nwBytes[3]);
        }
        
        return 0xff << 24 | r << 16 | g << 8 | b;
    }

}
