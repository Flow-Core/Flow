package spark.components;

import flow.String;
import spark.Component;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

/**
 * A simple text component for displaying text
 */
public class Text extends Component {
    /** The text content */
    public String text;

    private Text(String text) {
        this.text = text;
    }

    public static Text text(String text) {
        return new Text(text);
    }

    @Override
    public void createNativePeer() {
        JLabel label = new JLabel(text.value);
        if (style != null) {
            label.setFont(style.font != null ? style.font : new Font("Arial", Font.PLAIN, 14));
            label.setForeground(style.textColor != null ? style.textColor : new Color(33, 33, 33));
        } else {
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setForeground(new Color(33, 33, 33));
        }
        this.nativePeer = label;
    }

    @Override
    public void updateNativePeer() {
        if (nativePeer instanceof JLabel lbl) {
            lbl.setText(text.value);
            lbl.setEnabled(enabled);
        }
    }
}
