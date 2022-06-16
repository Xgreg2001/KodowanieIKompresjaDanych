import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

public class Noise {
    Random random = new Random();

    public void run(DataInputStream in, DataOutputStream out, double p){
        int b;
        try {
            b = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (b != -1){

            //flip random bits
            for (int i = 0; i < 8; i++){
                if (random.nextDouble() <= p){
                    b = b ^ (1 << i);
                }
            }

            //write result to output and read next byte
            try {
                out.write(b);
                b = in.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
