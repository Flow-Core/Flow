package spark;

import javax.swing.JFrame;
import java.awt.Dimension;

import flow.Consumer1;
import flow.String;
import spark.containers.Column;

/**
 * Represents a top-level window
 */
public class Window {
    /** Window title */
    public String title;
    /** Window width */
    public int width;
    /** Window height */
    public int height;
    /** Root component of the window */
    public Component content;

    private JFrame frame;

    private Window(String title, int width, int height, Component content) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.content = content;
    }

    public static Window window(String title, int width, int height, Consumer1<Container> contentBuilder) {
        Column root = new Column();
        contentBuilder.invoke(root);
        Window window = new Window(title, width, height, root);

        window.show();
        return window;
    }

    /** Displays the window */
    public void show() {
        content.createNativePeer();
        frame = new JFrame(title.value);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(new Dimension(width, height));

        if (content.nativePeer instanceof java.awt.Container) {
            frame.setContentPane((java.awt.Container) content.nativePeer);
        }

        frame.setVisible(true);
    }

    /** Closes the window. */
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }
}
