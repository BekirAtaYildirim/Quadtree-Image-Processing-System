public class QuadtreeNode {
    private int x, y;        
    private int size;        
    
    private Color meanColor;    
    private double meanError;   
    
    private QuadtreeNode ne, nw, se, sw;
    
    private PPMImage image;

    public QuadtreeNode(PPMImage image, int x, int y, int size, double threshold) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.size = size;
        
        calculateMeanColor();
        calculateMeanError();
        
        if (shouldSplit(threshold)) {
            split(threshold);
        }
    }

    private void calculateMeanColor() {
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        int count = 0;
        
        for (int dy = 0; dy < size; dy++) {
            for (int dx = 0; dx < size; dx++) {
                Color c = image.getPixel(x + dx, y + dy);
                sumRed += c.red;
                sumGreen += c.green;
                sumBlue += c.blue;
                count++;
            }
        }
        
        if (count > 0) {
            int avgR = (int) (sumRed / count);
            int avgG = (int) (sumGreen / count);
            int avgB = (int) (sumBlue / count);
            meanColor = new Color(avgR, avgG, avgB);
        } 
        else {
            meanColor = new Color(0, 0, 0);
        }
    }

    private void calculateMeanError() {
        double sumError = 0;
        int count = 0;
        
        for (int dy = 0; dy < size; dy++) {
            for (int dx = 0; dx < size; dx++) {

                Color c = image.getPixel(x + dx, y + dy);
                sumError += meanColor.squaredDistanceTo(c);
                count++;
            }
        }
        
        if (count > 0) {
            meanError = sumError / count;
        } 
        else {
            meanError = 0;
        }
    }

    private boolean shouldSplit(double threshold) {
        if (size <= 1) {
            return false;
        }
        
        return meanError > threshold;
    }

    private void split(double threshold) {
        int halfSize = size / 2;
        
        if (halfSize > 0) {
            nw = new QuadtreeNode(image, x, y, halfSize, threshold);
            ne = new QuadtreeNode(image, x + halfSize, y, halfSize, threshold);
            sw = new QuadtreeNode(image, x, y + halfSize, halfSize, threshold);
            se = new QuadtreeNode(image, x + halfSize, y + halfSize, halfSize, threshold);
        }
    }

    public boolean isLeaf() {
        return nw == null;
    }

    public Color getMeanColor() {
        return meanColor;
    }

    public int getX() { 
        return x; 
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public QuadtreeNode getNe() {
        return ne;
    }

    public void setNe(QuadtreeNode ne) {
        this.ne = ne;
    }

    public QuadtreeNode getNw() {
        return nw;
    }

    public void setNw(QuadtreeNode nw) {
        this.nw = nw;
    }

    public QuadtreeNode getSe() {
        return se;
    }

    public void setSe(QuadtreeNode se) {
        this.se = se;
    }

    public QuadtreeNode getSw() {
        return sw;
    }

    public void setSw(QuadtreeNode sw) {
        this.sw = sw;
    }

    public int countLeaves() {
        if (isLeaf()) {
            return 1;
        }
        
        int count = 0;
        if (nw != null) count += nw.countLeaves();
        if (ne != null) count += ne.countLeaves();
        if (sw != null) count += sw.countLeaves();
        if (se != null) count += se.countLeaves();
        
        return count;
    }

    public void fillImage(PPMImage output) {
        if (isLeaf()) {
            for (int dy = 0; dy < size; dy++) {
                for (int dx = 0; dx < size; dx++) {
                    output.setPixel(x + dx, y + dy, meanColor);
                }
            }
        } else {
            if (nw != null) nw.fillImage(output);
            if (ne != null) ne.fillImage(output);
            if (sw != null) sw.fillImage(output);
            if (se != null) se.fillImage(output);
        }
    }

    public void drawOutline(PPMImage output) {
        if (isLeaf()) {
            Color white = new Color(255, 255, 255);
            
            for (int dx = 0; dx < size; dx++) {
                output.setPixel(x + dx, y, white);
                if (y + size - 1 < output.getHeight()) {
                    output.setPixel(x + dx, y + size - 1, white);
                }
            }
            
            for (int dy = 0; dy < size; dy++) {
                output.setPixel(x, y + dy, white);
                if (x + size - 1 < output.getWidth()) {
                    output.setPixel(x + size - 1, y + dy, white);
                }
            }
        } else {
            if (nw != null) nw.drawOutline(output);
            if (ne != null) ne.drawOutline(output);
            if (sw != null) sw.drawOutline(output);
            if (se != null) se.drawOutline(output);
        }
    }

    public void applyEdgeDetection(PPMImage input, PPMImage output, int sizeThreshold) {
        if (isLeaf() && size <= sizeThreshold) {
            applyEdgeFilterToRegion(input, output);
        } 
        else if (isLeaf()) {
            Color black = new Color(0, 0, 0);
            for (int dy = 0; dy < size; dy++) {
                for (int dx = 0; dx < size; dx++) {
                    output.setPixel(x + dx, y + dy, black);
                }
            }
        } 
        else {
            if (nw != null) nw.applyEdgeDetection(input, output, sizeThreshold);
            if (ne != null) ne.applyEdgeDetection(input, output, sizeThreshold);
            if (sw != null) sw.applyEdgeDetection(input, output, sizeThreshold);
            if (se != null) se.applyEdgeDetection(input, output, sizeThreshold);
        }
    }

    private void applyEdgeFilterToRegion(PPMImage input, PPMImage output) {
        int[][] kernel = {
            {-1, -1, -1},
            {-1,  8, -1},
            {-1, -1, -1}
        };

        for (int dy = 0; dy < size; dy++) {
            for (int dx = 0; dx < size; dx++) {
                int px = x + dx;
                int py = y + dy;
                
                int sumRed = 0, sumGreen = 0, sumBlue = 0;
                
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        Color c = input.getPixel(px + kx, py + ky);
                        int weight = kernel[ky + 1][kx + 1];
                        sumRed += c.red * weight;
                        sumGreen += c.green * weight;
                        sumBlue += c.blue * weight;
                    }
                }
                
                output.setPixel(px, py, new Color(sumRed, sumGreen, sumBlue));
            }
        }
    }
}