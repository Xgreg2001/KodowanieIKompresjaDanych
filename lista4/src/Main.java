import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

public class Main {
    private static JLabel createTGALabel(String path) {

        FileInputStream fis;
        byte[] buffer;
        int[] pixels;
        try {
            fis = new FileInputStream(path);

            buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            pixels = TGAReader.read(buffer, TGAReader.ARGB);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int width = TGAReader.getWidth(buffer);
        int height = TGAReader.getHeight(buffer);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);

        ImageIcon icon = new ImageIcon(image);
        return new JLabel(icon);
    }

    private static void showTGAImage(String title, String path) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(createTGALabel(path));
        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    private static void showTGAImage(String title, int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        ImageIcon icon = new ImageIcon(image);
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(icon));
        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    private static void saveTGAImage(String path, int[] pixels, int width, int height) throws IOException {
        byte[] buffer = TGAWriter.write(pixels, width, height, TGAReader.ARGB, TGAWriter.EncodeType.NONE);
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(buffer);
        fos.close();
    }

    public static void main(String[] args) {
        // 18 bytes header
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

        int[] jpegLS1Pixels = new int[width * height];
        int[] jpegLS2Pixels = new int[width * height];
        int[] jpegLS3Pixels = new int[width * height];
        int[] jpegLS4Pixels = new int[width * height];
        int[] jpegLS5Pixels = new int[width * height];
        int[] jpegLS6Pixels = new int[width * height];
        int[] jpegLS7Pixels = new int[width * height];
        int[] jpegLSNewPixels = new int[width * height];


        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (i == 0 && j == 0) {
                    jpegLS1Pixels[i + width * j] = Predictors.jpegLS1(0xff000000, 0xff000000, 0xff000000);
                    jpegLS2Pixels[i + width * j] = Predictors.jpegLS2(0xff000000, 0xff000000, 0xff000000);
                    jpegLS3Pixels[i + width * j] = Predictors.jpegLS3(0xff000000, 0xff000000, 0xff000000);
                    jpegLS4Pixels[i + width * j] = Predictors.jpegLS4(0xff000000, 0xff000000, 0xff000000);
                    jpegLS5Pixels[i + width * j] = Predictors.jpegLS5(0xff000000, 0xff000000, 0xff000000);
                    jpegLS6Pixels[i + width * j] = Predictors.jpegLS6(0xff000000, 0xff000000, 0xff000000);
                    jpegLS7Pixels[i + width * j] = Predictors.jpegLS7(0xff000000, 0xff000000, 0xff000000);
                    jpegLSNewPixels[i + width * j] = Predictors.jpegLSNew(0xff000000, 0xff000000, 0xff000000);
                } else if (i == 0) {
                    jpegLS1Pixels[i + width * j] = Predictors.jpegLS1(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                    jpegLS2Pixels[i + width * j] = Predictors.jpegLS2(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                    jpegLS3Pixels[i + width * j] = Predictors.jpegLS3(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                    jpegLS4Pixels[i + width * j] = Predictors.jpegLS4(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                    jpegLS5Pixels[i + width * j] = Predictors.jpegLS5(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                    jpegLS6Pixels[i + width * j] = Predictors.jpegLS6(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                    jpegLS7Pixels[i + width * j] = Predictors.jpegLS7(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                    jpegLSNewPixels[i + width * j] = Predictors.jpegLSNew(pixels[i + width * (j - 1)], 0xff000000, 0xff000000);
                } else if (j == 0) {
                    jpegLS1Pixels[i + width * j] = Predictors.jpegLS1(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                    jpegLS2Pixels[i + width * j] = Predictors.jpegLS2(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                    jpegLS3Pixels[i + width * j] = Predictors.jpegLS3(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                    jpegLS4Pixels[i + width * j] = Predictors.jpegLS4(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                    jpegLS5Pixels[i + width * j] = Predictors.jpegLS5(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                    jpegLS6Pixels[i + width * j] = Predictors.jpegLS6(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                    jpegLS7Pixels[i + width * j] = Predictors.jpegLS7(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                    jpegLSNewPixels[i + width * j] = Predictors.jpegLSNew(0xff000000, pixels[(i - 1) + width * j], 0xff000000);
                } else {
                    jpegLS1Pixels[i + width * j] = Predictors.jpegLS1(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                    jpegLS2Pixels[i + width * j] = Predictors.jpegLS2(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                    jpegLS3Pixels[i + width * j] = Predictors.jpegLS3(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                    jpegLS4Pixels[i + width * j] = Predictors.jpegLS4(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                    jpegLS5Pixels[i + width * j] = Predictors.jpegLS5(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                    jpegLS6Pixels[i + width * j] = Predictors.jpegLS6(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                    jpegLS7Pixels[i + width * j] = Predictors.jpegLS7(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                    jpegLSNewPixels[i + width * j] = Predictors.jpegLSNew(pixels[i + width * (j - 1)], pixels[(i - 1) + width * j], pixels[(i - 1) + width * (j - 1)]);
                }
            }
        }

        try {
            saveTGAImage("1.tga", jpegLS1Pixels, width, height);
            saveTGAImage("2.tga", jpegLS2Pixels, width, height);
            saveTGAImage("3.tga", jpegLS3Pixels, width, height);
            saveTGAImage("4.tga", jpegLS4Pixels, width, height);
            saveTGAImage("5.tga", jpegLS5Pixels, width, height);
            saveTGAImage("6.tga", jpegLS6Pixels, width, height);
            saveTGAImage("7.tga", jpegLS7Pixels, width, height);
            saveTGAImage("8.tga", jpegLSNewPixels, width, height);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int[] diffJpegLS1 = calculateDifferences(jpegLS1Pixels, pixels);
        int[] diffJpegLS2 = calculateDifferences(jpegLS2Pixels, pixels);
        int[] diffJpegLS3 = calculateDifferences(jpegLS3Pixels, pixels);
        int[] diffJpegLS4 = calculateDifferences(jpegLS4Pixels, pixels);
        int[] diffJpegLS5 = calculateDifferences(jpegLS5Pixels, pixels);
        int[] diffJpegLS6 = calculateDifferences(jpegLS6Pixels, pixels);
        int[] diffJpegLS7 = calculateDifferences(jpegLS7Pixels, pixels);
        int[] diffJpegLSNew = calculateDifferences(jpegLSNewPixels, pixels);

        double entropyOriginal = calculateEntropy(pixels, width, height);
        double entropyJpegLS1 = calculateEntropy(diffJpegLS1, width, height);
        double entropyJpegLS2 = calculateEntropy(diffJpegLS2, width, height);
        double entropyJpegLS3 = calculateEntropy(diffJpegLS3, width, height);
        double entropyJpegLS4 = calculateEntropy(diffJpegLS4, width, height);
        double entropyJpegLS5 = calculateEntropy(diffJpegLS5, width, height);
        double entropyJpegLS6 = calculateEntropy(diffJpegLS6, width, height);
        double entropyJpegLS7 = calculateEntropy(diffJpegLS7, width, height);
        double entropyJpegLSNew = calculateEntropy(diffJpegLSNew, width, height);

        double entropyRedOriginal = calculateEntropyRed(pixels, width, height);
        double entropyRedJpegLS1 = calculateEntropyRed(diffJpegLS1, width, height);
        double entropyRedJpegLS2 = calculateEntropyRed(diffJpegLS2, width, height);
        double entropyRedJpegLS3 = calculateEntropyRed(diffJpegLS3, width, height);
        double entropyRedJpegLS4 = calculateEntropyRed(diffJpegLS4, width, height);
        double entropyRedJpegLS5 = calculateEntropyRed(diffJpegLS5, width, height);
        double entropyRedJpegLS6 = calculateEntropyRed(diffJpegLS6, width, height);
        double entropyRedJpegLS7 = calculateEntropyRed(diffJpegLS7, width, height);
        double entropyRedJpegLSNew = calculateEntropyRed(diffJpegLSNew, width, height);

        double entropyGreenOriginal = calculateEntropyGreen(pixels, width, height);
        double entropyGreenJpegLS1 = calculateEntropyGreen(diffJpegLS1, width, height);
        double entropyGreenJpegLS2 = calculateEntropyGreen(diffJpegLS2, width, height);
        double entropyGreenJpegLS3 = calculateEntropyGreen(diffJpegLS3, width, height);
        double entropyGreenJpegLS4 = calculateEntropyGreen(diffJpegLS4, width, height);
        double entropyGreenJpegLS5 = calculateEntropyGreen(diffJpegLS5, width, height);
        double entropyGreenJpegLS6 = calculateEntropyGreen(diffJpegLS6, width, height);
        double entropyGreenJpegLS7 = calculateEntropyGreen(diffJpegLS7, width, height);
        double entropyGreenJpegLSNew = calculateEntropyGreen(diffJpegLSNew, width, height);

        double entropyBlueOriginal = calculateEntropyBlue(pixels, width, height);
        double entropyBlueJpegLS1 = calculateEntropyBlue(diffJpegLS1, width, height);
        double entropyBlueJpegLS2 = calculateEntropyBlue(diffJpegLS2, width, height);
        double entropyBlueJpegLS3 = calculateEntropyBlue(diffJpegLS3, width, height);
        double entropyBlueJpegLS4 = calculateEntropyBlue(diffJpegLS4, width, height);
        double entropyBlueJpegLS5 = calculateEntropyBlue(diffJpegLS5, width, height);
        double entropyBlueJpegLS6 = calculateEntropyBlue(diffJpegLS6, width, height);
        double entropyBlueJpegLS7 = calculateEntropyBlue(diffJpegLS7, width, height);
        double entropyBlueJpegLSNew = calculateEntropyBlue(diffJpegLSNew, width, height);

        System.out.println("Entropia orginalnego obrazu: " + entropyOriginal);
        System.out.println("Red: " + entropyRedOriginal);
        System.out.println("Green: " + entropyGreenOriginal);
        System.out.println("Blue: " + entropyBlueOriginal);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu 1: " + entropyJpegLS1);
        System.out.println("Red: " + entropyRedJpegLS1);
        System.out.println("Green: " + entropyGreenJpegLS1);
        System.out.println("Blue: " + entropyBlueJpegLS1);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu 2: " + entropyJpegLS2);
        System.out.println("Red: " + entropyRedJpegLS2);
        System.out.println("Green: " + entropyGreenJpegLS2);
        System.out.println("Blue: " + entropyBlueJpegLS2);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu 3: " + entropyJpegLS3);
        System.out.println("Red: " + entropyRedJpegLS3);
        System.out.println("Green: " + entropyGreenJpegLS3);
        System.out.println("Blue: " + entropyBlueJpegLS3);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu 4: " + entropyJpegLS4);
        System.out.println("Red: " + entropyRedJpegLS4);
        System.out.println("Green: " + entropyGreenJpegLS4);
        System.out.println("Blue: " + entropyBlueJpegLS4);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu 5: " + entropyJpegLS5);
        System.out.println("Red: " + entropyRedJpegLS5);
        System.out.println("Green: " + entropyGreenJpegLS5);
        System.out.println("Blue: " + entropyBlueJpegLS5);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu 6: " + entropyJpegLS6);
        System.out.println("Red: " + entropyRedJpegLS6);
        System.out.println("Green: " + entropyGreenJpegLS6);
        System.out.println("Blue: " + entropyBlueJpegLS6);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu 7: " + entropyJpegLS7);
        System.out.println("Red: " + entropyRedJpegLS7);
        System.out.println("Green: " + entropyGreenJpegLS7);
        System.out.println("Blue: " + entropyBlueJpegLS7);
        System.out.println("--------------------------------------------------");

        System.out.println("Entropia predykatu nowego standardu: " + entropyJpegLSNew);
        System.out.println("Red: " + entropyRedJpegLSNew);
        System.out.println("Green: " + entropyGreenJpegLSNew);
        System.out.println("Blue: " + entropyBlueJpegLSNew);
        System.out.println("--------------------------------------------------");

        double bestEntropy = Double.MAX_VALUE;
        double bestEntropyRed = Double.MAX_VALUE;
        double bestEntropyGreen = Double.MAX_VALUE;
        double bestEntropyBlue = Double.MAX_VALUE;
        String bestPredictorRed = "";
        String bestPredictorGreen = "";
        String bestPredictorBlue = "";
        String bestPredictor = "";

        if (entropyJpegLS1 < bestEntropy) {
            bestEntropy = entropyJpegLS1;
            bestPredictor = "predykat 1";
        }
        if (entropyJpegLS2 < bestEntropy) {
            bestEntropy = entropyJpegLS2;
            bestPredictor = "predykat 2";
        }
        if (entropyJpegLS3 < bestEntropy) {
            bestEntropy = entropyJpegLS3;
            bestPredictor = "predykat 3";
        }
        if (entropyJpegLS4 < bestEntropy) {
            bestEntropy = entropyJpegLS4;
            bestPredictor = "predykat 4";
        }
        if (entropyJpegLS5 < bestEntropy) {
            bestEntropy = entropyJpegLS5;
            bestPredictor = "predykat 5";
        }
        if (entropyJpegLS6 < bestEntropy) {
            bestEntropy = entropyJpegLS6;
            bestPredictor = "predykat 6";
        }
        if (entropyJpegLS7 < bestEntropy) {
            bestEntropy = entropyJpegLS7;
            bestPredictor = "predykat 7";
        }
        if (entropyJpegLSNew < bestEntropy) {
            bestEntropy = entropyJpegLSNew;
            bestPredictor = "predykat nowego standardu";
        }

        if (entropyRedJpegLS1 < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLS1;
            bestPredictorRed = "predykat 1";
        }
        if (entropyRedJpegLS2 < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLS2;
            bestPredictorRed = "predykat 2";
        }
        if (entropyRedJpegLS3 < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLS3;
            bestPredictorRed = "predykat 3";
        }
        if (entropyRedJpegLS4 < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLS4;
            bestPredictorRed = "predykat 4";
        }
        if (entropyRedJpegLS5 < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLS5;
            bestPredictorRed = "predykat 5";
        }
        if (entropyRedJpegLS6 < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLS6;
            bestPredictorRed = "predykat 6";
        }
        if (entropyRedJpegLS7 < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLS7;
            bestPredictorRed = "predykat 7";
        }
        if (entropyRedJpegLSNew < bestEntropyRed) {
            bestEntropyRed = entropyRedJpegLSNew;
            bestPredictorRed = "predykat nowego standardu";
        }

        if (entropyGreenJpegLS1 < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLS1;
            bestPredictorGreen = "predykat 1";
        }
        if (entropyGreenJpegLS2 < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLS2;
            bestPredictorGreen = "predykat 2";
        }
        if (entropyGreenJpegLS3 < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLS3;
            bestPredictorGreen = "predykat 3";
        }
        if (entropyGreenJpegLS4 < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLS4;
            bestPredictorGreen = "predykat 4";
        }
        if (entropyGreenJpegLS5 < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLS5;
            bestPredictorGreen = "predykat 5";
        }
        if (entropyGreenJpegLS6 < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLS6;
            bestPredictorGreen = "predykat 6";
        }
        if (entropyGreenJpegLS7 < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLS7;
            bestPredictorGreen = "predykat 7";
        }
        if (entropyGreenJpegLSNew < bestEntropyGreen) {
            bestEntropyGreen = entropyGreenJpegLSNew;
            bestPredictorGreen = "predykat nowego standardu";
        }

        if (entropyBlueJpegLS1 < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLS1;
            bestPredictorBlue = "predykat 1";
        }
        if (entropyBlueJpegLS2 < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLS2;
            bestPredictorBlue = "predykat 2";
        }
        if (entropyBlueJpegLS3 < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLS3;
            bestPredictorBlue = "predykat 3";
        }
        if (entropyBlueJpegLS4 < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLS4;
            bestPredictorBlue = "predykat 4";
        }
        if (entropyBlueJpegLS5 < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLS5;
            bestPredictorBlue = "predykat 5";
        }
        if (entropyBlueJpegLS6 < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLS6;
            bestPredictorBlue = "predykat 6";
        }
        if (entropyBlueJpegLS7 < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLS7;
            bestPredictorBlue = "predykat 7";
        }
        if (entropyBlueJpegLSNew < bestEntropyBlue) {
            bestEntropyBlue = entropyBlueJpegLSNew;
            bestPredictorBlue = "predykat nowego standardu";
        }

        System.out.println("Najlepszy predykat: " + bestPredictor + " : " + bestEntropy);
        System.out.println("Najlepszy predykat Red: " + bestPredictorRed + " : " + bestEntropyRed);
        System.out.println("Najlepszy predykat Green: " + bestPredictorGreen + " : " + bestEntropyGreen);
        System.out.println("Najlepszy predykat Blue: " + bestPredictorBlue + " : " + bestEntropyBlue);

    }

    private static double calculateEntropyRed(int[] pixels, int width, int height) {
        int[] numOccurrences = new int[256];
        int numberOfBytes = width * height * 3;
        byte[] bytes = new byte[numberOfBytes];
        TGAWriter.writeRaw(pixels, bytes, 0, 3, TGAReader.ARGB);

        for (int i = 2; i < numberOfBytes; i += 3) {
            numOccurrences[bytes[i] & 0xff]++;
        }

        numberOfBytes /= 3;

        double entropy = 0;
        for (int i = 0; i < 256; i++) {
            if (numOccurrences[i] != 0) {
                double propabilty = ((double) numOccurrences[i]) / (double) numberOfBytes;
                entropy -= propabilty * Math.log(propabilty) / Math.log(2);
            }
        }
        return entropy;
    }

    private static double calculateEntropyGreen(int[] pixels, int width, int height) {
        int[] numOccurrences = new int[256];
        int numberOfBytes = width * height * 3;
        byte[] bytes = new byte[numberOfBytes];
        TGAWriter.writeRaw(pixels, bytes, 0, 3, TGAReader.ARGB);

        for (int i = 1; i < numberOfBytes; i += 3) {
            numOccurrences[bytes[i] & 0xff]++;
        }

        numberOfBytes /= 3;

        double entropy = 0;
        for (int i = 1; i < 256; i++) {
            if (numOccurrences[i] != 0) {
                double propabilty = ((double) numOccurrences[i]) / (double) numberOfBytes;
                entropy -= propabilty * Math.log(propabilty) / Math.log(2);
            }
        }
        return entropy;
    }

    private static double calculateEntropyBlue(int[] pixels, int width, int height) {
        int[] numOccurrences = new int[256];
        int numberOfBytes = width * height * 3;
        byte[] bytes = new byte[numberOfBytes];
        TGAWriter.writeRaw(pixels, bytes, 0, 3, TGAReader.ARGB);

        for (int i = 0; i < numberOfBytes; i += 3) {
            numOccurrences[bytes[i] & 0xff]++;
        }

        numberOfBytes /= 3;

        double entropy = 0;
        for (int i = 0; i < 256; i++) {
            if (numOccurrences[i] != 0) {
                double propabilty = ((double) numOccurrences[i]) / (double) numberOfBytes;
                entropy -= propabilty * Math.log(propabilty) / Math.log(2);
            }
        }
        return entropy;
    }

    private static double calculateEntropy(int[] differences, int width, int height) {
        int[] numOccurrences = new int[256];
        int numberOfBytes = width * height * 3;
        byte[] bytes = new byte[numberOfBytes];
        TGAWriter.writeRaw(differences, bytes, 0, 3, TGAReader.ARGB);

        for (int i = 0; i < numberOfBytes; i++) {
            numOccurrences[bytes[i] & 0xff]++;
        }

        double entropy = 0;
        for (int i = 0; i < 256; i++) {
            if (numOccurrences[i] != 0) {
                double propabilty = ((double) numOccurrences[i]) / (double) numberOfBytes;
                entropy -= propabilty * Math.log(propabilty) / Math.log(2);
            }
        }
        return entropy;
    }

    private static int[] calculateDifferences(int[] predictedPixels, int[] pixels) {
        int[] diffs = new int[pixels.length];

        for (int i = 0; i < pixels.length; i++) {
            byte[] original = ByteBuffer.allocate(4).putInt(pixels[i]).array();
            byte[] predicted = ByteBuffer.allocate(4).putInt(predictedPixels[i]).array();

            int r = (((int) original[1] & 0xff) - ((int) predicted[1] & 0xff));
            int g = (((int) original[2] & 0xff) - ((int) predicted[2] & 0xff));
            int b = (((int) original[3] & 0xff) - ((int) predicted[3] & 0xff));

            diffs[i] = 0xff << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
        }

        return diffs;
    }


}