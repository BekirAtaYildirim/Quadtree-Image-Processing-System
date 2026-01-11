public class Color {
    public int red, green, blue;

    public Color(int r, int g, int b) {
        this.red = control(r);
        this.green = control(g);
        this.blue = control(b);
    }

    public Color(Color other) {
        this.red = other.red;
        this.green = other.green;
        this.blue = other.blue;
    }

    private int control(int value) {
        if (value < 0) return 0;
        if (value > 255) return 255;
        return value;
    }

    public double squaredDistanceTo(Color other) {
        int dr = this.red - other.red;
        int dg = this.green - other.green;
        int db = this.blue - other.blue;
        return (dr * dr) + (dg * dg) + (db * db);
    }

    @Override
    public String toString() {
        return "(" + red + ", " + green + ", " + blue + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Color)) return false;
        Color other = (Color) obj;
        return this.red == other.red && this.green == other.green && this.blue == other.blue;
    }
}