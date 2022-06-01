public class LowPassFilter {
    public static int[] applyFilter(int[] pixels) {
        int[] result = new int[pixels.length];

        result[0] = pixels[0];

        for (int i = 1; i < pixels.length; i++) {
            int original_red = ((pixels[i] >> 16) & 0xff);
            int original_green = ((pixels[i] >> 8) & 0xff);
            int original_blue = ((pixels[i]) & 0xff);

            int prev_red = ((pixels[i - 1] >> 16) & 0xff);
            int prev_green = ((pixels[i - 1] >> 8) & 0xff);
            int prev_blue = ((pixels[i - 1]) & 0xff);

            result[i] = 0xff << 24 |
                    (((original_red + prev_red) / 2) & 0xff) << 16 |
                    (((original_green + prev_green) / 2) & 0xff) << 8 |
                    (((original_blue + prev_blue) / 2) & 0xff);
        }

        return result;
    }
}
