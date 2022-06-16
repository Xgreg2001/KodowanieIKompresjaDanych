import java.io.*;

public class Main {

    private static final double p = 0.0001;

    public static void main(String[] args) {
        HammingCode h = new HammingCode();
        Noise n = new Noise();
        Check c = new Check();

        DataInputStream inputFile;
        DataOutputStream encodedOut;
        DataInputStream encodedIn;
        DataOutputStream noiseOut;
        DataInputStream noiseIn;
        DataOutputStream decodedOut;
        DataInputStream decodedIn;

        try {
            inputFile = new DataInputStream( new BufferedInputStream( new FileInputStream(args[0])));
            encodedOut = new DataOutputStream( new BufferedOutputStream( new FileOutputStream("encoded.txt")));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        h.encode(inputFile, encodedOut);

        try {
            inputFile.close();
            encodedOut.close();
            encodedIn = new DataInputStream( new BufferedInputStream( new FileInputStream("encoded.txt")));
            noiseOut = new DataOutputStream(new BufferedOutputStream( new FileOutputStream("noised.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        n.run(encodedIn, noiseOut, p);

        try {
            encodedIn.close();
            noiseOut.close();
            noiseIn = new DataInputStream(new BufferedInputStream( new FileInputStream("noised.txt")));
            decodedOut = new DataOutputStream(new BufferedOutputStream( new FileOutputStream("decoded.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int errors = h.decode(noiseIn, decodedOut);

        try {
            noiseIn.close();
            decodedOut.close();
            decodedIn = new DataInputStream(new BufferedInputStream( new FileInputStream("decoded.txt")));
            inputFile = new DataInputStream( new BufferedInputStream( new FileInputStream(args[0])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int mistakes = c.run(decodedIn, inputFile);

        try {
            decodedIn.close();
            inputFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("decoding algorithm found " + errors + " errors");
        System.out.println("decoding algorithm couldn't fix " + mistakes + " errors");



    }
}