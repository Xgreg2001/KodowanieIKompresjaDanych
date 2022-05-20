import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Quantizer {

    private final int[] pixels;
    private ArrayList<Integer> quantifiers = new ArrayList<>(); // aktualne wartości kwantyzatorów
    private HashMap<Integer, Integer> quantizerMaping = new HashMap<>(); // mapa z pixela na jego kwantyzację

    private HashMap<Integer, FastInteger> regionCount = new HashMap<>();

    private double epsilon;

    private final Random random = new Random();

    Quantizer(int[] pixels) {
        this.pixels = pixels;
    }

    public int[] quantize(int numberOfColors, double epsilon) {
        if (numberOfColors < 0 || numberOfColors >= 24 || Math.pow(2, numberOfColors) >= pixels.length) {
            return pixels;
        }

        this.epsilon = epsilon;
        int iteration = 0;

        initializeQuantifiers();

        while (iteration < numberOfColors) {
            perturbQuantifiers();
            LBG();
            iteration++;
        }

        int[] output = new int[pixels.length];

        for (int i = 0; i < pixels.length; i++) {
            output[i] = quantizerMaping.get(pixels[i]);
        }

//        System.out.println(quantifiers.size());
//        System.out.println(regionCount.size());

        return output;
    }

    private void LBG() {
        double distortion = 0;
        double previousDistortion = 0;
        boolean skipCalculatingNewQuantifiers = true;

        do {
            // poza 1 iteracją robimy uśrednianie kwantyzatorów
            if (!skipCalculatingNewQuantifiers) {
                findNewQuantifiers();
            }

            // wyrzucamy stare regiony kwantyzacji
            quantizerMaping = new HashMap<>();
            regionCount = new HashMap<>();

            // znajdujemy regiony kwantyzacji
            for (int pixel : pixels) {
                if (!quantizerMaping.containsKey(pixel)) {
                    // jeśli go jeszcze nie znamy znajdujemy najbliższy wektor
                    int distanceSoFar = Integer.MAX_VALUE;
                    int bestQuantifier = 0;
                    for (int quantifier : quantifiers) {
                        int distance = getDistance(pixel, quantifier);
                        if (distance < distanceSoFar) {
                            bestQuantifier = quantifier;
                            distanceSoFar = distance;
                        }
                    }

                    // dodajemy do mapy przejscie z wektora na jego kwantyzator
                    quantizerMaping.put(pixel, bestQuantifier);

                    // updatujemy ilośc wykorzystań danego kwantyzatora
                    FastInteger j = regionCount.get(bestQuantifier);
                    if (j == null) {
                        j = new FastInteger();
                        regionCount.put(bestQuantifier, j);
                    }
                    j.value++;
                } else {
                    // jeśli znamy już najlepszy kwantyzator to jedynie zwiększamy jego liczbę wykorzystań
                    Integer bestQuantifier = quantizerMaping.get(pixel);
                    FastInteger j = regionCount.get(bestQuantifier);
                    j.value++;
                }
            }

            //remove empty regions
            ArrayList<Integer> emptyRegions = new ArrayList<>();

            // znajdujemy puste regiony oraz ten najbardziej popularny
            for (Integer quantifier : quantifiers) {
                FastInteger count = regionCount.get(quantifier);
                if (count == null) {
                    emptyRegions.add(quantifier);
                }
            }


            // usuwamy puste regiony
            if (emptyRegions.size() != 0) {
                for (Integer quantifier : emptyRegions) {
                    Integer randomRegion = (int) regionCount.keySet().toArray()[random.nextInt(regionCount.size())];
                    for (int pixel : pixels) {
                        if (quantizerMaping.get(pixel).equals(randomRegion)) {
                            quantifiers.remove(quantifier);
                            quantifiers.add(pixel);
                            quantizerMaping.replace(pixel, pixel);
                            break;
                        }
                    }
                }
                skipCalculatingNewQuantifiers = true;
                continue;
                // szukamy jeszcze raz regionów kwantyzacji
            }


            previousDistortion = distortion;
            distortion = calculateDistortion();
            skipCalculatingNewQuantifiers = false;

        } while ((distortion - previousDistortion) / distortion > epsilon | skipCalculatingNewQuantifiers);

    }

    private void findNewQuantifiers() {
        HashMap<Integer, FastDouble> redAverageMap = new HashMap<>();
        HashMap<Integer, FastDouble> greenAverageMap = new HashMap<>();
        HashMap<Integer, FastDouble> blueAverageMap = new HashMap<>();

        for (int pixel : pixels) {
            Integer quantifier = quantizerMaping.get(pixel);

            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = pixel & 0xff;

            FastDouble redAverage = redAverageMap.get(quantifier);
            FastDouble greenAverage = greenAverageMap.get(quantifier);
            FastDouble blueAverage = blueAverageMap.get(quantifier);

            if (redAverage == null) {
                redAverage = new FastDouble();
                redAverageMap.put(quantifier, redAverage);
            }

            if (greenAverage == null) {
                greenAverage = new FastDouble();
                greenAverageMap.put(quantifier, greenAverage);
            }

            if (blueAverage == null) {
                blueAverage = new FastDouble();
                blueAverageMap.put(quantifier, blueAverage);
            }

            double pixelCount = regionCount.get(quantifier).value;

            redAverage.value += (double) red / pixelCount;
            greenAverage.value += (double) green / pixelCount;
            blueAverage.value += (double) blue / pixelCount;
        }

        ArrayList<Integer> newQuantifiers = new ArrayList<>();

        for (Integer quantifier : quantifiers) {
            double redAverage = redAverageMap.get(quantifier).value;
            double greenAverage = greenAverageMap.get(quantifier).value;
            double blueAverage = blueAverageMap.get(quantifier).value;

            int averagePixel = 0xff << 24 | (int) Math.floor(redAverage) << 16 | (int) Math.floor(greenAverage) << 8 | (int) Math.floor(blueAverage);

            newQuantifiers.add(averagePixel);
        }

        quantifiers = newQuantifiers;
    }

    // oblicz zniekształcenie
    private double calculateDistortion() {
        double distortion = 0;
        double numPixels = pixels.length;
        for (int pixel : pixels) {
            int quantifier = quantizerMaping.get(pixel);
            distortion += Math.pow(getDistance(pixel, quantifier), 2) / numPixels;
        }
        return distortion;
    }

    // oblicz metrykę taksówkową pomiedzy dwoma pixelami
    private int getDistance(int pixel, int quantifier) {
        int pixelRed = (pixel >> 16) & 0xff;
        int pixelGreen = (pixel >> 8) & 0xff;
        int pixelBlue = pixel & 0xff;

        int quantifierRed = (quantifier >> 16) & 0xff;
        int quantifierGreen = (quantifier >> 8) & 0xff;
        int quantifierBlue = quantifier & 0xff;

        return Math.abs(pixelRed - quantifierRed) + Math.abs(pixelGreen - quantifierGreen) + Math.abs(pixelBlue - quantifierBlue);
    }

    private void perturbQuantifiers() {
        ArrayList<Integer> newQuantifiers = new ArrayList<>();
        for (int quantifier : quantifiers) {
            newQuantifiers.add(perturbQuantifier(quantifier));
        }
        quantifiers.addAll(newQuantifiers);
    }

    private Integer perturbQuantifier(int quantifier) {
        int redPerturbation = random.nextInt(255);
        int greenPerturbation = random.nextInt(255);
        int bluePerturbation = random.nextInt(255);

        int red = (quantifier >> 16) & 0xff;
        int green = (quantifier >> 8) & 0xff;
        int blue = quantifier & 0xff;

        red = (red + redPerturbation) % 256;
        green = (green + greenPerturbation) % 256;
        blue = (blue + bluePerturbation) % 256;

        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    //Dodaje średni pixel na listę kwantyzatorów
    private void initializeQuantifiers() {
        quantifiers = new ArrayList<>();
        quantizerMaping = new HashMap<>();
        double redAverage = 0;
        double greenAverage = 0;
        double blueAverage = 0;

        double numPixels = pixels.length;

        for (int pixel : pixels) {
            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = pixel & 0xff;

            redAverage += (double) red / numPixels;
            greenAverage += (double) green / numPixels;
            blueAverage += (double) blue / numPixels;
        }

        int averagePixel = 0xff << 24 | (int) Math.floor(redAverage) << 16 | (int) Math.floor(greenAverage) << 8 | (int) Math.floor(blueAverage);

        quantifiers.add(averagePixel);

        for (int pixel : pixels) {
            quantizerMaping.put(pixel, averagePixel);
        }
    }
}
