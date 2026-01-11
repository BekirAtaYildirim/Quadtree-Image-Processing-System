public class Quadtree {
    private QuadtreeNode root;
    private PPMImage originalImage;

    public Quadtree(PPMImage image, double threshold) {
        this.originalImage = image;
        int size = image.getWidth(); 
        this.root = new QuadtreeNode(image, 0, 0, size, threshold);
    }

    public int countLeaves() {
        if (root == null) {
            return 0;
        }
        return root.countLeaves();
    }

    public PPMImage generateImage() {
        int size = originalImage.getWidth();
        PPMImage output = new PPMImage(size, size);
        
        if (root != null) {
            root.fillImage(output);
        }
        
        return output;
    }

    public void drawTreeOutline(PPMImage image) {
        if (root != null) {
            root.drawOutline(image);
        }
    }

    public PPMImage applyEdgeDetection(PPMImage input) {
        int size = input.getWidth();
        PPMImage output = new PPMImage(size, size);
        
        int sizeThreshold = 8;
        
        if (root != null) {
            root.applyEdgeDetection(input, output, sizeThreshold);
        }
        
        return output;
    }
}