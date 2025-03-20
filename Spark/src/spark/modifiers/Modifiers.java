package spark.modifiers;

import spark.Modifier;
import java.awt.Insets;

import spark.style.Color;
import spark.style.Style;
import java.awt.Font;

/**
 * Provides helper methods for common modifiers
 */
public class Modifiers {
    public static Modifier padding(int top, int right, int bottom, int left) {
        return component -> {
            if (component.style == null) {
                component.style = new Style();
            }
            component.style.padding = new Insets(top, left, bottom, right);
        };
    }

    public static Modifier margin(int top, int right, int bottom, int left) {
        return component -> {
            if (component.style == null) {
                component.style = new Style();
            }
            component.style.margin = new Insets(top, left, bottom, right);
        };
    }

    public static Modifier backgroundColor(Color color) {
        return component -> {
            if (component.style == null) {
                component.style = new Style();
            }
            component.style.backgroundColor = color.value;
        };
    }

    public static Modifier textColor(Color color) {
        return component -> {
            if (component.style == null) {
                component.style = new Style();
            }
            component.style.textColor = color.value;
        };
    }

    public static Modifier font(Font font) {
        return component -> {
            if (component.style == null) {
                component.style = new Style();
            }
            component.style.font = font;
        };
    }
}
