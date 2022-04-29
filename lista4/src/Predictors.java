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

        int r = ((int) nBytes[1] & 0xff) + ((int) wBytes[1] & 0xff) - ((int) nwBytes[1] & 0xff);
        int g = ((int) nBytes[2] & 0xff) + ((int) wBytes[2] & 0xff) - ((int) nwBytes[2] & 0xff);
        int b = ((int) nBytes[3] & 0xff) + ((int) wBytes[3] & 0xff) - ((int) nwBytes[3] & 0xff);

        return 0xff << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    public static int jpegLS5(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();
        byte[] nwBytes = ByteBuffer.allocate(4).putInt(nw).array();

        int r = ((int) nBytes[1] & 0xff) + (((int) wBytes[1] & 0xff) - ((int) nwBytes[1] & 0xff)) / 2;
        int g = ((int) nBytes[2] & 0xff) + (((int) wBytes[2] & 0xff) - ((int) nwBytes[2] & 0xff)) / 2;
        int b = ((int) nBytes[3] & 0xff) + (((int) wBytes[3] & 0xff) - ((int) nwBytes[3] & 0xff)) / 2;

        return 0xff << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    public static int jpegLS6(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();
        byte[] nwBytes = ByteBuffer.allocate(4).putInt(nw).array();

        int r = ((int) wBytes[1] & 0xff) + (((int) nBytes[1] & 0xff) - ((int) nwBytes[1] & 0xff)) / 2;
        int g = ((int) wBytes[2] & 0xff) + (((int) nBytes[2] & 0xff) - ((int) nwBytes[2] & 0xff)) / 2;
        int b = ((int) wBytes[3] & 0xff) + (((int) nBytes[3] & 0xff) - ((int) nwBytes[3] & 0xff)) / 2;

        return 0xff << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    public static int jpegLS7(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();

        int r = (((int) wBytes[1] & 0xff) + ((int) nBytes[1] & 0xff)) / 2;
        int g = (((int) wBytes[2] & 0xff) + ((int) nBytes[2] & 0xff)) / 2;
        int b = (((int) wBytes[3] & 0xff) + ((int) nBytes[3] & 0xff)) / 2;

        return 0xff << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    public static int jpegLSNew(int n, int w, int nw) {
        // ARGB format
        byte[] nBytes = ByteBuffer.allocate(4).putInt(n).array();
        byte[] wBytes = ByteBuffer.allocate(4).putInt(w).array();
        byte[] nwBytes = ByteBuffer.allocate(4).putInt(nw).array();

        int r;
        int g;
        int b;

        if (((int) nwBytes[1] & 0xff) >= Math.max(((int) wBytes[1] & 0xff), ((int) nBytes[1] & 0xff))) {
            r = Math.max(((int) wBytes[1] & 0xff), ((int) nBytes[1] & 0xff));
        } else if (((int) nwBytes[1] & 0xff) <= Math.min(((int) wBytes[1] & 0xff), ((int) nBytes[1] & 0xff))) {
            r = Math.min(((int) wBytes[1] & 0xff), ((int) nBytes[1] & 0xff));
        } else {
            r = ((int) nBytes[1] & 0xff) + ((int) wBytes[1] & 0xff) - ((int) nwBytes[1] & 0xff);
        }

        if (((int) nwBytes[2] & 0xff) >= Math.max(((int) wBytes[2] & 0xff), ((int) nBytes[2] & 0xff))) {
            g = Math.max(((int) wBytes[2] & 0xff), ((int) nBytes[2] & 0xff));
        } else if (((int) nwBytes[2] & 0xff) <= Math.min(((int) wBytes[2] & 0xff), ((int) nBytes[2] & 0xff))) {
            g = Math.min(((int) wBytes[2] & 0xff), ((int) nBytes[2] & 0xff));
        } else {
            g = ((int) nBytes[2] & 0xff) + ((int) wBytes[2] & 0xff) - ((int) nwBytes[2] & 0xff);
        }

        if (((int) nwBytes[3] & 0xff) >= Math.max(((int) wBytes[3] & 0xff), ((int) nBytes[3] & 0xff))) {
            b = Math.max(((int) wBytes[3] & 0xff), ((int) nBytes[3] & 0xff));
        } else if (((int) nwBytes[3] & 0xff) <= Math.min(((int) wBytes[3] & 0xff), ((int) nBytes[3] & 0xff))) {
            b = Math.min(((int) wBytes[3] & 0xff), ((int) nBytes[3] & 0xff));
        } else {
            b = ((int) nBytes[3] & 0xff) + ((int) wBytes[3] & 0xff) - ((int) nwBytes[3] & 0xff);
        }

        return 0xff << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

}
