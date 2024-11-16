package main.ui.swing;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

public class NoScrollScrollPane extends JScrollPane {
    public NoScrollScrollPane(JComponent component, int width, int height) {
        super(component);

        if (width > 0 && height > 0) {
            getViewport().setPreferredSize(new Dimension(width, height));
            setPreferredSize(new Dimension(width, height));
        }

        // Hide both scrollbars
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        setBorder(BorderFactory.createEmptyBorder());

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        setComponentZOrder(getVerticalScrollBar(), 0);
        setComponentZOrder(getViewport(), 1);
        getVerticalScrollBar().setOpaque(false);
        getVerticalScrollBar().setUnitIncrement(1);
        getViewport().setOpaque(false);
        setOpaque(false);

        // Remove the paint methods to keep them completely transparent
        getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {}
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
            @Override protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }

            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {}
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
            @Override protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }

            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }
}
