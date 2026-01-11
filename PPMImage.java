import java.io.*;
import java.util.Scanner;

public class PPMImage {
    private int width;
    private int height;
    private Color[][] pixels;

    public PPMImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[height][width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = new Color(0, 0, 0);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return new Color(0, 0, 0); 
        }
        return pixels[y][x];
    }

    public void setPixel(int x, int y, Color color) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y][x] = new Color(color);
        }
    }

    public static PPMImage readPPM(String filename) throws IOException {
        Scanner scanner = new Scanner(new File(filename));

        String magic = scanner.next();
        if (!magic.equals("P3")) {
            scanner.close();
            throw new IOException("Only P3 PPM format is supported");
        }

        scanner.nextLine();
        String line = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                break;
            }
        }

        String[] dims = line.split("\\s+");
        int width = Integer.parseInt(dims[0]);
        int height = Integer.parseInt(dims[1]);

        int maxVal = scanner.nextInt();

        PPMImage image = new PPMImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = scanner.nextInt();
                int g = scanner.nextInt();
                int b = scanner.nextInt();
                image.setPixel(x, y, new Color(r, g, b));
            }
        }

        scanner.close();
        return image;
    }

    public void writePPM(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));

        writer.println("P3");
        writer.println(width + " " + height);
        writer.println("255");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = pixels[y][x];
                
                if (c == null) {
                    writer.print("0 0 0");
                } 
                else {
                    writer.print(c.red + " " + c.green + " " + c.blue);
                }
                
                if (x < width - 1) {
                    writer.print(" ");
                } 
                else {
                    writer.println();
                }
            }
        }

        writer.close();
    }

    public PPMImage copy() {
        PPMImage newImage = new PPMImage(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                
                newImage.setPixel(x, y, this.getPixel(x, y));
            }
        }
        return newImage;
    }
}