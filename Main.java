public class Main {
    public static void main(String[] args) {
        String inputFile = null;
        String outputFile = null;
        boolean compress = false;
        boolean edgeDetect = false;
        boolean showTree = false;

        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-i":
                    if (i + 1 < args.length) {
                        inputFile = args[++i];
                    }
                    break;
                case "-o":
                    if (i + 1 < args.length) {
                        outputFile = args[++i];
                    }
                    break;
                case "-c":
                    compress = true;
                    break;
                case "-e":
                    edgeDetect = true;
                    break;
                case "-t":
                    showTree = true;
                    break;
            }
        }

        if (inputFile == null) {
            System.err.println("Input file not specified");
            return;
        }

        if (outputFile == null) {
            System.err.println("Output file not specified");
            return;
        }

        try {
            PPMImage image = PPMImage.readPPM(inputFile);

            if (image.getWidth() != image.getHeight()) {
                System.err.println("Image must be square");
                return;
            }

            if (compress) {
                performCompression(image, outputFile, showTree);
            } else if (edgeDetect) {
                performEdgeDetection(image, outputFile, showTree);
            } else {
                System.err.println("Specify either compression or edge detection");
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void performCompression(PPMImage image, String outputBase, boolean showTree) {
        double[] targetLevels = {0.002, 0.004, 0.01, 0.033, 0.077, 0.2, 0.5, 0.65};
        int pixelCount = image.getWidth() * image.getHeight();

        System.out.println("Original image size: " + image.getWidth() + "x" + image.getHeight());
        System.out.println("Total pixels: " + pixelCount);
        System.out.println();

        for (int i = 0; i < targetLevels.length; i++) {

            double targetLevel = targetLevels[i];
            int targetLeaves = (int) (pixelCount * targetLevel);

            double threshold = findThresholdForCompression(image, targetLeaves, pixelCount);

            if (threshold < 0) {
                System.out.println("Compression level " + targetLevel + " not good for this image.");
                continue;
            }

            Quadtree tree = new Quadtree(image, threshold);
            int leaves = tree.countLeaves();
            double actualLevel = (double) leaves / pixelCount;

            PPMImage compressed = tree.generateImage();

            if (showTree) {
                tree.drawTreeOutline(compressed);
            }

            String outputFile = outputBase + "-" + (i + 1) + ".ppm";
            try {
                compressed.writePPM(outputFile);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
            }

            System.out.println("    Image " + (i + 1) + ":");
            System.out.println("    Output file: " + outputFile);
            System.out.println("    Quadtree leaves: " + leaves);
            System.out.println("    Pixel count: " + pixelCount);
            System.out.println("    Compression level: " + String.format("%.6f", actualLevel));
            System.out.println("    Threshold used: " + String.format("%.1f", threshold));
            System.out.println();
        }
    }

    private static double findThresholdForCompression(PPMImage image, int targetLeaves, int pixelCount) {
        double minThreshold = 0.0;
        double maxThreshold = 200000.0;
        double bestThreshold = -1;
        int bestDiff = Integer.MAX_VALUE;

        for (int iter = 0; iter < 30; iter++) {
            double midThreshold = (minThreshold + maxThreshold) / 2.0;
            Quadtree tree = new Quadtree(image, midThreshold);
            int leaves = tree.countLeaves();

            int diff = Math.abs(leaves - targetLeaves);

            if (diff < bestDiff) {
                bestDiff = diff;
                bestThreshold = midThreshold;
            }

            if (leaves > targetLeaves) {
                minThreshold = midThreshold;
            } 
            else if (leaves < targetLeaves) {
                maxThreshold = midThreshold;
            } 
            else {
                return midThreshold;
            }

            if (diff < targetLeaves * 0.1) {
                break;
            }
        }

        return bestThreshold;
    }

    private static void performEdgeDetection(PPMImage image, String outputFile, boolean showTree) {
        double threshold = 5000.0;
        Quadtree tree = new Quadtree(image, threshold);

        PPMImage result = tree.applyEdgeDetection(image);

        if (showTree) {
            tree.drawTreeOutline(result);
        }

        if (!outputFile.endsWith(".ppm")) {
            outputFile = outputFile + ".ppm";
        }

        try {
            result.writePPM(outputFile);
            System.out.println("Edge detection complete.");
            System.out.println("Output file: " + outputFile);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}