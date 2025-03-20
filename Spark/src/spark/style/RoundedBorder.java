package spark.style;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.border.Border;

/**
 * A rounded border for creating rounded button corners.
 * You can specify a border color.
 */
public class RoundedBorder implements Border {
    private final int radius;
    private final Color borderColor;

    public RoundedBorder(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        java.awt.Color oldColor = g.getColor();
        g.setColor(borderColor);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g.setColor(oldColor);
    }
}
