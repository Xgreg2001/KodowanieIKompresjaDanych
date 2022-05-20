import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    private static void saveTGAImage(String path, int[] pixels, int width, int height) throws IOException {
        byte[] buffer = TGAWriter.write(pixels, width, height, TGAReader.ARGB, TGAWriter.EncodeType.NONE);
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(buffer);
        fos.close();
    }

    public static void main(String[] args) {
        byte[] buffer;
        int[] pixels;

        try (FileInputStream fis = new FileInputStream(args[0])) {
            buffer = new byte[fis.available()];
            fis.read(buffer);
            pixels = TGAReader.read(buffer, TGAReader.ARGB);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int width = TGAReader.getWidth(buffer);
        int height = TGAReader.getHeight(buffer);

        Quantizer quantizer = new Quantizer(pixels);

        long startTime = System.currentTimeMillis();

        int[] output = quantizer.quantize(Integer.parseInt(args[2]), 0.001);

        long endTime = System.currentTimeMillis();

        long diff = endTime - startTime;

        System.out.println("for k = " + Integer.parseInt(args[2]) + " took: " + diff + "ms");

        System.out.println("mse: " + Metrics.mse(pixels, output));
        System.out.println("snr: " + Metrics.snr(pixels, output));
        System.out.println("snr in dB: " + Metrics.snrLogarithmic(pixels, output));

        try {
            saveTGAImage(args[1], output, width, height);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}