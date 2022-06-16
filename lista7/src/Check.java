import java.io.DataInputStream;
import java.io.IOException;

public class Check {

    public int run(DataInputStream first, DataInputStream second){
        int b1;
        int b2;
        int mistakes = 0;

        try {
            b1 = first.read();
            b2 = second.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (b1 != -1 && b2 != -1){
            int lowB1 = b1 & 0xf;
            int highB1 = (b1 & 0xf0) >> 4;
            int lowB2 = b2 & 0xf;
            int highB2 = (b2 & 0xf0) >> 4;

            if(lowB1 != lowB2){
                mistakes++;
            }
            if(highB1 != highB2){
                mistakes++;
            }

            try {
                b1 = first.read();
                b2 = second.read();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        while (b1 != -1){
            mistakes += 2;
            try {
                b1 = first.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        while (b2 != -1){
            mistakes += 2;
            try {
                b2 = first.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return mistakes;
    }
}
