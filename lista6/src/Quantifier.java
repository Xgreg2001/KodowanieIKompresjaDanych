import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Quantifier {
    // map from all 8bit integer to a value that represent it
    HashMap<Integer, Integer> quantifiersMap;
    ArrayList<Integer> quantifiersIndexes = new ArrayList<>();

    int nextIndex = 0;

    private Random random = new Random();

    public Quantifier(int k) {
        quantifiersMap = constructQuantifiers(k);
    }

    private HashMap<Integer, Integer> constructQuantifiers(int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int jump = (int) Math.pow(2, 8 - k);
        for (int i = 0; i < 256; i++) {
            int nearest_j = 0;
            int lowest_dist = Integer.MAX_VALUE;
            for (int j = 0; j < 256; j += jump) {
                int dist = Math.abs(j - i);
                if (dist < lowest_dist) {
                    lowest_dist = dist;
                    nearest_j = j;
                }
            }
            map.put(i, nearest_j);
            if (!quantifiersIndexes.contains(nearest_j)) {
                quantifiersIndexes.add(nearest_j);
            }
        }
        System.out.println(quantifiersIndexes.size());
        return map;
    }

//    private HashMap<Integer, Integer> constructQuantifiers(int k) {
//        HashMap<Integer, Integer> map = new HashMap<>();
//        for (int i = 0; i < 256; i++) {
//            map.put(i, i);
//            quantifiersIndexes.add(i);
//        }
//
//        return map;
//    }

//    private HashMap<Integer, Integer> constructQuantifiers(int k) {
//        HashMap<Integer, Integer> map = new HashMap<>();
//        int jump = (int) Math.pow(2, 8 - k);
//        for (int i = 0; i < 256; i++) {
//            int y = magicFunction(i - 128);
//            int nearest_j = 0;
//            int lowest_dist = Integer.MAX_VALUE;
//            for (int j = -128; j < 128; j += jump) {
//                int dist = Math.abs(j - y);
//                if (dist < lowest_dist) {
//                    lowest_dist = dist;
//                    nearest_j = j;
//                }
//            }
//            int x = inverseMagicFunction(nearest_j) + 128;
//            map.put(i, x);
//            if (!quantifiersIndexes.contains(x)) {
//                quantifiersIndexes.add(x);
//            }
//        }
//        System.out.println(quantifiersIndexes.size());
//        return map;
//    }

    private int magicFunction(int x) {
        return (int) Math.round(128 * Math.tanh((double) x / 62));
    }

    private int inverseMagicFunction(int x) {
        return (int) Math.round(62 * (0.5 * Math.log((1 + (double) x / 128) / (1 - (double) x / 128))));
    }

    public int quantify(int value) {
        return quantifiersMap.get(value);
    }

    public int[] quantify(int[] values) {
        int[] result = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            int original_red = ((values[i] >> 16) & 0xff);
            int original_green = ((values[i] >> 8) & 0xff);
            int original_blue = ((values[i]) & 0xff);

            result[i] = 0xff << 24 |
                    (quantify(original_red)) << 16 |
                    (quantify(original_green)) << 8 |
                    (quantify(original_blue));
        }
        return result;
    }

    public int getIndexFromValue(int val) {
        return quantifiersIndexes.indexOf(val);
    }

    public int getValueFromIndex(int idx) {
        return quantifiersIndexes.get(idx);
    }


}
