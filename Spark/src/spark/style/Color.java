package spark.style;

/**
 * A Flow color wrapper
 */
public class Color {
    public final java.awt.Color value;

    public Color(java.awt.Color value) {
        this.value = value;
    }

    /**
     * Creates a Flow Color from red, green, blue values
     */
    public static Color of(int r, int g, int b) {
        return new Color(new java.awt.Color(r, g, b));
    }

    /**
     * Creates a Flow Color from red, green, blue, alpha values
     */
    public static Color of(int r, int g, int b, int a) {
        return new Color(new java.awt.Color(r, g, b, a));
    }
}
