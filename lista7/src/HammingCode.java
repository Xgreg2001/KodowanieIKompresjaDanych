import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class HammingCode {

    HashMap<Integer, Integer> generatingMatrixMapping = new HashMap<>();
    HashMap<Integer, Integer> decoderMapping = new HashMap<>();
    int[][] generatingMatrix = {
            new int[]{1, 1, 0, 1, 0, 0, 0, 1},
            new int[]{0, 1, 1, 0, 1, 0, 0, 1},
            new int[]{0, 0, 1, 1, 0, 1, 0, 1},
            new int[]{0, 0, 0, 1, 1, 0, 1, 1}
    };

    public HammingCode() {
        initializeDecoderMapping();
    }

    private void initializeDecoderMapping() {
        for (int i = 0; i < 16; i ++){
            int hammingCode = getHammingCode(i);

            //poprawny kod
            decoderMapping.put(hammingCode, i);

            //wszystkie kody gdzie pojedynczy bit jest błędny
            for (int mask = 1; mask < 0x100; mask <<= 1){
                int error = hammingCode ^ mask;
                decoderMapping.put(error, i);
            }
        }
    }

    public int getHammingCode(int bits){
        if (bits != (bits & 0xf)){
            throw new IllegalArgumentException("More then 4 bit long input");
        }

        if (generatingMatrixMapping.containsKey(bits)){
            return generatingMatrixMapping.get(bits);
        }

        int[][] inputVector = {
                new int[]{(bits >> 3) & 1, (bits >> 2) & 1, (bits >> 1) & 1, bits & 1}
        };

        int[][] resultVector = multiplyMatrices(inputVector, generatingMatrix);

        assert(resultVector.length == 1 && resultVector[0].length == 8);

        int result = 0;
        for (int i : resultVector[0]){
            result = (result << 1) | i;
        }

        // sprawdzamy czy mamy max 8 bitów
        assert (result == (result & 0xff));

        generatingMatrixMapping.put(bits, result);

        return result;
    }


    int[][] multiplyMatrices(int[][] firstMatrix, int[][] secondMatrix) {
        int[][] result = new int[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col) % 2;
            }
        }

        return result;
    }

    int multiplyMatricesCell(int[][] firstMatrix, int[][] secondMatrix, int row, int col) {
        int cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    public void encode(DataInputStream in, DataOutputStream out){
        int b;
        try {
            b = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (b != -1){
            int lowB = b & 0xf;
            int highB = (b >> 4) & 0xf;

            //write result to output and read next byte
            try {
                out.write(getHammingCode(highB));
                out.write(getHammingCode(lowB));
                b = in.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int decode(DataInputStream in, DataOutputStream out){
        int b;
        try {
            b = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int buffer = 0;
        boolean bufferLoaded = false;

        int errors = 0;

        while (b != -1){

            if(decoderMapping.containsKey(b)){
                if(bufferLoaded){
                    buffer = buffer << 4 | decoderMapping.get(b);
                    try {
                        out.write(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    bufferLoaded = false;
                } else {
                    buffer = decoderMapping.get(b);
                    bufferLoaded = true;
                }
            } else {
                errors++;
                if(bufferLoaded){
                    buffer = buffer << 4 ;
                    try {
                        out.write(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    bufferLoaded = false;
                } else {
                    buffer = 0;
                    bufferLoaded = true;
                }
            }

            try {
                b = in.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return errors;
    }
}
