package spark.containers;

import spark.Component;
import spark.Container;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

/**
 * A container that stacks children on top of each other
 */
public class Box extends Container {
    @Override
    public void createNativePeer() {
        JPanel panel = new JPanel();
        panel.setLayout(new OverlayLayout(panel));
        this.nativePeer = panel;
        for (int i = 0; i < children.count(); i++) {
            Component child = children.get(i);

            child.createNativePeer();
            if (child.nativePeer instanceof java.awt.Component) {
                panel.add((java.awt.Component) child.nativePeer);
            }
        }
    }

    @Override
    public void updateNativePeer() {
        super.updateNativePeer();
    }
}
