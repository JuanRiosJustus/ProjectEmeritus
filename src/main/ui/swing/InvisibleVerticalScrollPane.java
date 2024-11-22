package main.ui.swing;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;

public class InvisibleVerticalScrollPane extends JScrollPane {
    public InvisibleVerticalScrollPane(JPanel panel, int width, int height) {
        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
        scrollPane.getViewport().setMaximumSize(new Dimension(width, height));
        scrollPane.getViewport().setMinimumSize(new Dimension(width, height));
        scrollPane.setPreferredSize(new Dimension(width, height));

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }
}
