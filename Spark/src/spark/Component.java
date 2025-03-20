package spark;

import flow.collections.List;
import spark.style.Style;
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
    public List<Modifier> modifiers = new List<>();

    /**
     * Applies all modifiers to this component
     */
    public void applyModifiers() {
        for (int i = 0; i < modifiers.count(); i++) {
            Modifier modifier = modifiers.get(i);

            modifier.apply(this);
        }
    }

    public abstract void createNativePeer();
    public abstract void updateNativePeer();
}
