package spark;

import flow.collections.List;
import spark.style.Style;

import javax.swing.*;
import java.util.UUID;

/**
 * Base class for all UI components
 */
public abstract class Component {
    public String id = UUID.randomUUID().toString();
    public Component parent;
    public Style style;
    public boolean visible = true;
    public boolean enabled = true;
    public Object nativePeer;
    private final List<Modifier> modifiers = new List<>();

    /**
     * Applies all modifiers to this component
     */
    public void applyModifiers() {
        for (int i = 0; i < modifiers.count(); i++) {
            Modifier modifier = modifiers.get(i);
            modifier.apply(this);
        }
        updateNativePeer();
    }

    /**
     * Applies style properties to the native UI component.
     * This helper is intended for Swing components.
     */
    protected void applyStyleToNative() {
        if (nativePeer instanceof JComponent comp && style != null) {
            if (style.backgroundColor != null) {
                comp.setBackground(style.backgroundColor);
            }
            if (style.textColor != null) {
                comp.setForeground(style.textColor);
            }
            if (style.font != null) {
                comp.setFont(style.font);
            }
        }
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
        modifier.apply(this);
        updateNativePeer();
    }

    public abstract void createNativePeer();
    public abstract void updateNativePeer();
}
