package spark.containers;

import spark.Component;
import spark.Container;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * A horizontal container (row layout)
 */
public class Row extends Container {
    @Override
    public void createNativePeer() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
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
