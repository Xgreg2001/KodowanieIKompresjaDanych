public class Metrics {

    public static double mse(int[] original, int[] modified) {

        int numberOfBytes = original.length * 3;
        byte[] originalBytes = new byte[numberOfBytes];
        byte[] modifiedBytes = new byte[numberOfBytes];
        TGAWriter.writeRaw(original, originalBytes, 0, 3, TGAReader.ARGB);
        TGAWriter.writeRaw(modified, modifiedBytes, 0, 3, TGAReader.ARGB);

        double mse = 0;

        for (int i = 0; i < numberOfBytes; i++) {
            mse += Math.pow(originalBytes[i] - modifiedBytes[i], 2) / (double) numberOfBytes;
        }

        return mse;
    }

    public static double snr(int[] original, int[] modified) {
        double mse = mse(original, modified);
        int numberOfBytes = original.length * 3;
        byte[] originalBytes = new byte[numberOfBytes];
        TGAWriter.writeRaw(original, originalBytes, 0, 3, TGAReader.ARGB);
        double snr = 0;

        for (int i = 0; i < numberOfBytes; i++) {
            snr += originalBytes[i] * originalBytes[i] / (double) numberOfBytes;
        }

        snr /= mse;

        return snr;
    }

    public static double snrLogarithmic(int[] original, int[] modified) {
        return 10 * Math.log10(snr(original, modified));
    }
}
