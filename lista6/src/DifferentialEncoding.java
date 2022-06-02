public class DifferentialEncoding {
    public static int[] encode(int[] data, Quantifier q) {
        int[] result = new int[data.length];

        result[0] = data[0];

        int previous_decoded_red = q.quantify((data[0] >> 16) & 0xff);
        int previous_decoded_green = q.quantify((data[0] >> 8) & 0xff);
        int previous_decoded_blue = q.quantify((data[0]) & 0xff);

        for (int i = 1; i < data.length; i++) {
            int original_red = ((data[i] >> 16) & 0xff);
            int original_green = ((data[i] >> 8) & 0xff);
            int original_blue = ((data[i]) & 0xff);

            int difference_red = ((original_red - previous_decoded_red) & 0xff);
            int difference_green = ((original_green - previous_decoded_green) & 0xff);
            int difference_blue = ((original_blue - previous_decoded_blue) & 0xff);

            int quantified_difference_red = (q.quantify(difference_red));
            int quantified_difference_green = (q.quantify(difference_green) & 0xff);
            int quantified_difference_blue = (q.quantify(difference_blue) & 0xff);

            int decoded_red = (previous_decoded_red + quantified_difference_red);
            int decoded_green = (previous_decoded_green + quantified_difference_green);
            int decoded_blue = ((previous_decoded_blue + quantified_difference_blue));

            result[i] = 0xff << 24 |
                    (quantified_difference_red & 0xff) << 16 |
                    (quantified_difference_green & 0xff) << 8 |
                    (quantified_difference_blue & 0xff);

            previous_decoded_red = (decoded_red) & 0xff;
            previous_decoded_green = (decoded_green) & 0xff;
            previous_decoded_blue = (decoded_blue) & 0xff;
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
