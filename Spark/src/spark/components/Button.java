package spark.components;

import flow.Procedure;
import flow.String;
import spark.Component;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A clickable button component
 */
public class Button extends Component {
    public String text;
    public Procedure onClick;

    private Button(String text) {
        this.text = text;
    }

    public static Button button(String text, Procedure onClick) {
        Button btn = new Button(text);
        btn.onClick = onClick;
        return btn;
    }

    @Override
    public void createNativePeer() {
        JButton btn = new JButton(text.value) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getBackground();
                if (getModel().isPressed()) {
                    bg = bg.darker();
                }
                g2.setColor(bg);
                int arc = 20;
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBackground(new Color(66, 133, 244));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(5, 15, 5, 15));

        if (onClick != null) {
            btn.addActionListener(e -> onClick.invoke());
        }

        this.nativePeer = btn;
        applyModifiers();
    }

    @Override
    public void updateNativePeer() {
        if (nativePeer instanceof JButton btn) {
            btn.setText(text.value);
            btn.setEnabled(enabled);
            applyStyleToNative();
        }
    }
}