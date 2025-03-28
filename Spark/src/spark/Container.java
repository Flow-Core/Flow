package spark;

import flow.collections.List;

import javax.swing.JPanel;

/**
 * A container that can hold child components.
 */
public abstract class Container extends Component {
    /** Child components */
    public List<Component> children = new List<>();

    /** Adds a child component to this container */
    public void add(Component child) {
        children.add(child);
        child.parent = this;
        if (this.nativePeer != null) {
            if (child.nativePeer == null) {
                child.createNativePeer();
            }
            if (this.nativePeer instanceof JPanel && child.nativePeer instanceof java.awt.Component) {
                ((JPanel) this.nativePeer).add((java.awt.Component) child.nativePeer);
            }
        }
    }

    @Override
    public void updateNativePeer() {
        for (int i = 0; i < children.count(); i++) {
            children.get(i).updateNativePeer();
        }
    }
}
