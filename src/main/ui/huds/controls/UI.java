package main.ui.huds.controls;

import javax.swing.JComponent;
import java.awt.Dimension;

public class UI {

    private UI() { }

    public static void setDimensions(JComponent container, int width, int height) {
        container.setPreferredSize(new Dimension(width, height));
//        container.setMaximumSize(new Dimension(width, height));
//        container.setMinimumSize(new Dimension(width, height));
    }
}
