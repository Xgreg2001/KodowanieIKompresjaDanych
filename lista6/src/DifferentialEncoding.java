public class DifferentialEncoding {
    public static int[] encode(int[] data) {
        int[] result = new int[data.length];

        result[0] = data[0];

        for (int i = 1; i < data.length; i++) {
            int original_red = ((data[i] >> 16) & 0xff);
            int original_green = ((data[i] >> 8) & 0xff);
            int original_blue = ((data[i]) & 0xff);

            int prev_red = ((data[i - 1] >> 16) & 0xff);
            int prev_green = ((data[i - 1] >> 8) & 0xff);
            int prev_blue = ((data[i - 1]) & 0xff);


            result[i] = 0xff << 24 |
                    ((original_red - prev_red) & 0xff) << 16 |
                    ((original_green - prev_green) & 0xff) << 8 |
                    ((original_blue - prev_blue) & 0xff);
        }

        return result;
    }

    public static int[] decode(int[] data) {
        int[] result = new int[data.length];

        result[0] = data[0];

        for (int i = 1; i < data.length; i++) {
            int encoded_red = ((data[i] >> 16) & 0xff);
            int encoded_green = ((data[i] >> 8) & 0xff);
            int encoded_blue = ((data[i]) & 0xff);

            int prev_red = ((result[i - 1] >> 16) & 0xff);
            int prev_green = ((result[i - 1] >> 8) & 0xff);
            int prev_blue = ((result[i - 1]) & 0xff);

            result[i] = 0xff << 24 |
                    ((encoded_red + prev_red) & 0xff) << 16 |
                    ((encoded_green + prev_green) & 0xff) << 8 |
                    ((encoded_blue + prev_blue) & 0xff);
        }
        return result;
    }
}
