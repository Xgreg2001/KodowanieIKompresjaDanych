import java.io.*;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {

        //encoding
        File inFile = new File(args[0]);
        File outFile = new File("encoded.txt");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(Files.newInputStream(inFile.toPath()));
            out = new BufferedOutputStream(new FileOutputStream(outFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Encoder enc;
        if (args.length < 3){
            enc = new Encoder(in, out, new OmegaEncoding(in, out));
            System.out.println("using Omega");
        } else if (args[2].equals("gamma")){
            enc = new Encoder(in, out, new GammaEncoding(in, out));
            System.out.println("using Gamma");
        } else if (args[2].equals("delta")) {
            enc = new Encoder(in, out, new DeltaEncoding(in, out));
            System.out.println("using Delta");
        } else if (args[2].equals("fibonacci")) {
            enc = new Encoder(in, out, new FibonacciEncoding(in, out));
            System.out.println("using Fibonacci");
        } else {
            enc = new Encoder(in, out, new OmegaEncoding(in, out));
            System.out.println("using Omega");
        }

        try {
            enc.encode();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //decoding
        File decodedFile = new File(args[1]);

        BufferedInputStream encoded = null;
        BufferedOutputStream decoded = null;
        try {
            encoded = new BufferedInputStream(new FileInputStream(outFile));
            decoded = new BufferedOutputStream(new FileOutputStream(decodedFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Decoder dec;
        if (args.length < 3){
            dec = new Decoder(encoded, decoded, new OmegaEncoding(encoded, decoded));
        } else if (args[2].equals("gamma")){
            dec = new Decoder(encoded, decoded, new GammaEncoding(encoded, decoded));
        } else if (args[2].equals("delta")) {
            dec = new Decoder(encoded, decoded, new DeltaEncoding(encoded, decoded));
        } else if (args[2].equals("fibonacci")) {
            dec = new Decoder(encoded, decoded, new FibonacciEncoding(encoded, decoded));
        } else {
            dec = new Decoder(encoded, decoded, new OmegaEncoding(encoded, decoded));
        }

        try {
            dec.decode();
            encoded.close();
            decoded.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Dlugosc kodowanego pliku:" + inFile.length());
        System.out.println("Dlugosc zakodowanego pliku:" + outFile.length());
        System.out.println("Stopien kompresji: " + ((double) inFile.length() / (double) outFile.length()));
        System.out.println("Entropia kodowanego: ");

        ProcessBuilder builder = new ProcessBuilder("C:\\Users\\Xgreg\\AppData\\Local\\Programs\\Julia-1.7.2\\bin\\julia.exe", "lista1/entropia.jl", args[0]);
        builder.inheritIO();
        Process p = null;
        try {
            p = builder.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Entropia zakodowanego: ");
        builder = new ProcessBuilder("C:\\Users\\Xgreg\\AppData\\Local\\Programs\\Julia-1.7.2\\bin\\julia.exe", "lista1/entropia.jl", "encoded.txt");
        builder.inheritIO();
        p = null;
        try {
            p = builder.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
